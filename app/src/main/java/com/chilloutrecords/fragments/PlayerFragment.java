package com.chilloutrecords.fragments;

import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.view.InflateException;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.SeekBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.AppCompatImageView;
import androidx.fragment.app.Fragment;

import com.chilloutrecords.BuildConfig;
import com.chilloutrecords.R;
import com.chilloutrecords.activities.ParentActivity;
import com.chilloutrecords.interfaces.UrlInterface;
import com.chilloutrecords.models.NavigationModel;
import com.chilloutrecords.models.TrackModel;
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

import java.util.Objects;

import static com.chilloutrecords.activities.ParentActivity.PAGE_TITLE_UPGRADE;
import static com.chilloutrecords.activities.ParentActivity.STR_DOWNLOAD_URL;
import static com.chilloutrecords.activities.ParentActivity.STR_FILE_NAME;
import static com.chilloutrecords.activities.ParentActivity.STR_SAVE_TO_PATH;
import static com.chilloutrecords.utils.StaticMethods.getTimeFromMillis;
import static com.chilloutrecords.utils.StaticVariables.EXTRA_STRING;
import static com.chilloutrecords.utils.StaticVariables.EXTRA_TRACK_COLLECTION;
import static com.chilloutrecords.utils.StaticVariables.EXTRA_TRACK_SINGLE;
import static com.chilloutrecords.utils.StaticVariables.EXTRA_VIDEO;
import static com.chilloutrecords.utils.StaticVariables.STR_COLLECTION_ID;
import static com.chilloutrecords.utils.StaticVariables.TRACK_MODEL;
import static com.chilloutrecords.utils.StaticVariables.USER_MODEL;
import static com.chilloutrecords.utils.StaticVariables.VIDEO_MODEL;

