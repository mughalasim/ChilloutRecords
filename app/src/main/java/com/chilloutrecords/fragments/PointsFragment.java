package com.chilloutrecords.fragments;

import android.os.Bundle;
import android.os.Handler;
import android.view.InflateException;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.chilloutrecords.BuildConfig;
import com.chilloutrecords.R;
import com.chilloutrecords.utils.Database;
import com.chilloutrecords.utils.StaticMethods;
import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.InterstitialAd;
import com.google.android.gms.ads.LoadAdError;
import com.google.android.material.button.MaterialButton;

import java.util.Calendar;
import java.util.Objects;

import static com.chilloutrecords.utils.StaticVariables.USER_MODEL;

public class PointsFragment extends Fragment {
    private View root_view;
    private TextView txt_message, txt_points;
    private MaterialButton btn_watch;

    private InterstitialAd ad;

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

                updateWatchButton(false, "Loading.. please wait...");

                checkIfReadyToWatchAd();

            } catch (InflateException e) {
                e.printStackTrace();
            }
        } else {
            ((ViewGroup) container.getParent()).removeView(root_view);
        }
        return root_view;
    }

    // CLASS METHODS ===============================================================================
    private void checkIfReadyToWatchAd(){
        // check if ready to watch video
        if(USER_MODEL.ad_watch_date == 0){
            USER_MODEL.ad_watch_date = Calendar.getInstance().getTimeInMillis() + BuildConfig.AD_WATCH_GRACE_PERIOD;
            Database.updateAdWatchedDate();
            checkIfReadyToWatchAd();
        } else if (USER_MODEL.ad_watch_date < Calendar.getInstance().getTimeInMillis() ) {
            // allow to watch
            initAdObject();
        } else {
            // start a per second timer to keep updating the message
            String time = String.valueOf((USER_MODEL.ad_watch_date - Calendar.getInstance().getTimeInMillis())/1000);
            txt_message.setText("Wait for " + time + " seconds to watch your next ad");

            final Handler handler = new Handler();
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                   checkIfReadyToWatchAd();
                }
            }, 1000);
        }
    }

    private void initAdObject() {
        // init the ad object
        updateWatchButton(false, "Loading.. please wait...");
        ad = new InterstitialAd(Objects.requireNonNull(getActivity()));
        ad.setAdUnitId(BuildConfig.AD_UNIT_ID);
        ad.loadAd(new AdRequest.Builder().build());


        ad.setAdListener(new AdListener() {
            @Override
            public void onAdLoaded() {
                // Code to be executed when an ad finishes loading.
                updateWatchButton(true, "");
            }

            @Override
            public void onAdFailedToLoad(LoadAdError adError) {
                // Code to be executed when an ad request fails.
                updateWatchButton(false, "Failed to load ad, please try again later");
            }

            @Override
            public void onAdOpened() {
                // Code to be executed when the ad is displayed.
            }

            @Override
            public void onAdClicked() {
                // Code to be executed when the user clicks on an ad.
            }

            @Override
            public void onAdLeftApplication() {
                // Code to be executed when the user has left the app.
            }

            @Override
            public void onAdClosed() {
                // Code to be executed when the interstitial ad is closed.
                Database.updateProfilePoints(BuildConfig.POINTS_EARN_AD);
                Database.updateAdWatchedDate();
                USER_MODEL.ad_watch_date = Calendar.getInstance().getTimeInMillis() + BuildConfig.AD_WATCH_GRACE_PERIOD;
                txt_points.setText(String.valueOf(USER_MODEL.points + BuildConfig.POINTS_EARN_AD));
                updateWatchButton(false, "Loading.. please wait...");
                checkIfReadyToWatchAd();
                StaticMethods.showToast(1, "You just earned " + BuildConfig.POINTS_EARN_AD + " points to use anywhere within the app");
            }
        });


        btn_watch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // show the video here and add the points once the ad ends
                if (ad.isLoaded()) {
                    ad.show();
                } else {
                    StaticMethods.showToast(1, "Loading ad, Wait a few seconds and try again");
                }
            }
        });
    }

    private void updateWatchButton(Boolean is_shown, String message) {
        if (is_shown) {
            btn_watch.setVisibility(View.VISIBLE);
            txt_message.setVisibility(View.GONE);
            txt_message.setText("");
        } else {
            btn_watch.setVisibility(View.GONE);
            txt_message.setVisibility(View.VISIBLE);
            txt_message.setText(message);
        }
    }
}
