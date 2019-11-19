package com.chilloutrecords.models;

import androidx.annotation.Keep;

public class TrackModel {

    public String id = "";
    public String art = "";
    public String lyrics = "";
    public String name = "";
    public String url = "";

    public int play_count;
    public int number;

    public long release_date;

    @Keep
    public TrackModel(){
        // Default constructor required for calls to DataSnapshot.getValue(TrackModel.class)
    }

}
