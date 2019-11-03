package com.chilloutrecords.activities;

import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;

import com.chilloutrecords.R;
import com.chilloutrecords.utils.Helpers;

public class StartUpActivity extends AppCompatActivity {

    Helpers helper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        helper = new Helpers(StartUpActivity.this);

        setContentView(R.layout.activity_start_up);

    }

}

