package asimmughal.chilloutrecords.main_pages.activities;

import android.Manifest;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.app.AppCompatDelegate;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.Html;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RatingBar;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.facebook.appevents.AppEventsLogger;
import com.facebook.share.model.ShareLinkContent;
import com.facebook.share.widget.ShareDialog;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.gson.JsonObject;
import com.makeramen.roundedimageview.RoundedImageView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import asimmughal.chilloutrecords.R;
import asimmughal.chilloutrecords.main_pages.adapters.GalleryAdapterLarge;
import asimmughal.chilloutrecords.main_pages.adapters.GalleryAdapterSmall;
import asimmughal.chilloutrecords.main_pages.adapters.RestaurantAdapter;
import asimmughal.chilloutrecords.main_pages.adapters.ReviewAdapter;
import asimmughal.chilloutrecords.main_pages.models.GalleryModel;
import asimmughal.chilloutrecords.main_pages.models.RestaurantModel;
import asimmughal.chilloutrecords.main_pages.models.ReviewModel;
import asimmughal.chilloutrecords.services.RestaurantService;
import asimmughal.chilloutrecords.utils.Database;
import asimmughal.chilloutrecords.utils.Helpers;
import asimmughal.chilloutrecords.utils.SharedPrefs;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static asimmughal.chilloutrecords.BuildConfig.YUMMY_CARD;
import static asimmughal.chilloutrecords.utils.Helpers.ADAPTER_DISTANCE;
import static asimmughal.chilloutrecords.utils.Helpers.SORT_NEARBY;

public class RestaurantDetailsActivity extends AppCompatActivity implements OnMapReadyCallback {
    Helpers helper;
    private Toolbar toolbar;
    CollapsingToolbarLayout collapsingToolbarLayout;
    private BroadcastReceiver receiver;

    static final int
            REQUEST_CAMERA = 333,
            SELECT_FILE = 222;

    private ImageView
            picture_restaurant,
            picture_delete,
            picture_add;

    private RecyclerView
            restaurant_recyclerView,
            gallery_recyclerView_small,
            gallery_recyclerView_large,
            review_recyclerView;

    private ArrayList<GalleryModel>
            galleryList_small = new ArrayList<>(),
            galleryList_large = new ArrayList<>();
    private ArrayList<RestaurantModel>
            restaurantsList = new ArrayList<>();
    private ArrayList<ReviewModel>
            reviewList = new ArrayList<>();

    private GalleryAdapterSmall galleryAdapterSmall;
    private GalleryAdapterLarge galleryAdapterLarge;
    private RestaurantAdapter restaurant_adapter;
    private ReviewAdapter review_adapter;

    private final LinearLayoutManager
            gallery_layoutManager_small = new LinearLayoutManager(this),
            gallery_layoutManager_large = new LinearLayoutManager(this),
            restaurant_layoutManager = new LinearLayoutManager(this),
            review_layoutManager = new LinearLayoutManager(this);

    private WebView pdf_menu_view;

    private Database db;
    private Menu menu;

    private String[] TERMS_AND_CONDITIONS;

    private RatingBar restaurant_rating;
    private RelativeLayout
            TB_BTN_Info,
            TB_BTN_PDF,
            TB_BTN_Map,
            TB_BTN_Reviews,
            TB_BTN_Share;

    private TextView
            txt_restaurant_name,
            txt_restaurant_location,
            txt_restaurant_cuisine,
            txt_restaurant_description,
            day1open, day1close,
            day2open, day2close,
            day3open, day3close,
            day4open, day4close,
            day5open, day5close,
            day6open, day6close,
            day7open, day7close;

    private RelativeLayout
            llday1,
            llday2,
            llday3,
            llday4,
            llday5,
            llday6,
            llday7;

    public Float
            GEOLAT = 0.0f,
            GEOLNG = 0.0f,
            ZOOM_IN = 17.5f;

    private LinearLayout
            btn_call_support,
            LL_information,
            LL_loading,
            LL_error,
            LL_other_offers,
            LL_map,
            LL_pdf,
            LL_review,
            LL_gallery_large,
            LL_offers,
            TB_Layout,
            btn_review;

    private String
            STATE_INFORMATION = "INFORMATION",
            STATE_REVIEWS = "REVIEWS",
            STATE_MAP = "MAP",
            STATE_PDF = "PDF",
            STATE_LOADING = "LOADING",
            STATE_ERROR = "ERROR",
            STATE_OTHER_OFFERS = "OTHER_OFFERS",
            STR_RESTAURANT_ID = "",
            STR_PAGE_TITLE = "",
            STR_PAGE_DESCRIPTION = "",
            STR_SHOW_OFFER = "",
            STR_PICTURE = "",
            STR_SHARELINK = "Hey, check out this restaurant on EatOut: https://eatout.co.ke",
            STR_LATITUDE = "",
            STR_LONGITUDE = "",
            STR_MESSAGE_BODY = "";

    private GoogleMap googleMap;

    private int
            CURRENT_PAGE = 0,
            LAST_PAGE = 1,
            pastVisibleItems,
            visibleItemCount,
            totalItemCount;

    private boolean bool_loading = true;

    private SwipeRefreshLayout
            review_swiperefresh;
//            restaurant_swiperefresh;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_restaurant_details);

        AppCompatDelegate.setCompatVectorFromResourcesEnabled(true);

        helper = new Helpers(RestaurantDetailsActivity.this);
        db = new Database();

        listenExitBroadcast();

        findAllViews();

        STR_RESTAURANT_ID = getIntent().getStringExtra("restaurant_id");
        STR_PAGE_TITLE = getIntent().getStringExtra("restaurant_name");
        STR_PAGE_DESCRIPTION = getIntent().getStringExtra("restaurant_desc");
        STR_SHOW_OFFER = getIntent().getStringExtra("offer");

        txt_restaurant_name.setText(STR_PAGE_TITLE);

        setUpToolBarAndTabs();

        setOnclickListeners();

        asyncInformation();

        asyncReview(true);


    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        this.menu = menu;
        inflater.inflate(R.menu.restaurant_details_menu, menu);

        if (db.getLikedRestaurantIDMatch(STR_RESTAURANT_ID)) {
            setUpLikeButton(true);
        } else {
            setUpLikeButton(false);
        }

