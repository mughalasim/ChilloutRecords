package asimmughal.chilloutrecords.main_pages.activities;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.makeramen.roundedimageview.RoundedImageView;

import asimmughal.chilloutrecords.R;
import asimmughal.chilloutrecords.utils.Database;
import asimmughal.chilloutrecords.utils.Helpers;
import asimmughal.chilloutrecords.utils.SharedPrefs;

public class ParentActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    Helpers helper;
    Database db;
    Toolbar toolbar;
    DrawerLayout drawer;
    ActionBarDrawerToggle toggle;
    NavigationView navigationView;
    private View header;
    private BroadcastReceiver receiver;
    TextView userName, userEmail, toolbar_text;
    private RoundedImageView userPic;
    private int drawer_id;
    String toolbarTitle;

    void initialize(int drawer_id, String toolbarTitle) {
        helper = new Helpers(ParentActivity.this);
        db = new Database();

        this.drawer_id = drawer_id;
        this.toolbarTitle = toolbarTitle;

        toolbar = findViewById(R.id.toolbar);
        toolbar_text = toolbar.findViewById(R.id.toolbar_text);

        drawer = findViewById(R.id.drawer_layout);
        navigationView = findViewById(R.id.nav_view);

        listenExitBroadcast();

        setUpToolBarAndDrawer();

        updateDrawer();

    }

    private void setUpToolBarAndDrawer() {
        LinearLayout toolbar_image = toolbar.findViewById(R.id.toolbar_image);

        if (toolbarTitle.equals("")) {
            toolbar_image.setVisibility(View.VISIBLE);
            toolbar_text.setVisibility(View.GONE);
        } else {
            toolbar_image.setVisibility(View.GONE);
            toolbar_text.setVisibility(View.VISIBLE);
            toolbar_text.setText(toolbarTitle);
        }

        toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        navigationView.setNavigationItemSelectedListener(this);
        header = navigationView.getHeaderView(0);
        userName = header.findViewById(R.id.username);
        userEmail = header.findViewById(R.id.userEmail);
        userPic = header.findViewById(R.id.userPic);


    }

    void updateDrawer() {
        userName.setText(SharedPrefs.getUserFullName());
        Glide.with(this).load(SharedPrefs.getUserPic()).into(userPic);
        userEmail.setText(SharedPrefs.getUserEmail());

        TextView app_version = findViewById(R.id.app_version);
        try {
            PackageInfo pInfo = this.getPackageManager().getPackageInfo(getPackageName(), 0);
            app_version.setText(getString(R.string.txt_version).concat(pInfo.versionName));
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }

        navigationView.getMenu().findItem(drawer_id).setChecked(true);
    }

    private void listenExitBroadcast() {
        IntentFilter filter = new IntentFilter();
        filter.addAction(Helpers.BroadcastValue);
        receiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                finish();
            }
        };
        registerReceiver(receiver, filter);
    }

    @Override
    public void onBackPressed() {
        finish();
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();
        helper.Drawer_Item_Clicked(ParentActivity.this, id);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    @Override
    protected void onDestroy() {
        if (receiver != null) {
            unregisterReceiver(receiver);
            receiver = null;
        }
        super.onDestroy();
    }

    @Override
    public void onResume() {
        super.onResume();
        updateDrawer();
        navigationView.getMenu().findItem(drawer_id).setChecked(true);
    }
}
