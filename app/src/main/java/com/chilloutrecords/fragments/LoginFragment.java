package com.chilloutrecords.fragments;

import android.os.Bundle;
import android.view.InflateException;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.chilloutrecords.BuildConfig;
import com.chilloutrecords.R;
import com.chilloutrecords.utils.Database;
import com.chilloutrecords.utils.DialogMethods;
import com.chilloutrecords.utils.StaticMethods;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.AuthResult;

import java.util.Objects;

import static com.chilloutrecords.utils.StaticVariables.FIREBASE_AUTH;

public class LoginFragment extends Fragment {
    private View root_view;
    private DialogMethods dialogs;
    private final String TAG_LOG = "LOGIN";

    private TextInputEditText
            et_email,
            et_password;
    private TextInputLayout
            etl_email,
            etl_password;

    // OVERRIDE METHODS ============================================================================
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        if (root_view == null && getActivity() != null) {
            try {
                root_view = inflater.inflate(R.layout.frag_login, container, false);

                dialogs = new DialogMethods(getActivity());

                et_email = root_view.findViewById(R.id.et_email);
                et_password = root_view.findViewById(R.id.et_password);

                etl_email = root_view.findViewById(R.id.etl_email);
                etl_password = root_view.findViewById(R.id.etl_password);

                if (BuildConfig.DEBUG) {
                    et_email.setText(BuildConfig.TEST_EMAIL);
                    et_password.setText(BuildConfig.TEST_PASSWORD);
                }

                root_view.findViewById(R.id.btn_login).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        if (StaticMethods.validateEmail(et_email, etl_email) && StaticMethods.validateEmptyEditText(et_password, etl_password, getString(R.string.error_field_required))) {

                            dialogs.setProgressDialog(getString(R.string.progress_login));

                            FIREBASE_AUTH
                                    .signInWithEmailAndPassword(
                                            Objects.requireNonNull(et_email.getText()).toString().trim(),
                                            Objects.requireNonNull(et_password.getText()).toString().trim())
                                    .addOnCompleteListener(Objects.requireNonNull(getActivity()), new OnCompleteListener<AuthResult>() {
                                        @Override
                                        public void onComplete(@NonNull Task<AuthResult> task) {

                                            dialogs.dismissProgressDialog();

                                            if (task.isSuccessful()) {
                                                StaticMethods.logg(TAG_LOG, "Sign in success");
                                                Database.getUserIdAndLogin(getActivity());
                                            } else {
                                                if (task.getException() != null && !task.getException().toString().equals("") && task.getException().toString().contains(":")) {
                                                    String[] messages = task.getException().toString().split(":");
                                                    dialogs.setDialogCancel(getString(R.string.txt_alert), messages[1]);
                                                } else {
                                                    dialogs.setDialogCancel(getString(R.string.txt_alert), getString(R.string.error_login_failed));
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
