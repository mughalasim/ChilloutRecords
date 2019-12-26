package com.chilloutrecords.fragments;

import android.os.Bundle;
import android.view.InflateException;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.chilloutrecords.BuildConfig;
import com.chilloutrecords.R;
import com.chilloutrecords.activities.ParentActivity;
import com.chilloutrecords.adapters.HomeAdapter;
import com.chilloutrecords.interfaces.GeneralInterface;
import com.chilloutrecords.interfaces.HomeInterface;
import com.chilloutrecords.models.HomeModel;
import com.chilloutrecords.models.NavigationModel;
import com.chilloutrecords.utils.CustomRecyclerView;
import com.chilloutrecords.utils.DialogMethods;
import com.chilloutrecords.utils.StaticMethods;

import java.util.ArrayList;
import java.util.Objects;

import static com.chilloutrecords.activities.ParentActivity.PAGE_TITLE_ABOUT;
import static com.chilloutrecords.activities.ParentActivity.PAGE_TITLE_ARTISTS;
import static com.chilloutrecords.activities.ParentActivity.PAGE_TITLE_LOGOUT;
import static com.chilloutrecords.activities.ParentActivity.PAGE_TITLE_POLICY;
import static com.chilloutrecords.activities.ParentActivity.PAGE_TITLE_PROFILE;
import static com.chilloutrecords.activities.ParentActivity.PAGE_TITLE_SHARE;
import static com.chilloutrecords.activities.ParentActivity.PAGE_TITLE_VIDEOS;

public class HomeFragment extends Fragment {
    private View root_view;
    private CustomRecyclerView recycler_view;
    private HomeAdapter adapter;
    private HomeModel model;
    private ArrayList<HomeModel> models = new ArrayList<>();
    private TextView txt_no_results;
    DialogMethods dialogs;

    // OVERRIDE METHODS ============================================================================
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        if (root_view == null && getActivity() != null) {
            try {

                dialogs = new DialogMethods(getActivity());

                root_view = inflater.inflate(R.layout.layout_custom_recycler, container, false);
                recycler_view = root_view.findViewById(R.id.recycler_view);
                txt_no_results = root_view.findViewById(R.id.txt_no_results);

                fetchHome();

                recycler_view.setTextView(txt_no_results, getString(R.string.progress_loading));
                recycler_view.setHasFixedSize(true);
                recycler_view.setLayoutManager(new LinearLayoutManager(getContext()));

                adapter = new HomeAdapter(getActivity(), models, new HomeInterface() {
                    @Override
                    public void clicked(String page_title, String url) {
                        switch (page_title) {
                            case PAGE_TITLE_ARTISTS:
                                ((ParentActivity) Objects.requireNonNull(getActivity())).loadFragment(new NavigationModel(new ArtistFragment(), page_title, url, null, true));
                                break;

                            case PAGE_TITLE_VIDEOS:
                                ((ParentActivity) Objects.requireNonNull(getActivity())).loadFragment(new NavigationModel(new VideosFragment(), page_title, url, null, true));
                                break;

                            case PAGE_TITLE_PROFILE:
                                ((ParentActivity) Objects.requireNonNull(getActivity())).loadFragment(new NavigationModel(new ProfileFragment(), page_title, url, null, true));
                                break;

                            case PAGE_TITLE_ABOUT:
                                ((ParentActivity) Objects.requireNonNull(getActivity())).loadFragment(new NavigationModel(new TextFragment(), page_title, url, null, true));
                                break;

                            case PAGE_TITLE_POLICY:
                                ((ParentActivity) Objects.requireNonNull(getActivity())).loadFragment(new NavigationModel(new TextFragment(), page_title, url, null, true));
                                break;

                            case PAGE_TITLE_SHARE:
                                ((ParentActivity) Objects.requireNonNull(getActivity())).loadFragment(new NavigationModel(new ShareFragment(), page_title, url, null, true));
                                break;

                            case PAGE_TITLE_LOGOUT:
                                logout();
                                break;
                        }
                    }
                });

                recycler_view.setAdapter(adapter);

            } catch (InflateException e) {
                e.printStackTrace();
            }
        } else {
            ((ViewGroup) container.getParent()).removeView(root_view);
        }
        return root_view;
    }

    // CLASS METHODS ===============================================================================
    private void fetchHome() {
        models.clear();

        model = new HomeModel();
        model.txt = "Artists";
        model.img = "home/home_artists.jpg";
        model.url = BuildConfig.DB_REF_USERS;
        model.page_title = PAGE_TITLE_ARTISTS;
        models.add(model);

        model = new HomeModel();
        model.txt = "Videos";
        model.img = "home/home_videos.jpg";
        model.url = BuildConfig.DB_REF_VIDEOS;
        model.page_title = PAGE_TITLE_VIDEOS;
        models.add(model);

        model = new HomeModel();
        model.txt = "My Profile";
        model.img = "users/default.jpg";
        model.url = "";
        model.page_title = PAGE_TITLE_PROFILE;
        models.add(model);

        model = new HomeModel();
        model.txt = "About us";
        model.img = "home/home_about.jpg";
        model.url = BuildConfig.DB_REF_ABOUT_US;
        model.page_title = PAGE_TITLE_ABOUT;
        models.add(model);

        model = new HomeModel();
        model.txt = "Privacy Policy";
        model.img = "home/home_privacy.png";
        model.url = BuildConfig.DB_REF_POLICY;
        model.page_title = PAGE_TITLE_POLICY;
        models.add(model);

        model = new HomeModel();
        model.txt = "Share";
        model.img = "";
        model.url = "";
        model.page_title = PAGE_TITLE_SHARE;
        models.add(model);

        model = new HomeModel();
        model.txt = "Logout";
        model.img = "home/home_logout.jpg";
        model.url = "";
        model.page_title = PAGE_TITLE_LOGOUT;
        models.add(model);

    }

    private void logout() {
        dialogs.setDialogConfirm(
                getString(R.string.nav_logout),
                getString(R.string.txt_logout),
                getString(R.string.nav_logout),
                new GeneralInterface() {
                    @Override
                    public void success() {
                        StaticMethods.logOutUser(false);
                        Objects.requireNonNull(getActivity()).finish();
                    }

                    @Override
                    public void failed() {

                    }
                });
    }

}
