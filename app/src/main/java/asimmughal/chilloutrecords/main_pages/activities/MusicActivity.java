package asimmughal.chilloutrecords.main_pages.activities;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.google.android.exoplayer2.DefaultLoadControl;
import com.google.android.exoplayer2.DefaultRenderersFactory;
import com.google.android.exoplayer2.ExoPlayerFactory;
import com.google.android.exoplayer2.LoadControl;
import com.google.android.exoplayer2.SimpleExoPlayer;
import com.google.android.exoplayer2.extractor.DefaultExtractorsFactory;
import com.google.android.exoplayer2.source.ExtractorMediaSource;
import com.google.android.exoplayer2.source.MediaSource;
import com.google.android.exoplayer2.trackselection.AdaptiveTrackSelection;
import com.google.android.exoplayer2.trackselection.DefaultTrackSelector;
import com.google.android.exoplayer2.trackselection.TrackSelection;
import com.google.android.exoplayer2.trackselection.TrackSelector;
import com.google.android.exoplayer2.ui.SimpleExoPlayerView;
import com.google.android.exoplayer2.upstream.DefaultBandwidthMeter;
import com.google.android.exoplayer2.upstream.DefaultHttpDataSourceFactory;
import com.google.android.exoplayer2.util.Util;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.gson.Gson;
import com.makeramen.roundedimageview.RoundedImageView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import asimmughal.chilloutrecords.R;
import asimmughal.chilloutrecords.utils.Helpers;

public class MusicActivity extends ParentActivity {

    LinearLayout
            no_list_items,
            LL_tracks;

    private ImageView
            ppic;

    private String
            STR_NAME = "",
            STR_TRACK_URL = "",
            DB_REFERENCE = "",
            STR_PPIC = "";

    private SimpleExoPlayerView
            playerView;

    SimpleExoPlayer
            player;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_music);

        findAllViews();

        handleExtraBundles();

        initialize(R.id.home, STR_NAME);

        fetchData(DB_REFERENCE);

    }

    private void findAllViews() {
        ppic = findViewById(R.id.ppic);
        LL_tracks = findViewById(R.id.LL_tracks);
        no_list_items = findViewById(R.id.no_list_items);
        no_list_items.setVisibility(View.GONE);

        playerView = (SimpleExoPlayerView) findViewById(R.id.video_view);

        final DefaultBandwidthMeter bandwidthMeter = new DefaultBandwidthMeter();
        final TrackSelection.Factory videoTrackSelectionFactory = new AdaptiveTrackSelection.Factory(bandwidthMeter);
        final TrackSelector trackSelector = new DefaultTrackSelector(videoTrackSelectionFactory);

        final LoadControl loadControl = new DefaultLoadControl();

        player = ExoPlayerFactory.newSimpleInstance(MusicActivity.this, trackSelector, loadControl);

    }

    private void initializePlayer() {
        Uri uri = Uri.parse(STR_TRACK_URL);
        MediaSource mediaSource = buildMediaSource(uri);
        player.prepare(mediaSource, true, false);
        player = ExoPlayerFactory.newSimpleInstance(
                new DefaultRenderersFactory(MusicActivity.this),
                new DefaultTrackSelector(), new DefaultLoadControl());

        playerView.setPlayer(player);

//        player.setPlayWhenReady(playWhenReady);
//        player.seekTo(currentWindow, playbackPosition);
    }

    private MediaSource buildMediaSource(Uri uri) {
        return new ExtractorMediaSource(uri,
                new DefaultHttpDataSourceFactory("ua"),
                new DefaultExtractorsFactory(), null, null);
    }

    private void handleExtraBundles() {
        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            STR_NAME = extras.getString("album_name");
            STR_PPIC = extras.getString("album_art");
            DB_REFERENCE ="Tracks/" + extras.getString("track_url");
            Glide.with(MusicActivity.this).load(STR_PPIC).into(ppic);
        } else {
            finish();
            helper.ToastMessage(MusicActivity.this, getString(R.string.error_500));
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        if (Util.SDK_INT <= 23) {
            releasePlayer();
        }
    }

    @Override
    public void onStop() {
        super.onStop();
        if (Util.SDK_INT > 23) {
            releasePlayer();
        }
    }

//    @Override
//    public void onStart() {
//        super.onStart();
//        if (Util.SDK_INT > 23) {
//            initializePlayer();
//        }
//    }
//
//    @Override
//    public void onResume() {
//        super.onResume();
////        hideSystemUi();
//        if ((Util.SDK_INT <= 23 || player == null)) {
//            initializePlayer();
//        }
//    }

    private void releasePlayer() {
        if (player != null) {
//            playbackPosition = player.getCurrentPosition();
//            currentWindow = player.getCurrentWindowIndex();
//            playWhenReady = player.getPlayWhenReady();
            player.release();
            player = null;
        }
    }

    public void fetchData(String dbRef) {
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        Helpers.LogThis("DATABASE REF: " + dbRef);
        DatabaseReference myRef = database.getReference(dbRef);

        myRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                try {
                    Gson gson = new Gson();
                    JSONArray jsonArray = new JSONArray(gson.toJson(dataSnapshot.getValue()));
                    Helpers.LogThis("AFTER PARSING: " + jsonArray.toString());

                    addTracks(jsonArray, LL_tracks);

                } catch (Exception e) {
                    e.printStackTrace();
                    no_list_items.setVisibility(View.VISIBLE);
                    Helpers.LogThis(e.toString());
                }

                helper.progressDialog(false);
            }

            @Override
            public void onCancelled(DatabaseError error) {
                helper.progressDialog(false);
                no_list_items.setVisibility(View.VISIBLE);
                Log.w("HOMEPAGE: ", "Failed to read value.", error.toException());
            }
        });

    }

    private void addTracks(final JSONArray jsonArray, final LinearLayout LL) throws JSONException {
        LayoutInflater layoutInflater;
        layoutInflater = LayoutInflater.from(MusicActivity.this);
        LL.removeAllViews();

        int jsonArrayLength = jsonArray.length();
        if (jsonArrayLength > 0) {
            for (int i = 0; i < jsonArrayLength; i++) {
                // FIND ALL THE VIEWS ON THE CARD
                @SuppressLint("InflateParams")
                View child = layoutInflater.inflate(R.layout.list_item_track, null);

                final TextView track_name = child.findViewById(R.id.track_name);
                final TextView track_no = child.findViewById(R.id.track_no);
                final TextView track_url = child.findViewById(R.id.track_url);

                JSONObject albumObject = jsonArray.getJSONObject(i);
                final String str_track_name = albumObject.getString("track_name");
                final String str_track_no = albumObject.getString("track_no");
                STR_TRACK_URL = albumObject.getString("track_url");

                track_name.setText(str_track_name);
                track_no.setText(str_track_no);
                track_url.setText(STR_TRACK_URL);


                child.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                       initializePlayer();
                    }
                });

                LL.addView(child);
            }
            LL.setVisibility(View.VISIBLE);
        } else {
            LL.setVisibility(View.GONE);
            no_list_items.setVisibility(View.VISIBLE);
        }
    }

}
