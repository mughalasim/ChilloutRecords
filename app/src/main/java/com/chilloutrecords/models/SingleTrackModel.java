package com.chilloutrecords.models;

import java.io.Serializable;

public class SingleTrackModel implements Serializable {

    public String art = "";
    public String lyrics = "";
    public String name = "";
    public String url = "";

    public int play_count;

    public long release_date;

}
