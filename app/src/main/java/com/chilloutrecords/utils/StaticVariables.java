package com.chilloutrecords.utils;

import com.chilloutrecords.models.UserModel;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.FirebaseDatabase;

public class StaticVariables {
    // FIREBASE VARIABLES ==========================================================================
    public static FirebaseAuth FIREBASE_AUTH;
    public static FirebaseUser FIREBASE_USER;
    public static FirebaseDatabase FIREBASE_DB;

    // USER MODEL ==================================================================================
    public static UserModel USER;

    // ANIMATION TIME ==============================================================================
    public static int INT_ANIMATION_TIME = 1000;

    // FIREBASE ====================================================================================
    public static final String FIREBASE_MESSAGING_TOKEN = "FIREBASE_MESSAGING_TOKEN";
    public static final String BOOL_FIREBASE_REGISTERED = "BOOL_FIREBASE_REGISTERED";

    // BUNDLE EXTRA VARIABLES ======================================================================
    public static final String EXTRA_STRING = "EXTRA_STRING";
    public static final String EXTRA_TRUE = "TRUE";
    public static final String EXTRA_FALSE = "FALSE";

    // BROADCAST VARIABLES =========================================================================
    public static final String BROADCAST_LOG_OUT = "BROADCAST_LOG_OUT";

    // GLOBAL STATIC VARIABLES======================================================================
    public static final String CHANNEL_ID = "CHANNEL_ID";
    public static final String CHANNEL_NAME = "CHANNEL_NAME";
    public static final String CHANNEL_DESC = "CHANNEL_DESC";
}
