package com.chilloutrecords.utils;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.provider.Settings;
import android.view.View;
import android.view.Window;
import android.widget.TextView;

import com.chilloutrecords.R;
import com.chilloutrecords.interfaces.GeneralInterface;
import com.google.android.material.button.MaterialButton;

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

    public void setDialogPermissions(int[] grant_results){
        for (int result : grant_results) {
            if (result == -1) {
                final Dialog dialog = new Dialog(context);
                dialog.setContentView(R.layout.dialog_confirm);
                Objects.requireNonNull(dialog.getWindow()).setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                final TextView txt_message = dialog.findViewById(R.id.txt_message);
                final MaterialButton btn_confirm = dialog.findViewById(R.id.btn_confirm);
                final MaterialButton btn_cancel = dialog.findViewById(R.id.btn_cancel);
                final TextView txt_title = dialog.findViewById(R.id.txt_title);
                txt_title.setText(R.string.txt_alert);
                txt_message.setText(R.string.error_permissions);
                btn_confirm.setText(R.string.txt_take_me_there);
                btn_cancel.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        dialog.cancel();
                    }
                });
                btn_confirm.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent();
                        intent.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
                        intent.setAction(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                        Uri uri = Uri.fromParts("package", context.getPackageName(), null);
                        intent.setData(uri);
                        context.startActivity(intent);
                        dialog.cancel();
                    }
                });
                dialog.show();
                break;
            }
        }
    }


}
