package com.chilloutrecords.activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.viewpager.widget.ViewPager;

import com.chilloutrecords.R;
import com.chilloutrecords.adapters.ViewPagerAdapter;
import com.chilloutrecords.fragments.ForgotPasswordFragment;
import com.chilloutrecords.fragments.LoginFragment;
import com.chilloutrecords.fragments.RegisterFragment;
import com.chilloutrecords.utils.Database;
import com.chilloutrecords.utils.StaticMethods;
import com.google.android.material.tabs.TabLayout;

import static com.chilloutrecords.utils.StaticVariables.EXTRA_STRING;

public class StartUpActivity extends AppCompatActivity {

    private TabLayout
            tab_layout;
    private ViewPager
            view_pager;
    private int[] fragment_title_list = {
            R.string.nav_login,
            R.string.nav_register,
            R.string.nav_forgot_password
    };
    private final String[] fragment_extras = {
            "",
            "",
            ""
    };
    private Fragment[] fragment_list = {
            new LoginFragment(),
            new RegisterFragment(),
            new ForgotPasswordFragment()
    };
    private RelativeLayout rl_splash;
    private LinearLayout ll_start_up;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_start_up);

        ll_start_up = findViewById(R.id.ll_start_up);
        rl_splash = findViewById(R.id.rl_splash);

        rl_splash.setVisibility(View.VISIBLE);

        // CHECK IF THE USER IS LOGGED IN
        if (Database.isUserLoggedIn()) {
            startActivity(new Intent(StartUpActivity.this, ParentActivity.class));
            finish();
        } else{
            setupViewPager(fragment_list, fragment_title_list, fragment_extras);
            StaticMethods.animate_slide_out(rl_splash, 3000, ll_start_up);
        }

    }

    public void setupViewPager(Fragment[] fragment_list, int[] fragment_title_list, String[] fragment_extras) {
        view_pager = findViewById(R.id.view_pager);
        tab_layout = findViewById(R.id.tabs);

        ViewPagerAdapter adapter = new ViewPagerAdapter(getSupportFragmentManager());

        for (int i = 0; i < fragment_title_list.length; i++) {
            Bundle bundle = new Bundle();
            bundle.putString(EXTRA_STRING, fragment_extras[i]);

            Fragment fragment = fragment_list[i];
            fragment.setArguments(bundle);
            adapter.addFragment(fragment, getString(fragment_title_list[i]));
        }

        view_pager.setAdapter(adapter);

        tab_layout.setupWithViewPager(view_pager, true);

    }

    public void forgotPassword(View view) {
        view_pager.setCurrentItem(2);
    }

    public void backToLogin() {
        view_pager.setCurrentItem(0);
    }
}

