package com.chilloutrecords.utils;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;

import androidx.annotation.NonNull;

import com.chilloutrecords.BuildConfig;
import com.chilloutrecords.activities.ParentActivity;
import com.chilloutrecords.activities.StartUpActivity;
import com.chilloutrecords.interfaces.GeneralInterface;
import com.chilloutrecords.interfaces.UrlInterface;
import com.chilloutrecords.models.UserModel;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.StorageReference;

import static com.chilloutrecords.utils.StaticVariables.FIREBASE_AUTH;
import static com.chilloutrecords.utils.StaticVariables.FIREBASE_DB;
import static com.chilloutrecords.utils.StaticVariables.FIREBASE_STORAGE;
import static com.chilloutrecords.utils.StaticVariables.FIREBASE_USER;

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

    // CHECK IF USER IS AUTHENTICATED ==============================================================
    public static void getUserIdAndLogin(Activity context) {
        FIREBASE_USER = FIREBASE_AUTH.getCurrentUser();
        if (FIREBASE_USER != null) {
            StaticMethods.logg("FETCH USER", FIREBASE_USER.getUid());
            context.startActivity(new Intent(context, ParentActivity.class));
        } else {
            context.startActivity(new Intent(context, StartUpActivity.class));
        }
        context.finish();
    }

    // HANDLE ERROR ================================================================================
    public static void handleDatabaseError(DatabaseError error) {
        int code = error.getCode();
        if (code == DatabaseError.DISCONNECTED || code == DatabaseError.NETWORK_ERROR || code == DatabaseError.UNAVAILABLE ) {
            // TODO - Start network error activity
            StaticMethods.showToast("Network error");
        } else if (code == DatabaseError.PERMISSION_DENIED || code == DatabaseError.EXPIRED_TOKEN || code == DatabaseError.INVALID_TOKEN  ) {
            StaticMethods.logOutUser(true);
        }
    }

    // STORAGE FILE URL ============================================================================
    public static void getFileUrl(String path, String file_name, final UrlInterface listener) {
        StorageReference file_reference = FIREBASE_STORAGE.getReference().child(path).child(file_name);
        try {
            file_reference.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                @Override
                public void onSuccess(Uri uri) {
                    listener.success(uri.toString());
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception exception) {
                    listener.failed();
                }
            });
        } catch (Exception e) {
            StaticMethods.logg("STATIC METHOD", "File not found");
            listener.failed();
        }


    }

}