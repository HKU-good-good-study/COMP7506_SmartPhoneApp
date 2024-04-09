package com.example.groupproject.activity;

import android.content.Intent;
import android.os.Bundle;
import android.provider.Settings;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.groupproject.controller.DatabaseCallback;
import com.example.groupproject.controller.DatabaseController;
import com.example.groupproject.databinding.ActivitySignUpBinding;
import com.example.groupproject.model.User;
import com.google.firebase.auth.FirebaseAuth;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class SignUpActivity extends AppCompatActivity {
    ActivitySignUpBinding binding;
    FirebaseAuth auth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivitySignUpBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        auth = FirebaseAuth.getInstance();

        binding.signupBtn.setOnClickListener(v -> {
            String email = Objects.requireNonNull(binding.emailETSignUp.getText()).toString();
            String username = Objects.requireNonNull(binding.usernameETSignUp.getText()).toString();
            String password = Objects.requireNonNull(binding.passwordETSignUp.getText()).toString();

            if (email.isEmpty() || username.isEmpty() || password.isEmpty()) {
                binding.emailETSignUp.setError("Email is empty");
                binding.usernameETSignUp.setError("Username is empty");
                binding.passwordETSignUp.setError("Password is empty");
                return;
            }

            // Create a new User object
            User newUser = new User(username, new ArrayList<>(), "email@example.com", false, Settings.Secure.getString(getContentResolver(), Settings.Secure.ANDROID_ID));

            // Create a DatabaseCallback
            DatabaseCallback databaseCallback = new DatabaseCallback(this) {
                @Override
                public void run(List<Object> dataList) {
                    Intent intent = new Intent(SignUpActivity.this, MainActivity.class);
                    startActivity(intent);
                    finish();
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

            // Get the instance of DatabaseController
            DatabaseController db = DatabaseController.getInstance();

            // Call the createUser method
            db.createUser(databaseCallback, newUser);
        });

        binding.gotoLogin.setOnClickListener(v -> {
            Intent intent = new Intent(SignUpActivity.this, LoginActivity.class);
            startActivity(intent);
        });
    }
}
