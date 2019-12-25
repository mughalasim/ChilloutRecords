package com.chilloutrecords.utils;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;

public class StaticVariables {
    // FIREBASE VARIABLES ==========================================================================
    public static FirebaseAuth FIREBASE_AUTH;
    public static FirebaseUser FIREBASE_USER;
    public static FirebaseDatabase FIREBASE_DB;
    public static FirebaseStorage FIREBASE_STORAGE;

    // ANIMATION TIME ==============================================================================
    public static int INT_ANIMATION_TIME = 1000;

    // BUNDLE EXTRA VARIABLES ======================================================================
    public static final String EXTRA_STRING = "EXTRA_STRING";
    public static final String EXTRA_DATA = "EXTRA_DATA";
    public static final String EXTRA_VIDEO = "EXTRA_VIDEO";
    public static final String EXTRA_TRACK_SINGLE = "EXTRA_TRACK_SINGLE";
    public static final String EXTRA_TRACK_COLLECTION = "EXTRA_TRACK_COLLECTION";

    // IMAGE FOR IMAGE_FRAGMENT ====================================================================
    public static String STR_IMAGE_URL = "";

}
