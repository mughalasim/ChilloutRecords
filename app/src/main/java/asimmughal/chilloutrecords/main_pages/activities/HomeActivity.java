package asimmughal.chilloutrecords.main_pages.activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import asimmughal.chilloutrecords.R;

public class HomeActivity extends ParentActivity {


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
        startActivity(new Intent(HomeActivity.this, ArtistActivity.class));
    }

    public void VIDEOS(View view) {

    }

    public void EQUIPMENTS(View view) {

    }

    public void UPCOMING(View view) {

    }
}
