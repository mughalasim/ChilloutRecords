package asimmughal.chilloutrecords.main_pages.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;

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

public class HomeActivity extends ParentActivity {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        initialize(R.id.home, "");

        findAllViews();


    }

    private void findAllViews() {


    }

    public void ARTISTS(View view) {
        startActivity(new Intent(HomeActivity.this, ArtistActivity.class));
    }

    public void VIDEOS(View view) {

    }

    public void EQUIPMENTS(View view) {

    }

    public void UPCOMING(View view) {

    }
}
