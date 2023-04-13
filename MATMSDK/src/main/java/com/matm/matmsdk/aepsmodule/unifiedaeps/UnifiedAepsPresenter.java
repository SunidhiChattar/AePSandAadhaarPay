package com.matm.matmsdk.aepsmodule.unifiedaeps;

import android.content.Context;
import android.util.Base64;
import android.util.Log;
import android.widget.Toast;

import com.androidnetworking.AndroidNetworking;
import com.androidnetworking.common.Priority;
import com.androidnetworking.error.ANError;
import com.androidnetworking.interfaces.JSONObjectRequestListener;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonIOException;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.matm.matmsdk.Utils.SdkConstants;
import com.matm.matmsdk.aepsmodule.balanceenquiry.BalanceEnquiryAEPS2RequestModel;
import com.matm.matmsdk.aepsmodule.balanceenquiry.BalanceEnquiryAPI;
import com.matm.matmsdk.aepsmodule.balanceenquiry.BalanceEnquiryContract;
import com.matm.matmsdk.aepsmodule.balanceenquiry.BalanceEnquiryRequestModel;
import com.matm.matmsdk.aepsmodule.cashwithdrawal.AepsResponse;
import com.matm.matmsdk.aepsmodule.ministatement.StatementList_Adapter;
import com.matm.matmsdk.aepsmodule.ministatement.TransactionList;
import com.matm.matmsdk.aepsmodule.network.api.ApiInterface;
import com.matm.matmsdk.aepsmodule.network.api.RetrofitInstance;
import com.matm.matmsdk.aepsmodule.network.model.Base64Response;
import com.matm.matmsdk.aepsmodule.network.model.balanceEnquiry.BalanceEnquiryResponse;
import com.matm.matmsdk.aepsmodule.utils.AEPSAPIService;
import com.matm.matmsdk.aepsmodule.utils.Constants;
import com.matm.matmsdk.aepsmodule.utils.Session;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;

