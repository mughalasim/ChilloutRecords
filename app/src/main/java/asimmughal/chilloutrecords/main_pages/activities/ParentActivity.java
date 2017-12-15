package asimmughal.chilloutrecords.main_pages.activities;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
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

import asimmughal.chilloutrecords.R;
import asimmughal.chilloutrecords.utils.Database;
import asimmughal.chilloutrecords.utils.Helpers;
import asimmughal.chilloutrecords.utils.SharedPrefs;
import de.hdodenhof.circleimageview.CircleImageView;

public class ParentActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {
    static Helpers helper;
    protected Database db;
    protected Toolbar toolbar;
    protected DrawerLayout drawer;
    protected ActionBarDrawerToggle toggle;
    protected NavigationView navigationView;
    protected View header;
    protected BroadcastReceiver receiver;
    protected TextView userName, userEmail;
    protected CircleImageView userPic;
    protected int drawer_id;
    protected String toolbarTitle;

    public void initialize(int drawer_id, String toolbarTitle) {
        helper = new Helpers(ParentActivity.this);
        db = new Database();

        this.drawer_id = drawer_id;
        this.toolbarTitle = toolbarTitle;

        toolbar = (Toolbar) findViewById(R.id.toolbar);
        drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        navigationView = (NavigationView) findViewById(R.id.nav_view);

        listenExitBroadcast();

        setUpToolBarAndDrawer();

        updateDrawer();

    }

    public void setUpToolBarAndDrawer() {
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(true);
        getSupportActionBar().setTitle(toolbarTitle);
        toolbar.setTitle(toolbarTitle);
        LinearLayout toolbar_image = (LinearLayout) toolbar.findViewById(R.id.toolbar_image);

        if (toolbarTitle.equals("")) {
            toolbar_image.setVisibility(View.VISIBLE);
        } else {
            toolbar_image.setVisibility(View.GONE);
        }

        toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        navigationView.setNavigationItemSelectedListener(this);
        header = navigationView.getHeaderView(0);
        userName = (TextView) header.findViewById(R.id.username);
        userEmail = (TextView) header.findViewById(R.id.userEmail);
        userPic = (CircleImageView) header.findViewById(R.id.userPic);


    }

    public void updateDrawer() {
        userName.setText(SharedPrefs.getUserFullName());
        Glide.with(this).load(SharedPrefs.getUserPic()).into(userPic);
        userEmail.setText(SharedPrefs.getUserEmail());

        navigationView.getMenu().findItem(drawer_id).setChecked(true);
    }

    public void listenExitBroadcast() {
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
