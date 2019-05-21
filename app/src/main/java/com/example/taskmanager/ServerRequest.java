package com.example.taskmanager;

import android.content.Context;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.AsyncTask;
import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;
import java.security.KeyManagementException;
import java.security.KeyStore;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.cert.Certificate;
import java.security.cert.CertificateException;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.TrustManager;
import javax.net.ssl.TrustManagerFactory;
import javax.net.ssl.X509TrustManager;

public class ServerRequest extends AsyncTask<Void, Void, String> {

    // Конец строки
    private String lineEnd = "\r\n";
    private String amp = "&";
    private String parameters = "";
    private byte [] binaryData;
    private Context mContext = null;
    // Адрес метода api для загрузки файла на сервер
    public static String SERVER_URL_DEFAULT = "https://192.168.0.158";
    public String SERVER_URL = "https://172.20.10.2";

    public ServerRequest(String serverUrl) {
        this.SERVER_URL = SERVER_URL_DEFAULT+serverUrl;
    }
    public ServerRequest(Context mContext,String serverUrl) {
        this.SERVER_URL = SERVER_URL_DEFAULT+serverUrl;
        this.mContext = mContext;
    }

    public void putTextData(String fieldName,String value) {
        if (parameters!=""){ parameters+=amp; }
        parameters+=fieldName+"="+value;
    }

    public void putTextJsonData(JSONObject js) {
        parameters=js.toString();
    }




    @Override
    protected void onPreExecute() {
        super.onPreExecute();

        if (mContext==null) {
            setAllCertificatesTrusted();
            Log.d("ServerRequest", "TRUST_ALL_CERTIFICATES_MODE");
        }
        else {
            setOnlyMyCertificateTrusted(R.raw.rootca);
            Log.d("ServerRequest", "TRUST_ONLY_CURRENT_CERTIFICATE_MODE");
        }

    }
    @Override
    protected String doInBackground(Void... params) {
        String result = null;

        try {
            URL Url = new URL(SERVER_URL);
            HttpsURLConnection connection = (HttpsURLConnection) Url.openConnection();
            connection.setHostnameVerifier(DUMMY_VERIFIER);
            connection.setDoInput(true);
            connection.setDoOutput(true);
            // Задание запросу типа POST
            connection.setRequestMethod("POST");
            connection.setRequestProperty("Content-Length", "" + Integer.toString(parameters.getBytes().length));
            OutputStream os = connection.getOutputStream();
            binaryData = parameters.getBytes("UTF-8");
            os.write(binaryData);
            binaryData = null;
            connection.connect();
            // Получение ответа от сервера
            int serverResponseCode = connection.getResponseCode();
            // Закрытие соединений и потоков
            os.flush();
            os.close();

            // Считка ответа от сервера в зависимости от успеха
            if(serverResponseCode == 200) {
                result = readStream(connection.getInputStream());
                Log.d("my",result);
            } else {
                result = readStream(connection.getErrorStream());
                Log.d("mye",result);
            }
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (ProtocolException e) {
            e.printStackTrace();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return result;
    }

    // Считка потока в строку
    public static String readStream(InputStream inputStream) throws IOException {
        StringBuffer buffer = new StringBuffer();
        BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));

        String line;
        while ((line = reader.readLine()) != null) {
            buffer.append(line);
        }

        return buffer.toString();
    }
    public JSONObject convertToJSON(String s){
        try {
            return new JSONObject(s);
        }catch (JSONException e) {
            e.printStackTrace();
        }
        return new JSONObject();
    }

    public static void setAllCertificatesTrusted(){
        // Try Trust all self-signed cert
        TrustManager localTrustmanager = new X509TrustManager() {

            @Override
            public X509Certificate[] getAcceptedIssuers() {
                return null;
            }

            @Override
            public void checkServerTrusted(X509Certificate[] chain,
                                           String authType) throws CertificateException {
            }

            @Override
            public void checkClientTrusted(X509Certificate[] chain,
                                           String authType) throws CertificateException {
            }
        };

        // Create SSLContext and set the socket factory as default
        try {
            SSLContext sslc = SSLContext.getInstance("TLS");
            sslc.init(null, new TrustManager[]{localTrustmanager},
                    new SecureRandom());
            HttpsURLConnection.setDefaultSSLSocketFactory(sslc
                    .getSocketFactory());
        } catch (NoSuchAlgorithmException e) { e.printStackTrace();
        } catch (KeyManagementException e) { e.printStackTrace(); }
    }

    private void setOnlyMyCertificateTrusted(int certRecourceId){
        CertificateFactory cf = null;
        try {
            cf = CertificateFactory.getInstance("X.509");
            InputStream caInput = new BufferedInputStream(mContext.getResources().openRawResource(certRecourceId));
            Certificate ca = null;
            try {
                ca = cf.generateCertificate(caInput);
                System.out.println("ca=" + ((X509Certificate) ca).getSubjectDN());
            } catch (CertificateException e) {
                e.printStackTrace();
            } finally {
                caInput.close();
            }

// Create a KeyStore containing our trusted CAs
            String keyStoreType = KeyStore.getDefaultType();
            KeyStore keyStore = KeyStore.getInstance(keyStoreType);
            keyStore.load(null, null);
            keyStore.setCertificateEntry("ca", ca);

// Create a TrustManager that trusts the CAs in our KeyStore
            String tmfAlgorithm = TrustManagerFactory.getDefaultAlgorithm();
            TrustManagerFactory tmf = TrustManagerFactory.getInstance(tmfAlgorithm);
            tmf.init(keyStore);

// Create an SSLContext that uses our TrustManager
            SSLContext sslContext = SSLContext.getInstance("TLS");
            sslContext.init(null, tmf.getTrustManagers(), null);
            HttpsURLConnection.setDefaultSSLSocketFactory(sslContext
                    .getSocketFactory());
        } catch (Exception e){
            e.printStackTrace();
        }
    }

    public static SSLContext getOnlyMyCertificateTrustedSSLFactory(Context mContext,int certRecourceId) throws NoSuchAlgorithmException {
        CertificateFactory cf = null;
        try {
            cf = CertificateFactory.getInstance("X.509");
            InputStream caInput = new BufferedInputStream(mContext.getResources().openRawResource(certRecourceId));
            Certificate ca = null;
            try {
                ca = cf.generateCertificate(caInput);
                System.out.println("ca=" + ((X509Certificate) ca).getSubjectDN());
            } catch (CertificateException e) {
                e.printStackTrace();
            } finally {
                caInput.close();
            }

// Create a KeyStore containing our trusted CAs
            String keyStoreType = KeyStore.getDefaultType();
            KeyStore keyStore = KeyStore.getInstance(keyStoreType);
            keyStore.load(null, null);
            keyStore.setCertificateEntry("ca", ca);

// Create a TrustManager that trusts the CAs in our KeyStore
            String tmfAlgorithm = TrustManagerFactory.getDefaultAlgorithm();
            TrustManagerFactory tmf = TrustManagerFactory.getInstance(tmfAlgorithm);
            tmf.init(keyStore);

// Create an SSLContext that uses our TrustManager
            SSLContext sslContext = SSLContext.getInstance("TLS");
            sslContext.init(null, tmf.getTrustManagers(), null);
            return sslContext;
        } catch (Exception e){
            e.printStackTrace();
        }
        return SSLContext.getInstance("TLS");
    }
    public static final HostnameVerifier DUMMY_VERIFIER = new HostnameVerifier() {
        public boolean verify(String hostname, SSLSession session) {
            return true;
        }
    };
}
