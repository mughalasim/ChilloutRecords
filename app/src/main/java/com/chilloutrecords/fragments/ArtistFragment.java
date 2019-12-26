package com.chilloutrecords.fragments;

import android.os.Bundle;
import android.view.InflateException;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.chilloutrecords.BuildConfig;
import com.chilloutrecords.R;
import com.chilloutrecords.activities.ParentActivity;
import com.chilloutrecords.adapters.HomeAdapter;
import com.chilloutrecords.interfaces.HomeInterface;
import com.chilloutrecords.models.HomeModel;
import com.chilloutrecords.models.NavigationModel;
import com.chilloutrecords.models.UserModel;
import com.chilloutrecords.utils.CustomRecyclerView;
import com.chilloutrecords.utils.Database;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Objects;

import static com.chilloutrecords.activities.ParentActivity.PAGE_TITLE_ARTISTS;
import static com.chilloutrecords.utils.StaticVariables.FIREBASE_DB;

public class ArtistFragment extends Fragment {
    private View root_view;
    private CustomRecyclerView recycler_view;
    private HomeAdapter adapter;
    private HomeModel model;
    private ArrayList<HomeModel> models = new ArrayList<>();
    private TextView txt_no_results;

    // OVERRIDE METHODS ============================================================================
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        if (root_view == null && getActivity() != null) {
            try {

                root_view = inflater.inflate(R.layout.layout_custom_recycler, container, false);
                recycler_view = root_view.findViewById(R.id.recycler_view);
                txt_no_results = root_view.findViewById(R.id.txt_no_results);

                fetchArtists();

                recycler_view.setTextView(txt_no_results, getString(R.string.progress_loading));
                recycler_view.setHasFixedSize(true);
                recycler_view.setLayoutManager(new LinearLayoutManager(getContext()));

                adapter = new HomeAdapter(getActivity(), models, new HomeInterface() {
                    @Override
                    public void clicked(String page_title, String url) {
                        ((ParentActivity) Objects.requireNonNull(getActivity())).loadFragment(new NavigationModel(new ProfileFragment(), page_title, url, null, true));
                    }
                });

                recycler_view.setAdapter(adapter);

            } catch (InflateException e) {
                e.printStackTrace();
            }
        } else {
            ((ViewGroup) container.getParent()).removeView(root_view);
        }
        return root_view;
    }

    // CLASS METHODS ===============================================================================
    private void fetchArtists() {
        FIREBASE_DB.getReference().child(BuildConfig.DB_REF_USERS).orderByChild("is_artist").equalTo(true).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                for (DataSnapshot child : dataSnapshot.getChildren()) {
                    UserModel artist = child.getValue(UserModel.class);

                    model = new HomeModel();
                    assert artist != null;
                    model.txt = artist.stage_name;
                    model.img = artist.p_pic;
                    model.url = artist.id;
                    model.page_title = PAGE_TITLE_ARTISTS + " / " + artist.name;

                    models.add(model);

                }

                if (models.size() < 1) {
                    recycler_view.setTextView(txt_no_results, getString(R.string.error_no_artists));
                }

                adapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Database.handleDatabaseError(databaseError);
                recycler_view.setTextView(txt_no_results, getString(R.string.error_500));
            }
        });
    }

}
