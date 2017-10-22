package asimmughal.chilloutrecords.utils;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.Signature;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.util.Base64;
import android.util.Log;
import android.util.Patterns;
import android.view.ActionMode;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.animation.Animation;
import android.view.animation.ScaleAnimation;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.daimajia.androidanimations.library.Techniques;
import com.daimajia.androidanimations.library.YoYo;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.regex.Pattern;

import asimmughal.chilloutrecords.BuildConfig;
import asimmughal.chilloutrecords.R;
import asimmughal.chilloutrecords.main_pages.activities.AboutUsActivity;
import asimmughal.chilloutrecords.main_pages.activities.ChangeLocationActivity;
import asimmughal.chilloutrecords.main_pages.activities.FavoritesActivity;
import asimmughal.chilloutrecords.main_pages.activities.HomeActivity;
import asimmughal.chilloutrecords.main_pages.activities.HowItWorksActivity;
import asimmughal.chilloutrecords.main_pages.activities.MyAccountActivity;
import asimmughal.chilloutrecords.main_pages.activities.RestaurantSearchActivity;
import asimmughal.chilloutrecords.start_up.LoginActivity;
import asimmughal.chilloutrecords.start_up.SplashScreenActivity;

public class Helpers {
    public String Mobile_Number_Error = "Invalid Mobile Number has been entered";

    public static String SORT_NEARBY = "n";
    public static String SORT_A_Z = "az";
    public static String SORT_FEATURED = "pre";
    public static String SORT_RATING = "p";

    public static String ORDER_ASCENDING = "a";
    public static String ORDER_DESCENDING = "d";

    public static String ADAPTER_DEFAULT = "DEFAULT";
    public static String ADAPTER_DISTANCE = "DISTANCE";

    public static String STR_LOGGED_OUT_EXTRA = "logged_out";
    public static String STR_LOGGED_OUT_TRUE= "true";
    public static String STR_LOGGED_OUT_FALSE = "false";

    private Context context;
    private static Toast mToast;
    private TextView ProgressDialogMessage;
    private final Dialog dialog;
    public static String BroadcastValue = "asimmughal.chilloutrecords.ACTIONLOGOUT";

