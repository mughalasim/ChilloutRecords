package asimmughal.chilloutrecords.main_pages.activities;

import android.annotation.SuppressLint;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.TextView;

import com.afollestad.easyvideoplayer.EasyVideoCallback;
import com.afollestad.easyvideoplayer.EasyVideoPlayer;
import com.bumptech.glide.Glide;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.gson.Gson;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import asimmughal.chilloutrecords.R;
import asimmughal.chilloutrecords.utils.Helpers;
import pl.droidsonroids.gif.GifImageView;

public class MusicActivity extends ParentActivity implements EasyVideoCallback {

    LinearLayout
            no_list_items,
            LL_tracks;

    RelativeLayout
            LL_player;

    private ImageView
            ppic;

    private String
            STR_NAME = "",
            STR_TRACK_URL = "",
            DB_REFERENCE = "",
            STR_PPIC = "";

    private SeekBar loading_progress;

    private EasyVideoPlayer player;

    private GifImageView current_animation;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_music);

        findAllViews();

        handleExtraBundles();

        initialize(R.id.home, STR_NAME);

        fetchData(DB_REFERENCE);

        player = findViewById(R.id.player);

        player.setCallback(MusicActivity.this);

    }

    private void findAllViews() {
        ppic = findViewById(R.id.ppic);
        LL_tracks = findViewById(R.id.LL_tracks);
        LL_player = findViewById(R.id.LL_player);
        no_list_items = findViewById(R.id.no_list_items);
        no_list_items.setVisibility(View.GONE);

        loading_progress = findViewById(R.id.loading_progress);
        loading_progress.setProgress(0);
        loading_progress.setMax(100);
        loading_progress.setClickable(false);
        loading_progress.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                return true;
            }
        });

    }

    private void handleExtraBundles() {
        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            STR_NAME = extras.getString("album_name");
            STR_PPIC = extras.getString("album_art");
            DB_REFERENCE = "Tracks/" + extras.getString("track_url");
            Glide.with(MusicActivity.this).load(STR_PPIC).into(ppic);
        } else {
            finish();
            helper.ToastMessage(MusicActivity.this, getString(R.string.error_500));
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
                    noData();
                    Helpers.LogThis(e.toString());
                }

                helper.progressDialog(false);
            }

            @Override
            public void onCancelled(DatabaseError error) {
                helper.progressDialog(false);
                noData();
                Log.e("HOMEPAGE: ", "Failed to read value.", error.toException());
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
                @SuppressLint("InflateParams") final View child = layoutInflater.inflate(R.layout.list_item_track, null);

                final TextView track_name = child.findViewById(R.id.track_name);
                final TextView track_no = child.findViewById(R.id.track_no);
                final TextView track_url = child.findViewById(R.id.track_url);
                final GifImageView music_playing = child.findViewById(R.id.music_playing);
                final ImageView download = child.findViewById(R.id.download);

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
                        updateAnimation(current_animation, false);
                        current_animation = music_playing;
                        helper.animate_flash(child, 10000, 5);
                        player.setSource(Uri.parse(track_url.getText().toString()));
                    }
                });

                download.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        helper.myDialog(MusicActivity.this, "Alert", "Oops! Feature coming soon, so hang in there buddy!");
                    }
                });

                LL.addView(child);
            }
            LL.setVisibility(View.VISIBLE);
            LL_player.setVisibility(View.VISIBLE);
        } else {
            LL.setVisibility(View.GONE);
            noData();
        }
    }

    public void noData() {
        no_list_items.setVisibility(View.VISIBLE);
        LL_player.setVisibility(View.GONE);
    }

    public void updateAnimation(GifImageView view, Boolean show) {
        if (current_animation != null) {
            if (show) {
                view.setVisibility(View.VISIBLE);
            } else {
                view.setVisibility(View.GONE);
            }
        }
    }

    // Methods for the implemented EasyVideoCallback ===============================================

    @Override
    public void onPreparing(EasyVideoPlayer player) {
    }

    @Override
    public void onPrepared(EasyVideoPlayer player) {
        player.start();
    }

    @Override
    public void onBuffering(int percent) {
        loading_progress.setProgress(percent);
        if (percent > 1) {
            player.start();
        }
    }

    @Override
    public void onError(EasyVideoPlayer player, Exception e) {
        helper.ToastMessage(MusicActivity.this, getString(R.string.error_500));
        finish();
    }

    @Override
    public void onCompletion(EasyVideoPlayer player) {
        updateAnimation(current_animation, false);
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
        updateAnimation(current_animation, true);
    }

    @Override
    public void onPaused(EasyVideoPlayer player) {
        // TODO handle if needed
        updateAnimation(current_animation, false);
    }

}
