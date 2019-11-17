package com.chilloutrecords.activities;

import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.viewpager.widget.ViewPager;

import com.bumptech.glide.Glide;
import com.chilloutrecords.BuildConfig;
import com.chilloutrecords.R;
import com.chilloutrecords.adapters.ViewPagerAdapter;
import com.chilloutrecords.fragments.TrackFragment;
import com.chilloutrecords.interfaces.UrlInterface;
import com.chilloutrecords.models.TrackModel;
import com.chilloutrecords.models.UserModel;
import com.chilloutrecords.utils.Database;
import com.chilloutrecords.utils.StaticMethods;
import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.tabs.TabLayout;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;
import com.makeramen.roundedimageview.RoundedImageView;

import java.util.ArrayList;

import static com.chilloutrecords.utils.StaticVariables.EXTRA_DATA;
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

    private TabLayout
            tab_layout;
    private ViewPager
            view_pager;
    private int[] fragment_title_list = {
            R.string.nav_singles,
            R.string.nav_collections
    };
    private final String[] fragment_extras = {
            BuildConfig.DB_REF_SINGLES,
            BuildConfig.DB_REF_COLLECTIONS,
    };
    private Fragment[] fragment_list = {
            new TrackFragment(),
            new TrackFragment()
    };
    private ArrayList<ArrayList<String>> extra_array_list = new ArrayList<>();

    private LinearLayout ll_bottom_sheet;
    private BottomSheetBehavior bs_behaviour; // lol BS behavior


    // OVERRIDE METHODS ============================================================================
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_profile);

        reference = FIREBASE_DB.getReference(BuildConfig.DB_REF_USERS);

        // FIND ALL VIEWS
        toolbar = findViewById(R.id.toolbar);
        txt_page_title = findViewById(R.id.txt_page_title);
        ll_bottom_sheet = findViewById(R.id.ll_bottom_sheet);
        bs_behaviour = BottomSheetBehavior.from(ll_bottom_sheet);
        bs_behaviour.setState(BottomSheetBehavior.STATE_HIDDEN);

        txt_name = findViewById(R.id.txt_name);
        txt_stage_name = findViewById(R.id.txt_stage_name);
        txt_email = findViewById(R.id.txt_email);
        txt_gender = findViewById(R.id.txt_gender);
        txt_profile_visits = findViewById(R.id.txt_profile_visits);
        txt_member_since = findViewById(R.id.txt_member_since);

        img_profile = findViewById(R.id.img_profile);
        btn_edit_picture = findViewById(R.id.btn_edit_picture);
        btn_edit_profile = findViewById(R.id.btn_edit_profile);

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

        view_pager = findViewById(R.id.view_pager);
        tab_layout = findViewById(R.id.tabs);

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
            // Image
            Database.getFileUrl(BuildConfig.STORAGE_IMAGES, model.p_pic, new UrlInterface() {
                @Override
                public void success(String url) {
                    Glide.with(ProfileActivity.this).load(url).into(img_profile);
                }
            });
            // General info
            txt_name.setText(model.name);
            txt_stage_name.setText(getString(R.string.txt_stage_name).concat(model.stage_name));
            txt_email.setText(getString(R.string.txt_email).concat(model.email));
            txt_gender.setText(StaticMethods.getGender(model.gender));
            // stats
            txt_profile_visits.setText(getString(R.string.txt_profile_visits).concat(String.valueOf(model.profile_visits)));
            txt_member_since.setText(getString(R.string.txt_member_since).concat(StaticMethods.getDate(model.member_since_date)));
            // Music content
            if (model.is_artist && model.music != null) {
                tab_layout.setVisibility(View.VISIBLE);
                view_pager.setVisibility(View.VISIBLE);
                extra_array_list.add(model.music.singles);
                extra_array_list.add(model.music.collections);
                setupViewPager(fragment_list, fragment_title_list, fragment_extras, extra_array_list);
            } else {
                tab_layout.setVisibility(View.GONE);
                view_pager.setVisibility(View.GONE);
            }
        }
    }

    public void ShareProfile(View view) {
        StaticMethods.showToast("Coming soon");
    }

    public void setupViewPager(Fragment[] fragment_list, int[] fragment_title_list, String[] extra_string, ArrayList<ArrayList<String>> extra_array_list) {

        ViewPagerAdapter adapter = new ViewPagerAdapter(getSupportFragmentManager());

        for (int i = 0; i < fragment_title_list.length; i++) {
            Bundle bundle = new Bundle();
            bundle.putString(EXTRA_STRING, extra_string[i]);
            bundle.putStringArrayList(EXTRA_DATA, extra_array_list.get(i));

            Fragment fragment = fragment_list[i];
            fragment.setArguments(bundle);
            adapter.addFragment(fragment, getString(fragment_title_list[i]));
        }

        view_pager.setAdapter(adapter);

        tab_layout.setupWithViewPager(view_pager, true);

    }

    public void showPlayer(TrackModel model) {
        bs_behaviour.setState(BottomSheetBehavior.STATE_HALF_EXPANDED);

    }

}

