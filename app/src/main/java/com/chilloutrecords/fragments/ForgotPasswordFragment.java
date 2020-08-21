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
import com.chilloutrecords.activities.StartUpActivity;
import com.chilloutrecords.utils.DialogMethods;
import com.chilloutrecords.utils.StaticMethods;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

import java.util.Objects;

import static com.chilloutrecords.utils.StaticVariables.FIREBASE_AUTH;

public class ForgotPasswordFragment extends Fragment {
    private View root_view;
    private DialogMethods dialogs;
    private final String TAG_LOG = "PASSWORD RESET";

    private TextInputEditText
            et_email;
    private TextInputLayout
            etl_email;

    // OVERRIDE METHODS ============================================================================
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        if (root_view == null && getActivity() != null) {
            try {
                root_view = inflater.inflate(R.layout.frag_forgot_password, container, false);

                dialogs = new DialogMethods(getActivity());

                et_email = root_view.findViewById(R.id.et_email);
                etl_email = root_view.findViewById(R.id.etl_email);
                MaterialButton btn_reset = root_view.findViewById(R.id.btn_reset);

                if(BuildConfig.DEBUG){
                    et_email.setText(BuildConfig.TEST_EMAIL);
                }

                btn_reset.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        if (StaticMethods.validateEmail(et_email, etl_email)) {

                            dialogs.setProgressDialog(getString(R.string.progress_password_reset));

                            FIREBASE_AUTH
                                    .sendPasswordResetEmail(Objects.requireNonNull(et_email.getText()).toString().trim())
                                    .addOnCompleteListener(Objects.requireNonNull(getActivity()), new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {

                                            dialogs.dismissProgressDialog();

                                            if (task.isSuccessful()) {
                                                StaticMethods.logg(TAG_LOG, "Reset link sent successfully");
                                                StaticMethods.showToast(1, getString(R.string.toast_reset_success));
                                                ((StartUpActivity) getActivity()).backToLogin();
                                            } else {
                                                if (task.getException() != null && !task.getException().toString().equals("") && task.getException().toString().contains(":")) {
                                                    String[] messages = task.getException().toString().split(":");
                                                    dialogs.setDialogCancel(getString(R.string.txt_alert), messages[1]);
                                                } else {
                                                    dialogs.setDialogCancel(getString(R.string.txt_alert), getString(R.string.error_reset_failed));
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
