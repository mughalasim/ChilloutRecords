package asimmughal.chilloutrecords.main_pages.activities;

import android.content.Context;
import android.content.Intent;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.location.LocationProvider;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.design.widget.Snackbar;
import android.support.v4.widget.DrawerLayout;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;

import com.google.gson.JsonObject;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import asimmughal.chilloutrecords.R;
import asimmughal.chilloutrecords.main_pages.adapters.RestaurantAdapter;
import asimmughal.chilloutrecords.main_pages.models.RestaurantModel;
import asimmughal.chilloutrecords.services.RestaurantService;
import asimmughal.chilloutrecords.utils.Helpers;
import asimmughal.chilloutrecords.utils.SharedPrefs;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static asimmughal.chilloutrecords.utils.Database.RETRIEVE_ALL_OFFERS;
import static asimmughal.chilloutrecords.utils.Database.RETRIEVE_ALL_RESTAURANTS;
import static asimmughal.chilloutrecords.utils.Helpers.ADAPTER_DEFAULT;
import static asimmughal.chilloutrecords.utils.Helpers.ADAPTER_DISTANCE;
import static asimmughal.chilloutrecords.utils.Helpers.ORDER_ASCENDING;
import static asimmughal.chilloutrecords.utils.Helpers.ORDER_DESCENDING;
import static asimmughal.chilloutrecords.utils.Helpers.SORT_A_Z;
import static asimmughal.chilloutrecords.utils.Helpers.SORT_FEATURED;
import static asimmughal.chilloutrecords.utils.Helpers.SORT_NEARBY;
import static asimmughal.chilloutrecords.utils.Helpers.SORT_RATING;

public class RestaurantSearchActivity extends ParentActivity {

    private ArrayList<RestaurantModel> restaurantsList = new ArrayList<>();
    private RecyclerView recyclerView;
    private final LinearLayoutManager layoutManager = new LinearLayoutManager(this);
    private SwipeRefreshLayout swiperefresh;
    private RestaurantAdapter restaurantAdapter;

    private boolean
            continue_pagination = true,
            isBlockedScrollView = false;

    private int
            pastVisibleItems,
            visibleItemCount,
            totalItemCount,
            CURRENT_PAGE = 0,
            LAST_PAGE = 1;

    private String
            SEARCH_QUERRY_STRING = "",
            SORT = "",
            ORDER = "",
            LATITUDE = "",
            LONGITUDE = "",
            TAG_CLICKED = "1",
            TAG_NOT_CLICKED = "0",
            TAG_LOG = "SEARCH PAGE: ",
            STR_BUNDLE_EXTRA = "";

    private RelativeLayout LL_search;

    private ImageView search_bar_icon, search_bar_cancel;

    private Spinner
            spinner_area,
            spinner_cuisine;

    private TextView
            category_nearby,
            category_featured,
            category_az,
            category_rating,
            search_bar_text;

    private EditText search_criteria;

