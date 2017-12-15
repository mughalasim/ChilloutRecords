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

        setNewDatabaseRef(DB_REFERENCE);
    }

    private void findAllViews() {
        recyclerView = findViewById(R.id.list_item);
        adapter = new artistAdapter(ArtistActivity.this, arrayList);
        recyclerView.setAdapter(adapter);
        recyclerView.setHasFixedSize(true);
        layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);

    }

    public static void setNewDatabaseRef(String dbRef) {
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference myRef = database.getReference(dbRef);

        myRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                try {
                    helper.progressDialog(false);
                    Object value = dataSnapshot.getValue();
                    if (value instanceof List) {
                        List<Object> values = (List<Object>) value;
                        int length = values.size();
                        Helpers.LogThis(values.toString());
                        arrayList.clear();
                        for (int i = 0; i < length; i++) {
                            getHashMap((Map<String, String>) values.get(i));
                        }
                    }
                } catch (Exception e) {
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

    public static void getHashMap(Map<String, String> data) {
        if (data != null) {
            ArtistModel artistModel = new ArtistModel();
            for (Map.Entry<String, String> entry : data.entrySet()) {
                String ListItem = entry.getValue();
                if (entry.toString().contains("id")) {
                    artistModel.id = ListItem;
                } else if (entry.toString().contains("name")) {
                    artistModel.name = ListItem;
                } else if (entry.toString().contains("stage_name")) {
                    artistModel.stage_name = ListItem;
                } else if (entry.toString().contains("ppic")) {
                    artistModel.ppic = ListItem;
                } else if (entry.toString().contains("info")) {
                    artistModel.info = ListItem;
                } else if (entry.toString().contains("year_since")) {
                    artistModel.year_since = ListItem;
                }
            }
            arrayList.add(artistModel);
            adapter.notifyDataSetChanged();

        } else {
            noLists();
        }
    }


}
