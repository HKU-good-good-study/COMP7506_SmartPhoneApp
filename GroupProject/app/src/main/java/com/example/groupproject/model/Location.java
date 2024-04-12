package com.example.groupproject.model;

import java.util.ArrayList;

public class Location {
    private String longitude;
    private String latitude;
    private String city;
    private ArrayList<String> posts;
    
    public Location(String longitude, String latitude, String city, ArrayList<String> posts){
        this.longitude = longitude;
        this.latitude = latitude;
        this.city = city;
        this.posts = posts;
    }

    public String getLongitude() {
        return longitude;
    }

    public void setLongitude(String longitude) {
        this.longitude = longitude;
    }

    public String getLatitude() {
        return latitude;
    }

    public void setLatitude(String latitude) {
        this.latitude = latitude;
    }

    public String getcity() {
        return city;
    }

    public void setcity(String city) {
        city = city;
    }

    public ArrayList<String> getPosts() {
        return posts;
    }

    public void setPosts(ArrayList<String> posts) {
        this.posts = posts;
    }

    public void addPost(String postid) {this.posts.add(postid);}

    public void deletePost(String postid) {this.posts.remove(postid);}
}