    LocationManager locationManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_restaurant_search);

        initialize(R.id.find_restaurants, "SEARCH RESTAURANTS");

        findAllViews();

        setListeners();

        updateDrawer();

        resetCategories();

        updateSpinners();

        swiperefresh.setOnRefreshListener(
                new SwipeRefreshLayout.OnRefreshListener() {
                    @Override
                    public void onRefresh() {
                        asyncSearch(true);
                    }
                }
        );

        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                if (dy > 0) {
                    visibleItemCount = layoutManager.getChildCount();
                    totalItemCount = layoutManager.getItemCount();
                    pastVisibleItems = layoutManager.findFirstVisibleItemPosition();
                    if (continue_pagination) {
                        if ((visibleItemCount + pastVisibleItems) >= totalItemCount) {
                            swiperefresh.setRefreshing(true);
                            asyncSearch(false);
                            helper.LogThis(TAG_LOG + "Load more Restaurants");
                        }
                    }
                }
            }
        });

        recyclerView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                return isBlockedScrollView;
            }
        });

        handleExtraBundles();

    }

    private void handleExtraBundles() {
        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            STR_BUNDLE_EXTRA = extras.getString("extra");

            if (STR_BUNDLE_EXTRA != null && !STR_BUNDLE_EXTRA.equals("")) {
                if (STR_BUNDLE_EXTRA.equals("offer")) {
                    fetchFromDB_OFFER_RESTAURANTS();

                } else if (STR_BUNDLE_EXTRA.equals("nearby")) {
                    View nearby_view = findViewById(R.id.category_nearby);
                    nearby_view.setTag(TAG_NOT_CLICKED);
                    CATEGORIZE(nearby_view);

                } else {
                    fetchFromDB_ALL_RESTAURANTS();
                }
            }
        } else {
            fetchFromDB_ALL_RESTAURANTS();
        }
    }

    private void setListeners() {
        search_bar_cancel.setVisibility(View.GONE);
        search_bar_icon.setVisibility(View.VISIBLE);

        search_bar_text.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if (search_bar_text.getText().toString().length() > 1) {
                    search_bar_icon.setVisibility(View.GONE);
                    search_bar_cancel.setVisibility(View.VISIBLE);
                } else {
                    search_bar_icon.setVisibility(View.VISIBLE);
                    search_bar_cancel.setVisibility(View.GONE);
                }
            }
        });

        search_bar_cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                search_criteria.setText("");
                setSearchCriteria();
                fetchFromDB_ALL_RESTAURANTS();
            }
        });

        search_criteria.setOnKeyListener(new View.OnKeyListener() {
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if ((event.getAction() == KeyEvent.ACTION_DOWN) && (keyCode == KeyEvent.KEYCODE_ENTER)) {
                    InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(search_criteria.getWindowToken(), 0);
                    showSearchScreen(false);
                    setSearchCriteria();
                    if (search_criteria.getText().toString().equals("")) {
                        fetchFromDB_ALL_RESTAURANTS();
                    } else {
                        asyncSearch(true);
                    }
                    return true;
                }
                return false;
            }
        });

        search_criteria.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if (!search_criteria.getText().toString().equals("")) {
                    SEARCH_QUERRY_STRING = search_criteria.getText().toString().trim();
                } else {
                    SEARCH_QUERRY_STRING = "";
                }
            }
        });
    }

    private void fetchFromDB_ALL_RESTAURANTS() {
        restaurantsList.clear();
        restaurantsList = db.getAllRestaurants(RETRIEVE_ALL_RESTAURANTS, "");
        recyclerView.invalidate();
        restaurantAdapter.updateData(restaurantsList);
        restaurantAdapter.notifyDataSetChanged();
        CURRENT_PAGE = LAST_PAGE;

    }

    private void fetchFromDB_OFFER_RESTAURANTS() {
        restaurantsList.clear();
        restaurantsList = db.getAllRestaurants(RETRIEVE_ALL_OFFERS, "");
        recyclerView.invalidate();
        restaurantAdapter.updateData(restaurantsList);
        restaurantAdapter.notifyDataSetChanged();
        CURRENT_PAGE = LAST_PAGE;
    }

    private void updateSpinners() {
        ArrayAdapter<String> dataAdapter = new ArrayAdapter<>(this,
                R.layout.spinner_items, db.getAllCuisines());
        spinner_cuisine.setAdapter(dataAdapter);
        ArrayAdapter<String> dataAdapter2 = new ArrayAdapter<>(this,
                R.layout.spinner_items, db.getAllAreas());
        spinner_area.setAdapter(dataAdapter2);
    }

    @Override
    public void onResume() {
        super.onResume();
        updateDrawer();
        updateSpinners();
    }

    @Override
    protected void onPause() {
        super.onPause();
        System.gc();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.restaurant_search_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.home:
                startActivity(new Intent(RestaurantSearchActivity.this, HomeActivity.class));
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    private void findAllViews() {

        swiperefresh = (SwipeRefreshLayout) findViewById(R.id.swiperefresh);
        drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        navigationView = (NavigationView) findViewById(R.id.nav_view);
        recyclerView = (RecyclerView) findViewById(R.id.application_recycler);
        restaurantAdapter = new RestaurantAdapter(RestaurantSearchActivity.this, restaurantsList, ADAPTER_DEFAULT);
        recyclerView.setAdapter(restaurantAdapter);

        search_bar_text = (TextView) findViewById(R.id.search_bar_text);
        search_bar_icon = (ImageView) findViewById(R.id.search_bar_icon);
        search_bar_cancel = (ImageView) findViewById(R.id.search_bar_cancel);

        spinner_area = (Spinner) findViewById(R.id.spinner_area);
        spinner_cuisine = (Spinner) findViewById(R.id.spinner_cuisine);

        category_nearby = (TextView) findViewById(R.id.category_nearby);
        category_featured = (TextView) findViewById(R.id.category_featured);
        category_az = (TextView) findViewById(R.id.category_az);
        category_rating = (TextView) findViewById(R.id.category_rating);

        recyclerView.setHasFixedSize(true);
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        recyclerView.setLayoutManager(layoutManager);

        search_criteria = (EditText) findViewById(R.id.search_criteria);
        helper.setDefaultEditTextSelectionMode(search_criteria);

        LL_search = (RelativeLayout) findViewById(R.id.LL_search);
        showSearchScreen(false);

        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);

    }

    @Override
    public void onBackPressed() {
        if (LL_search.getVisibility() == View.VISIBLE) {
            showSearchScreen(false);
        } else {
            finish();
        }
    }

    private void noRestaurants() {
        recyclerView.invalidate();
        restaurantsList.clear();
        RestaurantModel restaurantModel = new RestaurantModel();
        restaurantsList.add(restaurantModel);
        restaurantAdapter.notifyDataSetChanged();
    }

    public void SEARCH(View view) {
        setSearchCriteria();
        showSearchScreen(false);
        if (search_criteria.getText().toString().equals("")) {
            fetchFromDB_ALL_RESTAURANTS();
        } else {
            asyncSearch(true);
        }
    }

    // SEARCH MANAGEMENT ===========================================================================

    private void showSearchScreen(Boolean show) {
        if (show) {
            LL_search.setVisibility(View.VISIBLE);
            recyclerView.setVisibility(View.GONE);
            InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.showSoftInputFromInputMethod(search_criteria.getWindowToken(), 0);
            getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);
        } else {
            LL_search.setVisibility(View.GONE);
            recyclerView.setVisibility(View.VISIBLE);
            InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(search_criteria.getWindowToken(), 0);
            getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);

        }
    }

    public void START_SEARCH(View view) {
        showSearchScreen(true);
    }

    public void setSearchCriteria() {
        String criteria_string = "", area_string = "", cuisine_string = "";
        if (!SEARCH_QUERRY_STRING.equals("")) {
            criteria_string = " for \"" + SEARCH_QUERRY_STRING + "\"";
        }

        if (spinner_area.getSelectedItemPosition() > 0) {
            area_string = " in " + spinner_area.getSelectedItem().toString();
        }

        if (spinner_cuisine.getSelectedItemPosition() > 0) {
            cuisine_string = " for " + spinner_cuisine.getSelectedItem().toString() + " Restaurants";
        }

        criteria_string = criteria_string + area_string + cuisine_string;

        if (!criteria_string.equals("")) {
            search_bar_text.setText("Results" + criteria_string);

        } else {
            search_bar_text.setText("");

        }
    }


    // CATEGORIES MANAGEMENT =======================================================================

    public void CATEGORIZE(View view) {
        disableFetchLocation();

        if (view.getTag().equals(TAG_CLICKED)) {
            resetCategories();
            SORT = "";
            fetchFromDB_ALL_RESTAURANTS();

        } else {

            resetCategories();
            if (view.getId() == R.id.category_nearby) {
                category_nearby.setTag(TAG_CLICKED);
                SORT = SORT_NEARBY;
                fetchLocation();

            } else if (view.getId() == R.id.category_featured) {
                category_featured.setBackgroundResource(R.drawable.bckgrd_button_yes);
                category_featured.setTextColor(getResources().getColor(R.color.white));
                SORT = SORT_FEATURED;
                ORDER = ORDER_ASCENDING;
                category_featured.setTag(TAG_CLICKED);
                asyncSearch(true);

            } else if (view.getId() == R.id.category_az) {
                category_az.setBackgroundResource(R.drawable.bckgrd_button_yes);
                category_az.setTextColor(getResources().getColor(R.color.white));
                SORT = SORT_A_Z;
                ORDER = ORDER_ASCENDING;
                category_az.setTag(TAG_CLICKED);
                asyncSearch(true);

            } else if (view.getId() == R.id.category_rating) {
                category_rating.setBackgroundResource(R.drawable.bckgrd_button_yes);
                category_rating.setTextColor(getResources().getColor(R.color.white));
                SORT = SORT_RATING;
                category_rating.setTag(TAG_CLICKED);
                ORDER = ORDER_DESCENDING;
                asyncSearch(true);
            }
        }
    }

    private void resetCategories() {
        category_nearby.setBackgroundResource(R.drawable.bckgrd_button_no_search);
        category_featured.setBackgroundResource(R.drawable.bckgrd_button_no_search);
        category_az.setBackgroundResource(R.drawable.bckgrd_button_no_search);
        category_rating.setBackgroundResource(R.drawable.bckgrd_button_no_search);

        category_nearby.setTextColor(getResources().getColor(R.color.dark_grey));
        category_featured.setTextColor(getResources().getColor(R.color.dark_grey));
        category_az.setTextColor(getResources().getColor(R.color.dark_grey));
        category_rating.setTextColor(getResources().getColor(R.color.dark_grey));

        category_nearby.setTag(TAG_NOT_CLICKED);
        category_featured.setTag(TAG_NOT_CLICKED);
        category_az.setTag(TAG_NOT_CLICKED);
        category_rating.setTag(TAG_NOT_CLICKED);

    }


    // LOCATIONS MANAGEMENT  =======================================================================

    private void fetchLocation() {
        locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
        if (!locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
            helper.dialogNoGPS(RestaurantSearchActivity.this);
        } else {
            category_nearby.setBackgroundResource(R.drawable.bckgrd_button_yes);
            category_nearby.setTextColor(getResources().getColor(R.color.white));

            Helpers.LogThis(TAG_LOG + "NEARBY CLICKED");
            helper.setProgressDialogMessage("Fetching your current location, Please wait...");
            helper.progressDialog(true);

            Criteria criteria = new Criteria();
            String provider = locationManager.getBestProvider(criteria, true);
            Location location = locationManager.getLastKnownLocation(provider);
            if (location != null) {
                mLocationListener.onLocationChanged(location);
            }
            locationManager.requestLocationUpdates(provider, 30000, 0, mLocationListener);
        }
    }

    private void disableFetchLocation() {
        locationManager.removeUpdates(mLocationListener);
    }

    private final LocationListener mLocationListener = new LocationListener() {
        @Override
        public void onLocationChanged(final Location location) {
            LONGITUDE = String.valueOf(location.getLongitude());
            LATITUDE = String.valueOf(location.getLatitude());
            Helpers.LogThis(TAG_LOG + "LONGITUDE: " + LONGITUDE);
            Helpers.LogThis(TAG_LOG + "LATITUDE: " + LATITUDE);
            if (!LONGITUDE.equals("")) {
                asyncSearch(true);
            }
        }

        @Override
        public void onStatusChanged(String provider, int status, Bundle extras) {

            if (status == LocationProvider.OUT_OF_SERVICE) {
                Helpers.LogThis(TAG_LOG + "GPS Status: DISABLED");
                noRestaurants();
                LATITUDE = "";
                LONGITUDE = "";
                resetCategories();
                disableFetchLocation();
            } else {
                Helpers.LogThis(TAG_LOG + "GPS Status: OK");
            }
        }

        @Override
        public void onProviderEnabled(String provider) {
            if (category_nearby.getTag().equals(TAG_CLICKED)) {
                helper.ToastMessage(RestaurantSearchActivity.this, "GPS Enabled");
            }

        }

        @Override
        public void onProviderDisabled(String provider) {
            if (category_nearby.getTag().equals(TAG_CLICKED)) {
                helper.ToastMessage(RestaurantSearchActivity.this, "GPS Disabled");
                fetchFromDB_ALL_RESTAURANTS();
                resetCategories();
                LATITUDE = "";
                LONGITUDE = "";
            }
        }
    };


    // ASYNC FUNCTION ==============================================================================

    private void asyncSearch(final Boolean startAfresh) {
            isBlockedScrollView = true;
            if (startAfresh) {
                CURRENT_PAGE = 0;
                restaurantsList.clear();
            }

            if (CURRENT_PAGE >= 1) {
                helper.setProgressDialogMessage("Loading more restaurants, Please wait");
                helper.progressDialog(true);
            }

            swiperefresh.setRefreshing(true);
            RestaurantService restaurantService = RestaurantService.retrofit.create(RestaurantService.class);
            final Call<JsonObject> call = restaurantService.searchRestaurant(
                    SEARCH_QUERRY_STRING,
                    String.valueOf(CURRENT_PAGE + 1),
                    SharedPrefs.getCityCode(),
                    db.getAreaIDByName(spinner_area.getSelectedItem().toString()),
                    db.getCuisineIDByName(spinner_cuisine.getSelectedItem().toString()),
                    LATITUDE,
                    LONGITUDE,
                    SORT,
                    ORDER
            );

            call.enqueue(new Callback<JsonObject>() {
                @Override
                public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                    try {
                        JSONObject main = new JSONObject(response.body().toString());
                        String ERROR = main.getString("error");
                        if (ERROR.equals("false")) {

                            Helpers.LogThis(TAG_LOG + "PAGE NUMBER Internal: " + CURRENT_PAGE);
                            Helpers.LogThis(TAG_LOG + "PAGE NUMBER Server: " + main.getInt("last_page"));

                            LAST_PAGE = main.getInt("last_page");
                            CURRENT_PAGE = main.getInt("current_page");

                            if (CURRENT_PAGE >= LAST_PAGE) {
                                continue_pagination = false;
                            }

                            JSONArray jArray = main.getJSONArray("result");
                            int result_length = jArray.length();

                            Helpers.LogThis(TAG_LOG + "Length of Response: " + result_length);

                            if (result_length <= 0 && CURRENT_PAGE <= 1) {
                                Helpers.LogThis(TAG_LOG + "No Restaurants");
                                noRestaurants();
                            } else {
                                CURRENT_PAGE++;
                                for (int i = 0; i < result_length; i++) {
                                    RestaurantModel restaurantModel = db.setRestaurants(jArray.getJSONObject(i));
                                    restaurantsList.add(restaurantModel);
                                }
                                continue_pagination = true;
                            }

                            if (startAfresh) {
                                restaurantAdapter.notifyDataSetChanged();
                                if (SORT.equals(SORT_NEARBY) && !LONGITUDE.equals("")) {
                                    restaurantAdapter = new RestaurantAdapter(RestaurantSearchActivity.this, restaurantsList, ADAPTER_DISTANCE);
                                } else {
                                    restaurantAdapter = new RestaurantAdapter(RestaurantSearchActivity.this, restaurantsList, ADAPTER_DEFAULT);
                                }
                                recyclerView.setAdapter(restaurantAdapter);
                            } else {
                                if (CURRENT_PAGE == 1) {
                                    recyclerView.postInvalidate();
                                    restaurantAdapter.updateData(restaurantsList);
                                }
                                restaurantAdapter.notifyDataSetChanged();
                            }
                        } else {
                            noRestaurants();
                        }
                    } catch (JSONException e) {
                        Helpers.LogThis(TAG_LOG + "JSON exception " + e.toString());
                        noRestaurants();

                    } catch (Exception e) {
                        noRestaurants();
                        Helpers.LogThis(TAG_LOG + "Other exception " + e.toString());
                    }

                    swiperefresh.setRefreshing(false);
                    helper.progressDialog(false);
                    isBlockedScrollView = false;
                }

                @Override
                public void onFailure(Call<JsonObject> call, Throwable t) {
                    Helpers.LogThis(TAG_LOG + "Failure: " + t.toString());
                    Helpers.LogThis(TAG_LOG + "Failure: " + call.toString());

                    helper.progressDialog(false);
                    swiperefresh.setRefreshing(false);

                    if (helper.validateInternetNotPresent()) {
                        fetchFromDB_ALL_RESTAURANTS();
                        final Snackbar snackBar = Snackbar.make(findViewById(R.id.drawer_layout),
                                getString(R.string.error_connection), Snackbar.LENGTH_INDEFINITE);
                        snackBar.setAction("Retry", new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                snackBar.dismiss();
                                asyncSearch(true);
                            }
                        });
                        snackBar.show();
                    } else {
                        noRestaurants();
                    }
                    isBlockedScrollView = false;
                }

            });
    }


}