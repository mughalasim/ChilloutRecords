package asimmughal.chilloutrecords.main_pages.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.design.widget.Snackbar;
import android.support.v4.widget.DrawerLayout;
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
import asimmughal.chilloutrecords.services.RestaurantService;
import asimmughal.chilloutrecords.utils.Helpers;
import asimmughal.chilloutrecords.utils.SharedPrefs;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ChangeLocationActivity extends ParentActivity {

    private LinearLayout LL_countries;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_change_location);

        initialize(R.id.change_location, "CHANGE LOCATION");

        findAllViews();

        int count = db.getAllAreas().size();
        if (count < 1) {
            asyncFetchAreas(SharedPrefs.getCityCode());
        }

        asyncGetCountries();
    }

    private void findAllViews() {
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        navigationView = (NavigationView) findViewById(R.id.nav_view);
        LL_countries = (LinearLayout) findViewById(R.id.LL_countries);

    }

    // ASYNC FUNCTIONS =============================================================================

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
                        linf = LayoutInflater.from(ChangeLocationActivity.this);
                        LL_countries.removeAllViews();

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
                                Glide.with(ChangeLocationActivity.this).load(COUNTRY_FLAG).into(country_flag);

                                if (CITY_ID.equals(SharedPrefs.getCityCode())) {
                                    child.setBackgroundColor(getResources().getColor(R.color.orange));
                                }

                                child.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        if (helper.validateIsLoggedIn()) {
                                            asyncUpdateUser(CITY_ID, COUNTRY_CODE);
                                            SharedPrefs.setSupportNumber(SUPPORT_NUMBER);
                                        }
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
                getString(R.string.error_500), Snackbar.LENGTH_INDEFINITE);
        snackBar.setAction("Retry", new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                snackBar.dismiss();
                asyncGetCountries();
            }
        });
        snackBar.show();
    }

    private void asyncUpdateUser(String City_Code, String country_code) {
        helper.progressDialog(true);
        helper.setProgressDialogMessage(getString(R.string.progress_loading_location));

        RestaurantService restaurantService = RestaurantService.retrofit.create(RestaurantService.class);
        final Call<JsonObject> call = restaurantService.update_user(
                SharedPrefs.getUserFirstName(),
                SharedPrefs.getUserLastName(),
                SharedPrefs.getUserEmail(),
                City_Code,
                country_code,
                "",
                SharedPrefs.getUserPhone(),
                SharedPrefs.getUserDOB(),
                SharedPrefs.getUserFacebookID()
        );

        call.enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                helper.progressDialog(false);
                try {
                    JSONObject main = new JSONObject(response.body().toString());
                    String request_msg = main.getString("success");
                    if (request_msg.equals("true")) {

                        Helpers.LogThis(response.body().toString());
                        JSONObject user = main.getJSONObject("user");

                        SharedPrefs.setUserFirstName(user.getString("first_name"));
                        SharedPrefs.setUserLastName(user.getString("last_name"));
                        SharedPrefs.setUserEmail(user.getString("email"));
//                        if(!user.getString("country_code").equals(SharedPrefs.getCountryCode())){
//                            db.deleteRestaurantTable();
//                        }
                        SharedPrefs.setCountryCode(user.getString("country_code"));

                        JSONObject city = user.getJSONObject("city");
                        SharedPrefs.setCityCode(city.getString("id"));

                        asyncFetchAreas(city.getString("id"));

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

                if (helper.validateInternetNotPresent()) {
                    final Snackbar snackBar = Snackbar.make(findViewById(R.id.drawer_layout),
                            getString(R.string.error_connection), Snackbar.LENGTH_INDEFINITE);
                    snackBar.setAction("Retry", new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            snackBar.dismiss();
                            asyncUpdateUser(SharedPrefs.getCityCode(), SharedPrefs.getCountryCode());
                        }
                    });
                    snackBar.show();
                    failed_to_update();
                }
            }
        });
    }

    private void asyncFetchAreas(String city_code) {
        helper.progressDialog(true);
        helper.setProgressDialogMessage(getString(R.string.progress_loading_area_update));
        RestaurantService restaurantService = RestaurantService.retrofit.create(RestaurantService.class);
        final Call<JsonObject> call = restaurantService.getAllAreas(city_code);

        call.enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                helper.progressDialog(false);
                try {
                    JSONObject main = new JSONObject(response.body().toString());
                    String request_msg = main.getString("error");

                    if (request_msg.equals("false")) {
                        db.deleteAreaTable();
                        JSONArray jArray = main.getJSONArray("result");
                        db.setArea(
                                "0",
                                "All"
                        );

                        for (int i = 0; i < jArray.length(); i++) {
                            JSONObject json_data = jArray.getJSONObject(i);
                            db.setArea(
                                    json_data.getString("id"),
                                    json_data.getString("name")
                            );
                        }

                       startActivity(new Intent(ChangeLocationActivity.this, HomeActivity.class));

                        SharedPrefs.setFirstTimeLogin(false);
                    }
                } catch (JSONException e) {
                    Helpers.LogThis("JSON exception " + e.toString());
                    failed_to_update();

                } catch (Exception e) {
                    Helpers.LogThis("Other exception " + e.toString());
                    failed_to_update();
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

}
