package com.chilloutrecords.activities;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.FragmentTransaction;

import com.chilloutrecords.BuildConfig;
import com.chilloutrecords.R;
import com.chilloutrecords.fragments.HomeFragment;
import com.chilloutrecords.fragments.ProfileFragment;
import com.chilloutrecords.interfaces.GeneralInterface;
import com.chilloutrecords.models.NavigationModel;
import com.chilloutrecords.services.LoginStateService;
import com.chilloutrecords.utils.Database;
import com.chilloutrecords.utils.DialogMethods;
import com.chilloutrecords.utils.StaticMethods;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.wallet.AutoResolveHelper;
import com.google.android.gms.wallet.PaymentData;
import com.google.firebase.storage.UploadTask;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Objects;

import me.leolin.shortcutbadger.ShortcutBadger;
import pub.devrel.easypermissions.AfterPermissionGranted;
import pub.devrel.easypermissions.EasyPermissions;

import static com.chilloutrecords.utils.StaticVariables.EXTRA_DATA;
import static com.chilloutrecords.utils.StaticVariables.EXTRA_STRING;
import static com.chilloutrecords.utils.StaticVariables.FIREBASE_STORAGE;
import static com.chilloutrecords.utils.StaticVariables.FIREBASE_USER;
import static com.chilloutrecords.utils.StaticVariables.INT_PERMISSIONS_CAMERA;
import static com.chilloutrecords.utils.StaticVariables.LOAD_PAYMENT_DATA_REQUEST_CODE;
import static com.chilloutrecords.utils.StaticVariables.USER_MODEL;

public class ParentActivity extends AppCompatActivity {

    Toolbar toolbar;
    TextView txt_page_title;
    DialogMethods dialog;

    public ArrayList<NavigationModel> navigation_list = new ArrayList<>();

    public final static String
            PAGE_TITLE_HOME = "Home",
            PAGE_TITLE_ARTISTS = "Home / Artists",
            PAGE_TITLE_VIDEOS = "Home / Videos",
            PAGE_TITLE_PROFILE = "Home / Profile",
            PAGE_TITLE_PROFILE_EDIT = "Home / Profile Edit",
            PAGE_TITLE_SHARE = "Home / Share",
            PAGE_TITLE_ABOUT = "Home / About us",
            PAGE_TITLE_UPGRADE = "Home / Upgrade",
            PAGE_TITLE_LOGOUT = "Home / Logout";

    final String[] perms = {
            Manifest.permission.CAMERA,
            Manifest.permission.WRITE_EXTERNAL_STORAGE,
            Manifest.permission.READ_EXTERNAL_STORAGE};

