package asimmughal.chilloutrecords.main_pages.activities;

import android.os.Bundle;
import android.util.Log;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.List;
import java.util.Map;

import asimmughal.chilloutrecords.R;
import asimmughal.chilloutrecords.main_pages.models.ArtistModel;
import asimmughal.chilloutrecords.utils.Helpers;

public class ArtistDetailsActivity extends ParentActivity {

    private String
            STR_ID = "",
            STR_NAME = "";
    private String DB_REFERENCE = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_about_us);

        handleExtraBundles();

        initialize(R.id.about_us, STR_NAME);

        findAllViews();

        setNewDatabaseRef(DB_REFERENCE);

    }

    private void findAllViews() {


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

    public static void setNewDatabaseRef(String dbRef) {
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        Helpers.LogThis("DATABASE REF: " + dbRef);
        DatabaseReference myRef = database.getReference(dbRef);

        myRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                helper.progressDialog(false);
                Object value = dataSnapshot.getValue();
                try {

                    if (value instanceof List) {
                        List<Object> values = (List<Object>) value;
                        int length = values.size();
                        Helpers.LogThis(values.toString());
//                        arrayList.clear();
                        for (int i = 0; i < length; i++) {
                            getHashMap((Map<String, String>) values.get(i));
                        }

                    } else if (value instanceof String) {
                        String info = (String) value;
                        Helpers.LogThis(info);
                    }

                } catch (Exception e) {
                    e.printStackTrace();
                    Helpers.LogThis(e.toString());
                }
            }

            @Override
            public void onCancelled(DatabaseError error) {
                helper.progressDialog(false);
//                noLists();
                Log.w("HOMEPAGE: ", "Failed to read value.", error.toException());
            }
        });

    }

//    public static void noLists() {
//        arrayList.clear();
//        recyclerView.setAdapter(adapter);
//    }

    public static void getHashMap(Map<String, String> data) {
        if (data != null) {
            ArtistModel artistModel = new ArtistModel();
            for (Map.Entry<String, String> entry : data.entrySet()) {
                String[] split = entry.toString().split("=");
                String ListItem = (String) entry.getValue();

                Helpers.LogThis(split[0]);
                Helpers.LogThis(ListItem);

                switch (split[0]) {
                    case "id":
                        artistModel.id = ListItem;
                        break;

                    case "name":
                        artistModel.name = ListItem;
                        break;

                    case "stage_name":
                        artistModel.stage_name = ListItem;
                        break;

                    case "ppic":
                        artistModel.ppic = ListItem;
                        break;

                    case "info":
                        artistModel.info = ListItem;
                        break;

                    case "year_since":
                        artistModel.year_since = ListItem;
                        break;
                }
            }
//            arrayList.add(artistModel);
//            adapter.notifyDataSetChanged();

//        } else {
//            noLists();
        }
    }


}
