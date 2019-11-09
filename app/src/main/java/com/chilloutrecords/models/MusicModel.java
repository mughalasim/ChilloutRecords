package com.chilloutrecords.models;

import java.util.ArrayList;

public class MusicModel {

    public ArrayList<String> collections = new ArrayList<>();
    public ArrayList<String> singles = new ArrayList<>();

    public MusicModel() {
        // Default constructor required for calls to DataSnapshot.getValue(MusicModel.class)
    }

}
