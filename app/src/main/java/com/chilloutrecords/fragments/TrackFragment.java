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
import com.chilloutrecords.adapters.TrackAdapter;
import com.chilloutrecords.interfaces.TrackInterface;
import com.chilloutrecords.models.TrackModel;
import com.chilloutrecords.utils.CustomRecyclerView;

import java.util.ArrayList;

import static com.chilloutrecords.utils.StaticVariables.EXTRA_DATA;
import static com.chilloutrecords.utils.StaticVariables.EXTRA_STRING;

public class TrackFragment extends Fragment {
    private View root_view;
    private CustomRecyclerView recycler_view;
    private TrackAdapter adapter;
    private RecyclerView.LayoutManager layout_manager;
    private ArrayList<String> STR_IDS = new ArrayList<>();
    private String STR_PATH = "";
    private TrackInterface listener;

    // OVERRIDE METHODS ============================================================================
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        if (root_view == null && getActivity() != null) {
            try {

                root_view = inflater.inflate(R.layout.layout_custom_recycler, container, false);
                recycler_view = root_view.findViewById(R.id.recycler_view);

                try {
                    listener = (TrackInterface) getActivity();
                } catch (ClassCastException e) {
                    throw new ClassCastException(getActivity().toString()
                            + " must implement TextClicked");
                }

                Bundle bundle = this.getArguments();
                if (bundle != null) {
                    STR_IDS = bundle.getStringArrayList(EXTRA_DATA);
                    STR_PATH = bundle.getString(EXTRA_STRING);
                }

                recycler_view.setHasFixedSize(true);

                layout_manager = new LinearLayoutManager(getContext());
                recycler_view.setLayoutManager(layout_manager);

                TextView txt_no_results = root_view.findViewById(R.id.txt_no_results);
                recycler_view.setTextView(txt_no_results, "No content to display");

                adapter = new TrackAdapter(getActivity(), STR_PATH, STR_IDS, new TrackInterface() {
                    @Override
                    public void success(TrackModel model, String db_path, String storage_path) {
                        listener.success(model, db_path, storage_path);
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

}
