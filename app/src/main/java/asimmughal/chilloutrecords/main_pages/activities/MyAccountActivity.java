package asimmughal.chilloutrecords.main_pages.activities;

import android.app.DatePickerDialog;
import android.os.Bundle;
import android.view.View;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.makeramen.roundedimageview.RoundedImageView;

import java.util.Calendar;
import java.util.TimeZone;

import asimmughal.chilloutrecords.R;
import asimmughal.chilloutrecords.utils.SharedPrefs;

public class MyAccountActivity extends ParentActivity {

    private RoundedImageView
            user_thumbnail;

    private EditText
            register_first_name,
            register_last_name,
            register_email,
            register_phone;

    private TextView
            register_dob,
            picture_add,
            picture_delete;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_my_account);

        initialize(R.id.my_account, "MY ACCOUNT");

        findAllViews();

        updateFields();

        setDates();

        setOnclickListeners();

    }

    private void updateFields() {
        Glide.with(MyAccountActivity.this).load(SharedPrefs.getUserPic()).into(user_thumbnail);
        if (SharedPrefs.getUserPic().contains("default-icon-user.png")) {
            picture_delete.setVisibility(View.GONE);
        } else {
            picture_delete.setVisibility(View.VISIBLE);
        }
        register_first_name.setText(SharedPrefs.getUserFirstName());
        register_last_name.setText(SharedPrefs.getUserLastName());
        register_email.setText(SharedPrefs.getUserEmail());
        register_phone.setText(SharedPrefs.getUserPhone());
        register_dob.setText(SharedPrefs.getUserDOB());
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
            SharedPrefs.setUserFirstName(register_first_name.getText().toString());
            SharedPrefs.setUserLastName(register_last_name.getText().toString());
            SharedPrefs.setUserEmail(register_email.getText().toString());
            SharedPrefs.setUserPhone(register_phone.getText().toString());
            SharedPrefs.setUserDOB(register_dob.getText().toString());
            helper.ToastMessage(MyAccountActivity.this, "Successfully Updated");
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
        toolbar = findViewById(R.id.toolbar);
        drawer = findViewById(R.id.drawer_layout);
        navigationView = findViewById(R.id.nav_view);

        register_first_name = findViewById(R.id.register_first_name);
        register_last_name = findViewById(R.id.register_last_name);
        register_email = findViewById(R.id.register_email);
        register_phone = findViewById(R.id.register_phone);
        register_dob = findViewById(R.id.register_dob);

        user_thumbnail = findViewById(R.id.user_thumbnail);
        picture_add = findViewById(R.id.picture_add);
        picture_delete = findViewById(R.id.picture_delete);

        helper.setDefaultEditTextSelectionMode(register_first_name);
        helper.setDefaultEditTextSelectionMode(register_last_name);
        helper.setDefaultEditTextSelectionMode(register_email);
        helper.setDefaultEditTextSelectionMode(register_phone);

    }

    private void setOnclickListeners() {
        picture_add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                helper.myDialog(MyAccountActivity.this, getString(R.string.txt_alert), getString(R.string.txt_coming_soon));
            }
        });

        picture_delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SharedPrefs.setUserPic("https://api.eatout.co.ke/assets/img/default-icon-user.png");
                updateFields();
            }
        });

    }
}
