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
import com.chilloutrecords.adapters.ListingAdapter;
import com.chilloutrecords.interfaces.HomeInterface;
import com.chilloutrecords.models.ListingModel;
import com.chilloutrecords.models.VideoModel;
import com.chilloutrecords.utils.CustomRecyclerView;
import com.chilloutrecords.utils.Database;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

import static com.chilloutrecords.activities.ParentActivity.PAGE_TITLE_VIDEOS;
import static com.chilloutrecords.utils.StaticVariables.FIREBASE_DB;

public class VideosFragment extends Fragment {
    private View root_view;
    private CustomRecyclerView recycler_view;
    private ListingAdapter adapter;
    private ListingModel model;
    private ArrayList<ListingModel> models = new ArrayList<>();
    private TextView txt_no_results;

    // OVERRIDE METHODS ============================================================================
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        if (root_view == null && getActivity() != null) {
            try {

                root_view = inflater.inflate(R.layout.layout_custom_recycler, container, false);
                recycler_view = root_view.findViewById(R.id.recycler_view);
                txt_no_results = root_view.findViewById(R.id.txt_no_results);

                fetchVideos();

                recycler_view.setTextView(txt_no_results, getString(R.string.progress_loading));
                recycler_view.setHasFixedSize(true);
                recycler_view.setLayoutManager(new LinearLayoutManager(getContext()));

                adapter = new ListingAdapter(getActivity(), models, new HomeInterface() {
                    @Override
                    public void clicked(String page_title, String url) {
//                        ((ParentActivity) Objects.requireNonNull(getActivity())).loadFragment(new VideosFragment(), url, page_title);
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
    private void fetchVideos() {
        FIREBASE_DB.getReference().child(BuildConfig.DB_REF_VIDEOS).orderByChild("is_live").equalTo(true).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                for (DataSnapshot child : dataSnapshot.getChildren()) {
                    VideoModel video = child.getValue(VideoModel.class);

                    model = new ListingModel();
                    assert video != null;
                    model.txt = video.name;
                    model.img = video.art;
                    model.url = video.id;
                    model.page_title = PAGE_TITLE_VIDEOS + " / " + video.name;

                    models.add(model);

                }

                if (models.size() < 1) {
                    recycler_view.setTextView(txt_no_results, getString(R.string.error_no_videos));
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
