package com.example.groupproject.activity;

import android.os.Bundle;
import android.util.Log;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.groupproject.R;
import com.example.groupproject.controller.DatabaseCallback;
import com.example.groupproject.controller.DatabaseController;
import com.example.groupproject.model.Post;
import com.example.groupproject.model.User;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class LeaderboardActivity extends AppCompatActivity {
    RecyclerView recyclerView;
    DatabaseController dbcontroller = DatabaseController.getInstance();
    DatabaseCallback dbcallback = new DatabaseCallback(this) {
        @Override
        public void run(List<Object> dataList) {
            ArrayList<ArrayList<Object>> display_list = new ArrayList<>();
            for (Object item : dataList) {
                User user_info = (User) item;
                String username = user_info.getUsername();
                ArrayList<String> postlist = user_info.getPostList();
                int count = postlist.size();

                ArrayList<Object> display_item = new ArrayList<>();
                display_item.add(username);
                display_item.add(count);


                display_list.add(display_item);
            }
            
            display_list.sort(Comparator.comparing(list -> (Integer) list.get(1)));

            LeaderboardAdapter adapter = new LeaderboardAdapter(getContext(), display_list);
            recyclerView = findViewById(R.id.leaderboardrecyclerView);

            recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
            recyclerView.setAdapter(adapter);
        }
        @Override
        public void successlistener(Boolean success) {

        }
    };
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_leaderboard);

        dbcontroller.getUser(dbcallback, true, null);

    }
}
