package com.chilloutrecords.activities;

import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.ImageView;


import com.chilloutrecords.R;
import com.chilloutrecords.utils.SharedPrefs;

public class HomeActivity extends ParentActivity {
    public static String ARTISTS = "Artists";
    public static String VIDEOS = "Videos";
    public static String UPCOMING_PROJECTS = "Upcoming_Projects";
    public static String FREESTYLE_FRIDAYS = "Freestyle_fridays";
    public static String KEY = "DB_REFERENCE";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        initialize(R.id.home, "");

        findAllViews();


    }

    private void findAllViews() {


    }

    public void ARTISTS(View view) {
//        if (helper.validateInternetConnection()) {
//            startActivity(new Intent(HomeActivity.this, GeneralListActivity.class).putExtra(KEY, ARTISTS));
//        } else {
//            helper.myDialog(HomeActivity.this, getString(R.string.txt_alert), getString(R.string.error_connection));
//        }
    }

    public void VIDEOS(View view) {
//        if (helper.validateInternetConnection()) {
//            startActivity(new Intent(HomeActivity.this, GeneralListActivity.class).putExtra(KEY, VIDEOS));
//        } else {
//            helper.myDialog(HomeActivity.this, getString(R.string.txt_alert), getString(R.string.error_connection));
//        }
    }

    public void FREESTYLES(View view) {
//        if (helper.validateInternetConnection()) {
//            startActivity(new Intent(HomeActivity.this, GeneralListActivity.class).putExtra(KEY, FREESTYLE_FRIDAYS));
//        } else {
//            helper.myDialog(HomeActivity.this, getString(R.string.txt_alert), getString(R.string.error_connection));
//        }
    }

    public void UPCOMING(View view) {
//        if (helper.validateInternetConnection()) {
//            startActivity(new Intent(HomeActivity.this, GeneralListActivity.class).putExtra(KEY, UPCOMING_PROJECTS));
//        } else {
//            helper.myDialog(HomeActivity.this, getString(R.string.txt_alert), getString(R.string.error_connection));
//        }
    }

    public void SHARE(View view) {
        final String STR_SHARELINK = "Hey! Please help a friend out by downloading this amazing app from this link here -> "+ SharedPrefs.getDownLoadURL();
        final Dialog dialog = new Dialog(HomeActivity.this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.dialog_share);
//        final ImageView share_facebook = dialog.findViewById(R.id.share_facebook);
//        final ImageView share_email = dialog.findViewById(R.id.share_email);
//        final ImageView share_messenger = dialog.findViewById(R.id.share_messenger);
//        final ImageView share_sms = dialog.findViewById(R.id.share_sms);
//        final ImageView share_whatsapp = dialog.findViewById(R.id.share_whatsapp);

//        share_email.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                Intent emailIntent = new Intent(Intent.ACTION_SEND);
//                emailIntent.setType("text/html");
//                emailIntent.putExtra(Intent.EXTRA_SUBJECT, getString(R.string.app_name));
//                emailIntent.putExtra(Intent.EXTRA_TEXT, STR_SHARELINK);
//                startActivity(Intent.createChooser(emailIntent, "Send Email"));
//                dialog.cancel();
//            }
//        });

//        share_messenger.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                if (helper.validateAppIsInstalled("com.facebook.orca")) {
//                    Intent messengerIntent = new Intent();
//                    messengerIntent.setAction(Intent.ACTION_SEND);
//                    messengerIntent.putExtra(Intent.EXTRA_TEXT, STR_SHARELINK);
//                    messengerIntent.setType("text/plain");
//                    messengerIntent.setPackage("com.facebook.orca");
//                    startActivity(messengerIntent);
//                }
//            }
//        });

//        share_sms.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                Intent smsIntent = new Intent(Intent.ACTION_VIEW);
//                smsIntent.putExtra("sms_body", STR_SHARELINK);
//                smsIntent.setType("vnd.android-dir/mms-sms");
//                startActivity(smsIntent);
//                dialog.cancel();
//            }
//        });

//        share_whatsapp.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
////                if (helper.validateAppIsInstalled("com.whatsapp")) {
////                    Intent whatsappIntent = new Intent(Intent.ACTION_SEND);
////                    whatsappIntent.setType("text/plain");
////                    whatsappIntent.setPackage("com.whatsapp");
////                    whatsappIntent.putExtra(Intent.EXTRA_TEXT, STR_SHARELINK);
////                    startActivity(whatsappIntent);
////                    dialog.cancel();
////                }
//            }
//        });

        dialog.setCancelable(true);
        dialog.show();
    }
}
