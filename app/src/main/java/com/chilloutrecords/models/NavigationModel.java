package com.chilloutrecords.models;

import androidx.fragment.app.Fragment;

import java.util.ArrayList;

public class NavigationModel {

    public NavigationModel(
            Fragment fragment,
            String page_title,
            String extra_bundles,
            ArrayList<String> extra_string_array,
            boolean add_to_back_stack
    ){
        this.extra_bundles = extra_bundles;
        this.page_title = page_title;
        this.fragment = fragment;
        this.extra_string_array = extra_string_array;
        this.add_to_back_stack = add_to_back_stack;
    }

    public Fragment fragment;
    public String extra_bundles;
    public ArrayList<String> extra_string_array;
    public String page_title;
    public boolean add_to_back_stack;
}
