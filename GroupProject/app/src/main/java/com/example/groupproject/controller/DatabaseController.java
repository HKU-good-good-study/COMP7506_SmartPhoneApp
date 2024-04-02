package com.example.groupproject.controller;

import android.provider.ContactsContract;
import android.util.Log;

import com.example.groupproject.model.Post;
import com.example.groupproject.model.User;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.firebase.firestore.CollectionReference;

import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class DatabaseController {
    private static User currentUser;
    private static DatabaseController dbInstance;
    final private FirebaseFirestore db;
    final private String[] collections = {"User", "Post","LocationIndex"};
    private DatabaseController(){
        db = FirebaseFirestore.getInstance();
    }

    public static DatabaseController getInstance() {
        if (dbInstance == null)
            dbInstance = new DatabaseController();

        return dbInstance;
    }

    /**
     *
     */
    public void createUser(DatabaseCallback databaseCallback,  User newuser){
        String machineCode = newuser.getMachineCode();
        CollectionReference collectionReference = db.collection("User");
        List<Object> temp = new ArrayList<Object>();

        Query task = collectionReference.whereEqualTo("username", newuser.getUsername());
        task.get().addOnCompleteListener((OnCompleteListener<QuerySnapshot>) runningTask -> {
            boolean success = true;
            if (runningTask.isSuccessful()) {
                for (QueryDocumentSnapshot document : runningTask.getResult()) //fetching from database
                    temp.add(document.getData());
                if (temp.isEmpty()) { //if it's a new user && username is unique:
                    collectionReference.document(newuser.getUsername()).set(newuser);
                } else {
                    //if success is false, maybe the user is login from a new device, logout required in callback
                    success = false;
                }
                    databaseCallback.successlistener(success);
            }
        });
    }

    /**
     * TODO:
     *  Generate unqiue post id to assign
     *  Save post to Firestore with given info
     *  return in databaseCallback once finished
     */
    public void createPost(DatabaseCallback databaseCallback, Post post) {
        String id = java.util.UUID.randomUUID().toString(); //generate unique id
        CollectionReference collectionReference = db.collection("Post");
        collectionReference
                .whereEqualTo("id",id)
                .get()
                .addOnCompleteListener((OnCompleteListener<QuerySnapshot>) runningTask -> {
            if (runningTask.isSuccessful()) {
                if (runningTask.getResult().isEmpty()){ //if id is unique
                    post.setId(id);
                    collectionReference.document(id).set(post);
                    databaseCallback.successlistener(true);
                } else {
                    databaseCallback.successlistener(false);
                }
            }
        });
    }

    /**
     * Fetch current userdata based on macineCode of current phone
     * Add the returned current user to a temporary list as return in callback function
     * @param databaseCallback call back class that runs once Firestore replies
     * @param machineCode A String stands for current phone's id to fetch current user
     */
    public void getCurrentUser(DatabaseCallback databaseCallback, String machineCode){
        List<Object> temp = new ArrayList<Object>();
        Log.e("DBController :", "MachineCode = "+machineCode);
        if (currentUser == null) {
            db.collection("User").whereEqualTo("machineCode", machineCode).get()
                    .addOnCompleteListener((OnCompleteListener<QuerySnapshot>) runningTask-> {


                        if (runningTask.isSuccessful()) {
                            for (QueryDocumentSnapshot document : runningTask.getResult()) //fetch user from db
                                temp.add(document.toObject(User.class));
                            currentUser = (User)temp.get(0);
                            databaseCallback.run(temp);
                        }
                    });
//            Log.e("DBController :", "MachineCode = "+temp.toString());
        } else {
            temp.add(currentUser);
            databaseCallback.run(temp);
        }
    }

    /**
     * TODO:
     *  return a user based on username, return in databaseCallback
     *  Make sure get password as well!
     *
     */
    public void  getUser(DatabaseCallback databaseCallback, List<Object> result, String username) {

    }

    /**
     * Return a post based on post id, return in databaseCallback
     */
    public void getPost(DatabaseCallback databaseCallback, String id) {

    }

    /**
     * Return a list of post based on username, return in databaseCallback
     */
    public void getPosts(DatabaseCallback databaseCallback, String username) {

    }

    /**
     * Mostly for admin method or by user's request; Delete user data
     */
    public void deleteUser(DatabaseCallback databaseCallback, String username) {
        deleteData(databaseCallback, "User", username);
    }

    /**
     * Delete original user's machine code and update machine code from a new device
     * @param databaseCallback Callback class once Firestore replies
     * @param username a String value stands for username to search user for
     */
    public void logoutUser(DatabaseCallback databaseCallback, String username, String newMachineCode) {
        updateData(databaseCallback, "User", "username", username, "machineCode", newMachineCode);
    }

    /**
     * Delete a post with given post id
     */
    public  void deletePost(DatabaseCallback databaseCallback, String postid) {
        deleteData(databaseCallback, "Post", postid);
    }

    /**
     * Update a data record based on given identifier field and update field
     * @param databaseCallback callback class once firestore replied
     * @param objectType a string represents which type of data collection record belongs to
     * @param identifierField a string for indexing the target record
     * @param identifierValue the value to match the indexing field
     * @param updateField the field to be updated for target record
     * @param updateValue the value to be updated for target record's update field
     */
    public void updateData (DatabaseCallback databaseCallback,String objectType , String identifierField, String identifierValue, String updateField, Object updateValue){
        CollectionReference collectionReference = db.collection(objectType);
        List<Map> temp = new ArrayList<Map>();
        Query task = collectionReference.whereEqualTo(identifierField, identifierValue);
        task.get().addOnCompleteListener((OnCompleteListener<QuerySnapshot>) runningTask-> {
            boolean success = true;
            if (runningTask.isSuccessful()) {
                for (QueryDocumentSnapshot document: runningTask.getResult())
                    temp.add(document.getData());
                if (!temp.isEmpty()) { //The record needs to be updated is found
                    collectionReference.document(identifierValue).update(updateField, updateValue);
                }
                databaseCallback.successlistener(success);
            }
        });

    }

    public void deleteData(DatabaseCallback databaseCallback, String objectType, String identifierValue) {

        CollectionReference collectionReference = db.collection(objectType);
        Log.e("DatabaseController: ",identifierValue);
        collectionReference.document(identifierValue).delete().addOnCompleteListener((OnCompleteListener<Void>) runningTask-> {

            databaseCallback.successlistener(runningTask.isSuccessful());
        });
    }
}