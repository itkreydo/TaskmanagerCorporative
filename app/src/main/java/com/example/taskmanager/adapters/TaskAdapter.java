package com.example.taskmanager.adapters;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.widget.RecyclerView;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;


import com.example.taskmanager.AuthUser;
import com.example.taskmanager.CreateTaskActivity;
import com.example.taskmanager.LoginActivity;
import com.example.taskmanager.MainActivity;
import com.example.taskmanager.R;
import com.example.taskmanager.ServerRequest;
import com.example.taskmanager.TaskDetailActivity;
import com.example.taskmanager.models.Task;

import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.List;
public class TaskAdapter extends RecyclerView.Adapter {
    private boolean isManager=false;
    private Context mContext;
    private List<Task> mTaskList;
    SharedPreferences sharedPreferences;

    private final View.OnClickListener mOnClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            String id = (String) v.findViewById(R.id.task_title).getTag().toString();
            Intent intent = new Intent(mContext, TaskDetailActivity.class);
            intent.putExtra("id_task", id);
            mContext.startActivity(intent);
        }
    };
    private final View.OnLongClickListener mOnLongClickListener = new View.OnLongClickListener() {
        @Override
        public boolean onLongClick(View v) {
            if (MainActivity.isManager(mContext))
                showOnLongClickPopupMenu(v);
            return false;
        }

    };

