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
import com.chilloutrecords.utils.Database;
import com.chilloutrecords.utils.DialogMethods;
import com.chilloutrecords.utils.StaticMethods;

import java.util.ArrayList;
import java.util.Objects;

import static com.chilloutrecords.activities.ParentActivity.PAGE_TITLE_ABOUT;
import static com.chilloutrecords.activities.ParentActivity.PAGE_TITLE_ARTISTS;
import static com.chilloutrecords.activities.ParentActivity.PAGE_TITLE_LOGOUT;
import static com.chilloutrecords.activities.ParentActivity.PAGE_TITLE_PROFILE;
import static com.chilloutrecords.activities.ParentActivity.PAGE_TITLE_SHARE;
import static com.chilloutrecords.activities.ParentActivity.PAGE_TITLE_UPGRADE;
import static com.chilloutrecords.activities.ParentActivity.PAGE_TITLE_VIDEOS;
import static com.chilloutrecords.utils.StaticVariables.USER_MODEL;

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

                            case PAGE_TITLE_UPGRADE:
                                ((ParentActivity) Objects.requireNonNull(getActivity())).loadFragment(new NavigationModel(new PayFragment(), page_title, url, null, true));
                                break;

                            case PAGE_TITLE_SHARE:
                                StaticMethods.share(BuildConfig.DEFAULT_SHARE_LINK);
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

    @Override
    public void onResume() {
        Database.getUser(new GeneralInterface() {
            @Override
            public void success() {
                fetchHome();
                adapter.notifyDataSetChanged();
            }

            @Override
            public void failed() {
                recycler_view.setTextView(txt_no_results, getString(R.string.error_500));
            }
        });
        super.onResume();
    }

    // CLASS METHODS ===============================================================================
    private void fetchHome() {
        models.clear();

        model = new HomeModel();
        model.txt = getString(R.string.nav_artists);
        model.img = "home/home_artists.jpg";
        model.url = BuildConfig.DB_REF_USERS;
        model.page_title = PAGE_TITLE_ARTISTS;
        models.add(model);

        model = new HomeModel();
        model.txt = getString(R.string.nav_videos);
        model.img = "home/home_videos.jpg";
        model.url = BuildConfig.DB_REF_VIDEOS;
        model.page_title = PAGE_TITLE_VIDEOS;
        models.add(model);

        model = new HomeModel();
        model.txt = getString(R.string.nav_my_profile);
        model.img = USER_MODEL.p_pic;
        model.url = "";
        model.page_title = PAGE_TITLE_PROFILE;
        models.add(model);

        model = new HomeModel();
        model.txt = getString(R.string.nav_share);
        model.img = "home/home_share.jpg";
        model.url = "";
        model.page_title = PAGE_TITLE_SHARE;
        models.add(model);

        model = new HomeModel();
        model.txt = getString(R.string.nav_about_us);
        model.img = "home/home_about.jpg";
        model.url = BuildConfig.DB_REF_ABOUT_US;
        model.page_title = PAGE_TITLE_ABOUT;
        models.add(model);

        if(!USER_MODEL.is_activated){
            model = new HomeModel();
            model.txt = getString(R.string.nav_upgrade);
            model.img = "home/home_upgrade.jpg";
            model.url = "";
            model.page_title = PAGE_TITLE_UPGRADE;
            models.add(model);
        }

        model = new HomeModel();
        model.txt = getString(R.string.nav_logout);
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
