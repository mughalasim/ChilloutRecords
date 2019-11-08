package com.chilloutrecords.models;

import androidx.annotation.Keep;

import com.google.gson.annotations.Expose;

import java.util.ArrayList;
import java.util.List;

public class UserModel {
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

    public MusicModel music = new MusicModel();

    @Keep
    public UserModel() {
        // Default constructor required for calls to DataSnapshot.getValue(UserModel.class)
    }

    @Keep
    public class MusicModel {
        @Expose
        public ArrayList<String>
                collections = new ArrayList<>(),
                singles = new ArrayList<>();

        @Keep
        public MusicModel() {
            // Default constructor required for calls to DataSnapshot.getValue(MusicModel.class)
        }

    }

}
