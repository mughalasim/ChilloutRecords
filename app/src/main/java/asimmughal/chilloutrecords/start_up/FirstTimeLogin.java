package asimmughal.chilloutrecords.start_up;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.google.gson.JsonObject;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import asimmughal.chilloutrecords.R;
import asimmughal.chilloutrecords.services.LoginService;
import asimmughal.chilloutrecords.utils.Helpers;
import asimmughal.chilloutrecords.utils.SharedPrefs;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static asimmughal.chilloutrecords.utils.Helpers.STR_LOGGED_OUT_EXTRA;
import static asimmughal.chilloutrecords.utils.Helpers.STR_LOGGED_OUT_TRUE;

public class FirstTimeLogin extends AppCompatActivity {

    private Helpers helper;
    private Toolbar toolbar;
    private BroadcastReceiver receiver;
    private LinearLayout LL_countries;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_firsttime_login);

        helper = new Helpers(FirstTimeLogin.this);

        listenExitBroadcast();

        findAllViews();

        setUpToolBarAndDrawer();

        asyncGetCountries();

    }

    @Override
    public void onResume() {
        super.onResume();
    }

    private void findAllViews() {
        toolbar = (Toolbar) findViewById(R.id.toolbar);

        LL_countries = (LinearLayout) findViewById(R.id.LL_countries);

    }

    private void setUpToolBarAndDrawer() {
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        LinearLayout toolbar_image = (LinearLayout) toolbar.findViewById(R.id.toolbar_image);
        toolbar_image.setVisibility(View.VISIBLE);
    }

    private void listenExitBroadcast() {
        IntentFilter filter = new IntentFilter();
        filter.addAction(helper.BroadcastValue);
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
    protected void onDestroy() {
        if (receiver != null) {
            unregisterReceiver(receiver);
            receiver = null;
        }
        super.onDestroy();
    }

    private void saveAndUpdate(String cityCode, String Country_Code) {
        SharedPrefs.setCountryCode(Country_Code);
        SharedPrefs.setCityCode(cityCode);
        finish();
        startActivity(new Intent(FirstTimeLogin.this, LoginActivity.class).putExtra(STR_LOGGED_OUT_EXTRA, STR_LOGGED_OUT_TRUE));
        SharedPrefs.setFirstTimeLogin(false);
    }


    // ASYNC GET ALL COUNTRIES =====================================================================

    private void asyncGetCountries() {
        helper.progressDialog(true);
        helper.setProgressDialogMessage(getString(R.string.progress_loading_countries));

        LoginService getCountries = LoginService.retrofit.create(LoginService.class);
        final Call<JsonObject> call = getCountries.getCountries();

        call.enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                helper.progressDialog(false);
                try {
                    JSONObject main = new JSONObject(response.body().toString());
                    String ERROR = main.getString("error");
                    if (ERROR.equals("false")) {

                        JSONArray jArray = main.getJSONArray("result");
                        int result_length = jArray.length();

                        LayoutInflater linf;
                        linf = LayoutInflater.from(FirstTimeLogin.this);

                        for (int i = 0; i < result_length; i++) {
                            JSONObject countryObject = jArray.getJSONObject(i);

                            String COUNTRY_NAME = countryObject.getString("name");
                            final String SUPPORT_NUMBER = countryObject.getString("support_number");
                            final String COUNTRY_CODE = countryObject.getString("code");
                            String COUNTRY_FLAG = countryObject.getString("country_flag");
                            String CITY_NAME = "";

                            JSONArray active_cities = countryObject.getJSONArray("active_cities");
                            int result_length2 = active_cities.length();
                            for (int w = 0; w < result_length2; w++) {
                                JSONObject cityObject = active_cities.getJSONObject(w);
                                final String CITY_ID = cityObject.getString("id");
                                CITY_NAME = cityObject.getString("name");

                                View child = linf.inflate(R.layout.list_item_countries, null);

                                TextView country_name = (TextView) child.findViewById(R.id.country_name);
                                TextView support_number = (TextView) child.findViewById(R.id.support_number);
                                TextView city_id = (TextView) child.findViewById(R.id.city_id);
                                TextView city_name = (TextView) child.findViewById(R.id.city_name);
                                ImageView country_flag = (ImageView) child.findViewById(R.id.country_flag);

                                country_name.setText(COUNTRY_NAME);
                                support_number.setText(SUPPORT_NUMBER);
                                city_id.setText(CITY_ID);
                                city_name.setText(CITY_NAME);
                                Glide.with(FirstTimeLogin.this).load(COUNTRY_FLAG).into(country_flag);

                                child.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        saveAndUpdate(CITY_ID, COUNTRY_CODE);
                                        SharedPrefs.setSupportNumber(SUPPORT_NUMBER);
                                    }
                                });

                                LL_countries.addView(child);
                                helper.animate_slide_in(child, 900, 20 * w);
                            }
                        }

                    } else {
                        failed_to_update();
                    }

                } catch (JSONException e) {
                    Helpers.LogThis("JSON exception " + e.toString());
                    failed_to_update();

                } catch (Exception e) {
                    failed_to_update();
                    Helpers.LogThis("Other exception " + e.toString());
                }
            }

            @Override
            public void onFailure(Call<JsonObject> call, Throwable t) {
                Helpers.LogThis("Failure: " + t.toString());
                Helpers.LogThis("Failure: " + call.toString());
                helper.progressDialog(false);
                failed_to_update();
            }
        });
    }

    public void failed_to_update() {
        final Snackbar snackBar = Snackbar.make(findViewById(R.id.drawer_layout),
                getString(R.string.error_connection), Snackbar.LENGTH_INDEFINITE);
        snackBar.setAction("Retry", new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                snackBar.dismiss();
                asyncGetCountries();
            }
        });
        snackBar.show();
    }

}
