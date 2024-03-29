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
     * TODO:
     *  Ask firestore to save user with given info
     *  Check datatype
     *  Check given username is unique or not
     *  If successful, return true; false otherwise
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
                    collectionReference.document(machineCode).set(newuser);
                } else {
                    //if success is false, maybe the user is login from a new device, logout required in callback
                    success = false;
                }
                    databaseCallback.successlistener(success);
            }
        });
    }

    /**
     * Update Location, Post and User with given field and identifier
     * For updating user, give machineCode and update field then update value
     * For Location, give location index and update field and update value
     * For Post update, give post id and given field then value
     * @param object: which object updating to : User/Location/Post
     * @param databaseCallback : Callback made from Activity or Controller that asks for update in database
     * @param updateField: which field needs to be updated
     * @param updateValue: what value to be assigned to
     */
    public void updateData (DatabaseCallback databaseCallback,String object , String identifierField, String identifierValue, String updateField, Object updateValue){
        CollectionReference collectionReference = db.collection(object);
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
     * TODO:
     *  Update a given post with post ID
     *  return in databaseCallback once it's finished
     * @param databaseCallback
     * @param postId
     * @param post
     */
    public void udpatePost(DatabaseCallback databaseCallback, String postId, Post post) {

    }

    /**
     * TODO:
     *  return current ser in databaseCallback check if current user has already loaded or not
     *  if loaded return current user, otherwise ask firestore with user's machine code
     */
    public void getCurrentUser(DatabaseCallback databaseCallback, String machineCode){
        List<Object> temp = new ArrayList<Object>();
        Log.e("DBController :", "MachineCode = "+machineCode);
        if (currentUser == null) {
            db.collection("User").whereEqualTo("username", "firstuser1").get()
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
     *
     * TODO: return a user based on username, return in databaseCallback
     *  This method should not touch machine code!
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
    public void deleteUser(DatabaseCallback databaseCallback, String machineCode) {

    }

    /**
     * Delete original user's machine code, update it with new code
     * @param databaseCallback
     * @param username
     */
    public void logoutUser(DatabaseCallback databaseCallback, String username) {

    }

    /**
     * Delete a post with given post id
     */
    public  void deletePost(DatabaseCallback databaseCallback, String postid) {

    }
}