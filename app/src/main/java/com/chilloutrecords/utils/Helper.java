package com.chilloutrecords.utils;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.widget.TextView;
import android.widget.Toast;

import com.chilloutrecords.R;
import com.chilloutrecords.activities.AboutUsActivity;
import com.chilloutrecords.activities.HomeActivity;
import com.chilloutrecords.activities.MyAccountActivity;
import com.google.android.material.button.MaterialButton;

import java.util.Objects;

public class Helper {
    private Context context;
    private static Toast toast;
    private TextView txt_loading_message;
    private final Dialog dialog_progress;

    public Helper(Context context) {
        this.context = context;

        dialog_progress = new Dialog(context);
        dialog_progress.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog_progress.setContentView(R.layout.dialog_loading);
        dialog_progress.setCancelable(false);

        txt_loading_message = dialog_progress.findViewById(R.id.txt_loading_message);
    }

    // DRAWER NAVIGATION ===========================================================================
    public void Drawer_Item_Clicked(Context context, int id) {
        if (id == R.id.home) {
            context.startActivity(new Intent(context, HomeActivity.class));
        } else if (id == R.id.my_account) {
            context.startActivity(new Intent(context, MyAccountActivity.class));
        } else if (id == R.id.about_us) {
            context.startActivity(new Intent(context, AboutUsActivity.class));
        } else if (id == R.id.log_out) {
            StaticMethods.broadcastLogout();
        }
    }


    // DIALOGS =====================================================================================
    public void setProgressDialog(String message) {
        if (!dialog_progress.isShowing()) {
            dialog_progress.show();
            txt_loading_message.setText(message);
        } else {
            dialog_progress.cancel();
            dialog_progress.show();
            txt_loading_message.setText(message);
        }
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
            final StaticMethods.DialogListener response) {

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
                response.yes();
                dialog.cancel();
            }
        });
        btn_cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                response.no();
                dialog.cancel();
            }
        });
        dialog.show();
    }

    public void setDialogCancel (
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

    public void showToast(String message) {
        if (toast != null) {
            toast.cancel();
        }
        toast = Toast.makeText(context, message, Toast.LENGTH_LONG);
        toast.setGravity(Gravity.CENTER, 0, 0);
        toast.show();
    }


}
