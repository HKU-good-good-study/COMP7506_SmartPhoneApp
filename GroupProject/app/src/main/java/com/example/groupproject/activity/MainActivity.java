package com.example.groupproject.activity;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.provider.Settings;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import com.example.groupproject.fragment.HomeFragment;
import com.example.groupproject.fragment.ProfileFragment;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.example.groupproject.R;
import com.example.groupproject.controller.DatabaseCallback;
import com.example.groupproject.controller.DatabaseController;
import com.example.groupproject.model.User;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class MainActivity extends AppCompatActivity {
    DatabaseController db = DatabaseController.getInstance();
    ActivityResultLauncher<String[]> permissionResultLauncher;
    private boolean readPermission;
    private boolean locationPermission;
    private boolean cameraPermission;
    private TextView usernameDisplay;
    private BottomNavigationView bottomNavigationView;

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
//        usernameDisplay = findViewById(R.id.username_display);

        // User this callback to fetch current user or other operations with databaseController
        DatabaseCallback databaseCallback = new DatabaseCallback(this) {
            @Override
            public void run(List<Object> dataList) { //Used for fetch user
                if (dataList.isEmpty()) {
                    // Start login activity
                    Intent intent = new Intent(MainActivity.this, LoginActivity.class);
                    startActivity(intent);
                    finish();
                } else {
//                    User currentUser = (User) dataList.get(0);
//                    usernameDisplay.setText(currentUser.getUsername());

                }
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

//        db.createUser(databaseCallback, new User("firstuser7",new ArrayList<>(), "123678.com", false, Settings.Secure.getString(getContentResolver(), Settings.Secure.ANDROID_ID)));
        db.getCurrentUser(databaseCallback, Settings.Secure.getString(getContentResolver(), Settings.Secure.ANDROID_ID));
//        db.logoutUser(databaseCallback, "firstuser7",Settings.Secure.getString(getContentResolver(), Settings.Secure.ANDROID_ID));
//        db.deleteUser(databaseCallback, "2434f84c0dfd68de");

        bottomNavigationView = findViewById(R.id.bottom_navigation);
        bottomNavigationView.setOnNavigationItemSelectedListener(item -> {
            Fragment selectedFragment = null;
            int id = item.getItemId();
            if (id == R.id.navigation_home) {
                // Open home fragment
                selectedFragment = new HomeFragment();
            } else if (id == R.id.navigation_profile) {
                // Open profile fragment
                selectedFragment = new ProfileFragment();
            }

            assert selectedFragment != null;
            getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, selectedFragment).commit();
            return true;
        });

        getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new HomeFragment()).commit();
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