package com.nebulas.io.update;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.text.Html;
import android.text.TextUtils;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.nebulas.io.R;
import com.nebulas.io.ui.CustomDialog;
import com.nebulas.io.util.Utils;

import java.io.File;

/**
 */
public class UpdateChecker {

    private Activity activity;
    private UpdateInfo mUpdateInfo;


    public UpdateChecker(Activity mContext, UpdateInfo info) {
        this.activity = mContext;
        this.mUpdateInfo = info;
    }


    private UpdateDialog dialog;
    public void checkoutUpdate() {

        if (mUpdateInfo == null) {
            return;
        }

        //提示更新信息
        if (activity != null) {

            dialog = new UpdateDialog(activity, new OnButtonClickListener() {
                @Override
                public void onButtonClick(int id) {
                    if(id == OnButtonClickListener.CANCEL){
                        dialog.dismiss();
                    } else if (id == OnButtonClickListener.UPDATE) {
                        startDownLoad(mUpdateInfo.getApkUrl());
                        dialog.dismiss();
                    }
                }
            },mUpdateInfo);
            dialog.show();

        }
    }

    public void startDownLoad(String url) {

        boolean sdCardExist = Environment.getExternalStorageState()
                .equals(android.os.Environment.MEDIA_MOUNTED); //判断sd卡是否存在
        if (!sdCardExist) {
            Toast.makeText(activity, "找不到SD卡，请插入SD卡后再次下载", Toast.LENGTH_SHORT).show();
            return;
        }
        if (!Utils.isAvaiableSpace(20)) {
            Toast.makeText(activity, "SD卡空间不足", Toast.LENGTH_SHORT).show();
            return;
        }

        DownloadManagerCompat.Request request = new DownloadManagerCompat.Request(Uri.parse(url), new DownloadManagerCompat.DownloadListener() {
            @Override
            public void onStart(long downloadId) {
                currentDownloadId = downloadId;
                showProgressDialog();
            }

            @Override
            public void onProgress(long downloadId, int downloadedSize, int totalSize) {
                updateDialogProgress(downloadedSize, totalSize);
            }

            @Override
            public void onComplete(long downloadId, String fileUri) {
                dismissProgressDialog();
                startInstallActivity(activity, fileUri == null ? null : getPathFromUri(activity, Uri.parse(fileUri)));
            }

            @Override
            public void onTimeout(long downloadId) {
                dismissProgressDialog();
                Toast.makeText(activity, "下载超时", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onFailed(long downloadId, int reasonCode) {
                dismissProgressDialog();
                Toast.makeText(activity, "下载失败", Toast.LENGTH_SHORT).show();
            }
        }).showNotificationOnDownloading(true).showNotificationOnDownloaded(false);
        request.timeout(20 * 60 * 1000);
        DownloadManagerCompat.getInstance(activity).download(request);
    }

    private long currentDownloadId;

    private void updateDialogProgress(int downloadedSize, int totalSize) {
        if (progressDialogContainer != null) {
            int percent = (int) (((float) downloadedSize / totalSize) * 100);
            float currentSize = downloadedSize * 1.0f / 1024 / 1024;
            float maxSize = totalSize * 1.0f / 1024 / 1024;
            float fixedCurrentSize = (float) (Math.round(currentSize * 100)) / 100;
            float fixedTotalSize = (float) (Math.round(maxSize * 100)) / 100;
            ((TextView) progressDialogContainer.findViewById(R.id.percent_count)).setText(percent + "%");
            ((TextView) progressDialogContainer.findViewById(R.id.percent_data)).setText(fixedCurrentSize + "M/" + fixedTotalSize + "M");
            ((ProgressBar) progressDialogContainer.findViewById(R.id.progress)).setProgress(percent);
        }
    }

    private CustomDialog progressDialog;
    View progressDialogContainer;
    private CustomDialog.Builder progressDialogBuilder;

    private void showProgressDialog() {
        progressDialogBuilder = new CustomDialog.Builder(activity);
        progressDialogContainer = activity.getLayoutInflater().inflate(R.layout.common_download_layout, null);
        progressDialog = progressDialogBuilder.setTitle("更新提示").setPositiveButton("后台下载", null).setNegativeButton("取消", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                DownloadManagerCompat.getInstance(activity).cancelDownload(currentDownloadId, true);
            }
        }).setContentView(progressDialogContainer).create();
        progressDialog.show();
    }

    private void dismissProgressDialog() {
        if (progressDialog != null && progressDialog.isShowing()) {
            progressDialog.dismiss();
            progressDialog = null;
        }
    }

    private static String getPathFromUri(Context context, Uri uri) {
        String path = null;
        String uriScheme = uri.getScheme();
        if (uriScheme.equals("file")) {
            path = uri.getSchemeSpecificPart();
        } else if (uriScheme.equals("content")) {
            String[] projection = {MediaStore.Images.Media.DATA};
            Cursor cursor = context.getContentResolver().query(uri, projection, null, null, null);
            if (cursor != null) {
                if (cursor.moveToFirst()) {
                    int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
                    path = cursor.getString(column_index);
                }
                cursor.close();
            }
        }
        return path;
    }

    private static void startInstallActivity(Context context, String filePath) {

        File installFile = null;

        if (!TextUtils.isEmpty(filePath)) {
            installFile = new File(filePath);
        }

        if (installFile == null || !installFile.exists()) {
            Toast.makeText(context, "对不起，找不到安装文件，请到官网下载安装最新版本~", Toast.LENGTH_LONG).show();
        } else {
            try {
                Intent notificationIntent = new Intent();
                notificationIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                notificationIntent.setAction(Intent.ACTION_VIEW);

                notificationIntent.setDataAndType(Uri.fromFile(installFile), "application/vnd.android.package-archive");
                context.startActivity(notificationIntent);
            } catch (Exception e) {
                e.printStackTrace();
                Toast.makeText(context, "对不起，安装失败，请稍候再试", Toast.LENGTH_SHORT).show();
            }
        }
    }
}

