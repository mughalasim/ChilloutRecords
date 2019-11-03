package com.chilloutrecords.models;

import java.io.Serializable;

public class UserModel implements Serializable {
    public int id;
    public int profile_visits;
    public int play_count;
    public int gender;

    public String name = "";
    public String stage_name = "";
    public String email = "";
    public String info = "";

    public Boolean is_artist;

    public long member_since_date;

}
