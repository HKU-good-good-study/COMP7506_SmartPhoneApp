package com.example.groupproject.activity;

import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.core.content.PackageManagerCompat;

import android.content.Intent;
import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

import com.example.groupproject.R;
import com.example.groupproject.controller.DatabaseCallback;
import com.example.groupproject.controller.DatabaseController;
import com.example.groupproject.model.Post;
import com.example.groupproject.model.User;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MainActivity extends AppCompatActivity {
    DatabaseController db = DatabaseController.getInstance();
    ActivityResultLauncher<String[]> permissionResultLauncher;
    private boolean readPermission;
    private boolean locationPermission;
    private boolean cameraPermission;
    private TextView usernameDisplay;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        permissionResultLauncher = registerForActivityResult(new ActivityResultContracts.RequestMultiplePermissions(), new ActivityResultCallback<Map<String, Boolean>>() {
            @Override
            public void onActivityResult(Map<String, Boolean> o) {
                if (o.get(Manifest.permission.READ_EXTERNAL_STORAGE) != null)
                    readPermission = o.get(Manifest.permission.READ_EXTERNAL_STORAGE);

                if (o.get(Manifest.permission.CAMERA) != null)
                    readPermission = o.get(Manifest.permission.CAMERA);

                if (o.get(Manifest.permission.ACCESS_FINE_LOCATION) != null)
                    readPermission = o.get(Manifest.permission.ACCESS_FINE_LOCATION);

            }
        });
        requestPermissions();

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
//                    Intent postCreate = new Intent(getContext(), PostListActivity.class);
//                    getContext().startActivity(postCreate);
                    Intent postCreate = new Intent(getContext(), PostCreateActivity.class);
                    getContext().startActivity(postCreate);
                    Toast.makeText(getContext(),"Username conflict!", Toast.LENGTH_SHORT).show();
                }
            }
        };

        db.createUser(databaseCallback, new User("111",new ArrayList<>(), "123.com", false, Settings.Secure.getString(getContentResolver(), Settings.Secure.ANDROID_ID)));
//        db.createPost(databaseCallback, new Post("111","somewhere", new HashMap<>(),null, true));
//        db.getCurrentUser(databaseCallback, Settings.Secure.getString(getContentResolver(), Settings.Secure.ANDROID_ID));
//        db.logoutUser(databaseCallback, "firstuser2",Settings.Secure.getString(getContentResolver(), Settings.Secure.ANDROID_ID));
//        db.deleteUser(databaseCallback, "203a5e7037811ed9");
    }

    private void requestPermissions(){
        cameraPermission = ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.CAMERA
        ) == PackageManager.PERMISSION_GRANTED;

        readPermission = ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.READ_EXTERNAL_STORAGE
        ) == PackageManager.PERMISSION_GRANTED;

        locationPermission = ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)==PackageManager.PERMISSION_GRANTED;

        List<String> permissionRequest = new ArrayList<String>();

        if (!cameraPermission) {
            permissionRequest.add(Manifest.permission.CAMERA);
        }
        if (!readPermission)
            permissionRequest.add(Manifest.permission.READ_EXTERNAL_STORAGE);
        if (!locationPermission)
            permissionRequest.add(Manifest.permission.ACCESS_FINE_LOCATION);

        if (!permissionRequest.isEmpty())
            permissionResultLauncher.launch(permissionRequest.toArray(new String[0]));
    }




}