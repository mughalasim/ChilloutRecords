package com.chilloutrecords.activities;

import android.net.Uri;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;

import com.afollestad.easyvideoplayer.EasyVideoCallback;
import com.afollestad.easyvideoplayer.EasyVideoPlayer;

import com.chilloutrecords.R;
import com.chilloutrecords.utils.DialogMethods;
import com.chilloutrecords.utils.StaticMethods;

import static com.chilloutrecords.utils.StaticVariables.EXTRA_STRING;

public class VideoActivity extends AppCompatActivity implements EasyVideoCallback {

    private String
            VIDEO_ID = "",
            VIDEO_URL = "";
    private DialogMethods dialogs;
    private EasyVideoPlayer player;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        handleExtraBundles();

        setContentView(R.layout.activity_video);

        //TODO make this autorotate to Horizontal orientation
//        dialogs = new DialogMethods(VideoActivity.this);
//        player = findViewById(R.id.player);
//        player.setCallback(this);
//        player.setSource(Uri.parse(VIDEO_URL));

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

    @Override
    public void onPause() {
        super.onPause();
        // Make sure the player stops playing if the user presses the home button.
        player.pause();
    }

    // Methods for the implemented EasyVideoCallback ===============================================

    @Override
    public void onPreparing(EasyVideoPlayer player) {

    }

    @Override
    public void onPrepared(EasyVideoPlayer player) {

    }

    @Override
    public void onBuffering(int percent) {

    }

    @Override
    public void onError(EasyVideoPlayer player, Exception e) {
//        dialogs.ToastMessage(VideoActivity.this, getString(R.string.error_500));
        finish();
    }

    @Override
    public void onCompletion(EasyVideoPlayer player) {
        finish();
    }

    @Override
    public void onRetry(EasyVideoPlayer player, Uri source) {
        // TODO handle if used
    }

    @Override
    public void onSubmit(EasyVideoPlayer player, Uri source) {
        // TODO handle if used
    }

    @Override
    public void onStarted(EasyVideoPlayer player) {

    }

    @Override
    public void onPaused(EasyVideoPlayer player) {
        // TODO handle if needed
    }
}