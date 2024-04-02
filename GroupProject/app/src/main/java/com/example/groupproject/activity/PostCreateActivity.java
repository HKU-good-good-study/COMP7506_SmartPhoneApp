package com.example.groupproject.activity;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.location.LocationManager;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Switch;
import android.widget.TextView;

import androidx.activity.result.ActivityResultLauncher;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.groupproject.R;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;
import java.util.HashMap;


public class PostCreateActivity extends AppCompatActivity {
    private ImageView photoImage;
    private Button confirmButton;

    private ActivityResultLauncher<Intent> activityResultLauncher;

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
        @SuppressLint("UseSwitchCompatOrMaterialCode") Switch photoSave = findViewById(R.id.privateOnly_switch);
        @SuppressLint("UseSwitchCompatOrMaterialCode") Switch locationSave = findViewById(R.id.location_switch);

        // set click listener for back button
        backButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                finish();
            }
        });


    }

    public void savePostImage(Context context, String qrHash, String location, String bitmap, String codeContent){
        // Image added
        ArrayList<String> location1 = new ArrayList<>();
        location1.add(location);
        HashMap<String, String> bitmapHash = new HashMap<>();
//                    Log.e("saveImage",bitmap);
//        bitmapHash.put((String) currentPlayer.get("Identifier"), bitmap);
//        qrCodeController.saveQr(qrHash, score, location1,null ,bitmapHash,codeContent, true);

    }


}
