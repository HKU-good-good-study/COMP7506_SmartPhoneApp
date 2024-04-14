package com.example.groupproject.activity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.SearchView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.groupproject.adapter.UserAdapter;
import com.example.groupproject.controller.DatabaseCallback;
import com.example.groupproject.controller.DatabaseController;
import com.example.groupproject.databinding.ActivitySearchBinding;
import com.example.groupproject.model.User;

import java.util.ArrayList;
import java.util.List;

public class SearchActivity extends AppCompatActivity implements UserAdapter.OnItemClickListener {
    private ActivitySearchBinding binding;
    private DatabaseController db = DatabaseController.getInstance();
    private RecyclerView recyclerView;
    private SearchView searchView;
    private UserAdapter userAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivitySearchBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        recyclerView = binding.usersRV;
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        userAdapter = new UserAdapter(new ArrayList<>());
        recyclerView.setAdapter(userAdapter);

        searchView = binding.searchBar;

        userAdapter.setOnItemClickListener(this);

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                searchUsers(query);
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                searchUsers(newText);
                return false;
            }
        });

        binding.backButtonInSearch.setOnClickListener(v -> {
            Intent intent = new Intent(SearchActivity.this, MainActivity.class);
            startActivity(intent);
            finish();
        });
    }

    private void searchUsers(String query) {
        DatabaseCallback databaseCallback = new DatabaseCallback(this) {
            @Override
            public void run(List<Object> dataList) {
                List<User> users = new ArrayList<>();
                for (Object data : dataList) {
                    if (data != null) {
                        users.add((User) data);
                    }
                }

                userAdapter.updateData(users);
            }

            @Override
            public void successlistener(Boolean success) {
                if (success) {
                    Toast.makeText(SearchActivity.this, "Search completed successfully", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(SearchActivity.this, "Search failed!", Toast.LENGTH_SHORT).show();
                }
            }
        };

        // Fetch users based on the search query
        db.searchUser(databaseCallback, query);
    }

    @Override
    public void onItemClick(User user) {
        Intent intent = new Intent(this, ProfileActivity.class);
        intent.putExtra("USER", user.getUsername());
        startActivity(intent);
    }
}