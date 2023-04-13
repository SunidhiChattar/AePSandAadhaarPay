package com.matm.matmsdk;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.matm.matmsdk.MPOS.POSTransactionDetailsActivity;
import com.matm.matmsdk.Model.otpmodel.GetOtpServiceRequestModel;
import com.matm.matmsdk.Model.otpmodel.GetOtpServiceResponseModel;
import com.matm.matmsdk.Model.otpmodel.MessagedataEntity;
import com.matm.matmsdk.Utils.SdkConstants;
import com.matm.matmsdk.posverification.responsemodel.MerchantOnboardRequestModel;
import com.matm.matmsdk.posverification.responsemodel.MerchantOnboardResponseModel;
import com.matm.matmsdk.posverification.retrofit.ApiService;
import com.matm.matmsdk.posverification.retrofit.RetrofitInstance;

import java.util.Random;

import isu.otpView.otptextview.OtpTextView;
import isumatm.androidsdk.equitas.R;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class PosOtpActivity extends AppCompatActivity {

    ProgressDialog progressLoadingDialog;
    String saltStr;
    String str_otp;
    Button btnBankTransfer;
    private String userName;
    private String adminName;
    private String userMobileNo;
    private String userEmail="";
    private ImageView iv_arrow;
    private MessagedataEntity messagedataEntity;
    private String beni_bankCode = "";
    private String params = "";
    private String pincode = "";
    private String beni_ifsc_code = "";
    private String beni_accno = "";
    private String beni_bank = "";
    private String beni_name = "";
    private String accountType = "";
    private String username = "";
    private String _token;
    private OtpTextView otp;
    private TextView tv_otptimer, tv_resend;
    private String resendmsg;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.acitivity_pos_otp);
        progressLoadingDialog = new ProgressDialog(this);
        userName = SdkConstants.userNameFromCoreApp;
        adminName = SdkConstants.ADMIN_NAME;
        userMobileNo = SdkConstants.USER_MOBILE_NO;
        _token = SdkConstants.tokenFromCoreApp;
        otp = findViewById(R.id.layout_otp);
        iv_arrow = findViewById(R.id.iv_arrow);
        tv_otptimer = findViewById(R.id.tv_otptimer);
        tv_resend = findViewById(R.id.tv_resend);
        userEmail = SdkConstants.USER_EMAIL;
        messagedataEntity = new MessagedataEntity();
        messagedataEntity.setOtp("");
        btnBankTransfer = findViewById(R.id.btnBankTransfer);
        getTransactionID();

        btnBankTransfer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                initiateMerchantOnboardApicall();
            }
        });

        iv_arrow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
        new CountDownTimer(60000, 1000) {
            public void onTick(long millisUntilFinished) {
                tv_otptimer.setText("Remaining Time is : " + millisUntilFinished / 1000 + " Sec");
                tv_resend.setVisibility(View.GONE);
            }

            public void onFinish() {
                tv_otptimer.setText("OTP Expired");
                tv_resend.setVisibility(View.VISIBLE);
            }
        }.start();
        tv_resend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                otp.setOTP("");
                otpVerificationApiCall();
                new CountDownTimer(60000, 1000) {
                    public void onTick(long millisUntilFinished) {
                        tv_otptimer.setText("Remaining Time is : " + millisUntilFinished / 1000 + " Sec");
                        tv_resend.setVisibility(View.GONE);
                    }

                    public void onFinish() {
                        tv_otptimer.setText("OTP Expired");
                        tv_resend.setVisibility(View.VISIBLE);
                    }
                }.start();
            }
        });


    }

    public String getTransactionID() {

        String SALTCHARS = "ABCDEFGHIJKLMNOPQRSTUVWXYZ1234567890";
        String randomNumber = String.valueOf(8 + new Random().nextInt(SALTCHARS.length() - 8));
        StringBuilder salt = new StringBuilder();
        Random rnd = new Random();
        while (salt.length() < Integer.parseInt(randomNumber)) { // length of the random string.
            int index = (int) (rnd.nextFloat() * SALTCHARS.length());
            salt.append(SALTCHARS.charAt(index));
        }
        saltStr = salt.toString();
        return saltStr;

    }


    private void initiateMerchantOnboardApicall() {
        progressLoadingDialog.setMessage("Please wait");
        progressLoadingDialog.show();

        username = SdkConstants.userNameFromCoreApp;
        accountType = getIntent().getStringExtra("beneAccType");
        beni_name = getIntent().getStringExtra("beneName");
        beni_bank = getIntent().getStringExtra("beneBank");
        beni_accno = getIntent().getStringExtra("beneAccNo");
        beni_ifsc_code = getIntent().getStringExtra("beneIfscCode");
        beni_bankCode = getIntent().getStringExtra("beneBankCode");
        params = getIntent().getStringExtra("params");
        pincode = getIntent().getStringExtra("pincode");
        str_otp = otp.getOTP();

        MerchantOnboardRequestModel initiateMerchantRequestModel = new MerchantOnboardRequestModel(beni_name, beni_bankCode, beni_ifsc_code, beni_bank, beni_accno, username, accountType, str_otp, pincode, params);
        ApiService apiService = RetrofitInstance.initiateMerchantOnboardInstanceMaker().create(ApiService.class);
        Call<MerchantOnboardResponseModel> initiateMerchantResponseModelCall = apiService.getMerchantOnboardResponse(initiateMerchantRequestModel, _token);
        initiateMerchantResponseModelCall.enqueue(new Callback<MerchantOnboardResponseModel>() {
            @Override
            public void onResponse(@NonNull Call<MerchantOnboardResponseModel> call, @NonNull Response<MerchantOnboardResponseModel> response) {
                if (response.body() != null) {
                    progressLoadingDialog.dismiss();
                    int status = response.body().getStatus();
                    String message = response.body().getMessage();

                    if (status == 1) {

                        Intent intent = new Intent(PosOtpActivity.this, POSTransactionDetailsActivity.class);
                        startActivity(intent);
                        finish();

                    } else if (status == 0) {
                        AlertDialog.Builder builder = new AlertDialog.Builder(PosOtpActivity.this);
                        builder.setTitle("Alert!!");
                        builder.setMessage(message);
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

                    } else if (status == 2) {
                        showAlert(message);

                    } else {
                        showAlert(message);
                    }

                } else {
                    showAlert(response.body().getMessage());
                    progressLoadingDialog.dismiss();
                }
            }

            @Override
            public void onFailure(@NonNull Call<MerchantOnboardResponseModel> call, @NonNull Throwable t) {
                showAlert("Please try again after sometime !!");
                progressLoadingDialog.dismiss();
            }
        });
    }

    private void otpVerificationApiCall() {
        progressLoadingDialog.setMessage("Please wait");
        progressLoadingDialog.setCancelable(false);
        progressLoadingDialog.show();
        username = SdkConstants.userNameFromCoreApp;
        params = getIntent().getStringExtra("params");
        userEmail = SdkConstants.USER_EMAIL;
        GetOtpServiceRequestModel getOtpServiceRequestModel = new GetOtpServiceRequestModel(messagedataEntity,
                params, "", userEmail, userMobileNo, "OTP", "OTP", adminName, userName);
        getOtpServiceRequestModel.setMessagedata(messagedataEntity);
        ApiService apiService = RetrofitInstance.otpServiceInstanceMaker().create(ApiService.class);
        Call<GetOtpServiceResponseModel> otpServiceResponseModelCall = apiService.getResendOtpServiceResponse(getOtpServiceRequestModel);
        otpServiceResponseModelCall.enqueue(new Callback<GetOtpServiceResponseModel>() {
            @Override
            public void onResponse(@NonNull Call<GetOtpServiceResponseModel> call, @NonNull Response<GetOtpServiceResponseModel> response) {
                if (response.body() != null) {
                    progressLoadingDialog.dismiss();
                    if (response.body().getStatus() == 1) {
                        resendmsg = response.body().getMessage();
                        Toast.makeText(PosOtpActivity.this, "OTP resent Successfully", Toast.LENGTH_SHORT).show();
                        progressLoadingDialog.dismiss();
                    } else {
                        progressLoadingDialog.dismiss();
                        showAlert(resendmsg);
                    }
                } else {
                    showAlert("Please try again after sometime !!");
                    progressLoadingDialog.dismiss();
                }
            }

            @Override
            public void onFailure(@NonNull Call<GetOtpServiceResponseModel> call, @NonNull Throwable t) {
                progressLoadingDialog.dismiss();
                showAlert("Please try again after sometime !!");
            }
        });
    }

    public void showAlert(String msg) {

        AlertDialog.Builder builder = new AlertDialog.Builder(PosOtpActivity.this);
        builder.setTitle("Alert!!");
        builder.setMessage(msg);
        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
                finish();
            }
        });
        AlertDialog dialog = builder.create();
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setCancelable(false);
        dialog.show();
    }


}