        return true;
    }

    private void hideOption(int id) {
        MenuItem item = menu.findItem(id);
        item.setVisible(false);
    }

    private void showOption(int id) {
        MenuItem item = menu.findItem(id);
        item.setVisible(true);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (helper.validateIsLoggedIn()) {
            switch (item.getItemId()) {
                case R.id.liked:
                    asyncLike();
                    break;
                case R.id.unliked:
                    asyncLike();
                    break;
            }
        }
        return super.onOptionsItemSelected(item);
    }


    private void findAllViews() {
        toolbar = (Toolbar) findViewById(R.id.toolbar);

        // Floating Action Buttons
        btn_call_support = (LinearLayout) findViewById(R.id.btn_call_support);

        btn_review = (LinearLayout) findViewById(R.id.btn_review);

        // Collapsing ToolBar Items
        txt_restaurant_name = (TextView) findViewById(R.id.restaurant_name);
        txt_restaurant_location = (TextView) findViewById(R.id.restaurant_location);
        txt_restaurant_cuisine = (TextView) findViewById(R.id.restaurant_cuisine);
        picture_restaurant = (ImageView) findViewById(R.id.restaurant_image);
        collapsingToolbarLayout = (CollapsingToolbarLayout) findViewById(R.id.collapsing_toolbar);

        // Information Screen items
        restaurant_rating = (RatingBar) findViewById(R.id.rating);
        txt_restaurant_description = (TextView) findViewById(R.id.description);

        // Review Screen Items
        review_swiperefresh = (SwipeRefreshLayout) findViewById(R.id.swiperefresh);
        review_recyclerView = (RecyclerView) findViewById(R.id.review_recycler);
        review_adapter = new ReviewAdapter(reviewList);
        review_recyclerView.setAdapter(review_adapter);
        review_recyclerView.setHasFixedSize(true);
        review_recyclerView.setLayoutManager(review_layoutManager);
        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(review_recyclerView.getContext(),
                review_layoutManager.getOrientation());
        review_recyclerView.addItemDecoration(dividerItemDecoration);


        // PDF Menu Screen
        pdf_menu_view = (WebView) findViewById(R.id.pdf_menu_view);


        //More Offers
//        restaurant_swiperefresh = (SwipeRefreshLayout) findViewById(R.id.restaurant_swiperefresh);
        restaurant_recyclerView = (RecyclerView) findViewById(R.id.restaurant_recyclerView);
        restaurant_recyclerView.setHasFixedSize(true);

        restaurant_layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        restaurant_recyclerView.setLayoutManager(restaurant_layoutManager);

        restaurant_adapter = new RestaurantAdapter(RestaurantDetailsActivity.this, restaurantsList, ADAPTER_DISTANCE);
        restaurant_recyclerView.setAdapter(restaurant_adapter);


        // Showing Screens
        LL_information = (LinearLayout) findViewById(R.id.LL_information);
        LL_map = (LinearLayout) findViewById(R.id.LL_map);
        LL_review = (LinearLayout) findViewById(R.id.LL_review);
        LL_pdf = (LinearLayout) findViewById(R.id.LL_pdf);
        LL_gallery_large = (LinearLayout) findViewById(R.id.LL_gallery_large);

        LL_offers = (LinearLayout) findViewById(R.id.LL_offers);

        LL_loading = (LinearLayout) findViewById(R.id.LL_loading);
        LL_error = (LinearLayout) findViewById(R.id.LL_error);
        LL_other_offers = (LinearLayout) findViewById(R.id.LL_other_offers);

        // small Gallery Images
        gallery_recyclerView_small = (RecyclerView) findViewById(R.id.gallery_recycler_small);
        galleryAdapterSmall = new GalleryAdapterSmall(galleryList_small);
        gallery_recyclerView_small.setAdapter(galleryAdapterSmall);
        gallery_recyclerView_small.setHasFixedSize(true);
        gallery_layoutManager_small.setOrientation(LinearLayoutManager.HORIZONTAL);
        gallery_recyclerView_small.setLayoutManager(gallery_layoutManager_small);

        // large Gallery Images
        gallery_recyclerView_large = (RecyclerView) findViewById(R.id.gallery_recycler_large);
        galleryAdapterLarge = new GalleryAdapterLarge(galleryList_large);
        gallery_recyclerView_large.setAdapter(galleryAdapterLarge);
        gallery_recyclerView_large.setHasFixedSize(true);
        gallery_layoutManager_large.setOrientation(LinearLayoutManager.VERTICAL);
        gallery_recyclerView_large.setLayoutManager(gallery_layoutManager_large);

        // Bottom Toolbar Items
        TB_Layout = (LinearLayout) findViewById(R.id.TB_Layout);
        TB_BTN_Info = (RelativeLayout) findViewById(R.id.TB_BTN_Info);
        TB_BTN_PDF = (RelativeLayout) findViewById(R.id.TB_BTN_PDF);
        TB_BTN_Map = (RelativeLayout) findViewById(R.id.TB_BTN_Map);
        TB_BTN_Reviews = (RelativeLayout) findViewById(R.id.TB_BTN_Reviews);
        TB_BTN_Share = (RelativeLayout) findViewById(R.id.TB_BTN_Share);
        resetTB_BTNs();

        // Working Hours
        day1open = (TextView) findViewById(R.id.day1open);
        day1close = (TextView) findViewById(R.id.day1close);
        day2open = (TextView) findViewById(R.id.day2open);
        day2close = (TextView) findViewById(R.id.day2close);
        day3open = (TextView) findViewById(R.id.day3open);
        day3close = (TextView) findViewById(R.id.day3close);
        day4open = (TextView) findViewById(R.id.day4open);
        day4close = (TextView) findViewById(R.id.day4close);
        day5open = (TextView) findViewById(R.id.day5open);
        day5close = (TextView) findViewById(R.id.day5close);
        day6open = (TextView) findViewById(R.id.day6open);
        day6close = (TextView) findViewById(R.id.day6close);
        day7open = (TextView) findViewById(R.id.day7open);
        day7close = (TextView) findViewById(R.id.day7close);
        llday1 = (RelativeLayout) findViewById(R.id.llday1);
        llday2 = (RelativeLayout) findViewById(R.id.llday2);
        llday3 = (RelativeLayout) findViewById(R.id.llday3);
        llday4 = (RelativeLayout) findViewById(R.id.llday4);
        llday5 = (RelativeLayout) findViewById(R.id.llday5);
        llday6 = (RelativeLayout) findViewById(R.id.llday6);
        llday7 = (RelativeLayout) findViewById(R.id.llday7);

        llday1.setVisibility(View.GONE);
        llday2.setVisibility(View.GONE);
        llday3.setVisibility(View.GONE);
        llday4.setVisibility(View.GONE);
        llday5.setVisibility(View.GONE);
        llday6.setVisibility(View.GONE);
        llday7.setVisibility(View.GONE);

    }

    private void setUpLikeButton(Boolean pressed) {
        if (pressed) {
            showOption(R.id.liked);
            hideOption(R.id.unliked);
        } else {
            showOption(R.id.unliked);
            hideOption(R.id.liked);
        }
    }

    private void setUpToolBarAndTabs() {
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
    }

    private void setOnclickListeners() {

        btn_call_support.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (btn_call_support.getTag().toString().equals("")) {
                    helper.ToastMessage(RestaurantDetailsActivity.this, "No Number available to call, please try again later");

                } else if (btn_call_support.getTag().toString().equals(SharedPrefs.getSupportNumber())) {
                    dialogMakeCall("You are about to call Chillout Records, Do you wish to proceed?", btn_call_support.getTag().toString());

                } else {
                    dialogMakeCall("You are about to call " + txt_restaurant_name.getText().toString()
                            + ". Do you wish to proceed?", btn_call_support.getTag().toString());
                }
            }
        });

        btn_review.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (helper.validateIsLoggedIn()) {
                    captureFullReview(0);
                }
            }
        });


        review_swiperefresh.setOnRefreshListener(
                new SwipeRefreshLayout.OnRefreshListener() {
                    @Override
                    public void onRefresh() {
                        asyncReview(true);
                    }
                }
        );

