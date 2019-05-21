package com.example.taskmanager;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.ContextMenu;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.taskmanager.adapters.WorkerMiniAdapter;
import com.example.taskmanager.models.Task;
import com.example.taskmanager.models.User;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Map;

public class CreateTaskActivity extends AppCompatActivity  {

    private static final int CM_DELETE_ID = 1;

    int DIALOG_DATE = 1;
    int myYear = 2019;
    int myMonth = 02;
    int myDay = 03;
    TextView tvDate;
    ListView lvWorkers;
    EditText etDateDeadline,etTitle,etDescription;
    Button btnChooseDate;
    final int REQUEST_CODE_CHOOSE_WORKERS=1;
    ArrayList<User> adaprerDataDeleted;
    ArrayList<User> adaprerData;
    Map<String, Object> m;
    WorkerMiniAdapter workerMiniAdapter ;
    String type_Activity = null;
    String id_task = null;
    String BarTitle = "Создание задачи";
    public String ServerUrl = "/createTask";

    public static final String ACCESS_TOKEN_TITLE = "access_token";
    public static final String ACCESS_TOKEN_EXPIRES = "expires_in";
    public static final String USER_NAME_TITLE = "username";
    public static final String NOT_FOUND = "0";
    private Intent i;
    private static final String APP_PREFERENCES = "com.example.taskmanager.settings";
    private SharedPreferences sharedPreferences;
    SharedPreferences.Editor editor;
    AuthUser authUser;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        authUser = new AuthUser(getApplicationContext());
        authUser.init();
        if (!authUser.checkTokenExist())
            authUser.sessionClose();
        setContentView(R.layout.create_task_activity);
        Intent intent = getIntent();
        type_Activity = intent.getStringExtra("type_activity");
        if (type_Activity != null){
            id_task = intent.getStringExtra("id_task");
            BarTitle = "Редактирование задачи";
            ServerUrl = "/editTask";
        }
        getSupportActionBar().setTitle(BarTitle);

        btnChooseDate = (Button) findViewById(R.id.btn_choosedate);
        etDateDeadline = (EditText) findViewById(R.id.taskCreate_deadline);
        etTitle = (EditText) findViewById(R.id.taskCreate_title);
        etDescription = (EditText) findViewById(R.id.taskCreate_description);
        lvWorkers = (ListView) findViewById(R.id.listview_workers);


        // упаковываем данные в понятную для адаптера структуру
        adaprerData = new ArrayList<User>();
        adaprerDataDeleted = new ArrayList<User>();

