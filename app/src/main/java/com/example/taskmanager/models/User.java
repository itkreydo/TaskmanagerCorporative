package com.example.taskmanager.models;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import com.example.taskmanager.MainActivity;
import com.example.taskmanager.R;

import org.json.JSONArray;
import org.json.JSONException;

import java.util.ArrayList;

public class User {
    public static final Bitmap avatarDefault = Bitmap.createBitmap(58, 58, Bitmap.Config.ARGB_4444);
    int id;
    String profession;
    int num_tasks;
    String nickname;
    String profileUrl;
    String avatarUrl;
    Bitmap avatar;
    public int progress;
    boolean checked=false;
    String manager;
    String type="old";

    public int getId() {
        return id;
    }
    public void setId(int id) {
        this.id = id;
    }
    public String getProfession() {
        return profession;
    }
    public void setProfession(String profession) {
        this.profession = profession;
    }
    public int getNum_tasks() {
        return num_tasks;
    }
    public void setNum_tasks(int num_tasks) {
        this.num_tasks = num_tasks;
    }

    public String getNickname() {
        return nickname;
    }

    public void setNickname(String nickname) {
        this.nickname = nickname;
    }

    public String getProfileUrl() {
        return profileUrl;
    }

    public void setProfileUrl(String profileUrl) {
        this.profileUrl = profileUrl;
    }
    public Bitmap getAvatar() {
        return avatar;
    }
    public void setAvatar(Bitmap avatar) {
        this.avatar = avatar;
    }
    public boolean isChecked() {
        return checked;
    }
    public void setChecked(boolean checked) {
        this.checked = checked;
    }

    public int getProgress() {
        return progress;
    }
    public void setProgress(int progress) {
        this.progress = progress;
    }

    public static ArrayList<User> convertJson(JSONArray jsonArrayMessage){
        ArrayList<User> UserList = new ArrayList<User>();
        Bitmap.Config conf = Bitmap.Config.ARGB_4444; // see other conf types
        Bitmap bmp = Bitmap.createBitmap(58, 58, conf);
        try {
            for (int i=0;i<jsonArrayMessage.length();i++){
                User t = new User();
                t.setId(jsonArrayMessage.getJSONObject(i).getInt("id"));
                t.setNickname(jsonArrayMessage.getJSONObject(i).getString("name"));
                t.setProfession(jsonArrayMessage.getJSONObject(i).getString("profession"));
                t.setNum_tasks(jsonArrayMessage.getJSONObject(i).getInt("num_tasks"));
                //t.setAvatarUrl(jsonArrayMessage.getJSONObject(i).getString("avatarURL"));
                t.setAvatar(bmp);
                UserList.add(t);
            }
        }catch (JSONException e) {
            e.printStackTrace();
        }
        return UserList;
    }

    public String getManager() {
        return manager;
    }

    public void setManager(String manager) {
        this.manager = manager;
    }
    public boolean isManager(){
        if (manager.equals("0")){
            return true;
        }
        return false;
    }

    public String getAvatarUrl() {
        return avatarUrl;
    }

    public void setAvatarUrl(String avatarUrl) {
        this.avatarUrl = avatarUrl;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }
}
