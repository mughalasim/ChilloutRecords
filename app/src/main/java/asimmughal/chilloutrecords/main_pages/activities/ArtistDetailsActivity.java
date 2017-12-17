package asimmughal.chilloutrecords.main_pages.activities;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.gson.Gson;
import com.makeramen.roundedimageview.RoundedImageView;

import org.json.JSONArray;
import org.json.JSONObject;

import asimmughal.chilloutrecords.R;
import asimmughal.chilloutrecords.utils.Helpers;

public class ArtistDetailsActivity extends ParentActivity {

    LinearLayout
            scroll_albums,
            scroll_mixtapes,
            scroll_singles,
            LL_album,
            LL_mixtapes,
            LL_singles;

    private TextView information;

    private String
            STR_ID = "",
            STR_NAME = "",
            DB_REFERENCE = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_artist_details);

        handleExtraBundles();

        initialize(R.id.about_us, STR_NAME);

        findAllViews();

        fetchData(DB_REFERENCE);

    }

    private void findAllViews() {
        information = findViewById(R.id.information);

        scroll_albums = findViewById(R.id.scroll_albums);
        scroll_mixtapes = findViewById(R.id.scroll_mixtapes);
        scroll_singles = findViewById(R.id.scroll_singles);

        LL_album = findViewById(R.id.LL_albums);
        LL_mixtapes = findViewById(R.id.LL_mixtapes);
        LL_singles = findViewById(R.id.LL_singles);

        LL_album.setVisibility(View.GONE);
        LL_mixtapes.setVisibility(View.GONE);
        LL_singles.setVisibility(View.GONE);

    }

    private void handleExtraBundles() {
        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            STR_ID = extras.getString("id");
            STR_NAME = extras.getString("name");
            DB_REFERENCE = "Music/" + STR_ID;
        } else {
            finish();
            helper.ToastMessage(ArtistDetailsActivity.this, getString(R.string.error_500));
        }
    }

    public void fetchData(String dbRef) {
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        Helpers.LogThis("DATABASE REF: " + dbRef);
        DatabaseReference myRef = database.getReference(dbRef);

        myRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                helper.progressDialog(false);

                try {
                    Gson gson = new Gson();
                    JSONObject jsonObject = new JSONObject(gson.toJson(dataSnapshot.getValue()));
                    Helpers.LogThis("AFTER PARSING: " + jsonObject.toString());

                    information.setText(jsonObject.getString("info"));

                    LayoutInflater albumInflater;
                    albumInflater = LayoutInflater.from(ArtistDetailsActivity.this);
                    JSONArray albumArray = jsonObject.getJSONArray("Albums");
                    int albumLength = albumArray.length();
                    for(int i = 0;i<albumLength; i++){
                        // FIND ALL THE VIEWS ON THE CARD
                        @SuppressLint("InflateParams")
                        View child = albumInflater.inflate(R.layout.list_item_music, null);
                        final RoundedImageView album_art = child.findViewById(R.id.album_art);
                        final TextView name = child.findViewById(R.id.name);
                        final TextView release_year = child.findViewById(R.id.release_year);

                        JSONObject albumObject = albumArray.getJSONObject(i);
                        name.setText(albumObject.getString("name"));
                        release_year.setText(albumObject.getString("release_year"));
                        Glide.with(ArtistDetailsActivity.this).load(albumObject.getString("album_art")).into(album_art);

                        JSONArray tracks = albumObject.getJSONArray("tracks");
                        int trackLength = albumArray.length();
                        for(int x = 0;x<trackLength; x++){
                            JSONObject trackObject = tracks.getJSONObject(i);
                            String track_name = trackObject.getString("track_name");
                            String track_no = trackObject.getString("track_no");
                        }
                        LL_album.addView(child);
                        LL_album.setVisibility(View.VISIBLE);
                    }

                } catch (Exception e) {
                    e.printStackTrace();
                    information.setText("No Information available just yet! ;)");
                    Helpers.LogThis(e.toString());
                }
            }

            @Override
            public void onCancelled(DatabaseError error) {
                helper.progressDialog(false);
//                noLists();
                information.setText("No Information available just yet! ;)");
                Log.w("HOMEPAGE: ", "Failed to read value.", error.toException());
            }
        });

    }

}
