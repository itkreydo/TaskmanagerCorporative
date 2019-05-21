package com.example.taskmanager.adapters;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.taskmanager.AuthUser;
import com.example.taskmanager.R;
import com.example.taskmanager.TaskDetailActivity;
import com.example.taskmanager.models.User;

import java.util.ArrayList;
import java.util.List;

public class WorkerAdapter extends RecyclerView.Adapter {

    private Context mContext;
    private List<User> mUserList;
    private boolean choosable = false;
    private ArrayList<User> checkedUsersArray;
    private ArrayList<Integer> checkedUsersIdArray;
    private ArrayList<String> checkedUsersNameArray;
    private AuthUser authUser;
    private final View.OnClickListener mOnClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            String id = (String) v.findViewById(R.id.tvWorkerName).getTag().toString();

            Toast.makeText(mContext, id, Toast.LENGTH_SHORT).show();
            Intent intent = new Intent(mContext, TaskDetailActivity.class);
            intent.putExtra("id_task", id);
            mContext.startActivity(intent);
        }
    };

    public WorkerAdapter(Context context, List<User> messageList) {
        mContext = context;
        mUserList = messageList;
        authUser = new AuthUser(context);
        authUser.init();
    }
    public WorkerAdapter(Context context, List<User> messageList,boolean choosable) {
        mContext = context;
        mUserList = messageList;
        this.choosable = choosable;
        checkedUsersArray = new ArrayList<User>();
        checkedUsersIdArray = new ArrayList<Integer>();
        checkedUsersNameArray = new ArrayList<String>();
        authUser = new AuthUser(context);
        authUser.init();
    }

    @Override
    public int getItemCount() {
        return mUserList.size();
    }

    // Determines the appropriate ViewType according to the sender of the message.
    @Override
    public int getItemViewType(int position) {
        User user = (User) mUserList.get(position);
        return 1;
    }



    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        View view;
        if (choosable==false) {
            view = LayoutInflater.from(viewGroup.getContext())
                    .inflate(R.layout.item_worker, viewGroup, false);
            view.setOnClickListener(mOnClickListener);
            return new WorkerAdapter.WorkerHolder(view);
        }else{

            view = LayoutInflater.from(viewGroup.getContext())
                    .inflate(R.layout.item_worker_choosable, viewGroup, false);
            //view.setOnClickListener(mOnClickListener);
            return new WorkerAdapter.WorkerHolderChoosable(view);
        }

    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder viewHolder, final int position) {
        User user = (User) mUserList.get(position);
        if (choosable==false) {
            ((WorkerAdapter.WorkerHolder) viewHolder).bind(user);
        }else{

            ((WorkerAdapter.WorkerHolderChoosable) viewHolder).bind(user);
        }
        }

    public ArrayList<Integer> getCheckedUserId() {
        return checkedUsersIdArray;
    }
    public ArrayList<String> getCheckedUserName() {
        return checkedUsersNameArray;
    }
    public ArrayList<User> getCheckedUser() {
        return checkedUsersArray;
    }

    public class WorkerHolder extends RecyclerView.ViewHolder {
        TextView user_name, user_profession,user_status;
        View divideLine;
        ImageView user_image;

        WorkerHolder(View itemView) {
            super(itemView);
            user_name = (TextView) itemView.findViewById(R.id.tvWorkerName);
            user_profession = (TextView) itemView.findViewById(R.id.tvWorkerProfession);
            user_status = (TextView) itemView.findViewById(R.id.tvWorkerStatus);
            user_image = (ImageView) itemView.findViewById(R.id.tvWorkerImage);
            divideLine = (View) itemView.findViewById(R.id.view1);
        }

        void bind(User user) {
            user_name.setText(user.getNickname());
            user_name.setTag(user.getId());
            user_image.setImageBitmap(user.getAvatar());
            // Format the stored timestamp into a readable String using method.
            user_profession.setText(user.getProfession());
            if (user.getNum_tasks()>3){
                user_status.setTextColor(mContext.getResources().getColor(R.color.colorWorkerBusy));
                user_status.setText("Занят "+Integer.toString(user.getNum_tasks()));
            }else{
                user_status.setTextColor(mContext.getResources().getColor(R.color.colorWorkerFree));
                user_status.setText("Свободен "+Integer.toString(user.getNum_tasks()));
            }

            // Insert the profile image from the URL into the ImageView.
            //Utils.displayRoundImageFromUrl(getContext(), message.getSender().getProfileUrl(), profileImage);
        }
    }

    public class WorkerHolderChoosable extends RecyclerView.ViewHolder {
        TextView user_name, user_profession,user_status;
        View divideLine;
        ImageView user_image;
        CheckBox user_checkbox;

        WorkerHolderChoosable(View itemView) {
            super(itemView);
            user_name = (TextView) itemView.findViewById(R.id.tvWorkerName);
            user_profession = (TextView) itemView.findViewById(R.id.tvWorkerProfession);
            user_status = (TextView) itemView.findViewById(R.id.tvWorkerStatus);
            user_image = (ImageView) itemView.findViewById(R.id.tvWorkerImage);
            divideLine = (View) itemView.findViewById(R.id.view1);
            user_checkbox = (CheckBox) itemView.findViewById(R.id.checkBox);
            user_checkbox.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    User user = mUserList.get(getAdapterPosition());
                    if (!user.isChecked()){
                        checkedUsersArray.add(user);
                        checkedUsersIdArray.add(user.getId());
                        checkedUsersNameArray.add(user.getNickname());
                    }else{
                        checkedUsersArray.remove(user);
                        checkedUsersNameArray.remove(checkedUsersIdArray.indexOf(user.getId()));
                        checkedUsersIdArray.remove((Object)user.getId());

                    }
                    user.setChecked(!user.isChecked());

                    mUserList.set(getAdapterPosition(),user);
                    Log.d("123", Integer.toString(getAdapterPosition()));
                }
            });
        }

        void bind(User user) {

            user_name.setText(user.getNickname());
            user_name.setTag(user.getId());
            user_image.setImageBitmap(user.getAvatar());
            // Format the stored timestamp into a readable String using method.
            user_profession.setText(user.getProfession());
            if (user.getNum_tasks()>3){
                user_status.setTextColor(mContext.getResources().getColor(R.color.colorWorkerBusy));
                user_status.setText("Занят");
            }else{
                user_status.setTextColor(mContext.getResources().getColor(R.color.colorWorkerFree));
                user_status.setText("Свободен");
            }

        }
    }
}