        // создаем адаптер
        workerMiniAdapter = new WorkerMiniAdapter(this, adaprerData);
        //if edit
        if (type_Activity!=null){
            ServerRequest requestToServer = new ServerRequest("/getTaskDetail"){
                @Override
                protected void onPostExecute(String result) {
                    super.onPostExecute(result);
                    JSONObject res  = this.convertToJSON(result);
                    try {
                        if (res.getString("status").equals("OK")){
                            Task t = Task.convertTask(res.getJSONObject("task"));
                            etTitle.setText(t.getTitle());
                            etDescription.setText(t.getDescription());
                            etDateDeadline.setText(t.getdeadlineDate_time());

                            for (int i =0;i<t.getWorkers().size();i++){
                                 getBitmapFromUrl getBitmapFromUrl = new getBitmapFromUrl(getApplicationContext(), ServerRequest.SERVER_URL_DEFAULT + "/getFiles/avatar?token=" + authUser.getAcc_token() + "&id_user=" + t.getWorkers().get(i).getId(),t.getWorkers().get(i)) {
                                    @Override
                                    protected void onPostExecute(Bitmap bitmap) {
                                        super.onPostExecute(bitmap);
                                        user.setAvatar(bitmap);
                                        workerMiniAdapter.notifyDataSetChanged();
                                    }
                                };
                                getBitmapFromUrl.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
                            }
                            adaprerData.addAll(t.getWorkers());
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
        }

        lvWorkers.setAdapter(workerMiniAdapter);
        registerForContextMenu(lvWorkers);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.add_task_menu, menu);
        return true;
    }
    @Override
    public void onCreateContextMenu(ContextMenu menu, View v,
                                    ContextMenu.ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);
        menu.add(0, CM_DELETE_ID, 0, "Удалить исполнителя");
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {
        if (item.getItemId() == CM_DELETE_ID) {
            // получаем инфу о пункте списка
            AdapterView.AdapterContextMenuInfo acmi = (AdapterView.AdapterContextMenuInfo) item.getMenuInfo();
            // удаляем Map из коллекции, используя позицию пункта в списке
            if (type_Activity!=null){
                if (acmi.position != 0) {
                    adaprerData.get(acmi.position).setType("del");
                    adaprerDataDeleted.add(adaprerData.get(acmi.position));
                }else{
                    Toast.makeText(this, "Вы не можете удалить руководителя", Toast.LENGTH_SHORT).show();
                }
            }
            if (type_Activity==null)
                adaprerData.remove(acmi.position);
            else
                if (acmi.position != 0)
                    adaprerData.remove(acmi.position);
            // уведомляем, что данные изменились
            workerMiniAdapter.notifyDataSetChanged();
            return true;
        }
        return super.onContextItemSelected(item);
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case R.id.item_accept:

                JSONArray workers = new JSONArray();
                //get id workers in array
                try {

                    for (int i=0;i<adaprerData.size();i++){
                        JSONObject workerInfo = new JSONObject();
                        workerInfo.put("id", adaprerData.get(i).getId());
                        workerInfo.put("type", adaprerData.get(i).getType());
                        workers.put(workerInfo);
                    }
                    if (type_Activity !=null){
                        for (int i=0;i<adaprerDataDeleted.size();i++){
                            JSONObject workerInfo = new JSONObject();
                            workerInfo.put("id", adaprerDataDeleted.get(i).getId());
                            workerInfo.put("type", adaprerDataDeleted.get(i).getType());
                            workers.put(workerInfo);
                        }
                    }

                } catch (JSONException e) { e.printStackTrace(); }


                ServerRequest requestToServer = new ServerRequest(ServerUrl){
                    @Override
                    protected void onPostExecute(String result) {
                        super.onPostExecute(result);
                        JSONObject res  = this.convertToJSON(result);
                        try {
                            if (res.getString("status").equals("OK")){
                                Toast.makeText(getApplicationContext(), "Задача успешно добавлена!", Toast.LENGTH_SHORT).show();
                                finish();
                            }else if(res.getString("status").equals("OLD_TOKEN")){
                                Toast.makeText(getApplicationContext(), "Токен устарел", Toast.LENGTH_SHORT).show();
                                authUser.sessionClose();
                            }
                            else{
                                Toast.makeText(getApplicationContext(), "Пришло непонятно что", Toast.LENGTH_SHORT).show();
                            }
                        } catch (JSONException e) { e.printStackTrace(); }


                    }
                };
                requestToServer.putTextData(AuthUser.ACCESS_TOKEN_TITLE, authUser.getAcc_token());
                if (type_Activity!=null)
                    requestToServer.putTextData("id_task",id_task);
                requestToServer.putTextData("title", etTitle.getText().toString());
                requestToServer.putTextData("description", etDescription.getText().toString());
                try {
                    requestToServer.putTextData("date_deadline", prepareDate(etDateDeadline.getText().toString()));
                } catch (ParseException e) { e.printStackTrace(); }

                requestToServer.putTextData("workers", workers.toString());
                requestToServer.execute();
                break;
        }
        return super.onOptionsItemSelected(item);
    }
    public void onclickChooseWorker(View view) {
        Intent i = new Intent(this, ChooseWorkersActivity.class);
        startActivityForResult(i, REQUEST_CODE_CHOOSE_WORKERS);
    }
    public boolean checkIssetWorker(String id){
        for (int i = 0;i<adaprerData.size();i++){
            if (Integer.toString(adaprerData.get(i).getId()) == id)
                return true;
        }
        return false;
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        // запишем в лог значения requestCode и resultCode
        Log.d("myLogs", "requestCode = " + requestCode + ", resultCode = " + resultCode);
        // если пришло ОК
        if (resultCode == RESULT_OK) {
            switch (requestCode) {
                case REQUEST_CODE_CHOOSE_WORKERS:

                    ArrayList<Integer> workersId = (ArrayList<Integer>) data.getIntegerArrayListExtra("workers_id");
                    ArrayList<String> workersName = (ArrayList<String>) data.getStringArrayListExtra("workers_name");
                    for (int i =0;i<workersId.size();i++){
                        if (checkIssetWorker(workersId.get(i).toString())==true)
                            continue;
                        User u = new User();
                        u.setId(workersId.get(i));
                        u.setNickname(workersName.get(i));
                        u.setType("new");
                            getBitmapFromUrl getBitmapFromUrl = new getBitmapFromUrl(getApplicationContext(), ServerRequest.SERVER_URL_DEFAULT + "/getFiles/avatar?token=" + authUser.getAcc_token() + "&id_user=" + u.getId(), u) {
                                @Override
                                protected void onPostExecute(Bitmap bitmap) {
                                    super.onPostExecute(bitmap);
                                    user.setAvatar(bitmap);
                                    workerMiniAdapter.notifyDataSetChanged();
                                }
                            };
                            getBitmapFromUrl.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
                        adaprerData.add(u);
                    }
                    // уведомляем, что данные изменились
                    workerMiniAdapter.notifyDataSetChanged();

                    //tvText.setTextColor(color);
                    break;
            }
            // если вернулось не ОК
        } else {
            Toast.makeText(this, "Wrong result", Toast.LENGTH_SHORT).show();
        }
    }

    public void onclickChooseDate(View view) {
        showDialog(DIALOG_DATE);
    }


    protected Dialog onCreateDialog(int id) {
        if (id == DIALOG_DATE) {
            DatePickerDialog tpd = new DatePickerDialog(this, myCallBack, myYear, myMonth, myDay);
            return tpd;
        }
        return super.onCreateDialog(id);
    }

    DatePickerDialog.OnDateSetListener myCallBack = new DatePickerDialog.OnDateSetListener() {

        public void onDateSet(DatePicker view, int year, int monthOfYear,
                              int dayOfMonth) {
            myYear = year;
            myMonth = monthOfYear+1;
            myDay = dayOfMonth;
            etDateDeadline.setText(myDay + "." + myMonth + "." + myYear);
            //tvDate.setText("Today is " + myDay + "/" + myMonth + "/" + myYear);
        }
    };
    public String prepareDate(String date) throws ParseException {
        SimpleDateFormat df_old = new SimpleDateFormat("dd.MM.yyyy");
        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd");
        Toast.makeText(this, df.format(df_old.parse(date)).toString(), Toast.LENGTH_SHORT).show();
        return df.format(df_old.parse(date)).toString();
    }
}
