package com.borg.mvp.model.Network;

import android.util.Log;

import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.SocketException;
import java.net.URL;
import java.net.UnknownHostException;
import java.security.SecureRandom;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

/**
 * Created by Gulliver(feilong) on 16/1/21.
 * 访问网络获取短连接和token
 */
public class QrNetworkHelper {
    private static final String TAG = QrNetworkHelper.class.getSimpleName();
    private static final String DSP = "TVLogin";
    private static final String GET_QR_URL_DAILY = "https://qrlogin.daily.taobao.net/qrcodelogin/generateNoLoginQRCode.do?lt=m&dsp="+DSP;
    private static final String GET_QR_URL = "https://qrlogin.taobao.com/qrcodelogin/generateNoLoginQRCode.do?lt=m&dsp="+DSP;
    private static final String GET_TOKEN_DAILY = "https://qrlogin.daily.taobao.net/qrcodelogin/loginByQRCode.do?lt=m&";
    private static final String GET_TOKEN = "https://qrlogin.taobao.com/qrcodelogin/loginByQRCode.do?lt=m&";

    private static boolean isDaily = false;

    /**
     * 获取二维码
     * {"success":true,"message":"null","url":"http://ma.taobao.com/rl/70f84e66ba2cf277ddd80c703eb57d56","t":1390547803260,"at":"123sdjr74832sdefr876d423kr432se3"}
     * @param callback
     */
    public static void getQrUrl(final INetworkCallback callback){
        String url;
        if (isDaily){
            url = GET_QR_URL_DAILY;
        }else {
            url = GET_QR_URL;
        }
        getHttps(url, callback);
    }

    /**
     * 登录轮询
     * @param timestamp
     * @param at
     */
    public static void getToken(long timestamp,String at,final INetworkCallback callback){
        String url;
        if(isDaily){
            url = GET_TOKEN_DAILY;
        }else {
            url = GET_TOKEN;
        }
        url = url+"t="+timestamp+"&at="+at;
        getHttps(url, callback);
    }

    public static void setDaily(boolean daily){
        isDaily = daily;
    }
    private static void get(final String surl,final INetworkCallback callback){
        Log.d(TAG,"url="+surl);
        new Thread(new Runnable() {
            @Override
            public void run() {
                try{
                    URL url = new URL(surl);
                    HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                    conn.setConnectTimeout(6*1000);
                    int code = conn.getResponseCode();
                    if (code != 200){
                        callback.onFail(code,conn.getResponseMessage());
                    }else {
                        InputStreamReader in = new InputStreamReader(conn.getInputStream());
                        BufferedReader buffer = new BufferedReader(in);
                        String result="";
                        String inputLine = null;
                        while ((inputLine = buffer.readLine()) != null) {
                            result += inputLine + "\n";
                        }
                        in.close();
                        conn.disconnect();
                        callback.onSuccess(result);
                    }
                }catch (Exception e){
                    callback.onFail(-1,"url is error");
                }
            }
        }).start();
    }

    private static void getHttps(final String surl,final INetworkCallback callback){
        Log.d(TAG,"getHttps="+surl);
        new Thread(new Runnable() {
            @Override
            public void run() {
                try{
                    SSLContext sc = SSLContext.getInstance("TLS");
                    sc.init(null, new TrustManager[]{new MyTrustManager()}, new SecureRandom());
                    HttpsURLConnection.setDefaultSSLSocketFactory(sc.getSocketFactory());
                    HttpsURLConnection.setDefaultHostnameVerifier(new MyHostnameVerifier());
                    HttpsURLConnection conn = (HttpsURLConnection)new URL(surl).openConnection();
                    conn.setDoOutput(true);
                    conn.setDoInput(true);
                    conn.connect();

                    BufferedReader br = new BufferedReader(new InputStreamReader(conn.getInputStream()));
                    StringBuffer sb = new StringBuffer();
                    String line;
                    while ((line = br.readLine()) != null)
                        sb.append(line);
                    callback.onSuccess(sb.toString());

                }catch(Exception e){
                    callback.onFail(-1,"");
                }
            }
        }).start();
    }

    private static class MyHostnameVerifier implements HostnameVerifier {

        @Override
        public boolean verify(String hostname, SSLSession session) {
            // TODO Auto-generated method stub
            return true;
        }
    }

    private static class MyTrustManager implements X509TrustManager {

        @Override
        public void checkClientTrusted(X509Certificate[] chain, String authType)
                throws CertificateException {
            // TODO Auto-generated method stub
        }

        @Override
        public void checkServerTrusted(X509Certificate[] chain, String authType)
                throws CertificateException {
            // TODO Auto-generated method stub

        }

        @Override
        public X509Certificate[] getAcceptedIssuers() {
            // TODO Auto-generated method stub
            return null;
        }
    }
}
