package com.example.taskmanager;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.taskmanager.adapters.WorkerMiniAdapter;
import com.example.taskmanager.models.Task;
import com.example.taskmanager.models.User;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Map;


public class TaskDetailActivity extends AppCompatActivity {
    private static final String APP_PREFERENCES = "com.example.taskmanager.settings";
    private SharedPreferences sharedPreferences;
    SharedPreferences.Editor editor;
    TextView tv_title,tv_description,tv_deadline,tv_workers;
    String username;
    FragmentDialog fdialog;
    FragmentTransaction ftrans;

    ArrayList<User> adaprerData;
    Map<String, Object> m;
    WorkerMiniAdapter workerMiniAdapter;
    ListView lvWorkers;
    AuthUser authUser;
    Bundle bundle;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        authUser = new AuthUser(getApplicationContext());
        authUser.init();
        if (!authUser.checkTokenExist())
            authUser.sessionClose();
        setContentView(R.layout.activity_task_detail);

        Intent intent = getIntent();

        String id_task = intent.getStringExtra("id_task");

        getSupportActionBar().setTitle("Детали задачи");

        tv_title=(TextView) findViewById(R.id.taskDetail_title);
        tv_description=(TextView) findViewById(R.id.taskDetail_description);
        tv_deadline=(TextView) findViewById(R.id.taskDetail_deadline);
        tv_workers=(TextView) findViewById(R.id.taskDetail_workers);
        lvWorkers = (ListView) findViewById(R.id.lv_detail_workers);


        // упаковываем данные в понятную для адаптера структуру
        adaprerData = new ArrayList<User>();
        // массив имен атрибутов, из которых будут читаться данные
        String[] from = { "name", "progress" };
        // массив ID View-компонентов, в которые будут вставлять данные
        int[] to = { R.id.tvWorkerName, R.id.tvWorkersId};

        // создаем адаптер
        workerMiniAdapter = new WorkerMiniAdapter(this, adaprerData);
        //sAdapter.setViewBinder(new MyViewWorkerTaskBinder());
        lvWorkers.setAdapter(workerMiniAdapter);

        bundle = new Bundle();
        bundle.putString("id_task", id_task);



        ServerRequest requestToServer = new ServerRequest("/getTaskDetail"){
            @Override
            protected void onPostExecute(String result) {
                super.onPostExecute(result);
                JSONObject res  = this.convertToJSON(result);
                try {
                    if (res.getString("status").equals("OK")){
                        Task t = Task.convertTask(res.getJSONObject("task"));
                        tv_title.setText(t.getTitle());
                        tv_description.setText(t.getDescription());
                        tv_deadline.setText(t.getdeadlineDate_time());

                        for (int i =0;i<t.getWorkers().size();i++){
                            User u = new User();
                            u.setNickname(t.getWorkers().get(i).getNickname());
                            u.setId(t.getWorkers().get(i).getId());
                            if (i==0)
                                u.setProgress(777);
                            else
                                u.setProgress(t.getWorkers().get(i).getProgress());
                            adaprerData.add(u);
                            //load avatars
                            getBitmapFromUrl getBitmapFromUrl = new getBitmapFromUrl(getApplicationContext(), ServerRequest.SERVER_URL_DEFAULT + "/getFiles/avatar?token=" + authUser.getAcc_token() + "&id_user=" + u.getId(),u) {
                                @Override
                                protected void onPostExecute(Bitmap bitmap) {
                                    super.onPostExecute(bitmap);
                                    user.setAvatar(bitmap);
                                    workerMiniAdapter.notifyDataSetChanged();
                                }
                            };
                            getBitmapFromUrl.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
                        }
                        // уведомляем, что данные изменились
                        workerMiniAdapter.notifyDataSetChanged();
                    }else{
                        Toast.makeText(getApplicationContext(), "Пришло непонятно что", Toast.LENGTH_SHORT).show();
                    }
                } catch (JSONException e) { e.printStackTrace(); }


            }
        };
        requestToServer.putTextData(AuthUser.ACCESS_TOKEN_TITLE, authUser.getAcc_token());
        requestToServer.putTextData("id_task", id_task);
        requestToServer.execute();
        // получение вью нижнего экрана
        //LinearLayout llBottomSheet = (LinearLayout) findViewById(R.id.bottom_sheet);
// настройка поведения нижнего экрана
        //BottomSheetBehavior bottomSheetBehavior = BottomSheetBehavior.from(llBottomSheet);
// настройка состояний нижнего экрана
        //bottomSheetBehavior.setState(BottomSheetBehavior.STATE_EXPANDED);
        fdialog = new FragmentDialog();
        fdialog.setArguments(bundle);
        ftrans = getSupportFragmentManager().beginTransaction();
        ftrans.replace(R.id.container_chat, fdialog);
        ftrans.commit();
    }

}
