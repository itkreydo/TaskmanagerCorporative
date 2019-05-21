package com.example.taskmanager.other;

import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.util.Log;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
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

/**
 * Загружает файл на сервер
 */
public class FilesUploadingTaskNew extends AsyncTask<Void, Void, String> {



    // Путь к файлу в памяти устройства
    private Bitmap filePath;

    // Адрес метода api для загрузки файла на сервер
    public static final String API_FILES_UPLOADING_PATH = "http://192.168.0.180:3000/upload";

    // Ключ, под которым файл передается на сервер
    public static final String FORM_FILE_NAME = "test";

    public FilesUploadingTaskNew(Bitmap filePath) {
        this.filePath = filePath;
    }

    @Override
    protected String doInBackground(Void... params) {
        // Результат выполнения запроса, полученный от сервера
        String result = null;

        try {
            // Создание ссылки для отправки файла
            URL uploadUrl = new URL(API_FILES_UPLOADING_PATH);

            // Создание соединения для отправки файла
            HttpURLConnection connection = (HttpURLConnection) uploadUrl.openConnection();
            // Задание запросу типа POST
            connection.setRequestMethod("POST");
            // Разрешение ввода соединению
            connection.setDoInput(true);
            // Разрешение вывода соединению
            connection.setDoOutput(true);
            // Отключение кеширования
            connection.setUseCaches(false);//connection.setRequestProperty("Cache-Control", "no-cache");

            connection.setReadTimeout(35000);
            connection.setConnectTimeout(35000);


            // Задание необходимых свойств запросу
            connection.setRequestProperty("Connection", "Keep-Alive");
            //connection.setRequestProperty("Content-Type", "multipart/form-data; boundary="+boundary);

            OutputStream os = connection.getOutputStream();
            filePath.compress(Bitmap.CompressFormat.JPEG, 100, os);
            os.flush();
            os.close();

            int serverResponseCode = connection.getResponseCode();
            System.out.println("Response Code: " + serverResponseCode);

            InputStream in = new BufferedInputStream(connection.getInputStream());
            Log.d("sdfs", "sfsd");
            BufferedReader responseStreamReader = new BufferedReader(new InputStreamReader(in));
            String line = "";
            StringBuilder stringBuilder = new StringBuilder();
            while ((line = responseStreamReader.readLine()) != null)
                stringBuilder.append(line).append("\n");
            responseStreamReader.close();

            String response = stringBuilder.toString();
            System.out.println(response);

            connection.disconnect();
//            // Считка ответа от сервера в зависимости от успеха
//            if(serverResponseCode == 200) {
//                result = readStream(connection.getInputStream());
//                Log.d("my",result);
//            } else {
//                result = readStream(connection.getErrorStream());
//                Log.d("mye",result);
//            }
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
}