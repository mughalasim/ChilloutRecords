package com.chilloutrecords.models;

import androidx.annotation.Keep;

public class SingleTrackModel {

    public String art = "";
    public String lyrics = "";
    public String name = "";
    public String url = "";

    public int play_count;

    public long release_date;

    @Keep
    public SingleTrackModel(){
        // Default constructor required for calls to DataSnapshot.getValue(SingleTrackModel.class)
    }

}
