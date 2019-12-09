package com.chilloutrecords.activities;

import android.os.Bundle;
import android.view.MenuItem;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;

import com.chilloutrecords.BuildConfig;
import com.chilloutrecords.R;
import com.chilloutrecords.fragments.HomeFragment;
import com.chilloutrecords.fragments.ProfileFragment;
import com.chilloutrecords.fragments.TextFragment;
import com.chilloutrecords.interfaces.GeneralInterface;
import com.chilloutrecords.interfaces.TrackInterface;
import com.chilloutrecords.models.TrackModel;
import com.chilloutrecords.models.UserModel;
import com.chilloutrecords.services.LoginStateService;
import com.chilloutrecords.utils.DialogMethods;
import com.chilloutrecords.utils.StaticMethods;
import com.google.android.material.navigation.NavigationView;

import java.util.Objects;

import static com.chilloutrecords.utils.StaticVariables.EXTRA_STRING;

public class ParentActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener, TrackInterface {

    DialogMethods dialogs;
    Toolbar toolbar;
    DrawerLayout drawer;
    NavigationView navigation_view;

    public static UserModel user_model = new UserModel();

    // OVERRIDE METHODS ============================================================================
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_parent);

        StaticMethods.startServiceIfNotRunning(this, LoginStateService.class);

        dialogs = new DialogMethods(ParentActivity.this);

        toolbar = findViewById(R.id.toolbar);

        drawer = findViewById(R.id.drawer_layout);
        navigation_view = findViewById(R.id.nav_view);

        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawer, toolbar, R.string.nav_drawer_open, R.string.nav_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        navigation_view.setNavigationItemSelectedListener(this);

        loadFragment(new HomeFragment(), R.id.nav_home, "");

    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();
        switch (id) {
            case R.id.nav_home:
                loadFragment(new HomeFragment(), id, "");
                break;

            case R.id.nav_profile:
                loadFragment(new ProfileFragment(), id, "");
                break;

            case R.id.nav_share:
//                TODO - Make share fragment
//                loadFragment(new ShareFragment(), id, "");
                break;

            case R.id.nav_about_us:
                loadFragment(new TextFragment(), id, BuildConfig.DB_REF_ABOUT_US);
                break;

            case R.id.nav_policy:
                loadFragment(new TextFragment(), id, BuildConfig.DB_REF_POLICY);
                break;

            case R.id.nav_log_out:
                logout();
                break;

        }
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    @Override
    public void onBackPressed() {
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else if (this.isTaskRoot()) {
            logout();
        }
    }

    // BASIC METHODS ===============================================================================
    public void loadFragment(Fragment fragment, int drawer_id, String extra_bundle) {
        if (drawer_id!=0 && drawer_id != R.id.nav_log_out) {
            navigation_view.getMenu().findItem(drawer_id).setChecked(true);
        }
        if (!extra_bundle.equals("")) {
            Bundle bundle = new Bundle();
            bundle.putString(EXTRA_STRING, extra_bundle);
            fragment.setArguments(bundle);
        }

        this.getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.ll_fragment, fragment)
                .addToBackStack(null)
                .commit();
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
                        finish();
                    }

                    @Override
                    public void failed() {

                    }
                });
    }

    @Override
    public void success(TrackModel model, String db_path, String storage_path) {
        ((ProfileFragment) Objects.requireNonNull(getSupportFragmentManager().findFragmentById(R.id.ll_fragment))).showPlayer(model, db_path, storage_path);
    }
}
