package com.example.groupproject.model;

import java.util.ArrayList;

public class Location {
    private String longitude;
    private String latitude;
    private String City;
    private String Country;
    private ArrayList<String> posts;

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

    public String getCity() {
        return City;
    }

    public void setCity(String city) {
        City = city;
    }

    public String getCountry() {
        return Country;
    }

    public void setCountry(String country) {
        Country = country;
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
