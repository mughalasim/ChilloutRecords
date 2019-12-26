package com.chilloutrecords.interfaces;

import com.chilloutrecords.models.TrackModel;

public interface TrackListingInterface {

    void success(TrackModel model, String track_type, String collection_id);

}
