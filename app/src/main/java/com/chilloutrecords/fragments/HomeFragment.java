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
import androidx.recyclerview.widget.RecyclerView;

import com.chilloutrecords.R;
import com.chilloutrecords.adapters.ListingAdapter;
import com.chilloutrecords.models.ListingModel;
import com.chilloutrecords.utils.CustomRecyclerView;
import com.chilloutrecords.utils.StaticMethods;

import java.util.ArrayList;

public class HomeFragment extends Fragment {
    private View root_view;
    private CustomRecyclerView recycler_view;
    private ListingAdapter adapter;
    private RecyclerView.LayoutManager layout_manager;
    private ListingModel model;
    private ArrayList<ListingModel> models = new ArrayList<>();
    private final String TAG_LOG = "HOME";

    // OVERRIDE METHODS ============================================================================
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        if (root_view == null && getActivity() != null) {
            try {
                root_view = inflater.inflate(R.layout.layout_custom_recycler, container, false);

                recycler_view = root_view.findViewById(R.id.recycler_view);
                recycler_view.setHasFixedSize(true);

                layout_manager = new LinearLayoutManager(getContext());
                recycler_view.setLayoutManager(layout_manager);

                TextView emptyView = root_view.findViewById(R.id.txt_no_results);
                recycler_view.setTextView(emptyView, "No selectable data");

                for (int i = 0; i < 3; i++) {
                    model = new ListingModel();
                    model.txt = "TEST " + (i + 1);
                    model.img = "gs://chillout-records.appspot.com/Images/home_artists.JPG";
                    model.url = "Videos";
                    models.add(model);
                }

                adapter = new ListingAdapter(getActivity(), models);
                recycler_view.setAdapter(adapter);

                StaticMethods.logg(TAG_LOG, "Size: " + adapter.getItemCount());

            } catch (InflateException e) {
                e.printStackTrace();
            }
        } else {
            ((ViewGroup) container.getParent()).removeView(root_view);
        }
        return root_view;
    }

    private void loadList(String path){

    }

}
