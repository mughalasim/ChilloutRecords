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
import static com.chilloutrecords.utils.StaticVariables.USER_MODEL;

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

    // GET USER INFO ===============================================================================
    public static void getUser(final GeneralInterface listener) {
        FIREBASE_DB.getReference(BuildConfig.DB_REF_USERS).child(FIREBASE_USER.getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                final UserModel model = dataSnapshot.getValue(UserModel.class);
                if (model != null) {
                    USER_MODEL = model;
                    listener.success();
                } else {
                    listener.failed();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Database.handleDatabaseError(databaseError);
                listener.failed();
            }
        });
    }

    // CHECK IF USER IS AUTHENTICATED ==============================================================
    public static Boolean isUserLoggedIn() {
        FIREBASE_USER = FIREBASE_AUTH.getCurrentUser();
        if (FIREBASE_USER != null) {
            StaticMethods.logg("FETCH USER", FIREBASE_USER.getUid());
            return true;
        } else {
            return false;
        }
    }

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
        if (code == DatabaseError.DISCONNECTED || code == DatabaseError.NETWORK_ERROR || code == DatabaseError.UNAVAILABLE) {
            StaticMethods.showToast("Network error");
        } else if (code == DatabaseError.PERMISSION_DENIED || code == DatabaseError.EXPIRED_TOKEN || code == DatabaseError.INVALID_TOKEN) {
            StaticMethods.logOutUser(true);
        }
    }

    // STORAGE FILE URL ============================================================================
    public static void getFileUrl(final String path, final String file_name, final String default_file, final UrlInterface listener) {
        try {
            StorageReference file_reference = FIREBASE_STORAGE.getReference().child(path).child(file_name);
            file_reference.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                @Override
                public void onSuccess(Uri uri) {
                    listener.completed(true, uri.toString());
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception exception) {
                    if (default_file != null && !default_file.equals("")) {
                        try {
                            StorageReference file_reference = FIREBASE_STORAGE.getReference().child(path).child(default_file);
                            file_reference.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                @Override
                                public void onSuccess(Uri uri) {
                                    listener.completed(true, uri.toString());
                                }
                            }).addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception exception) {
                                    listener.completed(false, "");
                                }
                            });
                        } catch (Exception f) {
                            StaticMethods.logg("STATIC METHOD", "File not found");
                            listener.completed(false, "");
                        }
                    } else {
                        StaticMethods.logg("STATIC METHOD", "File not found");
                        listener.completed(false, "");
                    }
                }
            });
        } catch (Exception e) {
            if (default_file != null && !default_file.equals("")) {
                try {
                    StorageReference file_reference = FIREBASE_STORAGE.getReference().child(path).child(default_file);
                    file_reference.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                        @Override
                        public void onSuccess(Uri uri) {
                            listener.completed(true, uri.toString());
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception exception) {
                            listener.completed(false, "");
                        }
                    });
                } catch (Exception f) {
                    StaticMethods.logg("STATIC METHOD", "File not found");
                    listener.completed(false, "");
                }
            } else {
                StaticMethods.logg("STATIC METHOD", "File not found");
                listener.completed(false, "");
            }
        }

    }

    // UPDATE TRACK PLAY COUNT =====================================================================
    public static void updateTrackPlayCount(String path, int current_play_count) {
        current_play_count++;
        DatabaseReference reference = FIREBASE_DB.getReference();
        reference.child(path).child("play_count").setValue(current_play_count).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()) {
                    StaticMethods.logg("Database", "Updated play count");
                } else {
                    StaticMethods.logg("Database", "Failed to update play count");
                }
            }
        });
    }

}