//    private final View.OnCreateContextMenuListener mOnContextMenuListener = new View.OnCreateContextMenuListener() {
//        @Override
//        public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
//                menu.setHeaderTitle("Select The Action");
//                menu.add(this, v.getId(), 0, "Call");//groupId, itemId, order, title
//                menu.add(0, v.getId(), 0, "SMS");
//    }
//
//    };


    public TaskAdapter(Context context, List<Task> messageList) {
        mContext = context;
        mTaskList = messageList;
        sharedPreferences = mContext.getSharedPreferences(AuthUser.APP_PREFERENCES,mContext.MODE_PRIVATE);
    }
    public TaskAdapter(Context context, List<Task> messageList,boolean isManager) {
        mContext = context;
        mTaskList = messageList;
        this.isManager=isManager;
    }

    @Override
    public int getItemCount() {
        return mTaskList.size();
    }

    // Determines the appropriate ViewType according to the sender of the message.
    @Override
    public int getItemViewType(int position) {
        Task task = (Task) mTaskList.get(position);
        return 1;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        View view;
        view = LayoutInflater.from(viewGroup.getContext())
                .inflate(R.layout.item_task, viewGroup, false);

        view.setOnClickListener(mOnClickListener);
        if (MainActivity.isManager(mContext)){
            view.setOnLongClickListener(mOnLongClickListener);
        }
        return new TaskHolder(view);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder viewHolder, int position) {
        Task task = (Task) mTaskList.get(position);
        ((TaskHolder) viewHolder).bind(task);

        }

    public class TaskHolder extends RecyclerView.ViewHolder implements View.OnCreateContextMenuListener {
        TextView task_description, task_time, task_title;
        View itemView;
        TextView task_progress;


        TaskHolder(View itemView) {
            super(itemView);
            this.itemView = itemView;
            task_title = (TextView) itemView.findViewById(R.id.task_title);
            task_description = (TextView) itemView.findViewById(R.id.task_description);
            task_time = (TextView) itemView.findViewById(R.id.task_time);
            task_progress = (TextView) itemView.findViewById(R.id.task_progress);
            if (!MainActivity.isManager(mContext)){
                itemView.setOnCreateContextMenuListener(this);
            }
        }

        void bind(Task task) {
            Integer progress;
            progress=task.getUser_progress();
            if (MainActivity.isManager(mContext))
                progress=task.getTask_progress();


            task_title.setText(task.getTitle());
            task_title.setTag(task.getId());
            task_description.setText(task.getDescription());
            task_time.setText(task.getdeadlineDate_time());
            task_progress.setText(Integer.toString(progress)+"%");

            if (progress>=75){
                task_progress.setBackgroundResource(R.drawable.rounded_circle_green);
                task_progress.setTextColor(mContext.getResources().getColor(R.color.colorWorkerFree));
            }else if (progress>=25){
                task_progress.setBackgroundResource(R.drawable.rounded_circle_yellow);
                task_progress.setTextColor(mContext.getResources().getColor(R.color.colorTaskMiddle));
            }else if (progress>=0){
                task_progress.setBackgroundResource(R.drawable.rounded_circle_red);
                task_progress.setTextColor(mContext.getResources().getColor(R.color.colorTaskLow));
            }

//            if (task.getStatus()=="1"){
//                task_title.setTextColor(mContext.getResources().getColor(R.color.colorWorkerFree));
//                itemView.setBackgroundColor(mContext.getResources().getColor(R.color.colorTaskBgSuccess));
//            }
        }

        @Override
        public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
                menu.setHeaderTitle("Ваш прогресс");

                menu.add(this.getAdapterPosition(), R.id.menu_progress_100, 0, "100%");
                menu.add(this.getAdapterPosition(), R.id.menu_progress_75, 0, "75%");
                menu.add(this.getAdapterPosition(), R.id.menu_progress_50, 0, "50%");
                menu.add(this.getAdapterPosition(), R.id.menu_progress_25, 0, "25%");

        }
    }

    public void showOnLongClickPopupMenu(View v) {
        String id = (String) v.findViewById(R.id.task_title).getTag().toString();
        PopupMenu popupMenu = new PopupMenu(mContext, v);
        popupMenu.getMenu().add(Integer.parseInt(id), R.id.menu_check, 0, "Выполнено").setIcon(R.drawable.ic_check_green);
        popupMenu.getMenu().add(Integer.parseInt(id), R.id.menu_edit, 0, "Редактировать").setIcon(R.drawable.ic_edit);
        popupMenu.getMenu().add(Integer.parseInt(id), R.id.menu_delete, 0, "Удалить").setIcon(R.drawable.ic_close);
        //popupMenu.getMenu().getItem(0);


        try {
            Field[] fields = popupMenu.getClass().getDeclaredFields();
            for (Field field : fields) {
                if ("mPopup".equals(field.getName())) {
                    field.setAccessible(true);
                    Object menuPopupHelper = field.get(popupMenu);
                    Class<?> classPopupHelper = Class.forName(menuPopupHelper.getClass().getName());
                    Method setForceIcons = classPopupHelper.getMethod("setForceShowIcon", boolean.class);
                    setForceIcons.invoke(menuPopupHelper, true);
                    break;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

//        popupMenu.inflate(R.menu.popupmenu);
//        popupMenu.getMenu().getItem(0).getGroupId();

        popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                String id_task = Integer.toString(item.getGroupId());
                ServerRequest requestToServer;
                switch (item.getItemId()) {

                    case R.id.menu_check:
                        requestToServer = new ServerRequest("/checkTask"){
                            @Override
                            protected void onPostExecute(String result) {
                                super.onPostExecute(result);
                                JSONObject res  = this.convertToJSON(result);
                                try {
                                    if (res.getString("status").equals("OK")) {
                                        Toast.makeText(mContext, "Прогресс по задаче обновлён!", Toast.LENGTH_SHORT).show();
                                    } else if (res.getString("status").equals("OLD_TOKEN")) {
                                        Toast.makeText(mContext, "Токен устарел", Toast.LENGTH_SHORT).show();
                                        Intent i = new Intent(mContext, LoginActivity.class);
                                        //i.putExtra("action","clear_token");
                                        sharedPreferences = mContext.getSharedPreferences(AuthUser.APP_PREFERENCES, mContext.MODE_PRIVATE);
                                        SharedPreferences.Editor editor = sharedPreferences.edit();
                                        editor.clear().commit();
                                        mContext.startActivity(i);
                                    } else {
                                        Toast.makeText(mContext, "Пришло непонятно что", Toast.LENGTH_SHORT).show();
                                    }
                                } catch (JSONException e) { e.printStackTrace(); }


                            }
                        };
                        requestToServer.putTextData(AuthUser.ACCESS_TOKEN_TITLE, sharedPreferences.getString(AuthUser.ACCESS_TOKEN_TITLE,"0"));
                        requestToServer.putTextData("id_task", id_task);
                        requestToServer.putTextData("progress", "100");
                        requestToServer.execute();
                        return true;
                    case R.id.menu_edit:
                        Intent intent = new Intent(mContext, CreateTaskActivity.class);
                        intent.putExtra("type_activity", "editTask");
                        intent.putExtra("id_task", id_task);
                        mContext.startActivity(intent);
                        return true;
                    case R.id.menu_delete:
                        requestToServer = new ServerRequest("/deleteTask"){
                            @Override
                            protected void onPostExecute(String result) {
                                super.onPostExecute(result);
                                JSONObject res  = this.convertToJSON(result);
                                try {
                                    if (res.getString("status").equals("OK")) {
                                        Toast.makeText(mContext, "Задача удалена", Toast.LENGTH_SHORT).show();
                                    } else if (res.getString("status").equals("OLD_TOKEN")) {
                                        Toast.makeText(mContext, "Токен устарел", Toast.LENGTH_SHORT).show();
                                        Intent i = new Intent(mContext, LoginActivity.class);
                                        //i.putExtra("action","clear_token");
                                        sharedPreferences = mContext.getSharedPreferences(AuthUser.APP_PREFERENCES, mContext.MODE_PRIVATE);
                                        SharedPreferences.Editor editor = sharedPreferences.edit();
                                        editor.clear().commit();
                                        mContext.startActivity(i);
                                    } else {
                                        Toast.makeText(mContext, "Пришло непонятно что", Toast.LENGTH_SHORT).show();
                                    }
                                } catch (JSONException e) { e.printStackTrace(); }


                            }
                        };
                        requestToServer.putTextData(AuthUser.ACCESS_TOKEN_TITLE, sharedPreferences.getString(AuthUser.ACCESS_TOKEN_TITLE,"0"));
                        requestToServer.putTextData("id_task", id_task);
                        requestToServer.execute();
                        return true;
                    default:
                        return false;
                }
            }
        });

        popupMenu.show();
    }

}