    public Helpers(Context context) {
        this.context = context;
        dialog = new Dialog(context);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.dialog_loading);
        dialog.setCancelable(false);
        ProgressDialogMessage = (TextView) dialog.findViewById(R.id.messageText);
    }

    // DRAWER CLICKS ===============================================================================

    public void Drawer_Item_Clicked(Context context, int id) {
        if (id == R.id.home) {
            context.startActivity(new Intent(context, HomeActivity.class));
        } else if (id == R.id.find_restaurants) {
            context.startActivity(new Intent(context, RestaurantSearchActivity.class));
        } else if (id == R.id.favorites) {
            context.startActivity(new Intent(context, FavoritesActivity.class));
        } else if (id == R.id.how_it_works) {
            context.startActivity(new Intent(context, HowItWorksActivity.class));
        } else if (id == R.id.change_location) {
            context.startActivity(new Intent(context, ChangeLocationActivity.class));
        } else if (id == R.id.log_in) {
            SharedPrefs.deleteAllSharedPrefs();
            context.startActivity(new Intent(context, LoginActivity.class).addFlags(Intent.FLAG_ACTIVITY_NEW_TASK));
            context.sendBroadcast(new Intent().setAction(BroadcastValue));
        } else if (id == R.id.my_account) {
            context.startActivity(new Intent(context, MyAccountActivity.class));
        } else if (id == R.id.about_us) {
            context.startActivity(new Intent(context, AboutUsActivity.class));
        }
    }


    // BROADCASTS ==================================================================================

    public static void sendLogoutBroadcast() {
        Context context = MyApplication.getAppContext();
        SharedPrefs.deleteAllSharedPrefs();
        SharedPrefs.setTemporaryToken("");
        context.startActivity(new Intent(context, SplashScreenActivity.class).addFlags(Intent.FLAG_ACTIVITY_NEW_TASK).putExtra(STR_LOGGED_OUT_EXTRA, STR_LOGGED_OUT_TRUE));
        context.sendBroadcast(new Intent().setAction(BroadcastValue));
    }

    public static void sessionExpiryBroadcast() {
        Context context = MyApplication.getAppContext();
        SharedPrefs.deleteAllSharedPrefs();
        SharedPrefs.setTemporaryToken("");
        context.startActivity(new Intent(context, SplashScreenActivity.class).addFlags(Intent.FLAG_ACTIVITY_NEW_TASK).putExtra(STR_LOGGED_OUT_EXTRA, STR_LOGGED_OUT_TRUE));
        context.sendBroadcast(new Intent().setAction(BroadcastValue));
    }


    // LOGS ========================================================================================

    public static void LogThis(String data) {
        if (BuildConfig.LOGGING) {
            Log.e("EAT OUT KENYA: ", data);
        }
    }

    public void getShaCertificate() {
        PackageInfo info;
        try {

            info = context.getPackageManager().getPackageInfo(
                    "asimmughal.chilloutrecords", PackageManager.GET_SIGNATURES);

            for (Signature signature : info.signatures) {
                MessageDigest md;
                md = MessageDigest.getInstance("SHA");
                md.update(signature.toByteArray());
                String something = new String(Base64.encode(md.digest(), 0));
                Log.e("Hash key", something);
                System.out.println("Hash key" + something);
            }

        } catch (PackageManager.NameNotFoundException e1) {
            Log.e("name not found", e1.toString());
        } catch (NoSuchAlgorithmException e) {
            Log.e("no such an algorithm", e.toString());
        } catch (Exception e) {
            Log.e("exception", e.toString());
        }
    }


    // DIALOGS AND DISPLAYS ========================================================================

    public void progressDialog(Boolean status) {
        if (status) {
            dialog.show();
        } else {
            dialog.cancel();
        }
    }

    public void setProgressDialogMessage(String message) {
        ProgressDialogMessage.setText(message);
    }

    public void myDialog(Context Dialogcontext, String Title, String Message) {
        final Dialog dialog = new Dialog(Dialogcontext);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.dialog_confirm);
        final TextView txtMessage = (TextView) dialog.findViewById(R.id.txtMessage);
        final TextView txtOk = (TextView) dialog.findViewById(R.id.txtOk);
        final TextView txtTitle = (TextView) dialog.findViewById(R.id.txtTitle);
        txtTitle.setText(Title);
        txtMessage.setText(Message);
        txtOk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.cancel();
            }
        });
        dialog.show();
    }

    public void dialogNoGPS(final Context context) {
        final Dialog dialog = new Dialog(context);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.dialog_confirm);
        final TextView txtMessage = (TextView) dialog.findViewById(R.id.txtMessage);
        final TextView txtOk = (TextView) dialog.findViewById(R.id.txtOk);
        final TextView txtCancel = (TextView) dialog.findViewById(R.id.txtCancel);
        final TextView txtTitle = (TextView) dialog.findViewById(R.id.txtTitle);
        txtTitle.setText("LOCATION SERVICES");
        txtMessage.setText("EatOut needs to access your locations, your GPS seems to be disabled, do you want to enable it?");
        txtOk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                context.startActivity(new Intent(android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS));
                dialog.cancel();
            }
        });
        txtCancel.setVisibility(View.VISIBLE);
        txtCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.cancel();
            }
        });
        dialog.show();
    }

    public void ToastMessage(Context MessageContext, String Messsage) {
        if (mToast != null) {
            mToast.cancel();
        }
        mToast = Toast.makeText(MessageContext, Messsage, Toast.LENGTH_LONG);
        mToast.setGravity(Gravity.CENTER, 0, 0);
        mToast.show();
    }


    // VALIDATIONS =================================================================================

    public boolean validateEmail(String email) {
        Pattern pattern = Patterns.EMAIL_ADDRESS;
        return pattern.matcher(email).matches();
    }

    public boolean validateIsLoggedIn() {
        if (SharedPrefs.getWelcomeTitle().equals("")) {
            return true;
        } else {
            myDialog(context, "Alert", "Please login to use this feature");
            return false;
        }
    }

    public boolean validateIsLoggedInDrawer() {
        if (SharedPrefs.getWelcomeTitle().equals("")) {
            return true;
        } else {
            return false;
        }
    }

    public boolean validateAppIsInstalled(String uri) {
        PackageManager pm = context.getPackageManager();
        boolean app_installed;
        try {
            pm.getPackageInfo(uri, PackageManager.GET_ACTIVITIES);
            app_installed = true;
        } catch (PackageManager.NameNotFoundException e) {
            app_installed = false;
            ToastMessage(context, "This app has not been installed");
        }
        return app_installed;
    }

    public boolean validateInternetNotPresent() {
        ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = cm.getActiveNetworkInfo();
        return !(netInfo != null && netInfo.isConnected());

    }

    public boolean validateMobileNumber(String MobileNumber) {
        if (MobileNumber.length() < 9) {
            Mobile_Number_Error = "Invalid Mobile number entered";
            return true;
        } else if (
                MobileNumber.contains(" ") ||
                        MobileNumber.contains(",") ||
                        MobileNumber.contains(".") ||
                        MobileNumber.contains("(") ||
                        MobileNumber.contains("#") ||
                        MobileNumber.contains(")")
                ) {
            Mobile_Number_Error = "Mobile Number contains invalid characters";
            return true;
        } else {
            return false;
        }
    }

    public void fetchLocation(){

    }

    // CREATE RANDOM STRING ========================================================================

    private static final String AB = "0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz";
    private static SecureRandom rnd = new SecureRandom();

    public String generateRandomString() {
        StringBuilder sb = new StringBuilder(12);
        for (int i = 0; i < 12; i++)
            sb.append(AB.charAt(rnd.nextInt(AB.length())));
        return sb.toString();
    }

    // ACTION BAR SETTINGS FOR THE EDIT TEXT =======================================================
    public void setDefaultEditTextSelectionMode(EditText editText) {
        editText.setCustomSelectionActionModeCallback(new ActionMode.Callback() {
            @Override
            public boolean onCreateActionMode(ActionMode mode, Menu menu) {
                return false;
            }

            @Override
            public boolean onPrepareActionMode(ActionMode mode, Menu menu) {
                return false;
            }

            @Override
            public boolean onActionItemClicked(ActionMode mode, MenuItem item) {
                return false;
            }

            @Override
            public void onDestroyActionMode(ActionMode mode) {

            }
        });
    }



    // ANIMATIONS ==================================================================================

    public void animate_flash(View v, int time, int animation_delay) {
        YoYo.with(Techniques.Flash)
                .duration(time).delay(animation_delay)
                .playOn(v);
    }

    public static void animate_recyclerview(View view) {
        ScaleAnimation anim = new ScaleAnimation(
                0.0f, 1.0f, 0.0f, 1.0f,
                Animation.RELATIVE_TO_SELF, 0.5f,
                Animation.RELATIVE_TO_SELF, 0.5f);
        anim.setDuration(400);
        view.startAnimation(anim);
    }

    public void animate_slide_in(View v, int time, int animation_delay) {
        YoYo.with(Techniques.SlideInLeft)
                .duration(time).delay(animation_delay)
                .playOn(v);
    }

}
