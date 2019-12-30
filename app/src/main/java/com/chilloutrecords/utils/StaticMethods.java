package com.chilloutrecords.utils;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.SuppressLint;
import android.app.ActivityManager;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.util.Log;
import android.util.Patterns;
import android.view.Gravity;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.ScaleAnimation;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.chilloutrecords.BuildConfig;
import com.chilloutrecords.R;
import com.chilloutrecords.activities.StartUpActivity;
import com.daimajia.androidanimations.library.Techniques;
import com.daimajia.androidanimations.library.YoYo;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Formatter;
import java.util.Locale;
import java.util.regex.Pattern;

import static com.chilloutrecords.utils.StaticVariables.FIREBASE_AUTH;
import static com.chilloutrecords.utils.StaticVariables.FIREBASE_USER;
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
    public static void animate_recycler_view(View view) {
        ScaleAnimation anim = new ScaleAnimation(
                0.0f, 1.0f, 0.0f, 1.0f,
                Animation.RELATIVE_TO_SELF, 0.5f,
                Animation.RELATIVE_TO_SELF, 0.5f);
        anim.setDuration(INT_ANIMATION_TIME);
        view.startAnimation(anim);
    }

    public static void animate_slide_out(final View to_hide, final int animation_delay, final View to_show) {
        YoYo.with(Techniques.FadeOutUp).duration(1).playOn(to_show);
        YoYo.with(Techniques.FadeOutUp)
                .duration(INT_ANIMATION_TIME).delay(animation_delay)
                .withListener(new AnimatorListenerAdapter() {
                    @Override
                    public void onAnimationEnd(Animator animation) {
                        YoYo.with(Techniques.FadeInUp).duration(INT_ANIMATION_TIME).playOn(to_show);
                        super.onAnimationEnd(animation);
                    }
                }).playOn(to_hide);
    }

    // LOGOUT ======================================================================================
    public static void logOutUser(Boolean is_session_expired) {
        Context context = ChilloutRecords.getAppContext();
        SharedPrefs.deleteAllSharedPrefs();

        FIREBASE_AUTH.signOut();
        FIREBASE_USER = null;

        if (is_session_expired) {
            showToast("Your session has expired. Kindly login to continue");
        }
        context.startActivity(new Intent(context, StartUpActivity.class).addFlags(Intent.FLAG_ACTIVITY_NEW_TASK));
    }

    // SHARE =======================================================================================
    public static void share(String link) {
        Context context = ChilloutRecords.getAppContext();
        Intent share = new Intent(Intent.ACTION_SEND);
        share.setType("text/plain");
        share.putExtra(Intent.EXTRA_TEXT, link);
        context.startActivity(Intent.createChooser(share, "Share " + context.getString(R.string.app_name)).setFlags(Intent.FLAG_ACTIVITY_NEW_TASK));
    }

    // VALIDATIONS =================================================================================
    public static boolean validateInternetConnection() {
        ConnectivityManager cm = (ConnectivityManager) ChilloutRecords.getAppContext().getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = cm.getActiveNetworkInfo();
        return netInfo != null && netInfo.isConnected();

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

    public static void startServiceIfNotRunning(Context context, Class<?> service_class) {
        boolean isNotRunning = true;
        ActivityManager manager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        assert manager != null;
        for (ActivityManager.RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
            if (service_class.getName().equals(service.service.getClassName())) {
                isNotRunning = false;
            }
        }
        if (isNotRunning) {
            context.startService(new Intent(context, service_class));
        }
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
        @SuppressLint("SimpleDateFormat") SimpleDateFormat formatter = new SimpleDateFormat("MMM yyyy");

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

    @NonNull
    public static String getTimeFromMillis(int time_in_millis) {
        StringBuilder builder;
        Formatter formatter;
        builder = new StringBuilder();
        formatter = new Formatter(builder, Locale.getDefault());
        int total_seconds = time_in_millis / 1000;

        int seconds = total_seconds % 60;
        int minutes = (total_seconds / 60) % 60;
        int hours = total_seconds / 3600;

        builder.setLength(0);
        if (hours > 0) {
            return formatter.format("%d:%02d:%02d", hours, minutes, seconds).toString();
        } else {
            return formatter.format("%02d:%02d", minutes, seconds).toString();
        }
    }

}
