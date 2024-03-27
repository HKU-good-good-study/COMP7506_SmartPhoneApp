package com.example.groupproject.controllers;

import com.google.firebase.Firebase;
import com.google.firebase.firestore.FirebaseFirestore;

public class DatabaseController {
    private static DatabaseController dbInstance;
    final private FirebaseFirestore db;
    final private String[] collections = {"User", "Post","LocationIndex"};
    private DatabaseController(){
        db = FirebaseFirestore.getInstance();
    }
}
