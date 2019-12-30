package com.chilloutrecords.utils;

import android.app.Activity;
import android.content.SharedPreferences;

import androidx.annotation.NonNull;

import java.util.ArrayList;
import java.util.HashSet;


public class SharedPrefs {
    private static final String SHARED_PREFS = "SHARED_PREFS";
    private static final int MODE = Activity.MODE_PRIVATE;

    private static SharedPreferences prefs = ChilloutRecords.getAppContext().getSharedPreferences(SHARED_PREFS, MODE);
    private static SharedPreferences.Editor editor = ChilloutRecords.getAppContext().getSharedPreferences(SHARED_PREFS, MODE).edit();


    // GENERIC GET AND SET INTEGER VARIABLES =======================================================
    public static int getInt(String name) {
        return prefs.getInt(name, 0);
    }

    public static void setInt(String name, int value) {
        editor.putInt(name, value).apply();
    }


    // GENERIC GET AND SET STRING VARIABLES ========================================================
    public static String getString(String name) {
        return prefs.getString(name, "");
    }

    public static void setString(String name, String value) {
        editor.putString(name, value).apply();
    }


    // GENERIC GET AND SET ARRAY LIST VARIABLES ====================================================
    public static ArrayList<String> getArrayList(String name) {
        HashSet<String> set = (HashSet<String>) prefs.getStringSet(name, null);
        if (set != null) {
            return new ArrayList<>(set);
        } else {
            return new ArrayList<>();
        }
    }

    public static void setArrayList(String name, ArrayList<String> value) {
        HashSet<String> set = new HashSet<>(value);
        editor.putStringSet(name, set).apply();
    }


    // GENERIC GET AND SET BOOLEAN VARIABLES =======================================================
    @NonNull
    public static Boolean getBool(String name) {
        return prefs.getBoolean(name, false);
    }

    public static void setBool(String name, Boolean value) {
        editor.putBoolean(name, value);
        editor.apply();
    }


    // GENERIC GET AND SET DOUBLE ==================================================================
    public static Double getDouble(String name) {
        String value = prefs.getString(name, "0.0");
        if (value != null) {
            return Double.parseDouble(value);
        } else {
            return 0.0;
        }
    }

    public static void setDouble(String name, Double longitude) {
        editor.putString(name, String.valueOf(longitude)).apply();
    }


    // DELETE FUNCTION =============================================================================
    public static void deleteAllSharedPrefs() {
        editor.clear().apply();
    }

}
