package com.example.taskmanager.other;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.util.Log;

import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
public class DownloadImageTask extends AsyncTask<String, Void, Bitmap> {
    Bitmap mIcon11;

    public DownloadImageTask(){
        Log.d("1337", "1337");
    }

    protected Bitmap doInBackground(String... urls) {
        String urldisplay = urls[0];
        try {
            Log.d("1337", urldisplay);
            URL url = new URL(urldisplay);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setDoInput(true);
            connection.connect();
            InputStream input = connection.getInputStream();
            mIcon11 = BitmapFactory.decodeStream(input);

        } catch (Exception e) {
            e.printStackTrace();
            Log.d("1337",e.toString());
            return null;
        }
        return mIcon11;
    }

    public Bitmap getBitmap() {
        return this.mIcon11;
    }
}
