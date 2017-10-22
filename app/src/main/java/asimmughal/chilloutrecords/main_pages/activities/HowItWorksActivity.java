package asimmughal.chilloutrecords.main_pages.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.widget.Toolbar;
import android.view.View;

import java.util.ArrayList;
import java.util.List;

import asimmughal.chilloutrecords.R;
import asimmughal.chilloutrecords.main_pages.fragments.HIW_Events_Fragment;
import asimmughal.chilloutrecords.main_pages.fragments.HIW_Explore_Fragment;
import asimmughal.chilloutrecords.main_pages.fragments.HIW_Offers_Fragment;
import asimmughal.chilloutrecords.main_pages.fragments.HIW_Reviews_Fragment;

public class HowItWorksActivity extends ParentActivity {

    private TabLayout tabLayout;
    private ViewPager viewPager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_how_it_works);

        initialize(R.id.how_it_works, "HOW IT WORKS");

        findAllViews();

    }

    private void findAllViews() {
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        navigationView = (NavigationView) findViewById(R.id.nav_view);
        viewPager = (ViewPager) findViewById(R.id.viewpager);
        tabLayout = (TabLayout) findViewById(R.id.tabs);

        setupViewPager(viewPager);
        tabLayout.setupWithViewPager(viewPager, true);
    }

    private void setupViewPager(ViewPager viewPager) {
        HowItWorksActivity.ViewPagerAdapter adapter = new ViewPagerAdapter(getSupportFragmentManager());
        adapter.addFragment(new HIW_Explore_Fragment());
        adapter.addFragment(new HIW_Offers_Fragment());
        adapter.addFragment(new HIW_Reviews_Fragment());
        adapter.addFragment(new HIW_Events_Fragment());

        viewPager.setAdapter(adapter);
    }

    private class ViewPagerAdapter extends FragmentPagerAdapter {
        private final List<Fragment> mFragmentList = new ArrayList<>();

        private ViewPagerAdapter(FragmentManager manager) {
            super(manager);
        }

        @Override
        public Fragment getItem(int position) {
            return mFragmentList.get(position);
        }

        @Override
        public int getCount() {
            return mFragmentList.size();
        }

        private void addFragment(Fragment fragment) {
            mFragmentList.add(fragment);
        }

    }

    public void EXPLORE(View view) {
        startActivity(new Intent(HowItWorksActivity.this, RestaurantSearchActivity.class));
    }

}
