package com.example.groupproject.activity;

import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.groupproject.R;
import com.example.groupproject.controller.DatabaseCallback;
import com.example.groupproject.controller.DatabaseController;
import com.example.groupproject.model.Post;
import com.example.groupproject.model.User;

import java.util.ArrayList;
import java.util.List;

public class PostListActivity extends AppCompatActivity {
    RecyclerView recyclerView;
    DatabaseController db = DatabaseController.getInstance();
    ArrayList<Object> list = new ArrayList<>();
    DatabaseCallback databaseCallbackPost = new DatabaseCallback(this) {
        @Override
        public void run(List<Object> dataList) { //get all posts
            ArrayList<Post> temp = new ArrayList<>();
            for (Object item : dataList) {
                temp.add((Post) item);
            }
//            Toast.makeText(getContext(),"In PostListActivity!!", Toast.LENGTH_SHORT).show();
            RecyclerViewAdapter recyclerViewAdapter = new RecyclerViewAdapter(getContext(),  temp);
            recyclerView = findViewById(R.id.my_recycler_view);
//            Log.e("PostListActivity: ","Post found: " + temp.toString());
            recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
            recyclerView.setAdapter(recyclerViewAdapter);
        }
        @Override
        public void successlistener(Boolean success) {}
    };
    DatabaseCallback databaseCallback = new DatabaseCallback(this) {
        @Override
        public void run(List<Object> dataList) {
            User current_user = (User) dataList.get(0);
            db.getPosts(databaseCallbackPost,current_user.getUsername());
        }
        @Override
        public void successlistener(Boolean success) {}
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post_creation);


        db.getCurrentUser(databaseCallback,Settings.Secure.getString(getContentResolver(), Settings.Secure.ANDROID_ID));
    }
}
