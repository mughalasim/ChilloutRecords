package asimmughal.chilloutrecords.main_pages.activities;

import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.widget.TextView;

import asimmughal.chilloutrecords.R;

public class AboutUsActivity extends ParentActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_about_us);

        initialize(R.id.about_us, "ABOUT US");

        findAllViews();

    }

    private void findAllViews() {
        TextView version_number = findViewById(R.id.version_number);

        try {
            PackageInfo pInfo = this.getPackageManager().getPackageInfo(getPackageName(), 0);
            version_number.setText(pInfo.versionName);
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }

    }

}
