package com.example.groupproject.activity;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.location.LocationManager;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;


import com.example.groupproject.R;
import com.example.groupproject.controller.DatabaseCallback;
import com.example.groupproject.controller.DatabaseController;
import com.example.groupproject.model.Post;
import com.example.groupproject.model.User;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.io.ByteArrayOutputStream;
import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class PostCreateActivity extends AppCompatActivity {
    private static final int CAMERA_ACTION_CODE = 100;
    private ImageView photoImage;
    private Button confirmButton;

    private ActivityResultLauncher<Intent> activityResultLauncher;

    private Bitmap photoBitmap;

    private Post currentpost = new Post();

    private Boolean private_Only = false, saveLocation = false;

    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_postpose);

//        // setup for getting location:
//        LocationManager locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
//        FusedLocationProviderClient fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);
//
//        // get location in locationController
//        locationController.run(this, null, locationManager,  fusedLocationProviderClient);

        // binding variables with layout
        photoImage = findViewById(R.id.pose_ImageView);
        confirmButton = findViewById(R.id.confirm_Button);
        FloatingActionButton backButton = findViewById(R.id.backButton);
        TextView title = findViewById(R.id.title_textView);
        EditText userInputEditText = findViewById(R.id.pose_TextView);
        @SuppressLint("UseSwitchCompatOrMaterialCode") Switch privateOnlySave = findViewById(R.id.privateOnly_switch);
        @SuppressLint("UseSwitchCompatOrMaterialCode") Switch locationSave = findViewById(R.id.location_switch);

        // set click listener for back button
        backButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                finish();
            }
        });

        activityResultLauncher = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), new ActivityResultCallback<ActivityResult>() {
            @Override
            public void onActivityResult(ActivityResult result) {

                if ( result.getData() != null) {
                    // get the bitmap for the picture
//                    Bundle bundle = result.getData().getExtras();
//                    photoBitmap = (Bitmap) bundle.get("data");
//                    photoImage.setImageBitmap(photoBitmap);
//
//                    ArrayList<Bitmap> photoList = new ArrayList<Bitmap>();
//                    photoList.add(photoBitmap);

                    try {
                        //edit text

                        userInputEditText.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                // 用户点击了输入框，可以执行一些操作，例如弹出软键盘
                                InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                                imm.showSoftInput(userInputEditText, InputMethodManager.SHOW_IMPLICIT);
//                                userInputEditText.setHint(""); // 清除提示文本
                            }
                        });

                        // two switch buttons let user choose either to save image and location or not
                        privateOnlySave.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                if (privateOnlySave.isChecked()) {
                                    Toast.makeText(PostCreateActivity.this, "Published private only", Toast.LENGTH_SHORT).show();
                                    private_Only = true;
                                } else {
                                    private_Only = false;
                                    Toast.makeText(PostCreateActivity.this, "Published to public", Toast.LENGTH_SHORT).show();
                                }
                            }
                        });

                        locationSave.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                if (locationSave.isChecked()) {
                                    saveLocation = true;
                                } else {

                                }
                            }
                        });

                        confirmButton.setOnClickListener(new View.OnClickListener() { // save the post after confirm.
                            @Override
                            public void onClick(View view) {

                                try { // save post's information

                                    Post currentpost = new Post();
                                    Toast.makeText(PostCreateActivity.this, "now is in confirm", Toast.LENGTH_SHORT).show();
                                    DatabaseController db = DatabaseController.getInstance();

                                    DatabaseCallback databaseCallbackUser = new DatabaseCallback(PostCreateActivity.this) {
                                        @Override
                                        public void run(List<Object> dataList) {
                                            User current_user = (User) dataList.get(0);
                                            currentpost.setUser(current_user.getUsername());
                                        }
                                        @Override
                                        public void successlistener(Boolean success) {}
                                    };
                                    DatabaseCallback databaseCallback = new DatabaseCallback(PostCreateActivity.this) {
                                        @Override
                                        public void run(List<Object> dataList) {

                                        }

                                        @Override
                                        public void successlistener(Boolean success) {
                                            try {
                                                String userText = userInputEditText.getText().toString();
                                                currentpost.setContent(userText);
//                                            currentpost.setPhoto(photoList);
                                                currentpost.setPublic(private_Only);
//                                                        currentpost.setLocation();

                                            }  catch (Exception e) {
                                                e.printStackTrace();}

                                        }
                                    };
                                    db.createPost(databaseCallback,currentpost);


                                }catch (Exception e){
                                    e.printStackTrace();
//                                        Log.e("QrScannedActivity: ",e.toString());
                                }
                            }
                        });

                } catch (Exception e) {
                        e.printStackTrace();
//                                    Log.e("QrScannedActivity: ",e.toString());
                    }
                }
            }



        });


    }
}