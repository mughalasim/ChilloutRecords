package com.chilloutrecords.activities;

import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.viewpager.widget.ViewPager;

import com.chilloutrecords.R;
import com.chilloutrecords.adapters.ViewPagerAdapter;
import com.chilloutrecords.fragments.LoginFragment;
import com.chilloutrecords.fragments.RegisterFragment;
import com.google.android.material.tabs.TabLayout;

import static com.chilloutrecords.utils.StaticVariables.EXTRA_STRING;

public class StartUpActivity extends AppCompatActivity {

    private TabLayout tab_layout;
    private ViewPager view_pager;

    private int[] fragment_title_list = {
            R.string.nav_login,
            R.string.nav_register
    };

    private final String[] fragment_extras = {
            "",
            ""
    };

    private Fragment[] fragment_list = {
            new LoginFragment(),
            new RegisterFragment()
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_start_up);

        setupViewPager(fragment_list, fragment_title_list, fragment_extras);

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

}

