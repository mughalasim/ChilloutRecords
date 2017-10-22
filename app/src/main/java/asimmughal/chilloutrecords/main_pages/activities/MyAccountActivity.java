package asimmughal.chilloutrecords.main_pages.activities;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.design.widget.Snackbar;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TextView;

import com.google.gson.JsonObject;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Calendar;
import java.util.TimeZone;

import asimmughal.chilloutrecords.R;
import asimmughal.chilloutrecords.services.RestaurantService;
import asimmughal.chilloutrecords.utils.Helpers;
import asimmughal.chilloutrecords.utils.SharedPrefs;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MyAccountActivity extends ParentActivity {

    private EditText
            register_first_name,
            register_last_name,
            register_email,
            register_password,
            register_password_confirm,
            register_phone;
    private TextView
            register_country_code,
            register_dob;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_account);

        initialize(R.id.my_account, "MY ACCOUNT");

        findAllViews();

        updateFields();

        setDates();

    }

    private void updateFields() {
        register_first_name.setText(SharedPrefs.getUserFirstName());
        register_last_name.setText(SharedPrefs.getUserLastName());
        register_email.setText(SharedPrefs.getUserEmail());
        register_country_code.setText(SharedPrefs.getCountryCode());
        register_phone.setText(SharedPrefs.getUserPhone());
        register_dob.setText(SharedPrefs.getUserDOB());
        register_password_confirm.setText("");
        register_password.setText("");
    }

    public void UpdatePassword(View view) {
        if (register_password.getText().toString().length() < 8) {
            helper.myDialog(MyAccountActivity.this, "Update Failure", "Field Required: Password must be 8 characters long");
        } else if (register_password_confirm.getText().toString().length() < 8) {
            helper.myDialog(MyAccountActivity.this, "Update Failure", "Field Required: Confirm Password must be 8 characters long");
        } else if (!register_password.getText().toString().trim().equals(register_password_confirm.getText().toString().trim())) {
            helper.myDialog(MyAccountActivity.this, "Update Failure", "Field Required: Confirm Password and Password do not match");
        } else {
            async(
                    SharedPrefs.getUserFirstName(),
                    SharedPrefs.getUserLastName(),
                    SharedPrefs.getUserEmail(),
                    register_password.getText().toString(),
                    SharedPrefs.getUserPhone(),
                    SharedPrefs.getUserDOB()
            );
        }
    }

    public void UpdateDetails(View view) {
        if (register_first_name.getText().toString().length() < 1) {
            helper.myDialog(MyAccountActivity.this, "Update Failure", "Field Required: First Name");
        } else if (register_last_name.getText().toString().length() < 1) {
            helper.myDialog(MyAccountActivity.this, "Update Failure", "Field Required: Last Name");
        } else if (register_email.getText().toString().length() < 1) {
            helper.myDialog(MyAccountActivity.this, "Update Failure", "Field Required: Email");
        } else if (!helper.validateEmail(register_email.getText().toString())) {
            helper.myDialog(MyAccountActivity.this, "Update Failure", "Field Required: Invalid Email");
        } else if (helper.validateMobileNumber(register_phone.getText().toString().trim())) {
            helper.myDialog(MyAccountActivity.this, "Update Failure", helper.Mobile_Number_Error);
        } else if (register_dob.getText().toString().length() < 1) {
            helper.myDialog(MyAccountActivity.this, "Update Failure", "Set your Date of Birth");
        } else {
            async(
                    register_first_name.getText().toString().trim(),
                    register_last_name.getText().toString().trim(),
                    register_email.getText().toString().trim(),
                    "",
                    register_phone.getText().toString().trim(),
                    register_dob.getText().toString().trim()
            );
        }
    }

    private void setDates() {

        final DatePickerDialog.OnDateSetListener datePickerListener = new DatePickerDialog.OnDateSetListener() {
            public void onDateSet(DatePicker view, int selectedYear,
                                  int selectedMonth, int selectedDay) {
                register_dob.setText(
                        String.valueOf(selectedYear).concat("-").concat(String.valueOf(selectedMonth + 1)).concat("-").concat(String.valueOf(selectedDay)));
            }
        };


        register_dob.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Calendar cal = Calendar.getInstance(TimeZone.getDefault());
                DatePickerDialog datePicker = new DatePickerDialog(MyAccountActivity.this,
                        R.style.AppTheme, datePickerListener,
                        cal.get(Calendar.YEAR),
                        cal.get(Calendar.MONTH),
                        cal.get(Calendar.DAY_OF_MONTH));
                datePicker.setCancelable(false);
                datePicker.setTitle("Set Date Of Birth");
                datePicker.show();
                datePicker.getDatePicker().setMaxDate(System.currentTimeMillis() - 1000);
                datePicker.show();
            }
        });

    }

    @Override
    public void onResume() {
        super.onResume();
        updateDrawer();
        updateFields();
    }

    private void findAllViews() {
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        navigationView = (NavigationView) findViewById(R.id.nav_view);

        register_first_name = (EditText) findViewById(R.id.register_first_name);
        register_last_name = (EditText) findViewById(R.id.register_last_name);
        register_email = (EditText) findViewById(R.id.register_email);
        register_password = (EditText) findViewById(R.id.register_password);
        register_password_confirm = (EditText) findViewById(R.id.register_password_confirm);
        register_country_code = (TextView) findViewById(R.id.register_country_code);
        register_phone = (EditText) findViewById(R.id.register_phone);
        register_dob = (TextView) findViewById(R.id.register_dob);

        helper.setDefaultEditTextSelectionMode(register_first_name);
        helper.setDefaultEditTextSelectionMode(register_last_name);
        helper.setDefaultEditTextSelectionMode(register_email);
        helper.setDefaultEditTextSelectionMode(register_password);
        helper.setDefaultEditTextSelectionMode(register_password_confirm);
        helper.setDefaultEditTextSelectionMode(register_phone);


        register_country_code.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MyAccountActivity.this, ChangeLocationActivity.class));
            }
        });

