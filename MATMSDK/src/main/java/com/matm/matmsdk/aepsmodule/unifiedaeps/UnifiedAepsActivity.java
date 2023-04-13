package com.matm.matmsdk.aepsmodule.unifiedaeps;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatCheckBox;
import androidx.appcompat.widget.SwitchCompat;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;
import androidx.core.content.ContextCompat;

import android.annotation.TargetApi;
import android.app.Activity;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.graphics.drawable.ColorDrawable;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.text.method.PasswordTransformationMethod;
import android.util.Log;
import android.util.TypedValue;
import android.view.KeyEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.matm.matmsdk.CustomThemes;
import com.matm.matmsdk.GpsTracker;
import com.matm.matmsdk.Utils.SdkConstants;
import com.matm.matmsdk.aepsmodule.alertShow.SingletonClass;
import com.matm.matmsdk.aepsmodule.bankspinner.BankNameListActivity;
import com.matm.matmsdk.aepsmodule.bankspinner.BankNameModel;
import com.matm.matmsdk.aepsmodule.transactionstatus.TransactionStatusModel;
import com.matm.matmsdk.aepsmodule.utils.Session;
import com.matm.matmsdk.aepsmodule.utils.Util;
import com.matm.matmsdk.callbacks.OnDriverDataListener;
import com.matm.matmsdk.notification.NotificationHelper;
import com.moos.library.HorizontalProgressView;

import org.json.JSONException;
import org.json.JSONObject;

import isumatm.androidsdk.equitas.R;

