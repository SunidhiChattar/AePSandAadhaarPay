package com.matm.matmsdk.MPOS;

import static com.matm.matmsdk.Utils.SdkConstants.REQUEST_CODE;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.matm.matmsdk.Error.Error2Activity;
import com.matm.matmsdk.Error.ErrorActivity;
import com.matm.matmsdk.GpsTracker;
import com.matm.matmsdk.Service.JsonAssets;
import com.matm.matmsdk.Utils.SdkConstants;
import com.matm.matmsdk.transaction_report.TransactionStatusActivity;

import org.json.JSONException;
import org.json.JSONObject;


public class PosServiceActivity extends AppCompatActivity {

    private static final String TAG = PosActivity.class.getSimpleName();
    String latLong = "";
    Double latitude, longitude;
    private GpsTracker gpsTracker;
    private String dataObject;
    String brandName;
    String shopName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getLocation();

        if (getIntent() != null) {
            dataObject = getIntent().getStringExtra("data");
            try {
                JSONObject object=new JSONObject(dataObject);
                brandName=object.getString("brand_name");
                shopName=object.getString("shop_name");
            } catch (JSONException e) {
                e.printStackTrace();
            }
            Log.i("dataObject",dataObject);
            checkAppInstalledOrNot("com.linkmatm.service");
        }

    }

    public void getLocation() {
        gpsTracker = new GpsTracker(PosServiceActivity.this);
        if (gpsTracker.canGetLocation()) {
            latitude = gpsTracker.getLatitude();
            longitude = gpsTracker.getLongitude();

            latLong = latitude + "," + longitude;
//            Toast.makeText(this, "MatmGPS:" +latitude+ "," + longitude , Toast.LENGTH_LONG).show();

        } else {
            gpsTracker.showSettingsAlert();
        }
    }

    private boolean appInstalledOrNot(String uri) {
        PackageManager pm = getPackageManager();
        try {
            pm.getPackageInfo(uri, PackageManager.GET_ACTIVITIES);
            return true;
        } catch (PackageManager.NameNotFoundException e) {
        }
        return false;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode,Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (data != null && resultCode == RESULT_OK) {
            Log.i("ResponseData",data.toString());
            if (data.hasExtra("error1Response")) {
                Intent intent = new Intent(PosServiceActivity.this, ErrorActivity.class);
                intent.putExtra("errorResponse", data.getIntExtra("error1Response", 0));
                startActivity(intent);
                finish();
            } else if (data.hasExtra("errorResponse")) {
                Intent intent = new Intent(PosServiceActivity.this, Error2Activity.class);
                intent.putExtra("errorResponse", data.getStringExtra("errorResponse"));
                startActivity(intent);
                finish();
            } else {
                Intent intent = new Intent(PosServiceActivity.this, TransactionStatusActivity.class);
                intent.putExtra("flag", data.getStringExtra("flag"));
                intent.putExtra("TRANSACTION_ID", data.getStringExtra("TRANSACTION_ID"));
                intent.putExtra("TRANSACTION_TYPE", data.getStringExtra("TRANSACTION_TYPE"));
                intent.putExtra("TRANSACTION_AMOUNT", data.getStringExtra("TRANSACTION_AMOUNT"));
                intent.putExtra("RRN_NO", data.getStringExtra("RRN_NO"));
                intent.putExtra("RESPONSE_CODE", data.getStringExtra("RESPONSE_CODE"));
                intent.putExtra("APP_NAME", data.getStringExtra("APP_NAME"));
                intent.putExtra("AID", data.getStringExtra("AID"));
                intent.putExtra("AMOUNT", data.getStringExtra("AMOUNT"));
                intent.putExtra("MID", data.getStringExtra("MID"));
                intent.putExtra("TID", data.getStringExtra("TID"));
                intent.putExtra("TXN_ID", data.getStringExtra("TXN_ID"));
                intent.putExtra("INVOICE", data.getStringExtra("INVOICE"));
                intent.putExtra("CARD_TYPE", data.getStringExtra("CARD_TYPE"));
                intent.putExtra("APPR_CODE", data.getStringExtra("APPR_CODE"));
                intent.putExtra("CARD_NUMBER", data.getStringExtra("CARD_NUMBER"));
                intent.putExtra("CARD_HOLDERNAME", data.getStringExtra("CARD_HOLDERNAME"));
                intent.putExtra("brand_name",brandName);
                intent.putExtra("shop_name",shopName);
                intent.putExtra("status_code", data.getStringExtra("status_code"));
                String status_code=data.getStringExtra("status_code");
                Log.i("status_code",status_code);

//                intent.putExtra("BRAND_NAME", data.getStringExtra("userBrand"));
//                Log.i("status_code",status_code);
//                intent.putExtra("SHOP_NAME", data.getStringExtra("shopName"));
//                Log.i("SHOP_NAME",data.toString());


                startActivity(intent);
                finish();
            }
        } else {
            finish();
        }
    }

    public void showAlert(Context context) {
        try {
            AlertDialog.Builder alertbuilderupdate;
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                alertbuilderupdate = new AlertDialog.Builder(context, android.R.style.Theme_Material_Light_Dialog_Alert);
            } else {
                alertbuilderupdate = new AlertDialog.Builder(context);
            }
            alertbuilderupdate.setCancelable(false);
            String message = "Please download the IPPB MATM SERVICE APP.";
            alertbuilderupdate.setTitle("Alert")
                    .setMessage(message)
                    .setPositiveButton("Download Now", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            redirectToPlayStore();
                            finish();
                        }
                    })
                    .setNegativeButton("Not Now", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                            finish();
                        }
                    });
            AlertDialog alert11 = alertbuilderupdate.create();
            alert11.show();

        } catch (Exception e) {
        }
    }

    public void redirectToPlayStore() {
        Uri uri = Uri.parse("https://liveappstore.in/shareapp?com.linkmatm.service=");
        Intent goToMarket = new Intent(Intent.ACTION_VIEW, uri);
        goToMarket.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY |
                Intent.FLAG_ACTIVITY_NEW_DOCUMENT |
                Intent.FLAG_ACTIVITY_MULTIPLE_TASK);
        try {
            startActivity(goToMarket);
        } catch (ActivityNotFoundException e) {
            startActivity(new Intent(Intent.ACTION_VIEW,
                    Uri.parse("https://liveappstore.in/shareapp?com.linkmatm.service=")));
        }
    }



    private void sendDataToService(String packageName) {
        PackageManager manager = getPackageManager();

        Intent intent = manager.getLaunchIntentForPackage(packageName);
        intent.setFlags(0);
        intent.putExtra("dataToService",dataObject);
        intent.putExtra("latitude", latitude);
        intent.putExtra("longitude", longitude);
        intent.addCategory(Intent.CATEGORY_LAUNCHER);
        startActivityForResult(intent, REQUEST_CODE);

    }

    private void checkAppInstalledOrNot(String packageName) {
        boolean installed = appInstalledOrNot(packageName);
        try {
            if (installed) {
                sendDataToService(packageName);
            } else {
                /*It will show if the app is not install in your phone*/
                showAlert(PosServiceActivity.this);
            }
        } catch (Exception e) {

        }
    }
}
