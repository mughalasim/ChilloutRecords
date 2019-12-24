package com.chilloutrecords.models;

import androidx.fragment.app.Fragment;

public class NavigationModel {

    public NavigationModel(Fragment fragment, String page_title, String extra_bundles){
        this.extra_bundles = extra_bundles;
        this.page_title = page_title;
        this.fragment = fragment;
    }

    public Fragment fragment;
    public String extra_bundles = "";
    public String page_title = "";
}
