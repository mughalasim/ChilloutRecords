package com.chilloutrecords.models;

import androidx.annotation.Keep;

public class MusicModel {
    public CollectionTrackModel collections;
    public SingleTrackModel singles;

    @Keep
    public MusicModel() {
        // Default constructor required for calls to DataSnapshot.getValue(MusicModel.class)
    }
}
