package asimmughal.chilloutrecords.start_up;

import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.Window;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.gson.JsonObject;

import org.json.JSONException;
import org.json.JSONObject;

import asimmughal.chilloutrecords.BuildConfig;
import asimmughal.chilloutrecords.R;
import asimmughal.chilloutrecords.services.FirebaseInstanceIDService;
import asimmughal.chilloutrecords.services.LoginService;
import asimmughal.chilloutrecords.utils.Database;
import asimmughal.chilloutrecords.utils.Helpers;
import asimmughal.chilloutrecords.utils.SharedPrefs;
import me.leolin.shortcutbadger.ShortcutBadger;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static asimmughal.chilloutrecords.start_up.LoginActivity.DEFAULT_EMAIL;
import static asimmughal.chilloutrecords.start_up.LoginActivity.DEFAULT_PASSWORD;
import static asimmughal.chilloutrecords.utils.Helpers.STR_LOGGED_OUT_EXTRA;
import static asimmughal.chilloutrecords.utils.Helpers.STR_LOGGED_OUT_FALSE;
import static asimmughal.chilloutrecords.utils.Helpers.STR_LOGGED_OUT_TRUE;

public class SplashScreenActivity extends AppCompatActivity {
    Helpers helper;
    final int  ANIMATION_LENGTH = 1000;
    private String
            TAG_LOG = "SPLASH PAGE: ",
            STR_NOTIF_TITLE = "",
            STR_NOTIF_BODY = "";
    private ImageView mainlogo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_splash_screen);

        ShortcutBadger.applyCount(SplashScreenActivity.this, 0);

        helper = new Helpers(SplashScreenActivity.this);
        Database db = new Database();

        FirebaseInstanceIDService firebaseInstanceIDService = new FirebaseInstanceIDService();
        firebaseInstanceIDService.onTokenRefresh();

//         Uncomment Only when SHA Cert needed for Facebook API
//         helper.getShaCertificate();

        if (SharedPrefs.getOldDataBaseVersion() < BuildConfig.DATABASE_VERSION) {
            db.deleteAllTables();
            SharedPrefs.deleteAllSharedPrefs();
            SharedPrefs.setNewDataBaseVersion(BuildConfig.DATABASE_VERSION);
        }

        findAllViews();

        handleExtraBundles();

    }

    @Override
    protected void onResume() {
        super.onResume();
        SharedPrefs.setTemporaryToken("");
        checkForTemporaryToken();
    }

    private void handleExtraBundles() {
        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            if (extras.getString(STR_LOGGED_OUT_EXTRA) != null) {
                if (extras.getString(STR_LOGGED_OUT_EXTRA).equals(STR_LOGGED_OUT_TRUE)) {
                    helper.ToastMessage(SplashScreenActivity.this, "Thank you for using Chillout Records, We hope to see you soon");
                } else if (extras.getString(STR_LOGGED_OUT_EXTRA).equals(STR_LOGGED_OUT_FALSE)) {
                    helper.ToastMessage(SplashScreenActivity.this, "Your session has expired, please login again");
                }
                checkForTemporaryToken();
            }
            STR_NOTIF_TITLE = extras.getString("notification_title");
            if (STR_NOTIF_TITLE != null && !STR_NOTIF_TITLE.equals("")) {
                STR_NOTIF_BODY = extras.getString("notification_body");

                final Dialog dialog = new Dialog(SplashScreenActivity.this);
                dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                dialog.setContentView(R.layout.dialog_confirm);
                final TextView txtMessage = (TextView) dialog.findViewById(R.id.txtMessage);
                final TextView txtOk = (TextView) dialog.findViewById(R.id.txtOk);
                final TextView txtTitle = (TextView) dialog.findViewById(R.id.txtTitle);
                txtTitle.setText(STR_NOTIF_TITLE);
                txtMessage.setText(STR_NOTIF_BODY);
                txtOk.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        dialog.cancel();
                        checkForTemporaryToken();
                    }
                });
                dialog.show();
            }
        } else {
            checkForTemporaryToken();
        }
    }

    private void checkForTemporaryToken() {
        if (SharedPrefs.getTemporaryToken().equals("")) {
            asyncGetTemporaryToken();
        } else {
            startUp();
        }
    }

    private void findAllViews() {
        mainlogo = (ImageView) findViewById(R.id.mainLogo);
        helper.animate_flash(mainlogo, ANIMATION_LENGTH, 0);

    }



    private void startUp() {
            finish();
            if (SharedPrefs.getFirstTimeLogin()) {
                startActivity(new Intent(SplashScreenActivity.this, FirstTimeLogin.class));
            } else {
                startActivity(new Intent(SplashScreenActivity.this, LoginActivity.class).putExtra(STR_LOGGED_OUT_EXTRA, STR_LOGGED_OUT_TRUE));
            }
    }

    // GET TEMPORARY TOKEN =========================================================================
    private void asyncGetTemporaryToken() {
        SharedPrefs.setTemporaryToken("");
        LoginService loginService = LoginService.retrofit.create
                (LoginService.class);

        final Call<JsonObject> call = loginService.login(DEFAULT_EMAIL, DEFAULT_PASSWORD);

        call.enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                try {
                    JSONObject main = new JSONObject(response.body().toString());
                    Helpers.LogThis(response.body().toString());
                    String request_msg = main.getString("success");
                    if (request_msg.equals("true")) {
                        Helpers.LogThis(main.getString("token"));
                        SharedPrefs.setTemporaryToken(main.getString("token"));
                        startUp();
                    } else {
                        showSnackBar();
                    }
                } catch (JSONException e) {
                    showSnackBar();
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(Call<JsonObject> call, Throwable t) {
                t.printStackTrace();
                showSnackBar();
            }
        });
    }

    private void showSnackBar(){
        final Snackbar snackBar = Snackbar.make(findViewById(R.id.splash_screen),
                getString(R.string.error_500), Snackbar.LENGTH_INDEFINITE);
        snackBar.show();
        snackBar.setAction("RETRY", new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                asyncGetTemporaryToken();
            }
        });
    }
}
