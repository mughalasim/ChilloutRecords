package com.chilloutrecords.models;

import androidx.annotation.Keep;

import java.util.ArrayList;

public class CollectionTrackModel {
    public String name = "";
    public long release_date;
    public String art = "";
    public String type = "";

    public ArrayList<TrackModel> track_models;

    @Keep
    public CollectionTrackModel(){
        // Default constructor required for calls to DataSnapshot.getValue(CollectionTrackModel.class)
    }

}
