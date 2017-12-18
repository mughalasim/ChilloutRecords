package asimmughal.chilloutrecords.main_pages.activities;

import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.gson.Gson;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import asimmughal.chilloutrecords.R;
import asimmughal.chilloutrecords.main_pages.adapters.artistAdapter;
import asimmughal.chilloutrecords.main_pages.models.ArtistModel;
import asimmughal.chilloutrecords.utils.Helpers;

public class ArtistActivity extends ParentActivity {
    public static ArrayList<ArtistModel> arrayList = new ArrayList<>();
    public static RecyclerView recyclerView;
    private LinearLayoutManager layoutManager;
    public static artistAdapter adapter;
    private String DB_REFERENCE = "Artists";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_artists);

        initialize(R.id.home, "Artists");

        findAllViews();

        helper.setProgressDialogMessage("Loading Artists.. Please wait");
        helper.progressDialog(true);

        fetchData(DB_REFERENCE);
    }

    private void findAllViews() {
        recyclerView = findViewById(R.id.list_item);
        adapter = new artistAdapter(ArtistActivity.this, arrayList);
        recyclerView.setAdapter(adapter);
        recyclerView.setHasFixedSize(true);
        layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);

    }

    public void fetchData(String dbRef) {
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference myRef = database.getReference(dbRef);

        myRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                helper.progressDialog(false);
                try {

                    Gson gson = new Gson();
                    JSONArray jsonArray = new JSONArray(gson.toJson(dataSnapshot.getValue()));
                    Helpers.LogThis("AFTER PARSING: " + jsonArray.toString());

                    int length = jsonArray.length();
                    if (length > 1) {
                        arrayList.clear();
                        for (int i = 0; i < length; i++) {
                            JSONObject object = jsonArray.getJSONObject(i);
                            ArtistModel artistModel = new ArtistModel();
                            artistModel.id = object.getString("id");
                            artistModel.info = object.getString("info");
                            artistModel.name = object.getString("name");
                            artistModel.ppic = object.getString("ppic");
                            artistModel.stage_name = object.getString("stage_name");
                            artistModel.year_since = object.getString("year_since");
                            arrayList.add(artistModel);
                        }
                        adapter.notifyDataSetChanged();
                    } else {
                        noLists();
                    }

                } catch (Exception e) {
                    noLists();
                    e.printStackTrace();
                    Helpers.LogThis(e.toString());
                }
            }

            @Override
            public void onCancelled(DatabaseError error) {
                helper.progressDialog(false);
                noLists();
                Log.w("HOMEPAGE: ", "Failed to read value.", error.toException());
            }
        });

    }

    public static void noLists() {
        arrayList.clear();
        recyclerView.setAdapter(adapter);

    }

}
