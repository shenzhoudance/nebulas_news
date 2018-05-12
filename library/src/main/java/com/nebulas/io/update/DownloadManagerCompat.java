package com.nebulas.io.update;

import android.app.DownloadManager;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.CountDownTimer;
import android.os.Environment;
import android.os.ParcelFileDescriptor;
import android.util.Log;


import com.nebulas.io.permission.PermissionCheckHelper;
import com.nebulas.io.permission.PermissionRequestInfo;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class DownloadManagerCompat {

    private static final String TAG = "DownloadManagerCompat";
    private static final long TIMER = 1 * 1000;
    private static final long TIMEOUT = 3 * 60 * 1000;
    private static DownloadManagerCompat sInstance;
    private final boolean isSupportDownloadManager = findDownloadManagerClass() != null;
    private Context appContext;
    private DownloadManagerAdapter downloadManager;
    private CountDownTimer downloadTimer;
    private Map<Long, Request> requestMap = new ConcurrentHashMap<Long, Request>();

    private DownloadManagerCompat(Context context) {
        Log.v(TAG, "isSupportDownloadManager=" + isSupportDownloadManager);
        this.appContext = context.getApplicationContext();
        if (isSupportDownloadManager) {
            downloadManager = new DownloadManagerAdapter(appContext);
        }
    }

    public static DownloadManagerCompat getInstance(Context context) {
        if (sInstance == null) {
            synchronized (DownloadManagerCompat.class) {
                if (sInstance == null) {
                    sInstance = new DownloadManagerCompat(context);
                }
            }
        }
        return sInstance;
    }

    private static Class findDownloadManagerClass() {
        try {
            return Class.forName("android.app.DownloadManager");
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * Download the uri.
     *
     * @param request
     * @return downloadId
     */
    public long download(final Request request) {
        Log.v(TAG, "download(); uri=" + request.downlaodUri);

        PermissionCheckHelper.instance().requestPermissions(appContext, 0,  new String[]{PermissionRequestInfo.PERMISSION_STORAGE},new String[]{ PermissionRequestInfo.PERMISSION_STORAGE_MSG}, new PermissionCheckHelper.PermissionCallbackListener() {
            @Override
            public void onPermissionCheckCallback(int requestCode, String[] permissions, int[] grantResults) {
                for (String permission : permissions) {
                    if (!PermissionCheckHelper.isPermissionGranted(appContext, permission)) {
                        return;
                    }
                }
                if (isSupportDownloadManager) {
                    long downloadId = -1;
                    try {
                        downloadId = downloadManager.enqueue(request.downlaodUri, request.showNotificationOnDownloading, request.showNotificationOnDownloaded);
                        if (downloadId > 0) {
                            addDownloadTask(downloadId, request);
                            request.downloadListener.onStart(downloadId);
                        }
                    } catch (Throwable ex) {
                        // 出错之后走网页版下载
                        Intent intent = new Intent(Intent.ACTION_VIEW);
                        intent.addCategory(Intent.CATEGORY_BROWSABLE);
                        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        intent.setData(request.downlaodUri);
                        appContext.startActivity(intent);
                        downloadId = -1;
                    }
                } else {
                    Intent intent = new Intent(Intent.ACTION_VIEW);
                    intent.addCategory(Intent.CATEGORY_BROWSABLE);
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    intent.setData(request.downlaodUri);
                    appContext.startActivity(intent);
                }
            }
        });
        return 0;
    }

    public void cancelDownload(long downloadId, boolean delDownloadManagerTask) {
        Log.v(TAG, "cancelDownload(); downloadId=" + downloadId);
        if (isSupportDownloadManager && downloadId > 0) {
            removeDownloadTask(downloadId, delDownloadManagerTask);
        }
    }

    private void addDownloadTask(long downloadId, Request request) {
        requestMap.put(downloadId, request);
        if (requestMap.size() >= 1) {
            registerDownloadReceiver();
        }
    }

    private void removeDownloadTask(long downloadId, boolean delDownloadManagerTask) {
        Request request = requestMap.get(downloadId);
        if (request != null && delDownloadManagerTask) {
            downloadManager.remove(downloadId);
        }
        requestMap.remove(downloadId);
        if (requestMap.size() == 0) {
            unregisterDownloadReceiver();
        }
    }

    private synchronized void registerDownloadReceiver() {
        if (downloadTimer == null) {
            downloadTimer = new DownloadTimer(Long.MAX_VALUE, TIMER);
            downloadTimer.start();
        }
    }

    private synchronized void unregisterDownloadReceiver() {
        if (downloadTimer != null) {
            downloadTimer.cancel();
            downloadTimer = null;
        }
    }

    public boolean isSupportDownloadManager() {
        return isSupportDownloadManager;
    }

    public interface DownloadListener {
        void onStart(long downloadId);

        void onProgress(long downloadId, int downloadedSize, int totalSize);

        void onComplete(long downloadId, String fileUri);

        void onTimeout(long downloadId);

        void onFailed(long downloadId, int reasonCode);
    }

    public static final class Request {
        private Uri downlaodUri;
        private DownloadListener downloadListener; // download listener (only effective for those roms which support {@link android.app.DownloadManager} )
        private boolean showNotificationOnDownloading; // show notification on downloading
        private boolean showNotificationOnDownloaded; // show notification on downloaded (API Level 11)
        private long startTime = System.currentTimeMillis();
        private long timeout = TIMEOUT;

        public Request(Uri downlaodUri, DownloadListener downloadListener) {
            this.downlaodUri = downlaodUri;
            this.downloadListener = downloadListener;
        }

        public Request timeout(int timeout) {
            this.timeout = timeout;
            return this;
        }

        public Request showNotificationOnDownloading(boolean showNotificationOnDownloading) {
            this.showNotificationOnDownloading = showNotificationOnDownloading;
            return this;
        }

        public Request showNotificationOnDownloaded(boolean showNotificationOnDownloaded) {
            this.showNotificationOnDownloaded = showNotificationOnDownloaded;
            return this;
        }

        public long getTimeoutTime() {
            return startTime + timeout;
        }
    }

    /**
     * A DownloadManager Wrapper.
     * Avoid ClassNotFoundException before API level 9.
     */
    public static final class DownloadManagerAdapter {

        private final DownloadManager downloadManager;

        public DownloadManagerAdapter(Context context) {
            this.downloadManager = (DownloadManager) context.getSystemService(Context.DOWNLOAD_SERVICE);
        }

        public long enqueue(Uri uri, boolean showNotificationOnDownloading, boolean showNotificationOnDownloaded) {
            DownloadManager.Request request = new DownloadManager.Request(uri);
            // download folder
            File downloadDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS);
            // cannot create download folder, return -1
            if (downloadDir == null || (!downloadDir.exists() && !downloadDir.mkdirs())) {
                return -1;
            }

            try {
                File downloadDira = new File(downloadDir.getPath() + "/" + uri.getLastPathSegment());
                if (downloadDira.exists()) {
                    downloadDira.delete();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }

            request.setDestinationInExternalPublicDir(Environment.DIRECTORY_DOWNLOADS, uri.getLastPathSegment());
            // Whether show download notification
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
                /**
                 * {@link DownloadManager.Request#setNotificationVisibility(int)} start from HONEYCOMB
                 */
                int visibility = DownloadManager.Request.VISIBILITY_HIDDEN;
                if (showNotificationOnDownloading && showNotificationOnDownloaded) {
                    visibility = DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED;
                } else if (!showNotificationOnDownloading && showNotificationOnDownloaded) {
                    visibility = DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_ONLY_COMPLETION;
                } else if (showNotificationOnDownloading && !showNotificationOnDownloaded) {
                    visibility = DownloadManager.Request.VISIBILITY_VISIBLE;
                }
                try {
                    request.setNotificationVisibility(visibility);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            } else {
                /**
                 * {@link DownloadManager.Request#setShowRunningNotification(boolean)} before HONEYCOMB
                 */
                request.setShowRunningNotification(showNotificationOnDownloading);
            }
            return downloadManager.enqueue(request);
        }

        public long addCompletedDownload(String title, String description, boolean isMediaScannerScannable, String mimeType, String path, long length, boolean showNotification) {
            return downloadManager.addCompletedDownload(title, description, isMediaScannerScannable, mimeType, path, length, showNotification);
        }

        public ParcelFileDescriptor openDownloadedFile(long id) throws FileNotFoundException {
            return downloadManager.openDownloadedFile(id);
        }

        public String getMimeTypeForDownloadedFile(long id) {
            return downloadManager.getMimeTypeForDownloadedFile(id);
        }

        public Uri getUriForDownloadedFile(long id) {
            return downloadManager.getUriForDownloadedFile(id);
        }

        public Cursor query(DownloadManager.Query query) {
            return downloadManager.query(query);
        }

        public int remove(long... ids) {
            return downloadManager.remove(ids);
        }
    }

    private class DownloadTimer extends CountDownTimer {

        private DownloadTimer(long millisInFuture, long countDownInterval) {
            super(millisInFuture, countDownInterval);
        }

        @Override
        public void onTick(long millisUntilFinished) {
            int size = requestMap.size();
            if (size > 0) {
                // Get all ids in downloading
                List<Long> ids = new ArrayList(requestMap.keySet());
                long[] downloadIds = new long[size];
                for (int i = 0; i < size; i++) {
                    downloadIds[i] = ids.get(i);
                }
                DownloadManager.Query downloadQuery = new DownloadManager.Query();
                downloadQuery.setFilterById(downloadIds);
                Cursor downloadCursor = downloadManager.query(downloadQuery);
                try {
                    while (downloadCursor.moveToNext()) {
                        long downloadId = downloadCursor.getLong(downloadCursor.getColumnIndex(DownloadManager.COLUMN_ID));
                        int downloadStatus = downloadCursor.getInt(downloadCursor.getColumnIndex(DownloadManager.COLUMN_STATUS));

                        Request request = requestMap.get(downloadId);
                        if (request != null) {
                            DownloadListener downloadListener = request.downloadListener;
                            // time out?
                            if (System.currentTimeMillis() > request.getTimeoutTime() &&
                                    downloadStatus != DownloadManager.STATUS_SUCCESSFUL) {
                                downloadListener.onTimeout(downloadId);
                                removeDownloadTask(downloadId, false);
                                continue;
                            }
                            // downloading
                            switch (downloadStatus) {
                                case DownloadManager.STATUS_SUCCESSFUL:
                                    String fileUri = downloadCursor.getString(downloadCursor.getColumnIndex(DownloadManager.COLUMN_LOCAL_URI));
                                    downloadListener.onComplete(downloadId, fileUri);
                                    removeDownloadTask(downloadId, false);
                                    break;
                                case DownloadManager.STATUS_RUNNING:
                                    int downloadedSize = downloadCursor.getInt(downloadCursor.getColumnIndex(DownloadManager.COLUMN_BYTES_DOWNLOADED_SO_FAR));
                                    int totalSize = downloadCursor.getInt(downloadCursor.getColumnIndex(DownloadManager.COLUMN_TOTAL_SIZE_BYTES));
                                    if (downloadedSize >= totalSize && totalSize > 0) {
                                        // download success, some rom dosn't change to DownloadManager.STATUS_SUCCESSFUL, so check download process
                                        fileUri = downloadCursor.getString(downloadCursor.getColumnIndex(DownloadManager.COLUMN_LOCAL_URI));
                                        downloadListener.onComplete(downloadId, fileUri);
                                        removeDownloadTask(downloadId, false);
                                    } else {
                                        downloadListener.onProgress(downloadId, downloadedSize, totalSize);
                                    }
                                    break;
                                case DownloadManager.STATUS_FAILED:
                                case DownloadManager.STATUS_PAUSED:
                                    int reasonCode = downloadCursor.getInt(downloadCursor.getColumnIndex(DownloadManager.COLUMN_REASON));
                                    downloadListener.onFailed(downloadId, reasonCode);
                                    removeDownloadTask(downloadId, false);
                                    break;
                            }
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                } finally {
                    downloadCursor.close();
                }
            }
        }

        @Override
        public void onFinish() {
        }
    }
}


