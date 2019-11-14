package com.chilloutrecords.activities;

import android.os.Bundle;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.viewpager.widget.ViewPager;

import com.chilloutrecords.R;
import com.chilloutrecords.adapters.ViewPagerAdapter;
import com.chilloutrecords.fragments.ForgotPasswordFragment;
import com.chilloutrecords.fragments.LoginFragment;
import com.chilloutrecords.fragments.RegisterFragment;
import com.google.android.material.tabs.TabLayout;

import static com.chilloutrecords.utils.StaticVariables.EXTRA_STRING;

public class ProfileActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_profile);


    }

}