public class UnifiedAepsActivity extends AppCompatActivity implements
        UnifiedAepsContract.View, OnDriverDataListener {
    final static String MARKER = "|"; // filtered in layout not to be in the string
    Boolean adharbool = true;
    Boolean virtualbool = false;
    AppCompatCheckBox terms;
    Session session;
    UnifiedAepsRequestModel unifiedAepsRequestModel;
    String bankIINNumber = "";
    ProgressDialog loadingView;
    String flagNameRdService = "";
    Class driverActivity;
    String balanaceInqueryAadharNo = "";
    Boolean flagFromDriver = false;
    String vid = "", uid = "";
    TextView virtualidText, aadharText;
    boolean mInside = false;
    boolean mWannaDeleteHyphen = false;
    boolean mKeyListenerSet = false;
    String latLong = "";
    String fmDeviceId = "Startek Eng-Inc.";
    String fmDeviceId2 = "Startek Eng-Inc.\u0000";
    String fmDeviceId3 = "Startek Eng. Inc.";
    String fmDeviceId4 = "Startek";
    SharedPreferences settings;
    SharedPreferences.Editor editor;
    TypedValue typedValue;
    Resources.Theme theme;
    LocationManager locationManager;
    private EditText aadharNumber, aadharVirtualID;
    private TextView balanceEnquiryExpandButton, cashWithdrawalButton, fingerprintStrengthDeposit, depositNote;
    private EditText mobileNumber, bankspinner, amountEnter;
    private ImageView fingerprint, virtualID, aadhaar;
    private HorizontalProgressView depositBar;
    private Button submitButton;
    private GpsTracker gpsTracker;
    private int gatewayPriority = 0;
    //notification 22 july
    private NotificationHelper notificationHelper;
    private UnifiedAepsPresenter unifiedAepsPresenter;
    private String aadharNumberMain = "";
    Double latitude, longitude;
    // to save latlong
    String lastLatlong;
    public static final String SHARED_PREFS = "LastLatlong";
    private String dataObj = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        /*
         * For CustomThemes
         * @Author - RashmiRanjan
         * */
        new CustomThemes(this);
        // setTheme(R.style.MediumSlateBlue);
        if (SdkConstants.dashboardLayout != 0) {
            setContentView(SdkConstants.dashboardLayout);
        } else {
            setContentView(R.layout.activity_unified_aeps);
        }
        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        session = new Session(UnifiedAepsActivity.this);

        if (getIntent() != null) {
            if (getIntent().hasExtra("data")) {
                dataObj = getIntent().getStringExtra("data");
                try {
                    JSONObject object = new JSONObject(dataObj);
                    if (object.has("applicationType")) {
                        SdkConstants.applicationType = object.getString("applicationType");
                    }
                    if (object.has("token")) {
                        SdkConstants.tokenFromCoreApp = object.getString("token");
                    }
                    if (object.has("userName")) {
                        SdkConstants.userNameFromCoreApp = object.getString("userName");
                    }
                    if (object.has("API_USER_NAME_VALUE")) {
                        SdkConstants.API_USER_NAME_VALUE = object.getString("API_USER_NAME_VALUE");
                    }
                    if (object.has("driver_activity")) {
                        SdkConstants.DRIVER_ACTIVITY = object.getString("driver_activity");
                    }
                    if (object.has("manufacture_flag")) {
                        SdkConstants.MANUFACTURE_FLAG = object.getString("manufacture_flag");
                    }
                    if (object.has("internalFpName")) {
                        SdkConstants.internalFPName = object.getString("internalFpName");
                    }
                    if (object.has("transactionType")) {
                        SdkConstants.transactionType = object.getString("transactionType");
                    }
                    if (object.has("transactionAmount")) {
                        SdkConstants.transactionAmount = object.getString("transactionAmount");
                    }
                    if (object.has("paramA")) {
                        SdkConstants.paramA = object.getString("paramA");
                    }
                    if (object.has("paramB")) {
                        SdkConstants.paramB = object.getString("paramB");
                    }
                    if (object.has("paramC")) {
                        SdkConstants.paramC = object.getString("paramC");
                    }
                    if (object.has("brand_name")) {
                        SdkConstants.BRAND_NAME = object.getString("brand_name");
                    }
                    if (object.has("shop_name")) {
                        SdkConstants.SHOP_NAME = object.getString("shop_name");
                    }
                    if (object.has("applicationUserName")) {
                        SdkConstants.applicationUserName = object.getString("applicationUserName");
                    }
                    if (object.has("refeshUI")) {
                        SdkConstants.refeshUI = Boolean.valueOf(object.getString("refeshUI"));
                    }
                    if (object.has("bioauth")) {
                        SdkConstants.bioauth = Boolean.valueOf(object.getString("bioauth"));
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }

        getRDServiceClass();
        typedValue = new TypedValue();
        theme = this.getTheme();
        try {
            if (ContextCompat.checkSelfPermission(getApplicationContext(), android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this, new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION}, 101);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }


        disableAutofill();

//        flagFromDriver = true;
        getLocation();
        //notification 22 july
//        notificationHelper = new NotificationHelper(this);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
        fingerprintStrengthDeposit = findViewById(R.id.fingerprintStrengthDeposit);
        depositNote = findViewById(R.id.depositNote);
        depositNote.setVisibility(View.GONE);
        fingerprintStrengthDeposit.setVisibility(View.GONE);
        aadharVirtualID = (EditText) findViewById(R.id.aadharVirtualID);
        virtualID = findViewById(R.id.virtualID);
        aadhaar = findViewById(R.id.aadhaar);
        virtualidText = findViewById(R.id.virtualidText);
        aadharText = findViewById(R.id.aadharText);
        aadharNumber = findViewById(R.id.aadharNumber);
        mobileNumber = findViewById(R.id.mobileNumber);
        bankspinner = findViewById(R.id.bankspinner);
        amountEnter = findViewById(R.id.amountEnter);
        fingerprint = findViewById(R.id.fingerprint);
        fingerprint.setEnabled(false);
        fingerprint.setClickable(false);
        submitButton = findViewById(R.id.submitButton);
        depositBar = findViewById(R.id.depositBar);
        depositBar.setVisibility(View.GONE);
        depositNote.setVisibility(View.GONE);
        fingerprintStrengthDeposit.setVisibility(View.GONE);
        fingerprintStrengthDeposit.setVisibility(View.GONE);
        terms = findViewById(R.id.terms);
        cashWithdrawalButton = findViewById(R.id.cashWithdrawalButton);
        balanceEnquiryExpandButton = findViewById(R.id.balanceEnquiryExpandButton);
        if (SdkConstants.applicationType.equalsIgnoreCase("CORE")) {
            session.setUserToken(SdkConstants.tokenFromCoreApp);
            session.setUsername(SdkConstants.userNameFromCoreApp);
            SdkConstants.isSL = false;
        } else {
            SdkConstants.isSL = true;
            session.setUserToken(SdkConstants.tokenFromCoreApp);
            session.setUsername(SdkConstants.userNameFromCoreApp);
            terms.setVisibility(View.GONE);
        }
        if (SdkConstants.transactionType.equalsIgnoreCase(SdkConstants.balanceEnquiry)) {
            balanceEnquiryExpandButton.setVisibility(View.VISIBLE);
            cashWithdrawalButton.setVisibility(View.GONE);
            amountEnter.setVisibility(View.GONE);
        } else if (SdkConstants.transactionType.equalsIgnoreCase(SdkConstants.ministatement)) {
            balanceEnquiryExpandButton.setVisibility(View.VISIBLE);
            balanceEnquiryExpandButton.setText(getResources().getString(R.string.mini_statement));
            cashWithdrawalButton.setVisibility(View.GONE);
            amountEnter.setVisibility(View.GONE);
        } else if (SdkConstants.transactionType.equalsIgnoreCase(SdkConstants.cashWithdrawal)) {
            balanceEnquiryExpandButton.setVisibility(View.GONE);
            cashWithdrawalButton.setVisibility(View.VISIBLE);
            amountEnter.setText(SdkConstants.transactionAmount);
        }
        amountEnter.setEnabled(SdkConstants.editable);
        virtualID.setBackgroundResource(R.drawable.ic_language);
        virtualidText.setTextColor(getResources().getColor(R.color.grey));
        aadhaar.setBackgroundResource(R.drawable.ic_fingerprint_blue);
        bankspinner.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                showLoader();
                mobileNumber.clearFocus();
                Intent in = new Intent(UnifiedAepsActivity.this, BankNameListActivity.class);
                if (SdkConstants.transactionType.equalsIgnoreCase(SdkConstants.balanceEnquiry)) {
                    startActivityForResult(in, SdkConstants.REQUEST_FOR_ACTIVITY_BALANCE_ENQUIRY_CODE);
                } else if (SdkConstants.transactionType.equalsIgnoreCase(SdkConstants.cashWithdrawal)) {
                    startActivityForResult(in, SdkConstants.REQUEST_FOR_ACTIVITY_CASH_WITHDRAWAL_CODE);
                } else if (SdkConstants.transactionType.equalsIgnoreCase(SdkConstants.ministatement)) {
                    startActivityForResult(in, SdkConstants.REQUEST_FOR_ACTIVITY_BALANCE_ENQUIRY_CODE);
                }

            }
        });
        fingerprint.setOnClickListener(new View.OnClickListener() {
            @TargetApi(Build.VERSION_CODES.LOLLIPOP)
            @Override
            public void onClick(View v) {
                try {
                    showLoader();

                    fingerprint.setEnabled(false);
                    fingerprint.setColorFilter(R.color.colorGrey);
                    flagFromDriver = true;


                 if (SdkConstants.MANUFACTURE_FLAG.equalsIgnoreCase(fmDeviceId) || SdkConstants.MANUFACTURE_FLAG.contains(fmDeviceId4) || SdkConstants.MANUFACTURE_FLAG.equalsIgnoreCase(fmDeviceId2) || SdkConstants.MANUFACTURE_FLAG.equalsIgnoreCase(fmDeviceId3)) {
                     SdkConstants.aadharNumberValue = aadharNumber.getText().toString();
                     Log.i("aadharNumberValue",SdkConstants.aadharNumberValue );
                     SdkConstants.mobileNumberValue = mobileNumber.getText().toString();
                     SdkConstants.bankValue = bankspinner.getText().toString();
                     bankIINNumber = SdkConstants.bankIIN;
                     SdkConstants.bankIIN = bankIINNumber;

                     if(mobileNumber.getText().toString().isEmpty() || bankspinner.getText().toString().isEmpty()){
                         hideLoader();
                         Toast.makeText(UnifiedAepsActivity.this, "Fill up details", Toast.LENGTH_SHORT).show();
                     }else{
                         fingerprint.setEnabled(false);
                         aadharNumber.setEnabled(false);
                         aadharNumber.setTextColor(getResources().getColor(R.color.grey));
                         mobileNumber.setEnabled(false);
                         mobileNumber.clearFocus();
                         bankspinner.setEnabled(false);
                         SdkConstants.OnBackpressedValue = false;
                         fingerprint.setColorFilter(R.color.colorGrey);
                         if(SdkConstants.onDriverDataListener != null){
                             SdkConstants.onDriverDataListener.onFingerClick(balanaceInqueryAadharNo,
                                     mobileNumber.getText().toString(),
                                     bankspinner.getText().toString(),
                                     flagNameRdService
                                     ,session.getFreshnessFactor(),UnifiedAepsActivity.this);
                             hideLoader();
                             finish();
                         }
                     }
                 }else{
                     Intent launchIntent = new Intent(UnifiedAepsActivity.this, driverActivity);
                     launchIntent.putExtra("driverFlag", flagNameRdService);
                     launchIntent.putExtra("freshnesFactor", session.getFreshnessFactor());
                     launchIntent.putExtra("AadharNo", balanaceInqueryAadharNo);
                     startActivityForResult(launchIntent, 1);
                 }








                    /*Intent launchIntent = new Intent(UnifiedAepsActivity.this, driverActivity);
                    launchIntent.putExtra("driverFlag", flagNameRdService);
                    launchIntent.putExtra("freshnesFactor", session.getFreshnessFactor());
                    launchIntent.putExtra("AadharNo", balanaceInqueryAadharNo);
                    startActivityForResult(launchIntent, 1);*/
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
        aadharNumber.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                if (!mKeyListenerSet) {
                    aadharNumber.setOnKeyListener(new View.OnKeyListener() {
                        @Override
                        public boolean onKey(View v, int keyCode, KeyEvent event) {
                            try {
                                mWannaDeleteHyphen = (keyCode == KeyEvent.KEYCODE_DEL
                                        && aadharNumber.getSelectionEnd() - aadharNumber.getSelectionStart() <= 1
                                        && aadharNumber.getSelectionStart() > 0
                                        && aadharNumber.getText().toString().charAt(aadharNumber.getSelectionEnd() - 1) == '-');
                            } catch (IndexOutOfBoundsException e) {
                                // never to happen because of checks
                            }
                            return false;
                        }
                    });
                    mKeyListenerSet = true;
                }
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (mInside) // to avoid recursive calls
                    return;
                mInside = true;
                int currentPos = aadharNumber.getSelectionStart();
                String string = aadharNumber.getText().toString().toUpperCase();
                String newString = makePrettyString(string);
                if (count == 14) {
                    fingerprint.setEnabled(true);
                    fingerprint.setClickable(true);
                    //fingerprint.setImageDrawable(getResources().getDrawable(R.drawable.ic_scanner));
                    theme.resolveAttribute(R.attr.colorPrimary, typedValue, true);
                    int color = typedValue.data;
                    fingerprint.setColorFilter(color);
                } else {
                }
                aadharNumber.setText(newString);
                try {
                    aadharNumber.setSelection(getCursorPos(string, newString, currentPos, mWannaDeleteHyphen));
                } catch (IndexOutOfBoundsException e) {
                    aadharNumber.setSelection(aadharNumber.length()); // last resort never to happen
                }
                mWannaDeleteHyphen = false;
                mInside = false;
            }

            @Override
            public void afterTextChanged(Editable s) {
                /*if (s.length() < 1) {
                    aadharNumber.setError(getResources().getString(R.string.aadhaarnumber));
                }*/
                if (s.length() > 0) {
                    aadharNumber.setError(null);
                    String aadharNo = aadharNumber.getText().toString();
                    if (aadharNo.contains("-")) {
                        aadharNo = aadharNo.replaceAll("-", "").trim();
                        balanaceInqueryAadharNo = aadharNo;
                        if (balanaceInqueryAadharNo.length() >= 12) {

                            if (Util.validateAadharNumber(aadharNo) == false) {
                                aadharNumber.setError(getResources().getString(R.string.Validaadhaarerror));
                            } else {
                                fingerprint.setEnabled(true);
                                fingerprint.setClickable(true);
                                //fingerprint.setImageDrawable(getResources().getDrawable(R.drawable.ic_scanner));
                                theme.resolveAttribute(R.attr.colorPrimary, typedValue, true);
                                int color = typedValue.data;
                                fingerprint.setColorFilter(color);
                                aadharNumber.clearFocus();
                                mobileNumber.requestFocus();
                            }

                        }

                    }

                }
            }
        });
        aadharNumber.setTransformationMethod(new ChangeTransformationMethod());

        aadharVirtualID.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                if (!mKeyListenerSet) {
                    aadharVirtualID.setOnKeyListener(new View.OnKeyListener() {
                        @Override
                        public boolean onKey(View v, int keyCode, KeyEvent event) {
                            try {
                                mWannaDeleteHyphen = (keyCode == KeyEvent.KEYCODE_DEL
                                        && aadharVirtualID.getSelectionEnd() - aadharVirtualID.getSelectionStart() <= 1
                                        && aadharVirtualID.getSelectionStart() > 0
                                        && aadharVirtualID.getText().toString().charAt(aadharVirtualID.getSelectionEnd() - 1) == '-');
                            } catch (IndexOutOfBoundsException e) {
                                // never to happen because of checks
                            }
                            return false;
                        }
                    });
                    mKeyListenerSet = true;
                }
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (mInside) // to avoid recursive calls
                    return;
                mInside = true;
                int currentPos = aadharVirtualID.getSelectionStart();
                String string = aadharVirtualID.getText().toString().toUpperCase();
                String newString = makePrettyString(string);
                if (count == 19) {
                    fingerprint.setEnabled(true);
                    fingerprint.setClickable(true);
                    //fingerprint.setImageDrawable(getResources().getDrawable(R.drawable.ic_scanner));
                    theme.resolveAttribute(R.attr.colorPrimary, typedValue, true);
                    int color = typedValue.data;
                    fingerprint.setColorFilter(color);
                }
                aadharVirtualID.setText(newString);
                try {
                    aadharVirtualID.setSelection(getCursorPos(string, newString, currentPos, mWannaDeleteHyphen));
                } catch (IndexOutOfBoundsException e) {
                    aadharVirtualID.setSelection(aadharVirtualID.length()); // last resort never to happen
                }
                mWannaDeleteHyphen = false;
                mInside = false;
            }

            @Override
            public void afterTextChanged(Editable s) {
                if (s.length() < 1) {
                    aadharVirtualID.setError(getResources().getString(R.string.aadhaarVID));
                }

                if (s.length() > 0) {
                    aadharVirtualID.setError(null);
                    String aadharNo = aadharVirtualID.getText().toString();
                    if (aadharNo.contains("-")) {
                        aadharNo = aadharNo.replaceAll("-", "").trim();
                        balanaceInqueryAadharNo = aadharNo;
                        if (balanaceInqueryAadharNo.length() >= 16) {
                            if (Util.validateAadharVID(aadharNo) == false) {
                                aadharVirtualID.setError(getResources().getString(R.string.valid_aadhar__uid_error));
                            } else {
                                fingerprint.setEnabled(true);
                                fingerprint.setClickable(true);
                                theme.resolveAttribute(R.attr.colorPrimary, typedValue, true);
                                int color = typedValue.data;
                                fingerprint.setColorFilter(color);
                                aadharVirtualID.clearFocus();
                                mobileNumber.requestFocus();
                            }
                        }
                    }

                }
            }

        });
        bankspinner.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                if (s.length() < 1) {
                    bankspinner.setError(getResources().getString(R.string.select_bank_error));
                    mobileNumber.clearFocus();
                }
                if (s.length() > 0) {
                    bankspinner.setError(null);
                }
            }
        });
        amountEnter.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                if (s.length() < 1) {
                    amountEnter.setError(getResources().getString(R.string.amount_error));
                }
                if (s.length() > 0) {
                    amountEnter.setError(null);
                }
            }
        });
        mobileNumber.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                String number = "";
                if (s.length() > 0 && s.length() < 9) {
                    mobileNumber.setError(null);
                    number = s.toString();
                    if (number.startsWith("0")) {
                        mobileNumber.setError(getResources().getString(R.string.mobilevaliderror));
                    }
                }
                if (s.length() == 10) {
                    if (Util.isValidMobile(mobileNumber.getText().toString().trim()) == false) {
                        if (number.startsWith("0")) {
                            mobileNumber.setError(getResources().getString(R.string.mobilevaliderror));
                        } else {
                            mobileNumber.setError(getResources().getString(R.string.mobilevaliderror));

                        }
                    }
                }
            }
        });
        virtualID.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                /* Will implement when VID will used*/
                aadharNumber.setVisibility(View.GONE);
                aadharVirtualID.setVisibility(View.VISIBLE);
                virtualID.setEnabled(false);
                aadhaar.setEnabled(true);
                virtualbool = true;
                adharbool = false;
                virtualID.setBackgroundResource(R.drawable.ic_language_blue);
                //virtualidText.setTextColor(getResources().getColor(R.color.colorPrimary));
                theme.resolveAttribute(R.attr.colorPrimary, typedValue, true);
                int color = typedValue.data;
                virtualidText.setTextColor(color);
                aadhaar.setBackground(getResources().getDrawable(R.drawable.ic_fingerprint_grey));
                aadharText.setTextColor(getResources().getColor(R.color.grey));
            }
        });
        aadhaar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                aadharNumber.setVisibility(View.VISIBLE);
                aadharVirtualID.setVisibility(View.GONE);
                virtualID.setEnabled(true);
                aadhaar.setEnabled(false);
                virtualID.setBackgroundResource(R.drawable.ic_language);
                virtualidText.setTextColor(getResources().getColor(R.color.grey));
                adharbool = true;
                virtualbool = false;
                aadhaar.setBackgroundResource(R.drawable.ic_fingerprint_blue);
                theme.resolveAttribute(R.attr.colorPrimary, typedValue, true);
                int color = typedValue.data;
                aadharText.setTextColor(color);
                //  aadharText.setTextColor(getResources().getColor(R.color.colorPrimary));
            }
        });
        terms.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                SdkConstants.firstCheck = true;
                showTermsDetails(UnifiedAepsActivity.this);
            }
        });
        submitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                //showLoader();
                String balanceaadharNo = "";
                String balanceaadharVid = "";
                balanceaadharNo = aadharNumber.getText().toString();
                if (adharbool == true) {
                    if (balanceaadharNo.contains("-")) {
                        balanceaadharNo = balanceaadharNo.replaceAll("-", "").trim();
                    }
                   /* if (balanceaadharNo == null || balanceaadharNo.matches("")) {
                        aadharNumber.setError(getResources().getString(R.string.valid_aadhar_error));
                        return;
                    }
                    if (Util.validateAadharNumber(balanceaadharNo) == false) {
                        aadharNumber.setError(getResources().getString(R.string.valid_aadhar_error));
                        return;
                    }
                    if (aadharNumber.getText().toString().length() < 14) {
                        aadharNumber.setError("Enter valid aadhaar no.");
                        return;
                    }*/
                } else if (virtualbool == true) {
                    balanceaadharVid = aadharVirtualID.getText().toString().trim();
                    if (balanceaadharVid.contains("-")) {
                        balanceaadharVid = balanceaadharVid.replaceAll("-", "").trim();
                    }
                    if (balanceaadharVid == null || balanceaadharVid.matches("")) {
                        aadharVirtualID.setError(getResources().getString(R.string.valid_vid_error));
                        return;
                    }
                    if (Util.validateAadharNumber(balanceaadharVid) == false) {
                        aadharVirtualID.setError(getResources().getString(R.string.valid_aadhar_error));
                        return;
                    }
                }
                if (!flagFromDriver) {
                    Toast.makeText(UnifiedAepsActivity.this, "Please do Biometric Verification", Toast.LENGTH_LONG).show();
                    return;
                } else {
                    try {
                        JSONObject respObj = new JSONObject(SdkConstants.RECEIVE_DRIVER_DATA);
                        String scoreStr = respObj.getString("pidata_qscore");
                        String[] scoreList = scoreStr.split(",");
                        scoreStr = scoreList[0];
                        if (Float.parseFloat(scoreStr) <= 40) {
                            showAlert("Bad Fingerprint Strength, Please try Again !");
                            return;
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
                if (mobileNumber.getText() == null || mobileNumber.getText().toString().trim().matches("") || Util.isValidMobile(mobileNumber.getText().toString().trim()) == false) {
                    mobileNumber.setError(getResources().getString(R.string.mobileerror));
                    return;
                }
                SdkConstants.AADHAAR_NUMBER = aadharNumber.getText().toString().trim();
                aadharNumberMain = SdkConstants.AADHAAR_NUMBER;
                if (aadharNumberMain== null || Util.validateAadharNumber(balanaceInqueryAadharNo) == false) {
                    aadharNumber.setError(getResources().getString(R.string.Validaadhaarerror));
                    return;
                }
                SdkConstants.MOBILENUMBER = mobileNumber.getText().toString().trim();
                String panaaadhaar = mobileNumber.getText().toString().trim();

                if (!panaaadhaar.contains(" ") && panaaadhaar.length() == 10) {
                } else {
                    mobileNumber.setError(getResources().getString(R.string.mobileerror));
                    return;
                }
                if (bankspinner.getText() == null || bankspinner.getText().toString().trim().matches("")) {
                    bankspinner.setError(getResources().getString(R.string.select_bank_error));
                    return;
                }

                SdkConstants.BANK_NAME = bankspinner.getText().toString().trim();
                String userType;
                if (!SdkConstants.applicationType.equalsIgnoreCase("CORE")){
                    userType = "APIUSER";
                }else{
                    userType = "ANDROIDUSER";
                }
                if (SdkConstants.firstCheck) {
                        if (SdkConstants.transactionType.equalsIgnoreCase(SdkConstants.balanceEnquiry)) {
                            showLoader();
                            try {
                                JSONObject respObj = new JSONObject(SdkConstants.RECEIVE_DRIVER_DATA);
                                String pidData = respObj.getString("base64pidData");
                                unifiedAepsRequestModel = new UnifiedAepsRequestModel("",balanaceInqueryAadharNo,
                                        bankIINNumber, mobileNumber.getText().toString().trim(), userType, bankspinner.getText().toString(), pidData, lastLatlong, SdkConstants.paramA, SdkConstants.paramB, SdkConstants.paramC, SdkConstants.API_USER_NAME_VALUE,session.getUserName());
                                unifiedAepsPresenter = new UnifiedAepsPresenter(UnifiedAepsActivity.this);
                                unifiedAepsPresenter.performUnifiedResponse(UnifiedAepsActivity.this, session.getUserToken(), unifiedAepsRequestModel, balanceEnquiryExpandButton.getText().toString(), gatewayPriority);
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        } else if (SdkConstants.transactionType.equalsIgnoreCase(SdkConstants.ministatement)) {
                            showLoader();
                            try {
                                JSONObject respObj = new JSONObject(SdkConstants.RECEIVE_DRIVER_DATA);
                                String pidData = respObj.getString("base64pidData");
                                unifiedAepsRequestModel = new UnifiedAepsRequestModel("", balanaceInqueryAadharNo,
                                        bankIINNumber, mobileNumber.getText().toString().trim(), userType, bankspinner.getText().toString(), pidData.toString().trim(), lastLatlong, SdkConstants.paramA, SdkConstants.paramB, SdkConstants.paramC, SdkConstants.API_USER_NAME_VALUE, session.getUserName());
                                unifiedAepsPresenter = new UnifiedAepsPresenter(UnifiedAepsActivity.this);
                                unifiedAepsPresenter.performUnifiedResponse(UnifiedAepsActivity.this, session.getUserToken(), unifiedAepsRequestModel, balanceEnquiryExpandButton.getText().toString(), gatewayPriority);
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        } else {
                            showLoader();
                            if (amountEnter.getText() == null || amountEnter.getText().toString().trim().matches("")) {
                                amountEnter.setError(getResources().getString(R.string.amount_error));
                                return;
                            }
                            try {
                                JSONObject respObj = new JSONObject(SdkConstants.RECEIVE_DRIVER_DATA);
                                String pidData = respObj.getString("base64pidData");
                                unifiedAepsRequestModel = new UnifiedAepsRequestModel(amountEnter.getText().toString().trim(), balanaceInqueryAadharNo,
                                        bankIINNumber, mobileNumber.getText().toString().trim(), userType, bankspinner.getText().toString(), pidData, lastLatlong, SdkConstants.paramA, SdkConstants.paramB, SdkConstants.paramC, SdkConstants.API_USER_NAME_VALUE, session.getUserName());
                                unifiedAepsPresenter = new UnifiedAepsPresenter(UnifiedAepsActivity.this);
                                unifiedAepsPresenter.performUnifiedResponse(UnifiedAepsActivity.this, session.getUserToken(), unifiedAepsRequestModel, cashWithdrawalButton.getText().toString(), gatewayPriority);
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                    } else {
                        Toast.makeText(UnifiedAepsActivity.this, "Accept/Enable the Terms & Conditions for the further transaction", Toast.LENGTH_SHORT).show();
                    }

//                if (SdkConstants.applicationType.equalsIgnoreCase("CORE")) {
//                    if (SdkConstants.firstCheck) {
//                        if (SdkConstants.transactionType.equalsIgnoreCase(SdkConstants.balanceEnquiry)) {
//                            showLoader();
//                            try {
//                                JSONObject respObj = new JSONObject(SdkConstants.RECEIVE_DRIVER_DATA);
//                                String pidData = respObj.getString("base64pidData");
//                                unifiedAepsRequestModel = new UnifiedAepsRequestModel("", balanaceInqueryAadharNo,
//                                        bankIINNumber, mobileNumber.getText().toString().trim(), "ANDROIDUSER", bankspinner.getText().toString(), pidData, latLong, SdkConstants.paramA, SdkConstants.paramB, SdkConstants.paramC, session.getUserName());
//                                unifiedAepsPresenter = new UnifiedAepsPresenter(UnifiedAepsActivity.this);
//                                unifiedAepsPresenter.performUnifiedResponse(UnifiedAepsActivity.this, session.getUserToken(), unifiedAepsRequestModel, balanceEnquiryExpandButton.getText().toString(), gatewayPriority);
//                            } catch (JSONException e) {
//                                e.printStackTrace();
//                            }
//                        } else if (SdkConstants.transactionType.equalsIgnoreCase(SdkConstants.ministatement)) {
//                            showLoader();
//                            try {
//                                JSONObject respObj = new JSONObject(SdkConstants.RECEIVE_DRIVER_DATA);
//                                String pidData = respObj.getString("base64pidData");
//                                unifiedAepsRequestModel = new UnifiedAepsRequestModel("", balanaceInqueryAadharNo,
//                                        bankIINNumber, mobileNumber.getText().toString().trim(), "ANDROIDUSER", bankspinner.getText().toString(), pidData.toString().trim(), latLong, SdkConstants.paramA, SdkConstants.paramB, SdkConstants.paramC, session.getUserName());
//                                unifiedAepsPresenter = new UnifiedAepsPresenter(UnifiedAepsActivity.this);
//                                unifiedAepsPresenter.performUnifiedResponse(UnifiedAepsActivity.this, session.getUserToken(), unifiedAepsRequestModel, balanceEnquiryExpandButton.getText().toString(), gatewayPriority);
//                            } catch (JSONException e) {
//                                e.printStackTrace();
//                            }
//                        } else {
//                            showLoader();
//                            if (amountEnter.getText() == null || amountEnter.getText().toString().trim().matches("")) {
//                                amountEnter.setError(getResources().getString(R.string.amount_error));
//                                return;
//                            }
//                            try {
//                                JSONObject respObj = new JSONObject(SdkConstants.RECEIVE_DRIVER_DATA);
//                                String pidData = respObj.getString("base64pidData");
//                                unifiedAepsRequestModel = new UnifiedAepsRequestModel(amountEnter.getText().toString().trim(), balanaceInqueryAadharNo,
//                                        bankIINNumber, mobileNumber.getText().toString().trim(), "ANDROIDUSER", bankspinner.getText().toString(), pidData, latLong, SdkConstants.paramA, SdkConstants.paramB, SdkConstants.paramC, session.getUserName());
//                                unifiedAepsPresenter = new UnifiedAepsPresenter(UnifiedAepsActivity.this);
//                                unifiedAepsPresenter.performUnifiedResponse(UnifiedAepsActivity.this, session.getUserToken(), unifiedAepsRequestModel, cashWithdrawalButton.getText().toString(), gatewayPriority);
//                            } catch (JSONException e) {
//                                e.printStackTrace();
//                            }
//                        }
//                    } else {
//                        Toast.makeText(UnifiedAepsActivity.this, "Accept/Enable the Terms & Conditions for the further transaction", Toast.LENGTH_SHORT).show();
//                    }
//                }else {
//                    if (SdkConstants.firstCheck) {
//                        if (SdkConstants.transactionType.equalsIgnoreCase(SdkConstants.balanceEnquiry)) {
//                            showLoader();
//                            try {
//                                JSONObject respObj = new JSONObject(SdkConstants.RECEIVE_DRIVER_DATA);
//                                String pidData = respObj.getString("base64pidData");
//                                unifiedAepsRequestModel = new UnifiedAepsRequestModel("", balanaceInqueryAadharNo,
//                                        bankIINNumber, mobileNumber.getText().toString().trim(), "APIUSER", bankspinner.getText().toString(), pidData, latLong, SdkConstants.paramA, SdkConstants.paramB, SdkConstants.paramC, session.getUserName());
//                                unifiedAepsPresenter = new UnifiedAepsPresenter(UnifiedAepsActivity.this);
//                                unifiedAepsPresenter.performUnifiedResponse(UnifiedAepsActivity.this, session.getUserToken(), unifiedAepsRequestModel, balanceEnquiryExpandButton.getText().toString(), gatewayPriority);
//                            } catch (JSONException e) {
//                                e.printStackTrace();
//                            }
//                        } else if (SdkConstants.transactionType.equalsIgnoreCase(SdkConstants.ministatement)) {
//                            showLoader();
//                            try {
//                                JSONObject respObj = new JSONObject(SdkConstants.RECEIVE_DRIVER_DATA);
//                                String pidData = respObj.getString("base64pidData");
//                                unifiedAepsRequestModel = new UnifiedAepsRequestModel("", balanaceInqueryAadharNo,
//                                        bankIINNumber, mobileNumber.getText().toString().trim(), "APIUSER", bankspinner.getText().toString(), pidData.toString().trim(), latLong, SdkConstants.paramA, SdkConstants.paramB, SdkConstants.paramC, session.getUserName());
//                                unifiedAepsPresenter = new UnifiedAepsPresenter(UnifiedAepsActivity.this);
//                                unifiedAepsPresenter.performUnifiedResponse(UnifiedAepsActivity.this, session.getUserToken(), unifiedAepsRequestModel, balanceEnquiryExpandButton.getText().toString(), gatewayPriority);
//                            } catch (JSONException e) {
//                                e.printStackTrace();
//                            }
//                        } else {
//                            showLoader();
//                            if (amountEnter.getText() == null || amountEnter.getText().toString().trim().matches("")) {
//                                amountEnter.setError(getResources().getString(R.string.amount_error));
//                                return;
//                            }
//                            try {
//                                JSONObject respObj = new JSONObject(SdkConstants.RECEIVE_DRIVER_DATA);
//                                String pidData = respObj.getString("base64pidData");
//                                unifiedAepsRequestModel = new UnifiedAepsRequestModel(amountEnter.getText().toString().trim(), balanaceInqueryAadharNo,
//                                        bankIINNumber, mobileNumber.getText().toString().trim(), "APIUSER", bankspinner.getText().toString(), pidData, latLong, SdkConstants.paramA, SdkConstants.paramB, SdkConstants.paramC, session.getUserName());
//                                unifiedAepsPresenter = new UnifiedAepsPresenter(UnifiedAepsActivity.this);
//                                unifiedAepsPresenter.performUnifiedResponse(UnifiedAepsActivity.this, session.getUserToken(), unifiedAepsRequestModel, cashWithdrawalButton.getText().toString(), gatewayPriority);
//                            } catch (JSONException e) {
//                                e.printStackTrace();
//                            }
//                        }
//                    } else {
//                        Toast.makeText(UnifiedAepsActivity.this, "Accept/Enable the Terms & Conditions for the further transaction", Toast.LENGTH_SHORT).show();
//                    }
                }
        });
        if ((getIntent().getStringExtra("FAILEDVALUE") != null) && getIntent().getStringExtra("FAILEDVALUE").equalsIgnoreCase("FAILEDDATA")) {
            aadharNumber.setText(makePrettyString(SdkConstants.AADHAAR_NUMBER));
            bankspinner.setText(SdkConstants.BANK_NAME);
            mobileNumber.setText(SdkConstants.MOBILENUMBER);
            bankIINNumber = SdkConstants.bankIIN;
            aadharNumber.setEnabled(false);
            aadharNumber.setTextColor(getResources().getColor(R.color.grey));
            mobileNumber.setEnabled(false);
            mobileNumber.clearFocus();
            bankspinner.setEnabled(false);
            fingerprint.setEnabled(true);
            fingerprint.setClickable(true);
            theme.resolveAttribute(R.attr.colorPrimary, typedValue, true);
            int color = typedValue.data;
            fingerprint.setColorFilter(color);
            SdkConstants.RECEIVE_DRIVER_DATA = "";
            depositBar.setVisibility(View.GONE);
            depositNote.setVisibility(View.GONE);

            submitButton.setBackground(getResources().getDrawable(R.drawable.button_submit));
            gatewayPriority = gatewayPriority + 1;
        }


        if(SdkConstants.refeshUI){
                aadharNumber.setText(SdkConstants.aadharNumberValue);
                bankspinner.setText(SdkConstants.bankValue);
                mobileNumber.setText(SdkConstants.mobileNumberValue);
            SdkConstants.RECEIVE_DRIVER_DATA = getIntent().getStringExtra("recieverData");

            Log.e("SUBHA","receive data" + SdkConstants.RECEIVE_DRIVER_DATA);
            Log.e("SUBHA","aadhar data" + SdkConstants.aadharNumberValue);
            Log.e("SUBHA","bank data" + SdkConstants.bankValue);
            Log.e("SUBHA","mobile data" + SdkConstants.mobileNumberValue);
            fingerStrength();
            bankIINNumber = SdkConstants.bankIIN;
            fingerprint.setColorFilter(R.color.colorGrey);
            fingerprint.setEnabled(false);
            flagFromDriver = true;
            SdkConstants.refeshUI = false;

        }
    }

    private void getRDServiceClass() {
        //String accessClassName =  getIntent().getStringExtra("activity");
        //flagNameRdService = getIntent().getStringExtra("driverFlag");
        String accessClassName = SdkConstants.DRIVER_ACTIVITY;//getIntent().getStringExtra("activity");
        flagNameRdService = SdkConstants.MANUFACTURE_FLAG;//getIntent().getStringExtra("driverFlag");
        try {
            Class<? extends Activity> targetActivity = Class.forName(accessClassName).asSubclass(Activity.class);
            driverActivity = targetActivity;
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    @TargetApi(Build.VERSION_CODES.O)
    private void disableAutofill() {
        getWindow().getDecorView().setImportantForAutofill(View.IMPORTANT_FOR_AUTOFILL_NO_EXCLUDE_DESCENDANTS);
    }

    public void getLocation() {
        gpsTracker = new GpsTracker(UnifiedAepsActivity.this);
        if (gpsTracker.canGetLocation()) {
            latitude = gpsTracker.getLatitude();
            longitude = gpsTracker.getLongitude();
            latLong = latitude + "," + longitude;
        } else {
            gpsTracker.showSettingsAlert();
        }
        saveLocation();
    }
    public void saveLocation(){
        SharedPreferences sharedPreferences = getSharedPreferences(SHARED_PREFS, MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        if(!latLong.isEmpty()){
            lastLatlong = latLong;
        }
        editor.putString(lastLatlong, latLong);
        editor.apply();
    }
    public boolean validLatLng (double lat, double lng) {
        if(lat != 0.0 && lng != 0.0){
            return true;
        } else
            return false;
    }

    LocationListener mLocationListener = new LocationListener() {

        public void onLocationChanged (Location location){

            if ( validLatLng(location.getLatitude(), location.getLongitude())) {
                //System.out.println("got new location");
                //Log.i("mLocationListener", "Got location");   // for logCat should ->  import android.util.Log;
                // Stops the new update requests.
                locationManager.removeUpdates(mLocationListener);
                double latitude = location.getLatitude();
                double longitude = location.getLongitude();
                latLong = latitude + "," + longitude;
            }
        }

        public void onStatusChanged(String s, int i, Bundle bundle) {
        }

        public void onProviderEnabled(String s){
        }

        public void onProviderDisabled(String s){
        }

    };





    @Override
    public void onBackPressed() {
        super.onBackPressed();
        SdkConstants.mobileNumberValue = "";
        SdkConstants.bankValue = "";
        SdkConstants.aadharNumberValue = "";
        SdkConstants.RECEIVE_DRIVER_DATA = "";
        SdkConstants.OnBackpressedValue = true;
        depositBar.setVisibility(View.GONE);
        this.finish();
    }



    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == SdkConstants.REQUEST_FOR_ACTIVITY_BALANCE_ENQUIRY_CODE) {
            hideLoader();
            if (resultCode == RESULT_OK) {
                BankNameModel bankIINValue = (BankNameModel) data.getSerializableExtra(SdkConstants.IIN_KEY);
                bankspinner.setText(bankIINValue.getBankName());
                bankIINNumber = bankIINValue.getIin();
                SdkConstants.BANK_NAME = bankIINValue.getBankName();
                SdkConstants.bankIIN = bankIINNumber;
                checkBalanceEnquiryValidation();
            }
            checkBalanceEnquiryValidation();
        } else if (requestCode == SdkConstants.REQUEST_FOR_ACTIVITY_CASH_WITHDRAWAL_CODE) {
            hideLoader();
            if (resultCode == RESULT_OK) {
                BankNameModel bankIINValue = (BankNameModel) data.getSerializableExtra(SdkConstants.IIN_KEY);
                bankspinner.setText(bankIINValue.getBankName());
                bankIINNumber = bankIINValue.getIin();
                SdkConstants.bankIIN = bankIINNumber;
                SdkConstants.BANK_NAME = bankIINValue.getBankName();
                checkWithdrawalValidation();
            }
            checkWithdrawalValidation();
        } else if (requestCode == SdkConstants.REQUEST_CODE) {
            hideLoader();
            if (resultCode == RESULT_OK) {
                Intent respIntent = new Intent();
                respIntent.putExtra(SdkConstants.responseData, SdkConstants.transactionResponse);
                setResult(Activity.RESULT_OK, respIntent);
                finish();
            }
            checkWithdrawalValidation();
        } else if (requestCode == 1) {
            hideLoader();
        }
    }

    @Override
    public void checkUnifiedResponseStatus(String status, String message, UnifiedTxnStatusModel miniStatementResponseModel) {
        if (status.equalsIgnoreCase("SUCCESS")) {
            //  statusNotification("Success", "Balance Enquiry", UnifiedAepsTransactionActivity.class, transactionStatusModel);
            Intent intent = new Intent(UnifiedAepsActivity.this, UnifiedAepsTransactionActivity.class);
            intent.putExtra(SdkConstants.TRANSACTION_STATUS_KEY, miniStatementResponseModel);
            intent.putExtra("MOBILE_NUMBER", mobileNumber.getText().toString().trim());
            hideLoader();
            startActivity(intent);
        } else {
            //  statusNotification("Success", "Balance Enquiry", UnifiedAepsTransactionActivity.class, transactionStatusModel);
            Intent intent = new Intent(UnifiedAepsActivity.this, UnifiedAepsTransactionActivity.class);
            intent.putExtra(SdkConstants.TRANSACTION_STATUS_KEY, miniStatementResponseModel);
            intent.putExtra("MOBILE_NUMBER", mobileNumber.getText().toString().trim());
            hideLoader();
            startActivity(intent);
        }
        finish();
    }

    @Override
    public void checkMSunifiedStatus(String status, String message, UnifiedTxnStatusModel miniStatementResponseModel) {
        if (status.equalsIgnoreCase("SUCCESS")) {
            Gson gson = new Gson();
            session.setFreshnessFactor("");
            //  statusNotification("Success", "Balance Enquiry", UnifiedAepsTransactionActivity.class, transactionStatusModel);
            Intent intent = new Intent(UnifiedAepsActivity.this, UnifiedAepsMiniStatementActivity.class);
            intent.putExtra(SdkConstants.TRANSACTION_STATUS_KEY, miniStatementResponseModel);
            intent.putExtra("MOBILE_NUMBER", mobileNumber.getText().toString().trim());
            hideLoader();
            startActivity(intent);
        } else {
            session.setFreshnessFactor("");
//           statusNotification("Success", "Balance Enquiry", UnifiedAepsTransactionActivity.class, transactionStatusModel);
            Intent intent = new Intent(UnifiedAepsActivity.this, UnifiedAepsTransactionActivity.class);
            intent.putExtra(SdkConstants.TRANSACTION_STATUS_KEY, miniStatementResponseModel);
            intent.putExtra("MOBILE_NUMBER", mobileNumber.getText().toString().trim());
            hideLoader();
            startActivity(intent);
        }
        finish();
    }

    @Override
    public void showLoader() {
        try {
            if (loadingView == null) {
                loadingView = new ProgressDialog(UnifiedAepsActivity.this);
                loadingView.setCancelable(false);
                loadingView.setMessage("Please Wait..");
            }
            loadingView.show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void hideLoader() {
        try {
            if (loadingView != null) {
                loadingView.dismiss();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void checkBalanceEnquiryValidation() {
        // TODO Auto-generated method stub
        if (mobileNumber.getText() != null && !mobileNumber.getText().toString().trim().matches("")
                && Util.isValidMobile(mobileNumber.getText().toString().trim()) == true && bankspinner.getText() != null
                && !bankspinner.getText().toString().trim().matches("")) {
            boolean status = false;
            if (adharbool == true) {
                String aadharNo = aadharNumber.getText().toString();
                if (aadharNo.contains("-")) {
                    aadharNo = aadharNo.replaceAll("-", "").trim();
                    status = Util.validateAadharNumber(aadharNo);
                }
            } else if (virtualbool == true) {
                String aadharVid = aadharVirtualID.getText().toString();
                if (aadharVid.contains("-")) {
                    aadharVid = aadharVid.replaceAll("-", "").trim();
                    status = Util.validateAadharVID(aadharVid);
                }
            }
        } else {
            submitButton.setEnabled(false);
            submitButton.setBackground(getResources().getDrawable(R.drawable.button_submit));
        }
    }

    private void checkWithdrawalValidation() {
        // TODO Auto-generated method stub
        if (mobileNumber.getText() != null
                && !mobileNumber.getText().toString().trim().matches("")
                && Util.isValidMobile(mobileNumber.getText().toString().trim()) == true
                && mobileNumber.getText().toString().length() == 10
                && bankspinner.getText() != null
                && !bankspinner.getText().toString().trim().matches("")
                && amountEnter.getText() != null
                && !amountEnter.getText().toString().trim().matches("")) {
            boolean status = false;
            if (adharbool == true) {
                String aadharNo = aadharNumber.getText().toString();
                if (aadharNo.contains("-")) {
                    aadharNo = aadharNo.replaceAll("-", "").trim();
                    status = Util.validateAadharNumber(aadharNo);
                }
            } else if (virtualbool == true) {
                String aadharVid = aadharVirtualID.getText().toString();
                if (aadharVid.contains("-")) {
                    aadharVid = aadharVid.replaceAll("-", "").trim();
                    status = Util.validateAadharVID(aadharVid);
                }
            }
        } else {
            submitButton.setEnabled(false);
            submitButton.setBackground(getResources().getDrawable(R.drawable.button_submit));
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        hideKeyboard();

        if (flagFromDriver) {
            if (SdkConstants.RECEIVE_DRIVER_DATA.isEmpty() || SdkConstants.RECEIVE_DRIVER_DATA.equalsIgnoreCase("")) {
                theme.resolveAttribute(R.attr.colorPrimary, typedValue, true);
                int color = typedValue.data;
                fingerprint.setColorFilter(color);
                fingerprint.setEnabled(true);
                fingerprint.setClickable(true);
                submitButton.setBackgroundResource(R.drawable.button_submit);
                submitButton.setEnabled(false);
            } else if (balanaceInqueryAadharNo.equalsIgnoreCase("") || balanaceInqueryAadharNo.isEmpty()) {
                aadharNumber.setError("Enter Aadhar No.");
                fingerStrength();
            } else if (mobileNumber.getText().toString().isEmpty() || mobileNumber.getText().toString().equalsIgnoreCase("")) {
                mobileNumber.setError("Enter mobile no.");
                fingerStrength();
            } else if (bankspinner.getText().toString().isEmpty() || bankspinner.getText().toString().trim().equalsIgnoreCase("")) {
                bankspinner.setError("Choose your bank.");
                fingerStrength();
            } else {
                fingerStrength();
                //fingerprint.setImageDrawable(getResources().getDrawable(R.drawable.F));
                fingerprint.setColorFilter(R.color.colorGrey);
                fingerprint.setEnabled(false);
            }
        }
    }

    public void hideKeyboard() {
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
    }

    //Finger Strength
    public void fingerStrength() {
        try {
            JSONObject respObj = new JSONObject(SdkConstants.RECEIVE_DRIVER_DATA);
            Log.d("WiseasyDriverActivity", "respObj " + respObj);
            String scoreStr = respObj.getString("pidata_qscore");
            String[] scoreList = scoreStr.split(",");
            scoreStr = scoreList[0];
            if (scoreStr.equalsIgnoreCase("")) {
                hideLoader();
                showAlert("Invalid Fingerprint Data");
            } else {

                submitButton.setEnabled(true);
                submitButton.setBackgroundResource(R.drawable.button_submit_blue);

                if (Float.parseFloat(scoreStr) <= 40) {
                    depositBar.setVisibility(View.VISIBLE);
                    depositBar.setProgress(Float.parseFloat(scoreStr));
                    depositBar.setProgressTextMoved(true);
                    depositBar.setEndColor(getResources().getColor(R.color.red));
                    depositBar.setStartColor(getResources().getColor(R.color.red));
                    depositNote.setVisibility(View.VISIBLE);
                    fingerprintStrengthDeposit.setVisibility(View.VISIBLE);
                } /*else if (Float.parseFloat(scoreStr) >= 30 && Float.parseFloat(scoreStr) <= 60) {

                depositBar.setVisibility(View.VISIBLE);
                depositBar.setProgress(Float.parseFloat(scoreStr));
                depositBar.setProgressTextMoved(true);
                depositBar.setEndColor(getResources().getColor(R.color.yellow));
                depositBar.setStartColor(getResources().getColor(R.color.yellow));
                depositNote.setVisibility(View.VISIBLE);
                fingerprintStrengthDeposit.setVisibility(View.VISIBLE);
            }*/ else {

                    depositBar.setVisibility(View.VISIBLE);
                    depositBar.setProgress(Float.parseFloat(scoreStr));
                    depositBar.setProgressTextMoved(true);
                    depositBar.setEndColor(getResources().getColor(R.color.green));
                    depositBar.setStartColor(getResources().getColor(R.color.green));
                    depositNote.setVisibility(View.VISIBLE);
                    fingerprintStrengthDeposit.setVisibility(View.VISIBLE);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            showAlert("Invalid Fingerprint Data");
        }
    }

    public void releaseData() {
        amountEnter.setText(null);
        amountEnter.setError(null);
        aadharNumber.setText(null);
        aadharNumber.setError(null);
        mobileNumber.setText(null);
        mobileNumber.setError(null);
        bankspinner.setText(null);
        bankspinner.setError(null);
        bankIINNumber = "";
        unifiedAepsRequestModel = null;
        depositBar.setVisibility(View.GONE);
        depositNote.setVisibility(View.GONE);
        fingerprintStrengthDeposit.setVisibility(View.GONE);
        fingerprintStrengthDeposit.setVisibility(View.GONE);
    }

    public void showAlert(String msg) {
//        try {
//            AlertDialog.Builder builder = new AlertDialog.Builder(UnifiedAepsActivity.this);
//            builder.setTitle("Alert!!");
//            builder.setMessage(msg);
//            builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
//                @Override
//                public void onClick(DialogInterface dialog, int which) {
//                    dialog.dismiss();
//                    finish();
//                }
//            });
//            AlertDialog dialog = builder.create();
//            dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
//            dialog.setCancelable(false);
//            dialog.show();
//
//        } catch (Exception e) {
//            e.printStackTrace();
//        }

        SingletonClass.getInstance().showAlert(msg, UnifiedAepsActivity.this);
    }

    private String makePrettyString(String string) {
        String number = string.replaceAll("-", "");
        boolean isEndHyphen = string.endsWith("-") && (number.length() % 4 == 0);
        return number.replaceAll("(.{4}(?!$))", "$1-") + (isEndHyphen ? "-" : "");
    }

    private int getCursorPos(String oldString, String newString, int oldPos, boolean isDeleteHyphen) {
        int cursorPos = newString.length();
        if (oldPos != oldString.length()) {
            String stringWithMarker = oldString.substring(0, oldPos) + MARKER + oldString.substring(oldPos);

            cursorPos = (makePrettyString(stringWithMarker)).indexOf(MARKER);
            if (isDeleteHyphen)
                cursorPos -= 1;
        }
        return cursorPos;
    }

    public void statusNotification(String title, String body, Class intnetClass, TransactionStatusModel raw) {
        if (SdkConstants.showNotification) {
            NotificationCompat.Builder builder = notificationHelper.createTransactionStatusNotif(this,
                    title,
                    body,
                    intnetClass,
                    raw);
            if (builder != null) {
//                notificationHelper.create(0, builder);
            }
        }
    }

    public void showTermsDetails(Activity activity) {
        try {
            final Dialog dialog = new Dialog(activity);
            dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
            dialog.setCancelable(false);
            dialog.setContentView(R.layout.activity_terms_conditions);
            dialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
            dialog.getWindow().setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.WRAP_CONTENT);
            TextView firstText = (TextView) dialog.findViewById(R.id.firststText);
            TextView secondText = (TextView) dialog.findViewById(R.id.secondstText);
            SwitchCompat switchCompat = (SwitchCompat) dialog.findViewById(R.id.swOnOff);
            switchCompat.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    if (isChecked) {
                        firstText.setText(getResources().getString(R.string.hinditm1));
                        secondText.setText(getResources().getString(R.string.hinditm2));
                    } else {
                        firstText.setText(getResources().getString(R.string.term1));
                        secondText.setText(getResources().getString(R.string.term2));
                    }
                }
            });
            Button dialogBtn_close = (Button) dialog.findViewById(R.id.close_Btn);
            dialogBtn_close.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    dialog.cancel();
                }
            });
            dialog.show();
        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(UnifiedAepsActivity.this, "Error" + e.toString(), Toast.LENGTH_SHORT).show();
        }
    }


    @Override
    public void onFingerClick(String aadharNo, String mobileNumber, String bankName, String driverFlag, String freashnessFactor, OnDriverDataListener listener) {

    }

    @Override
    public void onPidUpdate(String data) {

        //TODO
    }

    public class ChangeTransformationMethod extends PasswordTransformationMethod {
        @Override
        public CharSequence getTransformation(CharSequence source, View view) {
            return new PasswordCharSequence(source);
        }

        private class PasswordCharSequence implements CharSequence {
            private CharSequence mSource;
            public PasswordCharSequence(CharSequence source) {
                mSource = source;
            }
            public char charAt(int index) {
                if(index <=8 && mSource.length() > 9)
                    return 'X';
                else
                    return mSource.charAt(index);
            }
            public int length() {
                return mSource.length();
            }
            public CharSequence subSequence(int start, int end) {
                return mSource.subSequence(start, end);
            }
        }
    }

    public void showSettingsAlert(){
//        AlertDialog.Builder alertDialog = new AlertDialog.Builder(UnifiedAepsActivity.this);
//        // Setting Dialog Title
//        alertDialog.setTitle("GPS is settings");
//        // Setting Dialog Message
//        alertDialog.setMessage("GPS is not enabled. Do you want to go to settings menu?");
//        // On pressing Settings button
//        alertDialog.setPositiveButton("Settings", new DialogInterface.OnClickListener() {
//            public void onClick(DialogInterface dialog,int which) {
//                Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
//                startActivity(intent);
//                finish();
//            }
//        });
//        // on pressing cancel button
//        alertDialog.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
//            public void onClick(DialogInterface dialog, int which) {
//                dialog.cancel();
//                finish();
//            }
//        });
//        alertDialog.show();
        SingletonClass.getInstance().showSettingsAlert(UnifiedAepsActivity.this);

    }

}