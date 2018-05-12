package com.nebulas.io.permission;

import android.Manifest;
import android.content.pm.PackageManager;


import java.util.HashMap;
import java.util.Map;

public class PermissionRequestInfo {

    public static final String PERMISSION_STORAGE = Manifest.permission.WRITE_EXTERNAL_STORAGE;
    public static final String PERMISSION_STORAGE_MSG = "需要外外部存储的权限";


    private int requestCode;
    private PermissionCheckHelper.PermissionCallbackListener listener;
    private String[] permissionArray;
    private String[] messageArray;
    private Map<String, Integer> permissionResultMap;
    private int count = 0;

    public PermissionRequestInfo(PermissionCheckHelper.PermissionCallbackListener listener,
                                 int requestCode, String[] permissionArray, String[] messageArray) {
        this.listener = listener;
        this.requestCode = requestCode;
        this.permissionArray = permissionArray;
        this.messageArray = messageArray;
        initMap();
    }

    private void initMap() {
        permissionResultMap = new HashMap<>();
        if (permissionArray == null || permissionArray.length == 0) {
            return;
        }
        for (String permission : permissionArray) {
            permissionResultMap.put(permission, PackageManager.PERMISSION_DENIED);
            count++;
        }
    }

    public int getRequestCode() {
        return requestCode;
    }

    public PermissionCheckHelper.PermissionCallbackListener getListener() {
        return listener;
    }

    public String[] getPermissionArray() {
        return permissionArray;
    }

    public void setPermissionArray(String[] permissionArray) {
        this.permissionArray = permissionArray;
    }

    public String[] getMessageArray() {
        return messageArray;
    }

    public void setMessageArray(String[] messageArray) {
        this.messageArray = messageArray;
    }

    public Map<String, Integer> getPermissionResultMap() {
        return permissionResultMap;
    }

    public int getPermissionCount() {
        return count;
    }

    public String[] getRequestPermissions() {
        String[] permissions = new String[count];
        if (permissionResultMap != null && count > 0) {
            permissions = permissionResultMap.keySet().toArray(new String[count]);
        }
        return permissions;
    }

    public int[] getRequestResults() {
        int[] grantResults = new int[count];
        if (permissionResultMap != null && count > 0) {
            Integer[] results = permissionResultMap.values().toArray(new Integer[count]);
            for (int index = 0; index < count; ++index) {
                grantResults[index] = results[index];
            }
        }
        return grantResults;
    }
}

