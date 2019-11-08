package com.chilloutrecords.models;

import androidx.annotation.Keep;

import java.util.ArrayList;

public class CollectionModel {
    public String id = "";
    public String name = "";
    public long release_date;
    public String art = "";
    public String type = "";

    public ArrayList<TrackModel> tracks;

    @Keep
    public CollectionModel(){
        // Default constructor required for calls to DataSnapshot.getValue(CollectionModel.class)
    }

}
