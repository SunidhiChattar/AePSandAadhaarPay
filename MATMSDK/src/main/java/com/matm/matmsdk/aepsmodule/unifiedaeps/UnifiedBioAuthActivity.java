package com.matm.matmsdk.aepsmodule.unifiedaeps;

import android.Manifest;
import android.annotation.TargetApi;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentSender;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Build;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.androidnetworking.AndroidNetworking;
import com.androidnetworking.common.Priority;
import com.androidnetworking.error.ANError;
import com.androidnetworking.interfaces.JSONObjectRequestListener;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResult;
import com.google.android.gms.location.LocationSettingsStatusCodes;
import com.matm.matmsdk.Utils.SdkConstants;
import com.matm.matmsdk.aepsmodule.network.api.ApiInterface;
import com.matm.matmsdk.aepsmodule.network.api.RetrofitInstance;
import com.matm.matmsdk.aepsmodule.network.model.AadharVerificationResponse;
import com.matm.matmsdk.aepsmodule.network.model.Base64Response;
import com.matm.matmsdk.aepsmodule.network.model.BioAuthRequest;
import com.matm.matmsdk.aepsmodule.network.model.BioAuthResponse;
import com.matm.matmsdk.aepsmodule.network.model.addressupdate.AadhaaarUpdateRequest;
import com.matm.matmsdk.aepsmodule.network.model.addressupdate.AddressUpdateResponse;
import com.matm.matmsdk.aepsmodule.network.model.pincode.Data;
import com.matm.matmsdk.aepsmodule.network.model.pincode.PinCodeRequest;
import com.matm.matmsdk.aepsmodule.network.model.pincode.PinCodeResponse;
import com.matm.matmsdk.aepsmodule.utils.Session;
import com.matm.matmsdk.aepsmodule.utils.Util;
import com.moos.library.HorizontalProgressView;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import isumatm.androidsdk.equitas.R;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class UnifiedBioAuthActivity extends AppCompatActivity implements GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener,
        LocationListener {
    final static String MARKER = "|"; // filtered in layout not to be in the string
    private final static int REQUEST_CHECK_SETTINGS_GPS = 0x1;
    private final static int REQUEST_ID_MULTIPLE_PERMISSIONS = 0x2;
    private static final int REQUEST_CAMERA_PERMISSIONS = 931;
    ImageView two_fact_fingerprint;
    Button two_fact_submitButton;
    ProgressDialog loadingView;
    Session session;
    TextView userName;
    EditText balanceAadharNumber, balanceAadharVID;
    String postalCode = "751017";
    int pincode = 751017;
    String userNameStr = "";
    Boolean isSL = false;
    LinearLayout bio_ll;
    String bankIINNumber = "";
    String flagNameRdService = "";
    Class driverActivity;
    String balanaceInqueryAadharNo = "";
    Boolean flagFromDriver = false;
    boolean mInside = false;
    boolean mWannaDeleteHyphen = false;
    boolean mKeyListenerSet = false;
    // ProgressBar two_fact_depositBar;
    private HorizontalProgressView depositBar;
    private Location mylocation;
    private GoogleApiClient googleApiClient;
    private Boolean location_flag = false;
    private String encodedUrl;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bio_auth);
        bio_ll = findViewById(R.id.bio_ll);
        SdkConstants.RECEIVE_DRIVER_DATA = "";
        session = new Session(UnifiedBioAuthActivity.this);
        getRDServiceClass();

        setUpGClient();
        if (SdkConstants.applicationType.equalsIgnoreCase("CORE")) {
            session.setUserToken(SdkConstants.tokenFromCoreApp);
            session.setUsername(SdkConstants.userNameFromCoreApp);
            userNameStr = SdkConstants.applicationUserName;
            checkAddressStatus();
            isSL = false;
            showLoader();

        } else {
            isSL = true;
            session.setUserToken(SdkConstants.tokenFromCoreApp);
            session.setUsername(SdkConstants.userNameFromCoreApp);
            checkAddressStatus();
            showLoader();
            /*if (SdkConstants.encryptedData.trim().length() != 0 && SdkConstants.transactionType.trim().length() != 0 && SdkConstants.loginID.trim().length() != 0) {
                getUserAuthToken();
            } else {
                showAlert("Request parameters are missing. Please check and try again..");
            }*/
        }


        two_fact_fingerprint = findViewById(R.id.two_fact_fingerprint);
        two_fact_fingerprint.setEnabled(true);
        depositBar = findViewById(R.id.depositBar);
        depositBar.setVisibility(View.GONE);

        two_fact_submitButton = findViewById(R.id.two_fact_submitButton);
        RadioButton bl_aadhar_no_rd = findViewById(R.id.bl_aadhar_no_rd);
        RadioButton bl_aadhar_uid_rd = findViewById(R.id.bl_aadhar_uid_rd);
        balanceAadharNumber = findViewById(R.id.balanceAadharNumber);
        balanceAadharVID = findViewById(R.id.balanceAadharVID);

        userName = findViewById(R.id.userName);
        userName.setText(SdkConstants.applicationUserName);

        two_fact_fingerprint.setEnabled(false);
        two_fact_fingerprint.setClickable(false);


        balanceAadharNumber.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                if (s.length() < 1) {
                    balanceAadharNumber.setError(getResources().getString(R.string.aadhaarnumber));

                }
                if (s.length() > 0) {
                    balanceAadharNumber.setError(null);
                    String aadharNo = balanceAadharNumber.getText().toString();
                    if (aadharNo.contains("-")) {
                        aadharNo = aadharNo.replaceAll("-", "").trim();
                        balanaceInqueryAadharNo = aadharNo;
                        if (balanaceInqueryAadharNo.length() >= 12) {
                            two_fact_fingerprint.setEnabled(true);
                        }
                    } else {
                        balanaceInqueryAadharNo = aadharNo;
                        if (balanaceInqueryAadharNo.length() >= 12) {
                            if (Util.validateAadharNumber(aadharNo) == false) {
                                balanceAadharNumber.setError(getResources().getString(R.string.Validaadhaarerror));
                            } else {
                                two_fact_fingerprint.setEnabled(true);
                                two_fact_fingerprint.setClickable(true);
                                two_fact_fingerprint.setBackgroundResource(R.color.buttonSolidColor);
                            }
                        }
                    }
      /*              if (Util.validateAadharNumber(aadharNo) == false) {
                        balanceAadharNumber.setError(getResources().getString(R.string.valid_aadhar_error));
                    }*/
                }
            }
        });

        balanceAadharVID.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                if (s.length() < 1) {
                    balanceAadharVID.setError(getResources().getString(R.string.aadhaarnumber_vid));

                }

                if (s.length() > 0) {
                    balanceAadharVID.setError(null);
                    String aadharNo = balanceAadharVID.getText().toString();
                    if (aadharNo.contains("-")) {
                        aadharNo = aadharNo.replaceAll("-", "").trim();
                        balanaceInqueryAadharNo = aadharNo;
                        if (balanaceInqueryAadharNo.length() >= 12) {
                            two_fact_fingerprint.setEnabled(true);
                        }
                    } else {
                        balanaceInqueryAadharNo = aadharNo;
                        if (balanaceInqueryAadharNo.length() >= 12) {
                            two_fact_fingerprint.setEnabled(true);
                        }
                    }
                    if (Util.validateAadharVID(aadharNo) == false) {
                        balanceAadharVID.setError(getResources().getString(R.string.valid_aadhar__uid_error));
                    } else {
                        two_fact_fingerprint.setEnabled(true);
                        two_fact_fingerprint.setClickable(true);
                        //fingerprint.setImageDrawable(getResources().getDrawable(R.drawable.ic_scanner));
//                        int color = typedValu etColorFilter(color);
                    }
                }
            }
        });

        two_fact_fingerprint.setOnClickListener(new View.OnClickListener() {
            @TargetApi(Build.VERSION_CODES.LOLLIPOP)
            @Override
            public void onClick(View v) {

                if (balanaceInqueryAadharNo.length() > 0 && Util.validateAadharNumber(balanaceInqueryAadharNo) == true) {
                    showLoader();
                    flagFromDriver = true;
                    Intent launchIntent = new Intent(UnifiedBioAuthActivity.this, driverActivity);
                    launchIntent.putExtra("driverFlag", flagNameRdService);
                    launchIntent.putExtra("freshnesFactor", session.getFreshnessFactor());
                    launchIntent.putExtra("AadharNo", balanaceInqueryAadharNo);
                    startActivityForResult(launchIntent, 1);
                } else {
                    Toast.makeText(UnifiedBioAuthActivity.this, R.string.Validaadhaarerror, Toast.LENGTH_SHORT).show();
                }
            }
        });
        two_fact_submitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (balanceAadharNumber.getText().toString().equals("")) {
                    Toast.makeText(UnifiedBioAuthActivity.this, R.string.aadhaarnumber, Toast.LENGTH_SHORT).show();
                } else {
                    if (Util.validateAadharNumber(balanaceInqueryAadharNo) == false) {
                        Toast.makeText(UnifiedBioAuthActivity.this, R.string.Validaadhaarerror, Toast.LENGTH_SHORT).show();
                    } else {
                        if (!flagFromDriver) {
                            Toast.makeText(UnifiedBioAuthActivity.this, "Please do Biometric Verification", Toast.LENGTH_LONG).show();
                            return;
                        } else {
                            try {
                                JSONObject respObj = new JSONObject(SdkConstants.RECEIVE_DRIVER_DATA);
                                String scoreStr = respObj.getString("pidata_qscore");
                                if (Float.parseFloat(scoreStr) <= 40) {
                                    Toast.makeText(UnifiedBioAuthActivity.this, "Bad Fingerprint Strength, Please try Again !", Toast.LENGTH_SHORT).show();
                                    return;
                                } else {
                                    try {
//                                        JSONObject respObj = new JSONObject(SdkConstants.RECEIVE_DRIVER_DATA);
                                        String CI = respObj.getString("CI");
                                        String DC = respObj.getString("DC");
                                        String DPID = respObj.getString("DPID");
                                        String DATAVALUE = respObj.getString("DATAVALUE");
                                        String HMAC = respObj.getString("HMAC");
                                        String MI = respObj.getString("MI");
                                        String MC = respObj.getString("MC");
                                        String RDSID = respObj.getString("RDSID");
                                        String RDSVER = respObj.getString("RDSVER");
                                        String value = respObj.getString("value");

                                        JSONObject obj = new JSONObject();
                                        obj.put("aadharNo", balanaceInqueryAadharNo);
                                        obj.put("dpId", DPID);
                                        obj.put("rdsId", RDSID);
                                        obj.put("rdsVer", RDSVER);
                                        obj.put("dc", DC);
                                        obj.put("mi", MI);
                                        obj.put("mcData", MC);
                                        obj.put("sKey", value);
                                        obj.put("hMac", HMAC);
                                        obj.put("encryptedPID", DATAVALUE);
                                        obj.put("ci", CI);
                                        obj.put("operation", "");
                                        obj.put("isSL", isSL);
                                        obj.put("apiUserName", SdkConstants.API_USER_NAME_VALUE);
                                        obj.put("retailer", SdkConstants.userNameFromCoreApp);

                                        submitBioAuthh(new BioAuthRequest(balanaceInqueryAadharNo,SdkConstants.API_USER_NAME_VALUE, CI, DC, DPID, DATAVALUE, HMAC, isSL, MC, MI, "", RDSID, RDSVER, SdkConstants.userNameFromCoreApp, value));
                                    } catch (JSONException e) {
                                        e.printStackTrace();
                                    }

                                }
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                    }

                }
            }

        });
    }

    private void getRDServiceClass() {
        String accessClassName = SdkConstants.DRIVER_ACTIVITY;//getIntent().getStringExtra("activity");
        flagNameRdService = SdkConstants.MANUFACTURE_FLAG;//getIntent().getStringExtra("driverFlag");

        try {
            Class<? extends Activity> targetActivity = Class.forName(accessClassName).asSubclass(Activity.class);
            driverActivity = targetActivity;
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    //............Rajesh
    private void submitBioAuthh(BioAuthRequest obj1) {
        showLoader();

        ApiInterface apiInterface = RetrofitInstance.retrofit().create(ApiInterface.class);
        apiInterface.setdata().enqueue(new Callback<Base64Response>() {
            @Override
            public void onResponse(Call<Base64Response> call, Response<Base64Response> response) {
                if (response.isSuccessful() && response.body() != null) {
                    byte[] data = Base64.decode(response.message(), Base64.DEFAULT);
                    try {
                        if (SdkConstants.applicationType.equals("CORE")) {
                            encodedUrl = new String(data, "UTF-8");
                        } else {
                            encodedUrl = "https://aeps-prod-gateway-as1-5pwajhaz.ts.gateway.dev/api/bioAuth";
                        }
//                        String encodedUrl = new String(data, "UTF-8");
                        submitBioAuth(obj1, encodedUrl);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }

            @Override
            public void onFailure(Call<Base64Response> call, Throwable t) {
                t.printStackTrace();
            }
        });


       /* AndroidNetworking.get("https://vpn.iserveu.tech/generate/v80")
                .setPriority(Priority.HIGH)
                .build()
                .getAsJSONObject(new JSONObjectRequestListener() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            JSONObject obj = new JSONObject(response.toString());
                            String key = obj.getString("hello");
                            System.out.println(">>>>-----" + key);
                            byte[] data = Base64.decode(key, Base64.DEFAULT);
                            if (SdkConstants.applicationType.equals("CORE")) {
                                encodedUrl = new String(data, "UTF-8");
                            } else {
                                encodedUrl = "https://aeps-prod-gateway-as1-5pwajhaz.ts.gateway.dev/api/bioAuth";
                            }
                            submitBioAuth(obj1, encodedUrl);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        } catch (UnsupportedEncodingException e) {
                            e.printStackTrace();
                        }
                    }

                    @Override
                    public void onError(ANError anError) {

                    }
                });*/
    }


    private void submitBioAuth(BioAuthRequest obj, String encodedUrl) {
        // showLoader();

        ApiInterface apiInterface = RetrofitInstance.retrofit().create(ApiInterface.class);
        apiInterface.getdata(encodedUrl, "Bearer " + SdkConstants.tokenFromCoreApp, obj).enqueue(new Callback<BioAuthResponse>() {
            @Override
            public void onResponse(Call<BioAuthResponse> call, Response<BioAuthResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    String status = response.body().getStatus();
                    if (status.equalsIgnoreCase("0")) {
                        //initView();
                        SdkConstants.bioauth = false;
                        Toast.makeText(UnifiedBioAuthActivity.this, "SUCCESS", Toast.LENGTH_SHORT).show();
                        startActivity(new Intent(UnifiedBioAuthActivity.this, UnifiedAepsActivity.class));
                        finish();
                    } else {
                        Toast.makeText(UnifiedBioAuthActivity.this, "FAILURE", Toast.LENGTH_SHORT).show();
                    }
                }
            }
            @Override
            public void onFailure(Call<BioAuthResponse> call, Throwable t) {
                t.printStackTrace();
            }
        });



       /* AndroidNetworking.post(encodedUrl)
                .setPriority(Priority.HIGH)
                .addJSONObjectBody(obj)
                .addHeaders("Authorization", "Bearer " + SdkConstants.tokenFromCoreApp)
                .build()
                .getAsJSONObject(new JSONObjectRequestListener() {
                    @Override
                    public void onResponse(JSONObject response) {
                        hideLoader();

                        try {
                            // profile_id,is_declaration
                            JSONObject obj = new JSONObject(response.toString());
                            String status = obj.getString("status");
                            if (status.equalsIgnoreCase("0")) {
                                //initView();
                                SdkConstants.bioauth = false;
                                Toast.makeText(UnifiedBioAuthActivity.this, "SUCCESS", Toast.LENGTH_SHORT).show();
                                startActivity(new Intent(UnifiedBioAuthActivity.this, UnifiedAepsActivity.class));
                                finish();
                            } else {
                                Toast.makeText(UnifiedBioAuthActivity.this, "FAILURE", Toast.LENGTH_SHORT).show();
                            }


                        } catch (JSONException e) {
                            e.printStackTrace();
                            hideLoader();
                        }*/


                   /* }

                    @Override
                    public void onError(ANError anError) {
                        anError.getErrorBody();
                        hideLoader();
                        Toast.makeText(UnifiedBioAuthActivity.this, "FAILURE", Toast.LENGTH_SHORT).show();

                    }
                });*/
    }

    public void showLoader() {
        if (loadingView == null) {
            loadingView = new ProgressDialog(UnifiedBioAuthActivity.this);
            loadingView.setCancelable(false);
            loadingView.setMessage("Please Wait..");
        }
        loadingView.show();
    }

    public void hideLoader() {
        try {
            if (loadingView != null) {
                loadingView.dismiss();
            }
        } catch (Exception e) {
        }
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1) {
            hideLoader();
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        this.finish();
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    protected void onResume() {
        super.onResume();
        hideKeyboard();

        if (flagFromDriver) {
            if (SdkConstants.RECEIVE_DRIVER_DATA.isEmpty() || SdkConstants.RECEIVE_DRIVER_DATA.equalsIgnoreCase("")) {

                two_fact_fingerprint.setEnabled(true);
                two_fact_submitButton.setEnabled(false);
            } else if (balanaceInqueryAadharNo.equalsIgnoreCase("") || balanaceInqueryAadharNo.isEmpty()) {
                balanceAadharNumber.setError("Enter Aadhar No.");
                fingerStrength();
            } else {
                fingerStrength();
                two_fact_fingerprint.setEnabled(false);
                two_fact_submitButton.setEnabled(true);
            }

        } else {
            checkPermission();
        }

    }

    public void hideKeyboard() {
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
    }


    @RequiresApi(api = Build.VERSION_CODES.M)
    public void checkPermission() {
        if (Build.VERSION.SDK_INT > 15) {
            final String[] permissions = {Manifest.permission.ACCESS_FINE_LOCATION};

            final List<String> permissionsToRequest = new ArrayList<>();
            for (String permission : permissions) {
                if (ActivityCompat.checkSelfPermission(UnifiedBioAuthActivity.this, permission) != PackageManager.PERMISSION_GRANTED) {
                    permissionsToRequest.add(permission);
                }
            }
            if (!permissionsToRequest.isEmpty()) {
                // ActivityCompat.requestPermissions(getActivity(), permissionsToRequest.toArray(new String[permissionsToRequest.size()]), REQUEST_CAMERA_PERMISSIONS);
                requestPermissions(new String[]{
                        Manifest.permission.ACCESS_FINE_LOCATION}, REQUEST_CAMERA_PERMISSIONS);

            } else {
                getMyLocation();
                //retriveAUTH();
            }
        }
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {

        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        int permissionLocation = ContextCompat.checkSelfPermission(UnifiedBioAuthActivity.this,
                Manifest.permission.ACCESS_FINE_LOCATION);
        if (permissionLocation == PackageManager.PERMISSION_GRANTED) {
            getMyLocation();
        } else {
            Toast.makeText(this, "Please accept the location permission", Toast.LENGTH_SHORT).show();

            if (!isFinishing()) {
                finish();
            }
        }
    }

    private synchronized void setUpGClient() {
        googleApiClient = new GoogleApiClient.Builder(this)
                .enableAutoManage(this, 0, this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();
        googleApiClient.connect();


    }

    @Override
    public void onLocationChanged(Location location) {
        mylocation = location;

        if (mylocation != null) {
            location_flag = true;
            Geocoder geocoder;
            List<Address> addresses;
            geocoder = new Geocoder(this, Locale.getDefault());
            double latitude = location.getLatitude();
            double longitude = location.getLongitude();
            Log.e("latitude", "latitude--" + latitude);
            try {
                Log.e("latitude", "inside latitude--" + latitude);
                addresses = geocoder.getFromLocation(latitude, longitude, 1);
                if (addresses != null && addresses.size() > 0) {
                    String address = addresses.get(0).getAddressLine(0);
                    String city = addresses.get(0).getLocality();
                    String state = addresses.get(0).getAdminArea();
                    String country = addresses.get(0).getCountryName();
                    postalCode = addresses.get(0).getPostalCode();
                    if (postalCode == null) {
                        postalCode = "751017";
                    }
                    getPin();
                }
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }

        }
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    public void onConnected(Bundle bundle) {
        checkPermission();
    }

    @Override
    public void onConnectionSuspended(int i) {
        //Do whatever you need
        //You can display a message here
    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {
        //You can display a message here
    }

    private void getMyLocation() {
        if (googleApiClient != null) {
            if (googleApiClient.isConnected()) {
                int permissionLocation = ContextCompat.checkSelfPermission(UnifiedBioAuthActivity.this,
                        Manifest.permission.ACCESS_FINE_LOCATION);
                if (permissionLocation == PackageManager.PERMISSION_GRANTED) {
                    mylocation = LocationServices.FusedLocationApi.getLastLocation(googleApiClient);
                    LocationRequest locationRequest = new LocationRequest();
                    locationRequest.setInterval(3000);
                    locationRequest.setFastestInterval(3000);
                    locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
                    LocationSettingsRequest.Builder builder = new LocationSettingsRequest.Builder()
                            .addLocationRequest(locationRequest);
                    builder.setAlwaysShow(true);
                    LocationServices.FusedLocationApi.requestLocationUpdates(googleApiClient, locationRequest, this);
                    PendingResult<LocationSettingsResult> result = LocationServices.SettingsApi
                            .checkLocationSettings(googleApiClient, builder.build());
                    result.setResultCallback(new ResultCallback<LocationSettingsResult>() {

                        @Override
                        public void onResult(LocationSettingsResult result) {
                            final Status status = result.getStatus();
                            switch (status.getStatusCode()) {
                                case LocationSettingsStatusCodes.SUCCESS:
                                    // All location settings are satisfied.
                                    // You can initialize location requests here.
                                    int permissionLocation = ContextCompat
                                            .checkSelfPermission(UnifiedBioAuthActivity.this,
                                                    Manifest.permission.ACCESS_FINE_LOCATION);
                                    if (permissionLocation == PackageManager.PERMISSION_GRANTED) {
                                        mylocation = LocationServices.FusedLocationApi
                                                .getLastLocation(googleApiClient);
                                    }
                                    break;
                                case LocationSettingsStatusCodes.RESOLUTION_REQUIRED:
                                    // Location settings are not satisfied.
                                    // But could be fixed by showing the user a dialog.
                                    try {
                                        // Show the dialog by calling startResolutionForResult(),
                                        // and check the result in onActivityResult().
                                        // Ask to turn on GPS automatically
                                        status.startResolutionForResult(UnifiedBioAuthActivity.this,
                                                REQUEST_CHECK_SETTINGS_GPS);
                                    } catch (IntentSender.SendIntentException e) {
                                        // Ignore the error.
                                    }
                                    break;
                                case LocationSettingsStatusCodes.SETTINGS_CHANGE_UNAVAILABLE:
                                    // Location settings are not satisfied.
                                    // However, we have no way
                                    // to fix the
                                    // settings so we won't show the dialog.
                                    // finish();
                                    break;
                            }
                        }
                    });
                }
            }
        }
    }


    public void getPin() {
        if (mylocation != null) {
            // updateLocationInterface.onLocationUpdate(location);
            location_flag = true;

            Geocoder geocoder;
            List<Address> addresses;
            geocoder = new Geocoder(this, Locale.getDefault());
            double latitude = mylocation.getLatitude();
            double longitude = mylocation.getLongitude();
            Log.e("latitude", "latitude--" + latitude);
            try {
                Log.e("latitude", "inside latitude--" + latitude);
                addresses = geocoder.getFromLocation(latitude, longitude, 1);

                if (addresses != null && addresses.size() > 0) {
                    postalCode = addresses.get(0).getPostalCode();
                    if (postalCode == null) {
                        postalCode = "751017";
                    }
                }
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }

        }
    }
    //--------------

    private void checkAddressStatus() {
        ApiInterface apiInterface = RetrofitInstance.retrofit().create(ApiInterface.class);
        apiInterface.getViewUserPropAddress().enqueue(new Callback<Base64Response>() {
            @Override
            public void onResponse(Call<Base64Response> call, Response<Base64Response> response) {
                if (response.isSuccessful() && response.body() != null) {
                    byte[] data = Base64.decode(response.body().getHello(), Base64.DEFAULT);
                    try {
                        if (SdkConstants.applicationType.equals("CORE")) {
                            encodedUrl = new String(data, "UTF-8");
                        } else {
                            encodedUrl = "https://aeps-prod-gateway-as1-5pwajhaz.ts.gateway.dev/api/viewUserPropAddress/" + SdkConstants.userNameFromCoreApp;
                        }
                        viewUserPropAddress(encodedUrl);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                }
            }

            @Override
            public void onFailure(Call<Base64Response> call, Throwable t) {
                t.printStackTrace();
                hideLoader();
                Toast.makeText(UnifiedBioAuthActivity.this, "Service is unavailable for this user, please contact our help desk for details.", Toast.LENGTH_LONG).show();
            }
        });
    }

    private void viewUserPropAddress(String url) {

        ApiInterface apiInterface = RetrofitInstance.retrofit().create(ApiInterface.class);
        apiInterface.getdecodedViewUserPropAddress(url, "Bearer " + SdkConstants.tokenFromCoreApp).enqueue(new Callback<AadharVerificationResponse>() {
            @Override
            public void onResponse(Call<AadharVerificationResponse> call, Response<AadharVerificationResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    try {
                        if (!response.body().getResponse().getBioauth()) {
                            String pin = response.body().getResponse().getPincode();
                            if (pin.length() != 0) {
                                int pincode = Integer.valueOf(pin);
                                getAddressFromPin(pincode);
                            }
                        } else {
                            hideLoader();
                            sendAEPS2Intent(true);
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                        hideLoader();
                        Toast.makeText(UnifiedBioAuthActivity.this, "Something went wrong, please contact our help desk for details.", Toast.LENGTH_LONG).show();
                    }
                } else {
                    hideLoader();
                    response.errorBody();
                    Log.e("UNIFIED", "unified aeps bioauth view address == " + response.errorBody());
                    String errorStr = String.valueOf(response.errorBody());
                    try {
                        JSONObject obj = new JSONObject(response.errorBody().string());
                        Log.e("UNIFIED", "unified aeps bioauth view address == " + obj);
                        String status = obj.getString("status");
                        if (status.equalsIgnoreCase("-1")) {
                            String sttsDesc = obj.getString("statusDesc");
                            //Toast.makeText(BioAuthActivity.this, sttsDesc, Toast.LENGTH_LONG).show();
                            showAlert(sttsDesc);

                        } else if (status.equalsIgnoreCase("400")) {
                            String message = "";
                            message = obj.optString("message");
                            //Toast.makeText(BioAuthActivity.this, message, Toast.LENGTH_LONG).show();
                            showAlert(message);

                        } else {
                            showAlert("Oops!! Server error.");
                            // Toast.makeText(BioAuthActivity.this, "Oops!! Server error.", Toast.LENGTH_LONG).show();

                        }

                    } catch (JSONException e) {
                        Log.e("UNIFIED", "unified aeps bioauth view address == " + e.toString());
                        e.printStackTrace();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }

            @Override
            public void onFailure(Call<AadharVerificationResponse> call, Throwable t) {
                t.printStackTrace();
                hideLoader();
                Toast.makeText(UnifiedBioAuthActivity.this, "Something went wrong, please contact our help desk for details.", Toast.LENGTH_LONG).show();
            }
        });

    }

    private void getAddressFromPin(int pin) {
        // showLoader();

        ApiInterface apiInterface = RetrofitInstance.getInstanceMaker("https://us-central1-iserveustaging.cloudfunctions.net/pincodeFetch/api/").create(ApiInterface.class);
        apiInterface.getPincodeData(new PinCodeRequest(pin)).enqueue(new Callback<PinCodeResponse>() {
            @Override
            public void onResponse(Call<PinCodeResponse> call, Response<PinCodeResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    String status = response.body().getData().getStatus();
                    if (status.equalsIgnoreCase("success")) {
                        Data data = response.body().getData();
                        String lat = "0.0";
                        String lng = "0.0";
                        if (mylocation != null) {
                            lat = String.valueOf(mylocation.getLatitude());
                            lng = String.valueOf(mylocation.getLongitude());
                        }
                        setAddress(new AadhaaarUpdateRequest(userNameStr, data.getData().getState(), String.valueOf(data.getData().getPincode()), data.getData().getCity(), lat + "," + lng, SdkConstants.API_USER_NAME_VALUE));
                    }
                }
            }

            @Override
            public void onFailure(Call<PinCodeResponse> call, Throwable t) {

            }
        });
    }

    private void setAddress(AadhaaarUpdateRequest itpl) {


        ApiInterface apiInterface = RetrofitInstance.retrofit().create(ApiInterface.class);
        apiInterface.setAddressEncodedUrl().enqueue(new Callback<Base64Response>() {
            @Override
            public void onResponse(Call<Base64Response> call, Response<Base64Response> response) {
                if (response.isSuccessful() && response.body() != null) {
                    byte[] data = Base64.decode(response.message(), Base64.DEFAULT);
                    try {
                        String encodedurl = new String(data, "UTF-8");
                        updateUserPropAddress(itpl, encodedurl);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }

            }

            @Override
            public void onFailure(Call<Base64Response> call, Throwable t) {

            }
        });
    }

    private void updateUserPropAddress(AadhaaarUpdateRequest obj, String url) {

        ApiInterface apiInterface = RetrofitInstance.retrofit().create(ApiInterface.class);
        apiInterface.getAadhaarUpdate(url, "Bearer " + SdkConstants.tokenFromCoreApp, obj).enqueue(new Callback<AddressUpdateResponse>() {
            @Override
            public void onResponse(Call<AddressUpdateResponse> call, Response<AddressUpdateResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    String status = response.body().getStatus();
                    String statusDesc = response.body().getStatusDesc();
                    if (status.equalsIgnoreCase("0")) {
                        bio_ll.setVisibility(View.VISIBLE);
                    } else {
                        Toast.makeText(UnifiedBioAuthActivity.this, statusDesc, Toast.LENGTH_SHORT).show();
                        finish();
                    }
                }

            }

            @Override
            public void onFailure(Call<AddressUpdateResponse> call, Throwable t) {

            }
        });
    }

    public void sendAEPS2Intent(boolean bioauth) {
        SdkConstants.bioauth = bioauth;
        Intent intent = new Intent(UnifiedBioAuthActivity.this, UnifiedAepsActivity.class);
        startActivityForResult(intent, SdkConstants.REQUEST_CODE);
        finish();
    }

    /*private void getUserAuthToken() {
        showLoader();
        String url = SdkConstants.BASE_URL + "/api/getAuthenticateData";
        //String url = "https://newapp.iserveu.online/AEPS2NEW"+"/api/getAuthenticateData";
        JSONObject obj = new JSONObject();
        try {
            obj.put("encryptedData", SdkConstants.encryptedData);
            obj.put("retailerUserName", SdkConstants.loginID);

            AndroidNetworking.post(url)
                    .setPriority(Priority.HIGH)
                    .addJSONObjectBody(obj)
                    .build()
                    .getAsJSONObject(new JSONObjectRequestListener() {
                        @Override
                        public void onResponse(JSONObject response) {
                            try {
                                JSONObject obj = new JSONObject(response.toString());
                                String status = obj.getString("status");

                                if (status.equalsIgnoreCase("success")) {

                                    userNameStr = obj.getString("username");
                                    String userToken = obj.getString("usertoken");
                                    session.setUsername(userNameStr);
                                    session.setUserToken(userToken);
//                                    hideLoader();
                                    checkAddressStatus();

                                } else {
                                    showAlert(status);
                                    hideLoader();
                                }

                            } catch (JSONException e) {
                                e.printStackTrace();
                                hideLoader();
                                showAlert("Invalid Encrypted Data");
                            }
                        }

                        @Override
                        public void onError(ANError anError) {
                            hideLoader();

                        }

                    });
        } catch (Exception e) {
            hideLoader();
            e.printStackTrace();
        }
    }*/

    public void showAlert(String msg) {
        try {
            AlertDialog.Builder builder = new AlertDialog.Builder(UnifiedBioAuthActivity.this);
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

        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    public void fingerStrength() {
        try {
            JSONObject respObj = new JSONObject(SdkConstants.RECEIVE_DRIVER_DATA);
            String scoreStr = respObj.getString("pidata_qscore");

            String[] scoreList = scoreStr.split(",");
            scoreStr = scoreList[0];
            if (scoreStr.equalsIgnoreCase("")) {
                hideLoader();
                showAlert("Invalid Fingerprint Data");
            } else {
                two_fact_submitButton.setEnabled(true);
                two_fact_submitButton.setBackgroundResource(R.drawable.button_submit_blue);
                if (Float.parseFloat(scoreStr) <= 40) {
                    depositBar.setVisibility(View.VISIBLE);
                    depositBar.setProgress(Float.parseFloat(scoreStr));
                    depositBar.setProgressTextMoved(true);
                    depositBar.setEndColor(getResources().getColor(R.color.red));
                    depositBar.setStartColor(getResources().getColor(R.color.red));
                } else {
                    depositBar.setVisibility(View.VISIBLE);
                    depositBar.setProgress(Float.parseFloat(scoreStr));
                    depositBar.setProgressTextMoved(true);
                    depositBar.setEndColor(getResources().getColor(R.color.green));
                    depositBar.setStartColor(getResources().getColor(R.color.green));
                }
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

}
