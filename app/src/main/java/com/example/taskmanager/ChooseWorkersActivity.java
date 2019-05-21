package com.example.taskmanager;

import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import com.example.taskmanager.models.User;

import java.util.ArrayList;
import java.util.List;

public class ChooseWorkersActivity extends AppCompatActivity {
    Bundle bundle;
    WorkersFragment workerFragment;
    FragmentTransaction ftrans;
    private SharedPreferences sharedPreferences;
    private static final String APP_PREFERENCES = "com.example.taskmanager.settings";
    SharedPreferences.Editor editor;
    AuthUser authUser;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        authUser = new AuthUser(getApplicationContext());
        authUser.init();
        if (!authUser.checkTokenExist())
            authUser.sessionClose();

        setContentView(R.layout.activity_choose_workers);


        getSupportActionBar().setTitle("Выберите исполнителей");

        bundle = new Bundle();
        bundle.putString(AuthUser.ACCESS_TOKEN_TITLE, authUser.getAcc_token() );
        bundle.putBoolean("choosable", true);

        workerFragment = new WorkersFragment();
        workerFragment.setArguments(bundle);
        ftrans = getSupportFragmentManager().beginTransaction();
        ftrans.replace(R.id.container, workerFragment);
        ftrans.commit();

    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.add_task_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case R.id.item_accept:
                ArrayList<Integer> chosen_users =  workerFragment.getChosenUsersIdFromAdapter();
                ArrayList<String> chosen_usersName =  workerFragment.getChosenUsersNameFromAdapter();
                Intent intent = new Intent();
                intent.putIntegerArrayListExtra("workers_id", chosen_users);
                intent.putStringArrayListExtra("workers_name", chosen_usersName);
                setResult(RESULT_OK, intent);
                finish();

                break;
        }

        return super.onOptionsItemSelected(item);
    }
}
