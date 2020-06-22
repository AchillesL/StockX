package com.example.stockx.view;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.view.View;

public class CommonAlertDialog {
    private AlertDialog.Builder mBuilder;

    public CommonAlertDialog(Context context, String title, String msg, DialogInterface.OnClickListener okButtonOnClickListener) {
        mBuilder = new AlertDialog.Builder(context).setTitle(title).setMessage(msg)
                .setNegativeButton(android.R.string.cancel, null)
                .setPositiveButton(android.R.string.ok, okButtonOnClickListener);
    }

    public CommonAlertDialog(Context context, String title, String msg, View view, DialogInterface.OnClickListener okButtonOnClickListener) {
        mBuilder = new AlertDialog.Builder(context).setTitle(title).setMessage(msg).setView(view)
                .setNegativeButton(android.R.string.cancel, null)
                .setPositiveButton(android.R.string.ok, okButtonOnClickListener);
    }

    public void show() {
        mBuilder.create().show();
    }
}
