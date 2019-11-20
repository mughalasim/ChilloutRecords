package com.chilloutrecords.models;

import java.io.Serializable;

public class UserModel implements Serializable {
    public int profile_visits;
    public int play_count;
    public int gender;

    public String id = "";
    public String name = "";
    public String stage_name = "";
    public String p_pic = "";
    public String email = "";
    public String info = "";

    public Boolean is_artist;

    public long member_since_date;

    public MusicModel music = new MusicModel();

    public UserModel() {
        // Default constructor required for calls to DataSnapshot.getValue(UserModel.class)
    }

}
