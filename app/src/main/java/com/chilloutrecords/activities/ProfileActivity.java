package com.chilloutrecords.activities;

import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatImageView;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.viewpager.widget.ViewPager;

import com.bumptech.glide.Glide;
import com.chilloutrecords.BuildConfig;
import com.chilloutrecords.R;
import com.chilloutrecords.adapters.ViewPagerAdapter;
import com.chilloutrecords.fragments.TrackFragment;
import com.chilloutrecords.interfaces.UrlInterface;
import com.chilloutrecords.models.TrackModel;
import com.chilloutrecords.models.UserModel;
import com.chilloutrecords.utils.Database;
import com.chilloutrecords.utils.StaticMethods;
import com.google.android.exoplayer2.DefaultLoadControl;
import com.google.android.exoplayer2.ExoPlayer;
import com.google.android.exoplayer2.ExoPlayerFactory;
import com.google.android.exoplayer2.LoadControl;
import com.google.android.exoplayer2.SimpleExoPlayer;
import com.google.android.exoplayer2.extractor.DefaultExtractorsFactory;
import com.google.android.exoplayer2.extractor.ExtractorsFactory;
import com.google.android.exoplayer2.source.ExtractorMediaSource;
import com.google.android.exoplayer2.source.MediaSource;
import com.google.android.exoplayer2.source.TrackGroupArray;
import com.google.android.exoplayer2.trackselection.DefaultTrackSelector;
import com.google.android.exoplayer2.trackselection.TrackSelectionArray;
import com.google.android.exoplayer2.trackselection.TrackSelector;
import com.google.android.exoplayer2.upstream.DefaultDataSourceFactory;
import com.google.android.exoplayer2.util.Util;
import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.tabs.TabLayout;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;
import com.makeramen.roundedimageview.RoundedImageView;

import java.util.ArrayList;
import java.util.Formatter;
import java.util.Locale;

import static com.chilloutrecords.utils.StaticVariables.EXTRA_DATA;
import static com.chilloutrecords.utils.StaticVariables.EXTRA_STRING;
import static com.chilloutrecords.utils.StaticVariables.FIREBASE_DB;
import static com.chilloutrecords.utils.StaticVariables.FIREBASE_USER;

public class ProfileActivity extends AppCompatActivity {

    private String STR_ID = "";
    private final String TAG_LOG = "PROFILE";

    private TextView
            txt_page_title,
            txt_name,
            txt_stage_name,
            txt_email,
            txt_gender,
            txt_profile_visits,
            txt_member_since;

    private Toolbar toolbar;

    private FloatingActionButton
            btn_edit_picture,
            btn_edit_profile;

    private RoundedImageView
            img_profile;

    private DatabaseReference
            reference;
    private ValueEventListener
            listener;

    private TabLayout
            tab_layout;
    private ViewPager
            view_pager;
    private int[] fragment_title_list = {
            R.string.nav_singles,
            R.string.nav_collections
    };
    private final String[] fragment_extras = {
            BuildConfig.DB_REF_SINGLES,
            BuildConfig.DB_REF_COLLECTIONS,
    };
    private Fragment[] fragment_list = {
            new TrackFragment(),
            new TrackFragment()
    };
    private ArrayList<ArrayList<String>> extra_array_list = new ArrayList<>();

    // MUSIC PLAYER LAYOUT ITEMS ===================================================================
    private LinearLayout ll_bottom_sheet;
    private BottomSheetBehavior bs_behaviour; // lol BS behavior
    private TextView
            txt_track_title,
            txt_track_lyrics,
            txt_track_current_time,
            txt_track_end_time;
    private AppCompatImageView
            btn_track_download;
    private SimpleExoPlayer player;
    private SeekBar seek_bar;
    private Handler handler;
    private ImageButton btn_track_play;
    private boolean
            isPlaying = false,
            initializeMediaControls = true;
    private int INT_PLAY_COUNT = 0;
    private String STR_TRACK_PATH = "";
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
                                bs_behaviour.setState(BottomSheetBehavior.STATE_HALF_EXPANDED);
                                Database.updateTrackPlayCount(STR_TRACK_PATH, INT_PLAY_COUNT);
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

