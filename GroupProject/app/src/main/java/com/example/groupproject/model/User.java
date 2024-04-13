package com.example.groupproject.model;

import java.util.ArrayList;

public class User {
    private String username;
    private String password;
    private ArrayList<String> postList;
    private String email;
    private Boolean owner;
    private String machineCode;

    public User() {} //For deserializing the class....

    public User(String username, String password, ArrayList<String> postList, String email, Boolean owner, String machineCode) {
        this.username = username;
        this.password = password;
        this.email = email;
        this.machineCode = machineCode;
        this.postList = postList;
        this.owner = owner;
    }

    public User(String username,  ArrayList<String> postList, String email, Boolean owner, String machineCode) {
        this.username = username;
        this.email = email;
        this.machineCode = machineCode;
        this.postList = postList;
        this.owner = owner;
    }

    public String getMachineCode() {
        return machineCode;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
    public void addPost (String postid) {
        postList.add(postid);
    }

    public void deletePost(String postid) {
        postList.remove(postid);
    }
    public void setMachineCode(String machineCode) {
        this.machineCode = machineCode;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public ArrayList<String> getPostList() {
        return postList;
    }

    public void setPostList(ArrayList<String> postList) {
        this.postList = postList;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public Boolean getOwner() {
        return owner;
    }

    public void setOwner(Boolean owner) {
        this.owner = owner;
    }


}
