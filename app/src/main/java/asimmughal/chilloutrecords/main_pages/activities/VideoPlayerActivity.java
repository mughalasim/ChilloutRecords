package asimmughal.chilloutrecords.main_pages.activities;

import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import com.afollestad.easyvideoplayer.EasyVideoCallback;
import com.afollestad.easyvideoplayer.EasyVideoPlayer;

import asimmughal.chilloutrecords.R;
import asimmughal.chilloutrecords.utils.Helpers;

public class VideoPlayerActivity extends AppCompatActivity implements EasyVideoCallback {

    private String
            VIDEO_URL = "";
    private Helpers helpers;
    private EasyVideoPlayer player;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_video_player);

        handleExtraBundles();

        helpers = new Helpers(VideoPlayerActivity.this);
        player = findViewById(R.id.player);
        player.setCallback(this);
        player.setSource(Uri.parse(VIDEO_URL));

    }

    private void handleExtraBundles() {
        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            VIDEO_URL = extras.getString("VIDEO_URL");

        } else {
            finish();
            helpers.ToastMessage(VideoPlayerActivity.this, getString(R.string.error_500));
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
        helpers.ToastMessage(VideoPlayerActivity.this, getString(R.string.error_500));
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