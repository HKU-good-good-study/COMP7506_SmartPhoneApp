package com.example.groupproject.controller;

import com.example.groupproject.model.Post;
import com.example.groupproject.model.User;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;

public class DatabaseController {
    private static final User currentUser=null;
    private static DatabaseController dbInstance;
    final private FirebaseFirestore db;
    final private String[] collections = {"User", "Post","LocationIndex"};
    private DatabaseController(){
        db = FirebaseFirestore.getInstance();
    }

    /**
     * TODO:
     *  Ask firestore to save user with given info
     *  Check datatype
     *  Check given username is unique or not
     *  If successful, return true; false otherwise
     */
    public Boolean createUser(){

        return false;
    }

    /**
     * TODO:
     *  Generate unqiue post id to assign
     *  Save post to Firestore with given info
     * @return true if successful, false otherwise
     */
    public Boolean createPost() {

        return false;
    }

    /**
     * TODO:
     *  return current user, check if current user has already loaded or not
     *  if loaded return current user, otherwise ask firestore with user's machine code
     */
    public User getCurrentUser(){

        return currentUser;
    }

    /**
     *
     * TODO: return a user based on username
     *  This method should not touch machine code!
     */
    public User getUser() {
        return null;
    }

    /**
     * Return a post based on post id
     */
    public Post getPost() {
        return null;
    }

    /**
     * Return a list of post based on username
     */
    public ArrayList<Post> getPosts() {
        return null;
    }
}