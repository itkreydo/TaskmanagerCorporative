package com.example.taskmanager;

import android.bluetooth.BluetoothClass;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.JsonReader;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.example.taskmanager.models.User;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Date;



public class LoginActivity extends AppCompatActivity {

    private EditText inp_login;
    private EditText inp_password;

    private Intent i;
    private SharedPreferences sharedPreferences;
    private AuthUser authUser;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        authUser = new AuthUser(getApplicationContext());
        i = new Intent(this,MainActivity.class);
        if (authUser.checkTokenExist()){
            i.putExtra(AuthUser.ACCESS_TOKEN_TITLE,authUser.getAcc_token());
            startActivity(i);
        }

        setContentView(R.layout.activity_login);
        getSupportActionBar().hide();
        inp_login = (EditText) findViewById(R.id.editText_login);
        inp_password = (EditText) findViewById(R.id.editText_password);

    }

    public void btnLogin(View view) throws IOException, JSONException {
        ServerRequest requestToServer = new ServerRequest(this,"/login"){
            @Override
            protected void onPostExecute(String result) {
                super.onPostExecute(result);
                JSONObject res  = this.convertToJSON(result);
                try {
                    if (res.getString("status").equals("OK")){
                        User u = new User();
                        u.setNickname(res.getString("username"));
                        u.setProfession(res.getString("profession"));
                        u.setManager(res.getString("manager"));
                        u.setAvatarUrl(res.getString("avatarURL"));
                        u.setId(res.getInt("id_user"));
                        authUser.sessionStart(res.getString("access_token"),res.getString("expires_in"),u);
                        startActivity(i);

                    }else{
                        Toast.makeText(LoginActivity.this, "Вы ввели неправильные данные", Toast.LENGTH_SHORT).show();
                    }
                } catch (JSONException e) { e.printStackTrace(); }


            }
        };

        requestToServer.putTextData("login", inp_login.getText().toString());
        requestToServer.putTextData("password", inp_password.getText().toString());

        requestToServer.execute();

    }

}
