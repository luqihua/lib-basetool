package com.lu.tool.app.update;

import android.os.Handler;
import android.os.Looper;
import android.os.Message;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.ref.WeakReference;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Map;

import javax.net.ssl.HttpsURLConnection;

/**
 * author: luqihua
 * date:2019-06-05
 * description:
 **/
public class HttpConnectionTools {

    private Handler mHandler;
    private IHttpCallback mCallback;

    public HttpConnectionTools() {
        this.mHandler = new MainHandler(this);
    }

    public void get(String path, IHttpCallback callback) {
        post(path, null, callback);
    }


    public void post(String path, Map<String, String> params, IHttpCallback callback) {
        this.mCallback = callback;
        doConn(path, params);
    }


    private void doConn(final String path, final Map<String, String> params) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                HttpURLConnection conn = null;
                try {
                    URL url = new URL(path);
                    conn = (HttpURLConnection) url.openConnection();
                    conn.setDoOutput(true);
                    conn.setDoInput(true);
                    conn.setReadTimeout(10000);
                    conn.setConnectTimeout(10000);
                    conn.connect();

                    if (params != null && params.size() > 0) {
                        OutputStream writer = conn.getOutputStream();
                        writer.write(formatParams(params));
                        writer.flush();
                        writer.close();
                    }

                    int responseCode = conn.getResponseCode();
                    if (responseCode != 200) {
                        mHandler.obtainMessage(responseCode, "网络响应出错").sendToTarget();
                        return;
                    }

                    ByteArrayOutputStream bos = new ByteArrayOutputStream();
                    InputStream in = conn.getInputStream();
                    byte[] buffer = new byte[1024];
                    int len;
                    while ((len = in.read(buffer)) > 0) {
                        bos.write(buffer, 0, len);
                    }

                    String responseData = bos.toString();
                    bos.close();
                    mHandler.obtainMessage(responseCode, responseData).sendToTarget();

                } catch (IOException e) {
                    mHandler.obtainMessage(-1, "网络连接出错").sendToTarget();
                } finally {
                    if (conn != null) {
                        conn.disconnect();
                    }
                }
            }
        }).start();
    }


    /**
     * 写入数据
     *
     * @throws IOException
     */
    private static byte[] formatParams(Map<String, String> params) {
        //======添加需要传递的参数============
        StringBuilder data = new StringBuilder();
        for (Map.Entry<String, String> entry : params.entrySet()) {
            data.append(entry.getKey())
                    .append("=")
                    .append(entry.getValue())
                    .append("&");
        }
        String postBody = data.substring(0, data.length() - 1);
        return postBody.getBytes();
    }


    private static class MainHandler extends Handler {
        private WeakReference<HttpConnectionTools> reference;

        public MainHandler(HttpConnectionTools tools) {
            super(Looper.getMainLooper());
            this.reference = new WeakReference<>(tools);
        }

        @Override
        public void handleMessage(Message msg) {
            if (reference.get() == null || reference.get().mCallback == null) return;
            if (msg.what == HttpsURLConnection.HTTP_OK) {
                reference.get().mCallback.onHttpSuccess((String) msg.obj);
            } else {
                reference.get().mCallback.onHttpError((String) msg.obj);
            }
        }
    }

    interface IHttpCallback {
        void onHttpSuccess(String response);

        void onHttpError(String errorMsg);
    }
}
