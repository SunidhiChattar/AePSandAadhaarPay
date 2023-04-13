package com.pax.pax_sdk_app;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.hardware.usb.UsbDevice;
import android.hardware.usb.UsbManager;
import android.os.Build;
import android.os.Bundle;
import android.text.InputType;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.androidnetworking.AndroidNetworking;
import com.androidnetworking.common.Priority;
import com.androidnetworking.error.ANError;
import com.androidnetworking.interfaces.JSONObjectRequestListener;
import com.google.gson.Gson;
import com.matm.matmsdk.Bluetooth.BluetoothActivity;
import com.matm.matmsdk.MPOS.BluetoothServiceActivity;
import com.matm.matmsdk.MPOS.PosServiceActivity;
import com.matm.matmsdk.Utils.SdkConstants;
import com.matm.matmsdk.aepsmodule.AEPSHomeActivity;
import com.matm.matmsdk.aepsmodule.utils.aadharpay.AadharpayActivity;
import com.matm.matmsdk.aepsmodule.unifiedaeps.UnifiedBioAuthActivity;
import com.matm.matmsdk.callbacks.OnFinishListener;
import com.matm.matmsdk.matm1.MatmActivity;
import com.matm.matmsdk.notification.NotificationHelper;
import com.matm.matmsdk.notification.service.SubscribeGlobal;
import com.matm.matmsdk.transaction_report.TransactionStatusActivity;
import com.pax.pax_sdk_app.retrofitService.DataModel;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;

import static com.matm.matmsdk.Utils.SdkConstants.Matm1BluetoothFlag;
import static com.matm.matmsdk.Utils.SdkConstants.balanceEnquiry;

public class MainActivity extends AppCompatActivity implements OnFinishListener {
    private final static String TAG = MainActivity.class.getSimpleName();
    RadioGroup rgTransactionType;
    RadioButton rbCashWithdrawal;
    RadioButton rbBalanceEnquiry, rb_mini, rb_adhaarpay;
    EditText etAmount;
    Button btn_proceedaeps;
    String manufactureFlag = "";
    UsbManager musbManager;

