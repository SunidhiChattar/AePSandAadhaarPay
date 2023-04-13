package com.matm.matmsdk.posverification;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.provider.Settings;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import com.androidnetworking.AndroidNetworking;
import com.androidnetworking.common.Priority;
import com.androidnetworking.error.ANError;
import com.androidnetworking.interfaces.JSONObjectRequestListener;
import com.matm.matmsdk.MPOS.MorefunServiceActivity;
import com.matm.matmsdk.MPOS.POSTransactionDetailsActivity;
import com.matm.matmsdk.MPOS.PosActivity;
import com.matm.matmsdk.Model.MPOS.BankSearchUserResponseModel;
import com.matm.matmsdk.Model.MPOS.GethAuthRequestModel;
import com.matm.matmsdk.Model.MPOS.GethAuthResponseModel;
import com.matm.matmsdk.Model.MPOS.TestUrlApi;
import com.matm.matmsdk.Model.otpmodel.GetOtpServiceRequestModel;
import com.matm.matmsdk.Model.otpmodel.GetOtpServiceResponseModel;
import com.matm.matmsdk.Model.otpmodel.MessagedataEntity;
import com.matm.matmsdk.PosOtpActivity;
import com.matm.matmsdk.Utils.EnvData;
import com.matm.matmsdk.Utils.SdkConstants;
import com.matm.matmsdk.aepsmodule.utils.Session;
import com.matm.matmsdk.posverification.responsemodel.BankDetailsEntity;
import com.matm.matmsdk.posverification.responsemodel.MerchantOnboardRequestModel;
import com.matm.matmsdk.posverification.responsemodel.MerchantOnboardResponseModel;
import com.matm.matmsdk.posverification.responsemodel.PosFetchAccountResponseModel;
import com.matm.matmsdk.posverification.retrofit.ApiService;
import com.matm.matmsdk.posverification.retrofit.RetrofitInstance;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import isumatm.androidsdk.equitas.R;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class POSSettlementAccountActivity extends AppCompatActivity {
    private static final String TAG = POSSettlementAccountActivity.class.getSimpleName();
    private static final int TRIGGER_AUTO_COMPLETE = 100;
    private static final long AUTO_COMPLETE_DELAY = 300;
    private final ArrayList<String> stringArrayList = new ArrayList<>();
    private final String panImageURL = "";
    private final String frontAadhaarImageURL = "";
    private final String backAadhaarImageURL = "";
    private final String selfieImageURL = "";
    private final String geoTaggedImageURL = "";
    public int bankListSize;
    Session session;
    TextView pincode_TV;
    EditText benificiary_name, ifscode, acc_no, cnf_acc_no, pincode;
    AutoCompleteTextView bank_name;
    Button submit;
    String name = "", shop = "", address = "", state = "", city = "", pin = "", refCode = "",
            dob = "", mail = "", panNumber = "", otp = "", phone = "", aadhaarNumber = "", pincode_str = "",
            username = "", password = "", createdBy = "", beni_name, beni_bank, beni_ifscode, beni_accno,
            cnf_beni_bank, shopType = "", merchantType = "", gstNo = "", annualIncome = "", fetch_beni_name = "",
            beni_ifsc_code = "", beni_bank_name = "", beni_acccountNo = "", beni_bankCode = "";
    boolean isEmailAvailable, isMobileAvailable, isPanAvailable, isAadhaarAvailable, isBasicInfoAvailable;
    Uri panImageUri, aadhaarFrontImageUri, aadhaarBackImageUri, selfieImageUri, geoTaggedImageUri;
    String bankName;
    String userName;
    String saltStr;
    String userEmail;
    String adminName;
    String userMobileNo;
    String IFSCCode, AccountNo, AcHolderName;
    android.app.Dialog dialogCondition;
    private AutoSuggestAdapter autoSuggestAdapter;
    private Handler handler;
    private ProgressDialog progressLoadingDialog;
    private AccountListAdapter accountListAdapter;
    private RecyclerView.LayoutManager layoutManager;
    private ArrayList<BankDetailsEntity> fetchAccountResponseModelArrayList = new ArrayList<>();
    private BankDetailsEntity bankDetailsEntity;
    private String _token;
    private String accountType = "";
    private boolean is_new_existing = false;
    private Spinner spSelectBank;
    private String deviceId;
    private CheckBox check_accept;
    private MessagedataEntity messagedataEntity;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settelment_account_info);
        session = new Session(POSSettlementAccountActivity.this);
        progressLoadingDialog = new ProgressDialog(this);

        deviceId = Settings.Secure.getString(getContentResolver(), Settings.Secure.ANDROID_ID);


        benificiary_name = findViewById(R.id.etBenificiary_Name);
        bank_name = findViewById(R.id.Bank_Name);
        ifscode = findViewById(R.id.etIFSC_code);
        acc_no = findViewById(R.id.ac_no);
        cnf_acc_no = findViewById(R.id.confrim_ACNO);
        pincode = findViewById(R.id.pincode_ET);
        pincode_TV = findViewById(R.id.pincode_TV);
        submit = findViewById(R.id.login_proceed);
        spSelectBank = findViewById(R.id.spSelectBank);
        check_accept = findViewById(R.id.check_accept);
        userEmail = SdkConstants.USER_EMAIL;
        adminName = SdkConstants.ADMIN_NAME;
        userMobileNo = SdkConstants.USER_MOBILE_NO;
        userName = SdkConstants.userNameFromCoreApp;
        messagedataEntity = new MessagedataEntity();
        messagedataEntity.setOtp("");
        getTransactionID();

        if (SdkConstants.applicationType.equalsIgnoreCase("CORE")) {
            session.setUserToken(SdkConstants.tokenFromCoreApp);
            session.setUsername(SdkConstants.userNameFromCoreApp);
            SdkConstants.isSL = false;
            FetchAccountApiCall();
        } else {
            SdkConstants.isSL = true;
            getUserAuthToken();
        }

        bank_name.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int
                    count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before,
                                      int count) {
                handler.removeMessages(TRIGGER_AUTO_COMPLETE);
                handler.sendEmptyMessageDelayed(TRIGGER_AUTO_COMPLETE,
                        AUTO_COMPLETE_DELAY);
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        //_token = user.get(SessionManager.KEY_TOKEN);
        autoSuggestAdapter = new AutoSuggestAdapter(this, R.layout.newdmt_row_bank_list);
        bank_name.setThreshold(1);
        bank_name.setAdapter(autoSuggestAdapter);
        handler = new Handler(new Handler.Callback() {
            @Override
            public boolean handleMessage(Message msg) {
                if (msg.what == TRIGGER_AUTO_COMPLETE) {
                    if (!TextUtils.isEmpty(bank_name.getText())) {
                        bankSearchUser(bank_name.getText().toString());
                        //makeApiCall(autoCompleteTextView.getText().toString());
                    }
                }
                return false;
            }
        });


        spSelectBank.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

                if (position == 0) {
                    //Do Nothing
                } else {
                        if (stringArrayList.get(position).equalsIgnoreCase("Add Account")) {
                            ifscode.setText("");
                            benificiary_name.setText("");
                            acc_no.setText("");
                            cnf_acc_no.setText("");
                            bank_name.setText("");
                            pincode.setText("");

                            is_new_existing=true;
                            pincode.setVisibility(View.VISIBLE);
                            pincode_TV.setVisibility(View.VISIBLE);

                            benificiary_name.setEnabled(true);
                            benificiary_name.setTextIsSelectable(true);
                            bank_name.setEnabled(true);
                            bank_name.setTextIsSelectable(true);
                            ifscode.setEnabled(true);
                            ifscode.setTextIsSelectable(true);
                            acc_no.setEnabled(true);
                            acc_no.setTextIsSelectable(true);
                            cnf_acc_no.setEnabled(true);
                            cnf_acc_no.setTextIsSelectable(true);
                            pincode.setEnabled(true);
                            pincode.setTextIsSelectable(true);
                            submit.setText("Generate OTP");

                        } else {
                            bankName = fetchAccountResponseModelArrayList.get(position - 1).getBankname();
                            IFSCCode = fetchAccountResponseModelArrayList.get(position - 1).getIfsccode();
                            AcHolderName = fetchAccountResponseModelArrayList.get(position - 1).getAccountholdername();
                            AccountNo = fetchAccountResponseModelArrayList.get(position - 1).getAccountnumber();
                            ifscode.setText(IFSCCode);
                            benificiary_name.setText(AcHolderName);
                            acc_no.setText(AccountNo);
                            cnf_acc_no.setText(AccountNo);
                            bank_name.setText(bankName);
                            benificiary_name.setEnabled(false);
                            benificiary_name.setTextIsSelectable(false);
                            bank_name.setEnabled(false);
                            bank_name.setTextIsSelectable(false);
                            ifscode.setEnabled(false);
                            ifscode.setTextIsSelectable(false);
                            acc_no.setEnabled(false);
                            acc_no.setTextIsSelectable(false);
                            cnf_acc_no.setEnabled(false);
                            cnf_acc_no.setTextIsSelectable(false);
                            pincode.setEnabled(false);
                            pincode.setTextIsSelectable(false);
                            pincode.setVisibility(View.GONE);
                            pincode_TV.setVisibility(View.GONE);
                            submit.setText("Submit");
                            is_new_existing = false;


                        }



                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });


        check_accept.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    TermsAndConditionsDialogPopup();
                }
            }
        });


        bank_name.setOnItemClickListener(
                new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view,
                                            int position, long id) {

                        bank_name.setText(autoSuggestAdapter.getObject(position).bank_name);
                        if (autoSuggestAdapter.getObject(position).ifsc_mode.equalsIgnoreCase("U")) {
                            ifscode.setText(autoSuggestAdapter.getObject(position).ifsc_code);
                            ifscode.setEnabled(true);
                        } else if (autoSuggestAdapter.getObject(position).ifsc_mode.equals("") || autoSuggestAdapter.getObject(position).ifsc_mode.equals(null)) {
                            ifscode.setText("");
                            ifscode.setEnabled(true);
                        } else if (autoSuggestAdapter.getObject(position).ifsc_mode.equalsIgnoreCase("F4")) {
                            String first4 = "";
                            try {
                                first4 = acc_no.getText().toString().substring(0, 4);
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                            ifscode.setText(autoSuggestAdapter.getObject(position).ifsc_code + first4);
                            ifscode.setEnabled(true);
                        } else if (autoSuggestAdapter.getObject(position).ifsc_mode.equalsIgnoreCase("3")) {
                            String first3 = "";
                            try {
                                first3 = acc_no.getText().toString().substring(0, 3);
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                            ifscode.setText(autoSuggestAdapter.getObject(position).ifsc_code + first3);
                            ifscode.setEnabled(true);
                        } else if (autoSuggestAdapter.getObject(position).ifsc_mode.equalsIgnoreCase("6")) {
                            String first6 = "";
                            try {
                                first6 = acc_no.getText().toString().substring(0, 6);
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                            ifscode.setText(autoSuggestAdapter.getObject(position).ifsc_code + first6);
                            ifscode.setEnabled(true);
                        }


                    }
                });


        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                hideKeyboard();
                getTransactionID();
                createdBy = username;
                username = SdkConstants.userNameFromCoreApp;
                beni_name = benificiary_name.getText().toString();
                beni_bank = bank_name.getText().toString();
                beni_ifscode = ifscode.getText().toString();
                if (is_new_existing) {
                    pincode_str = pincode.getText().toString();

                }
                beni_ifsc_code = beni_ifscode;
                beni_accno = acc_no.getText().toString();
                cnf_beni_bank = cnf_acc_no.getText().toString();
                pincode_str = pincode.getText().toString();
                if (!Validate.isValidName(beni_name)) {
                    Validate.showAlert(POSSettlementAccountActivity.this, "Enter a valid benificiary name.");
                } else if (beni_bank.trim().equals("")) {
                    Validate.showAlert(POSSettlementAccountActivity.this, "Enter a valid bank name.");
                } else if (!Validate.isValidIfscCode(beni_ifscode)) {
                    Validate.showAlert(POSSettlementAccountActivity.this, "Enter a valid ifsc code.");
                } else if (beni_accno.trim().equals("")) {
                    Validate.showAlert(POSSettlementAccountActivity.this, "Enter a valid account no.");
                } else if (cnf_beni_bank.trim().equals("")) {
                    Validate.showAlert(POSSettlementAccountActivity.this, "Enter a valid account no.");
                } else if (is_new_existing && pincode_str.trim().equals("") && pincode_str.length() < 6) {
                        Validate.showAlert(POSSettlementAccountActivity.this, "Enter a valid Pincode");
                } else if (spSelectBank.getSelectedItem().toString().equals("Select Bank")) {
                    Toast.makeText(POSSettlementAccountActivity.this, "Please Select a bank", Toast.LENGTH_SHORT).show();
                } else if (!beni_accno.equals(cnf_beni_bank)) {
                    Validate.showAlert(POSSettlementAccountActivity.this, "Account no and Confirm Account no should be same.");
                } else {

                    if (is_new_existing == false) {
                        accountType = "existing";
                        // call api here
                        initiateMerchantOnboardApicall();
                    } else {
                        otpVerificationApiCall();


                    }
                }
            }
        });

    }

    private void TermsAndConditionsDialogPopup() {
        dialogCondition = new Dialog(POSSettlementAccountActivity.this);

        dialogCondition.setContentView(R.layout.term_conditions_dilog_popup);
        TextView tv_date = dialogCondition.findViewById(R.id.tv_date);
        TextView tv_nameOfRetailer = dialogCondition.findViewById(R.id.tv_nameOfRetailer);
        TextView tv_retailer = dialogCondition.findViewById(R.id.tv_retailer);
        Button btn_accept = dialogCondition.findViewById(R.id.btn_accept);
        Button btn_cancel = dialogCondition.findViewById(R.id.btn_cancel);
       /* tv_date.setText("Date : " + formattedDate);
        tv_nameOfRetailer.setText("Name of Retailer : " + fullname);*/
        tv_retailer.setText("Retailer ID : " + username);
        btn_cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialogCondition.dismiss();
                check_accept.setChecked(false);
            }
        });
        btn_accept.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                check_accept.setChecked(true);
                dialogCondition.dismiss();
            }
        });
        dialogCondition.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialogCondition.setCancelable(true);
        dialogCondition.show();
    }

    private void otpVerificationApiCall() {
        progressLoadingDialog.setMessage("Please wait");
        progressLoadingDialog.show();
        GetOtpServiceRequestModel getOtpServiceRequestModel = new GetOtpServiceRequestModel(messagedataEntity,
                saltStr, "", userEmail, userMobileNo, "OTP", "OTP", adminName, userName);
        getOtpServiceRequestModel.setMessagedata(messagedataEntity);
        ApiService apiService = RetrofitInstance.otpServiceInstanceMaker().create(ApiService.class);
        Call<GetOtpServiceResponseModel> otpServiceResponseModelCall = apiService.getOtpServiceResponse(getOtpServiceRequestModel);
        otpServiceResponseModelCall.enqueue(new Callback<GetOtpServiceResponseModel>() {
            @Override
            public void onResponse(@NonNull Call<GetOtpServiceResponseModel> call, @NonNull Response<GetOtpServiceResponseModel> response) {
                if (response.body() != null) {
                    progressLoadingDialog.dismiss();
                    if (response.body().getStatus() == 1) {
                        accountType = "new";
                        Intent intent = new Intent(POSSettlementAccountActivity.this, PosOtpActivity.class);
                        intent.putExtra("beneName", benificiary_name.getText().toString());
                        intent.putExtra("beneBankCode", beni_bankCode);
                        intent.putExtra("beneIfscCode", beni_ifsc_code);
                        intent.putExtra("beneBank", beni_bank);
                        intent.putExtra("beneAccNo", beni_accno);
                        intent.putExtra("beneAccType", accountType);
                        intent.putExtra("params", saltStr);
                        intent.putExtra("pincode", pincode_str);
                        startActivity(intent);
                    } else {
                        Toast.makeText(POSSettlementAccountActivity.this, response.body().getMessage(), Toast.LENGTH_SHORT).show();
                        progressLoadingDialog.dismiss();
                    }
                } else {
                    progressLoadingDialog.dismiss();
                }
            }

            @Override
            public void onFailure(@NonNull Call<GetOtpServiceResponseModel> call, @NonNull Throwable t) {
                progressLoadingDialog.dismiss();
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
        MerchantOnboardRequestModel initiateMerchantRequestModel = new MerchantOnboardRequestModel(benificiary_name.getText().toString(), beni_bankCode, beni_ifsc_code, beni_bank, beni_accno, username, accountType, otp, pincode_str, saltStr);
        ApiService apiService = RetrofitInstance.initiateMerchantOnboardInstanceMaker().create(ApiService.class);
        Call<MerchantOnboardResponseModel> initiateMerchantResponseModelCall = apiService.getMerchantOnboardResponse(initiateMerchantRequestModel, SdkConstants.tokenFromCoreApp);
        initiateMerchantResponseModelCall.enqueue(new Callback<MerchantOnboardResponseModel>() {
            @Override
            public void onResponse(@NonNull Call<MerchantOnboardResponseModel> call, @NonNull Response<MerchantOnboardResponseModel> response) {
                if (response.body() != null) {
                    progressLoadingDialog.dismiss();
                    int status = response.body().getStatus();
                    String message = response.body().getMessage();

                    if (status == 1) {

                        Intent intent = new Intent(POSSettlementAccountActivity.this, POSTransactionDetailsActivity.class);
                        startActivity(intent);
                        finish();

                    } else if (status == 0) {
                        AlertDialog.Builder builder = new AlertDialog.Builder(POSSettlementAccountActivity.this);
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


    /**
     * hide keyboard pop-up
     */
    public void hideKeyboard() {
        View view = this.getCurrentFocus();
        if (view != null) {
            InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
    }


    //ELASTIC API_CALL for Bank Search

    private void bankSearchUser(String keyword) {
        Call<BankSearchUserResponseModel> fetchUser = RetrofitInstance.bankSearchUser().create(TestUrlApi.class).getBankSearchUserResponse();
        fetchUser.enqueue(new Callback<BankSearchUserResponseModel>() {
            @Override
            public void onResponse(Call<BankSearchUserResponseModel> call, Response<BankSearchUserResponseModel> response) {
                BankSearchUserResponseModel bankSearchUserResponseModel = response.body();
                if (bankSearchUserResponseModel != null) {
                    progressLoadingDialog.dismiss();
                    try {
                        String key = bankSearchUserResponseModel.getHello();
                        System.out.println(">>>>-----" + key);
                        byte[] data = Base64.decode(key, Base64.DEFAULT);
                        String decodedUrlBankSearch = new String(data, StandardCharsets.UTF_8);
                        encryptedUrlBankSearch(keyword, decodedUrlBankSearch);
                    } catch (Exception e) {
                            e.printStackTrace();
                        }
                }
            }
            @Override
            public void onFailure(Call<BankSearchUserResponseModel> call, Throwable t) {
                Toast.makeText(POSSettlementAccountActivity.this, t.getMessage(), Toast.LENGTH_LONG).show();
            }
        });


    }

    //ELASTIC API_CALL for Bank Search
    public void encryptedUrlBankSearch(String keyword, String encodedUrlBankSearch) {
        String fields = "[\"bank_name\"]";
        try {
            JSONObject obj = new JSONObject();
            obj.put("bank_name", keyword);
            Log.d("BANK_SEARCH", "Before API Call begin");
            AndroidNetworking.post(encodedUrlBankSearch)
                    .setPriority(Priority.HIGH)
                    .addJSONObjectBody(obj)
                    .addHeaders("Authorization", "Basic ZWxhc3RpYzpUQWhJamJ4U2RzRzRRRDY3WWVmZTZQdzg=")
                    .build()
                    .getAsJSONObject(new JSONObjectRequestListener() {
                        @Override
                        public void onResponse(JSONObject response) {
                            List<ModelBankSearch> stringList = new ArrayList<>();
                            try {
                                JSONObject obj = new JSONObject(response.toString());
                                JSONArray arrHits = obj.getJSONArray("data");
                                Log.d("BANK_SEARCH", "OnResponse, Total hits: " + arrHits.length());
                                for (int i = 0; i < arrHits.length(); i++) {
                                    JSONObject objHits = arrHits.getJSONObject(i);
                                    ModelBankSearch modelBankSearch = new ModelBankSearch();
                                    modelBankSearch.bank_name = objHits.optString("bank_name");
                                    modelBankSearch.ifsc_code = objHits.optString("ifsc_code");
                                    modelBankSearch.ifsc_mode = objHits.optString("ifsc_mode");
                                    modelBankSearch.dmt_enable = objHits.optBoolean("dmt_enable");
                                    modelBankSearch.dmt_imps_enable = objHits.optBoolean("dmt_imps_enable");
                                    modelBankSearch.dmt_neft_enable = objHits.optBoolean("dmt_neft_enable");
                                    modelBankSearch.bank_code = objHits.optString("bank_code");
                                    // BANK_CODE= obj.optString("bank_code");
                                    Log.e("TAG", "Response : obj " + objHits.toString());
                                    stringList.add(modelBankSearch);
                                }
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                            //IMPORTANT: set data here and notify
                            autoSuggestAdapter.setData(stringList);
                            autoSuggestAdapter.notifyDataSetChanged();
                        }

                        @Override
                        public void onError(ANError anError) {
                            //Show Toast message here
                            Log.d("BANK_SEARCH", "OnError");
                        }
                    });
        } catch (Exception e) {
            e.printStackTrace();
            //Handle exception here
        }
    }
/*
    public void bankSearchUser(String keyword) {

        String fields = "[\"bank_code\",\"bank_name\"]";

        JSONObject obj = new JSONObject();
        JSONObject objQuery = new JSONObject();
        JSONObject objBool = new JSONObject();
        JSONObject objQuery2 = new JSONObject();
        JSONObject objMust = new JSONObject();
        JSONObject objDMT = new JSONObject();
        JSONObject objMatch = new JSONObject();
        JSONObject objfilter = new JSONObject();
        JSONObject objMulti_match = new JSONObject();
        try {

            objQuery2.put("query", true);
            objDMT.put("dmt", objQuery2);
            objMatch.put("match", objDMT);
            objMulti_match.put("query", keyword);
            objMulti_match.put("type", "phrase_prefix");
            objMulti_match.put("fields", new JSONArray(fields));
            objMulti_match.put("lenient", true);
            objfilter.put("multi_match", objMulti_match);
            objBool.put("must", new JSONArray().put(objMatch));
            objBool.put("filter", objfilter);
            objQuery.put("bool", objBool);

            obj.put("size", 1000);
            obj.put("query", objQuery);

            Log.d("BANK_SEARCH", "Before API Call begin");
            String BANK_SEARCH = "https://b61295e3bdc84097ac04e0456792cac1.us-central1.gcp.cloud.es.io:9243/isu_bank/_search";

            AndroidNetworking.post(BANK_SEARCH)
                    .setPriority(Priority.HIGH)
                    .addJSONObjectBody(obj)
                    .addHeaders("Authorization", "Basic ZWxhc3RpYzpUQWhJamJ4U2RzRzRRRDY3WWVmZTZQdzg=")
                    .build()
                    .getAsJSONObject(new JSONObjectRequestListener() {
                        @Override
                        public void onResponse(JSONObject response) {
                            List<ModelBankSearch> stringList = new ArrayList<>();
                            try {
                                String hits = new JSONObject(
                                        new JSONObject(response.toString())
                                                .getString("hits"))
                                        .getString("hits");

                                JSONArray arrHits = new JSONArray(hits);
                                Log.d("BANK_SEARCH", "OnResponse, Total hits: " + arrHits.length());
                                for (int i = 0; i < arrHits.length(); i++) {
                                    JSONObject objHits = arrHits.getJSONObject(i);
                                    String _source = objHits.optString("_source");

                                    JSONObject obj = new JSONObject(_source);

                                    ModelBankSearch modelBankSearch = new ModelBankSearch();
                                    modelBankSearch.bank_name = obj.optString("bank_name");
                                    modelBankSearch.ifsc_code = obj.optString("ifsc_code");
                                    modelBankSearch.ifsc_mode = obj.optString("ifsc_mode");
                                    modelBankSearch.dmt_enable = obj.optBoolean("dmt_enable");
                                    modelBankSearch.dmt_imps_enable = obj.optBoolean("dmt_imps_enable");
                                    modelBankSearch.dmt_neft_enable = obj.optBoolean("dmt_neft_enable");
                                    modelBankSearch.bank_code = obj.optString("bank_code");
                                    beni_bankCode = obj.optString("bank_code");
                                    // BANK_CODE= obj.optString("bank_code");
                                    Log.e("TAG", "Response : obj " + obj.toString());

                                    stringList.add(modelBankSearch);

                                }

                            } catch (Exception e) {
                                e.printStackTrace();
                            }

                            //IMPORTANT: set data here and notify
                            autoSuggestAdapter.setData(stringList);
                            autoSuggestAdapter.notifyDataSetChanged();
                        }

                        @Override
                        public void onError(ANError anError) {
                            //Show Toast message here
                            Log.d("BANK_SEARCH", "OnError");

                        }
                    });
        } catch (Exception e) {
            e.printStackTrace();
            //Handle exception here
        }

    }
*/

    private void FetchAccountApiCall() {

        progressLoadingDialog.setCancelable(false);
        progressLoadingDialog.show();
        progressLoadingDialog.setMessage("Please Wait.....");

        ApiService apiService = RetrofitInstance.fetchAccountInstanceMaker().create(ApiService.class);
        Call<PosFetchAccountResponseModel> fetchAccountResponse = apiService.getFetchAccountResponse(session.getUserToken());
        fetchAccountResponse.enqueue(new Callback<PosFetchAccountResponseModel>() {
            @Override
            public void onResponse(@NonNull Call<PosFetchAccountResponseModel> call, @NonNull Response<PosFetchAccountResponseModel> response) {
                if (response.body() != null) {
                    int status = response.body().getResult().getStatus();
                    progressLoadingDialog.dismiss();
                    fetchAccountResponseModelArrayList = response.body().getBankDetailsEntityArrayList();
                    Log.e("Settelement", "response" + fetchAccountResponseModelArrayList);
                    setDataToSpinner(fetchAccountResponseModelArrayList);

                } else {
                    is_new_existing = true;
                }


            }

            @Override
            public void onFailure(@NonNull Call<PosFetchAccountResponseModel> call, @NonNull Throwable t) {
                progressLoadingDialog.dismiss();
            }
        });
    }


    private void getUserAuthToken() {
//        String url = SdkConstants.BASE_URL + "/api/getAuthenticateData";
//        JSONObject obj = new JSONObject();
//        try {
//            obj.put("encryptedData", SdkConstants.encryptedData);
//            obj.put("retailerUserName", SdkConstants.loginID);
//
//            AndroidNetworking.post(url)
//                    .setPriority(Priority.HIGH)
//                    .addJSONObjectBody(obj)
//                    .build()
//                    .getAsJSONObject(new JSONObjectRequestListener() {
//                        @Override
//                        public void onResponse(JSONObject response) {
//                            try {
//                                JSONObject obj = new JSONObject(response.toString());
//                                String status = obj.getString("status");
//
//                                if (status.equalsIgnoreCase("success")) {
//                                    String userName = obj.getString("username");
//                                    String userToken = obj.getString("usertoken");
//                                    SdkConstants.userNameFromCoreApp = userName;
//                                    session.setUsername(userName);
//                                    session.setUserToken(userToken);
//                                    progressLoadingDialog.dismiss();
//                                    FetchAccountApiCall();
//
//
//                                } else {
//                                    showAlert(status);
//                                    progressLoadingDialog.dismiss();
//                                }
//
//                            } catch (JSONException e) {
//                                e.printStackTrace();
//                                progressLoadingDialog.dismiss();
//                                showAlert("Invalid Encrypted Data");
//                            }
//                        }
//
//                        @Override
//                        public void onError(ANError anError) {
//                            progressLoadingDialog.dismiss();
//                        }
//
//                    });
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
        progressLoadingDialog.setMessage("Please Wait!");
        progressLoadingDialog.show();
        GethAuthRequestModel gethAuthRequestModel = new GethAuthRequestModel(SdkConstants.encryptedData,SdkConstants.loginID);
        Call<GethAuthResponseModel> getAuthResponse = RetrofitInstance.getUserAuthToken().create(ApiService.class)
                .getUserAuthTokenResponse(gethAuthRequestModel);
        getAuthResponse.enqueue(new Callback<GethAuthResponseModel>() {
            @Override
            public void onResponse(Call<GethAuthResponseModel> call, Response<GethAuthResponseModel> response) {

                GethAuthResponseModel gethAuthResponseModel = response.body();
                if(gethAuthResponseModel != null){
                    progressLoadingDialog.dismiss();
                    String status = gethAuthResponseModel.getStatus();
                    try {
                        if(status.equalsIgnoreCase("Success")) {
                            String username = gethAuthResponseModel.getUsername();
                            String userToken = gethAuthResponseModel.getUserToken();
                            session.setUsername(username);
                            session.setUserToken(userToken);
                            SdkConstants.userNameFromCoreApp = username;
                            FetchAccountApiCall();
                        } else {
                            showAlert(status);
                            progressLoadingDialog.dismiss();
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                        progressLoadingDialog.dismiss();
                        showAlert("Invalid Encrypted Data");
                    }
                }
            }

            @Override
            public void onFailure(Call<GethAuthResponseModel> call, Throwable t) {
                progressLoadingDialog.dismiss();
                Toast.makeText(POSSettlementAccountActivity.this, t.getMessage(), Toast.LENGTH_LONG).show();
            }
        });
    }


    public void showAlert(String msg) {

        AlertDialog.Builder builder = new AlertDialog.Builder(POSSettlementAccountActivity.this);
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

    private void setDataToSpinner(ArrayList<BankDetailsEntity> fetchAccountResponseModelArrayList) {
        if (fetchAccountResponseModelArrayList != null && fetchAccountResponseModelArrayList.size() <= 6) {
            stringArrayList.add("Select Bank");
            int length = fetchAccountResponseModelArrayList.size();
            for (int i = 0; i < length; i++) {
                BankDetailsEntity banDetail = fetchAccountResponseModelArrayList.get(i);
                stringArrayList.add(banDetail.getBankname() + " - " + banDetail.getAccountnumber());
            }

            if (stringArrayList.size() < 7) {
                stringArrayList.add("Add Account");
            } else {
                //do nothing
            }

            benificiary_name.setEnabled(false);
            benificiary_name.setTextIsSelectable(false);
            bank_name.setEnabled(false);
            bank_name.setTextIsSelectable(false);
            ifscode.setEnabled(false);
            ifscode.setTextIsSelectable(false);
            acc_no.setEnabled(false);
            acc_no.setTextIsSelectable(false);
            cnf_acc_no.setEnabled(false);
            cnf_acc_no.setTextIsSelectable(false);
            pincode.setEnabled(false);
            pincode.setTextIsSelectable(false);


            final ArrayAdapter<String> spinnerArrayAdapter = new ArrayAdapter<String>(
                    this, android.R.layout.simple_spinner_dropdown_item, stringArrayList) {
                @Override
                public boolean isEnabled(int position) {
                    return position != 0;
                }

                @Override
                public View getDropDownView(int position, View convertView,
                                            ViewGroup parent) {
                    View view = super.getDropDownView(position, convertView, parent);
                    TextView tv = (TextView) view;
                    if (position == 0) {
                        // Set the disable item text color
                        tv.setTextColor(Color.GRAY);
                    } else {
                        tv.setTextColor(Color.BLACK);
                    }
                    return view;
                }
            };
            spSelectBank.setAdapter(spinnerArrayAdapter);
        } else {

            stringArrayList.add("Select Bank");
            stringArrayList.add("Add Account");

            final ArrayAdapter<String> spinnerArrayAdapter = new ArrayAdapter<String>(
                    this, android.R.layout.simple_spinner_dropdown_item, stringArrayList) {
                @Override
                public boolean isEnabled(int position) {
                    return position != 0;
                }

                @Override
                public View getDropDownView(int position, View convertView,
                                            ViewGroup parent) {
                    View view = super.getDropDownView(position, convertView, parent);
                    TextView tv = (TextView) view;
                    if (position == 0) {
                        // Set the disable item text color
                        tv.setTextColor(Color.GRAY);
                    } else {
                        tv.setTextColor(Color.BLACK);
                    }
                    return view;
                }
            };

            spSelectBank.setAdapter(spinnerArrayAdapter);

        }


    }

}