//        Database db = new Database();
//        cuisine = (Spinner) findViewById(R.id.cuisine);
//        ArrayAdapter<String> dataAdapter = new ArrayAdapter<>(this,
//                R.layout.spinner_items, db.getAllCuisines());
//        cuisine.setAdapter(dataAdapter);
//        helper.ToastMessage(MyAccountActivity.this, db.getCuisineIDByName(cuisine.getSelectedItem().toString()));
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.my_account_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.log_out:
                finish();
                Helpers.sendLogoutBroadcast();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    private void async(
            String first_name,
            String last_name,
            String email,
            String password,
            String phone_number,
            String dob
    ) {
        helper.setProgressDialogMessage(getString(R.string.progress_loading_account_update));
        helper.progressDialog(true);
        RestaurantService restaurantService = RestaurantService.retrofit.create(RestaurantService.class);
        final Call<JsonObject> call = restaurantService.update_user(
                first_name,
                last_name,
                email,
                SharedPrefs.getCityCode(),
                SharedPrefs.getCountryCode(),
                password,
                phone_number,
                dob,
                SharedPrefs.getUserFacebookID()
        );

        call.enqueue(new Callback<JsonObject>() {
            @Override
            public void onResponse(Call<JsonObject> call, Response<JsonObject> response) {
                helper.progressDialog(false);
                try {
                    JSONObject main = new JSONObject(response.body().toString());
                    Helpers.LogThis(response.body().toString());
                    String request_msg = main.getString("success");
                    if (request_msg.equals("true")) {

                        JSONObject user = main.getJSONObject("user");

                        SharedPrefs.setUserFirstName(user.getString("first_name"));
                        SharedPrefs.setUserLastName(user.getString("last_name"));
                        SharedPrefs.setUserEmail(user.getString("email"));
                        SharedPrefs.setUserPhone(user.getString("phone"));
                        SharedPrefs.setUserDOB(user.getString("date_of_birth"));

                        updateDrawer();
                        updateFields();
                        helper.ToastMessage(MyAccountActivity.this, "Successfully Updated");

                    } else {
                        helper.ToastMessage(MyAccountActivity.this, "Invalid credentials entered, Please try again");
                        updateFields();
                    }
                } catch (JSONException e) {
                    Helpers.LogThis("JSON exception " + e.toString());
                    helper.ToastMessage(MyAccountActivity.this, "Invalid credentials entered, Please try again");
                } catch (Exception e) {
                    Helpers.LogThis("Other exception " + e.toString());
                    helper.ToastMessage(MyAccountActivity.this, "Invalid credentials entered, Please try again");
                }
            }

            @Override
            public void onFailure(Call<JsonObject> call, Throwable t) {
                Helpers.LogThis("Failure: " + t.toString());
                Helpers.LogThis("Failure: " + call.toString());

                helper.progressDialog(false);

                if (helper.validateInternetNotPresent()) {
                    final Snackbar snackBar = Snackbar.make(findViewById(R.id.drawer_layout),
                            getString(R.string.error_connection), Snackbar.LENGTH_LONG);
                    snackBar.show();
                }
            }
        });
    }

}
