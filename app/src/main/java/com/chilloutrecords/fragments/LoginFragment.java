package com.chilloutrecords.fragments;

import android.os.Bundle;
import android.view.InflateException;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.fragment.app.Fragment;

import com.chilloutrecords.R;
import com.chilloutrecords.utils.Helper;
import com.chilloutrecords.utils.StaticMethods;
import com.google.android.material.button.MaterialButton;

public class LoginFragment extends Fragment {
    private View root_view;
    private Helper helper;
    private final String TAG_LOG = "LOGIN";

    private EditText
            et_email,
            et_password;
    private MaterialButton btn_login;

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
                root_view = inflater.inflate(R.layout.frag_login, container, false);

                AppCompatDelegate.setCompatVectorFromResourcesEnabled(true);

                helper = new Helper(getActivity());

                et_email = root_view.findViewById(R.id.et_email);
                et_password = root_view.findViewById(R.id.et_password);
                btn_login = root_view.findViewById(R.id.btn_login);

                btn_login.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        if (StaticMethods.validateEmail(et_email) &&
                                StaticMethods.validateEmptyEditText(et_password, getString(R.string.error_field_required))) {
                            helper.setDialogConfirm("TEST", "Message to the user", "PRESS", new StaticMethods.DialogListener() {
                                @Override
                                public void yes() {
                                    helper.setDialogCancel("PRESSED", "YES YOU PRESSED THAT");
                                }

                                @Override
                                public void no() {
                                    helper.showToast("A TOASTY TOAST");
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
