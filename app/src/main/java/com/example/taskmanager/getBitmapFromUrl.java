package com.example.taskmanager;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.ImageView;

import com.example.taskmanager.models.User;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;

import javax.net.ssl.HttpsURLConnection;
class getBitmapFromUrl extends AsyncTask<Void, Void, Bitmap> {
    String src = null;
    ImageView iv = null;
    Context ctx = null;
    User user = null;
    Object obj = null;
    getBitmapFromUrl(Context ctx,String src,ImageView iv) {
        this.src = src;
        this.iv = iv;
        this.ctx = ctx;
    }
    getBitmapFromUrl(Context ctx,String src,User u ) {
        this.src = src;
        this.iv = iv;
        this.ctx = ctx;
        this.user = u;
    }
    getBitmapFromUrl(Context ctx,String src,Object obj ) {
        this.src = src;
        this.iv = iv;
        this.ctx = ctx;
        this.obj = obj;
    }
    getBitmapFromUrl(Context ctx,String src) {
        this.src = src;
        this.iv = iv;
        this.ctx = ctx;
    }

    @Override
    protected void onPostExecute(Bitmap bitmap) {
        super.onPostExecute(bitmap);
    }

    @Override
    protected Bitmap doInBackground(Void... voids) {
        try {
            URL url = new URL(src);
            ServerRequest.setAllCertificatesTrusted();
            HttpsURLConnection connection = (HttpsURLConnection) url.openConnection();
            connection.setHostnameVerifier(ServerRequest.DUMMY_VERIFIER);

            connection.setDoInput(true);
            connection.setUseCaches(false);
            connection.connect();
            InputStream input = connection.getInputStream();

            Bitmap myBitmap = BitmapFactory.decodeStream(input);
            if (myBitmap == null) {
                myBitmap = BitmapFactory.decodeResource(ctx.getResources(), R.mipmap.ic_launcher_round);
                Log.d("1","bitmap null");
            }
            Log.d("1","bitmap yes");
            //input.close();
            //connection.disconnect();
            return myBitmap;
        } catch (
                IOException e) {
            e.printStackTrace();
            return null;
        }

    }
}