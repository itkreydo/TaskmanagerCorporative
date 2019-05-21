package com.example.taskmanager;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.widget.ImageView;
import android.widget.Toast;

import com.example.taskmanager.models.User;

public class AuthUser {
    public static final String APP_PREFERENCES = "com.example.taskmanager.settings";
    public static final String ACCESS_TOKEN_TITLE = "access_token";
    public static final String ACCESS_TOKEN_EXPIRES = "expires_in";
    public static final String USER_NAME_TITLE = "username";
    public static final String PROFESSION_TITLE = "profession";
    public static final String MANAGER_TITLE = "manager";
    public static final String USER_AVATAR_URL = "avatarURL";
    public static final String NOT_FOUND = "0";
    private static final String USER_ID = "id_user";

    private String acc_token;
    private String expiress_in;
    private User user;
    private Context mContext;
    private SharedPreferences sharedPreferences;
    SharedPreferences.Editor editor;
    AuthUser(){

    }
    public AuthUser(Context mContext){
        this.mContext = mContext;
        sharedPreferences = mContext.getSharedPreferences(APP_PREFERENCES, mContext.MODE_PRIVATE);
        editor = sharedPreferences.edit();
        init();
    }
    public void init(){
        String acc_token = sharedPreferences.getString(ACCESS_TOKEN_TITLE, NOT_FOUND);
        //checkToken(acc_token);
        String expiress_in = sharedPreferences.getString(ACCESS_TOKEN_EXPIRES, NOT_FOUND);
        String username = sharedPreferences.getString(USER_NAME_TITLE, NOT_FOUND);
        String profession = sharedPreferences.getString(PROFESSION_TITLE, NOT_FOUND);
        String manager = sharedPreferences.getString(MANAGER_TITLE, NOT_FOUND);
        String avatar = sharedPreferences.getString(USER_AVATAR_URL, NOT_FOUND);
        String id_user = sharedPreferences.getString(USER_ID, NOT_FOUND);

        this.acc_token = acc_token;
        this.expiress_in = expiress_in;
        user = new User();
        user.setId(Integer.parseInt(id_user));
        user.setNickname(username);
        user.setProfession(profession);
        user.setManager(manager);
//        user.setAvatar(avatar);
    }
    public boolean checkTokenExist(){
        if (this.acc_token == NOT_FOUND)
            return false;
        else
            return true;
    }
    private void checkToken(String token){
        if (token == NOT_FOUND){
            sessionClose();
        }
    }
    public void sessionClose(){
        editor.clear().commit();
        Intent i = new Intent(mContext,LoginActivity.class);
        i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        mContext.startActivity(i);
    }
    public void sessionStart(String acc_token,String expires_in,User user){
        this.mContext = mContext;
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(ACCESS_TOKEN_TITLE, acc_token);
        editor.putString(ACCESS_TOKEN_EXPIRES, expires_in);
        editor.putString(USER_NAME_TITLE, user.getNickname());
        editor.putString(MANAGER_TITLE, user.getManager());
        editor.putString(PROFESSION_TITLE, user.getProfession());
        editor.putString(USER_ID, Integer.toString(user.getId()));

        editor.putString(USER_AVATAR_URL, user.getAvatarUrl());
        editor.commit();
    }
    public String getAcc_token() {
        return acc_token;
    }
    public void loadAvatar(ImageView iv){
        getBitmapFromUrl getBitmapFromUrl = new getBitmapFromUrl(mContext, ServerRequest.SERVER_URL_DEFAULT + "/getFiles/avatar?token=" + acc_token,iv) {
            @Override
            protected void onPostExecute(Bitmap bitmap) {
                super.onPostExecute(bitmap);
                iv.setImageBitmap(bitmap);
            }
        };
        getBitmapFromUrl.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
    }
    public void setAcc_token(String acc_token) {
        this.acc_token = acc_token;
    }

    public String getExpiress_in() {
        return expiress_in;
    }

    public void setExpiress_in(String expiress_in) {
        this.expiress_in = expiress_in;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }
}
