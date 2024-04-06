package com.example.groupproject.model;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.HashMap;

public class Post {
    private String id;
    private String user;
    private Boolean isPublic;
    private String location;
    // A user id --> A list of comments
    private HashMap<String, ArrayList<String>> comments;
    // multiple pics under one post
    private ArrayList<String> photo;
    private String content;
    private String title;
    public Post() {} //For deserializing the class....

    public Post(String id, String title, String content, String user, String location, HashMap<String, ArrayList<String>> comments, ArrayList <String> photo, Boolean isPublic){
         this.id = id;
         this.user = user;
         this.isPublic = isPublic;
         this.location = location;
         this.comments = comments;
         this.photo = photo;
         this.title = title;
         this. content = content;
    }

    public Post(String user, String title, String content, String location, HashMap<String, ArrayList<String>> comments, ArrayList <String> photo, Boolean isPublic){
        this.user = user;
        this.isPublic = isPublic;
        this.location = location;
        this.comments = comments;
        this.photo = photo;
        this.title = title;
        this. content = content;
    }

    public String getUser() {
        return user;
    }

    public void setUser(String user) {
        this.user = user;
    }

    public Boolean getPublic() {
        return isPublic;
    }

    public void setPublic(Boolean aPublic) {
        isPublic = aPublic;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public HashMap<String, ArrayList<String>> getComments() {
        return comments;
    }

    public void setComments(HashMap<String, ArrayList<String>> comments) {
        this.comments = comments;
    }

    public ArrayList<String> getPhoto() {
        return photo;
    }

    public void setPhoto(ArrayList<String> photo) {
        this.photo = photo;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public void setTitle(String title) {
        this.title = title;
    }
    public String getTitle() {
        return title;
    }
}
