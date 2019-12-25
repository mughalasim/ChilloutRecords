package com.chilloutrecords.fragments;

import android.os.Bundle;
import android.view.InflateException;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.viewpager.widget.ViewPager;

import com.bumptech.glide.Glide;
import com.chilloutrecords.BuildConfig;
import com.chilloutrecords.R;
import com.chilloutrecords.activities.ParentActivity;
import com.chilloutrecords.adapters.ViewPagerAdapter;
import com.chilloutrecords.interfaces.UrlInterface;
import com.chilloutrecords.models.NavigationModel;
import com.chilloutrecords.models.UserModel;
import com.chilloutrecords.utils.Database;
import com.chilloutrecords.utils.DialogMethods;
import com.chilloutrecords.utils.StaticMethods;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.tabs.TabLayout;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;
import com.makeramen.roundedimageview.RoundedImageView;

import java.util.ArrayList;
import java.util.Objects;

import static com.chilloutrecords.activities.ParentActivity.PAGE_TITLE_PROFILE_EDIT;
import static com.chilloutrecords.activities.ParentActivity.user_model;
import static com.chilloutrecords.utils.StaticVariables.EXTRA_DATA;
import static com.chilloutrecords.utils.StaticVariables.EXTRA_STRING;
import static com.chilloutrecords.utils.StaticVariables.FIREBASE_DB;
import static com.chilloutrecords.utils.StaticVariables.FIREBASE_USER;

public class ProfileFragment extends Fragment {
    private View root_view;

    private String STR_ID = "";
    private final String TAG_LOG = "PROFILE";
    private DialogMethods dialogs;

    private String IMG_PROFILE_URL = "";
    private TextView
            txt_name,
            txt_stage_name,
            txt_info,
            txt_profile_visits,
            txt_member_since;

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


    // OVERRIDE METHODS ============================================================================
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        if (root_view == null && getActivity() != null) {
            try {

                root_view = inflater.inflate(R.layout.frag_profile, container, false);

                reference = FIREBASE_DB.getReference(BuildConfig.DB_REF_USERS);
                dialogs = new DialogMethods(getActivity());

                // FIND ALL VIEWS
                txt_name = root_view.findViewById(R.id.txt_name);
                txt_stage_name = root_view.findViewById(R.id.txt_stage_name);
                txt_info = root_view.findViewById(R.id.txt_info);
                txt_profile_visits = root_view.findViewById(R.id.txt_profile_visits);
                txt_member_since = root_view.findViewById(R.id.txt_member_since);

                img_profile = root_view.findViewById(R.id.img_profile);
                btn_edit_picture = root_view.findViewById(R.id.btn_edit_picture);
                btn_edit_profile = root_view.findViewById(R.id.btn_edit_profile);

                Bundle bundle = this.getArguments();
                if (bundle != null) {
                    STR_ID = bundle.getString(EXTRA_STRING);
                    btn_edit_picture.hide();
                    btn_edit_profile.hide();
                } else {
                    STR_ID = FIREBASE_USER.getUid();
                    btn_edit_picture.show();
                    btn_edit_profile.show();
                }

                view_pager = root_view.findViewById(R.id.view_pager);
                tab_layout = root_view.findViewById(R.id.tabs);

                btn_edit_profile.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        ((ParentActivity) Objects.requireNonNull(getActivity())).loadFragment(new NavigationModel(new RegisterFragment(), PAGE_TITLE_PROFILE_EDIT, ""), true);
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
                        dialogs.setDialogImagePreview(IMG_PROFILE_URL);
                    }
                });


            } catch (InflateException e) {
                e.printStackTrace();
            }
        } else {
            ((ViewGroup) container.getParent()).removeView(root_view);
        }
        return root_view;
    }

    @Override
    public void onStart() {
        setDataListener();
        super.onStart();
    }

    @Override
    public void onDestroy() {
        if (listener != null) {
            reference.child(STR_ID).removeEventListener(listener);
            user_model = null;
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
            user_model = model;
            // Image
            Database.getFileUrl(BuildConfig.STORAGE_IMAGES, model.p_pic, new UrlInterface() {
                @Override
                public void completed(Boolean success, String url) {
                    if (success) {
                        Glide.with(getActivity()).load(url).into(img_profile);
                        IMG_PROFILE_URL = url;
                    }
                }
            });
            // General info
            txt_name.setText(model.name);
            txt_stage_name.setText(getString(R.string.txt_stage_name).concat(model.stage_name));
            txt_info.setText(model.info);
            // stats
            txt_profile_visits.setText(getString(R.string.txt_profile_visits).concat(String.valueOf(model.profile_visits)));
            txt_member_since.setText(getString(R.string.txt_member_since).concat(StaticMethods.getDate(model.member_since_date)));
            // Music content
            if (model.is_artist && model.music != null) {
                tab_layout.setVisibility(View.VISIBLE);
                view_pager.setVisibility(View.VISIBLE);
                extra_array_list.clear();
                extra_array_list.add(model.music.singles);
                extra_array_list.add(model.music.collections);
                setupViewPager(fragment_list, fragment_title_list, fragment_extras, extra_array_list);
            } else {
                tab_layout.setVisibility(View.GONE);
                view_pager.setVisibility(View.GONE);
            }
        }
    }

    private void setupViewPager(Fragment[] fragment_list, int[] fragment_title_list, String[] extra_string, ArrayList<ArrayList<String>> extra_array_list) {

        ViewPagerAdapter adapter = new ViewPagerAdapter(getFragmentManager());

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

}
