package asimmughal.chilloutrecords.main_pages.activities;

import android.os.Bundle;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Map;

import asimmughal.chilloutrecords.R;
import asimmughal.chilloutrecords.main_pages.adapters.listAdapter;
import asimmughal.chilloutrecords.utils.Helpers;

public class ArtistActivity extends ParentActivity {
    public static ArrayList<String> arrayList = new ArrayList<>();
    public static RecyclerView recyclerView;
    private LinearLayoutManager layoutManager;
    public static listAdapter adapter;
    private String DB_REFERENCE = "Artists";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_artists);

        initialize(R.id.home, "");

        findAllViews();

        helper.setProgressDialogMessage("Loading Artists.. Please wait");
        helper.progressDialog(true);

       setNewDatabaseRef(DB_REFERENCE);
    }

    private void findAllViews() {
        recyclerView = (RecyclerView) findViewById(R.id.list_item);

        adapter = new listAdapter(ArtistActivity.this, arrayList);
        recyclerView.setAdapter(adapter);
        recyclerView.setHasFixedSize(true);
        layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);
        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(recyclerView.getContext(),
                layoutManager.getOrientation());
        recyclerView.addItemDecoration(dividerItemDecoration);

    }

    public static void setNewDatabaseRef(String dbRef){
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference myRef = database.getReference(dbRef);

        myRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                helper.progressDialog(false);
                getHashMap((Map<String, String>) dataSnapshot.getValue());
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
        arrayList.add("");
        recyclerView.setAdapter(adapter);
    }

    public static void getHashMap(Map<String, String> users) {
        // Iterate through each element, ignoring their ID
        if (users != null) {
            arrayList.clear();
            for (Map.Entry<String, String> entry : users.entrySet()) {
                String ListItem = (String) entry.getValue();
                Helpers.LogThis(ListItem);
                arrayList.add(ListItem);
            }
            adapter.notifyDataSetChanged();

        } else {
            noLists();
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        updateDrawer();
    }


}
