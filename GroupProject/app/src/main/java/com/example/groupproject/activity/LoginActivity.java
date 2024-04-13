package com.example.groupproject.activity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.groupproject.controller.DatabaseCallback;
import com.example.groupproject.controller.DatabaseController;
import com.example.groupproject.databinding.ActivityLoginBinding;
import com.example.groupproject.model.User;

import java.util.List;
import java.util.Objects;

public class LoginActivity extends AppCompatActivity {
    ActivityLoginBinding binding;
    User currentUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityLoginBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        binding.loginBtn.setOnClickListener(v -> {
            String email = Objects.requireNonNull(binding.emailIdET.getText()).toString();
            String password = Objects.requireNonNull(binding.passwordET.getText()).toString();

            if (email.isEmpty() || password.isEmpty()) {
                binding.emailIdET.setError("Email or password is empty");
                binding.passwordET.setError("Email or password is empty");
                return;
            }

            DatabaseController db = DatabaseController.getInstance();
            db.getUser(new DatabaseCallback(this) {
                @Override
                public void run(List<Object> dataList) {
                    currentUser = (User) dataList.get(0);
                    Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                    startActivity(intent);
                    finish();
                }

                @Override
                public void successlistener(Boolean success) {
                    if (!success) {
                        Toast.makeText(LoginActivity.this, "Invalid email or password", Toast.LENGTH_SHORT).show();
                    }
                }
            }, false, email);

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
