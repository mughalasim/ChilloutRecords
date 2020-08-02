package com.chilloutrecords.fragments;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.view.InflateException;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.chilloutrecords.BuildConfig;
import com.chilloutrecords.R;
import com.chilloutrecords.activities.ParentActivity;
import com.chilloutrecords.interfaces.GeneralInterface;
import com.chilloutrecords.models.UserModel;
import com.chilloutrecords.utils.Database;
import com.chilloutrecords.utils.DialogMethods;
import com.chilloutrecords.utils.StaticMethods;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.AuthResult;

import java.util.Calendar;
import java.util.Objects;

import static com.chilloutrecords.utils.StaticVariables.FIREBASE_AUTH;
import static com.chilloutrecords.utils.StaticVariables.FIREBASE_USER;
import static com.chilloutrecords.utils.StaticVariables.USER_MODEL;

public class RegisterFragment extends Fragment {
    private View root_view;
    private DialogMethods dialogs;
    private final String TAG_LOG = "REGISTER";

    private TextInputEditText
            et_email,
            et_name,
            et_stage_name,
            et_info,
            et_password,
            et_password_confirm;
    private TextInputLayout
            etl_email,
            etl_name,
            etl_stage_name,
            etl_info,
            etl_password,
            etl_password_confirm;
    private boolean isRegistration = true;
    private Spinner spinner_gender;
    private UserModel extra_user = new UserModel();