    // OVERRIDE METHODS ============================================================================
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_parent);

        StaticMethods.startServiceIfNotRunning(this, LoginStateService.class);

        ShortcutBadger.applyCount(ParentActivity.this, 0);

        dialog = new DialogMethods(ParentActivity.this);

        toolbar = findViewById(R.id.toolbar);
        txt_page_title = findViewById(R.id.txt_page_navigation);

        // SETUP TOOLBAR
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });

        loadFragment(new NavigationModel(new HomeFragment(), PAGE_TITLE_HOME, "", null, true));

    }

    @Override
    public void onBackPressed() {
        int size = navigation_list.size();
        if (size > 1) {
            navigation_list.remove(size - 1);
            navigation_list.get(size - 2).add_to_back_stack = false;
            loadFragment(navigation_list.get(size - 2));
        } else {
            finish();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        EasyPermissions.onRequestPermissionsResult(requestCode, permissions, grantResults, this);
        dialog.setDialogPermissions(grantResults);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode != RESULT_CANCELED) {
            switch (requestCode) {
                // RESULT FOR PAYMENT ==============================================================
                case LOAD_PAYMENT_DATA_REQUEST_CODE:
                    switch (resultCode) {
                        case Activity.RESULT_OK:
                            PaymentData paymentData = PaymentData.getFromIntent(data);
                            handlePaymentSuccess(Objects.requireNonNull(paymentData));
                            break;
                        case AutoResolveHelper.RESULT_ERROR:
                            Status status = AutoResolveHelper.getStatusFromIntent(data);
                            StaticMethods.showToast(Objects.requireNonNull(status).getStatusMessage());
                            StaticMethods.logg("PAYMENT", "ERROR MESSAGE: " + status.getStatusMessage());
                            break;
                    }
                    // Re-enables the Google Pay payment button.
                    // btn_google_pay.setClickable(true);
                    break;

                    // RESULT FOR IMAGE UPLOAD =====================================================
                case CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE:
                    if (resultCode == RESULT_OK) {
                        CropImage.ActivityResult result = CropImage.getActivityResult(data);
                        Uri resultUri = result.getUri();
                        final String file_name = "/users/" + FIREBASE_USER.getUid() + ".jpg";
                        FIREBASE_STORAGE.getReference()
                                .child(BuildConfig.STORAGE_IMAGES + file_name)
                                .putFile(resultUri)
                                .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                                    @Override
                                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                                        StaticMethods.logg("FILE", "SUCCESSFULLY UPLOADED");
                                        USER_MODEL.p_pic = file_name;
                                        Database.setUser(USER_MODEL, new GeneralInterface() {
                                            @Override
                                            public void success() {
                                                try {
                                                    ProfileFragment fragment = (ProfileFragment) getSupportFragmentManager().findFragmentById(R.id.ll_fragment);
                                                    assert fragment != null;
                                                    fragment.updateArt(file_name);
                                                } catch (Exception e) {
                                                    e.printStackTrace();
                                                    StaticMethods.showToast(getString(R.string.error_unknown));
                                                }
                                            }

                                            @Override
                                            public void failed() {
                                                StaticMethods.showToast(getString(R.string.error_unknown));
                                            }
                                        });

                                    }
                                })
                                .addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                        StaticMethods.logg("FILE", "FAILED TO UPLOAD");
                                        StaticMethods.showToast(getString(R.string.error_unknown));
                                    }
                                });
                    } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
                        StaticMethods.showToast(getString(R.string.error_unknown));
                    }
                    break;
            }
        } else {
            StaticMethods.showToast("Cancelled");
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    @AfterPermissionGranted(INT_PERMISSIONS_CAMERA)
    private void startImageActivity() {
        CropImage.activity()
                .setGuidelines(CropImageView.Guidelines.ON)
                .start(ParentActivity.this);
    }

    public void checkImagePermissions() {
        if (EasyPermissions.hasPermissions(ParentActivity.this, perms)) {
            startImageActivity();
        } else {
            EasyPermissions.requestPermissions(ParentActivity.this, getString(R.string.rationale_image),
                    INT_PERMISSIONS_CAMERA, perms);
        }
    }

    // BASIC METHODS ===============================================================================
    public void loadFragment(NavigationModel navigation) {
        if (navigation.add_to_back_stack) {
            navigation_list.add(navigation);
        }

        if (navigation_list.size() < 2) {
            toolbar.setNavigationIcon(null);
        } else {
            toolbar.setNavigationIcon(getResources().getDrawable(R.drawable.ic_back));
        }

        Bundle bundle = new Bundle();

        if (!navigation.extra_bundles.equals("")) {
            bundle.putString(EXTRA_STRING, navigation.extra_bundles);
            navigation.fragment.setArguments(bundle);
        }
        if (navigation.extra_string_array != null) {
            bundle.putStringArrayList(EXTRA_DATA, navigation.extra_string_array);
            navigation.fragment.setArguments(bundle);
        }

        txt_page_title.setText(navigation.page_title);

        FragmentTransaction transaction = this.getSupportFragmentManager().beginTransaction();
        if (navigation.add_to_back_stack) {
            transaction.setCustomAnimations(R.anim.enter_right, R.anim.exit_left);
        } else {
            transaction.setCustomAnimations(R.anim.enter_left, R.anim.exit_right);
        }
        transaction.replace(R.id.ll_fragment, navigation.fragment).addToBackStack(null).commit();
    }

    private void handlePaymentSuccess(PaymentData paymentData) {
        String paymentInformation = paymentData.toJson();

        // Token will be null if PaymentDataRequest was not constructed using fromJson(String).
        if (paymentInformation == null) {
            return;
        }
        JSONObject paymentMethodData;

        try {
            paymentMethodData = new JSONObject(paymentInformation).getJSONObject("paymentMethodData");
            // If the gateway is set to "example", no payment information is returned - instead, the
            // token will only consist of "examplePaymentMethodToken".
            if (paymentMethodData
                    .getJSONObject("tokenizationData")
                    .getString("type")
                    .equals("PAYMENT_GATEWAY")
                    && paymentMethodData
                    .getJSONObject("tokenizationData")
                    .getString("token")
                    .equals("examplePaymentMethodToken")) {
                AlertDialog alertDialog =
                        new AlertDialog.Builder(this)
                                .setTitle("Warning")
                                .setMessage(
                                        "Gateway name set to \"example\" - please modify "
                                                + "Constants.java and replace it with your own gateway.")
                                .setPositiveButton("OK", null)
                                .create();
                alertDialog.show();
            }

            String billingName =
                    paymentMethodData.getJSONObject("info").getJSONObject("billingAddress").getString("name");
            Log.d("BillingName", billingName);
            Toast.makeText(this, "BILLING NAME:" + billingName, Toast.LENGTH_LONG)
                    .show();

            // Logging token string.
            Log.d("GooglePaymentToken", paymentMethodData.getJSONObject("tokenizationData").getString("token"));
        } catch (JSONException e) {
            Log.e("handlePaymentSuccess", "Error: " + e.toString());
        }
    }

}
