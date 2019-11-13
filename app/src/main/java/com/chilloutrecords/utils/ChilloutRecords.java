package com.chilloutrecords.utils;

import android.app.Application;
import android.content.Context;

import androidx.appcompat.app.AppCompatDelegate;

import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;

import java.util.List;

import com.chilloutrecords.BuildConfig;
import com.chilloutrecords.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;

import static com.chilloutrecords.utils.StaticVariables.FIREBASE_AUTH;
import static com.chilloutrecords.utils.StaticVariables.FIREBASE_DB;
import static com.chilloutrecords.utils.StaticVariables.FIREBASE_STORAGE;

public class ChilloutRecords extends Application {

    private static Context context;

    public void onCreate() {
        super.onCreate();
        ChilloutRecords.context = getApplicationContext();

        AppCompatDelegate.setCompatVectorFromResourcesEnabled(true);

        initFireBase();

    }

    public static Context getAppContext() {
        return ChilloutRecords.context;
    }

    public static void initFireBase() {
        FirebaseOptions builder = new FirebaseOptions.Builder()
                .setApplicationId(BuildConfig.FB_APP_ID)
                .setApiKey(getAppContext().getString(R.string.FB_API_KEY))
                .setDatabaseUrl(BuildConfig.FB_DB_URL)
                .setStorageBucket(BuildConfig.FB_STORE)
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
