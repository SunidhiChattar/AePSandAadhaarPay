package com.matm.matmsdk.aepsmodule.alertShow;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.provider.Settings;
import android.view.Window;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

public class SingletonClass extends AppCompatActivity {

    private static SingletonClass instance = null;
    public static synchronized SingletonClass getInstance() {
        if(instance == null) {
            instance = new SingletonClass();
        }
        // returns the singleton object
        return instance;
    }

    /**
     *
     * @param msg
     * @param showAlert_context
     */
    public void showAlert(String msg, Context showAlert_context) {
        try {
            AlertDialog.Builder builder = new AlertDialog.Builder(showAlert_context);
            builder.setTitle("Alert!!");
            builder.setMessage(msg);
            builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.dismiss();

                }
            });
            AlertDialog dialog = builder.create();
            dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
            dialog.setCancelable(false);
            dialog.show();

        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    /**
     *
     * @param settingAlert_context
     */
    public void showSettingsAlert(Context settingAlert_context){
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(settingAlert_context);
        // Setting Dialog Title
        alertDialog.setTitle("GPS is settings");
        // Setting Dialog Message
        alertDialog.setMessage("GPS is not enabled. Do you want to go to settings menu?");
        // On pressing Settings button
        alertDialog.setPositiveButton("Settings", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog,int which) {
                Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                settingAlert_context.startActivity(intent);
                finish();
            }
        });
        // on pressing cancel button
        alertDialog.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
                finish();
            }
        });
        alertDialog.show();
    }

}
