package com.example.groupproject.activity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.groupproject.controller.DatabaseCallback;
import com.example.groupproject.controller.DatabaseController;
import com.example.groupproject.databinding.ActivitySettingsBinding;
import com.example.groupproject.model.User;

import java.util.List;

public class SettingsActivity extends AppCompatActivity {

    private User user;
    ActivitySettingsBinding binding;
    DatabaseController db = DatabaseController.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivitySettingsBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        user = db.getCurrentUser();

        binding.backButtonInSettings.setOnClickListener(v -> {
            Intent intent = new Intent(SettingsActivity.this, MainActivity.class);
            startActivity(intent);
            finish();
        });

        binding.logoutButton.setOnClickListener(v -> {
            // Logout the user
            // Redirect to the login page
            user.setMachineCode("");
            db.updateUser(new DatabaseCallback(this) {
                @Override
                public void run(List<Object> dataList) {

                }

                @Override
                public void successlistener(Boolean success) {
                    if (success) {
                        Toast.makeText(SettingsActivity.this, "Successfully logged out!", Toast.LENGTH_SHORT).show();

                        db.deleteCurrentUser();

                        Intent intent = new Intent(SettingsActivity.this, LoginActivity.class);
                        // Set the new task and clear flags
                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);

                        startActivity(intent);

                        // This method is used to finish all the activities in the current task.
                        finishAffinity();
                    } else {
                        Toast.makeText(SettingsActivity.this, "Failed to logout!", Toast.LENGTH_SHORT).show();
                    }
                }
            }, user.getUsername(), user);
        });

        binding.deleteAccountButton.setOnClickListener(v -> {
            // Delete the user account
            // Redirect to the login page
            db.deleteUser(new DatabaseCallback(this) {
                @Override
                public void run(List<Object> dataList) {

                }

                @Override
                public void successlistener(Boolean success) {
                    if (success) {
                        Toast.makeText(SettingsActivity.this, "Successfully deleted account!", Toast.LENGTH_SHORT).show();

                        db.deleteCurrentUser();

                        Intent intent = new Intent(SettingsActivity.this, LoginActivity.class);
                        // Set the new task and clear flags
                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);

                        startActivity(intent);

                        // This method is used to finish all the activities in the current task.
                        finishAffinity();
                    } else {
                        Toast.makeText(SettingsActivity.this, "Failed to delete account!", Toast.LENGTH_SHORT).show();
                    }
                }
            }, user.getUsername());
        });
    }
}