    NotificationHelper notificationHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initView();
        //  FirebaseApp.initializeApp(MainActivity.this);
        notificationHelper = new NotificationHelper(this);
        subscribeGlobal();
        //retriveUserList();
        String str = "609384625620";
        String hashString = getSha256Hash(str);
        System.out.println("String Value :" + hashString);
        Date date = Calendar.getInstance().getTime();
        // Display a date in day, month, year format
        DateFormat formatter = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");
        String currentDateandTime = formatter.format(date);
        Log.e(TAG, "subscribeGlobal: current date " + currentDateandTime);
    }

    public void subscribeGlobal() {
        String specialChar = "!@#$%^&*";
        if (SdkConstants.USER_NAME_NOTIFY.matches(specialChar)) {
            SdkConstants.DEVICE_TOPIC = "";
        } else {
            SdkConstants.DEVICE_TOPIC = "AEPS_Snehasony";//+SdkConstants.loginID;
        }
        SdkConstants.COMPLETE_REGISTRATION = "registrationComplete";
        SdkConstants.PUSH_NOTIFICATION = "pushNotification";
        Log.d("TAG", "subscribeGlobal: " + SdkConstants.DEVICE_TOPIC);
        SubscribeGlobal global = new SubscribeGlobal(this);
        global.subscribe();
        global.registerBroadcast();
    }

    private void initView() {
        musbManager = (UsbManager) getSystemService(Context.USB_SERVICE);
        rgTransactionType = findViewById(R.id.rg_trans_type);
        rbCashWithdrawal = findViewById(R.id.rb_cw);
        rbBalanceEnquiry = findViewById(R.id.rb_be);

        rb_mini = findViewById(R.id.rb_mini);
        rb_adhaarpay = findViewById(R.id.rb_adhaarpay);
        etAmount = findViewById(R.id.et_amount);
        btn_proceedaeps = findViewById(R.id.proceedBtn);
        btn_proceedaeps.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                if(biometricDeviceConnect()){
                callAEPSSDKApp();
//                }else{
//                    Toast.makeText(MainActivity.this, "Connect your device.", Toast.LENGTH_SHORT).show();
//                }
            }
        });
        rgTransactionType.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                if (checkedId == R.id.rb_cw) {
                    etAmount.setClickable(true);
                    etAmount.setHint("Amount");
                    etAmount.setVisibility(View.VISIBLE);
                    etAmount.setText("");
                    etAmount.setEnabled(true);
                    etAmount.setInputType(InputType.TYPE_CLASS_NUMBER);
                } else if (checkedId == R.id.rb_be) {
                    etAmount.setVisibility(View.GONE);
                    etAmount.setClickable(false);
                    etAmount.setEnabled(false);
                } else if (checkedId == R.id.rb_adhaarpay) {
                    etAmount.setClickable(true);
                    etAmount.setHint("Amount");
                    etAmount.setVisibility(View.VISIBLE);
                    etAmount.setText("");
                    etAmount.setEnabled(true);
                    etAmount.setInputType(InputType.TYPE_CLASS_NUMBER);
                } else if (checkedId == R.id.rb_mini) {
                    etAmount.setVisibility(View.GONE);
                    etAmount.setClickable(false);
                    etAmount.setEnabled(false);
                }
            }
        });
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (data != null & resultCode == RESULT_OK) {
            /**
             *  FOR AEPS TRANSACTION RESPONSE
             */
            if (requestCode == SdkConstants.REQUEST_CODE) {
                String response = data.getStringExtra(SdkConstants.responseData);
                System.out.println("Response: " + response);
                //Toast.makeText(MainActivity.this,response,Toast.LENGTH_SHORT).show();
            }
            /**
             *  FOR MATM TRANSACTION RESPONSE
             */
            if (requestCode == SdkConstants.MATM_REQUEST_CODE) {
                String response = data.getStringExtra(SdkConstants.responseData);
                System.out.println("Response: " + response);
                //Toast.makeText(MainActivity.this,response,Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void callAEPSSDKApp() {
        SdkConstants.transactionAmount = etAmount.getText().toString().trim();
        if (rbCashWithdrawal.isChecked()) {
            SdkConstants.transactionType = SdkConstants.cashWithdrawal;
        } else if (rbBalanceEnquiry.isChecked()) {
            SdkConstants.transactionType = SdkConstants.balanceEnquiry;
        } else if (rb_mini.isChecked()) {
            SdkConstants.transactionType = SdkConstants.ministatement;
        } else if (rb_adhaarpay.isChecked()) {
            SdkConstants.transactionType = SdkConstants.adhaarPay;
        }
        SdkConstants.paramA = "test";
        SdkConstants.paramB = "BLS1";
        SdkConstants.paramC = "loanID";
        SdkConstants.applicationType = "CORE";
//        SdkConstants.tokenFromCoreApp = "eyJhbGciOiJSUzI1NiIsInR5cCI6IkpXVCJ9.eyJyZWRpcmVjdFVyaSI6Imh0dHA6Ly9sb2NhbGhvc3Q6NDU2OC8jL3YxL2Rhc2hib2FyZC9hbmFseXRpY3MiLCJhZG1pbk5hbWUiOiJpcHBidzQ1NyIsImJhbmtDb2RlIjoiaXBwYiIsInVzZXJfbmFtZSI6Ik1BTk9KQUFHRU5UIiwibW9iaWxlTnVtYmVyIjo4MDkzMDk4MDU2LCJjcmVhdGVkIjoxNjY2OTQ5NzEwMzQzLCJzY29wZSI6WyJyZWFkIiwid3JpdGUiXSwiZXhwIjoxNjY2OTUxNTEwLCJhdXRob3JpdGllcyI6WyJST0xFX1JFVEFJTEVSIl0sImp0aSI6IjlmYmJmNzUyLTQ0ZjktNGIzYS04M2E0LTQyOWE5MzkwMzhjNCIsImNsaWVudF9pZCI6ImlzdS1jbGllbnQifQ.XAiAAy1qQX_doa0rhQo9YL4IHqDi4BQzsS2YVjV0gxhJOnDEgIy90q0fx6WU8G8L6ZfkPkOse8D45ZmjnrtKt3F5rpF2plH59_GpTdQmuis9EffxU4yWtM6ltBLg4bGhfk90JIOHyrXB9hdzY83oY9Sm7IM8e9tbqXoqmXpDJ4RQAyCE9epAbtGv45srCBlplb1cWijNSQ44KwLnsRrzmJ0bhIHwncoI64Tcehm1N31_pfIVg2mNhRYZ_rrBKxecIV6gZU7vgg3RLEWSY7-pKUSLttznlFgunT34WVwNrUh2yCv5nVGBa1h9R_Vn8SgVXYaHNwVp1MeOm_3052IAxg";
        SdkConstants.userNameFromCoreApp = "";
//        SdkConstants.internalFPName = "wiseasy";
        SdkConstants.MANUFACTURE_FLAG = manufactureFlag;
        SdkConstants.DRIVER_ACTIVITY = "com.pax.pax_sdk_app.DriverActivity";
        SdkConstants.BRAND_NAME = "iServeU Technology";

        if (rb_adhaarpay.isChecked()) {
            Intent intent = new Intent(MainActivity.this, AadharpayActivity.class);
            startActivity(intent);
        } else {
            Intent intent = new Intent(MainActivity.this, UnifiedBioAuthActivity.class);
            startActivity(intent);
        }
    }

    @Override
    protected void onResume() {
        String str = SdkConstants.responseData;
//        Toast.makeText(getApplicationContext(),str,Toast.LENGTH_LONG).show();
        super.onResume();
    }

    public String getSha256Hash(String password) {
        try {
            MessageDigest digest = null;
            try {
                digest = MessageDigest.getInstance("SHA-256");
            } catch (NoSuchAlgorithmException e1) {
                e1.printStackTrace();
            }
            digest.reset();
            return bin2hex(digest.digest(password.getBytes()));
        } catch (Exception ignored) {
            return null;
        }
    }

    private String bin2hex(byte[] data) {
        StringBuilder hex = new StringBuilder(data.length * 2);
        for (byte b : data)
            hex.append(String.format("%02x", b & 0xFF));
        return hex.toString();
    }

    @Override
    public void onSDKFinish(String statusTxt, String paramA, String statusDesc) {
        Toast.makeText(MainActivity.this, statusTxt + paramA + statusDesc, Toast.LENGTH_SHORT).show();
        Log.e(TAG, "status .." + statusTxt);
        Log.e(TAG, "paramA.." + paramA);
        Log.e(TAG, "status desc.." + statusDesc);
    }
}