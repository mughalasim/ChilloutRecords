package asimmughal.chilloutrecords.main_pages.activities;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
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
import org.json.JSONException;
import org.json.JSONObject;

import asimmughal.chilloutrecords.R;
import asimmughal.chilloutrecords.main_pages.models.TrackModel;
import asimmughal.chilloutrecords.utils.Helpers;

public class ArtistDetailsActivity extends ParentActivity {

    LinearLayout
            scroll_albums,
            scroll_mixtapes,
            scroll_singles,
            LL_album,
            LL_mixtapes,
            LL_singles;
    private ImageView ppic;

    private TextView information;

    private String
            STR_ID = "",
            STR_NAME = "",
            STR_PPIC = "",
            DB_REFERENCE = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_artist_details);

        findAllViews();

        handleExtraBundles();

        initialize(R.id.home, STR_NAME);

        fetchData(DB_REFERENCE);

    }

    private void findAllViews() {
        information = findViewById(R.id.information);
        ppic = findViewById(R.id.ppic);

        scroll_albums = findViewById(R.id.scroll_albums);
        scroll_mixtapes = findViewById(R.id.scroll_mixtapes);
        scroll_singles = findViewById(R.id.scroll_singles);

        LL_album = findViewById(R.id.LL_albums);
        LL_mixtapes = findViewById(R.id.LL_mixtapes);
        LL_singles = findViewById(R.id.LL_singles);

    }

    private void handleExtraBundles() {
        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            STR_ID = extras.getString("id");
            STR_NAME = extras.getString("name");
            STR_PPIC = extras.getString("ppic");
            DB_REFERENCE = "Music/" + STR_ID;
            Glide.with(ArtistDetailsActivity.this).load(STR_PPIC).into(ppic);
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
                try {
                    Gson gson = new Gson();
                    JSONObject jsonObject = new JSONObject(gson.toJson(dataSnapshot.getValue()));
                    Helpers.LogThis("AFTER PARSING: " + jsonObject.toString());

                    information.setText(jsonObject.getString("info"));

                    addMusicLayout(jsonObject, "Albums", scroll_albums);

                    addMusicLayout(jsonObject, "Mixtape", scroll_mixtapes);

                    addMusicLayout(jsonObject, "Singles", scroll_singles);


                } catch (Exception e) {
                    e.printStackTrace();
                    information.setText("No Information available just yet! ;)");
                    Helpers.LogThis(e.toString());
                }
                helper.progressDialog(false);
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

    private void addMusicLayout(final JSONObject jsonObject, final String flag_name, final LinearLayout LL) throws JSONException {
        LayoutInflater layoutInflater;
        layoutInflater = LayoutInflater.from(ArtistDetailsActivity.this);
        LL.removeAllViews();
        JSONArray jsonArray = jsonObject.getJSONArray(flag_name);
        int jsonArrayLength = jsonArray.length();
        if (jsonArrayLength > 0) {
            for (int i = 0; i < jsonArrayLength; i++) {
                // FIND ALL THE VIEWS ON THE CARD
                @SuppressLint("InflateParams")
                View child = layoutInflater.inflate(R.layout.list_item_music, null);
//                child.setLayoutParams(childParams);
                final RoundedImageView album_art = child.findViewById(R.id.album_art);
                final TextView name = child.findViewById(R.id.name);
                final TextView release_year = child.findViewById(R.id.release_year);

                JSONObject albumObject = jsonArray.getJSONObject(i);
                final String str_album_name = albumObject.getString("name");
                final String str_album_release_year = albumObject.getString("release_year");
                final String str_album_art = albumObject.getString("album_art");
                final String str_track_url = albumObject.getString("track_url");

                name.setText(str_album_name);
                release_year.setText(str_album_release_year);
                Glide.with(ArtistDetailsActivity.this).load(str_album_art).into(album_art);

                child.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        startActivity(new Intent(ArtistDetailsActivity.this, MusicActivity.class)
                                .putExtra("album_name", str_album_name)
                                .putExtra("album_art", str_album_art)
                                .putExtra("album_release_year", str_album_release_year)
                                .putExtra("track_url", str_track_url)
                        );
                    }
                });

                LL.addView(child);
            }
            LL.setVisibility(View.VISIBLE);
        } else {
            LL.setVisibility(View.GONE);
        }
    }

}