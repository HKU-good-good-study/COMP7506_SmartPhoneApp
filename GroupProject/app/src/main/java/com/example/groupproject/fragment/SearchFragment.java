package com.example.groupproject.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.SearchView;
import android.widget.Toast;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.groupproject.R;
import com.example.groupproject.adapter.UserAdapter;
import com.example.groupproject.controller.DatabaseCallback;
import com.example.groupproject.controller.DatabaseController;
import com.example.groupproject.databinding.FragmentSearchBinding;
import com.example.groupproject.model.User;

import java.util.ArrayList;
import java.util.List;

public class SearchFragment extends Fragment {
    private FragmentSearchBinding binding;
    private DatabaseController db = DatabaseController.getInstance();
    private RecyclerView recyclerView;
    private SearchView searchView;
    private UserAdapter userAdapter;
    public SearchFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentSearchBinding.inflate(inflater, container, false);
        View view = binding.getRoot();

        recyclerView = view.findViewById(R.id.usersRV);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        userAdapter = new UserAdapter(new ArrayList<>());
        recyclerView.setAdapter(userAdapter);
        searchView = view.findViewById(R.id.searchMenuSearchBar);
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

        return view;
    }

    private void searchUsers(String query) {
        DatabaseCallback databaseCallback = new DatabaseCallback(getContext()) {
            @Override
            public void run(List<Object> dataList) {
                List<User> users = new ArrayList<>();
                for (Object data : dataList) {
                    users.add((User) data);
                }

                userAdapter.updateUsers(users);
            }

            @Override
            public void successlistener(Boolean success) {
                if (success) {
                    Toast.makeText(getContext(),"Search completed successfully", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(getContext(),"Search failed!", Toast.LENGTH_SHORT).show();
                }
            }
        };

        // Fetch users based on the search query
        db.getUser(databaseCallback, false, query);
    }
}