package com.chilloutrecords.models;

import androidx.annotation.Keep;

public class TrackModel {

    public String lyrics = "";
    public String name = "";
    public String track_no = "";
    public String url = "";

    public int play_count;

    @Keep
    public TrackModel(){
        // Default constructor required for calls to DataSnapshot.getValue(TrackModel.class)
    }
}
