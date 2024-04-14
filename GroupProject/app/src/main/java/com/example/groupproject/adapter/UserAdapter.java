package com.example.groupproject.adapter;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.groupproject.R;
import com.example.groupproject.model.User;

import java.util.List;

public class UserAdapter extends RecyclerView.Adapter<UserAdapter.UserViewHolder> {
    private final List<User> users;
    private OnItemClickListener listener;

    public UserAdapter(List<User> users) {
        this.users = users;
    }

    @NonNull
    @Override
    public UserViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.user_item, parent, false);
        return new UserViewHolder(view, listener, users);
    }

    @Override
    public void onBindViewHolder(@NonNull UserViewHolder holder, int position) {
        User user = users.get(position);
        Log.e("UserAdapter", "onBindViewHolder: " + position);
        holder.username.setText(user.getUsername());
        holder.email.setText(user.getEmail());
    }

    @Override
    public int getItemCount() {
        return users.size();
    }

    public void updateData(List<User> newUserList) {
        users.clear();
        if (newUserList != null) {
            users.addAll(newUserList);
        }
        notifyDataSetChanged();
    }

    public static class UserViewHolder extends RecyclerView.ViewHolder {
        TextView username;
        TextView email;
        OnItemClickListener listener;
        List<User> users;

        public UserViewHolder(@NonNull View itemView, OnItemClickListener listener, List<User> users) {
            super(itemView);
            this.listener = listener;
            this.users = users;
            username = itemView.findViewById(R.id.usernameItem);
            email = itemView.findViewById(R.id.emailItem);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int position = getAdapterPosition();
                    Log.d("UserAdapter", "onClick: " + (listener == null));
                    if (listener != null && position != RecyclerView.NO_POSITION) {
                        listener.onItemClick(users.get(position));
                    }
                }
            });
        }
    }

    public interface OnItemClickListener {
        void onItemClick(User user);
    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        this.listener = listener;
    }
}