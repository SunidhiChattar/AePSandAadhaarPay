package com.matm.matmsdk.MPOS;

import static com.matm.matmsdk.Utils.SdkConstants.REQUEST_CODE;
import static com.matm.matmsdk.Utils.SdkConstants.devicename;
import static com.matm.matmsdk.Utils.SdkConstants.encryptedData;
import static com.matm.matmsdk.Utils.SdkConstants.loginID;
import static com.matm.matmsdk.Utils.SdkConstants.paramA;
import static com.matm.matmsdk.Utils.SdkConstants.paramB;
import static com.matm.matmsdk.Utils.SdkConstants.paramC;
import static com.matm.matmsdk.Utils.SdkConstants.tokenFromCoreApp;
import static com.matm.matmsdk.Utils.SdkConstants.transactionAmount;
import static com.matm.matmsdk.Utils.SdkConstants.transactionType;
import static com.matm.matmsdk.Utils.SdkConstants.userNameFromCoreApp;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;

import com.matm.matmsdk.Error.Error2Activity;
import com.matm.matmsdk.Error.ErrorActivity;
import com.matm.matmsdk.GpsTracker;
import com.matm.matmsdk.Utils.SdkConstants;
import com.matm.matmsdk.posverification.POSSettlementAccountActivity;
import com.matm.matmsdk.posverification.TransactionStatusPOSActivity;
import com.matm.matmsdk.posverification.response.CheckKycStatusRequestModel;
import com.matm.matmsdk.posverification.response.CheckKycStatusResponseModel;
import com.matm.matmsdk.posverification.retrofit.ApiService;
import com.matm.matmsdk.posverification.retrofit.RetrofitInstance;
import com.matm.matmsdk.transaction_report.TransactionStatusActivity;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MorefunServiceActivity extends AppCompatActivity {
    String latLong = "";
    Double latitude, longitude;
    private GpsTracker gpsTracker;
    ProgressDialog progressLoadingDialog;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        progressLoadingDialog = new ProgressDialog(MorefunServiceActivity.this);

        if(SdkConstants.transactionType.equalsIgnoreCase("POS")){
            checkKycStatus();
        }else{
            getLocation();
            if(SdkConstants.IS_BETA_USER){
                checkAppInstalledOrNot("com.linkmatm.service");
            }else {
                checkAppInstalledOrNot("com.matm.matmservice");
            }
        }

    }

    public void getLocation() {
        gpsTracker = new GpsTracker(MorefunServiceActivity.this);
        if (gpsTracker.canGetLocation()) {
            latitude = gpsTracker.getLatitude();
            longitude = gpsTracker.getLongitude();

            latLong = latitude + "," + longitude;

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
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (data != null && resultCode == RESULT_OK) {
            if (data.hasExtra("error1Response")) {
                Intent intent = new Intent(MorefunServiceActivity.this, ErrorActivity.class);
                intent.putExtra("errorResponse", data.getIntExtra("error1Response", 0));
                startActivity(intent);
                finish();
            } else if (data.hasExtra("errorResponse")) {
                Intent intent = new Intent(MorefunServiceActivity.this, Error2Activity.class);
                intent.putExtra("errorResponse", data.getStringExtra("errorResponse"));
                startActivity(intent);
                finish();
            } else {


                if(SdkConstants.transactionType.equalsIgnoreCase( "POS")) {

                    Intent intent = new Intent(MorefunServiceActivity.this, TransactionStatusPOSActivity.class);
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
                    intent.putExtra("APPR_CODE", data.getStringExtra("APPR_CODE"));
                    intent.putExtra("CARD_NUMBER", data.getStringExtra("CARD_NUMBER"));
                    intent.putExtra("bank_name", data.getStringExtra("bank_name"));
                    intent.putExtra("card_name", data.getStringExtra("card_name"));
                    intent.putExtra("merchant_name", data.getStringExtra("merchant_name"));
                    intent.putExtra("location", data.getStringExtra("location"));
                    intent.putExtra("date", data.getStringExtra("date"));
                    intent.putExtra("mid_tid", data.getStringExtra("mid_tid"));
                    intent.putExtra("batch", data.getStringExtra("batch"));
                    intent.putExtra("sale", data.getStringExtra("sale"));
                    intent.putExtra("card", data.getStringExtra("card"));
                    intent.putExtra("card_type", data.getStringExtra("card_type"));
                    intent.putExtra("rrn", data.getStringExtra("rrn"));
                    intent.putExtra("pin", data.getStringExtra("pin"));
                    intent.putExtra("application_version", data.getStringExtra("application_version"));
                    intent.putExtra("application_cryptogram", data.getStringExtra("application_cryptogram"));
                    intent.putExtra("txn_status", data.getStringExtra("txn_status"));
                    intent.putExtra("dedicated_file_name", data.getStringExtra("dedicated_file_name"));
                    intent.putExtra("terminal_result", data.getStringExtra("terminal_result"));
                    intent.putExtra("appl_preferred_name", data.getStringExtra("appl_preferred_name"));


                  /*  intent.putExtra("bank_name", data.getStringExtra("bmAction = nullank_name"));
                    intent.putExtra("merchant_name", data.getStringExtra("merchant_name"));
                    intent.putExtra("location", );
                    intent.putExtra("date", ResponseConstant.DE04);
                    intent.putExtra("mid_tid", ResponseConstant.DE5);
                    intent.putExtra("batch", ResponseConstant.DE6);
                    intent.putExtra("sale", ResponseConstant.DE07);
                    intent.putExtra("card", ResponseConstant.DE8);
                    intent.putExtra("card_type", ResponseConstant.DE9);
                    intent.putExtra("rrn", ResponseConstant.DE10);
                    intent.putExtra("card_name", cardHolderName);
                    intent.putExtra("pin", ResponseConstant.DE013);
                    intent.putExtra("application_version", applicationVersion);
                    intent.putExtra("application_cryptogram", applicationCryptogram);
                    intent.putExtra("txn_status", txnStatus);
                    intent.putExtra("dedicated_file_name", dedicatedFileName);
                    intent.putExtra("terminal_result", terminalVerResult);
                    intent.putExtra("appl_preferred_name", applPreferredName);
                    */

                    startActivity(intent);
                    finish();
                }else{
                    Intent intent = new Intent(MorefunServiceActivity.this, TransactionStatusActivity.class);

                    //Bundle b = data.getExtras();
                    //intent.putExtras(b);

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

                    startActivity(intent);
                    finish();
                }

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
            String message = "Please download the MATM SERVICE- 2 app from the playstore.";
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
        Uri uri = Uri.parse("https://play.google.com/store/apps/details?id=com.linkmatm.service&hl=en_US");
        Intent goToMarket = new Intent(Intent.ACTION_VIEW, uri);
        goToMarket.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY |
                Intent.FLAG_ACTIVITY_NEW_DOCUMENT |
                Intent.FLAG_ACTIVITY_MULTIPLE_TASK);
        try {
            startActivity(goToMarket);
        } catch (ActivityNotFoundException e) {
            startActivity(new Intent(Intent.ACTION_VIEW,
                    Uri.parse("https://play.google.com/store/apps/details?id=com.linkmatm.service&hl=en_US")));
        }
    }
    public void showMatmAlert(Context context) {
        try {

            AlertDialog.Builder alertbuilderupdate;
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                alertbuilderupdate = new AlertDialog.Builder(context, android.R.style.Theme_Material_Light_Dialog_Alert);
            } else {
                alertbuilderupdate = new AlertDialog.Builder(context);
            }
            alertbuilderupdate.setCancelable(false);
            String message = "Please download the MATM SERVICE-II app from the playstore.";
            alertbuilderupdate.setTitle("Alert")
                    .setMessage(message)
                    .setPositiveButton("Download Now", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            redirectToMatm2PlayStore();
                            finish();
                        }
                    })
                    .setNegativeButton("Not Now", (dialog, which) -> {
                        dialog.dismiss();
                        finish();
                    });
//                    .show();
            AlertDialog alert11 = alertbuilderupdate.create();
            alert11.show();

        } catch (Exception e) {

        }
    }
    public void redirectToMatm2PlayStore() {
        Uri uri = Uri.parse("https://play.google.com/store/apps/details?id=com.matm.matmservice&hl=en_US");
        Intent goToMarket = new Intent(Intent.ACTION_VIEW, uri);
        goToMarket.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY |
                Intent.FLAG_ACTIVITY_NEW_DOCUMENT |
                Intent.FLAG_ACTIVITY_MULTIPLE_TASK);
        try {
            startActivity(goToMarket);
        } catch (ActivityNotFoundException e) {
            startActivity(new Intent(Intent.ACTION_VIEW,
                    Uri.parse("https://play.google.com/store/apps/details?id=com.matm.matmservice&hl=en_US")));
        }
    }
    public void checkAppInstalledOrNot(String packageName){
        //boolean installed = appInstalledOrNot("com.matm.matmservice"); // service app added 25th Aug 2021
        boolean installed = appInstalledOrNot(packageName); // morefun service app added in matm service 1
        try {
            if (installed) {
                PackageManager manager = getPackageManager();
                Intent sIntent = manager.getLaunchIntentForPackage(packageName);
                sIntent.setFlags(0);
                sIntent.putExtra("ActivityName", "morefun");

                if (SdkConstants.applicationType.equals("CORE")) {
                    sIntent.putExtra("UserName", SdkConstants.userNameFromCoreApp);
                    sIntent.putExtra("UserToken", SdkConstants.tokenFromCoreApp);
                    sIntent.putExtra("ApplicationType", "CORE");
                } else {
                    sIntent.putExtra("LoginID", SdkConstants.loginID);
                    sIntent.putExtra("EncryptedData", SdkConstants.encryptedData);
                    sIntent.putExtra("ApplicationType", "");
                }

                sIntent.putExtra("ParamA", SdkConstants.paramA);
                sIntent.putExtra("ParamB", SdkConstants.paramB);
                sIntent.putExtra("ParamC", SdkConstants.paramC);
                sIntent.putExtra("latitude", latitude);
                sIntent.putExtra("longitude", longitude);

                sIntent.putExtra("Amount", SdkConstants.transactionAmount);
                sIntent.putExtra("TransactionType", SdkConstants.transactionType);
                sIntent.putExtra("deviceName",SdkConstants.DEVICE_NAME);
                sIntent.putExtra("MobileNo",SdkConstants.USER_MOBILE_NO);


                sIntent.addCategory(Intent.CATEGORY_LAUNCHER);
                startActivityForResult(sIntent, REQUEST_CODE);

            } else {
                /*It will show if the app is not install in your phone*/
                //showAlert(MorefunServiceActivity.this);
                if(packageName.equals("com.linkmatm.service")) {
                    showAlert(MorefunServiceActivity.this);
                }else {
                    showMatmAlert(MorefunServiceActivity.this);
                }
            }
        } catch (Exception e) {
        }

    }

    private void checkKycStatus() {
        progressLoadingDialog.setMessage("Please wait");
        progressLoadingDialog.show();
        CheckKycStatusRequestModel checkKycStatusRequestModel = new CheckKycStatusRequestModel(SdkConstants.userNameFromCoreApp);
        ApiService apiService = RetrofitInstance.checkKycStatus().create(ApiService.class);
        Call<CheckKycStatusResponseModel> checkKycStatusResponseModelCall = apiService.getCheckKycStatusResponse(checkKycStatusRequestModel);
        checkKycStatusResponseModelCall.enqueue(new Callback<CheckKycStatusResponseModel>() {
            @Override
            public void onResponse(@NonNull Call<CheckKycStatusResponseModel> call, @NonNull Response<CheckKycStatusResponseModel> response) {
                if (response.body() != null) {
                    progressLoadingDialog.dismiss();
                    String message = response.body().getMessage();
                    if (response.body().getStatus() == 1) {
                        Integer commonOnBoardingStatus = response.body().getData().getCommonOnboardingStatus().getStatus();
                        String onBoardingStatusDesc = response.body().getData().getCommonOnboardingStatus().getStatusdesc();
                        Boolean onboarding = response.body().getData().getCommonOnboardingStatus().getCommonOnboarding();

                        String posStatus = response.body().getData().getPosOnboardingStatus().getStatus();
                        Integer posSubStatus = response.body().getData().getPosOnboardingStatus().getSubStatus();
                        String posStatusDesc = response.body().getData().getPosOnboardingStatus().getStatusDesc();
                        int posSub_Status = response.body().getData().getPosOnboardingStatus().getSubStatus();

                        if(commonOnBoardingStatus==0){
                            if(posStatus.equalsIgnoreCase("SUCCESS") && posSub_Status == 0 ){
                                Intent intent = new Intent(MorefunServiceActivity.this, POSTransactionDetailsActivity.class);
                                startActivity(intent);
                                finish();
                            }else if (posStatus.equalsIgnoreCase("NA")){
                                Intent intent = new Intent(MorefunServiceActivity.this, POSSettlementAccountActivity.class);
                                startActivity(intent);
                                finish();
                            }else{
                                showOnBoardingFailAlert(MorefunServiceActivity.this,message);

                            }
                        }else{
                            showOnBoardingFailAlert(MorefunServiceActivity.this,"Please complete your onboarding process to continue the transaction.");
                        }

                    } else {
                        showOnBoardingFailAlert(MorefunServiceActivity.this,message);
                        progressLoadingDialog.dismiss();
                    }
                } else {
                    showOnBoardingFailAlert(MorefunServiceActivity.this,"Please try again after sometime !!");
                    progressLoadingDialog.dismiss();
                }
            }

            @Override
            public void onFailure(@NonNull Call<CheckKycStatusResponseModel> call, @NonNull Throwable t) {
        //        showOnBoardingFailAlert(MorefunServiceActivity.this,"Please try again after sometime !!");
                progressLoadingDialog.dismiss();
            }
        });
    }

    private void showOnBoardingFailAlert(Context context,String s) {

        try {

            AlertDialog.Builder alertbuilderupdate;
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                alertbuilderupdate = new AlertDialog.Builder(context, android.R.style.Theme_Material_Light_Dialog_Alert);
            } else {
                alertbuilderupdate = new AlertDialog.Builder(context);
            }
            alertbuilderupdate.setCancelable(false);
            alertbuilderupdate.setTitle("Alert")
                    .setMessage(s)
                    .setPositiveButton("OK", new DialogInterface.OnClickListener() {
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


}