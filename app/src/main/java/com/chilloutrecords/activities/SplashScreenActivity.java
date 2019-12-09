package com.chilloutrecords.activities;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import com.chilloutrecords.R;
import com.chilloutrecords.utils.Database;

import me.leolin.shortcutbadger.ShortcutBadger;

public class SplashScreenActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash_screen);

        ShortcutBadger.applyCount(SplashScreenActivity.this, 0);

        // CHECK IF THE USER IS LOGGED IN
        Database.getUserIdAndLogin(SplashScreenActivity.this);

    }

}
