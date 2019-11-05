package com.chilloutrecords.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.view.InflateException;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.Spinner;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.fragment.app.Fragment;

import com.chilloutrecords.BuildConfig;
import com.chilloutrecords.R;
import com.chilloutrecords.activities.HomeActivity;
import com.chilloutrecords.models.UserModel;
import com.chilloutrecords.utils.Database;
import com.chilloutrecords.utils.Helper;
import com.chilloutrecords.utils.StaticMethods;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.button.MaterialButton;
import com.google.firebase.auth.AuthResult;

import java.util.Calendar;

import static com.chilloutrecords.utils.StaticVariables.FIREBASE_AUTH;

public class RegisterFragment extends Fragment {
    private View root_view;
    private Helper helper;
    private final String TAG_LOG = "REGISTER";

    private EditText et_email, et_name, et_stage_name, et_password, et_password_confirm;
    private Spinner spinner_gender;
    MaterialButton btn_register;

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

                AppCompatDelegate.setCompatVectorFromResourcesEnabled(true);

                helper = new Helper(getActivity());

                et_name = root_view.findViewById(R.id.et_name);
                et_stage_name = root_view.findViewById(R.id.et_stage_name);
                spinner_gender = root_view.findViewById(R.id.spinner_gender);

                et_email = root_view.findViewById(R.id.et_email);
                et_password = root_view.findViewById(R.id.et_password);
                et_password_confirm = root_view.findViewById(R.id.et_password_confirm);

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
                                StaticMethods.validateEmptyEditText(et_name, getString(R.string.error_field_required)) &&
                                        StaticMethods.validateEmptyEditText(et_stage_name, getString(R.string.error_field_required)) &&
                                        StaticMethods.validateEmail(et_email) &&
                                        StaticMethods.validateEmptyEditText(et_password, getString(R.string.error_field_required)) &&
                                        StaticMethods.validateEmptyEditText(et_password_confirm, getString(R.string.error_field_required)) &&
                                        StaticMethods.validateMatch(et_password, et_password_confirm)
                        ) {
                            helper.setProgressDialog(getString(R.string.progress_register));
                            FIREBASE_AUTH.createUserWithEmailAndPassword(et_email.getText().toString().trim(), et_password.getText().toString().trim())
                                    .addOnCompleteListener(getActivity(), new OnCompleteListener<AuthResult>() {
                                        @Override
                                        public void onComplete(@NonNull Task<AuthResult> task) {
                                            helper.dismissProgressDialog();
                                            if (task.isSuccessful()) {
                                                UserModel user = new UserModel();
                                                user.name = et_name.getText().toString().trim();
                                                user.stage_name = et_stage_name.getText().toString().trim();
                                                user.member_since_date = Calendar.getInstance().getTimeInMillis();
                                                user.email = et_email.getText().toString().trim();
                                                user.gender = spinner_gender.getSelectedItemPosition();
                                                user.is_artist = false;
                                                user.id = FIREBASE_AUTH.getCurrentUser().getUid();
                                                Database.updateUser(user);

                                                helper.showToast("success");

                                            } else {
                                                StaticMethods.logg(TAG_LOG, "Registration Failed " + task.getException());

                                                if (task.getException() != null && !task.getException().toString().equals("") && task.getException().toString().contains(":")) {
                                                    String[] messages = task.getException().toString().split(":");
                                                    helper.setDialogCancel(getString(R.string.txt_alert), messages[1]);
                                                } else {
                                                    helper.setDialogCancel(getString(R.string.txt_alert), getString(R.string.error_register_failed));
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
}
