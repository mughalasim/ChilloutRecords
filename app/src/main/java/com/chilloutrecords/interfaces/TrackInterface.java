package com.chilloutrecords.interfaces;

import com.chilloutrecords.models.TrackModel;

public interface TrackInterface {

    void success(TrackModel model, String db_path, String storage_path);

}