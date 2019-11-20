package com.chilloutrecords.utils;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import com.chilloutrecords.R;
import com.chilloutrecords.fragments.ImageViewFragment;
import com.chilloutrecords.interfaces.GeneralInterface;
import com.chilloutrecords.models.UserModel;
import com.google.android.material.button.MaterialButton;

import static com.chilloutrecords.utils.StaticVariables.EXTRA_DATA;
import static com.chilloutrecords.utils.StaticVariables.STR_IMAGE_URL;

import java.util.Objects;

public class DialogMethods {
    private Context context;
    private TextView txt_loading_message;
    private final Dialog dialog_progress;

    public DialogMethods(Context context) {
        this.context = context;

        dialog_progress = new Dialog(context);
        dialog_progress.requestWindowFeature(Window.FEATURE_NO_TITLE);
        Objects.requireNonNull(dialog_progress.getWindow()).setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog_progress.setContentView(R.layout.dialog_progress);
        dialog_progress.setCancelable(false);

        txt_loading_message = dialog_progress.findViewById(R.id.txt_loading_message);
    }

    public void setProgressDialog(String message) {
        if (dialog_progress.isShowing()) {
            dialog_progress.cancel();
        }
        if (message.equals("")) {
            message = context.getString(R.string.progress_loading);
        }
        txt_loading_message.setText(message);
        dialog_progress.show();
    }

    public void dismissProgressDialog() {
        if (dialog_progress != null && dialog_progress.isShowing()) {
            dialog_progress.dismiss();
        }
    }

    public void setDialogConfirm(
            String str_title,
            String str_message,
            String str_confirm,
            final GeneralInterface response) {

        final Dialog dialog = new Dialog(context);
        dialog.setContentView(R.layout.dialog_confirm);
        dialog.setCancelable(false);
        Objects.requireNonNull(dialog.getWindow()).setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        final TextView txt_message = dialog.findViewById(R.id.txt_message);
        final MaterialButton btn_confirm = dialog.findViewById(R.id.btn_confirm);
        final MaterialButton btn_cancel = dialog.findViewById(R.id.btn_cancel);
        final TextView txt_title = dialog.findViewById(R.id.txt_title);
        txt_title.setText(str_title);
        txt_message.setText(str_message);
        btn_confirm.setText(str_confirm);
        btn_confirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                response.success();
                dialog.cancel();
            }
        });
        btn_cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                response.failed();
                dialog.cancel();
            }
        });
        dialog.show();
    }

    public void setDialogCancel(
            String str_title,
            String str_message) {
        final Dialog dialog = new Dialog(context);
        dialog.setContentView(R.layout.dialog_cancel);
        dialog.setCancelable(false);
        Objects.requireNonNull(dialog.getWindow()).setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        final TextView txt_message = dialog.findViewById(R.id.txt_message);
        final MaterialButton btn_confirm = dialog.findViewById(R.id.btn_confirm);
        final TextView txt_title = dialog.findViewById(R.id.txt_title);
        txt_title.setText(str_title);
        txt_message.setText(str_message);
        btn_confirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.cancel();
            }
        });
        dialog.show();
    }

    public void setDialogImagePreview(String image_url) {
        STR_IMAGE_URL = image_url;
        final Dialog dialog = new Dialog(context);
        dialog.setContentView(R.layout.dialog_image_view);
        dialog.setCancelable(true);
        Objects.requireNonNull(dialog.getWindow()).setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.show();
        dialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialogInterface) {
                Fragment fragment = ((AppCompatActivity) context).getSupportFragmentManager().findFragmentById(R.id.fragment_image_view);
                if (fragment != null) {
                    ((AppCompatActivity) context).getSupportFragmentManager().beginTransaction().remove(fragment).commit();
                }
            }
        });
    }

    public void setDialogProfileUpdate() {
        final Dialog dialog = new Dialog(context);
        dialog.setContentView(R.layout.dialog_update_profile);
        dialog.setCancelable(true);
        Objects.requireNonNull(dialog.getWindow()).setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.show();
        dialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialogInterface) {
                Fragment fragment = ((AppCompatActivity) context).getSupportFragmentManager().findFragmentById(R.id.fragment_register);
                if (fragment != null) {
                    ((AppCompatActivity) context).getSupportFragmentManager().beginTransaction().remove(fragment).commit();
                }
            }
        });
    }


}