    // OVERRIDE METHODS ============================================================================
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getActivity() instanceof ParentActivity) {
            isRegistration = false;
            extra_user = USER_MODEL;
        }
    }

    @SuppressLint("SetTextI18n")
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        if (root_view == null && getActivity() != null) {
            try {
                root_view = inflater.inflate(R.layout.frag_register, container, false);

                dialogs = new DialogMethods(getActivity());

                spinner_gender = root_view.findViewById(R.id.spinner_gender);

                TextView txt_title = root_view.findViewById(R.id.txt_title);

                et_name = root_view.findViewById(R.id.et_name);
                et_stage_name = root_view.findViewById(R.id.et_stage_name);
                et_info = root_view.findViewById(R.id.et_info);
                et_email = root_view.findViewById(R.id.et_email);
                et_password = root_view.findViewById(R.id.et_password);
                et_password_confirm = root_view.findViewById(R.id.et_password_confirm);

                etl_name = root_view.findViewById(R.id.etl_name);
                etl_stage_name = root_view.findViewById(R.id.etl_stage_name);
                etl_info = root_view.findViewById(R.id.etl_info);
                etl_email = root_view.findViewById(R.id.etl_email);
                etl_password = root_view.findViewById(R.id.etl_password);
                etl_password_confirm = root_view.findViewById(R.id.etl_password_confirm);

                MaterialButton btn_confirm = root_view.findViewById(R.id.btn_confirm);

                if (isRegistration) {
                    txt_title.setText(getString(R.string.txt_register));
                    btn_confirm.setText(getString(R.string.nav_register));
                    etl_info.setVisibility(View.GONE);
                    etl_email.setVisibility(View.VISIBLE);
                    etl_password.setVisibility(View.VISIBLE);
                    etl_password_confirm.setVisibility(View.VISIBLE);

                if (BuildConfig.DEBUG) {
                        et_name.setText("User");
                        et_stage_name.setText("Test user");
                        et_email.setText(BuildConfig.TEST_EMAIL);
                        et_password.setText(BuildConfig.TEST_PASSWORD);
                        et_password_confirm.setText(BuildConfig.TEST_PASSWORD);
                        spinner_gender.setSelection(1);
                }

                    btn_confirm.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            if (
                                    StaticMethods.validateEmptyEditText(et_name, etl_name, getString(R.string.error_field_required)) &&
                                            StaticMethods.validateEmptyEditText(et_stage_name, etl_stage_name, getString(R.string.error_field_required)) &&
                                            StaticMethods.validateEmail(et_email, etl_email) &&
                                            StaticMethods.validateEmptyEditText(et_password, etl_password, getString(R.string.error_field_required)) &&
                                            StaticMethods.validateEmptyEditText(et_password_confirm, etl_password_confirm, getString(R.string.error_field_required)) &&
                                            StaticMethods.validateMatch(et_password, etl_password, et_password_confirm, etl_password_confirm)
                            ) {
                                dialogs.setProgressDialog(getString(R.string.progress_register));
                                FIREBASE_AUTH.createUserWithEmailAndPassword(Objects.requireNonNull(et_email.getText()).toString().trim(), Objects.requireNonNull(et_password.getText()).toString().trim())
                                        .addOnCompleteListener(Objects.requireNonNull(getActivity()), new OnCompleteListener<AuthResult>() {
                                            @Override
                                            public void onComplete(@NonNull Task<AuthResult> task) {
                                                dialogs.dismissProgressDialog();
                                                if (task.isSuccessful()) {
                                                    createUser();
                                                } else {
                                                    StaticMethods.logg(TAG_LOG, "Registration Failed " + task.getException());

                                                    if (task.getException() != null && !task.getException().toString().equals("") && task.getException().toString().contains(":")) {
                                                        String[] messages = task.getException().toString().split(":");
                                                        dialogs.setDialogCancel(getString(R.string.txt_alert), messages[1]);
                                                    } else {
                                                        dialogs.setDialogCancel(getString(R.string.txt_alert), getString(R.string.error_register_failed));
                                                    }

                                                }
                                            }
                                        });
                            }
                        }
                    });
                } else {
                    txt_title.setText(getString(R.string.txt_update_profile));
                    btn_confirm.setText(getString(R.string.txt_update));
                    etl_info.setVisibility(View.VISIBLE);
                    etl_email.setVisibility(View.GONE);
                    etl_password.setVisibility(View.GONE);
                    etl_password_confirm.setVisibility(View.GONE);
                    populateFromModel();
                    btn_confirm.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            if (
                                    StaticMethods.validateEmptyEditText(et_name, etl_name, getString(R.string.error_field_required)) &&
                                            StaticMethods.validateEmptyEditText(et_stage_name, etl_stage_name, getString(R.string.error_field_required)) &&
                                            StaticMethods.validateEmptyEditText(et_info, etl_info, getString(R.string.error_field_required))
                            ) {
                                updateUser();
                            }
                        }
                    });
                }

            } catch (InflateException e) {
                e.printStackTrace();
            }
        } else {
            ((ViewGroup) container.getParent()).removeView(root_view);
        }
        return root_view;
    }

    private void populateFromModel() {
        et_name.setText(extra_user.name);
        et_stage_name.setText(extra_user.stage_name);
        spinner_gender.setSelection(extra_user.gender);
        et_info.setText(extra_user.info);
    }

    private void createUser() {
        // CREATE THE NEW USER
        UserModel user = new UserModel();

        // STANDARD DATA
        user.id = Objects.requireNonNull(FIREBASE_AUTH.getCurrentUser()).getUid();
        user.name = Objects.requireNonNull(et_name.getText()).toString().trim();
        user.stage_name = Objects.requireNonNull(et_stage_name.getText()).toString().trim();
        user.email = Objects.requireNonNull(et_email.getText()).toString().trim();
        user.info = "..tell us something about you..";
        user.gender = spinner_gender.getSelectedItemPosition();
        user.p_pic = BuildConfig.DEFAULT_PROFILE_ART;
        user.is_activated = false;
        user.fcm_token = "";

        // MUSIC INFO
        user.music.collections.add("0");
        user.music.singles.add("0");

        // META DATA
        user.member_since_date = Calendar.getInstance().getTimeInMillis();
        user.is_artist = false;
        user.profile_visits = 0;

        // SET THE USER IN THE DATABASE
        Database.setUser(user, new GeneralInterface() {
            @Override
            public void success() {
                Database.getUserIdAndLogin(getActivity());
            }

            @Override
            public void failed() {
                // IF THE USER FAILED TO GET INSERTED, WE DELETE THE USER FORM THE AUTH LIST AND SIGN OUT
                FIREBASE_USER.delete();
                FIREBASE_AUTH.signOut();
                dialogs.setDialogCancel(getString(R.string.txt_alert), getString(R.string.error_register_failed));
            }
        });
    }

    private void updateUser() {
        // UPDATE USER
        extra_user.name = Objects.requireNonNull(et_name.getText()).toString().trim();
        extra_user.stage_name = Objects.requireNonNull(et_stage_name.getText()).toString().trim();
        extra_user.info = Objects.requireNonNull(et_info.getText()).toString().trim();
        extra_user.gender = spinner_gender.getSelectedItemPosition();

        // UPDATE THE USER IN THE DATABASE
        Database.setUser(extra_user, new GeneralInterface() {
            @Override
            public void success() {
                Objects.requireNonNull(getActivity()).onBackPressed();
            }

            @Override
            public void failed() {
                StaticMethods.showToast(getString(R.string.error_update_failed));
            }
        });
    }
}