//import isumatm.androidsdk.equitas.BuildConfig;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class UnifiedAepsPresenter implements UnifiedAepsContract.UserActionsListener {
    /**
     * Initialize LoginView
     */
    private UnifiedAepsContract.View unifiedAepsContract;
    private AEPSAPIService aepsapiService;
    private Session session;
    private String encodedUrl;
    private String transactionTypeCW = "Cash Withdrawal";
    private String transactionTypeBE = "Request Balance";
    private String transactionTypeMS = "Mini Statement";
    private String apiVPNUrl = "";

    public UnifiedAepsPresenter(UnifiedAepsContract.View unifiedAepsContract) {
        this.unifiedAepsContract = unifiedAepsContract;
    }

    @Override
    public void performUnifiedResponse(Context context, String token, UnifiedAepsRequestModel unifiedAepsRequestModel, String transaction_type, int gatewayPriority) {
//        String baseUrl_SDKUser = BuildConfig.BASE_URL;
        String baseUrl_SDKUser = "https://aeps-prod-gateway-as1-5pwajhaz.ts.gateway.dev";
        String encodedUrl_demo = "";
        if (transaction_type.equals(transactionTypeCW)) {
            if (SdkConstants.applicationType.equalsIgnoreCase("CORE")) {
                apiVPNUrl = "https://unifiedaepsbeta.iserveu.tech/generate/v103";
            } else {
//                apiVPNUrl = "https://unifiedaepsbeta.iserveu.tech/generate/v106";
                encodedUrl_demo = baseUrl_SDKUser + "/api/cashWithdrawal";
                Log.e("BioAuth", baseUrl_SDKUser + "/api/cashWithdrawal");

            }
        } else if (transaction_type.equals(transactionTypeBE)) {
            if (SdkConstants.applicationType.equalsIgnoreCase("CORE")) {
                apiVPNUrl = "https://unifiedaepsbeta.iserveu.tech/generate/v102";
            } else {
//                apiVPNUrl = "https://unifiedaepsbeta.iserveu.tech/generate/v105";
                encodedUrl_demo = baseUrl_SDKUser + "/api/balanceEnquiry";
                Log.e("BioAuth", baseUrl_SDKUser + "/api/balanceEnquiry");

            }
        } else {
            if (SdkConstants.applicationType.equalsIgnoreCase("CORE")) {
                apiVPNUrl = "https://unifiedaepsbeta.iserveu.tech/generate/v104";
            } else {
//                apiVPNUrl = "https://unifiedaepsbeta.iserveu.tech/generate/v107";
                encodedUrl_demo = baseUrl_SDKUser + "/api/miniStatement";
                Log.e("BioAuth", baseUrl_SDKUser + "/api/miniStatement");

            }
        }

        if (SdkConstants.applicationType.equalsIgnoreCase("CORE")) {

            ApiInterface apiInterface = RetrofitInstance.retrofit().create(ApiInterface.class);
            apiInterface.getVPNStatusforall(apiVPNUrl).enqueue(new Callback<Base64Response>() {
                @Override
                public void onResponse(Call<Base64Response> call, Response<Base64Response> response) {
                    if (response.isSuccessful() && response.body() != null) {
                        String key = response.body().getHello();
                        byte[] data = Base64.decode(key, Base64.DEFAULT);
                        try {
                            String encodedUrl = new String(data, "UTF-8");
                            encryptBalanceEnquiry(context, token, unifiedAepsRequestModel, encodedUrl, gatewayPriority);
                        } catch (UnsupportedEncodingException e) {
                            e.printStackTrace();
                        }
                    }
                }
                @Override
                public void onFailure(Call<Base64Response> call, Throwable t) {
                    Log.v("UNIFIEDAEPS", t.getLocalizedMessage().toString());
                }
            });

        } else {
            String tokenValue = "Bearer " + token;
            encryptBalanceEnquiry(context, tokenValue, unifiedAepsRequestModel, encodedUrl_demo, gatewayPriority);
        }

    }

    public void encryptBalanceEnquiry(Context context, final String token, final UnifiedAepsRequestModel unifiedAepsRequestModel, String encodedUrl, int gatewayPriority) {
        JSONObject obj = new JSONObject();
        try {
            obj.put("latLong", unifiedAepsRequestModel.getLatLong());
            obj.put("paramC", "");
            obj.put("paramB","");
            obj.put("paramA", "");
            obj.put("retailer", "");
            obj.put("apiUser", unifiedAepsRequestModel.getApiUser());
            obj.put("apiUserName", UnifiedAepsRequestModel.getApiUserName());
            obj.put("mobileNumber", unifiedAepsRequestModel.getMobileNumber());
            obj.put("iin", unifiedAepsRequestModel.getIin());
            obj.put("aadharNo", unifiedAepsRequestModel.getAadharNo());
            obj.put("amount", unifiedAepsRequestModel.getAmount());
            obj.put("bankName", unifiedAepsRequestModel.getBankName());
            obj.put("gatewayPriority", gatewayPriority);

            String piddata = unifiedAepsRequestModel.getPidData();
            String pidata = "";
            if (SdkConstants.internalFPName.equalsIgnoreCase("wiseasy")) {
                pidata = piddata.replaceAll("\\n+", "");
            } else {
                pidata = piddata.replaceAll("\\R+", "");
            }
            obj.put("pidData", pidata.trim());
            Log.e("TAG", "BalanceEnquiry: " + obj.toString());


            ApiInterface apiInterface = RetrofitInstance.retrofit().create(ApiInterface.class);
            UnifiedAepsRequestModel unifiedAepsRequestModel1 = new UnifiedAepsRequestModel(unifiedAepsRequestModel.getAmount(), unifiedAepsRequestModel.getAadharNo(), unifiedAepsRequestModel.getIin(), unifiedAepsRequestModel.getMobileNumber(), unifiedAepsRequestModel.getApiUser(), unifiedAepsRequestModel.getBankName(), pidata.trim(), unifiedAepsRequestModel.getLatLong(),"", "", "", UnifiedAepsRequestModel.getApiUserName(),"");
            apiInterface.getencryptedBalanceEnquiry(encodedUrl, token, unifiedAepsRequestModel1).enqueue(new Callback<ResponseBody>() {
                @Override
                public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                    if (response.isSuccessful() && response != null) {
                        try {
                            JSONObject obj = new JSONObject(response.body().string());
                            String status = obj.getString("status");
                            String transactionMode = obj.getString("transactionMode");
                            if (status.equalsIgnoreCase("SUCCESS")) {
                                unifiedAepsContract.hideLoader();
                                if (transactionMode.equalsIgnoreCase("AEPS_MINI_STATEMENT")) {
                                    Gson gson = new Gson();
                                    UnifiedTxnStatusModel aepsResponse = gson.fromJson(String.valueOf(obj), UnifiedTxnStatusModel.class);
                                    unifiedAepsContract.checkMSunifiedStatus(status, "Balance Enquiry Success", aepsResponse);
                                } else {
                                    Gson gson = new Gson();
                                    UnifiedTxnStatusModel aepsResponse = gson.fromJson(String.valueOf(obj), UnifiedTxnStatusModel.class);
                                    unifiedAepsContract.checkUnifiedResponseStatus(status, "Balance Enquiry Success", aepsResponse);
                                }
                            } else {
                                Gson gson = new Gson();
                                UnifiedTxnStatusModel aepsResponse = gson.fromJson(String.valueOf(obj), UnifiedTxnStatusModel.class);
                                unifiedAepsContract.checkUnifiedResponseStatus(status, "Fail", aepsResponse);
                                unifiedAepsContract.hideLoader();
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                            unifiedAepsContract.hideLoader();
                        }
                    }else {
                        try {
                            Gson gson = new Gson();
                            UnifiedTxnStatusModel aepsResponse = gson.fromJson(response.errorBody().string(), UnifiedTxnStatusModel.class);
                            UnifiedTxnStatusModel jsonObject = gson.fromJson(response.errorBody().string(),UnifiedTxnStatusModel.class);
                            unifiedAepsContract.checkUnifiedResponseStatus(jsonObject.getStatus(), "Fail", aepsResponse);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        unifiedAepsContract.hideLoader();
                    }
                }

                @Override
                public void onFailure(Call<ResponseBody> call, Throwable t) {
                    t.printStackTrace();
                }
            });

        } catch (Exception e) {
            e.printStackTrace();
            unifiedAepsContract.hideLoader();
        }
    }
}