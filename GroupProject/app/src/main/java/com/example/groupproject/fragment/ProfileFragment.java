package com.example.groupproject.fragment;

import android.os.Bundle;
import android.provider.Settings;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.fragment.app.Fragment;

import com.example.groupproject.controller.DatabaseCallback;
import com.example.groupproject.controller.DatabaseController;
import com.example.groupproject.databinding.FragmentProfileBinding;
import com.example.groupproject.model.User;

import java.util.List;

public class ProfileFragment extends Fragment {
    FragmentProfileBinding binding;
    DatabaseController db = DatabaseController.getInstance();

    public ProfileFragment() {

    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentProfileBinding.inflate(inflater, container, false);

        DatabaseCallback databaseCallback = new DatabaseCallback(getContext()) {
            @Override
            public void run(List<Object> dataList) {
                User currentUser = (User) dataList.get(0);
                // Set the user data to the respective views
                binding.usernameProfile.setText(currentUser.getUsername());
                binding.emailProfile.setText(currentUser.getEmail());
            }

            @Override
            public void successlistener(Boolean success) {
                if (success) {
                    Toast.makeText(getContext(),"User data fetched successfully", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(getContext(),"Failed to fetch user data!", Toast.LENGTH_SHORT).show();
                }
            }
        };

        db.getCurrentUser(databaseCallback, Settings.Secure.getString(getContext().getContentResolver(), Settings.Secure.ANDROID_ID));


        return binding.getRoot();
    }
}
