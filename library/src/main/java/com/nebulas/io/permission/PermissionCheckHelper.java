package com.nebulas.io.permission;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.support.v4.content.ContextCompat;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

public class PermissionCheckHelper {

    private final static String TAG = "PermissionCheckHelper";

    public interface PermissionCallbackListener {
        void onPermissionCheckCallback(int code, String[] permissions, int[] grantResults);
    }

    private PermissionCheckHelper() {

    }

    private static PermissionCheckHelper instance;

    public static PermissionCheckHelper instance() {
        if(instance == null){
            instance = new PermissionCheckHelper();
        }
        return instance;
    }

    private List<PermissionRequestInfo> requestList = new ArrayList<>();

    private boolean isRequesting = false;

    private final Object lock = new Object();

    /**
     * 是否在请求权限
     */
    public boolean isRequesting() {
        return isRequesting;
    }

    public void setRequestFinish(){
        synchronized (lock) {
            this.isRequesting = false;
        }
    }

    /**
     * 只检查权限是否授予，不请求权限
     *
     * @param context
     * @param permission
     * @return
     */
    public static synchronized boolean isPermissionGranted(Context context, String permission) {
        if (context == null || permission == null || "".equals(permission)) {
            Log.d(TAG, "权限授予检查参数错误!");
            return false;
        }
        int isGranted;
        try {
            isGranted = ContextCompat.checkSelfPermission(context, permission);
        } catch (Exception ex){
            PackageManager pm = context.getPackageManager();
            isGranted = pm.checkPermission(permission, context.getPackageName());
        }
        return isGranted == PackageManager.PERMISSION_GRANTED;
    }

    /**
     * 请求多个权限, 兼容低版本
     *
     * @param context
     * @param permissionArray 请求的权限数组
     * @param messageArray    各个权限对应的请求原因描述
     * @param listener
     */
    public void requestPermissions(Context context, int requestCode, String[] permissionArray, String[] messageArray,
                                   PermissionCallbackListener listener) {
        if (context == null || permissionArray == null || permissionArray.length == 0
                || messageArray == null || messageArray.length == 0 || listener == null) {
            Log.d(TAG, "权限检查参数错误!");
            return;
        }
        PermissionRequestInfo requestInfo = new PermissionRequestInfo(listener, requestCode, permissionArray, messageArray);
        requestInfo = findShouldCheckPermission(context, requestInfo);
        if (requestInfo != null) {
            if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M){
                requestInfo.getListener().onPermissionCheckCallback(requestInfo.getRequestCode(),
                        requestInfo.getRequestPermissions(), requestInfo.getRequestResults());
            } else {
                requestIntoQueue(context, requestInfo);
            }
        }
    }

    /**
     * 权限请求入队
     *
     * @param context
     * @param requestInfo
     */
    private void requestIntoQueue(Context context, PermissionRequestInfo requestInfo) {
        synchronized (lock) {
            requestList.add(requestInfo);
            if (!isRequesting) {
                try {
                    Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("nebulasnews://permission"));
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    context.startActivity(intent);
                    isRequesting = true;
                } catch (Exception ex){
                    Log.d(TAG, "permission handle activity start failed!");
                    isRequesting = false;
                }
            }
        }
    }

    /**
     * 检查单个权限是否授予
     *
     * @param requestInfo
     * @return
     */
    private boolean hasSinglePermission(Context context, PermissionRequestInfo requestInfo) {
        String[] permissionArray = requestInfo.getPermissionArray();
        if (isPermissionGranted(context, permissionArray[0])) {
            requestInfo.getListener().onPermissionCheckCallback(requestInfo.getRequestCode(),
                    requestInfo.getRequestPermissions(), createGrantedResults(requestInfo.getPermissionCount()));
            return true;
        }
        return false;
    }

    /**
     * 检查权限是否授予，剔除已经授予的权限请求
     *
     * @param requestInfo
     * @return
     */
    public PermissionRequestInfo findShouldCheckPermission(Context context, PermissionRequestInfo requestInfo) {
        String[] permissionArray = requestInfo.getPermissionArray();
        int count = permissionArray.length;
        if (count == 1) {
            return hasSinglePermission(context, requestInfo) ? null : requestInfo;
        }
        List<String> permissionList = new ArrayList<>(count);
        List<String> messageList = new ArrayList<>(count);
        for (int index = 0; index < count; ++index) {
            String permission = permissionArray[index];
            if (isPermissionGranted(context, permission)) {
                requestInfo.getPermissionResultMap().put(permission, PackageManager.PERMISSION_GRANTED);
                continue;
            }
            permissionList.add(permission);
            messageList.add(requestInfo.getMessageArray()[index]);
        }
        if (permissionList.size() == 0) {
            requestInfo.getListener().onPermissionCheckCallback(requestInfo.getRequestCode(),
                    requestInfo.getRequestPermissions(), createGrantedResults(requestInfo.getPermissionCount()));
            return null;
        }
        requestInfo.setPermissionArray(permissionList.toArray(new String[permissionList.size()]));
        requestInfo.setMessageArray(messageList.toArray(new String[messageList.size()]));
        return requestInfo;
    }

    private int[] createGrantedResults(int length) {
        int[] grantResults = new int[length];
        for (int index = 0; index < length; ++index) {
            grantResults[index] = PackageManager.PERMISSION_GRANTED;
        }
        return grantResults;
    }

    /**
     * 循环获取下一个需要check的权限请求
     *
     * @return
     */
    public PermissionRequestInfo getNextRequest(Context context) {
        synchronized (lock) {
            PermissionRequestInfo requestInfo = null;
            while (requestList != null && requestList.size() > 0) {
                requestInfo = requestList.get(0);
                requestList.remove(0);
                requestInfo = findShouldCheckPermission(context, requestInfo);
                if (requestInfo != null) {
                    break;
                }
            }
            if (requestInfo == null) {
                isRequesting = false;
            }
            return requestInfo;
        }
    }

}
