package com.example.groupproject.activity;

import android.Manifest;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.groupproject.R;
import com.example.groupproject.controller.DatabaseCallback;

import com.example.groupproject.controller.DatabaseController;
import com.example.groupproject.model.Post;
import com.example.groupproject.model.User;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class MapActivity extends AppCompatActivity implements OnMapReadyCallback, LocationListener {
    private GoogleMap gMap;
    private boolean isMapReady = false;
    private final int REQUEST_CODE = 101;
    SupportMapFragment mapFragment;
    Button back, post;
    Location currentLocation;
    FusedLocationProviderClient fusedLocationProviderClient;
    private LocationManager locationManager;
    DatabaseController dbcontroller = DatabaseController.getInstance();
    List<Object> postList;
    DatabaseCallback dbcallback = new DatabaseCallback(this) {
        @Override
        public void run(List<Object> dataList) {
            System.out.println("!!!!!!!!!!!!!!!!!");
            System.out.println(dataList);

            for (Object item : dataList) {
                Post post_info = (Post) item;
                String postid = post_info.getId();
                Boolean ispublic = post_info.getPublic();
                String location = post_info.getLocation();

                if (location != null && ispublic) {
                    String[] latLong = location.split(",");
                    System.out.println(latLong[0]);
                    double latitude = Double.parseDouble(latLong[0]);
                    double longitude = Double.parseDouble(latLong[1]);

                    List<Address> addressList = null;
                    Geocoder geocoder = new Geocoder(MapActivity.this);

                    try {
                        addressList = geocoder.getFromLocation(latitude, longitude, 1);
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                    if (addressList != null && !addressList.isEmpty()) {
                        Address address = addressList.get(0);
                        LatLng latLng = new LatLng(address.getLatitude(), address.getLongitude());
                        Marker marker = gMap.addMarker(new MarkerOptions().position(latLng).title("").icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_AZURE)));
                        marker.setTag(postid);
                    } else {
                        Toast.makeText(MapActivity.this, "Location not found", Toast.LENGTH_SHORT).show();
                    }
                }
            }
        }

        @Override
        public void successlistener(Boolean success) {
        }
    };

    DatabaseCallback getDbcallbacksinglepost = new DatabaseCallback(this) {
        @Override
        public void run(List<Object> dataList) {
            for (Object item : dataList) {
                Post post = (Post) item;
                System.out.println("******************");
                System.out.println(post);

                Intent ViewMyPost = new Intent(this.getContext(), ViewMyPostActivity.class);
                // 将整个 Post 对象作为一个 "extra" 放入 Intent 中
                ViewMyPost.putExtra("POST", post);
                this.getContext().startActivity(ViewMyPost);
            }
        }

        @Override
        public void successlistener(Boolean success) {
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map);
        back = findViewById(R.id.map_back);
        post = findViewById(R.id.map_make_post);

        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        post.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent postCreation = new Intent(MapActivity.this, PostCreateActivity.class);
                postCreation.putExtra("latitude", currentLocation.getLatitude());
                postCreation.putExtra("longitude", currentLocation.getLongitude());
                v.getContext().startActivity(postCreation);
            }
        });

        mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        getCurrentLocation();

    }

    @Override
    public void onMapReady(@NonNull GoogleMap googleMap) {
        gMap = googleMap;
        isMapReady = true;

    }


    public void getCurrentLocation() {
        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {}

        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, MapActivity.this);
        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED){
            ActivityCompat.requestPermissions( this,new String[] {Manifest.permission.ACCESS_FINE_LOCATION}, REQUEST_CODE);
            Log.e("getCurrentLocation: ", "Location asking!");
        } else {
            Log.e("getCurrentLocation: ", "Location granted");
        }
        Task<Location> task = fusedLocationProviderClient.getLastLocation();
        task.addOnSuccessListener(new OnSuccessListener<Location>() {
            @Override
            public void onSuccess(Location location) {
                if (location != null){
                    currentLocation = location;
                    Log.e("getCurrentLocation: ", location.toString());
                    mapFragment.getMapAsync(MapActivity.this);
                } if (location == null) {
                    getCurrentLocation();
                }
            }
        });
    }



    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == REQUEST_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                getCurrentLocation();
            } else {
                Toast.makeText(this, String.valueOf(grantResults[0]), Toast.LENGTH_LONG).show();
            }
        }
    }

    @Override
    public void onLocationChanged(@NonNull Location location) {
        currentLocation = location;
        Log.e("Map activity:","location is " + String.valueOf(location.getLatitude()) + " " + String.valueOf(location.getLongitude()));
        if(isMapReady) {
            dbcontroller.getPosts(dbcallback);
            LatLng geolocation = new LatLng(currentLocation.getLatitude(), currentLocation.getLongitude());
            gMap.addMarker(new MarkerOptions().position(geolocation).title("You"));
            gMap.moveCamera(CameraUpdateFactory.newLatLngZoom(geolocation, 12));
            gMap.getUiSettings().setZoomControlsEnabled(true);

            gMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
                @Override
                public boolean onMarkerClick(Marker marker) {
                    Object postid = marker.getTag();
                    System.out.println("post id:");
                    System.out.println(postid);
                    if (postid != null) {
                        String postidstr = postid.toString();
                        dbcontroller.getPost(getDbcallbacksinglepost, postidstr);
                    }
                    return false;
                }
            });
            mapFragment.getMapAsync(MapActivity.this);
        }
    }
}

