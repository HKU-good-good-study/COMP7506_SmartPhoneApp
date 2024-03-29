package com.example.groupproject.model;

import java.util.ArrayList;

public class User {
    private String username;

    private ArrayList<Object> postList;
    private String email;
    private Boolean owner;
    private String machineCode;

    public User() {} //For deserializing the class....

    public User(String username, ArrayList<Object> postList, String email, Boolean owner, String machineCode) {
        this.username = username;
        this.email = email;
        this.machineCode = machineCode;
        this.postList = postList;
        this.owner = owner;
    }

    public String getMachineCode() {
        return machineCode;
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

    public ArrayList<Object> getPostList() {
        return postList;
    }

    public void setPostList(ArrayList<Object> postList) {
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
