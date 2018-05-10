package com.nebulas.io.net.retrofit;


import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

import okhttp3.MediaType;
import okhttp3.RequestBody;
import okio.BufferedSink;


/**
 * Created by legend on 2018/5/6.
 */

public final class RequestBodyBuilder {
    private static final int DEFAULT_BUFFER_SIZE = 2048;

    /**
     * Returns a new request body that transmits {@code content}.
     */
    public static RequestBody build(final byte[] content, final String contentType) {
        return build(MediaType.parse(contentType), content);
    }

    /**
     * Returns a new request body that transmits {@code file}.
     */
    public static RequestBody build(final File file, final String contentType) {
        return build(file, contentType, null);
    }

    /**
     * Returns a new request body that transmits {@code file} with FileTransferCallbacks.
     */
    public static RequestBody build(final File file, final String contentType, final FileTransferCallbacks fileTransferCallbacks) {
        if (file == null) {
            throw new NullPointerException("file == null");
        } else {
            final MediaType mediaType = MediaType.parse(contentType);
            return new RequestBody() {
                public MediaType contentType() {
                    return (mediaType == null) ? null : mediaType;
                }

                public long contentLength() {
                    return file.length();
                }

                @Override
                public void writeTo(BufferedSink out) throws IOException {
                    long fileLength = contentLength();
                    long uploaded = 0;
                    FileInputStream fileStream = null;
                    byte[] buffer = new byte[DEFAULT_BUFFER_SIZE];
                    try {
                        int read;
                        fileStream = new FileInputStream(file);
                        while ((read = fileStream.read(buffer)) != -1) {
                            out.write(buffer, 0, read);
                            uploaded += read;
                            if (fileTransferCallbacks != null) {
                                fileTransferCallbacks.onProgressUpdate(uploaded, fileLength);
                            }
                        }
                    } finally {
                        try {
                            if (fileStream != null) {
                                fileStream.close();
                            }
                        } catch (Throwable ignored) {

                        }
                    }
                }
            };
        }
    }

    /**
     * Returns a new request body that transmits {@code content}.
     */
    static RequestBody build(final MediaType contentType, final byte[] content) {
        if (content == null) {
            throw new NullPointerException("content == null");
        }
        return build(contentType, content, 0, content.length);
    }

    static void checkOffsetAndCount(long arrayLength, long offset, long count) {
        if ((offset | count) < 0 || offset > arrayLength || arrayLength - offset < count) {
            throw new ArrayIndexOutOfBoundsException();
        }
    }


    /**
     * Returns a new request body that transmits {@code content}.
     */
    private static RequestBody build(final MediaType contentType, final byte[] content,
                                     final int offset, final int byteCount) {
        checkOffsetAndCount(content.length, offset, byteCount);
        return new RequestBody() {
            @Override
            public MediaType contentType() {
                return contentType;
            }

            @Override
            public long contentLength() {
                return byteCount;
            }

            @Override
            public void writeTo(BufferedSink sink) throws IOException {
                sink.write(content, offset, byteCount);

            }
        };
    }

    public interface FileTransferCallbacks {
        /**
         * Returns bytes that total transferred.
         */
        void onProgressUpdate(long transferred, long fileSize);
    }
}
