package com.chilloutrecords.utils;

import com.chilloutrecords.models.UserModel;
import com.google.firebase.database.DatabaseReference;

import static com.chilloutrecords.utils.StaticVariables.FIREBASE_DB;

public class Database {

    public static void updateUser(UserModel user){
        DatabaseReference reference = FIREBASE_DB.getReference("Users/" + user.id + "/");
        reference.setValue(user);
    }
}