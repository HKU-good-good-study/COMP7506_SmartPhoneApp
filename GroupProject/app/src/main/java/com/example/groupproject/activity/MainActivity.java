package com.example.groupproject.activity;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

import com.example.groupproject.R;
import com.example.groupproject.controller.DatabaseCallback;
import com.example.groupproject.controller.DatabaseController;
import com.example.groupproject.model.User;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class MainActivity extends AppCompatActivity {
    DatabaseController db = DatabaseController.getInstance();
    private TextView usernameDisplay;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        usernameDisplay = findViewById(R.id.username_display);

        // User this callback to fetch current user or other operations with databaseController
        DatabaseCallback databaseCallback = new DatabaseCallback(this) {
            @Override
            public void run(List<Object> dataList) { //Used for fetch user
                User currentUser = (User) dataList.get(0);
                usernameDisplay.setText(currentUser.getUsername());
            }

            @Override
            public void successlistener(Boolean success) {
                if (success) {
                    Toast.makeText(getContext(),"User is created", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(getContext(),"Username conflict!", Toast.LENGTH_SHORT).show();
                }
            }
        };

//        db.createUser(databaseCallback, new User("firstuser1",new ArrayList<>(), "123.com", false, Settings.Secure.getString(getContentResolver(), Settings.Secure.ANDROID_ID)));
        db.getCurrentUser(databaseCallback, Settings.Secure.getString(getContentResolver(), Settings.Secure.ANDROID_ID));
    }




}