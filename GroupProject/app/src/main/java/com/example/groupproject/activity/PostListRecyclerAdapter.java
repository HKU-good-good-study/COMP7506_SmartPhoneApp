package com.example.groupproject.activity;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.groupproject.R;
import com.example.groupproject.model.Post;

import java.util.ArrayList;

public class PostListRecyclerAdapter extends RecyclerView.Adapter<PostListRecyclerAdapter.MyViewHolder> {

    Context context;
    ArrayList<Post> postList;
    private ImageView photoImage;

    private String selectedBitmap;


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
//        Log.e("RecyclerViewAdapter: ", "Postid is " + postList.get(position).getId());
        holder.textView_postTitle.setText(postList.get(position).getTitle());

        //get and set the photo
        selectedBitmap = postList.get(position).getPhoto().get(0);
        holder.imageView.setImageBitmap(StringToBitMap(selectedBitmap));
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int position = holder.getAdapterPosition(); // get current location
                Intent ViewMyPost = new Intent(v.getContext(), ViewMyPostActivity.class);
                // 将整个 Post 对象作为一个 "extra" 放入 Intent 中
                ViewMyPost.putExtra("POST", postList.get(position));
                v.getContext().startActivity(ViewMyPost);
                Log.e("RecyclerViewAdapter: ", "Clicked Post ID: " + postList.get(position).getId());
            }
        });
    }

    @Override
    public int getItemCount() {
        return this.postList.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder{

        TextView textView_postTitle;
        ImageView imageView ;


        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            textView_postTitle = itemView.findViewById(R.id.postTitle);
            imageView = itemView.findViewById(R.id.bitmap_smallView);


        }
    }

    public Bitmap StringToBitMap(String encodedString){
        try {
            byte [] encodeByte= Base64.decode(encodedString,Base64.DEFAULT);
            Bitmap bitmap= BitmapFactory.decodeByteArray(encodeByte, 0, encodeByte.length);
            return bitmap;
        } catch(Exception e) {
            e.getMessage();
            return null;
        }
    }
}
