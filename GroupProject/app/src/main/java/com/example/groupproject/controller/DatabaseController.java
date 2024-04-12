package com.example.groupproject.controller;

import android.provider.ContactsContract;
import android.util.Log;

import com.example.groupproject.model.Location;
import com.example.groupproject.model.Post;
import com.example.groupproject.model.User;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.firebase.firestore.CollectionReference;

import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldPath;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.annotation.Nullable;

public class DatabaseController {
    private static User currentUser;
    private static DatabaseController dbInstance;
    final private FirebaseFirestore db;

    private DatabaseController(){
        db = FirebaseFirestore.getInstance();
    }

    public static DatabaseController getInstance() {
        if (dbInstance == null)
            dbInstance = new DatabaseController();

        return dbInstance;
    }

    public void createLocation (DatabaseCallback databaseCallback, Location newlocation) {
        CollectionReference collectionReference = db.collection("Location");
        List<Object> temp = new ArrayList<Object>();
        String location = newlocation.getLatitude()+","+newlocation.getLongitude();
        collectionReference.whereEqualTo("location",location )
                .get().addOnCompleteListener((OnCompleteListener<QuerySnapshot>) task -> {
                    if (task.isSuccessful()) {
                       collectionReference.document(location).set(newlocation); //update Location
                       databaseCallback.successlistener(true);
                   }
                    databaseCallback.successlistener(false);
                });
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


    public void createPost(DatabaseCallback databaseCallback, Post post) {
        String id = post.getId(); //generate unique id
        CollectionReference collectionReference = db.collection("Post");
        collectionReference
                .whereEqualTo("id",id)
                .get()
                .addOnCompleteListener((OnCompleteListener<QuerySnapshot>) runningTask -> {
            if (runningTask.isSuccessful()) {
                if (runningTask.getResult().isEmpty()){ //if id is unique
                    collectionReference.document(id).set(post);
                    databaseCallback.successlistener(true);
                } else {
                    databaseCallback.successlistener(false);
                }
            }
        });
    }

    /**
     * Fetch current userdata based on machineCode of current phone
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

    public User getCurrentUser() {
        if (currentUser != null) {

            return currentUser;
        }
        return null;
    }

    /**
     * Get one or all users
     * @param databaseCallback callback class that runs once firestore replies
     * @param flag true for fetching all users false for one user
     * @param username the username is the key to find the specific user null if fetching all users
     */
    public void getUser(DatabaseCallback databaseCallback, boolean flag, @Nullable String username) {
        getData(databaseCallback,"User", username, flag);
    }

    /**
     * Return a post based on post id, return in databaseCallback
     */
    public void getPost(DatabaseCallback databaseCallback, String id) {
        getData(databaseCallback,"Post",id);
    }

    /**
     * Return a list of post based on username, return in databaseCallback
     */
    public void getPosts(DatabaseCallback databaseCallback, String username) {
        List<Object> temp = new ArrayList<Object>();
        CollectionReference userCollectionReference = db.collection("User");
        userCollectionReference.document(username).get().addOnCompleteListener((OnCompleteListener<DocumentSnapshot>) runningTask-> {
            if (runningTask.isSuccessful()) {
                User user = runningTask.getResult().toObject(User.class);

                CollectionReference postCollectionReference = db.collection("Post");
                if (user != null){ //if user if found, retrieve all posts belonged to this user:
                    postCollectionReference.whereIn(FieldPath.documentId(), user.getPostList())
                            .get()
                            .addOnCompleteListener( (OnCompleteListener<QuerySnapshot>) subtask-> {
                                if (subtask.isSuccessful()) {
                                    for (QueryDocumentSnapshot document : subtask.getResult()) {
                                        temp.add(document.toObject(Post.class));
                                    }
                                }
                                databaseCallback.run(temp);
                            });
                }
            }
        });
    }
    
    public void getLocations (DatabaseCallback databaseCallback, String location){
        getData(databaseCallback,"Location",location,true);
    }

    /**
     * Mostly for admin method or by user's request; Delete user data
     */
    public void deleteUser(DatabaseCallback databaseCallback, String username) {
        deleteData(databaseCallback, "User", username);
    }

    /**
     * Delete a post with given post id
     */
    public  void deletePost(DatabaseCallback databaseCallback, String postid) {
        deleteData(databaseCallback, "Post", postid);
    }

    public void updateUser(DatabaseCallback databaseCallback, String username,  User user) {
        updateData(databaseCallback, "User", "username", username, user);
    }

    /**
     * Update a data record based on given identifier field and update field
     * @param databaseCallback callback class once firestore replied
     * @param objectType a string represents which type of data collection record belongs to
     * @param identifierField a string for indexing the target record
     * @param identifierValue the value to match the indexing field
     * @param updateValue the value to be updated for target record's update field
     */
    public void updateData (DatabaseCallback databaseCallback,String objectType , String identifierField, String identifierValue, Object updateValue){
        CollectionReference collectionReference = db.collection(objectType);
        List<Map> temp = new ArrayList<Map>();
        Query task = collectionReference.whereEqualTo(identifierField, identifierValue);
        task.get().addOnCompleteListener((OnCompleteListener<QuerySnapshot>) runningTask-> {
            boolean success = true;
            if (runningTask.isSuccessful()) {
                for (QueryDocumentSnapshot document: runningTask.getResult())
                    temp.add(document.getData());
                if (!temp.isEmpty()) { //The record needs to be updated is found
                    collectionReference.document(identifierValue).set(updateValue);
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

    /**
     * Get one/multiple records
     * @param databaseCallback callback class runs once Firestore replies
     * @param objectType A string value to query correct collection
     * @param identifierValue A string value to retrieve record
     * @param all flag to determine if fetching all records
     */
    public void getData (DatabaseCallback databaseCallback,String objectType, @Nullable String identifierValue, boolean all) {
        CollectionReference collectionReference = db.collection(objectType);
        List<Object> temp = new ArrayList<Object>();
        if (!all && identifierValue != null ) {
            collectionReference.document(identifierValue).get()
                    .addOnCompleteListener((OnCompleteListener<DocumentSnapshot>) runningTask-> {
                if (runningTask.isSuccessful()) {
                    if (objectType.equals("User")){
                        temp.add(runningTask.getResult().toObject(User.class));
                    } else if (objectType.equals("Location")) {
                            temp.add(runningTask.getResult().toObject(Location.class));
                    } else if (objectType.equals("Post")) {
                        temp.add(runningTask.getResult().toObject(Post.class));
                    }
                    databaseCallback.run(temp);
                }
            });
        } else { //fetch all users/locations/posts
            collectionReference.get().addOnCompleteListener((OnCompleteListener<QuerySnapshot>) runningTask-> {
                if (runningTask.isSuccessful()) {
                    for (DocumentSnapshot document : runningTask.getResult()) {
                        if (objectType.equals("User")){
                            temp.add(document.toObject(User.class));
                        } else if (objectType.equals("Location")) {
                            temp.add(document.toObject(Location.class));
                        } else if (objectType.equals("Post")) {
                            temp.add(document.toObject(Post.class));
                        }
                        databaseCallback.run(temp);
                    }
                }
            });
        }
    }

    /**
     * Get one record from Firestore
     * @param databaseCallback callback class runs once Firestore replies
     * @param objectType A string stands for type of data
     * @param identifierValue The id of a record
     */
    public void getData(DatabaseCallback databaseCallback, String objectType, String identifierValue) {
        getData(databaseCallback, objectType, identifierValue, false);
    }
}