package com.chilloutrecords.fragments;

import android.os.Bundle;
import android.view.InflateException;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.Spinner;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.chilloutrecords.BuildConfig;
import com.chilloutrecords.R;
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

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Objects;

import static com.chilloutrecords.utils.StaticVariables.FIREBASE_AUTH;
import static com.chilloutrecords.utils.StaticVariables.FIREBASE_USER;

public class RegisterFragment extends Fragment {
    private View root_view;
    private DialogMethods dialogs;
    private final String TAG_LOG = "REGISTER";

    private TextInputEditText
            et_email,
            et_name,
            et_stage_name,
            et_password,
            et_password_confirm;
    private TextInputLayout
            etl_email,
            etl_name,
            etl_stage_name,
            etl_password,
            etl_password_confirm;

    private Spinner spinner_gender;
    private MaterialButton btn_register;

    // OVERRIDE METHODS ============================================================================
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {

        }
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        if (root_view == null && getActivity() != null) {
            try {
                root_view = inflater.inflate(R.layout.frag_register, container, false);

                dialogs = new DialogMethods(getActivity());

                spinner_gender = root_view.findViewById(R.id.spinner_gender);

                et_name = root_view.findViewById(R.id.et_name);
                et_stage_name = root_view.findViewById(R.id.et_stage_name);
                et_email = root_view.findViewById(R.id.et_email);
                et_password = root_view.findViewById(R.id.et_password);
                et_password_confirm = root_view.findViewById(R.id.et_password_confirm);

                etl_name = root_view.findViewById(R.id.etl_name);
                etl_stage_name = root_view.findViewById(R.id.etl_stage_name);
                etl_email = root_view.findViewById(R.id.etl_email);
                etl_password = root_view.findViewById(R.id.etl_password);
                etl_password_confirm = root_view.findViewById(R.id.etl_password_confirm);

                btn_register = root_view.findViewById(R.id.btn_register);

                if (BuildConfig.DEBUG) {
                    et_name.setText("Asim");
                    et_stage_name.setText("Speedy");
                    et_email.setText("user@test.com");
                    et_password.setText("password");
                    et_password_confirm.setText("password");
                }

                btn_register.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        if (
                                StaticMethods.validateEmptyEditText(et_name, etl_name, getString(R.string.error_field_required)) &&
                                        StaticMethods.validateEmptyEditText(et_stage_name, etl_stage_name,getString(R.string.error_field_required)) &&
                                        StaticMethods.validateEmail(et_email, etl_email) &&
                                        StaticMethods.validateEmptyEditText(et_password, etl_password,getString(R.string.error_field_required)) &&
                                        StaticMethods.validateEmptyEditText(et_password_confirm, etl_password_confirm,getString(R.string.error_field_required)) &&
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


            } catch (InflateException e) {
                e.printStackTrace();
            }
        } else {
            ((ViewGroup) container.getParent()).removeView(root_view);
        }
        return root_view;
    }

    private void createUser(){
        // CREATE THE NEW USER
        UserModel user = new UserModel();

        // STANDARD DATA
        user.id = Objects.requireNonNull(FIREBASE_AUTH.getCurrentUser()).getUid();
        user.name = Objects.requireNonNull(et_name.getText()).toString().trim();
        user.stage_name = Objects.requireNonNull(et_stage_name.getText()).toString().trim();
        user.email = Objects.requireNonNull(et_email.getText()).toString().trim();
        user.info = "..tell us something about you..";
        user.gender = spinner_gender.getSelectedItemPosition();
        user.p_pic = "default_pic.jpg";

        // MUSIC INFO
        user.music.collections.add("0");
        user.music.singles.add("0");

        // META DATA
        user.member_since_date = Calendar.getInstance().getTimeInMillis();
        user.is_artist = false;
        user.play_count = 0;
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
}
