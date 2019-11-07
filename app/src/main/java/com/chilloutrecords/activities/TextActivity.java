package com.chilloutrecords.activities;

import android.os.Bundle;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.chilloutrecords.models.TextModel;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;

import com.chilloutrecords.R;

import static com.chilloutrecords.utils.StaticVariables.FIREBASE_DB;

public class TextActivity extends ParentToolBarActivity {

    private TextView
            txt_info;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_text);

        txt_info = findViewById(R.id.txt_info);

        setToolbar("");

        if (hasExtraBundles()) {
            fetchText();
        } else {
            txt_info.setText(getString(R.string.error_500));
            txt_page_title.setText(getString(R.string.error));
        }

    }

    private void fetchText() {
        FIREBASE_DB.getReference().child(STR_EXTRA).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                TextModel object = dataSnapshot.getValue(TextModel.class);
                assert object != null;
                txt_info.setText(object.info);
                txt_page_title.setText(object.title);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                txt_info.setText(getString(R.string.error_500));
                txt_page_title.setText(getString(R.string.error));
            }
        });

    }
}
