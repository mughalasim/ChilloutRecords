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
import com.google.android.material.button.MaterialButton;
import com.mopub.common.MoPub;
import com.mopub.common.SdkConfiguration;
import com.mopub.mobileads.MoPubErrorCode;
import com.mopub.mobileads.MoPubInterstitial;

import java.util.Calendar;
import java.util.Objects;

import static com.chilloutrecords.BuildConfig.AD_UNIT_ID;
import static com.chilloutrecords.utils.StaticVariables.USER_MODEL;
import static com.mopub.common.logging.MoPubLog.LogLevel.DEBUG;

public class PointsFragment extends Fragment {
    private View root_view;
    private TextView txt_message, txt_points;
    private MaterialButton btn_watch;

    private MoPubInterstitial ad;

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

                ad = new MoPubInterstitial(getActivity(), BuildConfig.AD_UNIT_ID);

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

    @Override
    public void onDestroy() {
        super.onDestroy();
        ad.destroy();
    }

    @Override
    public void onResume() {
        super.onResume();
        MoPub.onResume(Objects.requireNonNull(getActivity()));
    }

    @Override
    public void onPause() {
        super.onPause();
        MoPub.onPause(Objects.requireNonNull(getActivity()));
    }

    @Override
    public void onStop() {
        super.onStop();
        MoPub.onStop(Objects.requireNonNull(getActivity()));
    }

    // CLASS METHODS ===============================================================================
    private void checkIfReadyToWatchAd() {
        // check if ready to watch video
        if (USER_MODEL.ad_watch_date == 0) {
            USER_MODEL.ad_watch_date = Calendar.getInstance().getTimeInMillis() + BuildConfig.AD_WATCH_GRACE_PERIOD;
            Database.updateAdWatchedDate();
            checkIfReadyToWatchAd();
        } else if (USER_MODEL.ad_watch_date < Calendar.getInstance().getTimeInMillis()) {
            // allow to watch
            MoPub.initializeSdk(Objects.requireNonNull(getActivity()),
                    new SdkConfiguration.Builder(AD_UNIT_ID)
                            .withLogLevel(DEBUG)
                            .withLegitimateInterestAllowed(true)
                            .build(),
                    () -> {
                        StaticMethods.logg("POINTS", "Ad is initialized");
                        initAdObject();
                    });
        } else {
            // start a per second timer to keep updating the message
            String time = String.valueOf((USER_MODEL.ad_watch_date - Calendar.getInstance().getTimeInMillis()) / 1000);
            txt_message.setText("Wait for " + time + " seconds to watch your next ad");

            final Handler handler = new Handler();
            handler.postDelayed(() -> checkIfReadyToWatchAd(), 1000);
        }
    }

    private void initAdObject() {
        // init the ad object
        updateWatchButton(false, "Loading.. please wait...");
        ad.setInterstitialAdListener(new MoPubInterstitial.InterstitialAdListener() {
            @Override
            public void onInterstitialLoaded(MoPubInterstitial interstitial) {
                updateWatchButton(true, "");
            }

            @Override
            public void onInterstitialFailed(MoPubInterstitial interstitial, MoPubErrorCode errorCode) {
                StaticMethods.logg("POINTS", errorCode.toString());
                updateWatchButton(false, "Failed to load ad, please try again later");
            }

            @Override
            public void onInterstitialShown(MoPubInterstitial interstitial) {

            }

            @Override
            public void onInterstitialClicked(MoPubInterstitial interstitial) {

            }

            @Override
            public void onInterstitialDismissed(MoPubInterstitial interstitial) {
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
        ad.load();

        btn_watch.setOnClickListener(view -> {
            // show the video here and add the points once the ad ends
            if (ad.isReady()) {
                ad.show();
            } else {
                StaticMethods.showToast(1, "Loading ad, Wait a few seconds and try again");
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
