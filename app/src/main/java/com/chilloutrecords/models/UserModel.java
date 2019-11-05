package com.chilloutrecords.models;

import java.io.Serializable;

public class UserModel implements Serializable {
    public int getProfile_visits() {
        return profile_visits;
    }

    public int getPlay_count() {
        return play_count;
    }

    public int getGender() {
        return gender;
    }

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getStage_name() {
        return stage_name;
    }

    public String getEmail() {
        return email;
    }

    public String getInfo() {
        return info;
    }

    public Boolean getIs_artist() {
        return is_artist;
    }

    public long getMember_since_date() {
        return member_since_date;
    }

    public int profile_visits;
    public int play_count;
    public int gender;

    public String id = "";
    public String name = "";
    public String stage_name = "";
    public String email = "";
    public String info = "";

    public Boolean is_artist;

    public long member_since_date;

}
