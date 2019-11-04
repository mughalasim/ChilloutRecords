package com.chilloutrecords.adapters;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

import java.util.ArrayList;
import java.util.List;

public class ViewPagerAdapter extends FragmentPagerAdapter {
    private final List<Fragment> fragment_list = new ArrayList<>();
    private final List<String> fragment_titles = new ArrayList<>();

    public ViewPagerAdapter(FragmentManager manager) {
        super(manager);
    }

    @Override
    public Fragment getItem(int position) {
        return fragment_list.get(position);
    }

    @Override
    public int getCount() {
        return fragment_list.size();
    }

    public void addFragment(Fragment fragment, String title) {
        fragment_list.add(fragment);
        fragment_titles.add(title);
    }

    @Override
    public CharSequence getPageTitle(int position) {
        return fragment_titles.get(position);
    }

}