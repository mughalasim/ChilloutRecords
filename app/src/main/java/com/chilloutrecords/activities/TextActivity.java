package com.chilloutrecords.activities;

import android.app.Dialog;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.bumptech.glide.Glide;
import com.chilloutrecords.BuildConfig;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.gson.Gson;

import org.json.JSONObject;

import com.chilloutrecords.R;
import com.chilloutrecords.utils.SharedPrefs;

import static com.chilloutrecords.utils.StaticVariables.EXTRA_STRING;
import static com.chilloutrecords.utils.StaticVariables.FIREBASE_DB;

public class TextActivity extends AppCompatActivity {
    private Toolbar toolbar;
    private TextView
            txt_info,
            txt_page_title;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_about_us);

        txt_page_title = findViewById(R.id.txt_page_title);
        txt_info = findViewById(R.id.txt_info);

        handleExtraBundles();

    }

    private void handleExtraBundles() {
        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            fetchText(extras.getString(EXTRA_STRING));
            txt_page_title.setText(extras.getString(EXTRA_STRING));
        }
    }

    private void fetchText(String reference) {
        FIREBASE_DB.getReference().child(reference + "/info").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                txt_info.setText(dataSnapshot.toString());
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                txt_info.setText(getString(R.string.error_500));
            }
        });

    }

}
