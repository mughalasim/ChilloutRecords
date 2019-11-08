package com.chilloutrecords.utils;

import android.app.Activity;
import android.content.Intent;

import androidx.annotation.NonNull;

import com.chilloutrecords.BuildConfig;
import com.chilloutrecords.activities.ParentActivity;
import com.chilloutrecords.activities.StartUpActivity;
import com.chilloutrecords.interfaces.GeneralInterface;
import com.chilloutrecords.models.UserModel;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;

import static com.chilloutrecords.utils.StaticVariables.FIREBASE_AUTH;
import static com.chilloutrecords.utils.StaticVariables.FIREBASE_DB;
import static com.chilloutrecords.utils.StaticVariables.FIREBASE_USER;
import static com.chilloutrecords.utils.StaticVariables.USER;

public class Database {

    // SET / UPDATE USER INFO ======================================================================
    public static void setUser(UserModel user, final GeneralInterface listener) {
        DatabaseReference reference = FIREBASE_DB.getReference(BuildConfig.DB_REF_USERS);
        reference.child(user.id).setValue(user).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()) {
                    listener.success();
                } else {
                    listener.failed();
                }
            }
        });
    }

    // GET USER MODEL FROM FIREBASE ================================================================
    private static void getUser() {
        DatabaseReference reference = FIREBASE_DB.getReference(BuildConfig.DB_REF_USERS);
        reference.child(FIREBASE_USER.getUid()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                USER = dataSnapshot.getValue(UserModel.class);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    // CHECK IF USER IS AUTHENTICATED ==============================================================
    public static void getUserIdAndLogin(Activity context) {
        FIREBASE_USER = FIREBASE_AUTH.getCurrentUser();
        if (FIREBASE_USER != null) {
            StaticMethods.logg("FETCH USER", FIREBASE_USER.getUid());
            getUser();
            context.startActivity(new Intent(context, ParentActivity.class));
//            context.startActivity(new Intent(context, StartUpActivity.class));
        } else {
            context.startActivity(new Intent(context, StartUpActivity.class));
        }
        context.finish();
    }
}