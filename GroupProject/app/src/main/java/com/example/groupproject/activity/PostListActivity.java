package com.example.groupproject.activity;

import android.content.Intent;
import android.os.Bundle;
import android.provider.Settings;
import android.view.View;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.groupproject.R;
import com.example.groupproject.controller.DatabaseCallback;
import com.example.groupproject.controller.DatabaseController;
import com.example.groupproject.model.Post;
import com.example.groupproject.model.User;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;
import java.util.List;

public class PostListActivity extends AppCompatActivity {
    RecyclerView recyclerView;
    DatabaseController db = DatabaseController.getInstance();
    ArrayList<Object> list = new ArrayList<>();
    TextView title;
    DatabaseCallback databaseCallbackPost = new DatabaseCallback(this) {
        @Override
        public void run(List<Object> dataList) { //get all posts
            ArrayList<Post> temp = new ArrayList<>();
            for (Object item : dataList) {
                temp.add((Post) item);
            }
//            Toast.makeText(getContext(),"In PostListActivity!!", Toast.LENGTH_SHORT).show();
            PostListRecyclerAdapter postListRecyclerAdapter = new PostListRecyclerAdapter(getContext(),  temp);
            recyclerView = findViewById(R.id.my_recycler_view);
//            Log.e("PostListActivity: ","Post found: " + temp.toString());
            recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
            recyclerView.setAdapter(postListRecyclerAdapter);
        }
        @Override
        public void successlistener(Boolean success) {}
    };
    DatabaseCallback databaseCallback = new DatabaseCallback(this) {
        @Override
        public void run(List<Object> dataList) {
            User current_user = (User) dataList.get(0);
            title.setText("User Email:"+current_user.getEmail());
            db.getPosts(databaseCallbackPost,current_user.getUsername());
        }
        @Override
        public void successlistener(Boolean success) {}
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post_creation);



        FloatingActionButton backButton = findViewById(R.id.backButton_inPostList);
        title = findViewById(R.id.userMessage_inPostList);

        backButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                finish();
            }
        });

        // get the Email of the user and set it to title




        db.getCurrentUser(databaseCallback,Settings.Secure.getString(getContentResolver(), Settings.Secure.ANDROID_ID));
    }
}
