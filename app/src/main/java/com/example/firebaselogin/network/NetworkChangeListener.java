package com.example.firebaselogin.network;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import androidx.appcompat.app.AlertDialog;
import com.example.firebaselogin.R;

public class NetworkChangeListener extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            if(!NetworkState.isConnectedToInternet(context)){
                AlertDialog.Builder builder =  new AlertDialog.Builder(context);
                View view = LayoutInflater.from(context).inflate(R.layout.alertdialog_layout,null);
                builder.setView(view);
                Button btnRetry = view.findViewById(R.id.alerdialog_btn_retry);
                AlertDialog dialog = builder.create();
                dialog.show();
                dialog.setCancelable(false);
                dialog.getWindow().setGravity(Gravity.CENTER);

                btnRetry.setOnClickListener(view1 -> {
                    dialog.dismiss();
                    onReceive(context,intent);
                });
            }
        }

    }

