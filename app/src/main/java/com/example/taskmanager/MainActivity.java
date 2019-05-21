package com.example.taskmanager;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;

import com.example.taskmanager.models.User;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

import javax.net.ssl.HttpsURLConnection;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {
    private final int IDD_LIST_CATS = 1;

    private Intent i;
    private SharedPreferences sharedPreferences;
    SharedPreferences.Editor editor;
    User currentUser;
    MainTaskFragment mainTask;
    WorkersFragment workerFragment;
    FragmentTransaction ftrans;
    Bundle bundle;
    private AuthUser authUser;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        authUser = new AuthUser(getApplicationContext());
        authUser.init();
        if (!authUser.checkTokenExist())
            authUser.sessionClose();
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Задачи");

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        Menu nav_Menu = navigationView.getMenu();

        if (!authUser.getUser().isManager())
            nav_Menu.findItem(R.id.nav_workers).setVisible(false);
        View navHeader = navigationView.getHeaderView(0);
        ImageView avatar_view = (ImageView) navHeader.findViewById(R.id.user_avatar);
        TextView username_view = (TextView) navHeader.findViewById(R.id.username_title);
        TextView profession_view = (TextView) navHeader.findViewById(R.id.profession_title);
        username_view.setText(authUser.getUser().getNickname());
        profession_view.setText(authUser.getUser().getProfession());
        avatar_view.setImageBitmap(authUser.getUser().getAvatar());
        authUser.loadAvatar(avatar_view);
        bundle = new Bundle();
        bundle.putString(authUser.ACCESS_TOKEN_TITLE, authUser.getAcc_token() );

        mainTask = new MainTaskFragment();
        mainTask.setArguments(bundle);
        workerFragment = new WorkersFragment();
        workerFragment.setArguments(bundle);
        ftrans = getSupportFragmentManager().beginTransaction();
        ftrans.replace(R.id.container, mainTask);
        ftrans.commit();
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        int id = item.getItemId();

        ftrans = getSupportFragmentManager().beginTransaction();

        if (id == R.id.nav_tasks) {
            getSupportActionBar().setTitle("Задачи");
            ftrans.replace(R.id.container, mainTask);
        } else if (id == R.id.nav_workers) {
            getSupportActionBar().setTitle("Сотрудники");
            ftrans.replace(R.id.container, workerFragment);
        } else if (id == R.id.nav_settings) {

        }
        ftrans.commit();

        if (id == R.id.nav_exit) {
            authUser.sessionClose();
            finish();
        }
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

public static boolean isManager(Context ctx){
    SharedPreferences sharedPreferences;
    SharedPreferences.Editor editor;
    sharedPreferences = (SharedPreferences) ctx.getSharedPreferences(AuthUser.APP_PREFERENCES, ctx.MODE_PRIVATE);
    String m = sharedPreferences.getString("manager", AuthUser.NOT_FOUND);
    if (m.equals("0")){
        return true;
    }
    return false;

}
public final Context getMainActivityContext(){
        return this.getApplicationContext();
}

}
