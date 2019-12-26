package com.chilloutrecords.activities;

import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.chilloutrecords.R;
import com.chilloutrecords.fragments.HomeFragment;
import com.chilloutrecords.models.NavigationModel;
import com.chilloutrecords.models.TrackModel;
import com.chilloutrecords.models.UserModel;
import com.chilloutrecords.models.VideoModel;
import com.chilloutrecords.services.LoginStateService;
import com.chilloutrecords.utils.StaticMethods;

import java.util.ArrayList;

import me.leolin.shortcutbadger.ShortcutBadger;

import static com.chilloutrecords.utils.StaticVariables.EXTRA_DATA;
import static com.chilloutrecords.utils.StaticVariables.EXTRA_STRING;

public class ParentActivity extends AppCompatActivity {

    Toolbar toolbar;
    TextView txt_page_title;

    public ArrayList<NavigationModel> navigation_list = new ArrayList<>();

    public final static String
            PAGE_TITLE_HOME = "Home",
            PAGE_TITLE_ARTISTS = "Home / Artists",
            PAGE_TITLE_VIDEOS = "Home / Videos",
            PAGE_TITLE_PROFILE = "Home / Profile",
            PAGE_TITLE_PROFILE_EDIT = "Home / Profile Edit",
            PAGE_TITLE_SHARE = "Home / Share",
            PAGE_TITLE_ABOUT = "Home / About us",
            PAGE_TITLE_POLICY = "Home / Privacy Policy",
            PAGE_TITLE_LOGOUT = "Home / Logout";

    public static UserModel user_model = new UserModel();
    public static TrackModel track_model = new TrackModel();
    public static VideoModel video_model = new VideoModel();
    public static String STR_COLLECTION_ID = "";

    // OVERRIDE METHODS ============================================================================
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_parent);

        StaticMethods.startServiceIfNotRunning(this, LoginStateService.class);

        ShortcutBadger.applyCount(ParentActivity.this, 0);

        toolbar = findViewById(R.id.toolbar);
        txt_page_title = findViewById(R.id.txt_page_navigation);

        // SETUP TOOLBAR
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });

        loadFragment(new NavigationModel(new HomeFragment(), PAGE_TITLE_HOME, "", null, true));

    }

    @Override
    public void onBackPressed() {
        int size = navigation_list.size();
        if (size > 1) {
            navigation_list.remove(size - 1);
            navigation_list.get(size - 2).add_to_back_stack = false;
            loadFragment(navigation_list.get(size - 2));
        } else {
            finish();
        }
    }

    // BASIC METHODS ===============================================================================
    public void loadFragment(NavigationModel navigation) {
        if (navigation.add_to_back_stack) {
            navigation_list.add(navigation);
        }

        if (navigation_list.size() < 2) {
            toolbar.setNavigationIcon(null);
        } else {
            toolbar.setNavigationIcon(getResources().getDrawable(R.drawable.ic_back));
        }

        Bundle bundle = new Bundle();

        if (!navigation.extra_bundles.equals("")) {
            bundle.putString(EXTRA_STRING, navigation.extra_bundles);
            navigation.fragment.setArguments(bundle);
        }
        if (navigation.extra_string_array != null) {
            bundle.putStringArrayList(EXTRA_DATA, navigation.extra_string_array);
            navigation.fragment.setArguments(bundle);
        }

        txt_page_title.setText(navigation.page_title);

        this.getSupportFragmentManager()
                .beginTransaction()
                .setCustomAnimations(R.anim.enter, R.anim.exit)
                .replace(R.id.ll_fragment, navigation.fragment)
                .addToBackStack(null)
                .commit();
    }

}
