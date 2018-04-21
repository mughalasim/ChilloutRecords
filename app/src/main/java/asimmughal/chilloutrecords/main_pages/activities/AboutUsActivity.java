package asimmughal.chilloutrecords.main_pages.activities;

import android.app.Dialog;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.gson.Gson;

import org.json.JSONObject;

import asimmughal.chilloutrecords.R;
import asimmughal.chilloutrecords.utils.Helpers;
import asimmughal.chilloutrecords.utils.SharedPrefs;

public class AboutUsActivity extends ParentActivity {

    TextView about_us;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_about_us);

        initialize(R.id.about_us, "ABOUT US");

        findAllViews();

        fetchAboutUs();

    }

    private void findAllViews() {
        TextView version_number = findViewById(R.id.version_number);
        about_us = findViewById(R.id.about_us);

        try {
            PackageInfo pInfo = this.getPackageManager().getPackageInfo(getPackageName(), 0);
            version_number.setText(pInfo.versionName);
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }

    }

    private void fetchAboutUs() {
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference myRef = database.getReference("AboutUs");

        myRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                try {
                    Gson gson = new Gson();
                    JSONObject jsonObject = new JSONObject(gson.toJson(dataSnapshot.getValue()));
                    Helpers.LogThis("AFTER PARSING: " + jsonObject.toString());
                    final String info = jsonObject.getString("info");

                    about_us.setText(info);

                } catch (Exception e) {
                    e.printStackTrace();
                    about_us.setText(getString(R.string.error_500));
                    Helpers.LogThis(e.toString());
                }
            }

            @Override
            public void onCancelled(DatabaseError error) {
                Helpers.LogThis("DATABASE:" + error.toString());
                about_us.setText(getString(R.string.error_500));
            }
        });

    }

    public void SHARE(View view) {
        final String STR_SHARELINK = "Hey! Please help a friend out by downloading this amazing app from this link here -> "+SharedPrefs.getDownLoadURL();
        final Dialog dialog = new Dialog(AboutUsActivity.this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.dialog_share);
        final ImageView share_facebook = dialog.findViewById(R.id.share_facebook);
        final ImageView share_email = dialog.findViewById(R.id.share_email);
        final ImageView share_messenger = dialog.findViewById(R.id.share_messenger);
        final ImageView share_sms = dialog.findViewById(R.id.share_sms);
        final ImageView share_whatsapp = dialog.findViewById(R.id.share_whatsapp);


        share_email.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent emailIntent = new Intent(Intent.ACTION_SEND);
                emailIntent.setType("text/html");
                emailIntent.putExtra(Intent.EXTRA_SUBJECT, getString(R.string.app_name));
                emailIntent.putExtra(Intent.EXTRA_TEXT, STR_SHARELINK);
                startActivity(Intent.createChooser(emailIntent, "Send Email"));
                dialog.cancel();
            }
        });

        share_messenger.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (helper.validateAppIsInstalled("com.facebook.orca")) {
                    Intent messengerIntent = new Intent();
                    messengerIntent.setAction(Intent.ACTION_SEND);
                    messengerIntent.putExtra(Intent.EXTRA_TEXT, STR_SHARELINK);
                    messengerIntent.setType("text/plain");
                    messengerIntent.setPackage("com.facebook.orca");
                    startActivity(messengerIntent);
                }
            }
        });

        share_sms.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent smsIntent = new Intent(Intent.ACTION_VIEW);
                smsIntent.putExtra("sms_body", STR_SHARELINK);
                smsIntent.setType("vnd.android-dir/mms-sms");
                startActivity(smsIntent);
                dialog.cancel();
            }
        });

        share_whatsapp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (helper.validateAppIsInstalled("com.whatsapp")) {
                    Intent whatsappIntent = new Intent(Intent.ACTION_SEND);
                    whatsappIntent.setType("text/plain");
                    whatsappIntent.setPackage("com.whatsapp");
                    whatsappIntent.putExtra(Intent.EXTRA_TEXT, STR_SHARELINK);
                    startActivity(whatsappIntent);
                    dialog.cancel();
                }
            }
        });

        dialog.setCancelable(true);
        dialog.show();
    }
}
