package com.chilloutrecords.activities;

import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.chilloutrecords.BuildConfig;
import com.chilloutrecords.R;
import com.chilloutrecords.models.UserModel;
import com.chilloutrecords.utils.Database;
import com.chilloutrecords.utils.StaticMethods;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;
import com.makeramen.roundedimageview.RoundedImageView;

import static com.chilloutrecords.utils.StaticVariables.EXTRA_STRING;
import static com.chilloutrecords.utils.StaticVariables.FIREBASE_DB;
import static com.chilloutrecords.utils.StaticVariables.FIREBASE_USER;

public class ProfileActivity extends AppCompatActivity {

    private String STR_ID = "";

    private TextView
            txt_page_title,
            txt_name,
            txt_stage_name,
            txt_email,
            txt_gender,
            txt_profile_visits,
            txt_member_since;

    private Toolbar toolbar;

    private FloatingActionButton
            btn_edit_picture,
            btn_edit_profile;

    private RoundedImageView
            img_profile;

    private DatabaseReference
            reference;
    private ValueEventListener
            listener;


    // OVERRIDE METHODS ============================================================================
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_profile);

        reference = FIREBASE_DB.getReference(BuildConfig.DB_REF_USERS);

        // FIND ALL VIEWS
        toolbar = findViewById(R.id.toolbar);
        txt_page_title = findViewById(R.id.txt_page_title);

        txt_name = findViewById(R.id.txt_name);
        txt_stage_name = findViewById(R.id.txt_stage_name);
        txt_email = findViewById(R.id.txt_email);
        txt_gender = findViewById(R.id.txt_gender);
        txt_profile_visits = findViewById(R.id.txt_profile_visits);
        txt_member_since = findViewById(R.id.txt_member_since);

        btn_edit_picture = findViewById(R.id.btn_edit_picture);
        btn_edit_profile = findViewById(R.id.btn_edit_profile);
        img_profile = findViewById(R.id.img_profile);

        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            STR_ID = bundle.getString(EXTRA_STRING);
            txt_page_title.setText("PROFILE");
            btn_edit_picture.hide();
            btn_edit_profile.hide();
        } else {
            STR_ID = FIREBASE_USER.getUid();
            txt_page_title.setText("MY PROFILE");
            btn_edit_picture.show();
            btn_edit_profile.show();
        }

        // SETUP TOOLBAR
        toolbar.setNavigationIcon(getResources().getDrawable(R.drawable.ic_back));
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });

        btn_edit_profile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });

        btn_edit_picture.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });

        img_profile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });

    }

    @Override
    protected void onStart() {
        setDataListener();
        super.onStart();
    }

    @Override
    protected void onDestroy() {
        if (listener != null) {
            reference.child(STR_ID).removeEventListener(listener);
        }
        super.onDestroy();
    }

    // CLASS METHODS ===============================================================================
    private void setDataListener() {
        listener = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                try {
                    loadProfile(dataSnapshot.getValue(UserModel.class));
                } catch (Exception e) {
                    StaticMethods.showToast(getString(R.string.error_500));
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Database.handleDatabaseError(databaseError);
            }
        };

        reference.child(STR_ID).addValueEventListener(listener);
    }

    private void loadProfile(UserModel model) {
        if (model != null) {


            // General info
            txt_name.setText(model.name);
            txt_stage_name.setText(getString(R.string.txt_stage_name).concat(model.stage_name));
            txt_email.setText(getString(R.string.txt_email).concat(model.email));
            txt_gender.setText(StaticMethods.getGender(model.gender));
            // stats
            txt_profile_visits.setText(getString(R.string.txt_profile_visits).concat(String.valueOf(model.profile_visits)));
            txt_member_since.setText(getString(R.string.txt_member_since).concat(StaticMethods.getDate(model.member_since_date)));
        }
    }

    public void ShareProfile(View view) {
        StaticMethods.showToast("Coming soon");
    }

}

