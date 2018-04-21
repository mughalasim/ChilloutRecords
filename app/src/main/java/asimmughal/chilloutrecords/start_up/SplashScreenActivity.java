package asimmughal.chilloutrecords.start_up;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.widget.ImageView;

import com.crashlytics.android.Crashlytics;

import asimmughal.chilloutrecords.R;
import asimmughal.chilloutrecords.services.FirebaseInstanceIDService;
import asimmughal.chilloutrecords.utils.Helpers;
import io.fabric.sdk.android.Fabric;
import me.leolin.shortcutbadger.ShortcutBadger;

public class SplashScreenActivity extends AppCompatActivity {
    Helpers helper;
    final int ANIMATION_LENGTH = 1000;
    private ImageView mainlogo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Fabric.with(this, new Crashlytics());

        setContentView(R.layout.activity_splash_screen);

        ShortcutBadger.applyCount(SplashScreenActivity.this, 0);

        helper = new Helpers(SplashScreenActivity.this);

        FirebaseInstanceIDService firebaseInstanceIDService = new FirebaseInstanceIDService();
        firebaseInstanceIDService.onTokenRefresh();

        findAllViews();

        startUp();

    }


    private void findAllViews() {
        mainlogo = findViewById(R.id.mainLogo);

    }


    private void startUp() {
        helper.animate_flash(mainlogo, ANIMATION_LENGTH, 0);
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                startActivity(new Intent(SplashScreenActivity.this, LoginActivity.class));
                finish();
            }
        }, ANIMATION_LENGTH * 2);

    }
}
