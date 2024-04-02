package com.example.groupproject.activity;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.groupproject.R;

import java.util.ArrayList;

public class PostListActivity extends AppCompatActivity {
    RecyclerView recyclerView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post_creation);

        recyclerView = findViewById(R.id.my_recycler_view);

        ArrayList<UsersModel> usersList = new ArrayList<>();
        usersList.add(new UsersModel("老刘"));
        usersList.add(new UsersModel("老周"));
        usersList.add(new UsersModel("老郑"));
        RecyclerViewAdapter recyclerViewAdapter = new RecyclerViewAdapter(PostListActivity.this, usersList);
        recyclerView.setAdapter(recyclerViewAdapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(PostListActivity.this));
    }
}
