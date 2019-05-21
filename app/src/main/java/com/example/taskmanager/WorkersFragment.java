package com.example.taskmanager;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.example.taskmanager.adapters.WorkerAdapter;
import com.example.taskmanager.models.User;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link WorkersFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link WorkersFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class WorkersFragment extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;
    WorkerAdapter workerAdapter;
    ArrayList<User> workerList;
    private RecyclerView mWorkerRecycler;
    private Intent i;
    private static final String APP_PREFERENCES = "com.example.taskmanager.settings";
    private SharedPreferences sharedPreferences;
    SharedPreferences.Editor editor;
    private boolean choosable=false;
    private OnFragmentInteractionListener mListener;
    public AuthUser authUser;
    public ArrayList<User> getChosenUsersFromAdapter(){
        return workerAdapter.getCheckedUser();
    }
    public ArrayList<Integer> getChosenUsersIdFromAdapter(){
        return workerAdapter.getCheckedUserId();
    }
    public ArrayList<String> getChosenUsersNameFromAdapter(){
        return workerAdapter.getCheckedUserName();
    }
    public WorkersFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment WorkersFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static WorkersFragment newInstance(String param1, String param2) {
        WorkersFragment fragment = new WorkersFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        authUser = new AuthUser(getContext());
        authUser.init();
        if (getArguments() != null) {
            if (getArguments().containsKey("choosable")){
                choosable=getArguments().getBoolean("choosable");
            }
        }

        workerList = new ArrayList<User>();

        workerAdapter = new WorkerAdapter(getContext(), workerList,choosable);


        ServerRequest requestToServer = new ServerRequest("/getWorkers"){
            @Override
            protected void onPostExecute(String result) {
                super.onPostExecute(result);
                JSONObject res  = this.convertToJSON(result);
                try {
                    if (res.getString("status").equals("OK")){
                        ArrayList<User> u = User.convertJson(res.getJSONArray("users"));
                        for (int i = 0;i <u.size();i++) {
//                            if (u.get(i).getAvatarUrl().equals("0"))
//                                continue;
                            getBitmapFromUrl getBitmapFromUrl = new getBitmapFromUrl(getContext(), ServerRequest.SERVER_URL_DEFAULT + "/getFiles/avatar?token=" + authUser.getAcc_token() + "&id_user=" + u.get(i).getId(), u.get(i)) {
                                @Override
                                protected void onPostExecute(Bitmap bitmap) {
                                    super.onPostExecute(bitmap);
                                    user.setAvatar(bitmap);
                                    workerAdapter.notifyDataSetChanged();
                                }
                            };
                            getBitmapFromUrl.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
                        }
                        workerList.addAll(u);
                        workerAdapter.notifyDataSetChanged();
                    }else if(res.getString("status").equals("OLD_TOKEN")){
                        Toast.makeText(getContext(), "Токен устарел", Toast.LENGTH_SHORT).show();
                        authUser.sessionClose();
                    }
                    else{
                        Toast.makeText(getContext(), "Пришло непонятно что", Toast.LENGTH_SHORT).show();
                    }
                } catch (JSONException e) { e.printStackTrace(); }


            }
        };
        requestToServer.putTextData(AuthUser.ACCESS_TOKEN_TITLE, authUser.getAcc_token());
        requestToServer.execute();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_workers, container, false);
        mWorkerRecycler = v.findViewById(R.id.reyclerview_workers_list);
        final LinearLayoutManager linlayoutManager = new LinearLayoutManager(getContext());

        mWorkerRecycler.setLayoutManager(linlayoutManager);
        mWorkerRecycler.setAdapter(workerAdapter);
        FloatingActionButton fab = (FloatingActionButton) v.findViewById(R.id.fab);
        if (!MainActivity.isManager(getContext()))
            fab.hide();
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getContext(),CreateWorkerAccountActivity.class);
                startActivity(intent);
            }
        });
        return v;
    }

    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }

//    @Override
//    public void onAttach(Context context) {
//        super.onAttach(context);
//        if (context instanceof OnFragmentInteractionListener) {
//            mListener = (OnFragmentInteractionListener) context;
//        } else {
//            throw new RuntimeException(context.toString()
//                    + " must implement OnFragmentInteractionListener");
//        }
//    }

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
}
