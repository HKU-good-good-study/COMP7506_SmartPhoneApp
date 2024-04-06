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

public class PostListRecyclerAdapter extends RecyclerView.Adapter<PostListRecyclerAdapter.MyViewHolder> {

    Context context;
    ArrayList<Post> postList;

    public PostListRecyclerAdapter(Context context, ArrayList<Post> postList) {
        this.context = context;
        this.postList = postList;
    }

    @NonNull
    @Override
    public PostListRecyclerAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(this.context).inflate(R.layout.item_recyclerview_post,parent, false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull PostListRecyclerAdapter.MyViewHolder holder, int position) {
        Log.e("RecyclerViewAdapter: ", "Postid is " + postList.get(position).getId());
        holder.textView.setText(postList.get(position).getTitle());
    }

    @Override
    public int getItemCount() {
        return this.postList.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder{

        TextView textView;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            textView = itemView.findViewById(R.id.username);

        }
    }
}
