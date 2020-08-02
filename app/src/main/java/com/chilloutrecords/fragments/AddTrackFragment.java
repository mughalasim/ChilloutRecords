package com.chilloutrecords.fragments;

import android.os.Bundle;
import android.view.InflateException;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.chilloutrecords.R;
import com.chilloutrecords.utils.DialogMethods;
import com.chilloutrecords.utils.StaticMethods;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.makeramen.roundedimageview.RoundedImageView;

public class AddTrackFragment extends Fragment {
    private View root_view;
    private DialogMethods dialogs;
    private final String TAG_LOG = "ADD TRACK";
    private final String STR_AUDIO_LOCAL_PATH = "";
    private final String STR_IMAGE_LOCAL_PATH = "";

    private TextView txt_file_name;
    private FloatingActionButton btn_search_file;
    private MaterialButton btn_upload;
    private RoundedImageView img_track;

    private TextInputEditText
            et_track_name,
            et_collection_name,
            et_lyrics,
            et_release_date;

    private TextInputLayout
            etl_track_name,
            etl_collection_name,
            etl_lyrics,
            etl_release_date;

    private Spinner spinner_track_type;

    // OVERRIDE METHODS ============================================================================
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        if (root_view == null && getActivity() != null) {
            try {
                root_view = inflater.inflate(R.layout.frag_add_track, container, false);

                dialogs = new DialogMethods(getActivity());

                spinner_track_type = root_view.findViewById(R.id.spinner_track_type);
                img_track = root_view.findViewById(R.id.img_track);
                txt_file_name = root_view.findViewById(R.id.txt_file_name);
                btn_search_file = root_view.findViewById(R.id.btn_search_file);
                btn_upload = root_view.findViewById(R.id.btn_upload);

                et_track_name = root_view.findViewById(R.id.et_track_name);
                et_collection_name = root_view.findViewById(R.id.et_collection_name);
                et_lyrics = root_view.findViewById(R.id.et_lyrics);
                et_release_date = root_view.findViewById(R.id.et_release_date);
                etl_track_name = root_view.findViewById(R.id.etl_track_name);
                etl_collection_name = root_view.findViewById(R.id.etl_collection_name);
                etl_lyrics = root_view.findViewById(R.id.etl_lyrics);
                etl_release_date = root_view.findViewById(R.id.etl_release_date);

                updateSpinnerSelection();

                spinner_track_type.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                    @Override
                    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                        updateSpinnerSelection();
                    }

                    @Override
                    public void onNothingSelected(AdapterView<?> parent) {

                    }
                });

                btn_upload.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        if(spinner_track_type.getSelectedItemPosition() == 0){
                          dialogs.setDialogCancel(getString(R.string.txt_alert), "Please select a track type from the drop down");

                        } else if (spinner_track_type.getSelectedItemPosition()>1 && StaticMethods.validateEmptyEditText(et_collection_name, etl_collection_name, getString(R.string.error_field_required))){
                           // Upload collection
                            validateRemainingFields();

                        } else {
                            // Upload single
                            validateRemainingFields();
                        }
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

    private void validateRemainingFields() {
        if (
                StaticMethods.validateEmptyEditText(et_track_name, etl_track_name, getString(R.string.error_field_required)) &&
                        StaticMethods.validateEmptyEditText(et_release_date, etl_release_date, getString(R.string.error_field_required)) &&
                        StaticMethods.validateEmptyEditText(et_lyrics, etl_lyrics, getString(R.string.error_field_required))
//                        STR_AUDIO_LOCAL_PATH != "" &&
//                        STR_IMAGE_LOCAL_PATH!= ""
        ) {

        }
    }

    private void updateSpinnerSelection() {
        if (spinner_track_type.getSelectedItemPosition() > 1) {
            etl_collection_name.setVisibility(View.VISIBLE);
        } else {
            etl_collection_name.setVisibility(View.GONE);
        }
    }

}
