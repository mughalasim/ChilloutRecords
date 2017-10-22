package asimmughal.chilloutrecords.main_pages.activities;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.RectF;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatDelegate;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.view.View;
import android.widget.LinearLayout;
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

import static asimmughal.chilloutrecords.utils.Database.RETRIEVE_LIKED_RESTAURANTS;
import static asimmughal.chilloutrecords.utils.Helpers.ADAPTER_DEFAULT;

public class FavoritesActivity extends ParentActivity {

    private ArrayList<RestaurantModel> restaurantsList = new ArrayList<>();
    private RecyclerView recyclerView;
    private RestaurantAdapter adapter;
    private LinearLayoutManager layoutManager;
    private SwipeRefreshLayout swiperefresh;
    private Paint p = new Paint();
    LinearLayout no_restaurants;

    private int CURRENT_PAGE = 0;
    private int LAST_PAGE = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_favorites);

        AppCompatDelegate.setCompatVectorFromResourcesEnabled(true);

        initialize(R.id.favorites, "FAVOURITES");

        findAllViews();

        initSwipe();

        swiperefresh.setOnRefreshListener(
                new SwipeRefreshLayout.OnRefreshListener() {
                    @Override
                    public void onRefresh() {
                        async(true);
                    }
                }
        );

        fetchFromDB();

    }

    @Override
    public void onResume() {
        super.onResume();
        updateDrawer();
        fetchFromDB();
    }

    private void findAllViews() {
        no_restaurants = (LinearLayout) findViewById(R.id.no_restaurants);
        swiperefresh = (SwipeRefreshLayout) findViewById(R.id.swiperefresh);
        recyclerView = (RecyclerView) findViewById(R.id.application_recycler);
        adapter = new RestaurantAdapter(FavoritesActivity.this, restaurantsList, ADAPTER_DEFAULT);
        recyclerView.setAdapter(adapter);
        recyclerView.setHasFixedSize(true);
        layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);

        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(recyclerView.getContext(),
                layoutManager.getOrientation());
        recyclerView.addItemDecoration(dividerItemDecoration);
    }

    private void noRestaurants(Boolean show) {
        if (show) {
            no_restaurants.setVisibility(View.VISIBLE);
        } else {
            no_restaurants.setVisibility(View.GONE);
        }
    }

    private void fetchFromDB() {
        restaurantsList.clear();
        restaurantsList = db.getAllRestaurants(RETRIEVE_LIKED_RESTAURANTS, "");
        recyclerView.invalidate();
        adapter.updateData(restaurantsList);
        adapter.notifyDataSetChanged();
        if (restaurantsList.size() <= 0) {
            noRestaurants(true);
        } else {
            noRestaurants(false);
        }
    }

    @Override
    public void onBackPressed() {
        finish();
    }

    // SWIPE FUNCTION ==============================================================================
    private void initSwipe() {
        ItemTouchHelper.SimpleCallback simpleItemTouchCallback = new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT) {

            @Override
            public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, RecyclerView.ViewHolder target) {
                return false;
            }

            @Override
            public void onSwiped(RecyclerView.ViewHolder viewHolder, int direction) {
                int position = viewHolder.getAdapterPosition();
                View v = recyclerView.getLayoutManager().findViewByPosition(position);
                TextView txtId = (TextView) v.findViewById(R.id.id);

                if (direction == ItemTouchHelper.LEFT && !txtId.getText().toString().equals("")) {
                    asyncLike(txtId.getText().toString(), position);

                } else if (direction == ItemTouchHelper.RIGHT && !txtId.getText().toString().equals("")) {
                    asyncLike(txtId.getText().toString(), position);
                }
            }

            @Override
            public void onChildDraw(Canvas c, RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, float dX, float dY, int actionState, boolean isCurrentlyActive) {

                Bitmap icon;
                if (actionState == ItemTouchHelper.ACTION_STATE_SWIPE) {

                    View itemView = viewHolder.itemView;
                    float height = (float) itemView.getBottom() - (float) itemView.getTop();
                    float width = height / 3;

                    if (dX > 0) {
                        p.setColor(getResources().getColor(R.color.grey));
                        RectF background = new RectF((float) itemView.getLeft(), (float) itemView.getTop(), dX, (float) itemView.getBottom());
                        c.drawRect(background, p);
                        icon = BitmapFactory.decodeResource(getResources(), R.drawable.main_logo_icon_white);
                        RectF icon_dest = new RectF((float) itemView.getLeft() + width, (float) itemView.getTop() + width, (float) itemView.getLeft() + 2 * width, (float) itemView.getBottom() - width);
                        c.drawBitmap(icon, null, icon_dest, p);
                    } else {
                        p.setColor(getResources().getColor(R.color.grey));
                        RectF background = new RectF((float) itemView.getRight() + dX, (float) itemView.getTop(), (float) itemView.getRight(), (float) itemView.getBottom());
                        c.drawRect(background, p);
                        icon = BitmapFactory.decodeResource(getResources(), R.drawable.main_logo_icon_white);
                        RectF icon_dest = new RectF((float) itemView.getRight() - 2 * width, (float) itemView.getTop() + width, (float) itemView.getRight() - width, (float) itemView.getBottom() - width);
                        c.drawBitmap(icon, null, icon_dest, p);
                    }
                }
                super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive);
            }
        };
        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(simpleItemTouchCallback);
        itemTouchHelper.attachToRecyclerView(recyclerView);
    }

    // ASYNC FUNCTIONS =============================================================================
    private void async(final Boolean startAfresh) {
        if (startAfresh) {
            CURRENT_PAGE = 0;
            restaurantsList.clear();
        }

        noRestaurants(false);
        swiperefresh.setRefreshing(true);
        RestaurantService restaurantService = RestaurantService.retrofit.create(RestaurantService.class);
        final Call<JsonObject> call = restaurantService.getFavourites(SharedPrefs.getUserID());

        call.enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                swiperefresh.setRefreshing(false);
                helper.progressDialog(false);
                try {
                    JSONObject main = new JSONObject(response.body().toString());
                    String ERROR = main.getString("error");
                    if (ERROR.equals("false")) {
                        Helpers.LogThis("PAGE NUMBER Internal: " + CURRENT_PAGE);
                        Helpers.LogThis("PAGE NUMBER Server: " + main.getInt("last_page"));
                        LAST_PAGE = main.getInt("last_page");

                        JSONArray jArray = main.getJSONArray("result");
                        int result_length = jArray.length();

                        Helpers.LogThis("Length of Response: " + result_length);

                        if (result_length <= 0 && CURRENT_PAGE < 1) {
                            db.deleteLikeTable();
                        } else {
                            CURRENT_PAGE++;
                            restaurantsList.clear();
                            for (int i = 0; i < result_length; i++) {
                                RestaurantModel restaurantModel = db.setRestaurants(jArray.getJSONObject(i));
                                restaurantsList.add(restaurantModel);
                            }
                        }
                        adapter.notifyDataSetChanged();
                    }

                    fetchFromDB();

                } catch (JSONException e) {
                    Helpers.LogThis("JSON exception " + e.toString());
                    fetchFromDB();

                } catch (Exception e) {
                    noRestaurants(true);
                    fetchFromDB();
                }
            }

            @Override
            public void onFailure(Call<JsonObject> call, Throwable t) {
                Helpers.LogThis("Failure: " + t.toString());
                Helpers.LogThis("Failure: " + call.toString());

                helper.progressDialog(false);
                swiperefresh.setRefreshing(false);

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
                    fetchFromDB();
                }
            }
        });
    }

    private void asyncLike(final String restaurant_id, final int position) {
        RestaurantService restaurantService = RestaurantService.retrofit.create(RestaurantService.class);
        final Call<JsonObject> call = restaurantService.favoriteOneRestaurant(restaurant_id, SharedPrefs.getUserID());

        call.enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {

                try {
                    JSONObject main = new JSONObject(response.body().toString());
                    Boolean success = main.getBoolean("success");
                    if (success) {
                        String action = main.getString("action");
                        if (!action.equals("create")) {
                            db.deleteLikedRestaurant(restaurant_id);
                            adapter.removeItem(position);
                            fetchFromDB();
                        }
                    } else {
                        helper.ToastMessage(FavoritesActivity.this, "Unable to delete favourite restaurant from list, Please try again later");
                        async(true);
                    }
                } catch (JSONException e) {
                    Helpers.LogThis("JSON exception " + e.toString());
                    helper.ToastMessage(FavoritesActivity.this, "Unable to delete favourite restaurant from list, Please try again later");
                    async(true);

                } catch (Exception e) {
                    Helpers.LogThis("Other exception " + e.toString());
                    helper.ToastMessage(FavoritesActivity.this, "Unable to delete favourite restaurant from list, Please try again later");
                    async(true);
                }
            }

            @Override
            public void onFailure(Call<JsonObject> call, Throwable t) {
                Helpers.LogThis("Failure: " + t.toString());
                Helpers.LogThis("Failure: " + call.toString());
                helper.ToastMessage(FavoritesActivity.this, "Unable delete favourite restaurant from list, Please try again later");
            }
        });
    }

    public void BTN_SEARCH(View view) {
        startActivity(new Intent(FavoritesActivity.this, RestaurantSearchActivity.class));
    }
}
