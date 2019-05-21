package com.example.taskmanager;

import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.example.taskmanager.adapters.TaskAdapter;
import com.example.taskmanager.models.Task;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import static java.lang.Thread.sleep;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link MainTaskFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link MainTaskFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class MainTaskFragment extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    private static final String APP_PREFERENCES = "com.example.taskmanager.settings";
    TaskAdapter taskAdapter;
    ArrayList<Task> taskList;
    private RecyclerView mTaskRecycler;
    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;
    private String acc_token;
    private OnFragmentInteractionListener mListener;
    private SharedPreferences sharedPreferences;
    SharedPreferences.Editor editor;

    SwipeRefreshLayout swipeLayout;
    public MainTaskFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment MainTaskFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static MainTaskFragment newInstance(String param1, String param2) {
        MainTaskFragment fragment = new MainTaskFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState)  {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            acc_token = getArguments().getString(AuthUser.ACCESS_TOKEN_TITLE);
        }

        taskList = new ArrayList<Task>();
//        Task t;
//        t = new Task("Подгрузка задач....","...",new Date(),1);
//        taskList.add(t);
        taskAdapter = new TaskAdapter(getContext(), taskList);
        //ask tasks from server
        ServerRequest requestToServer = new ServerRequest("/getMyTasks"){
            @Override
            protected void onPostExecute(String result) {
                super.onPostExecute(result);
                JSONObject res  = this.convertToJSON(result);
                try {
                    if (res.getString("status").equals("OK")){
                        taskList.addAll(Task.convertJson(res.getJSONArray("tasks")));
                        taskAdapter.notifyDataSetChanged();
                    }else if(res.getString("status").equals("OLD_TOKEN")){
                        Toast.makeText(getContext(), "Токен устарел", Toast.LENGTH_SHORT).show();
                        Intent i = new Intent(getContext(),LoginActivity.class);
                        //i.putExtra("action","clear_token");
                        sharedPreferences = getActivity().getSharedPreferences(APP_PREFERENCES,getActivity().MODE_PRIVATE);
                        editor = sharedPreferences.edit();
                        editor.clear().commit();
                        startActivity(i);
                    }
                    else{
                        Toast.makeText(getContext(), "Пришло непонятно что", Toast.LENGTH_SHORT).show();
                    }
                } catch (JSONException e) { e.printStackTrace(); }


            }
        };
        requestToServer.putTextData(AuthUser.ACCESS_TOKEN_TITLE, acc_token);
        requestToServer.execute();



    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_main_task, container, false);
        mTaskRecycler = v.findViewById(R.id.reyclerview_message_list);
        final LinearLayoutManager linlayoutManager = new LinearLayoutManager(getContext());

        mTaskRecycler.setLayoutManager(linlayoutManager);
        mTaskRecycler.setAdapter(taskAdapter);


        swipeLayout = (SwipeRefreshLayout) v.findViewById(R.id.swipe_container);
        swipeLayout.setOnRefreshListener(onRefreshListener);
        swipeLayout.setColorScheme(android.R.color.holo_blue_bright,
                android.R.color.holo_green_light,
                android.R.color.holo_orange_light,
                android.R.color.holo_red_light);
        FloatingActionButton fab = (FloatingActionButton) v.findViewById(R.id.fab);
        if (!MainActivity.isManager(getContext())){
            fab.hide();
        }
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getContext(),CreateTaskActivity.class);
                startActivity(intent);

            }
        });
        return v;
    }
SwipeRefreshLayout.OnRefreshListener onRefreshListener = new SwipeRefreshLayout.OnRefreshListener() {
    @Override
    public void onRefresh() {
        ServerRequest requestToServer = new ServerRequest("/getMyTasks"){
            @Override
            protected void onPostExecute(String result) {
                super.onPostExecute(result);
                JSONObject res  = this.convertToJSON(result);
                try {
                    if (res.getString("status").equals("OK")){
                        swipeLayout.setRefreshing(false);
                        taskList.clear();
                        taskList.addAll(Task.convertJson(res.getJSONArray("tasks")));
                        taskAdapter.notifyDataSetChanged();
                    }else if(res.getString("status").equals("OLD_TOKEN")){
                        Toast.makeText(getContext(), "Токен устарел", Toast.LENGTH_SHORT).show();
                        Intent i = new Intent(getContext(),LoginActivity.class);
                        //i.putExtra("action","clear_token");
                        sharedPreferences = getActivity().getSharedPreferences(APP_PREFERENCES,getActivity().MODE_PRIVATE);
                        editor = sharedPreferences.edit();
                        editor.clear().commit();
                        startActivity(i);
                    }
                    else{
                        Toast.makeText(getContext(), "Пришло непонятно что", Toast.LENGTH_SHORT).show();
                    }
                } catch (JSONException e) { e.printStackTrace(); }


            }
        };
        requestToServer.putTextData(AuthUser.ACCESS_TOKEN_TITLE, acc_token);
        requestToServer.execute();
//        new Handler().postDelayed(new Runnable() {
//            @Override public void run() {
//                swipeLayout.setRefreshing(false);
//            }
//        }, 5000);
    }
};
    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }



    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {

        int percent = 0;
        String id_task = Integer.toString(taskList.get(item.getGroupId()).getId());
        switch (item.getItemId()){
            case R.id.menu_progress_100:
                percent=100;
                Toast.makeText(getContext(), taskList.get(item.getGroupId()).getTitle()+"position:" +item.getGroupId(), Toast.LENGTH_SHORT).show();
                break;
            case R.id.menu_progress_75:
                percent=75;
                Toast.makeText(getContext(), taskList.get(item.getGroupId()).getTitle()+"position:" +item.getGroupId(), Toast.LENGTH_SHORT).show();
                break;
            case R.id.menu_progress_50:
                percent=50;
                Toast.makeText(getContext(), taskList.get(item.getGroupId()).getTitle()+"position:" +item.getGroupId(), Toast.LENGTH_SHORT).show();
                break;
            case R.id.menu_progress_25:
                percent=25;
                Toast.makeText(getContext(), taskList.get(item.getGroupId()).getTitle()+"position:" +item.getGroupId(), Toast.LENGTH_SHORT).show();
                break;
                default:
                    break;
        }
        if (percent!=0) {
            ServerRequest requestToServer = new ServerRequest("/checkTask") {
                @Override
                protected void onPostExecute(String result) {
                    super.onPostExecute(result);
                    JSONObject res = this.convertToJSON(result);
                    try {
                        if (res.getString("status").equals("OK")) {
                            Toast.makeText(getContext(), "Ваш прогресс обновлён!", Toast.LENGTH_SHORT).show();
                        } else if (res.getString("status").equals("OLD_TOKEN")) {
                            Toast.makeText(getContext(), "Токен устарел", Toast.LENGTH_SHORT).show();
                            Intent i = new Intent(getContext(), LoginActivity.class);
                            //i.putExtra("action","clear_token");
                            sharedPreferences = getActivity().getSharedPreferences(APP_PREFERENCES, getActivity().MODE_PRIVATE);
                            editor = sharedPreferences.edit();
                            editor.clear().commit();
                            startActivity(i);
                        } else {
                            Toast.makeText(getContext(), "Пришло непонятно что", Toast.LENGTH_SHORT).show();
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }


                }
            };
            requestToServer.putTextData(AuthUser.ACCESS_TOKEN_TITLE, acc_token);
            requestToServer.putTextData("id_task", id_task);
            requestToServer.putTextData("progress", Integer.toString(percent));
            requestToServer.execute();
        }
        return super.onContextItemSelected(item);
    }
}