//        restaurant_swiperefresh.setOnRefreshListener(
//                new SwipeRefreshLayout.OnRefreshListener() {
//                    @Override
//                    public void onRefresh() {
//                        asyncFetchRestaurants();
//                    }
//                }
//        );

        review_recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                if (dy > 0) //check for scroll down
                {
                    visibleItemCount = review_layoutManager.getChildCount();
                    totalItemCount = review_layoutManager.getItemCount();
                    pastVisibleItems = review_layoutManager.findFirstVisibleItemPosition();

                    if (bool_loading) {
                        if ((visibleItemCount + pastVisibleItems) >= totalItemCount) {
                            asyncReview(false);
                        }
                    }
                }
            }
        });

        TB_BTN_Info.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                updateScreen(STATE_INFORMATION);
            }
        });


        TB_BTN_PDF.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                updateScreen(STATE_PDF);
            }
        });

        TB_BTN_Map.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                updateScreen(STATE_MAP);
            }
        });

        TB_BTN_Reviews.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                updateScreen(STATE_REVIEWS);
            }
        });

        TB_BTN_Share.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (helper.validateIsLoggedIn()) {

                    final Dialog dialog = new Dialog(RestaurantDetailsActivity.this);
                    dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                    dialog.setContentView(R.layout.dialog_share);
                    final ImageView share_facebook = (ImageView) dialog.findViewById(R.id.share_facebook);
                    final ImageView share_email = (ImageView) dialog.findViewById(R.id.share_email);
                    final ImageView share_messenger = (ImageView) dialog.findViewById(R.id.share_messenger);
                    final ImageView share_sms = (ImageView) dialog.findViewById(R.id.share_sms);
                    final ImageView share_whatsapp = (ImageView) dialog.findViewById(R.id.share_whatsapp);

                    share_facebook.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            if (helper.validateAppIsInstalled("com.facebook.katana")) {
                                ShareLinkContent content = new ShareLinkContent.Builder()
                                        .setContentUrl(Uri.parse(STR_SHARELINK))
                                        .build();
                                ShareDialog.show(RestaurantDetailsActivity.this, content);
                                dialog.cancel();
                            }
                        }
                    });

                    share_email.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            Intent emailIntent = new Intent(Intent.ACTION_SEND);
                            emailIntent.setType("text/html");
                            emailIntent.putExtra(Intent.EXTRA_SUBJECT, getString(R.string.app_name));
                            emailIntent.putExtra(Intent.EXTRA_TEXT, STR_SHARELINK);
                            startActivity(Intent.createChooser(emailIntent, "Send Email"));
                            dialog.cancel();
                        }
                    });

                    share_messenger.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            if (helper.validateAppIsInstalled("com.facebook.orca")) {
                                Intent messengerIntent = new Intent();
                                messengerIntent.setAction(Intent.ACTION_SEND);
                                messengerIntent.putExtra(Intent.EXTRA_TEXT, STR_SHARELINK);
                                messengerIntent.setType("text/plain");
                                messengerIntent.setPackage("com.facebook.orca");
                                startActivity(messengerIntent);
                            }
                        }
                    });

                    share_sms.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            Intent smsIntent = new Intent(Intent.ACTION_VIEW);
                            smsIntent.putExtra("sms_body", STR_SHARELINK);
                            smsIntent.setType("vnd.android-dir/mms-sms");
                            startActivity(smsIntent);
                            dialog.cancel();
                        }
                    });

                    share_whatsapp.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            if (helper.validateAppIsInstalled("com.whatsapp")) {
                                Intent whatsappIntent = new Intent(Intent.ACTION_SEND);
                                whatsappIntent.setType("text/plain");
                                whatsappIntent.setPackage("com.whatsapp");
                                whatsappIntent.putExtra(Intent.EXTRA_TEXT, STR_SHARELINK);
                                startActivity(whatsappIntent);
                                dialog.cancel();
                            }
                        }
                    });

                    dialog.setCancelable(true);
                    dialog.show();


                }
            }
        });
    }


    private void dialogMakeCall(final String Message, final String TelephoneNumber) {
        final Dialog dialog = new Dialog(RestaurantDetailsActivity.this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.dialog_confirm);
        final TextView txtMessage = (TextView) dialog.findViewById(R.id.txtMessage);
        final TextView txtOk = (TextView) dialog.findViewById(R.id.txtOk);
        final TextView txtCancel = (TextView) dialog.findViewById(R.id.txtCancel);
        final TextView txtTitle = (TextView) dialog.findViewById(R.id.txtTitle);
        txtOk.setText("YES");
        txtCancel.setText("NO");
        txtCancel.setVisibility(View.VISIBLE);

        txtTitle.setText("Make a call?");
        txtMessage.setText(Message);

        txtCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.cancel();
            }
        });

        txtOk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.cancel();
                if (ActivityCompat.checkSelfPermission
                        (RestaurantDetailsActivity.this, Manifest.permission.CALL_PHONE) ==
                        PackageManager.PERMISSION_GRANTED) {
                    Intent intent = new Intent(Intent.ACTION_CALL);
                    intent.setData(Uri.parse("tel:" + TelephoneNumber));
                    startActivity(intent);
                    return;
                } else {
                    helper.myDialog(RestaurantDetailsActivity.this,
                            "Alert", "Please grant calling permissions to the Chillout Records App to carry out this function");
                }
            }
        });
        dialog.show();
    }

    private void dialogShowTnC(final int position) {
        final Dialog dialog = new Dialog(RestaurantDetailsActivity.this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.dialog_confirm);
        final TextView txtMessage = (TextView) dialog.findViewById(R.id.txtMessage);
        final TextView txtOk = (TextView) dialog.findViewById(R.id.txtOk);
        final TextView txtCancel = (TextView) dialog.findViewById(R.id.txtCancel);
        final TextView txtTitle = (TextView) dialog.findViewById(R.id.txtTitle);
        txtOk.setText(getString(R.string.txt_back));
        txtCancel.setVisibility(View.GONE);
        txtOk.setVisibility(View.VISIBLE);

        txtTitle.setText("Terms and Conditions");
        txtMessage.setText(Html.fromHtml(TERMS_AND_CONDITIONS[position]));
        txtOk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.cancel();
            }
        });
        dialog.show();
    }


    // PAY BILL Functionality ======================================================================

    public void PAY_BILL(View view) {
        if (helper.validateIsLoggedIn()) {

            final Dialog dialog = new Dialog(RestaurantDetailsActivity.this);
            dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
            dialog.setContentView(R.layout.dialog_paybill);
            final TextView txtOk = (TextView) dialog.findViewById(R.id.txtOk);
            final TextView txtCancel = (TextView) dialog.findViewById(R.id.txtCancel);

            final TextView user_phone_display = (TextView) dialog.findViewById(R.id.user_phone_display);
            final TextView country_code = (TextView) dialog.findViewById(R.id.country_code);
            final EditText user_amount = (EditText) dialog.findViewById(R.id.user_amount);
            final EditText user_phone = (EditText) dialog.findViewById(R.id.user_phone);

            user_phone.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                }

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {
                    user_phone_display.setText(SharedPrefs.getCountryCode() + user_phone.getText().toString());
                }

                @Override
                public void afterTextChanged(Editable s) {

                }
            });

            country_code.setText(SharedPrefs.getCountryCode());
            user_phone.setText(SharedPrefs.getUserPhone());

            txtOk.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (user_phone.getText().toString().length() < 8) {
                        helper.ToastMessage(RestaurantDetailsActivity.this, "Invalid Phone Number");
                    } else if (user_amount.getText().toString().equals("")) {
                        helper.ToastMessage(RestaurantDetailsActivity.this, "Invalid Amount entered");
                    } else if (Float.valueOf(user_amount.getText().toString()) < 1) {
                        helper.ToastMessage(RestaurantDetailsActivity.this, "Invalid Amount entered");
                    } else {
                        helper.ToastMessage(RestaurantDetailsActivity.this, "coming soon :)");

                        dialog.cancel();

                    }
                }
            });
            txtCancel.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    dialog.cancel();
                }
            });

            dialog.show();
        }
    }


    // REVIEW Functionality ========================================================================

    private void captureFullReview(int progress) {
        final Dialog dialog = new Dialog(RestaurantDetailsActivity.this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.dialog_post_full_review);
        final TextView txtOk = (TextView) dialog.findViewById(R.id.txtOk);
        final TextView txtCancel = (TextView) dialog.findViewById(R.id.txtCancel);
        final SeekBar rating_average = (SeekBar) dialog.findViewById(R.id.rating_average);
        final TextView review_text = (TextView) dialog.findViewById(R.id.review_text);
        picture_add = (ImageView) dialog.findViewById(R.id.picture_add);
        picture_delete = (ImageView) dialog.findViewById(R.id.picture_delete);
        final TextView rating_average_text = (TextView) dialog.findViewById(R.id.rating_average_text);
        rating_average.setProgress(progress);
        rating_average_text.setText(String.valueOf(progress));

        picture_delete.setVisibility(View.GONE);
        picture_add.setImageResource(R.drawable.ic_camera);

        picture_add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final CharSequence[] items = {"Take Photo", "Choose from Library",
                        "Cancel"};
                AlertDialog.Builder builder = new AlertDialog.Builder(RestaurantDetailsActivity.this);
                builder.setTitle("Add Profile Picture");
                builder.setItems(items, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int item) {
                        if (items[item].equals("Take Photo")) {
                            Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                            File f = new File(Environment
                                    .getExternalStorageDirectory(), "temp.jpg");
                            intent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(f));
                            startActivityForResult(intent, REQUEST_CAMERA);
                        } else if (items[item].equals("Choose from Library")) {
                            Intent intent = new Intent(
                                    Intent.ACTION_PICK,
                                    MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                            intent.setType("image/*");
                            startActivityForResult(
                                    Intent.createChooser(intent, "Select File"),
                                    SELECT_FILE);
                        } else if (items[item].equals("Cancel")) {
                            dialog.dismiss();
                        }
                    }
                });
                builder.show();

            }
        });

        picture_delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                picture_add.setImageResource(R.drawable.ic_camera);
                STR_PICTURE = "";
                picture_delete.setVisibility(View.GONE);
            }
        });

        rating_average.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                rating_average_text.setText(String.valueOf(progress));
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
            }
        });

        txtCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.cancel();
            }
        });

        txtOk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (rating_average.getProgress() == 0) {
                    helper.ToastMessage(RestaurantDetailsActivity.this, "Please set a restaurant_rating");
                } else if (review_text.getText().toString().length() < 50) {
                    review_text.setError("must be more than 10 words");
                } else {
                    dialog.cancel();
                    asyncPostReview(
                            rating_average.getProgress(),
                            rating_average.getProgress(),
                            rating_average.getProgress(),
                            rating_average.getProgress(),
                            rating_average.getProgress(),
                            review_text.getText().toString().trim()
                    );
                }
            }
        });

        WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
        lp.copyFrom(dialog.getWindow().getAttributes());
        lp.width = WindowManager.LayoutParams.MATCH_PARENT;
        lp.height = WindowManager.LayoutParams.WRAP_CONTENT;
        dialog.show();
        dialog.getWindow().setAttributes(lp);
        dialog.show();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            if (requestCode == REQUEST_CAMERA) {
                File f = new File(Environment.getExternalStorageDirectory()
                        .toString());
                for (File temp : f.listFiles()) {
                    if (temp.getName().equals("temp.jpg")) {
                        f = temp;
                        break;
                    }
                }
                try {
                    Bitmap bm;
                    BitmapFactory.Options btmapOptions = new BitmapFactory.Options();
                    bm = BitmapFactory.decodeFile(f.getAbsolutePath(),
                            btmapOptions);
                    picture_add.setImageBitmap(returnCompressedBitmap(bm));
                    String path = Environment
                            .getExternalStorageDirectory()
                            + File.separator
                            + "Phoenix" + File.separator + "default";
                    f.delete();
                    OutputStream fOut = null;
                    File file = new File(path, String.valueOf(System
                            .currentTimeMillis()) + ".jpg");
                    try {
                        fOut = new FileOutputStream(file);
                        bm.compress(Bitmap.CompressFormat.JPEG, 85, fOut);
                        fOut.flush();
                        fOut.close();
                    } catch (FileNotFoundException e) {
                        e.printStackTrace();
                    } catch (IOException e) {
                        e.printStackTrace();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
                handlePictureString();
            } else if (requestCode == SELECT_FILE) {
                Uri selectedImageUri = data.getData();
                String tempPath = getPath(selectedImageUri);
                Bitmap bm;
                BitmapFactory.Options btmapOptions = new BitmapFactory.Options();
                bm = BitmapFactory.decodeFile(tempPath, btmapOptions);
                picture_add.setImageBitmap(returnCompressedBitmap(bm));
                handlePictureString();
            }


        }
    }

    private void handlePictureString() {
        if (!STR_PICTURE.equals("")) {
            picture_delete.setVisibility(View.VISIBLE);
        } else {
            picture_delete.setVisibility(View.GONE);
        }
    }

    private Bitmap returnCompressedBitmap(Bitmap bmp) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bmp = createScaledBitmapKeepingAspectRatio(bmp, 300);
        bmp.compress(Bitmap.CompressFormat.JPEG, 80, baos);
        byte[] b = baos.toByteArray();
        STR_PICTURE = android.util.Base64.encodeToString(b, android.util.Base64.DEFAULT);
        STR_PICTURE = STR_PICTURE.replaceAll("\\r\\n|\\r|\\n", "");
        Helpers.LogThis("encodedImage " + STR_PICTURE);
        return bmp;
    }

    private Bitmap createScaledBitmapKeepingAspectRatio(Bitmap bitmap, int maxSide) {
        int orgHeight = bitmap.getHeight();
        int orgWidth = bitmap.getWidth();
        int scaledWidth = (orgWidth >= orgHeight) ? maxSide : (int) ((float) maxSide * ((float) orgWidth / (float) orgHeight));
        int scaledHeight = (orgHeight >= orgWidth) ? maxSide : (int) ((float) maxSide * ((float) orgHeight / (float) orgWidth));
        return Bitmap.createScaledBitmap(bitmap, scaledWidth, scaledHeight, true);
    }

    public String getPath(Uri contentUri) {
        String res = null;
        String[] proj = {MediaStore.Images.Media.DATA};
        Cursor cursor = getContentResolver().query(contentUri, proj, null, null, null);
        assert cursor != null;
        if (cursor.moveToFirst()) {
            ;
            int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
            res = cursor.getString(column_index);
        }
        cursor.close();
        return res;
    }

    private void capturePartialReview() {
        final Dialog dialog = new Dialog(RestaurantDetailsActivity.this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.dialog_post_partial_review);
        final TextView txtOk = (TextView) dialog.findViewById(R.id.txtOk);
        final TextView txtfullReview = (TextView) dialog.findViewById(R.id.txtfullReview);
        final SeekBar rating_average = (SeekBar) dialog.findViewById(R.id.rating_average);
        final TextView rating_average_text = (TextView) dialog.findViewById(R.id.rating_average_text);
        rating_average.setProgress(0);

        rating_average.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                rating_average_text.setText(String.valueOf(progress));
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
            }
        });

        txtfullReview.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.cancel();
                captureFullReview(rating_average.getProgress());
            }
        });

        txtOk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (rating_average.getProgress() == 0) {
                    helper.ToastMessage(RestaurantDetailsActivity.this, "Please set a restaurant_rating");
                } else {
                    dialog.cancel();
                    asyncPostReview(
                            rating_average.getProgress(),
                            rating_average.getProgress(),
                            rating_average.getProgress(),
                            rating_average.getProgress(),
                            rating_average.getProgress(),
                            ""
                    );
                }
            }
        });

        dialog.show();
    }


    // MAPS Functionality ==========================================================================

    public void startMapStuff() {

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        int status = GooglePlayServicesUtil.isGooglePlayServicesAvailable(RestaurantDetailsActivity.this);
        if (status != ConnectionResult.SUCCESS) {
            int requestCode = 10;
            Dialog dialog = GooglePlayServicesUtil.getErrorDialog(status, RestaurantDetailsActivity.this, requestCode);
            dialog.show();
        } else {
            if (ActivityCompat.checkSelfPermission(RestaurantDetailsActivity.this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED &&
                    ActivityCompat.checkSelfPermission(RestaurantDetailsActivity.this, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED) {

                SupportMapFragment fm = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
                fm.getMapAsync(this);

            } else {
                helper.myDialog(RestaurantDetailsActivity.this,
                        "Permission not Granted!", "Please allow the Chillout Records App to access your location");
            }
        }

    }

    @Override
    public void onMapReady(final GoogleMap googleMap) {
        this.googleMap = googleMap;
        if (this.googleMap != null) {
            if (ActivityCompat.checkSelfPermission(RestaurantDetailsActivity.this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED &&
                    ActivityCompat.checkSelfPermission(RestaurantDetailsActivity.this, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED) {

                LatLng mylocation = new LatLng(GEOLAT, GEOLNG);
                this.googleMap.moveCamera(CameraUpdateFactory.newLatLng(mylocation));
                this.googleMap.animateCamera(CameraUpdateFactory.zoomTo(ZOOM_IN));

                Marker restaurant_marker = this.googleMap.addMarker(new MarkerOptions().position(
                        new LatLng(GEOLAT, GEOLNG))
                        .title(STR_PAGE_TITLE)
                        .snippet(STR_PAGE_DESCRIPTION));
                restaurant_marker.setVisible(true);


            } else {
                helper.myDialog(RestaurantDetailsActivity.this,
                        "Permission not Granted!", "Please allow the Chillout Records App to access your location");
            }
        }
    }

    @Override
    public void onBackPressed() {
        if (LL_gallery_large.getVisibility() == View.VISIBLE) {
            LL_gallery_large.setVisibility(View.GONE);
            LL_information.setVisibility(View.VISIBLE);
        } else {
            finish();
        }
    }

    private void listenExitBroadcast() {
        IntentFilter filter = new IntentFilter();
        filter.addAction(Helpers.BroadcastValue);
        receiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                finish();
            }
        };
        registerReceiver(receiver, filter);
    }

    @Override
    protected void onDestroy() {
        if (receiver != null) {
            unregisterReceiver(receiver);
            receiver = null;
        }
        super.onDestroy();

    }


    // NO ADAPTER DATA TO DISPLAY ==================================================================

    private void noReviews() {
        reviewList.clear();
        ReviewModel reviewModel = new ReviewModel();
        reviewList.add(reviewModel);
        review_recyclerView.getRecycledViewPool().clear();
        review_adapter.notifyDataSetChanged();
    }

    private void noSmallGalleryImages() {
        galleryList_small.clear();
        GalleryModel galleryModel = new GalleryModel();
        galleryList_small.add(galleryModel);
        galleryAdapterSmall.notifyDataSetChanged();
    }

    private void noLargeGalleryImages() {
        galleryList_large.clear();
        GalleryModel galleryModel = new GalleryModel();
        galleryList_large.add(galleryModel);
        galleryAdapterLarge.notifyDataSetChanged();
    }

    private void noRestaurants() {
        restaurantsList.clear();
        RestaurantModel restaurantModel = new RestaurantModel();
        restaurantsList.add(restaurantModel);
        restaurant_recyclerView.getRecycledViewPool().clear();
        restaurant_adapter.notifyDataSetChanged();
    }


    // UPDATE SCREEN FUNCTIONS =====================================================================

    private void updateScreen(String state) {
        resetTB_BTNs();
        hideAllScreens();

        if (state.equals(STATE_INFORMATION)) {
            LL_information.setVisibility(View.VISIBLE);
            TB_BTN_Info.setBackgroundColor(getResources().getColor(R.color.black));
            TB_Layout.setClickable(true);

        } else if (state.equals(STATE_MAP)) {
            LL_map.setVisibility(View.VISIBLE);
            TB_BTN_Map.setBackgroundColor(getResources().getColor(R.color.black));
            TB_Layout.setClickable(true);

        } else if (state.equals(STATE_REVIEWS)) {
            LL_review.setVisibility(View.VISIBLE);
            TB_BTN_Reviews.setBackgroundColor(getResources().getColor(R.color.black));
            TB_Layout.setClickable(true);

        } else if (state.equals(STATE_PDF)) {
            LL_pdf.setVisibility(View.VISIBLE);
            TB_BTN_PDF.setBackgroundColor(getResources().getColor(R.color.black));
            TB_Layout.setClickable(true);

        } else if (state.equals(STATE_LOADING)) {
            LL_loading.setVisibility(View.VISIBLE);

        } else if (state.equals(STATE_ERROR)) {
            LL_error.setVisibility(View.VISIBLE);
            TB_Layout.setVisibility(View.GONE);
            btn_call_support.setClickable(false);
            btn_call_support.setVisibility(View.GONE);
            restaurant_rating.setVisibility(View.GONE);
            picture_restaurant.setImageDrawable(getResources().getDrawable(R.drawable.main_logo_white));

        } else if (state.equals(STATE_OTHER_OFFERS)) {
            TB_Layout.setVisibility(View.GONE);
            LL_other_offers.setVisibility(View.VISIBLE);
            btn_call_support.setTag(SharedPrefs.getSupportNumber());
            restaurant_rating.setVisibility(View.GONE);
            asyncFetchRestaurants();
        }

    }

    private void hideAllScreens() {
        LL_information.setVisibility(View.GONE);
        LL_review.setVisibility(View.GONE);
        LL_map.setVisibility(View.GONE);
        LL_pdf.setVisibility(View.GONE);
        LL_loading.setVisibility(View.GONE);
        LL_error.setVisibility(View.GONE);
        LL_gallery_large.setVisibility(View.GONE);
        LL_other_offers.setVisibility(View.GONE);
    }

    public void showGalleryLargeLayout() {
        LL_gallery_large.setVisibility(View.VISIBLE);
        LL_information.setVisibility(View.GONE);
    }

    private void resetTB_BTNs() {
        TB_Layout.setClickable(false);
        TB_BTN_Info.setBackgroundColor(getResources().getColor(R.color.dark_grey));
        TB_BTN_PDF.setBackgroundColor(getResources().getColor(R.color.dark_grey));
        TB_BTN_Map.setBackgroundColor(getResources().getColor(R.color.dark_grey));
        TB_BTN_Reviews.setBackgroundColor(getResources().getColor(R.color.dark_grey));
        TB_BTN_Share.setBackgroundColor(getResources().getColor(R.color.dark_grey));


    }

    public void BTN_Back(View view) {
        finish();
    }

    public void BTN_Retry(View view) {
        asyncInformation();
        asyncReview(true);
    }

    // ASYNC FUNCTIONS =============================================================================

    private void asyncLike() {
        RestaurantService restaurantService = RestaurantService.retrofit.create(RestaurantService.class);
        final Call<JsonObject> call = restaurantService.favoriteOneRestaurant(STR_RESTAURANT_ID, SharedPrefs.getUserID());

        call.enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                try {
                    JSONObject main = new JSONObject(response.body().toString());
                    Boolean success = main.getBoolean("success");
                    if (success) {
                        String action = main.getString("action");
                        if (action.equals("create")) {
                            setUpLikeButton(true);
                            db.setLikedRestaurant(STR_RESTAURANT_ID);
                        } else {
                            setUpLikeButton(false);
                            db.deleteLikedRestaurant(STR_RESTAURANT_ID);
                        }
                    } else {
                        helper.ToastMessage(RestaurantDetailsActivity.this, "Unable to add to favourites, Please try again later");
                    }
                } catch (JSONException e) {
                    Helpers.LogThis("JSON exception " + e.toString());
                    helper.ToastMessage(RestaurantDetailsActivity.this, "Unable to add to favourites, Please try again later");

                } catch (Exception e) {
                    Helpers.LogThis("Other exception " + e.toString());
                    helper.ToastMessage(RestaurantDetailsActivity.this, "Unable to add to favourites, Please try again later");
                }
            }

            @Override
            public void onFailure(Call<JsonObject> call, Throwable t) {
                Helpers.LogThis("Failure: " + t.toString());
                Helpers.LogThis("Failure: " + call.toString());
                helper.ToastMessage(RestaurantDetailsActivity.this, "Unable to add to favourites, Please try again later");
            }
        });
    }

    private void asyncInformation() {
        updateScreen(STATE_LOADING);
        helper.setProgressDialogMessage(getString(R.string.progress_loading_restaurant_info));
        helper.progressDialog(true);
        RestaurantService restaurantService = RestaurantService.retrofit.create(RestaurantService.class);
        final Call<JsonObject> call = restaurantService.getOneRestaurant(STR_RESTAURANT_ID);

        call.enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {

                try {
                    JSONObject main = new JSONObject(response.body().toString());
                    String request_msg = main.getString("error");
                    if (request_msg.equals("false")) {

                        JSONObject json_data = main.getJSONObject("result");

                        //Update The DB
//                        db.setRestaurants(json_data);

                        // Setup the Collapsing toolbar
                        Glide.with(RestaurantDetailsActivity.this).load(json_data.getString("full_image_url")).into(picture_restaurant);
                        txt_restaurant_location.setText(json_data.getString("address"));
                        txt_restaurant_cuisine.setText(STR_PAGE_DESCRIPTION);
                        STR_LATITUDE = json_data.getString("geolat");
                        STR_LONGITUDE = json_data.getString("geolng");


                        if (json_data.getInt("premium_level") != 0) {
                            txt_restaurant_description.setText(json_data.getString("description"));
                            restaurant_rating.setRating(json_data.getInt("average_rating") / 2);

                            LayoutInflater linf;
                            linf = LayoutInflater.from(RestaurantDetailsActivity.this);

                            JSONArray active_offersArray = json_data.getJSONArray("active_offers");
                            TERMS_AND_CONDITIONS = new String[active_offersArray.length()];
                            for (int v = 0; v < active_offersArray.length(); v++) {
                                if (!active_offersArray.isNull(v)) {
                                    JSONObject active_offers = active_offersArray.getJSONObject(v);
                                    View child = linf.inflate(R.layout.list_item_offers, null);

                                    final TextView offer_id = (TextView) child.findViewById(R.id.offer_id);
                                    final TextView offer_discount_amount = (TextView) child.findViewById(R.id.offer_discount_amount);
                                    TextView offer_name = (TextView) child.findViewById(R.id.offer_name);
                                    TextView offer_btn = (TextView) child.findViewById(R.id.offer_btn);
                                    TextView offer_excerpt = (TextView) child.findViewById(R.id.offer_excerpt);
                                    final TextView offer_terms = (TextView) child.findViewById(R.id.offer_terms);
                                    ImageView offer_icon = (ImageView) child.findViewById(R.id.offer_icon);

                                    offer_id.setText(String.valueOf(active_offers.getInt("id")));
                                    offer_name.setText(active_offers.getString("name"));
                                    offer_excerpt.setText(active_offers.getString("excerpt"));

                                    final int OFFER_REDEEMABLE = active_offers.getInt("redeemable");
                                    Glide.with(RestaurantDetailsActivity.this).load(active_offers.getString("full_icon_image_url")).into(offer_icon);
                                    // Offer Category Info
                                    JSONObject offer_category = active_offers.getJSONObject("offer_category");
                                    final int OFFER_CATEGORY_ID = offer_category.getInt("id");
                                    final int DISCOUNT_AMOUNT = active_offers.getInt("discount_amount");
                                    offer_discount_amount.setText(String.valueOf(DISCOUNT_AMOUNT));
                                    final String CARD_TITLE = offer_category.getString("card_title");
                                    final String CARD_BUTTON_TEXT = offer_category.getString("card_button_text");
                                    final String CARD_DESC = offer_category.getString("card_description");
                                    final String CARD_BCKGRD = offer_category.getString("card_background_image");

                                    final String CARD_TEXT_COLOR = offer_category.getString("card_font_color_hex");
                                    final Boolean CARD_SHOW_CONFIRM_CODE = offer_category.getInt("show_confirmation_code_input") == 1;
                                    final Boolean CARD_ALLOW_REVIEW = offer_category.getInt("allow_reviews") == 1;

                                    if (OFFER_REDEEMABLE > 0) {
                                        offer_btn.setVisibility(View.VISIBLE);
                                        offer_btn.setText(active_offers.getString("redemption_button_text"));
                                        if (STR_SHOW_OFFER.equals("true") && helper.validateIsLoggedIn() && !helper.validateInternetNotPresent()) {

                                            generateDigitalCard(
                                                    DISCOUNT_AMOUNT,
                                                    OFFER_CATEGORY_ID,
                                                    CARD_TITLE,
                                                    CARD_BCKGRD,
                                                    CARD_BUTTON_TEXT,
                                                    CARD_DESC,
                                                    CARD_TEXT_COLOR,
                                                    CARD_ALLOW_REVIEW,
                                                    CARD_SHOW_CONFIRM_CODE
                                            );
                                        }
                                    } else {
                                        offer_btn.setVisibility(View.GONE);
                                    }

                                    TERMS_AND_CONDITIONS[v] = active_offers.getString("description");
                                    offer_terms.setTag(String.valueOf(v));

                                    offer_terms.setOnClickListener(new View.OnClickListener() {
                                        @Override
                                        public void onClick(View view) {
                                            dialogShowTnC(Integer.valueOf(offer_terms.getTag().toString()));
                                        }
                                    });

                                    offer_btn.setOnClickListener(new View.OnClickListener() {
                                        @Override
                                        public void onClick(View v) {
                                            if (helper.validateIsLoggedIn() && !helper.validateInternetNotPresent()) {
                                                generateDigitalCard(
                                                        DISCOUNT_AMOUNT,
                                                        OFFER_CATEGORY_ID,
                                                        CARD_TITLE,
                                                        CARD_BCKGRD,
                                                        CARD_BUTTON_TEXT,
                                                        CARD_DESC,
                                                        CARD_TEXT_COLOR,
                                                        CARD_ALLOW_REVIEW,
                                                        CARD_SHOW_CONFIRM_CODE
                                                );
                                            }
                                        }
                                    });

                                    LL_offers.addView(child);

                                }
                            }

                            String activeNumber = json_data.getString("active_phone_number");
                            btn_call_support.setTag(activeNumber);


                            GEOLAT = Float.valueOf(json_data.getString("geolat"));
                            GEOLNG = Float.valueOf(json_data.getString("geolng"));

                            startMapStuff();

                            galleryList_small.clear();
                            galleryList_large.clear();
                            JSONArray galleryImageArrays = json_data.getJSONArray("gallery");
                            if (!galleryImageArrays.isNull(0)) {
                                for (int v = 0; v < galleryImageArrays.length(); v++) {
                                    JSONObject galleryObject = galleryImageArrays.getJSONObject(v);
                                    GalleryModel galleryModel = new GalleryModel();
                                    galleryModel.image = galleryObject.getString("full_thumbnail_url");
                                    galleryModel.image_large = galleryObject.getString("full_image_url");
                                    galleryModel.caption = galleryObject.getString("caption");
                                    galleryList_small.add(galleryModel);
                                    galleryList_large.add(galleryModel);
                                }
                                galleryAdapterSmall.notifyDataSetChanged();
                            } else {
                                noSmallGalleryImages();
                                noLargeGalleryImages();
                            }


                            JSONArray operating_hours = json_data.getJSONArray("operating_hours");
                            if (!operating_hours.isNull(0)) {
                                for (int v = 0; v < operating_hours.length(); v++) {
                                    JSONObject operating_hoursObject = operating_hours.getJSONObject(v);

                                    String day_of_week = operating_hoursObject.getString("day_of_week");
                                    String open_time = operating_hoursObject.getString("open_time");
                                    String close_time = operating_hoursObject.getString("close_time");

                                    if (day_of_week.equals("1")) {
                                        llday1.setVisibility(View.VISIBLE);
                                        day1open.setText(open_time);
                                        day1close.setText(close_time);
                                    } else if (day_of_week.equals("2")) {
                                        llday2.setVisibility(View.VISIBLE);
                                        day2open.setText(open_time);
                                        day2close.setText(close_time);
                                    } else if (day_of_week.equals("3")) {
                                        llday3.setVisibility(View.VISIBLE);
                                        day3open.setText(open_time);
                                        day3close.setText(close_time);
                                    } else if (day_of_week.equals("4")) {
                                        llday4.setVisibility(View.VISIBLE);
                                        day4open.setText(open_time);
                                        day4close.setText(close_time);
                                    } else if (day_of_week.equals("5")) {
                                        llday5.setVisibility(View.VISIBLE);
                                        day5open.setText(open_time);
                                        day5close.setText(close_time);
                                    } else if (day_of_week.equals("6")) {
                                        llday6.setVisibility(View.VISIBLE);
                                        day6open.setText(open_time);
                                        day6close.setText(close_time);
                                    } else if (day_of_week.equals("7")) {
                                        llday7.setVisibility(View.VISIBLE);
                                        day7open.setText(open_time);
                                        day7close.setText(close_time);
                                    }
                                }
                            }

                            if (json_data.get("pdf_menu").equals("")) {
                                TB_BTN_PDF.setTag("false");
                                TB_BTN_PDF.setVisibility(View.GONE);
                            } else {
                                TB_BTN_PDF.setTag("true");

                                WebSettings settings = pdf_menu_view.getSettings();
                                settings.setJavaScriptEnabled(true);
                                settings.setLoadWithOverviewMode(true);
                                settings.setUseWideViewPort(false);
                                settings.setSupportZoom(true);
                                settings.setBuiltInZoomControls(true);
                                settings.setLayoutAlgorithm(WebSettings.LayoutAlgorithm.SINGLE_COLUMN);
                                settings.setCacheMode(WebSettings.LOAD_NO_CACHE);
                                settings.setDomStorageEnabled(true);
                                pdf_menu_view.setScrollBarStyle(WebView.SCROLLBARS_OUTSIDE_OVERLAY);
                                pdf_menu_view.setScrollbarFadingEnabled(true);
                                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                                    pdf_menu_view.setLayerType(View.LAYER_TYPE_HARDWARE, null);
                                } else {
                                    pdf_menu_view.setLayerType(View.LAYER_TYPE_SOFTWARE, null);
                                }
                                pdf_menu_view.loadUrl("https://docs.google.com/viewerng/viewer?url=" + json_data.getString("pdf_menu"));

                            }

                            STR_SHARELINK = STR_SHARELINK + json_data.getString("url");

                            updateScreen(STATE_INFORMATION);


                        } else {

                            updateScreen(STATE_OTHER_OFFERS);

                        }

                        helper.progressDialog(false);
                    } else {

                        helper.progressDialog(false);
                        updateScreen(STATE_ERROR);
                    }
                } catch (JSONException e) {
                    Helpers.LogThis("JSON exception " + e.toString());
                    updateScreen(STATE_ERROR);
                    helper.progressDialog(false);

                } catch (Exception e) {
                    Helpers.LogThis("Other exception " + e.toString());
                    updateScreen(STATE_ERROR);
                    helper.progressDialog(false);
                }
            }

            @Override
            public void onFailure(Call<JsonObject> call, Throwable t) {
                Helpers.LogThis("Failure: " + t.toString());
                Helpers.LogThis("Failure: " + call.toString());

                helper.progressDialog(false);
                updateScreen(STATE_ERROR);
            }
        });
    }

    private void asyncReview(final Boolean startAfresh) {
        if (!STR_MESSAGE_BODY.equals(STR_RESTAURANT_ID + String.valueOf(CURRENT_PAGE))) {
            STR_MESSAGE_BODY = STR_RESTAURANT_ID + String.valueOf(CURRENT_PAGE);

            if (startAfresh) {
                CURRENT_PAGE = 0;
                reviewList.clear();
            }

            review_swiperefresh.setRefreshing(true);

            RestaurantService restaurantService = RestaurantService.retrofit.create(RestaurantService.class);
            final Call<JsonObject> call = restaurantService.getOneRestaurantsReviews(
                    STR_RESTAURANT_ID,
                    String.valueOf(CURRENT_PAGE)
            );

            call.enqueue(new Callback<JsonObject>() {
                @Override
                public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                    review_swiperefresh.setRefreshing(false);
                    helper.progressDialog(false);
                    try {
                        JSONObject main = new JSONObject(response.body().toString());
                        String ERROR = main.getString("error");
                        if (ERROR.equals("false")) {

                            Helpers.LogThis("PAGE NUMBER Internal: " + CURRENT_PAGE);
                            Helpers.LogThis("PAGE NUMBER Server: " + main.getInt("last_page"));

                            LAST_PAGE = main.getInt("last_page");

                            if (CURRENT_PAGE >= LAST_PAGE) {
                                bool_loading = false;
                            }

                            JSONArray jArray = main.getJSONArray("result");
                            int result_length = jArray.length();

                            Helpers.LogThis("Length of Response: " + result_length);

                            if (result_length <= 0 && CURRENT_PAGE < 1) {
                                noReviews();
                            } else {
                                CURRENT_PAGE++;

                                for (int i = 0; i < result_length; i++) {
                                    JSONObject json_data = jArray.getJSONObject(i);
                                    ReviewModel reviewModel = new ReviewModel();

                                    reviewModel.user_display_name = json_data.getString("user_display_name");
                                    reviewModel.user_thumbnail = json_data.getString("user_thumbnail");
                                    reviewModel.review_text = json_data.getString("review_text");
                                    reviewModel.rating = json_data.getString("rating");

                                    JSONObject created_at = json_data.getJSONObject("created_at");
                                    if (!created_at.isNull("date")) {
                                        String[] splited = created_at.getString("date").split(" ");
                                        reviewModel.created_at_date = splited[0];
                                    }

                                    if (!reviewModel.review_text.equals("")) {
                                        reviewList.add(reviewModel);
                                    }
                                }
                            }
                            review_recyclerView.getRecycledViewPool().clear();
                            review_adapter.notifyDataSetChanged();
                        } else {
                            noReviews();
                        }

                    } catch (JSONException e) {
                        Helpers.LogThis("JSON exception " + e.toString());
                        noReviews();

                    } catch (Exception e) {
                        noReviews();
                        Helpers.LogThis("Other exception " + e.toString());
                    }
                }

                @Override
                public void onFailure(Call<JsonObject> call, Throwable t) {
                    Helpers.LogThis("Failure: " + t.toString());
                    Helpers.LogThis("Failure: " + call.toString());

                    helper.progressDialog(false);
                    review_swiperefresh.setRefreshing(false);
                    noReviews();

                }

            });

        } else {
            review_swiperefresh.setRefreshing(false);
        }

    }

    private void asyncPostReview(
            int rating_food,
            int rating_service,
            int rating_ambiance,
            int rating_value,
            int rating_average,
            String review_text) {

        helper.progressDialog(true);
        helper.setProgressDialogMessage(getString(R.string.progress_loading_review));

        RestaurantService restaurantService = RestaurantService.retrofit.create(RestaurantService.class);
        final Call<JsonObject> call = restaurantService.postRestaurantsReviews(
                STR_RESTAURANT_ID,
                SharedPrefs.getUserID(),
                rating_food,
                rating_service,
                rating_ambiance,
                rating_value,
                rating_average,
                review_text
        );

        call.enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                helper.progressDialog(false);
                try {
                    JSONObject main = new JSONObject(response.body().toString());
                    Boolean success = main.getBoolean("success");
                    if (success) {
                        helper.myDialog(RestaurantDetailsActivity.this,
                                "Chillout Records", "Thank you, Your post has been submitted and will be reviewed for publishing");
                    } else {
                        helper.myDialog(RestaurantDetailsActivity.this,
                                getString(R.string.app_name), getString(R.string.error_unknown));
                    }
                } catch (JSONException e) {
                    Helpers.LogThis("JSON exception " + e.toString());
                    helper.myDialog(RestaurantDetailsActivity.this,
                            getString(R.string.app_name), getString(R.string.error_unknown));

                } catch (Exception e) {
                    Helpers.LogThis("Other exception " + e.toString());
                    helper.myDialog(RestaurantDetailsActivity.this,
                            getString(R.string.app_name), getString(R.string.error_unknown));
                }
            }

            @Override
            public void onFailure(Call<JsonObject> call, Throwable t) {
                Helpers.LogThis("Failure: " + t.toString());
                Helpers.LogThis("Failure: " + call.toString());
                helper.progressDialog(false);
                helper.ToastMessage(RestaurantDetailsActivity.this,
                        "Please check your internet connection and try again later...");

            }
        });
    }

    private void asyncClaimOffer(final int OFFER_ID, final String CODE, final Boolean CARD_ALLOW_REVIEW) {
        helper.progressDialog(true);
        helper.setProgressDialogMessage(getString(R.string.progress_loading_claim_offer));

        RestaurantService restaurantService = RestaurantService.retrofit.create(RestaurantService.class);
        final Call<JsonObject> call = restaurantService.claimOffer(
                OFFER_ID,
                SharedPrefs.getUserID(),
                CODE
        );

        call.enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                helper.progressDialog(false);
                try {
                    JSONObject main = new JSONObject(response.body().toString());
                    Boolean success = main.getBoolean("success");
                    if (success) {
                        if (CARD_ALLOW_REVIEW) {
                            capturePartialReview();
                        } else {
                            helper.myDialog(RestaurantDetailsActivity.this,
                                    getString(R.string.app_name), "You have successfully redeemed your offer");
                        }
                    } else {
                        helper.myDialog(RestaurantDetailsActivity.this,
                                getString(R.string.app_name), getString(R.string.error_unknown));
                    }
                } catch (JSONException e) {
                    Helpers.LogThis("JSON exception " + e.toString());
                    helper.myDialog(RestaurantDetailsActivity.this,
                            getString(R.string.app_name), getString(R.string.error_unknown));

                } catch (Exception e) {
                    Helpers.LogThis("Other exception " + e.toString());
                    helper.myDialog(RestaurantDetailsActivity.this,
                            getString(R.string.app_name), getString(R.string.error_unknown));
                }
            }

            @Override
            public void onFailure(Call<JsonObject> call, Throwable t) {
                Helpers.LogThis("Failure: " + t.toString());
                Helpers.LogThis("Failure: " + call.toString());
                helper.progressDialog(false);
                helper.ToastMessage(RestaurantDetailsActivity.this, "Please check your internet connection and try again later...");

            }
        });
    }

    private void generateDigitalCard(
            final int DISCOUNT_AMOUNT,
            final int OFFER_CATEGORY_ID,
            final String CARD_TITLE,
            final String CARD_BCKGRD,
            final String CARD_BUTTON_TEXT,
            final String CARD_DESC,
            final String CARD_TEXT_COLOR,
            final Boolean CARD_ALLOW_REVIEW,
            final Boolean SHOW_CONFIRMATION_CODE) {

        final Dialog dialog = new Dialog(RestaurantDetailsActivity.this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.dialog_digital_card);

        final TextView digital_card_title = (TextView) dialog.findViewById(R.id.digital_card_title);
        final TextView digital_card_discount = (TextView) dialog.findViewById(R.id.digital_card_discount);
        final TextView digital_card_discount_sign = (TextView) dialog.findViewById(R.id.digital_card_discount_sign);
        final TextView digital_card_restaurant_name = (TextView) dialog.findViewById(R.id.digital_card_restaurant_name);
        final TextView digital_card_user_name = (TextView) dialog.findViewById(R.id.digital_card_user_name);
        final TextView digital_card_time = (TextView) dialog.findViewById(R.id.digital_card_time);
        final TextView digital_card_support_number = (TextView) dialog.findViewById(R.id.digital_card_support_number);
        final TextView digital_card_desc = (TextView) dialog.findViewById(R.id.digital_card_desc);
        final TextView txtOk = (TextView) dialog.findViewById(R.id.txtOk);
        final TextView txtCancel = (TextView) dialog.findViewById(R.id.txtCancel);
        final TextView digital_card_confirmation_code = (EditText) dialog.findViewById(R.id.digital_card_confirmation_code);

        final LinearLayout digital_card_top_panel = (LinearLayout) dialog.findViewById(R.id.digital_card_top_panel);
        RoundedImageView digital_card_background = (RoundedImageView) dialog.findViewById(R.id.digital_card_background);

//      "EEE, d MMM yyyy HH:mm:ss Z"------- Wed, 4 Jul 2001 12:08:56 -0700
        SimpleDateFormat sdf = new SimpleDateFormat("EEE, d MMM '@' h:mm a");
        String currentDateandTime = sdf.format(new Date());

        digital_card_discount.setText(String.valueOf(DISCOUNT_AMOUNT));
        digital_card_restaurant_name.setText(txt_restaurant_name.getText().toString());
        digital_card_user_name.setText(SharedPrefs.getUserFullName());
        digital_card_time.setText(currentDateandTime);
        digital_card_title.setText(CARD_TITLE);
        Glide.with(RestaurantDetailsActivity.this).load(CARD_BCKGRD).into(digital_card_background);
        txtOk.setText(CARD_BUTTON_TEXT);
        digital_card_desc.setText(CARD_DESC);
        digital_card_support_number.setText("Contact Support on " + SharedPrefs.getSupportNumber());
        digital_card_support_number.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialogMakeCall("Are you sure you wish to call support?", SharedPrefs.getSupportNumber());
            }
        });

        if (OFFER_CATEGORY_ID == YUMMY_CARD) {
            digital_card_top_panel.setVisibility(View.VISIBLE);

        } else {
            digital_card_top_panel.setVisibility(View.INVISIBLE);
        }

        int textColor = Color.parseColor(CARD_TEXT_COLOR);
        digital_card_discount.setTextColor(textColor);
        digital_card_discount_sign.setTextColor(textColor);
        digital_card_restaurant_name.setTextColor(textColor);
        digital_card_user_name.setTextColor(textColor);
        digital_card_time.setTextColor(textColor);
        digital_card_support_number.setTextColor(textColor);

        if (SHOW_CONFIRMATION_CODE) {
            digital_card_confirmation_code.setVisibility(View.VISIBLE);
            txtCancel.setVisibility(View.VISIBLE);
        } else {
            digital_card_confirmation_code.setVisibility(View.GONE);
            txtCancel.setVisibility(View.GONE);
        }
        txtCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.cancel();
            }
        });

        txtOk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (digital_card_confirmation_code.getVisibility() == View.VISIBLE) {
                    if (digital_card_confirmation_code.getText().toString().length() < 1) {
                        digital_card_confirmation_code.setError("Incorrect");
                    } else {
                        asyncClaimOffer(OFFER_CATEGORY_ID,
                                digital_card_confirmation_code.getText().toString(),
                                CARD_ALLOW_REVIEW);
                        dialog.cancel();
                    }
                } else {
                    asyncClaimOffer(OFFER_CATEGORY_ID,
                            "",
                            CARD_ALLOW_REVIEW);
                    dialog.cancel();
                }
            }
        });

        WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
        lp.copyFrom(dialog.getWindow().getAttributes());
        lp.width = WindowManager.LayoutParams.MATCH_PARENT;
        lp.height = WindowManager.LayoutParams.WRAP_CONTENT;
        dialog.show();
        dialog.getWindow().setAttributes(lp);

        //Tell Facebook you claimed an offer
        AppEventsLogger.newLogger(RestaurantDetailsActivity.this, "Offer Claimed");
    }

    // ASYNC SEARCH FUNCTION =======================================================================

    private void asyncFetchRestaurants() {
        restaurantsList.clear();
//        restaurant_swiperefresh.setRefreshing(true);

        RestaurantService restaurantService = RestaurantService.retrofit.create(RestaurantService.class);
        final Call<JsonObject> call = restaurantService.searchRestaurant(
                "",
                "",
                SharedPrefs.getCityCode(),
                "",
                "",
                STR_LATITUDE,
                STR_LONGITUDE,
                SORT_NEARBY,
                ""
        );

        call.enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
//                restaurant_swiperefresh.setRefreshing(false);
                try {
                    JSONObject main = new JSONObject(response.body().toString());
                    String ERROR = main.getString("error");
                    if (ERROR.equals("false")) {

                        JSONArray jArray = main.getJSONArray("result");
                        int result_length = jArray.length();

                        Helpers.LogThis("Length of Response: " + result_length);

                        if (result_length <= 0) {
                            noRestaurants();
                        } else {

                            for (int i = 0; i < 10; i++) {
                                RestaurantModel restaurantModel = db.setRestaurants(jArray.getJSONObject(i + 1));
                                restaurantsList.add(restaurantModel);
                            }
                        }
                        restaurant_adapter.notifyDataSetChanged();

                    } else {
                        noRestaurants();
                    }
                } catch (JSONException e) {
                    Helpers.LogThis("JSON exception search" + e.toString());
                    noRestaurants();

                } catch (Exception e) {
                    noRestaurants();
                    Helpers.LogThis("Other exception search" + e.toString());
                }
            }

            @Override
            public void onFailure(Call<JsonObject> call, Throwable t) {
                Helpers.LogThis("Failure: " + t.toString());
                Helpers.LogThis("Failure: " + call.toString());

//                restaurant_swiperefresh.setRefreshing(false);

                if (helper.validateInternetNotPresent()) {
                    final Snackbar snackBar = Snackbar.make(findViewById(R.id.drawer_layout),
                            getString(R.string.error_connection), Snackbar.LENGTH_INDEFINITE);
                    snackBar.setAction("Retry", new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            snackBar.dismiss();
                            asyncFetchRestaurants();
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
