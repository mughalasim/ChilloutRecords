package com.chilloutrecords.fragments;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.InflateException;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.AppCompatImageView;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.chilloutrecords.BuildConfig;
import com.chilloutrecords.R;
import com.chilloutrecords.activities.ParentActivity;
import com.chilloutrecords.activities.ProfileActivity;
import com.chilloutrecords.adapters.ListingAdapter;
import com.chilloutrecords.interfaces.UrlInterface;
import com.chilloutrecords.models.ListingModel;
import com.chilloutrecords.models.UserModel;
import com.chilloutrecords.models.VideoModel;
import com.chilloutrecords.utils.CustomRecyclerView;
import com.chilloutrecords.utils.Database;
import com.chilloutrecords.utils.StaticMethods;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Objects;

import static com.chilloutrecords.utils.StaticVariables.EXTRA_STRING;
import static com.chilloutrecords.utils.StaticVariables.FIREBASE_DB;

public class HomeFragment extends Fragment {
    private View root_view;
    private CustomRecyclerView recycler_view;
    private ListingAdapter adapter;
    private RecyclerView.LayoutManager layout_manager;
    private ListingModel model;
    private ArrayList<ListingModel> models = new ArrayList<>();
    private String PATH_URL = "";
    private TextView
            txt_page_title,
            txt_no_results;
    private AppCompatImageView
            btn_back;

    // OVERRIDE METHODS ============================================================================
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        if (root_view == null && getActivity() != null) {
            try {

                root_view = inflater.inflate(R.layout.frag_home, container, false);
                txt_page_title = root_view.findViewById(R.id.txt_page_title);
                recycler_view = root_view.findViewById(R.id.recycler_view);
                btn_back = root_view.findViewById(R.id.btn_back);

                Bundle bundle = this.getArguments();
                if (bundle != null) {
                    PATH_URL = bundle.getString(EXTRA_STRING);
                    if (PATH_URL != null && PATH_URL.equals(BuildConfig.DB_REF_USERS)) {
                        txt_page_title.setText(getString(R.string.nav_artists));
                        btn_back.setVisibility(View.VISIBLE);
                        fetchArtists();
                    } else if (PATH_URL != null && PATH_URL.equals(BuildConfig.DB_REF_VIDEOS)) {
                        txt_page_title.setText(getString(R.string.nav_videos));
                        btn_back.setVisibility(View.VISIBLE);
                        fetchVideos();
                    }
                } else {
                    models.clear();

                    model = new ListingModel();
                    model.txt = "Artists";
                    model.img = "home_artists.jpg";
                    model.url = BuildConfig.DB_REF_USERS;
                    models.add(model);

                    model = new ListingModel();
                    model.txt = "Videos";
                    model.img = "home_videos.jpg";
                    model.url = BuildConfig.DB_REF_VIDEOS;
                    models.add(model);

                    txt_page_title.setText(getString(R.string.nav_home));
                    btn_back.setVisibility(View.GONE);
                }

                recycler_view.setHasFixedSize(true);

                layout_manager = new LinearLayoutManager(getContext());
                recycler_view.setLayoutManager(layout_manager);

                txt_no_results = root_view.findViewById(R.id.txt_no_results);
                recycler_view.setTextView(txt_no_results, "No information to display at this time");

                adapter = new ListingAdapter(getActivity(), models, new UrlInterface() {
                    @Override
                    public void success(String url) {
                        if (btn_back.getVisibility() == View.GONE) {
                            ((ParentActivity) Objects.requireNonNull(getActivity())).loadFragment(new HomeFragment(), 0, url);
                        } else if (PATH_URL.equals(BuildConfig.DB_REF_USERS)) {
                            // TODO - open profile Activity with bundle extra with the USER ID
                            getActivity().startActivity(new Intent(getActivity(), ProfileActivity.class));
                        } else if (PATH_URL.equals(BuildConfig.DB_REF_VIDEOS)) {
                            // TODO - open Video with bundle extra with the the VIDEO ID
                        }
                    }

                    @Override
                    public void failed() {

                    }
                });

                recycler_view.setAdapter(adapter);

                btn_back.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        ((ParentActivity) Objects.requireNonNull(getActivity())).loadFragment(new HomeFragment(), 0, "");
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

    private void fetchArtists() {
        FIREBASE_DB.getReference().child(BuildConfig.DB_REF_USERS).orderByChild("is_artist").equalTo(true).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                for (DataSnapshot child : dataSnapshot.getChildren()) {
                    UserModel artist = child.getValue(UserModel.class);

                    model = new ListingModel();
                    model.txt = artist.stage_name;
                    model.img = artist.p_pic;
                    model.url = artist.id;

                    models.add(model);

                }

                adapter.notifyDataSetChanged();

                StaticMethods.logg(PATH_URL, dataSnapshot.toString());
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Database.handleDatabaseError(databaseError);
            }
        });
    }


    private void fetchVideos() {
        FIREBASE_DB.getReference().child(BuildConfig.DB_REF_VIDEOS).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                for (DataSnapshot child : dataSnapshot.getChildren()) {
                    VideoModel video = child.getValue(VideoModel.class);

                    model = new ListingModel();
                    model.txt = video.name;
                    model.img = video.art;
                    model.url = video.url;

                    models.add(model);

                }

                adapter.notifyDataSetChanged();

                StaticMethods.logg(PATH_URL, dataSnapshot.toString());
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Database.handleDatabaseError(databaseError);
            }
        });
    }

}
