package com.chilloutrecords.services;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;

import androidx.annotation.NonNull;

import com.chilloutrecords.utils.StaticMethods;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.GetTokenResult;

import java.util.Objects;

import static com.chilloutrecords.utils.StaticVariables.FIREBASE_AUTH;

public class LoginStateService extends Service {
    private static final String TAG_LOG = "FB SERVICE";

    // BASIC OVERRIDE METHODS ======================================================================
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        StaticMethods.logg(TAG_LOG, "ON_START");
        return START_NOT_STICKY;
    }

    @Override
    public void onCreate() {
        if (FIREBASE_AUTH != null) {
            Objects.requireNonNull(FIREBASE_AUTH.getCurrentUser()).getIdToken(true).addOnCompleteListener(new OnCompleteListener<GetTokenResult>() {
                @Override
                public void onComplete(@NonNull Task<GetTokenResult> task) {
                    if (!task.isSuccessful()) {
                        StaticMethods.logg(TAG_LOG, "FAILED TO GET TOKEN - LOGOUT USER");
                        stopSelf();
                        StaticMethods.logOutUser(true);
                    }
                }
            });
        } else {
            StaticMethods.logg(TAG_LOG, "NULL FIREBASE USER - LOGOUT USER");
            stopSelf();
            StaticMethods.logOutUser(true);
        }
    }

    @Override
    public void onTaskRemoved(Intent rootIntent) {
        StaticMethods.logg(TAG_LOG, "DESTROYED");
        stopSelf();
    }

}
