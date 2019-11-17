package com.chilloutrecords.fragments;

import android.os.Bundle;
import android.text.Html;
import android.view.InflateException;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.chilloutrecords.R;
import com.chilloutrecords.models.TextModel;
import com.chilloutrecords.utils.Database;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;

import static com.chilloutrecords.utils.StaticVariables.EXTRA_STRING;
import static com.chilloutrecords.utils.StaticVariables.FIREBASE_DB;

public class MusicPlayerFragment extends Fragment {
    private View root_view;
    String STR_EXTRA = "";

    // OVERRIDE METHODS ============================================================================
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        if (root_view == null && getActivity() != null) {
            try {

                root_view = inflater.inflate(R.layout.frag_music_player, container, false);

                Bundle bundle = this.getArguments();
                if (bundle != null) {
                    STR_EXTRA = bundle.getString(EXTRA_STRING);
                    fetchAudio();
                }

            } catch (InflateException e) {
                e.printStackTrace();
            }
        } else {
            ((ViewGroup) container.getParent()).removeView(root_view);
        }
        return root_view;
    }

    private void fetchAudio() {
        FIREBASE_DB.getReference().child(STR_EXTRA).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                TextModel object = dataSnapshot.getValue(TextModel.class);
                assert object != null;
//                txt_page_title.setText(object.title);
//                txt_info.setText(Html.fromHtml(object.info).toString());
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
//                txt_info.setText(getString(R.string.error_500));
//                txt_page_title.setText(getString(R.string.error));
                Database.handleDatabaseError(databaseError);
            }
        });

    }
}
