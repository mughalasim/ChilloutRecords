package com.chilloutrecords.activities;

import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.chilloutrecords.R;

import static com.chilloutrecords.utils.StaticVariables.EXTRA_STRING;

public class ParentToolBarActivity extends AppCompatActivity {

    Toolbar toolbar;
    TextView txt_page_title;
    String STR_EXTRA = "";

    public void setToolbar(String page_title) {
        toolbar = findViewById(R.id.toolbar);
        txt_page_title = findViewById(R.id.txt_page_title);
        txt_page_title.setText(page_title);

        toolbar.setNavigationIcon(getResources().getDrawable(R.drawable.ic_back));
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });

    }

    public boolean hasExtraBundles() {
        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            STR_EXTRA = (extras.getString(EXTRA_STRING));
            return true;
        } else {
            return false;
        }
    }

}
