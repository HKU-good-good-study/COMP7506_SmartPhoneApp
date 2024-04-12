package com.example.groupproject.activity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

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
    Button backbtn;
    TextView titlerank;
    String current_username;
    int current_ranking;
    DatabaseCallback dbcallback = new DatabaseCallback(this) {
        @Override
        public void run(List<Object> dataList) {
            User current_user = (User) dataList.get(0);
            current_username = current_user.getUsername();
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
            
//            display_list.sort(Comparator.comparing(list -> (Integer) list.get(1)));
            display_list.sort(Comparator.comparing(list -> (Integer) list.get(1), Comparator.reverseOrder()));


            current_ranking = findIndex(display_list, current_username);

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

        titlerank = findViewById(R.id.currentranking);
        String str_currentranking = String.valueOf(current_ranking + 1);
        String header;
        if (current_ranking == -1) {
            header = "You have not posted anything. Start sharing moments of your life now!";
        }
        else {
            header = "Congrats! You current rank is <" + str_currentranking + ">";
        }
        titlerank.setText(header);

        backbtn = findViewById(R.id.leaderboardbutton);
        backbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(LeaderboardActivity.this, PostCreateActivity.class));
            }
        });

    }
    public static int findIndex(List<ArrayList<Object>> display_list, String current_username) {
        for (int i = 0; i < display_list.size(); i++) {
            ArrayList<Object> display_item = display_list.get(i);
            String username = (String) display_item.get(0);
            if (username.equals(current_username)) {

                return i;
            }

        }
        return -1; // Element not found
    }
}
