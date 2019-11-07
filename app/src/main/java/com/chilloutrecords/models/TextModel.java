package com.chilloutrecords.models;

import androidx.annotation.Keep;

public class TextModel {
    public String info = "";
    public String title = "";

    @Keep
    public TextModel() {
        // Default constructor required for calls to DataSnapshot.getValue(TextModel.class)
    }
}
