package com.nebulas.io.permission;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.provider.Settings;
import android.support.v4.app.ActivityCompat;
import android.util.Log;

import com.nebulas.io.ui.BaseActivity;


public class PermissionHandlerActivity extends BaseActivity implements ActivityCompat.OnRequestPermissionsResultCallback {

    private static final int PERMISSION_REQUEST_CODE = 0;
    private static final int OPEN_APPLICATION_SETTING_CODE = 2;

    private PermissionRequestInfo requestInfo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestNextPermission();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        PermissionCheckHelper.instance().setRequestFinish();
    }

    /**
     * 处理下一个权限请求
     */
    private void requestNextPermission(){
        requestInfo = PermissionCheckHelper.instance().getNextRequest(this);
        if(requestInfo != null){
            String[] permissionArray = requestInfo.getPermissionArray();
            if(permissionArray != null && permissionArray.length > 0){
                ActivityCompat.requestPermissions(this, permissionArray, PERMISSION_REQUEST_CODE);
            }
        } else {
            PermissionHandlerActivity.this.finish();
        }
    }

    /**
     * 被拒绝的权限并且shouldShowRequestPermissionRationale返回false就是用户选中Never Ask Again的权限
     * 弹框提示用户去设置里授予权限，不请求权限
     *
     * @param permissionArray
     * @param messageArray
     * @return  true表示处理了Never Ask Again
     */
    private boolean handleNeverAsk(final String[] permissionArray, String[] messageArray, int[] grantResults){
        boolean hasNeverAsk = false;
        String rationaleMsg = "";
        for (int index = 0; index < permissionArray.length && grantResults[index] == PackageManager.PERMISSION_DENIED; ++index){
            String permission = permissionArray[index];
            if (!ActivityCompat.shouldShowRequestPermissionRationale(this, permission)) {
                hasNeverAsk = true;
                if(!messageArray[index].isEmpty()) {
                    rationaleMsg += messageArray[index];
                }
            }
        }
        if(hasNeverAsk){
            showNeverAskRationaleDialog(this, rationaleMsg);
        }
        return hasNeverAsk;
    }

    /**
     * 权限请求结果回调
     *
     * @param requestCode
     * @param permissions
     * @param grantResults
     */
    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        if(PERMISSION_REQUEST_CODE == requestCode) {
            for(int index = 0; index < permissions.length; ++index){
                requestInfo.getPermissionResultMap().put(permissions[index], grantResults[index]);
            }
            if(!handleNeverAsk(permissions, requestInfo.getMessageArray(), grantResults)) {
                doCallback();
                requestNextPermission();
            }
        }
    }

    /**
     *  回调最终权限请求结果
     */
    private void doCallback(){
        PermissionCheckHelper.PermissionCallbackListener listener = requestInfo.getListener();
        if (listener != null) {
            listener.onPermissionCheckCallback(requestInfo.getRequestCode(),
                    requestInfo.getRequestPermissions(), requestInfo.getRequestResults());
        }
    }

    /**
     * 打开设置中app应用信息界面
     *
     */
    private void openAppSetting(){
        try {
            Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
            Uri uri = Uri.fromParts("package", getPackageName(), null);
            intent.setData(uri);
            startActivityForResult(intent, OPEN_APPLICATION_SETTING_CODE);
        } catch (Exception ex){
            Log.d("PermissionHandler", "open app setting failed");
            doCallback();
            requestNextPermission();
        }
    }

    /**
     * 处理Never Ask Again情况，用户返回后再次去请求权限来获取最终的权限请求结果
     *
     * @param requestCode
     * @param resultCode
     * @param data
     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == OPEN_APPLICATION_SETTING_CODE) {
            requestInfo = PermissionCheckHelper.instance().findShouldCheckPermission(PermissionHandlerActivity.this, requestInfo);
            if(requestInfo != null) {
                doCallback();
            }
            requestNextPermission();
        }
    }

    /**
     * 处理Never Ask Again
     * 自定义权限请求解释提示框，带有设置引导
     *
     * @param context
     * @param message
     */
    private void showNeverAskRationaleDialog(Context context, String message) {
        if(!"".equals(message)){
            message = message.substring(0, message.length() - 1);
        }
        StringBuilder builder = new StringBuilder(message)
                .append("。")
                .append("\n操作路径：设置->应用->股票雷达->权限");

        new AlertDialog.Builder(context).setTitle("我们需要一些权限").setMessage(builder).setPositiveButton("去设置", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                openAppSetting();
            }
        }).setNegativeButton("取消", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                doCallback();
                requestNextPermission();
            }
        }).create().show();
    }
}