        setContentView(R.layout.activity_profile);

        reference = FIREBASE_DB.getReference(BuildConfig.DB_REF_USERS);

        // FIND ALL VIEWS
        toolbar = findViewById(R.id.toolbar);
        txt_page_title = findViewById(R.id.txt_page_title);
        ll_bottom_sheet = findViewById(R.id.ll_bottom_sheet);
        bs_behaviour = BottomSheetBehavior.from(ll_bottom_sheet);
        bs_behaviour.setHideable(true);
        bs_behaviour.setState(BottomSheetBehavior.STATE_HIDDEN);
        bs_behaviour.setBottomSheetCallback(new BottomSheetBehavior.BottomSheetCallback() {
            @Override
            public void onStateChanged(@NonNull View view, int i) {
                if (i == BottomSheetBehavior.STATE_HIDDEN) {
                    setPlayPause(false);
                }
            }

            @Override
            public void onSlide(@NonNull View view, float v) {

            }
        });
        txt_name = findViewById(R.id.txt_name);
        txt_stage_name = findViewById(R.id.txt_stage_name);
        txt_email = findViewById(R.id.txt_email);
        txt_gender = findViewById(R.id.txt_gender);
        txt_profile_visits = findViewById(R.id.txt_profile_visits);
        txt_member_since = findViewById(R.id.txt_member_since);

