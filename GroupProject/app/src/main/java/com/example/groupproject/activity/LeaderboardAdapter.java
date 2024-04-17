package com.example.groupproject.activity;

import android.content.Context;
import android.graphics.Color;
import android.graphics.Typeface;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.style.ForegroundColorSpan;
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
//        this.dataList.forEach(System.out::println);

        String username_str = this.dataList.get(position).get(0).toString();
        SpannableStringBuilder username = new SpannableStringBuilder(username_str);
        int uend = username_str.length();
//        username.setSpan(new android.text.style.StyleSpan(Typeface.ITALIC), 0, uend, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        username.setSpan(new android.text.style.RelativeSizeSpan(1.5f), 0, uend, 0);
//        username.setSpan(new ForegroundColorSpan(Color.parseColor("#E8B4D0")), 0, uend,0);
        holder.username.setText(username);

        String postcount_str = this.dataList.get(position).get(1).toString();
        SpannableStringBuilder postcount = new SpannableStringBuilder(postcount_str);
        int pend = postcount_str.length();
        postcount.setSpan(new android.text.style.StyleSpan(Typeface.BOLD), 0, pend, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        postcount.setSpan(new android.text.style.RelativeSizeSpan(1.8f), 0, pend, 0);
        holder.postcount.setText(postcount);
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
