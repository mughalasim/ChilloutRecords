package com.chilloutrecords.utils;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.util.Log;
import android.util.Patterns;
import android.view.Gravity;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.ScaleAnimation;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.chilloutrecords.BuildConfig;
import com.chilloutrecords.R;
import com.chilloutrecords.activities.SplashScreenActivity;
import com.daimajia.androidanimations.library.Techniques;
import com.daimajia.androidanimations.library.YoYo;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.regex.Pattern;

import static com.chilloutrecords.utils.StaticVariables.EXTRA_STRING;
import static com.chilloutrecords.utils.StaticVariables.FIREBASE_AUTH;
import static com.chilloutrecords.utils.StaticVariables.INT_ANIMATION_TIME;

public class StaticMethods {

    private static Toast toast;

    // LOGS ========================================================================================
    public static void logg(String page_title, String data) {
        if (BuildConfig.DEBUG) {
            Log.e(page_title + ": ", data);
        }
    }

    public static void showToast(String message) {
        if (toast != null) {
            toast.cancel();
        }
        toast = Toast.makeText(ChilloutRecords.getAppContext(), message, Toast.LENGTH_LONG);
        toast.setGravity(Gravity.CENTER, 0, 0);
        toast.show();
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

    // LOGOUT ======================================================================================
    public static void logOutUser(Boolean is_session_expired) {
        Context context = ChilloutRecords.getAppContext();
        SharedPrefs.deleteAllSharedPrefs();

        FIREBASE_AUTH.signOut();
        if (is_session_expired) {
            showToast("Your session has expired. Kindly login to continue");
        }
        context.startActivity(new Intent(context, SplashScreenActivity.class).addFlags(Intent.FLAG_ACTIVITY_NEW_TASK).putExtra(EXTRA_STRING, is_session_expired));
    }

    // VALIDATIONS =================================================================================
    public static boolean validateInternetConnection() {
        ConnectivityManager cm = (ConnectivityManager) ChilloutRecords.getAppContext().getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = cm.getActiveNetworkInfo();
        return netInfo != null && netInfo.isConnected();

    }

    public static boolean validateAppIsInstalled(String uri) {
        PackageManager pm = ChilloutRecords.getAppContext().getPackageManager();
        try {
            pm.getPackageInfo(uri, PackageManager.GET_ACTIVITIES);
            return true;
        } catch (PackageManager.NameNotFoundException e) {
            return false;
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

    public static boolean validateEmail(TextInputEditText edit_text, TextInputLayout layout) {
        Pattern pattern = Patterns.EMAIL_ADDRESS;
        if (edit_text.getText() != null && edit_text.getText().toString().length() < 1) {
            layout.setError("This field cannot be blank");
            return false;
        } else if (edit_text.getText() != null && !pattern.matcher(edit_text.getText().toString()).matches()) {
            layout.setError("Invalid email address");
            return false;
        } else {
            layout.setError(null);
            return true;
        }
    }

    public static boolean validateEmptyEditText(TextInputEditText edit_text,
                                                TextInputLayout layout, String error_message) {
        if (edit_text.getText() != null && edit_text.getText().toString().length() < 1) {
            layout.setError(error_message);
            return false;
        } else {
            layout.setError(null);
            return true;
        }
    }

    public static boolean validateMatch(TextInputEditText edit_text_1, TextInputLayout layout_1,
                                        TextInputEditText edit_text_2, TextInputLayout layout_2) {
        if (edit_text_1.getText() != null && edit_text_2.getText() != null) {
            if (edit_text_1.getText().toString().trim().length() != edit_text_2.getText().toString().trim().length()) {
                layout_1.setError("Passwords do not match");
                return false;
            } else {
                layout_1.setError(null);
                return true;
            }
        } else {
            layout_1.setError("Cannot be empty");
            layout_2.setError("Cannot be empty");
            return false;
        }
    }

    public static boolean validateSpinner(Spinner spinner) {
        return spinner.getSelectedItemPosition() != 0;
    }

    public static boolean validateEmptyTextView(TextView textView, String errorMessage) {
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


    // FORMAT ======================================================================================
    public static String getDate(long time_in_millis) {
        // Create a DateFormatter object for displaying date in specified format.
        @SuppressLint("SimpleDateFormat") SimpleDateFormat formatter = new SimpleDateFormat("dd - MM - yyyy");

        // Create a calendar object that will convert the date and time value in milliseconds to date.
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(time_in_millis);
        return formatter.format(calendar.getTime());
    }

    public static String getGender(int gender_number) {
        switch (gender_number) {
            case 0:
                return "Gender: Unspecified";
            case 1:
                return "Gender: Male";
            case 2:
                return "Gender: Female";
            default:
                return "Gender: Unknown";
        }
    }

}
