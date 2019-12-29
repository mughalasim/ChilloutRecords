package com.chilloutrecords.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.view.InflateException;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

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

                TextView txt_link = root_view.findViewById(R.id.txt_link);
                txt_link.setText(STR_SHARE_LINK);

                root_view.findViewById(R.id.btn_share_link).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Intent share = new Intent(Intent.ACTION_SEND);
                        share.setType("text/plain");
                        share.putExtra(Intent.EXTRA_TEXT, STR_SHARE_LINK);
                        startActivity(Intent.createChooser(share, "Share " + getString(R.string.app_name)));
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

}
