package com.chilloutrecords.activities;

import android.os.Bundle;
import android.widget.ImageView;

import androidx.appcompat.app.AppCompatActivity;

import com.chilloutrecords.R;
import com.chilloutrecords.utils.StaticMethods;
import com.google.firebase.auth.FirebaseAuth;

import me.leolin.shortcutbadger.ShortcutBadger;

import static com.chilloutrecords.utils.StaticVariables.FIREBASE_AUTH;

public class SplashScreenActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash_screen);

        ShortcutBadger.applyCount(SplashScreenActivity.this, 0);

        ImageView main_logo = findViewById(R.id.mainLogo);
        StaticMethods.animate_flash(main_logo, 0);

        // Check and see if the user is logged in
        StaticMethods.getUserIdAndLogin(SplashScreenActivity.this);

    }

}
