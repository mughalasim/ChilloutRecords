package com.chilloutrecords.utils;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.ScaleAnimation;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.chilloutrecords.BuildConfig;
import com.chilloutrecords.R;
import com.chilloutrecords.activities.SplashScreenActivity;
import com.daimajia.androidanimations.library.Techniques;
import com.daimajia.androidanimations.library.YoYo;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;

import java.util.regex.Pattern;

import static com.chilloutrecords.utils.StaticVariables.BROADCAST_LOG_OUT;
import static com.chilloutrecords.utils.StaticVariables.EXTRA_FALSE;
import static com.chilloutrecords.utils.StaticVariables.EXTRA_STRING;
import static com.chilloutrecords.utils.StaticVariables.EXTRA_TRUE;
import static com.chilloutrecords.utils.StaticVariables.INT_ANIMATION_TIME;

public class StaticMethods {

    public interface SuccessListener {
        void success();
        void failed();
    }

    public interface DialogListener {
        void yes();
        void no();
    }

    // LOGS ========================================================================================
    public static void LogThis(String data) {
        if (BuildConfig.DEBUG) {
            Log.e("LOGGED: ", data);
        }
    }

    // ANIMATIONS ==================================================================================
    public void animateSwipeRefresh(SwipeRefreshLayout swipe_refresh) {
        swipe_refresh.setSize(0);
        swipe_refresh.setDistanceToTriggerSync(INT_ANIMATION_TIME);
        swipe_refresh.setColorSchemeResources(
                R.color.colorAccent,
                R.color.colorPrimary
        );
    }

    public static void animate_flash(View v, int animation_delay) {
        YoYo.with(Techniques.Flash)
                .duration(INT_ANIMATION_TIME).delay(animation_delay)
                .playOn(v);
    }

    public static void animate_recycler_view(View view) {
        ScaleAnimation anim = new ScaleAnimation(
                0.0f, 1.0f, 0.0f, 1.0f,
                Animation.RELATIVE_TO_SELF, 0.5f,
                Animation.RELATIVE_TO_SELF, 0.5f);
        anim.setDuration(INT_ANIMATION_TIME);
        view.startAnimation(anim);
    }

    public static void animate_slide_in(View v, int time, int animation_delay) {
        YoYo.with(Techniques.SlideInLeft)
                .duration(INT_ANIMATION_TIME).delay(animation_delay)
                .playOn(v);
    }

    // BROADCASTS ==================================================================================
    public static void broadcastLogout() {
        Context context = MyApplication.getAppContext();
        SharedPrefs.deleteAllSharedPrefs();
        context.startActivity(new Intent(context, SplashScreenActivity.class).addFlags(Intent.FLAG_ACTIVITY_NEW_TASK).putExtra(EXTRA_STRING, EXTRA_TRUE));
        context.sendBroadcast(new Intent().setAction(BROADCAST_LOG_OUT));
    }

    public static void broadcastSessionExpiry() {
        Context context = MyApplication.getAppContext();
        SharedPrefs.deleteAllSharedPrefs();
        context.startActivity(new Intent(context, SplashScreenActivity.class).addFlags(Intent.FLAG_ACTIVITY_NEW_TASK).putExtra(EXTRA_STRING, EXTRA_FALSE));
        context.sendBroadcast(new Intent().setAction(BROADCAST_LOG_OUT));
    }

    // VALIDATIONS =================================================================================
    public static boolean validateInternetConnection() {
        ConnectivityManager cm = (ConnectivityManager) MyApplication.getAppContext().getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = cm.getActiveNetworkInfo();
        return netInfo != null && netInfo.isConnected();

    }

    public static void validateAppIsInstalled(String uri, SuccessListener listener) {
        PackageManager pm = MyApplication.getAppContext().getPackageManager();
        try {
            pm.getPackageInfo(uri, PackageManager.GET_ACTIVITIES);
            listener.success();
        } catch (PackageManager.NameNotFoundException e) {
            listener.failed();
        }
    }

    public static boolean validateGooglePlayServices(Activity activity) {
        GoogleApiAvailability googleApiAvailability = GoogleApiAvailability.getInstance();
        int status = googleApiAvailability.isGooglePlayServicesAvailable(activity);
        if (status != ConnectionResult.SUCCESS) {
            if (googleApiAvailability.isUserResolvableError(status)) {
                googleApiAvailability.getErrorDialog(activity, status, 2404).show();
            }
            return false;
        }
        return true;
    }

    public static boolean validateEmail(EditText edit_text) {
        Pattern pattern = Patterns.EMAIL_ADDRESS;
        if (edit_text.getText().toString().length() < 1) {
            edit_text.setError("This field cannot be blank");
            return false;
        } else if (!pattern.matcher(edit_text.getText().toString()).matches()) {
            edit_text.setError("Invalid email address");
            return false;
        } else {
            edit_text.setError(null);
            return true;
        }
    }

    public static boolean validateEmptyEditText(EditText editText, String error_message) {
        if (editText.getText().toString().length() < 1) {
            editText.setError(error_message);
            return false;
        } else {
            editText.setError(null);
            return true;
        }
    }

    public boolean validateSpinner(Spinner spinner) {
        return spinner.getSelectedItemPosition() != 0;
    }

    public boolean validateEmptyTextView(TextView textView, String errorMessage) {
        return textView.getText().toString().trim().length() >= 1;

    }


    // FETCH  ======================================================================================
    public static String fetchFromEditText(EditText et, String default_val) {
        String value = et.getText().toString();
        if (value.length() > 1) {
            if (value.equals(default_val)) {
                return "";
            } else {
                return value;
            }
        } else {
            return "";
        }
    }

}
