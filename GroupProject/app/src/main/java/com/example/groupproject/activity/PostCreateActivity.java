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
import android.provider.Settings;
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
import androidx.annotation.NonNull;
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
import java.util.UUID;


public class PostCreateActivity extends AppCompatActivity {
    private static final int CAMERA_ACTION_CODE = 100;
    private ImageView photoImage;
    private Button confirmButton;

    private ActivityResultLauncher<Intent> permissionResultLauncher;

    private Bitmap photoBitmap;

    private boolean cameraPermission;

    private Post currentpost;

    private ArrayList<String> photoList = new ArrayList<>();

    private Boolean private_Only = false, saveLocation = false;
    DatabaseController db = DatabaseController.getInstance();

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

        photoImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //get the bitmap for the picture
                checkPermission(v.getContext());
            }

        });

        permissionResultLauncher = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), new ActivityResultCallback<ActivityResult>() {
                @Override
            public void onActivityResult(ActivityResult result) {

                if ( result.getData() != null) {
                    Bundle bundle = result.getData().getExtras();
                    photoBitmap = (Bitmap) bundle.get("data");
                    photoImage.setImageBitmap(photoBitmap);
                    ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
                    photoBitmap.compress(Bitmap.CompressFormat.PNG,100, byteArrayOutputStream); // 100 is permission code for location
                    byte[] b = byteArrayOutputStream.toByteArray();
                    String bitMapString = Base64.encodeToString(b, Base64.DEFAULT);
                    photoList.add(bitMapString);

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


                                    DatabaseCallback databaseCallbackUser = new DatabaseCallback(PostCreateActivity.this) {
                                        @Override
                                        public void run(List<Object> dataList) {
                                            User current_user = (User) dataList.get(0);


//                                            Toast.makeText(PostCreateActivity.this, "getText:"+ userInputEditText.getText().toString(), Toast.LENGTH_SHORT).show();
                                            currentpost = new Post(UUID.randomUUID().toString(),
                                                    userInputEditText.getText().toString(),
                                                    null,
                                                    current_user.getUsername(),
                                                    null,
                                                    null,
                                                    photoList,
                                                    private_Only);

//                                            currentpost.setUser(current_user.getUsername());
//                                            String userText = userInputEditText.getText().toString();
//                                            currentpost.setTitle(userText);
//                                            currentpost.setPhoto(photoList);
//                                            currentpost.setPublic(private_Only);
////                                                        currentpost.setLocation();
//                                            currentpost.setId(UUID.randomUUID().toString());


                                            Toast.makeText(PostCreateActivity.this, "now is in confirm", Toast.LENGTH_SHORT).show();
                                            DatabaseCallback databaseCallback = new DatabaseCallback(PostCreateActivity.this) {
                                                @Override
                                                public void run(List<Object> dataList) {}
                                                @Override
                                                public void successlistener(Boolean success) {
                                                    if(success){
                                                        Toast.makeText(getContext(),"save finished", Toast.LENGTH_LONG).show();
                                                        DatabaseCallback databaseCallbackUser = new DatabaseCallback(PostCreateActivity.this) {
                                                            @Override
                                                            public void run(List<Object> dataList) {

                                                            }

                                                            @Override
                                                            public void successlistener(Boolean success) {
                                                                if(success){
                                                                    finish();
                                                                }
                                                            }
                                                        };
                                                        current_user.addPost(currentpost.getId());
                                                        db.updateUser(databaseCallbackUser,current_user.getUsername(),current_user);

                                                    }else{
                                                        Toast.makeText(getContext(),"save failed", Toast.LENGTH_LONG).show();
                                                    }
                                                }
                                            };

                                            db.createPost(databaseCallback,currentpost);




                                            }
                                        @Override
                                        public void successlistener(Boolean success) {}
                                    };

                                    db.getCurrentUser(databaseCallbackUser, Settings.Secure.getString(getContentResolver(), Settings.Secure.ANDROID_ID));



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


    /**
     * Check the permission of the camera
     * @param context QrScannedActivity
     */
    public void checkPermission(Context context){

        // ask permission for user camera
        if (ContextCompat.checkSelfPermission(context,
                Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions((Activity) context,
                    new String[]{Manifest.permission.CAMERA},
                    CAMERA_ACTION_CODE);
        } else {
            Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            permissionResultLauncher.launch(intent);
        }
    }
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        Log.e("PostCreateActivity: ", "Request now has the result");
        switch (requestCode) {
            case CAMERA_ACTION_CODE:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                    // Pop camera
                    Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                    permissionResultLauncher.launch(intent);
                    break;
                }
        }
    }
}