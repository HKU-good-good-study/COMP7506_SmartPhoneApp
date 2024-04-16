package com.example.groupproject.activity;

import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.groupproject.controller.DatabaseCallback;
import com.example.groupproject.controller.DatabaseController;
import com.example.groupproject.databinding.ActivityProfileBinding;
import com.example.groupproject.model.User;

import java.util.List;

public class ProfileActivity extends AppCompatActivity {
    ActivityProfileBinding binding;
    DatabaseController db = DatabaseController.getInstance();
    private User currentUser = null;
    private User profileUser = null;
    private Integer postCount = 0;

    private boolean isEditing = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityProfileBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        db.getCurrentUser(new DatabaseCallback(this) {
            @Override
            public void run(List<Object> dataList) {
                currentUser = (User) dataList.get(0);
//                binding.usernameProfile.setText(currentUser.getUsername());
//                binding.emailProfile.setText(currentUser.getEmail());
            }

            @Override
            public void successlistener(Boolean success) {
                if (success) {
                    Toast.makeText(ProfileActivity.this, "User data fetched successfully", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(ProfileActivity.this, "Failed to fetch user data!", Toast.LENGTH_SHORT).show();
                }
            }
        }, Settings.Secure.getString(getContentResolver(), Settings.Secure.ANDROID_ID));

        db.searchUser(new DatabaseCallback(this) {
            @Override
            public void run(List<Object> dataList) {
                if (!dataList.isEmpty()) {
                    profileUser = (User) dataList.get(0);
                }
                Log.d("ProfileActivityGetUser", "run: " + (profileUser == null));
            }

            @Override
            public void successlistener(Boolean success) {
                Log.d("ProfileActivityGetUser", "successlistener: " + success);
                if (success) {
                    Toast.makeText(ProfileActivity.this, "User data fetched successfully", Toast.LENGTH_SHORT).show();
//                    Log.d("ProfileActivity", "onCreate: " + currentUser.getUsername() + " " + profileUser.getUsername());

                    if (profileUser != null && !profileUser.equals(currentUser)) {
                        binding.usernameProfile.setText(profileUser.getUsername());
                        binding.emailProfile.setText(profileUser.getEmail());
                        binding.editProfile.setVisibility(View.GONE);
                        db.getPosts(new DatabaseCallback(ProfileActivity.this) {
                            @Override
                            public void run(List<Object> dataList) {
                                if (!dataList.isEmpty()) {
                                    postCount = dataList.size();
                                    binding.posts.setText(postCount.toString());
                                } else {
                                    binding.posts.setText("0");
                                }
                            }

                            @Override
                            public void successlistener(Boolean success) {

                            }
                        }, profileUser.getUsername());
                    } else if (currentUser != null) {
                        binding.usernameProfile.setText(currentUser.getUsername());
                        binding.emailProfile.setText(currentUser.getEmail());
                        binding.editProfile.setVisibility(View.VISIBLE);

                        db.getPosts(new DatabaseCallback(ProfileActivity.this) {
                            @Override
                            public void run(List<Object> dataList) {
                                if (!dataList.isEmpty()) {
                                    postCount = dataList.size();
                                    binding.posts.setText(postCount.toString());
                                } else {
                                    binding.posts.setText("0");
                                }
                            }

                            @Override
                            public void successlistener(Boolean success) {

                            }
                        }, currentUser.getUsername());
                    } else {
                        binding.usernameProfile.setText("Username");
                        binding.emailProfile.setText("Email");
                        binding.editProfile.setVisibility(View.GONE);
                        binding.posts.setText("0");
                    }
                } else {
                    Toast.makeText(ProfileActivity.this, "Failed to fetch user data!", Toast.LENGTH_SHORT).show();
                }
            }
        }, getIntent().getStringExtra("USER") == null ? "" : getIntent().getStringExtra("USER"));

        Log.d("ProfileActivity", "onCreate: (Intent)" + getIntent().getStringExtra("USER"));

//        if (getIntent().getStringExtra("USER") != null) {
//        }


        binding.backButtonInProfile.setOnClickListener(v -> {
            finish();
        });

        binding.editProfile.setOnClickListener(v -> {
            if (!isEditing) {
                binding.emailProfile.setEnabled(true);
                binding.editProfile.setText("Save");
                isEditing = true;
            } else {
                String email = binding.emailProfile.getText().toString();

                if (email.isEmpty()) {
                    binding.emailProfile.setError("Email is empty");
                    return;
                }

                DatabaseCallback saveCallback = new DatabaseCallback(this) {
                    @Override
                    public void run(List<Object> dataList) {
                        Toast.makeText(ProfileActivity.this, "User data saved successfully", Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void successlistener(Boolean success) {
                        if (success) {
                            Toast.makeText(ProfileActivity.this, "User data saved successfully", Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(ProfileActivity.this, "Failed to save user data!", Toast.LENGTH_SHORT).show();
                        }
                    }
                };

                db.updatePieceData(saveCallback, "User", "username", binding.usernameProfile.getText().toString(), "email", email);
                binding.emailProfile.setEnabled(false);
                binding.editProfile.setText("Edit");
                isEditing = false;
            }
        });
    }
}