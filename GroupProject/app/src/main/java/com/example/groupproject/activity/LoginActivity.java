package com.example.groupproject.activity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.provider.Settings;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.preference.PreferenceManager;

import com.example.groupproject.controller.DatabaseCallback;
import com.example.groupproject.controller.DatabaseController;
import com.example.groupproject.databinding.ActivityLoginBinding;
import com.example.groupproject.model.User;

import java.util.List;
import java.util.Objects;

public class LoginActivity extends AppCompatActivity {
    ActivityLoginBinding binding;
    User currentUser;
    SharedPreferences sharedPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityLoginBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());

        binding.loginBtn.setOnClickListener(v -> {
            String username = Objects.requireNonNull(binding.usernameET.getText()).toString();
            String password = Objects.requireNonNull(binding.passwordET.getText()).toString();

            if (username.isEmpty() || password.isEmpty()) {
                binding.usernameET.setError("Username or password is empty");
                binding.passwordET.setError("Username or password is empty");
                return;
            }

            DatabaseController db = DatabaseController.getInstance();
            db.getUser(new DatabaseCallback(this) {
                @Override
                public void run(List<Object> dataList) {
                    if (!dataList.isEmpty()) {
                        User fetchedUser = (User) dataList.get(0);
                        if (fetchedUser != null && fetchedUser.getPassword().equals(password)) {
                            currentUser = fetchedUser;
                            currentUser.setMachineCode(Settings.Secure.getString(getContentResolver(), Settings.Secure.ANDROID_ID));
                            db.updateUser(new DatabaseCallback(LoginActivity.this) {
                                @Override
                                public void run(List<Object> dataList) {
                                }

                                @Override
                                public void successlistener(Boolean success) {
                                    if (success) {
                                        Toast.makeText(LoginActivity.this, "Successfully logged in!", Toast.LENGTH_SHORT).show();

                                        SharedPreferences.Editor editor = sharedPreferences.edit();
                                        editor.putString("username", currentUser.getUsername());
                                        editor.apply();

                                        Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                                        finish();
                                        startActivity(intent);
                                    } else {
                                        Toast.makeText(LoginActivity.this, "Failed to login!", Toast.LENGTH_SHORT).show();
                                    }
                                }
                            }, currentUser.getUsername(), currentUser);
                        } else {
                            Toast.makeText(LoginActivity.this, "Invalid username or password", Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        Toast.makeText(LoginActivity.this, "Invalid username or password", Toast.LENGTH_SHORT).show();
                    }
                }

                @Override
                public void successlistener(Boolean success) {
                    if (!success) {
                        Toast.makeText(LoginActivity.this, "Error occurred while fetching user data", Toast.LENGTH_SHORT).show();
                    }
                }
            }, false, username);

        });

        binding.gotoSignup.setOnClickListener(v -> {
            Intent intent = new Intent(LoginActivity.this, SignUpActivity.class);
            startActivity(intent);
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
        if (currentUser != null) {
            Intent intent = new Intent(LoginActivity.this, MainActivity.class);
            startActivity(intent);
            finish();
        }
    }
}
