package com.chilloutrecords.utils;

import com.chilloutrecords.models.TrackModel;
import com.chilloutrecords.models.UserModel;
import com.chilloutrecords.models.VideoModel;
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

    // STATIC MODELS ===============================================================================
    public static UserModel USER_MODEL = new UserModel();
    public static TrackModel TRACK_MODEL = new TrackModel();
    public static VideoModel VIDEO_MODEL = new VideoModel();

    // TRACK COLLECTION ID =========================================================================
    public static String STR_COLLECTION_ID = "";

    // ANIMATION TIME ==============================================================================
    public static int INT_ANIMATION_TIME = 800;

    // PERMISSION VARIABLES ========================================================================
    public final static int INT_PERMISSIONS_CAMERA = 601;
    public final static int INT_PERMISSIONS_STORAGE = 604;

    // BUNDLE EXTRA VARIABLES ======================================================================
    public static final String EXTRA_STRING = "EXTRA_STRING";
    public static final String EXTRA_DATA = "EXTRA_DATA";
    public static final String EXTRA_VIDEO = "EXTRA_VIDEO";
    public static final String EXTRA_TRACK_SINGLE = "EXTRA_TRACK_SINGLE";
    public static final String EXTRA_TRACK_COLLECTION = "EXTRA_TRACK_COLLECTION";

}
