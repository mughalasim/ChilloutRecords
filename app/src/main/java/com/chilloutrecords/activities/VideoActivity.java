package com.chilloutrecords.activities;

import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.ImageButton;
import android.widget.SeekBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatImageView;
import androidx.appcompat.widget.Toolbar;

import com.chilloutrecords.BuildConfig;
import com.chilloutrecords.R;
import com.chilloutrecords.interfaces.UrlInterface;
import com.chilloutrecords.models.VideoModel;
import com.chilloutrecords.utils.Database;
import com.chilloutrecords.utils.StaticMethods;
import com.google.android.exoplayer2.DefaultLoadControl;
import com.google.android.exoplayer2.ExoPlayer;
import com.google.android.exoplayer2.ExoPlayerFactory;
import com.google.android.exoplayer2.SimpleExoPlayer;
import com.google.android.exoplayer2.source.MediaSource;
import com.google.android.exoplayer2.source.ProgressiveMediaSource;
import com.google.android.exoplayer2.source.TrackGroupArray;
import com.google.android.exoplayer2.trackselection.DefaultTrackSelector;
import com.google.android.exoplayer2.trackselection.TrackSelectionArray;
import com.google.android.exoplayer2.ui.PlayerView;
import com.google.android.exoplayer2.upstream.DefaultDataSourceFactory;
import com.google.android.exoplayer2.util.Util;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;

import java.util.Formatter;
import java.util.Locale;

import static com.chilloutrecords.utils.StaticVariables.EXTRA_STRING;
import static com.chilloutrecords.utils.StaticVariables.FIREBASE_DB;

public class VideoActivity extends AppCompatActivity {

    private Toolbar toolbar;
    private final String TAG_LOG = "VIDEO";
    private TextView
            txt_info,
            txt_track_title,
            txt_track_lyrics,
            txt_track_current_time,
            txt_track_end_time;
    private AppCompatImageView
            btn_track_download;
    private PlayerView player_view;
    private SimpleExoPlayer player;
    private SeekBar seek_bar;
    private Handler handler;
    private ImageButton btn_track_play;
    private boolean
            isPlaying = false;
    private int INT_PLAY_COUNT = 0;
    private String
            VIDEO_ID = "",
            STR_TRACK_PATH = "";
    private ExoPlayer.EventListener player_listener = new ExoPlayer.EventListener() {
        @Override
        public void onTracksChanged(TrackGroupArray trackGroups, TrackSelectionArray trackSelections) {
            StaticMethods.logg(TAG_LOG, "onTracksChanged");
        }

        @Override
        public void onLoadingChanged(boolean isLoading) {
            StaticMethods.logg(TAG_LOG, "onLoadingChanged");
        }

        @Override
        public void onPlayerStateChanged(boolean playWhenReady, int playbackState) {
            StaticMethods.logg(TAG_LOG, "onPlayerStateChanged: playWhenReady = " + String.valueOf(playWhenReady) + " playbackState = " + playbackState);
            switch (playbackState) {
                case ExoPlayer.STATE_ENDED:
                    StaticMethods.logg(TAG_LOG, "Playback ended!");
                    //Stop playback and return to start position
                    setPlayPause(false);
                    player.seekTo(0);
                    break;
                case ExoPlayer.STATE_READY:
                    StaticMethods.logg(TAG_LOG, "ExoPlayer ready! pos: " + player.getCurrentPosition() + " max: " + stringForTime((int) player.getDuration()));
                    handler.post(new Runnable() {
                        @Override
                        public void run() {
                            if (player != null && player.isPlaying()) {
//                                Database.updateTrackPlayCount(STR_TRACK_PATH, INT_PLAY_COUNT);
                            }
                        }
                    });
                    setProgress();
                    break;
                case ExoPlayer.STATE_BUFFERING:
                    StaticMethods.logg(TAG_LOG, "Playback buffering!");
                    break;
                case ExoPlayer.STATE_IDLE:
                    StaticMethods.logg(TAG_LOG, "ExoPlayer idle!");
                    break;
            }
        }
    };