        img_profile = findViewById(R.id.img_profile);
        btn_edit_picture = findViewById(R.id.btn_edit_picture);
        btn_edit_profile = findViewById(R.id.btn_edit_profile);

        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            STR_ID = bundle.getString(EXTRA_STRING);
            txt_page_title.setText("PROFILE");
            btn_edit_picture.hide();
            btn_edit_profile.hide();
        } else {
            STR_ID = FIREBASE_USER.getUid();
            txt_page_title.setText("MY PROFILE");
            btn_edit_picture.show();
            btn_edit_profile.show();
        }

        // SETUP TOOLBAR
        toolbar.setNavigationIcon(getResources().getDrawable(R.drawable.ic_back));
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });

        view_pager = findViewById(R.id.view_pager);
        tab_layout = findViewById(R.id.tabs);

        btn_edit_profile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });

        btn_edit_picture.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });

        img_profile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });

    }

    @Override
    protected void onStart() {
        setDataListener();
        super.onStart();
    }

    @Override
    protected void onPause() {
        if (player != null) {
            player.release();
            player = null;
        }
        super.onPause();

    }

    @Override
    protected void onDestroy() {
        if (listener != null) {
            reference.child(STR_ID).removeEventListener(listener);
        }
        super.onDestroy();
    }

    // CLASS METHODS ===============================================================================
    private void setDataListener() {
        listener = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                try {
                    loadProfile(dataSnapshot.getValue(UserModel.class));
                } catch (Exception e) {
                    StaticMethods.showToast(getString(R.string.error_500));
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Database.handleDatabaseError(databaseError);
            }
        };

        reference.child(STR_ID).addValueEventListener(listener);
    }

    private void loadProfile(UserModel model) {
        if (model != null) {
            // Image
            Database.getFileUrl(BuildConfig.STORAGE_IMAGES, model.p_pic, new UrlInterface() {
                @Override
                public void success(String url) {
                    Glide.with(ProfileActivity.this).load(url).into(img_profile);
                }
            });
            // General info
            txt_name.setText(model.name);
            txt_stage_name.setText(getString(R.string.txt_stage_name).concat(model.stage_name));
            txt_email.setText(getString(R.string.txt_email).concat(model.email));
            txt_gender.setText(StaticMethods.getGender(model.gender));
            // stats
            txt_profile_visits.setText(getString(R.string.txt_profile_visits).concat(String.valueOf(model.profile_visits)));
            txt_member_since.setText(getString(R.string.txt_member_since).concat(StaticMethods.getDate(model.member_since_date)));
            // Music content
            if (model.is_artist && model.music != null) {
                tab_layout.setVisibility(View.VISIBLE);
                view_pager.setVisibility(View.VISIBLE);
                extra_array_list.clear();
                extra_array_list.add(model.music.singles);
                extra_array_list.add(model.music.collections);
                setupViewPager(fragment_list, fragment_title_list, fragment_extras, extra_array_list);
                if (initializeMediaControls) {
                    initializeMediaControls = false;
                    initMediaControls();
                }
            } else {
                tab_layout.setVisibility(View.GONE);
                view_pager.setVisibility(View.GONE);
                bs_behaviour.setState(BottomSheetBehavior.STATE_HIDDEN);
            }
        }
    }

    public void ShareProfile(View view) {
        StaticMethods.showToast("Coming soon");
    }

    public void setupViewPager(Fragment[] fragment_list, int[] fragment_title_list, String[] extra_string, ArrayList<ArrayList<String>> extra_array_list) {

        ViewPagerAdapter adapter = new ViewPagerAdapter(getSupportFragmentManager());

        for (int i = 0; i < fragment_title_list.length; i++) {
            Bundle bundle = new Bundle();
            bundle.putString(EXTRA_STRING, extra_string[i]);
            bundle.putStringArrayList(EXTRA_DATA, extra_array_list.get(i));

            Fragment fragment = fragment_list[i];
            fragment.setArguments(bundle);
            adapter.addFragment(fragment, getString(fragment_title_list[i]));
        }

        view_pager.setAdapter(adapter);

        tab_layout.setupWithViewPager(view_pager, true);

    }

    // MUSIC PLAYER CONTROLS =======================================================================
    private void initMediaControls() {
        txt_track_title = findViewById(R.id.txt_track_title);
        txt_track_lyrics = findViewById(R.id.txt_track_lyrics);
        txt_track_current_time = findViewById(R.id.txt_track_current_time);
        txt_track_end_time = findViewById(R.id.txt_track_end_time);
        btn_track_download = findViewById(R.id.btn_track_download);
        btn_track_download.setVisibility(View.GONE);
        btn_track_play = findViewById(R.id.btn_track_play);
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
        TrackSelector trackSelector = new DefaultTrackSelector();
        LoadControl loadControl = new DefaultLoadControl();
        player = ExoPlayerFactory.newSimpleInstance(ProfileActivity.this, trackSelector, loadControl);
        player.addListener(player_listener);
    }

    public void showPlayer(TrackModel model, String full_path, String storage_path) {
        STR_TRACK_PATH = full_path;
        INT_PLAY_COUNT = model.play_count;
        bs_behaviour.setState(BottomSheetBehavior.STATE_HIDDEN);
        txt_track_title.setText(model.name);
        txt_track_lyrics.setText(model.lyrics);

        Database.getFileUrl(storage_path, model.url, new UrlInterface() {
            @Override
            public void success(String url) {
                try {
                    if (url != null && !url.equals("")) {
                        StaticMethods.logg("PROFILE", url);
                        Uri uri = Uri.parse(url);
                        DefaultDataSourceFactory dataSourceFactory = new DefaultDataSourceFactory(ProfileActivity.this, Util.getUserAgent(ProfileActivity.this, getString(R.string.app_name)), null);
                        ExtractorsFactory extractorsFactory = new DefaultExtractorsFactory();
                        MediaSource audioSource = new ExtractorMediaSource(uri, dataSourceFactory, extractorsFactory, null, null);
                        player.prepare(audioSource);
                        setPlayPause(true);

                    } else {
                        bs_behaviour.setState(BottomSheetBehavior.STATE_HIDDEN);
                        StaticMethods.showToast(getString(R.string.error_track));
                        StaticMethods.logg("PROFILE", "EMPTY URL");
                    }
                } catch (Exception e) {
                    bs_behaviour.setState(BottomSheetBehavior.STATE_HIDDEN);
                    StaticMethods.showToast(getString(R.string.error_track));
                    StaticMethods.logg("PROFILE", e.toString());
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
        seek_bar.setProgress(0);
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

