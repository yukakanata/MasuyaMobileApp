package com.yusuffahrudin.masuyamobileapp.controller;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.yusuffahrudin.masuyamobileapp.R;

public class DialogAlert {
    private String message, tipe;
    private Activity activity;
    private android.app.AlertDialog.Builder dialog;
    private View dialogView;
    private LayoutInflater inflater;

    public DialogAlert(String message, String tipe, Activity activity) {
        this.message = message;
        this.tipe = tipe;
        this.activity = activity;
        proses();
    }

    private void proses(){
        if (tipe.equalsIgnoreCase("success")){
            dialogSuccess(message);
        } else if (tipe.equalsIgnoreCase("attention")){
            dialogAttention(message);
        } else {
            dialogError(message);
        }
    }

    private void dialogSuccess(String pesan){
        dialog = new android.app.AlertDialog.Builder(activity);
        inflater = activity.getLayoutInflater();
        dialogView = inflater.inflate(R.layout.dialog_success, null);
        dialog.setView(dialogView);

        Button btn_ok = dialogView.findViewById(R.id.btn_ok);
        TextView tv_message = dialogView.findViewById(R.id.message);
        tv_message.setText(pesan);

        final android.app.AlertDialog alert = dialog.create();
        alert.show();

        btn_ok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                alert.dismiss();
                //activity.finish();
            }
        });
    }

    private void dialogError(String pesan){
        dialog = new android.app.AlertDialog.Builder(activity);
        inflater = activity.getLayoutInflater();
        dialogView = inflater.inflate(R.layout.dialog_error, null);
        dialog.setView(dialogView);

        Button btn_ok = dialogView.findViewById(R.id.btn_ok);
        TextView tv_message = dialogView.findViewById(R.id.message);
        tv_message.setText(pesan);

        final android.app.AlertDialog alert = dialog.create();
        alert.show();

        btn_ok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                alert.dismiss();
            }
        });
    }

    private void dialogAttention(String pesan){
        dialog = new android.app.AlertDialog.Builder(activity);
        inflater = activity.getLayoutInflater();
        dialogView = inflater.inflate(R.layout.dialog_attention, null);
        dialog.setView(dialogView);

        Button btn_ok = dialogView.findViewById(R.id.btn_ok);
        TextView tv_message = dialogView.findViewById(R.id.message);
        tv_message.setText(pesan);

        final android.app.AlertDialog alert = dialog.create();
        alert.show();

        btn_ok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                alert.dismiss();
            }
        });
    }
}