    // OVERRIDE METHODS ============================================================================
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            VIDEO_ID = extras.getString(EXTRA_STRING);
            setContentView(R.layout.activity_video);
            findAllViews();
            fetchVideo();
        } else {
            StaticMethods.showToast(getString(R.string.error_500));
            finish();
        }
    }

    @Override
    protected void onPause() {
        if (player != null) {
            player.release();
            player = null;
        }
        super.onPause();
    }

    private void findAllViews() {
        toolbar = findViewById(R.id.toolbar);
        // SETUP TOOLBAR
        toolbar.setNavigationIcon(getResources().getDrawable(R.drawable.ic_back));
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });

        txt_track_title = findViewById(R.id.txt_track_title);
        txt_info = findViewById(R.id.txt_info);
        txt_track_lyrics = findViewById(R.id.txt_track_lyrics);
        txt_track_current_time = findViewById(R.id.txt_track_current_time);
        txt_track_end_time = findViewById(R.id.txt_track_end_time);
        btn_track_download = findViewById(R.id.btn_track_download);
        btn_track_download.setVisibility(View.GONE);
        btn_track_play = findViewById(R.id.btn_track_play);
        player_view = findViewById(R.id.player_view);
        btn_track_play.requestFocus();
        btn_track_play.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                setPlayPause(!isPlaying);
            }
        });
        seek_bar = findViewById(R.id.seek_bar);
        seek_bar.requestFocus();
        seek_bar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if (!fromUser) {
                    // We're not interested in programmatically generated changes to
                    // the progress bar's position.
                    return;
                }

                player.seekTo(progress * 1000);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });
        seek_bar.setMax(0);
        player = ExoPlayerFactory.newSimpleInstance(VideoActivity.this,
                new DefaultTrackSelector(), new DefaultLoadControl());
        player.addListener(player_listener);
        player_view.setPlayer(player);
        player_view.setUseController(false);
    }

    private void fetchVideo() {
        FIREBASE_DB.getReference(BuildConfig.DB_REF_VIDEOS).child(VIDEO_ID).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                VideoModel model = dataSnapshot.getValue(VideoModel.class);
                assert model != null;
                startPlayer(model, BuildConfig.DB_REF_VIDEOS + "/" + model.id, BuildConfig.DB_REF_VIDEOS);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Database.handleDatabaseError(databaseError);
            }
        });

    }

    public void startPlayer(VideoModel model, String full_path, String storage_path) {
        STR_TRACK_PATH = full_path;
        INT_PLAY_COUNT = model.play_count;
        txt_track_title.setText(model.name);
        txt_info.setText(model.info);
        if(model.lyrics.equals("")){
            txt_track_lyrics.setText(getString(R.string.txt_coming_soon));
        }else{
            txt_track_lyrics.setText(model.lyrics);
        }

        Database.getFileUrl(storage_path, model.url, new UrlInterface() {
            @Override
            public void completed(Boolean success, String url) {
                try {
                    if (success) {
                        StaticMethods.logg(TAG_LOG, url);
                        Uri uri = Uri.parse(url);
                        DefaultDataSourceFactory dataSourceFactory = new DefaultDataSourceFactory(VideoActivity.this, Util.getUserAgent(VideoActivity.this, getString(R.string.app_name)), null);
//                        ExtractorsFactory extractorsFactory = new DefaultExtractorsFactory();
                        MediaSource videoSource = new ProgressiveMediaSource.Factory(dataSourceFactory).createMediaSource(uri);
                        player.prepare(videoSource);
                        setPlayPause(true);

                    } else {
                        StaticMethods.showToast(getString(R.string.error_track));
                        StaticMethods.logg(TAG_LOG, "EMPTY URL");
                    }
                } catch (Exception e) {
                    StaticMethods.showToast(getString(R.string.error_track));
                    StaticMethods.logg(TAG_LOG, e.toString());
                }
            }
        });


    }

    private void setPlayPause(boolean play) {
        isPlaying = play;
        player.setPlayWhenReady(play);
        if (!isPlaying) {
            btn_track_play.setImageResource(android.R.drawable.ic_media_play);
        } else {
            setProgress();
            btn_track_play.setImageResource(android.R.drawable.ic_media_pause);
        }
    }

    private String stringForTime(int timeMs) {
        StringBuilder mFormatBuilder;
        Formatter mFormatter;
        mFormatBuilder = new StringBuilder();
        mFormatter = new Formatter(mFormatBuilder, Locale.getDefault());
        int totalSeconds = timeMs / 1000;

        int seconds = totalSeconds % 60;
        int minutes = (totalSeconds / 60) % 60;
        int hours = totalSeconds / 3600;

        mFormatBuilder.setLength(0);
        if (hours > 0) {
            return mFormatter.format("%d:%02d:%02d", hours, minutes, seconds).toString();
        } else {
            return mFormatter.format("%02d:%02d", minutes, seconds).toString();
        }
    }

    private void setProgress() {
//        seek_bar.setProgress(0);
        seek_bar.setMax((int) player.getDuration() / 1000);
        txt_track_current_time.setText(stringForTime((int) player.getCurrentPosition()));
        txt_track_end_time.setText(stringForTime((int) player.getDuration()));

        if (handler == null) handler = new Handler();
        //Make sure you update Seekbar on UI thread
        handler.post(new Runnable() {
            @Override
            public void run() {
                if (player != null && isPlaying) {
                    seek_bar.setMax((int) player.getDuration() / 1000);
                    int mCurrentPosition = (int) player.getCurrentPosition() / 1000;
                    seek_bar.setProgress(mCurrentPosition);
                    txt_track_current_time.setText(stringForTime((int) player.getCurrentPosition()));
                    txt_track_end_time.setText(stringForTime((int) player.getDuration()));

                    handler.postDelayed(this, 1000);
                }
            }
        });
    }

}