public class PlayerFragment extends Fragment {
    private View root_view;
    private final String TAG_LOG = "PLAYER";
    private TextView
            txt_info,
            txt_track_title,
            txt_track_lyrics,
            txt_track_current_time,
            txt_track_end_time,
            txt_track_plays,
            txt_track_release;
    private AppCompatImageView
            btn_download;
    private PlayerView player_view;
    private SimpleExoPlayer player;
    private SeekBar seek_bar;
    private Handler handler;
    private ImageButton btn_track_play;
    private boolean
            isPlaying = false;
    private int
            INT_PLAY_COUNT = 0;
    private String
            STR_CONTENT_DB_PATH = "",
            STR_CONTENT_STORAGE_PATH = "";
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
            StaticMethods.logg(TAG_LOG, "onPlayerStateChanged: playWhenReady = " + playWhenReady + " playbackState = " + playbackState);
            switch (playbackState) {
                case ExoPlayer.STATE_ENDED:
                    StaticMethods.logg(TAG_LOG, "Playback ended!");
                    //Stop playback and return to start position
                    setPlayPause(false);
                    player.seekTo(0);
                    break;
                case ExoPlayer.STATE_READY:
                    btn_download.setVisibility(View.VISIBLE);
                    StaticMethods.logg(TAG_LOG, "ExoPlayer ready! pos: " + player.getCurrentPosition() + " max: " + getTimeFromMillis((int) player.getDuration()));
                    handler.post(new Runnable() {
                        @Override
                        public void run() {
                            if (player != null && player.isPlaying()) {
                                Database.updateTrackPlayCount(STR_CONTENT_DB_PATH, INT_PLAY_COUNT);
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
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        if (root_view == null && getActivity() != null) {
            try {
                root_view = inflater.inflate(R.layout.frag_player, container, false);
            } catch (InflateException e) {
                e.printStackTrace();
            }
        } else {
            ((ViewGroup) container.getParent()).removeView(root_view);
        }
        return root_view;
    }

    @Override
    public void onPause() {
        setPlayPause(false);
        super.onPause();
    }

    @Override
    public void onResume() {
        Bundle bundle = this.getArguments();
        if (bundle != null) {
            String extra = bundle.getString(EXTRA_STRING);
            findAllViews();
            assert extra != null;
            switch (extra) {
                case EXTRA_VIDEO:
                    STR_CONTENT_DB_PATH = BuildConfig.DB_REF_VIDEOS + "/" + VIDEO_MODEL.id;
                    STR_CONTENT_STORAGE_PATH = BuildConfig.DB_REF_VIDEOS;
                    STR_SAVE_TO_PATH = "Chillout Records/Videos/";
                    player_view.setVisibility(View.VISIBLE);
                    startVideoPlayer(VIDEO_MODEL);
                    break;

                case EXTRA_TRACK_SINGLE:
                    STR_CONTENT_DB_PATH = BuildConfig.DB_REF_SINGLES + "/" + TRACK_MODEL.id;
                    STR_CONTENT_STORAGE_PATH = BuildConfig.DB_REF_SINGLES;
                    STR_SAVE_TO_PATH = "Chillout Records/Music/";
                    player_view.setVisibility(View.GONE);
                    startMusicPlayer(TRACK_MODEL);
                    break;

                case EXTRA_TRACK_COLLECTION:
                    STR_CONTENT_DB_PATH = BuildConfig.DB_REF_COLLECTIONS + "/" + STR_COLLECTION_ID + "/tracks/" + TRACK_MODEL.id;
                    STR_CONTENT_STORAGE_PATH = BuildConfig.DB_REF_COLLECTIONS + "/" + STR_COLLECTION_ID;
                    STR_SAVE_TO_PATH = "Chillout Records/Music/";
                    player_view.setVisibility(View.GONE);
                    startMusicPlayer(TRACK_MODEL);
                    break;
            }
        }
        super.onResume();
    }

    @Override
    public void onDestroyView() {
        if (player != null) {
            if (player.isPlaying())
                player.stop();
            player.release();
        }
        super.onDestroyView();
    }

    @Override
    public void onDestroy() {
        if (player != null) {
            player = null;
        }
        super.onDestroy();
    }

    // CLASS METHODS ===============================================================================
    private void findAllViews() {
        txt_track_title = root_view.findViewById(R.id.txt_track_title);
        txt_info = root_view.findViewById(R.id.txt_info);
        txt_track_lyrics = root_view.findViewById(R.id.txt_track_lyrics);
        txt_track_current_time = root_view.findViewById(R.id.txt_track_current_time);
        txt_track_end_time = root_view.findViewById(R.id.txt_track_end_time);
        txt_track_plays = root_view.findViewById(R.id.txt_track_plays);
        txt_track_release = root_view.findViewById(R.id.txt_track_release);
        btn_download = root_view.findViewById(R.id.btn_download);
        btn_download.setVisibility(View.GONE);
        btn_track_play = root_view.findViewById(R.id.btn_track_play);
        player_view = root_view.findViewById(R.id.player_view);
        btn_track_play.requestFocus();
        btn_track_play.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                setPlayPause(!isPlaying);
            }
        });
        btn_download.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (USER_MODEL.is_activated) {
                    ((ParentActivity) Objects.requireNonNull(getActivity())).startFileDownload();
                } else {
                    ((ParentActivity) Objects.requireNonNull(getActivity())).loadFragment(new NavigationModel(new PayFragment(), PAGE_TITLE_UPGRADE, "", null, true));
                }
            }
        });


        seek_bar = root_view.findViewById(R.id.seek_bar);
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
        player = ExoPlayerFactory.newSimpleInstance(Objects.requireNonNull(getActivity()), new DefaultTrackSelector(), new DefaultLoadControl());
        player.addListener(player_listener);
        player_view.setPlayer(player);
        player_view.setUseController(false);
    }

    private void startVideoPlayer(VideoModel model) {
        INT_PLAY_COUNT = model.play_count;
        txt_track_title.setText(model.name);
        txt_info.setText(model.info);
        if (model.lyrics.equals("")) {
            txt_track_lyrics.setText(getString(R.string.txt_coming_soon));
        } else {
            txt_track_lyrics.setText(model.lyrics);
        }
        txt_track_plays.setText(String.valueOf(INT_PLAY_COUNT).concat(" Plays"));
        txt_track_release.setText(StaticMethods.getDate(model.release_date));
        getFile(model.url);
    }

    private void startMusicPlayer(TrackModel model) {
        INT_PLAY_COUNT = model.play_count;
        txt_track_title.setText(model.name);
        txt_info.setText(getString(R.string.txt_lyrics));
        if (model.lyrics.equals("")) {
            txt_track_lyrics.setText(getString(R.string.txt_coming_soon));
        } else {
            txt_track_lyrics.setText(model.lyrics);
        }
        txt_track_plays.setText(String.valueOf(INT_PLAY_COUNT).concat(" Plays"));
        txt_track_release.setText(StaticMethods.getDate(model.release_date));
        getFile(model.url);
    }

    private void getFile(String url) {
        STR_FILE_NAME = url;
        Database.getFileUrl(STR_CONTENT_STORAGE_PATH, url, "", new UrlInterface() {
            @Override
            public void completed(Boolean success, String url) {
                try {
                    if (success) {
                        StaticMethods.logg(TAG_LOG, url);
                        STR_DOWNLOAD_URL = url;
                        Uri uri = Uri.parse(url);
                        DefaultDataSourceFactory dataSourceFactory = new DefaultDataSourceFactory(Objects.requireNonNull(getActivity()), Util.getUserAgent(getActivity(), getString(R.string.app_name)), null);
                        MediaSource source = new ProgressiveMediaSource.Factory(dataSourceFactory).createMediaSource(uri);
                        player.prepare(source);
                        setPlayPause(true);
                    } else {
                        StaticMethods.logg(TAG_LOG, "EMPTY URL");
                        txt_info.setText(getString(R.string.txt_alert));
                        txt_track_lyrics.setText(getString(R.string.error_track_soon));
                    }
                } catch (Exception e) {
                    StaticMethods.logg(TAG_LOG, e.toString());
                    txt_info.setText(getString(R.string.error));
                    txt_track_lyrics.setText(getString(R.string.error_track));
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

    private void setProgress() {
        seek_bar.setMax((int) player.getDuration() / 1000);
        txt_track_current_time.setText(getTimeFromMillis((int) player.getCurrentPosition()));
        txt_track_end_time.setText(getTimeFromMillis((int) player.getDuration()));

        if (handler == null) handler = new Handler();
        //Make sure you update Seekbar on UI thread
        handler.post(new Runnable() {
            @Override
            public void run() {
                if (player != null && isPlaying) {
                    seek_bar.setMax((int) player.getDuration() / 1000);
                    int mCurrentPosition = (int) player.getCurrentPosition() / 1000;
                    seek_bar.setProgress(mCurrentPosition);
                    txt_track_current_time.setText(getTimeFromMillis((int) player.getCurrentPosition()));
                    txt_track_end_time.setText(getTimeFromMillis((int) player.getDuration()));
                    handler.postDelayed(this, 1000);
                }
            }
        });
    }
}
