package com.example.taskmanager.adapters;

import java.util.ArrayList;
        import android.content.Context;
        import android.view.LayoutInflater;
        import android.view.View;
        import android.view.ViewGroup;
        import android.widget.BaseAdapter;
        import android.widget.CheckBox;
        import android.widget.CompoundButton;
        import android.widget.CompoundButton.OnCheckedChangeListener;
        import android.widget.ImageView;
        import android.widget.TextView;

import com.example.taskmanager.R;
import com.example.taskmanager.models.User;

public class WorkerMiniAdapter extends BaseAdapter {
    Context ctx;
    LayoutInflater lInflater;
    ArrayList<User> users;

    public WorkerMiniAdapter(Context context, ArrayList<User> users) {
        ctx = context;
        this.users = users;
        lInflater = (LayoutInflater) ctx
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    // кол-во элементов
    @Override
    public int getCount() {
        return users.size();
    }

    // элемент по позиции
    @Override
    public Object getItem(int position) {
        return users.get(position);
    }

    // id по позиции
    @Override
    public long getItemId(int position) {
        return position;
    }

    // пункт списка
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        // используем созданные, но не используемые view
        View view = convertView;
        if (view == null) {
            view = lInflater.inflate(R.layout.item_worker_create, parent, false);
        }

        User u = getUser(position);

        // заполняем View в пункте списка данными из товаров: наименование, цена
        // и картинка
        String userProgress = (u.getProgress() == 777) ? "Руководитель" : Integer.toString(u.getProgress())+"%";
        ((TextView) view.findViewById(R.id.tvWorkerName)).setText(u.getNickname());
        ((TextView) view.findViewById(R.id.tvWorkersId)).setText(userProgress);
        ((ImageView) view.findViewById(R.id.tvWorkerImage)).setImageBitmap(u.getAvatar());

        // пишем позицию
        //cbBuy.setTag(position);
        // заполняем данными из товаров: в корзине или нет
        return view;
    }

    // user по позиции
    User getUser(int position) {
        return ((User) getItem(position));
    }

}