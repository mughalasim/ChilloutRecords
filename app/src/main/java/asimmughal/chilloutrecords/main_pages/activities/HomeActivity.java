package asimmughal.chilloutrecords.main_pages.activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import asimmughal.chilloutrecords.R;

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
        startActivity(new Intent(HomeActivity.this, GeneralListActivity.class).putExtra(KEY, ARTISTS));
    }

    public void VIDEOS(View view) {
        startActivity(new Intent(HomeActivity.this, GeneralListActivity.class).putExtra(KEY, VIDEOS));
    }

    public void FREESTYLES(View view) {

    }

    public void UPCOMING(View view) {

    }
}
