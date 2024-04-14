package com.example.groupproject.activity;

import android.Manifest;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.provider.Settings;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.preference.PreferenceManager;

import com.example.groupproject.R;
import com.example.groupproject.controller.DatabaseCallback;
import com.example.groupproject.controller.DatabaseController;
import com.example.groupproject.model.User;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class MainActivity extends AppCompatActivity implements View.OnClickListener, SharedPreferences.OnSharedPreferenceChangeListener {
    DatabaseController db = DatabaseController.getInstance();
    ActivityResultLauncher<String[]> permissionResultLauncher;
    private boolean readPermission;
    private boolean locationPermission;
    private boolean cameraPermission;
    private TextView usernameDisplay;
    private BottomNavigationView bottomNavigationView;

    private Button mapButt;
    private Button leaderboardButt;
    private Button profileButt;
    private Button search;
    private Button settingButt;
    private Button admin;

    private User currentUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mapButt =findViewById(R.id.Map_Button);
        leaderboardButt = findViewById(R.id.Leaderboard_Button);
        profileButt = findViewById(R.id.profile_Button);
        search = findViewById(R.id.Search_Button);
        settingButt = findViewById(R.id.Settings_Button);

        mapButt.setOnClickListener(this);
        profileButt.setOnClickListener(this);
        leaderboardButt.setOnClickListener(this);
        search.setOnClickListener(this);
        settingButt.setOnClickListener(this);
        usernameDisplay = findViewById(R.id.user_textView);

        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        prefs.registerOnSharedPreferenceChangeListener(this);

        //TODO: porbably don't need result launcher since Main activity doesn't need any permission based operations,
        // However, activity uses these permissions should still check permissions before they use camera, internet, location, storage, etc.
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

        // User this callback to fetch current user or other operations with databaseController
        db.getCurrentUser(new DatabaseCallback(this) {
            @Override
            public void run(List<Object> dataList) { //Used for fetch user
                if (dataList.isEmpty()) {
                    // Start login activity
                    Intent intent = new Intent(MainActivity.this, LoginActivity.class);
                    startActivity(intent);
                    finish();
                } else {
                    currentUser = (User) dataList.get(0);
                    usernameDisplay.setText(currentUser.getUsername());
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
        }, Settings.Secure.getString(getContentResolver(), Settings.Secure.ANDROID_ID));
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


    @Override
    public void onClick(View v) {
        int id = v.getId();
        if (v.getId() == R.id.Map_Button) {
            this.startActivity(new Intent(this, MapActivity.class));
        } else if (id == R.id.profile_Button) {
            Intent postList = new Intent(this, ProfileActivity.class);
            this.startActivity(postList);
        } else if (id == R.id.Search_Button) {
            this.startActivity(new Intent(this, SearchActivity.class));
        } else if (id == R.id.Leaderboard_Button) {
            this.startActivity(new Intent(this, LeaderboardActivity.class));
        } else if (id == R.id.Settings_Button) {
            this.startActivity(new Intent(this, SettingsActivity.class));
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        // Unregister the listener
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        prefs.unregisterOnSharedPreferenceChangeListener(this);
    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, @Nullable String key) {
        if (key.equals("username")) {
            // Call recreate() to refresh the activity
            recreate();
        }
    }
}