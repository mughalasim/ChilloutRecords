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
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;

import static com.chilloutrecords.utils.StaticVariables.FIREBASE_AUTH;
import static com.chilloutrecords.utils.StaticVariables.FIREBASE_AUTH_STATE_LISTENER;
import static com.chilloutrecords.utils.StaticVariables.FIREBASE_DB;
import static com.chilloutrecords.utils.StaticVariables.FIREBASE_USER;

public class Database {

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



    // GET USER ID AND LOGIN TO APP
    public static void getUserIdAndLogin(Activity context) {
        FIREBASE_USER = FIREBASE_AUTH.getCurrentUser();
        if (FIREBASE_USER != null) {
            StaticMethods.logg("FETCH USER", FIREBASE_USER.getUid());

            FIREBASE_AUTH_STATE_LISTENER = new FirebaseAuth.AuthStateListener() {
                @Override
                public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                    FirebaseUser user = firebaseAuth.getCurrentUser();
                    if (user == null) {
                        StaticMethods.broadcastLogout(true);
                    }
                }
            };

            FIREBASE_AUTH.addAuthStateListener(FIREBASE_AUTH_STATE_LISTENER);

            context.startActivity(new Intent(context, ParentActivity.class));

        } else {
            context.startActivity(new Intent(context, StartUpActivity.class));
        }
        context.finish();
    }
}