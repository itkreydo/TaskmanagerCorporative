package com.example.taskmanager;

import android.graphics.Bitmap;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import com.example.taskmanager.adapters.MessageListAdapter;
import com.example.taskmanager.models.Message;
import com.example.taskmanager.models.User;

import org.json.JSONException;
import org.json.JSONObject;

import java.net.URISyntaxException;
import java.text.ParseException;
import java.util.ArrayList;

import io.socket.client.IO;
import io.socket.client.Socket;
import io.socket.emitter.Emitter;

public class FragmentDialog extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;
    // имена атрибутов для Map

    ArrayList<Message> messagesData;
    private RecyclerView mMessageRecycler;
    private MessageListAdapter mMessageAdapter;

    Message m;
    private OnFragmentInteractionListener mListener;

    private Socket socket;
    private String Nickname ;
    private EditText messagetxt ;
    private AuthUser authUser;
    private String id_task;
    public FragmentDialog() {
        // Required empty public constructor
    }


    // TODO: Rename and change types and number of parameters
    public static FragmentDialog newInstance(String param1, String param2) {
        FragmentDialog fragment = new FragmentDialog();
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
            id_task = getArguments().getString("id_task");
        }

        Nickname = authUser.getUser().getNickname();
        try {
            socket = IO.socket("http://192.168.0.158:3000?token="+authUser.getAcc_token());
            socket.connect();
            socket.on("error", new Emitter.Listener() {
                @Override public void call(final Object... args) {
                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(getContext(), args[0].toString(), Toast.LENGTH_SHORT).show();
                        }
                    });
                }
            });
            socket.emit("join", Nickname, id_task);

        }
        catch (URISyntaxException e) {
            e.printStackTrace();
        }

        messagesData = new ArrayList<Message>();
        mMessageAdapter = new MessageListAdapter(getContext(), messagesData);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.chat_recycler_view, container, false);
        mMessageRecycler = v.findViewById(R.id.reyclerview_message_list);
        final LinearLayoutManager linlayoutManager = new LinearLayoutManager(getContext());
        linlayoutManager.setStackFromEnd(true);
        mMessageRecycler.setLayoutManager(linlayoutManager);
        mMessageRecycler.setAdapter(mMessageAdapter);
        ImageButton sendMessage = v.findViewById(R.id.button_chatbox_send);
        messagetxt = v.findViewById(R.id.edittext_chatbox);
        sendMessage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                JSONObject mJson = new JSONObject();
                m = new Message(messagetxt.getText().toString(), authUser.getUser(), Message.TYPE_MESSAGE_SENT);
                try {
                    mJson = m.convertToJson();
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                socket.emit("messagedetection", mJson.toString(),id_task);


                messagesData.add(m);
                mMessageAdapter.notifyDataSetChanged();
                mMessageRecycler.smoothScrollToPosition(mMessageAdapter.getItemCount() - 1);
                messagetxt.setText("");
            }
        });

        socket.on("message", new Emitter.Listener() {
            @Override public void call(final Object... args) {
                getActivity().runOnUiThread(new Runnable() {
                    @Override public void run() {
                        Log.d("11",args[0].toString());
                        JSONObject data = null;
                        try {
                            data = new JSONObject(args[0].toString());
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        Log.d("111",data.toString());
                        try {
                            User u = new User();
                            u.setNickname(data.getJSONObject("user").getString("nickname"));
                            u.setId(data.getJSONObject("user").getInt("id"));
                            int type = Message.TYPE_MESSAGE_RECEIVED;
                            m = new Message(data.getJSONObject("message").getString("text"),data.getJSONObject("message").getString("date"), u);
                            getBitmapFromUrl getBitmapFromUrl = new getBitmapFromUrl(getContext(), ServerRequest.SERVER_URL_DEFAULT + "/getFiles/avatar?token=" +authUser.getAcc_token()+ "&id_user="+m.getSender().getId(),m.getSender()) {
                                @Override
                                protected void onPostExecute(Bitmap bitmap) {
                                    super.onPostExecute(bitmap);
                                    user.setAvatar(bitmap);
                                    mMessageAdapter.notifyDataSetChanged();
                                }
                            };
                            getBitmapFromUrl.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
                            messagesData.add(m);
                            mMessageAdapter.notifyDataSetChanged();

                            if (linlayoutManager.findLastVisibleItemPosition()>=mMessageAdapter.getItemCount() - 3) {
                                mMessageRecycler.smoothScrollToPosition(mMessageAdapter.getItemCount() - 1);
                            }
                        }
                        catch (JSONException e) {
                            e.printStackTrace();
                        } catch (ParseException e) {
                            e.printStackTrace();
                        }
                    }
                });
            }

        });
        socket.on("updateDialog", new Emitter.Listener() {
            @Override public void call(final Object... args) {
                getActivity().runOnUiThread(new Runnable() {
                    @Override public void run() {
                        String datastr = args[0].toString();
                        try {
                            JSONObject data = (JSONObject) new JSONObject(datastr);
                           messagesData.addAll(Message.convertJson(data.getJSONArray("messages")));
                           mMessageAdapter.notifyDataSetChanged();
                           for (int i =0;i<messagesData.size();i++){
                               getBitmapFromUrl getBitmapFromUrl = new getBitmapFromUrl(getContext(), ServerRequest.SERVER_URL_DEFAULT + "/getFiles/avatar?token=" +authUser.getAcc_token()+ "&id_user="+messagesData.get(i).getSender().getId(),messagesData.get(i).getSender()) {
                                   @Override
                                   protected void onPostExecute(Bitmap bitmap) {
                                       super.onPostExecute(bitmap);
                                       user.setAvatar(bitmap);
                                       mMessageAdapter.notifyDataSetChanged();
                                   }
                               };
                               getBitmapFromUrl.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
                           }
                        }
                        catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                });
            }
        });

//        socket.on("userjoinedthechat", new Emitter.Listener() {
//            @Override public void call(final Object... args) {
//                getActivity().runOnUiThread(new Runnable() {
//                    @Override public void run() {
//                        String data = (String) args[0];
//                    }
//                });
//            }
//        });
        return v;

    }

    //socket place



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

    @Override
    public void onDestroy() {
        super.onDestroy();
        socket.disconnect();

    }

    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
    }
}
