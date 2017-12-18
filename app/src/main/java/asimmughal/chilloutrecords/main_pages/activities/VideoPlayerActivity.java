package asimmughal.chilloutrecords.main_pages.activities;

import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import com.afollestad.easyvideoplayer.EasyVideoCallback;
import com.afollestad.easyvideoplayer.EasyVideoPlayer;

import asimmughal.chilloutrecords.R;
import asimmughal.chilloutrecords.utils.Helpers;


public class VideoPlayerActivity extends AppCompatActivity implements EasyVideoCallback {

    private static final String
            VIDEO_URL = "https://firebasestorage.googleapis.com/v0/b/eatout-restaurant-guide.appspot.com/o/Main%20Video%20R1%20(1min%20version).mp4?alt=media&token=6bb55612-5ab0-4767-8bb2-e5de32eabe6a";
    private Helpers helpers;
    private EasyVideoPlayer player;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_video_player);

        helpers = new Helpers(VideoPlayerActivity.this);

        player = findViewById(R.id.player);
        player.setCallback(this);
        player.setSource(Uri.parse(VIDEO_URL));
        // From here, the player view will show a progress indicator until the player is prepared.
        // Once it's prepared, the progress indicator goes away and the controls become enabled for the user to begin playback.
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