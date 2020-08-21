package com.chilloutrecords.fragments;

import android.os.Bundle;
import android.view.InflateException;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.chilloutrecords.R;
import com.google.android.material.button.MaterialButton;

import static com.chilloutrecords.utils.StaticVariables.USER_MODEL;

public class PointsFragment extends Fragment {
    private View root_view;
    private TextView txt_message, txt_points;
    private MaterialButton btn_watch;

    // OVERRIDE METHODS ============================================================================
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        if (root_view == null && getActivity() != null) {
            try {
                root_view = inflater.inflate(R.layout.frag_points, container, false);
                txt_message = root_view.findViewById(R.id.txt_message);
                txt_points = root_view.findViewById(R.id.txt_points);
                btn_watch = root_view.findViewById(R.id.btn_watch);

                txt_points.setText(String.valueOf(USER_MODEL.points));

                txt_message.setText("5 hours and 20 Mins to the next available ad");
                btn_watch.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        // show the video here and add the points once the ad ends

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

    // CLASS METHODS ===============================================================================

}
