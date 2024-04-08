package com.example.groupproject.activity;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.groupproject.R;
import com.example.groupproject.model.Post;

import java.util.ArrayList;

public class LeaderboardAdapter extends RecyclerView.Adapter<LeaderboardAdapter.ViewHolder> {

    ArrayList<ArrayList<Object>> dataList;
    Context context;

    public LeaderboardAdapter(Context context, ArrayList<ArrayList<Object>> dataList) {
        this.dataList = dataList;
        this.context = context;
        System.out.println("*************************");
        System.out.println(this.dataList.get(0).get(0));
    }

    @NonNull
    @Override
    public LeaderboardAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(this.context).inflate(R.layout.activity_leaderboard_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull LeaderboardAdapter.ViewHolder holder, int position) {
        System.out.println("Successfully get the items in adapter onBind!!!!!!");
        this.dataList.forEach(System.out::println);
        holder.username.setText(this.dataList.get(0).get(0).toString());
        holder.postcount.setText(this.dataList.get(0).get(1).toString());
    }

    @Override
    public int getItemCount() {
        System.out.println(("^^^^^^^^^^^^^^^^^^^^^^^"));
        System.out.println(this.dataList);
        return this.dataList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView username;
        TextView postcount;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            username = itemView.findViewById(R.id.username);
            postcount = itemView.findViewById(R.id.postcount);
        }
    }
}
