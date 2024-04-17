package com.example.groupproject.activity;

import static android.icu.lang.UProperty.INT_START;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.provider.Settings;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.style.ForegroundColorSpan;
import android.text.style.LeadingMarginSpan;
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
    int current_post_count;

    DatabaseCallback getDbcallbackuser =  new DatabaseCallback(this) {
        @Override
        public void run(List<Object> dataList) {
            User current_user = (User) dataList.get(0);
            current_username = current_user.getUsername();

        }

        @Override
        public void successlistener(Boolean success) {}
    };
    DatabaseCallback dbcallback = new DatabaseCallback(this) {
        @Override
        public void run(List<Object> dataList) {
            dbcontroller.getCurrentUser(getDbcallbackuser, Settings.Secure.getString(getContentResolver(), Settings.Secure.ANDROID_ID));


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
            System.out.println("~~~~~~~~~~ current ranking **************");
            System.out.println(current_ranking);

            LeaderboardAdapter adapter = new LeaderboardAdapter(getContext(), display_list);
            recyclerView = findViewById(R.id.leaderboardrecyclerView);
            recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
            recyclerView.setAdapter(adapter);

            titlerank = findViewById(R.id.currentranking);
            String str_currentranking = String.valueOf(current_ranking + 1);

            if (current_ranking != -1) {
                current_post_count = (int) display_list.get(current_ranking).get(1);
            }

            if (current_ranking != -1 && current_post_count == 0) {
                String text = "You have not posted anything.\nClick here to share moments of your life now!";
                SpannableStringBuilder header = new SpannableStringBuilder(text);
                int start = 0;
                int end = text.length();
                int start2 = text.indexOf("Click");
                header.setSpan(new android.text.style.StyleSpan(Typeface.BOLD), start2, end, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                header.setSpan(new ForegroundColorSpan(Color.parseColor("#E8B4D0")), start2, end, 0);
                header.setSpan(new android.text.style.RelativeSizeSpan(1.2f), start2, end, 0);
                titlerank.setPadding(20, 20, 20, 0);
                titlerank.setText(header);
            }
            else if (current_ranking != -1) {
//                header = "Congrats! You current rank is <" + str_currentranking + ">";
                String text = "Congrats! You current rank is   " + str_currentranking;
                SpannableStringBuilder header = new SpannableStringBuilder(text);
                int startrank = text.indexOf("is") + 5;
                int endrank = startrank + str_currentranking.length();
                int starttext = 0;
                int endtext = starttext + "Congrats! You current rank is   ".length();
                // format rank
                header.setSpan(new android.text.style.StyleSpan(android.graphics.Typeface.BOLD), startrank, endrank, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                header.setSpan(new android.text.style.RelativeSizeSpan(2.8f), startrank, endrank, 0);
                header.setSpan(new ForegroundColorSpan(Color.parseColor("#E8B4D0")), startrank, endrank,0);
                // format text
                header.setSpan(new android.text.style.StyleSpan(Typeface.ITALIC), starttext, endtext, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                titlerank.setPadding(80,50, 20, 0);
                titlerank.setText(header);
                titlerank.setText(header);
            }
        }

        @Override
        public void successlistener(Boolean success) {

        }
    };
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_leaderboard);
        dbcontroller.getUser(dbcallback, true, null);

        backbtn = findViewById(R.id.leaderboardbutton);
        backbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
//                startActivity(new Intent(LeaderboardActivity.this, PostCreateActivity.class));
            }
        });

        if (current_post_count == 0) {
            TextView postbtn = findViewById(R.id.currentranking);
            postbtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    startActivity(new Intent(LeaderboardActivity.this, MapActivity.class));
                }
            });
        }

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
