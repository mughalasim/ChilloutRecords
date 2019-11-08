package com.chilloutrecords.activities;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;

import androidx.annotation.NonNull;

import com.chilloutrecords.BuildConfig;
import com.chilloutrecords.fragments.HomeFragment;
import com.chilloutrecords.fragments.LoginFragment;
import com.chilloutrecords.interfaces.GeneralInterface;
import com.chilloutrecords.utils.ChilloutRecords;
import com.chilloutrecords.utils.DialogMethods;
import com.chilloutrecords.utils.StaticMethods;
import com.chilloutrecords.utils.StaticVariables;
import com.google.android.material.navigation.NavigationView;

import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.makeramen.roundedimageview.RoundedImageView;

import com.chilloutrecords.R;
import com.chilloutrecords.utils.Database;
import com.chilloutrecords.utils.SharedPrefs;

import static com.chilloutrecords.utils.StaticVariables.EXTRA_STRING;

public class ParentActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    DialogMethods
            dialogs;
    Toolbar toolbar;
    DrawerLayout
            drawer;
    NavigationView
            navigation_view;
    private BroadcastReceiver
            receiver;
    private TextView
            txt_page_title;


    // OVERRIDE METHODS ============================================================================
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_parent);

        dialogs = new DialogMethods(ParentActivity.this);

        toolbar = findViewById(R.id.toolbar);
        txt_page_title = findViewById(R.id.txt_page_title);

        drawer = findViewById(R.id.drawer_layout);
        navigation_view = findViewById(R.id.nav_view);

        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawer, toolbar, R.string.nav_drawer_open, R.string.nav_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        navigation_view.setNavigationItemSelectedListener(this);

        listenExitBroadcast();

        loadFragment(new HomeFragment(), getString(R.string.nav_home), R.id.nav_home);

    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();
        switch (id) {
            case R.id.nav_home:
                loadFragment(new HomeFragment(), getString(R.string.nav_home), id);
                break;

            case R.id.nav_my_profile:
                loadFragment(new HomeFragment(), getString(R.string.nav_profile), id);
                break;

            case R.id.nav_share:
//                TODO - Make share dialog
//                dialogs.share();
                break;

            case R.id.nav_about_us:
                startActivity(new Intent(this, TextActivity.class).putExtra(EXTRA_STRING, BuildConfig.DB_REF_ABOUT_US));
                break;

            case R.id.nav_log_out:
                StaticMethods.broadcastLogout(false);
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
            dialogs.setDialogConfirm("Exit", "Are you sre you wish to exit the App", "Exit", new GeneralInterface() {
                @Override
                public void success() {
                    finish();
                }

                @Override
                public void failed() {

                }
            });
        }
    }

    @Override
    protected void onDestroy() {
        if (receiver != null) {
            unregisterReceiver(receiver);
        }
        super.onDestroy();
    }

    // BASIC METHODS ===============================================================================
    public void loadFragment(Fragment fragment, String page_title, int drawer_id) {
        txt_page_title.setText(page_title);
        navigation_view.getMenu().findItem(drawer_id).setChecked(true);
        FragmentManager manager = this.getSupportFragmentManager();
        FragmentTransaction transaction = manager.beginTransaction();
        transaction.replace(R.id.ll_fragment, fragment);
        transaction.addToBackStack(null);
        transaction.commit();

    }

    private void listenExitBroadcast() {
        IntentFilter filter = new IntentFilter();
        filter.addAction(StaticVariables.BROADCAST_LOG_OUT);
        receiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                finish();
            }
        };
        registerReceiver(receiver, filter);
    }

    public void openVideos(View view){

    }

    public void openArtists(View view){

    }


}
