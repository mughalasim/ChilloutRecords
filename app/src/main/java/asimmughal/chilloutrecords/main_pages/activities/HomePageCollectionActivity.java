package asimmughal.chilloutrecords.main_pages.activities;

import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.MotionEvent;
import android.view.View;
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
import asimmughal.chilloutrecords.utils.Database;
import asimmughal.chilloutrecords.utils.Helpers;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static asimmughal.chilloutrecords.utils.Helpers.ADAPTER_DEFAULT;

public class HomePageCollectionActivity extends ParentActivity {

    private ArrayList<RestaurantModel> restaurantsList = new ArrayList<>();
    private RecyclerView recyclerView;
    private LinearLayoutManager layoutManager;
    private SwipeRefreshLayout swiperefresh;
    private RestaurantAdapter adapter;
    private TextView pageTitle, pageDescription;
    private String
            Page_Title = "",
            Page_Description = "",
            TAG_LOG = "COLECTION PAGE: ";
    private String COLLECTION_ID = "";

    private boolean
            isBlockedScrollView = false;

    private int
            CURRENT_PAGE = 0,
            LAST_PAGE = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home_page_collection);

        initialize(R.id.home, "");

        findAllViews();

        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            COLLECTION_ID = extras.getString("collection_id");
            Page_Title = extras.getString("collection_name");
            Page_Description = extras.getString("collection_desc");

            if (Page_Title.equals("")) {
                pageTitle.setVisibility(View.GONE);
            } else {
                pageTitle.setText(Page_Title);
            }

            if (Page_Description.equals("")) {
                pageDescription.setVisibility(View.GONE);
            } else {
                pageDescription.setText(Page_Description);
            }

            setListeners();

            fetchFromDB();
        }


    }

    private void setListeners() {
        swiperefresh.setOnRefreshListener(
                new SwipeRefreshLayout.OnRefreshListener() {
                    @Override
                    public void onRefresh() {
                        async(true);
                    }
                }
        );

        recyclerView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                return isBlockedScrollView;
            }
        });
    }

    private void fetchFromDB() {
        restaurantsList.clear();
        restaurantsList = db.getAllRestaurants(Database.RETRIEVE_COLLECTION_RESTAURANTS, COLLECTION_ID);
        recyclerView.invalidate();
        adapter.updateData(restaurantsList);
        adapter.notifyDataSetChanged();
        async(true);
    }

    private void findAllViews() {
        pageTitle = (TextView) findViewById(R.id.restaurant_name);
        pageDescription = (TextView) findViewById(R.id.restaurant_location);

        swiperefresh = (SwipeRefreshLayout) findViewById(R.id.swiperefresh);
        recyclerView = (RecyclerView) findViewById(R.id.application_recycler);

        adapter = new RestaurantAdapter(HomePageCollectionActivity.this, restaurantsList, ADAPTER_DEFAULT);
        recyclerView.setAdapter(adapter);
        recyclerView.setHasFixedSize(true);
        layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);
        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(recyclerView.getContext(),
                layoutManager.getOrientation());
        recyclerView.addItemDecoration(dividerItemDecoration);

    }

    private void noRestaurants() {
        restaurantsList.clear();
        RestaurantModel restaurantModel = new RestaurantModel();
        restaurantsList.add(restaurantModel);
        recyclerView.setAdapter(adapter);
    }

    private void async(final Boolean startAfresh) {
        isBlockedScrollView = true;
        swiperefresh.setRefreshing(true);

        if (startAfresh) {
            CURRENT_PAGE = 0;
            restaurantsList.clear();
        }

        RestaurantService restaurantService = RestaurantService.retrofit.create(RestaurantService.class);
        final Call<JsonObject> call = restaurantService.getRestaurantCollection(
                COLLECTION_ID,
                String.valueOf(CURRENT_PAGE + 1)
        );

        call.enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                isBlockedScrollView = false;
                swiperefresh.setRefreshing(false);
                helper.progressDialog(false);
                try {
                    JSONObject main = new JSONObject(response.body().toString());
                    String request_msg = main.getString("error");

                    if (request_msg.equals("false")) {

                        Helpers.LogThis(TAG_LOG + "PAGE NUMBER Internal: " + CURRENT_PAGE);
                        Helpers.LogThis(TAG_LOG + "PAGE NUMBER Server: " + main.getInt("last_page"));

                        LAST_PAGE = main.getInt("last_page");
                        CURRENT_PAGE = main.getInt("current_page");

                        JSONArray jArray = main.getJSONArray("result");
                        int result_length = jArray.length();
                        Helpers.LogThis(TAG_LOG + "Length of Response: " + result_length);

                        if (result_length <= 0 && CURRENT_PAGE < 1) {
                            noRestaurants();
                        } else {
                            CURRENT_PAGE++;
                            for (int i = 0; i < result_length; i++) {
                                RestaurantModel restaurantModel = db.setRestaurants(jArray.getJSONObject(i));
                                restaurantsList.add(restaurantModel);
                            }
                        }
                        adapter.updateData(restaurantsList);
                        adapter.notifyDataSetChanged();

                        if (CURRENT_PAGE <= LAST_PAGE) {
                            swiperefresh.setRefreshing(true);
                            async(false);
                        }

                    } else {
                        noRestaurants();
                    }

                } catch (JSONException e) {
                    Helpers.LogThis("JSON exception " + e.toString());
                    noRestaurants();

                } catch (Exception e) {
                    noRestaurants();
                    Helpers.LogThis("Other exception " + e.toString());
                }
            }

            @Override
            public void onFailure(Call<JsonObject> call, Throwable t) {
                Helpers.LogThis("Failure: " + t.toString());
                Helpers.LogThis("Failure: " + call.toString());

                helper.progressDialog(false);
                swiperefresh.setRefreshing(false);
                isBlockedScrollView = false;

                if (helper.validateInternetNotPresent()) {
                    fetchFromDB();
                    final Snackbar snackBar = Snackbar.make(findViewById(R.id.drawer_layout),
                            getString(R.string.error_connection), Snackbar.LENGTH_INDEFINITE);
                    snackBar.setAction("Retry", new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            snackBar.dismiss();
                            async(true);
                        }
                    });
                    snackBar.show();
                } else {
                    noRestaurants();
                }
            }
        });
    }

}