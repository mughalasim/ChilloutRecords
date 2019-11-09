package com.chilloutrecords.activities;

import android.os.Bundle;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.chilloutrecords.adapters.ListingAdapter;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.gson.Gson;

import org.json.JSONArray;
import org.json.JSONObject;

import com.chilloutrecords.R;
//import com.chilloutrecords.models.GeneralModel;


public class GeneralListActivity extends ParentActivity {
//    public ArrayList<GeneralModel> arrayList = new ArrayList<>();
    public RecyclerView recyclerView;
    public ListingAdapter adapter;
    private String DB_REFERENCE = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_general_list);

        handleExtraBundles();

//        initialize(R.id.home, DB_REFERENCE);

        findAllViews();


    }

    private void handleExtraBundles() {
        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            DB_REFERENCE = extras.getString("DB_REFERENCE");
        } else {
            finish();
//            dialogs.ToastMessage(GeneralListActivity.this, getString(R.string.error_500));
        }
    }

    private void findAllViews() {
        recyclerView = findViewById(R.id.list_item);
//        adapter = new ListingAdapter(GeneralListActivity.this, arrayList, DB_REFERENCE);
        recyclerView.setAdapter(adapter);
        recyclerView.setHasFixedSize(true);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);

    }

    public void fetchArtists() {
//        dialogs.setTxt_progress_message("Loading " + DB_REFERENCE + ".. Please wait");
//        dialogs.progressDialog(true);
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference myRef = database.getReference(DB_REFERENCE);

        myRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
//                dialogs.progressDialog(false);
                try {

                    Gson gson = new Gson();
                    JSONArray jsonArray = new JSONArray(gson.toJson(dataSnapshot.getValue()));
//                    DialogMethods.LogThis("AFTER PARSING: " + jsonArray.toString());

                    int length = jsonArray.length();
                    if (length > 0) {
//                        arrayList.clear();
                        for (int i = 0; i < length; i++) {
                            JSONObject object = jsonArray.getJSONObject(i);
//                            GeneralModel generalModel = new GeneralModel();
//                            generalModel.artist_id = object.getString("id");
//                            generalModel.artist_info = object.getString("info");
//                            generalModel.artist_name = object.getString("name");
//                            generalModel.artist_ppic = object.getString("ppic");
//                            generalModel.artist_stage_name = object.getString("stage_name");
//                            generalModel.artist_year_since = object.getString("year_since");
//                            arrayList.add(generalModel);
                        }
                        adapter.notifyDataSetChanged();
                    } else {
                        noLists();
                    }

                } catch (Exception e) {
                    noLists();
                    e.printStackTrace();
//                    DialogMethods.LogThis(e.toString());
                }
            }

            @Override
            public void onCancelled(DatabaseError error) {
//                dialogs.progressDialog(false);
                noLists();
//                DialogMethods.LogThis(error.toString());
            }
        });

    }

    public void fetchVideos() {
//        dialogs.setTxt_progress_message("Loading " + DB_REFERENCE + ".. Please wait");
//        dialogs.progressDialog(true);
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference myRef = database.getReference(DB_REFERENCE);

        myRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
//                dialogs.progressDialog(false);
                try {

                    Gson gson = new Gson();
                    JSONArray jsonArray = new JSONArray(gson.toJson(dataSnapshot.getValue()));
//                    DialogMethods.LogThis("AFTER PARSING: " + jsonArray.toString());

                    int length = jsonArray.length();
                    if (length > 0) {
//                        arrayList.clear();
                        for (int i = 0; i < length; i++) {
                            JSONObject object = jsonArray.getJSONObject(i);
//                            GeneralModel generalModel = new GeneralModel();
//                            generalModel.video_desc = object.getString("info");
//                            generalModel.video_name = object.getString("name");
//                            generalModel.video_ppic = object.getString("ppic");
//                            generalModel.video_url = object.getString("video_url");
//                            arrayList.add(generalModel);
                        }
                        adapter.notifyDataSetChanged();
                    } else {
                        noLists();
                    }

                } catch (Exception e) {
                    noLists();
                    e.printStackTrace();
//                    DialogMethods.LogThis(e.toString());
                }
            }

            @Override
            public void onCancelled(DatabaseError error) {
//                dialogs.progressDialog(false);
                noLists();
//                DialogMethods.LogThis(error.toString());
            }
        });

    }

    public void noLists() {
//        arrayList.clear();
        recyclerView.setAdapter(adapter);
        adapter.notifyDataSetChanged();
    }

}
