package com.chilloutrecords.activities;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.widget.ImageView;

import androidx.appcompat.app.AppCompatActivity;

import com.chilloutrecords.R;
import com.chilloutrecords.utils.Helper;

import me.leolin.shortcutbadger.ShortcutBadger;

import static com.chilloutrecords.utils.StaticVariables.INT_ANIMATION_TIME;

public class SplashScreenActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash_screen);

        ShortcutBadger.applyCount(SplashScreenActivity.this, 0);

        ImageView main_logo = findViewById(R.id.mainLogo);
//        Helper.animate_flash(main_logo, 0);
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                startActivity(new Intent(SplashScreenActivity.this, StartUpActivity.class));
                finish();
            }
        }, INT_ANIMATION_TIME);
    }

}
