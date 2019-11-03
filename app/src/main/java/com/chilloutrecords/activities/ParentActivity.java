package com.chilloutrecords.activities;

import android.app.Dialog;
import android.app.DownloadManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Environment;
import androidx.annotation.NonNull;
import com.google.android.material.navigation.NavigationView;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.gson.Gson;
import com.makeramen.roundedimageview.RoundedImageView;

import org.json.JSONObject;

import com.chilloutrecords.BuildConfig;
import com.chilloutrecords.R;
import com.chilloutrecords.utils.Database;
import com.chilloutrecords.utils.Helpers;
import com.chilloutrecords.utils.SharedPrefs;
import me.leolin.shortcutbadger.ShortcutBadger;

public class ParentActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    Helpers helper;
    Database db;
    Toolbar toolbar;
    DrawerLayout drawer;
    NavigationView navigationView;
    private BroadcastReceiver receiver;
    private TextView userName, userEmail, toolbar_text;
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

        setNewDatabaseRef();

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

        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        navigationView.setNavigationItemSelectedListener(this);
        View header = navigationView.getHeaderView(0);
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

    // FIREBASE DATABASE VERSION CONTROL NOTIFICATION CHECKER ======================================

    private void setNewDatabaseRef() {
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference myRef = database.getReference("AppVersion");

        myRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                try {
                    Gson gson = new Gson();
                    JSONObject jsonObject = new JSONObject(gson.toJson(dataSnapshot.getValue()));
//                    Helpers.LogThis("AFTER PARSING: " + jsonObject.toString());
                    final int version_code = Integer.valueOf(jsonObject.getString("version_code"));
                    final String download_url = jsonObject.getString("download_url");
                    SharedPrefs.setDownLoadURL(download_url);

                    if (BuildConfig.VERSION_CODE < version_code) {
                        final Dialog dialog = new Dialog(ParentActivity.this);
                        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                        dialog.setContentView(R.layout.dialog_confirm);
                        final TextView txtMessage = dialog.findViewById(R.id.txtMessage);
                        final TextView txtOk = dialog.findViewById(R.id.txtOk);
                        final TextView txtCancel = dialog.findViewById(R.id.txtCancel);
                        final TextView txtTitle = dialog.findViewById(R.id.txtTitle);
                        txtCancel.setVisibility(View.VISIBLE);
                        txtTitle.setText(R.string.txt_old_version_title);
                        txtMessage.setText(R.string.txt_old_version_desc);

                        txtOk.setText(R.string.txt_update_now);
                        txtOk.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                try {
                                        DownloadManager.Request request = new DownloadManager.Request(Uri.parse(download_url));
                                        request.setDescription("Downloading APK");
                                        request.setTitle(getString(R.string.app_name));
                                        // in order for this if to run, you must use the android 3.2 to compile your app
                                        request.allowScanningByMediaScanner();
                                        request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED);
                                        request.setDestinationInExternalPublicDir(Environment.DIRECTORY_DOWNLOADS, "apk");
                                        // get download service and enqueue file
                                        DownloadManager manager = (DownloadManager) getSystemService(Context.DOWNLOAD_SERVICE);
                                        assert manager != null;
                                        manager.enqueue(request);
                                } catch (Exception e) {
                                    helper.ToastMessage(ParentActivity.this, getString(R.string.error_500));
                                }
                                dialog.cancel();
                            }
                        });

                        txtCancel.setText(R.string.txt_later);
                        txtCancel.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                dialog.cancel();
                            }
                        });
                        dialog.show();

                        ShortcutBadger.applyCount(ParentActivity.this, 1);

                    } else {
                        ShortcutBadger.applyCount(ParentActivity.this, 0);
                    }


                } catch (Exception e) {
                    e.printStackTrace();
                    Helpers.LogThis(e.toString());
                }
            }

            @Override
            public void onCancelled(DatabaseError error) {
                Helpers.LogThis("DATABASE:" + error.toString());
            }
        });

    }

}
