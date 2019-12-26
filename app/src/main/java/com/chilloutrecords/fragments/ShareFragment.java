package com.chilloutrecords.fragments;

import android.os.Bundle;
import android.view.InflateException;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.chilloutrecords.BuildConfig;
import com.chilloutrecords.R;

import static com.chilloutrecords.utils.StaticVariables.EXTRA_STRING;

public class ShareFragment extends Fragment {
    private View root_view;
    private String STR_SHARE_LINK = "";

    // OVERRIDE METHODS ============================================================================
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        if (root_view == null && getActivity() != null) {
            try {
                root_view = inflater.inflate(R.layout.frag_share, container, false);
                Bundle bundle = this.getArguments();
                if (bundle != null) {
                    STR_SHARE_LINK = bundle.getString(EXTRA_STRING);
                } else {
                    STR_SHARE_LINK = BuildConfig.DEFAULT_SHARE_LINK;
                }

            } catch (InflateException e) {
                e.printStackTrace();
            }
        } else {
            ((ViewGroup) container.getParent()).removeView(root_view);
        }
        return root_view;
    }


}
