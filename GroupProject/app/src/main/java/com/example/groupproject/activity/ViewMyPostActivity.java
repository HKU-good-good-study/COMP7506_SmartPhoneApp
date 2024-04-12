package com.example.groupproject.activity;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
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


import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.groupproject.R;
import com.example.groupproject.controller.DatabaseCallback;
import com.example.groupproject.controller.DatabaseController;
import com.example.groupproject.model.Post;
import com.example.groupproject.model.User;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.List;


public class ViewMyPostActivity extends AppCompatActivity {

    private ImageView photoImage;
    private String selectedBitmap;

    private Post clickedPost;

    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_showpost);

        Intent intent = getIntent();
        clickedPost = (Post) intent.getSerializableExtra("POST");//get the clicked post from last page;

        // binding variables with layout
        FloatingActionButton backButton = findViewById(R.id.backButton_inView);
        photoImage = findViewById(R.id.pose_ImageView_inView);
        TextView userInputText = findViewById(R.id.pose_TextView_inView);
        TextView privateflag = findViewById(R.id.post_privateflag_inView);
        TextView location = findViewById(R.id.pose_location_inView);
        Button deleteButton = findViewById(R.id.delete_Button);



        // set click listener for back button
        backButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
//                Toast.makeText(ViewMyPostActivity.this, "clickedPost:"+ clickedPost.toString(), Toast.LENGTH_SHORT).show();
                finish();
            }
        });

        // get the title of the post and set it to userInputText
        String postTitle = clickedPost.getTitle();
        userInputText.setText(postTitle);

        // get and set the image of the picture
        selectedBitmap = clickedPost.getPhoto().get(0);
        photoImage.setImageBitmap(StringToBitMap(selectedBitmap));

        //set position
        String position = clickedPost.getLocation();
        location.setText(position);

        //set privateflag
        boolean privateFlag = clickedPost.getPublic();
        if(privateFlag){
            privateflag.setText("Private");
        }else{privateflag.setText("Public");}

        DatabaseController db = DatabaseController.getInstance();

        deleteButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                DatabaseCallback databaseCallback = new DatabaseCallback(ViewMyPostActivity.this) {
                    @Override
                    public void run(List<Object> dataList) {

                    }

                    @Override
                    public void successlistener(Boolean success) {
                        Toast.makeText(ViewMyPostActivity.this, "successfully delete the post", Toast.LENGTH_SHORT).show();
                        finish();


                    }
                };
                DatabaseCallback databaseCallbackUser = new DatabaseCallback(ViewMyPostActivity.this) {
                    @Override
                    public void run(List<Object> dataList) {
                        User current_user = (User) dataList.get(0);
                        DatabaseCallback databaseCallbackUpdateNumber = new DatabaseCallback(ViewMyPostActivity.this) {
                            @Override
                            public void run(List<Object> dataList) {

                            }

                            @Override
                            public void successlistener(Boolean success) {

                            }
                        };
                        current_user.deletePost(clickedPost.getId());
                        db.updateUser(databaseCallbackUpdateNumber,current_user.getUsername(),current_user);

                    }

                    @Override
                    public void successlistener(Boolean success) {

                    }
                };
                db.deletePost(databaseCallback,clickedPost.getId());
                db.getCurrentUser(databaseCallbackUser, Settings.Secure.getString(getContentResolver(), Settings.Secure.ANDROID_ID));

            }
        });



//        Bundle bundle = result.getData().getExtras();
//        photoBitmap = (Bitmap) bundle.get("data");
//        photoImage.setImageBitmap(photoBitmap);
//        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
//        photoBitmap.compress(Bitmap.CompressFormat.PNG,100, byteArrayOutputStream); // 100 is permission code for location
//        byte[] b = byteArrayOutputStream.toByteArray();
//        String bitMapString = Base64.encodeToString(b, Base64.DEFAULT);
//        photoList.add(bitMapString);
//
//        try {
//
//            //show the photo

//
//        } catch (Exception e) {
//            e.printStackTrace();
////                                    Log.e("ViewMyPost: ",e.toString());
//        }
//
    }

    /**
     * Coverts a string to a bitmap
     *
     * @param encodedString the String needed to be converted to a bitmap
     * @return a bit map if no exception, else null
     */
    public Bitmap StringToBitMap(String encodedString){
        try {
            byte [] encodeByte= Base64.decode(encodedString,Base64.DEFAULT);
            Bitmap bitmap= BitmapFactory.decodeByteArray(encodeByte, 0, encodeByte.length);
            return bitmap;
        } catch(Exception e) {
            e.getMessage();
            return null;
        }
    }


}
