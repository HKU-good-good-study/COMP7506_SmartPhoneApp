package com.example.groupproject.controller;

import android.location.Address;
import android.location.Geocoder;
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

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
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
                            if (!temp.isEmpty())
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

    public void deleteCurrentUser() {
        currentUser = null;
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
     * if username is null return all posts
     */
    public void getPosts(DatabaseCallback databaseCallback, String username) {
        List<Object> temp = new ArrayList<Object>();
        CollectionReference userCollectionReference = db.collection("User");
        CollectionReference postCollectionReference = db.collection("Post");
        if (username != null) {
            userCollectionReference.document(username).get().addOnCompleteListener((OnCompleteListener<DocumentSnapshot>) runningTask -> {
                if (runningTask.isSuccessful()) {
                    User user = runningTask.getResult().toObject(User.class);
                    if (user != null && !user.getPostList().isEmpty()) { //if user if found, retrieve all posts belonged to this user:
                        postCollectionReference.whereIn(FieldPath.documentId(), user.getPostList())
                                .get()
                                .addOnCompleteListener((OnCompleteListener<QuerySnapshot>) subtask -> {
                                    if (subtask.isSuccessful()) {
                                        for (QueryDocumentSnapshot document : subtask.getResult()) {
                                            temp.add(document.toObject(Post.class));
                                        }
                                    }
                                    databaseCallback.run(temp);
                                });
                    } else if (user!= null){
                        databaseCallback.run(temp);
                    }
                }
            });
        } else {
            postCollectionReference.get().addOnCompleteListener((OnCompleteListener<QuerySnapshot>) runningTask -> {
                if( runningTask.isSuccessful()) {
                    for (QueryDocumentSnapshot document : runningTask.getResult()) {
                        temp.add(document.toObject(Post.class));
                    }
                }
                databaseCallback.run(temp);
            });
        }
    }

    /**
     * Get all posts
     * @param databaseCallback
     */
    public void getPosts(DatabaseCallback databaseCallback){
        getPosts(databaseCallback, null);
    }

    public void getLocation (DatabaseCallback databaseCallback, String location) {
        getData(databaseCallback,"Location",location,false);
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

    public void updateLocation(DatabaseCallback databaseCallback, Location location) {
        updateData(databaseCallback, "Location", "location", location.getLatitude() + ","+location.getLongitude(), location);
    }

    /**
     * Add a post to a Location class on firestore
     * @param databaseCallback
     * @param location A String in format "Latitude,Longitude"
     * @param postid post newly created
     */
    public void editPostToLocation (DatabaseCallback databaseCallback, String location, String postid, boolean add) {
        CollectionReference collectionReference = db.collection("Location");

        collectionReference.document(location).get().addOnCompleteListener((OnCompleteListener<DocumentSnapshot>) task -> {
            if (task.isSuccessful() && task.getResult()!=null) {
                Location currentLocation = task.getResult().toObject(Location.class);
                if (add)
                    currentLocation.addPost(postid);
                else
                    currentLocation.deletePost(postid);

                updateLocation(databaseCallback, currentLocation);
            } else { // if record not found, create a new location with current postid
                Geocoder geocoder = new Geocoder(databaseCallback.getContext(), Locale.getDefault());
                Double lati = Double.parseDouble(location.split(",")[1]);
                Double longit = Double.parseDouble(location.split(",")[0]);
                ArrayList<String> posts = new ArrayList<String>() ;
                posts.add(postid);
                try {
                    List<Address> address = geocoder.getFromLocation(lati,longit,1);
                    createLocation(databaseCallback,new Location(
                            location.split(",")[1],
                            location.split(",")[0],
                            address.get(0).getLocality(),
                            posts
                            ));
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }
        });

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

    public void updatePieceData(DatabaseCallback databaseCallback, String objectType, String identifierField, String identifierValue, String updateField, String updateValue) {
        CollectionReference collectionReference = db.collection(objectType);
        List<Map> temp = new ArrayList<Map>();
        Query task = collectionReference.whereEqualTo(identifierField, identifierValue);
        task.get().addOnCompleteListener((OnCompleteListener<QuerySnapshot>) runningTask-> {
            boolean success = false;
            if (runningTask.isSuccessful()) {
                for (QueryDocumentSnapshot document: runningTask.getResult())
                    temp.add(document.getData());
                if (!temp.isEmpty()) { //The record needs to be updated is found
                    collectionReference.document(identifierValue).update(updateField, updateValue);
                    success = true;
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
                            databaseCallback.successlistener(true);
                        } else {
                            databaseCallback.successlistener(false);
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
                    databaseCallback.successlistener(true);
                } else {
                    databaseCallback.successlistener(false);
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


    /**
     * This method is used to search data in the Firestore database.
     *
     * @param databaseCallback This is a callback interface which contains methods that will be called once the Firestore operation is completed.
     * @param objectType This is a string that represents the type of data to be searched. It corresponds to the collection name in Firestore.
     * @param searchField This is the field name in the Firestore document that we want to match.
     * @param searchValue This is the value that we want to match in the searchField.
     * <br><br>
     * The method performs a search operation in the Firestore database. It creates a query to search for documents where the searchField equals the searchValue in the specified objectType collection.
     * If the operation is successful, it adds the fetched documents to a list and calls the run method of the databaseCallback with this list.
     * It also calls the <code>successlistener</code> method of the <code>databaseCallback</code> with a value of true.
     * If the operation is not successful, it calls the <code>successlistener</code> method of the <code>databaseCallback</code> with a value of false.
     */
    public void searchData(DatabaseCallback databaseCallback, String objectType, String searchField, String searchValue) {
        CollectionReference collectionReference = db.collection(objectType);
        List<Object> temp = new ArrayList<>();
        Query task = collectionReference.whereEqualTo(searchField, searchValue);
        task.get().addOnCompleteListener(runningTask-> {
            if (runningTask.isSuccessful()) {
                for (QueryDocumentSnapshot document: runningTask.getResult()) {
                    if (objectType.equals("User"))
                        temp.add(document.toObject(User.class));
                    else if (objectType.equals("Location"))
                        temp.add(document.toObject(Location.class));
                    else if (objectType.equals("Post"))
                        temp.add(document.toObject(Post.class));
                }
                databaseCallback.run(temp);
                databaseCallback.successlistener(true);
            } else {
                databaseCallback.successlistener(false);
            }
        });
    }

    /**
     * This method is used to search for a user in the Firestore database.
     *
     * @param databaseCallback This is a callback interface that contains methods that will be called once the Firestore operation is completed.
     * @param searchValue This is the username of the user that we want to search for.
     * <br><br>
     * The method calls the searchData method with the <code>objectType</code> set to "User" and the <code>searchField</code> set to "username".
     * This means that it will search for a user whose username exactly matches the provided searchValue in the Firestore database.
     */
    public void searchUser(DatabaseCallback databaseCallback, String searchValue) {
        searchData(databaseCallback, "User", "username", searchValue);
    }
}