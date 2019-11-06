package com.chilloutrecords.activities;

import android.os.Bundle;
import android.widget.ImageView;

import androidx.appcompat.app.AppCompatActivity;

import com.chilloutrecords.R;
import com.chilloutrecords.utils.Database;
import com.chilloutrecords.utils.StaticMethods;

import me.leolin.shortcutbadger.ShortcutBadger;

public class SplashScreenActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash_screen);

        ShortcutBadger.applyCount(SplashScreenActivity.this, 0);

        ImageView main_logo = findViewById(R.id.main_logo);
        StaticMethods.animate_flash(main_logo, 0);

        // CHECK IF THE USER IS LOGGED IN
        Database.getUserIdAndLogin(SplashScreenActivity.this);

    }

}
