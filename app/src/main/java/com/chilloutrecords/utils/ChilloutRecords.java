package com.chilloutrecords.utils;

import android.annotation.SuppressLint;
import android.app.Application;
import android.content.Context;

import androidx.appcompat.app.AppCompatDelegate;

import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;

import java.util.List;

import static com.chilloutrecords.BuildConfig.FB_API_KEY;
import static com.chilloutrecords.BuildConfig.FB_APP_ID;
import static com.chilloutrecords.BuildConfig.FB_DB_URL;
import static com.chilloutrecords.BuildConfig.FB_STORE;
import static com.chilloutrecords.utils.StaticVariables.FIREBASE_AUTH;
import static com.chilloutrecords.utils.StaticVariables.FIREBASE_DB;
import static com.chilloutrecords.utils.StaticVariables.FIREBASE_STORAGE;

public class ChilloutRecords extends Application {

    @SuppressLint("StaticFieldLeak")
    private static Context context;

    public void onCreate() {
        super.onCreate();
        ChilloutRecords.context = getApplicationContext();

        AppCompatDelegate.setCompatVectorFromResourcesEnabled(true);

        // Initialise firebase
        initFireBase();

    }

    public static Context getAppContext() {
        return ChilloutRecords.context;
    }

    public static void initFireBase() {
        FirebaseOptions builder = new FirebaseOptions.Builder()
                .setApplicationId(FB_APP_ID)
                .setApiKey(FB_API_KEY)
                .setDatabaseUrl(FB_DB_URL)
                .setStorageBucket(FB_STORE)
                .build();

        List<FirebaseApp> fire_base_app_list = FirebaseApp.getApps(ChilloutRecords.getAppContext());

        if (fire_base_app_list.size() < 1) {
            FirebaseApp.initializeApp(getAppContext(), builder);
        }

        // Initialize Firebase Auth
        FIREBASE_AUTH = FirebaseAuth.getInstance();

        // Initialize Firebase DB
        FIREBASE_DB = FirebaseDatabase.getInstance();

        // Initialize Firebase Storage
        FIREBASE_STORAGE = FirebaseStorage.getInstance();

    }

}
