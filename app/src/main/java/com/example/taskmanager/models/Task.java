package com.example.taskmanager.models;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class Task {
    public int id;
    public String title;
    public String username_created;
    public String description;
    public Date dateCreated;
    public Date deadlineDate;
    public int user_progress;
    public int task_progress;

    public String status;
    public ArrayList<User> getWorkers() {
        return workers;
    }
    public String getWorkersString(){
        String res="";
        String divider = ", ";
        for (int i=0;i<workers.size();i++){
            if (i==workers.size()-1)
                divider="";
            res+=workers.get(i).getNickname()+divider;
        }
        return res;
    }
    public void setWorkers(ArrayList<User> workers) {
        this.workers = workers;
    }
    public void addWorker(User u ) {
        this.workers.add(u);
    }
    public String getStatus() {
        return status;
    }
    public void setStatus(String status) {
        this.status = status;
    }

    public ArrayList<User> workers = new ArrayList<User>();
    public Task(){

    }
    public Task(String title,String description, Date dedlineDate,int id) {
        this.id=id;
        this.title=title;
        this.description=description;
        this.deadlineDate=dedlineDate;
    }
    public Task(String title,String description, Date dedlineDate,int id,String username_created,Date dateCreated) {
        this.id=id;
        this.title=title;
        this.description=description;
        this.deadlineDate=dedlineDate;
        this.username_created=username_created;
        this.dateCreated=dateCreated;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Date getDeadlineDate() {
        return deadlineDate;
    }

    public void setDeadlineDate(Date deadlineDate) {
        this.deadlineDate = deadlineDate;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }
    public String getdeadlineDate_time() {
        SimpleDateFormat formatForDate_time = new SimpleDateFormat("dd.MM.yyyy HH:mm");//2:27
        return formatForDate_time.format(deadlineDate);
    }
    public String getUsername_created() {
        return username_created;
    }

    public void setUsername_created(String username_created) {
        this.username_created = username_created;
    }

    public int getUser_progress() {
        return user_progress;
    }
    public void setUser_progress(int user_progress) {
        this.user_progress = user_progress;
    }

    public int getTask_progress() {
        return task_progress;
    }
    public void setTask_progress(int task_progress) {
        this.task_progress = task_progress;
    }

    public int getId() {
        return id;
    }
    public void setId(int id) {
        this.id = id;
    }
    public static Task convertTask(JSONObject jsonTask){
        SimpleDateFormat dateFormat = new SimpleDateFormat(
                "yyyy-MM-dd'T'HH:mm:ss.000'Z'");
        Task t = new Task();
        try {
                t.setId(jsonTask.getInt("id"));
                t.setTitle(jsonTask.getString("title"));
                t.setDescription(jsonTask.getString("description"));
                t.setDateCreated(dateFormat.parse(jsonTask.getString("date_created")));
                t.setDeadlineDate(dateFormat.parse(jsonTask.getString("date_deadline")));
                JSONArray workers = jsonTask.getJSONArray("workers");
                User u;
                for (int i =0;i<workers.length();i++){
                    u = new User();
                    u.setId(workers.getJSONObject(i).getInt("id"));
                    u.setNickname(workers.getJSONObject(i).getString("name"));
                    u.setProgress(workers.getJSONObject(i).getInt("progress"));
                    u.setAvatar(User.avatarDefault);
                    t.addWorker(u);
                }


        }catch (JSONException e) {
            e.printStackTrace();
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return t;
    }
    public static ArrayList<Task> convertJson(JSONArray jsonArrayMessage){
        ArrayList<Task> TaskList = new ArrayList<Task>();
        SimpleDateFormat dateFormat = new SimpleDateFormat(
                "yyyy-MM-dd'T'HH:mm:ss.000'Z'");
        try {
            for (int i=0;i<jsonArrayMessage.length();i++){
                Task t = new Task();
                t.setId(jsonArrayMessage.getJSONObject(i).getInt("id"));
                t.setTitle(jsonArrayMessage.getJSONObject(i).getString("title"));
                t.setDescription(jsonArrayMessage.getJSONObject(i).getString("description"));
                t.setDateCreated(dateFormat.parse(jsonArrayMessage.getJSONObject(i).getString("date_created")));
                t.setDeadlineDate(dateFormat.parse(jsonArrayMessage.getJSONObject(i).getString("date_deadline")));
                t.setUser_progress(jsonArrayMessage.getJSONObject(i).getInt("user_progress"));
                t.setTask_progress(jsonArrayMessage.getJSONObject(i).getInt("task_progress"));
                t.setStatus(jsonArrayMessage.getJSONObject(i).getString("status"));
                TaskList.add(t);
            }
        }catch (JSONException e) {
            e.printStackTrace();
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return TaskList;
    }
    public Date getDateCreated() {
        return dateCreated;
    }

    public void setDateCreated(Date dateCreated) {
        this.dateCreated = dateCreated;
    }

}
