package com.example.taskmanager.other;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.util.Log;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

import static java.nio.charset.StandardCharsets.UTF_8;

/**
 * Загружает файл на сервер
 */
public class FilesUploadingTask extends AsyncTask<Void, Void, String> {

    // Конец строки
    private String lineEnd = "\r\n";
    // Два тире
    private String twoHyphens = "--";
    // Разделитель
    private String boundary =  "----WebKitFormBoundary9xFB2hiUhzqbBQ4M";
    private String boundaryLine = twoHyphens + boundary + lineEnd;
    private ByteArrayOutputStream dataToSendBinary= new ByteArrayOutputStream();
    // Переменные для считывания файла в оперативную память
    private int bytesRead, bytesAvailable, bufferSize;
    private byte[] buffer;
    private int maxBufferSize = 1*1024*1024;

    // Адрес метода api для загрузки файла на сервер
    public static String API_FILES_UPLOADING_PATH = "http://192.168.0.180:3000/upload";
    // Ключ, под которым файл передается на сервер
    public static final String FORM_FILE_NAME = "files";
    public int filename_count=0;
    public FilesUploadingTask(String serverUrl) {
        this.API_FILES_UPLOADING_PATH = serverUrl;
    }

    public void putTextData(String fieldName,String text) throws IOException {
        String tmp_info = boundaryLine;
        tmp_info += "Content-Disposition: form-data; name=\""+fieldName+"\"" + lineEnd + lineEnd;
        tmp_info += text+lineEnd;
        this.dataToSendBinary.write(tmp_info.getBytes());
    }
    public void putAnyFileData(Uri uriFile, String fileName) throws IOException {
        String nextFileName = FORM_FILE_NAME;

        String tmp_info = boundaryLine;
        tmp_info += "Content-Disposition: form-data; name=\"" +
                nextFileName + "\"; filename=\"" + fileName + "\"" + lineEnd;
        tmp_info += "Content-Type: application/octet-stream" + lineEnd+lineEnd;

        dataToSendBinary.write(tmp_info.getBytes());

        FileInputStream fileInputStream = new FileInputStream(new File(uriFile.getPath()));

            bytesAvailable = fileInputStream.available();
            bufferSize = Math.min(bytesAvailable, maxBufferSize);
            buffer = new byte[bufferSize];

            // Считывание файла в оперативную память и запись его в соединение
            bytesRead = fileInputStream.read(buffer, 0, bufferSize);

            while (bytesRead > 0) {
                dataToSendBinary.write(buffer, 0, bufferSize);
                bytesAvailable = fileInputStream.available();
                bufferSize = Math.min(bytesAvailable, maxBufferSize);
                bytesRead = fileInputStream.read(buffer, 0, bufferSize);
            }

        dataToSendBinary.write(lineEnd.getBytes());

    }
    public void putFileData(Bitmap file,String fileName) throws IOException {
        String nextFileName = FORM_FILE_NAME;

        String tmp_info = boundaryLine;
        tmp_info += "Content-Disposition: form-data; name=\"" +
                nextFileName + "\"; filename=\"" + fileName + "\"" + lineEnd;
        tmp_info += "Content-Type: image/jpeg" + lineEnd+lineEnd;

        dataToSendBinary.write(tmp_info.getBytes());
        file.compress(Bitmap.CompressFormat.JPEG, 100, dataToSendBinary);
        dataToSendBinary.write(lineEnd.getBytes());

    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        try {
            //add end of form symbols
            dataToSendBinary.write((twoHyphens + boundary + twoHyphens + lineEnd).getBytes());
        } catch (IOException e) {
            e.printStackTrace();
        }

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

            // Разрешение ввода соединению
            connection.setDoInput(true);
            // Разрешение вывода соединению
            connection.setDoOutput(true);
            // Отключение кеширования
            connection.setUseCaches(false);

            // Задание запросу типа POST
            connection.setRequestMethod("POST");

            // Задание необходимых свойств запросу
            connection.setRequestProperty("Connection", "Keep-Alive");
            connection.setRequestProperty("Content-Type", "multipart/form-data; boundary="+boundary);

            // Создание потока для записи в соединение
            DataOutputStream outputStream = new DataOutputStream(connection.getOutputStream());
            outputStream.write(dataToSendBinary.toByteArray());

            // Получение ответа от сервера
            int serverResponseCode = connection.getResponseCode();

            // Закрытие соединений и потоков
            dataToSendBinary.close();
            outputStream.flush();
            outputStream.close();

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
}