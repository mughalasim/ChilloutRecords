package com.chilloutrecords.activities;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import com.chilloutrecords.R;
import com.chilloutrecords.utils.StaticMethods;

import static com.chilloutrecords.utils.StaticVariables.EXTRA_STRING;

public class VideoActivity extends AppCompatActivity {

    private String VIDEO_ID = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        handleExtraBundles();

        setContentView(R.layout.activity_video);


    }

    private void handleExtraBundles() {
        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            VIDEO_ID = extras.getString(EXTRA_STRING);
            // TODO - from video ID get the video object and redesign the entire video activity

        } else {
            StaticMethods.showToast(getString(R.string.error_500));
            finish();
        }
    }

}