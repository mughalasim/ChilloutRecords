package com.chilloutrecords.models;

import java.io.Serializable;
import java.util.ArrayList;

public class CollectionTrackModel implements Serializable {
    public String name = "";
    public long release_date;
    public String art = "";
    public String type = "";

    public ArrayList<TrackModel> track_models;


}
