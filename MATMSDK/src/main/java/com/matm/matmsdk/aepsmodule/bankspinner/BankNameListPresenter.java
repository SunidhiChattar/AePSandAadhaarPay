package com.matm.matmsdk.aepsmodule.bankspinner;

import android.content.Context;

import com.androidnetworking.AndroidNetworking;
import com.androidnetworking.common.Priority;
import com.androidnetworking.error.ANError;
import com.androidnetworking.interfaces.JSONObjectRequestListener;
import com.matm.matmsdk.Utils.SdkConstants;
import com.matm.matmsdk.aepsmodule.network.api.ApiInterface;
import com.matm.matmsdk.aepsmodule.network.api.RetrofitInstance;
import com.matm.matmsdk.aepsmodule.network.model.BankListsSetGet;
import com.matm.matmsdk.aepsmodule.utils.Constants;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class BankNameListPresenter implements BankNameContract.UserActionsListener {
    private BankNameContract.View banknameView;
    public BankNameListPresenter(BankNameContract.View banknameView) {
        this.banknameView = banknameView;
    }

    @Override
    public void loadBankNamesList(Context context) {
        banknameView.showLoader();
        if( Constants.BANK_NAME.equalsIgnoreCase("")) {
            ArrayList<BankNameModel> bankNamesArrayList = new ArrayList<BankNameModel>();
            ApiInterface apiInterface = RetrofitInstance.getInstanceMaker("https://us-central1-iserveustaging.cloudfunctions.net/iin/api/v1/").create(ApiInterface.class);
            apiInterface.getUsers().enqueue(new Callback<BankListsSetGet>() {
                @Override
                public void onResponse(Call<BankListsSetGet> call, Response<BankListsSetGet> response) {
                    if (response.isSuccessful() && response.body()!= null) {
                        for (int i = 0; i < response.body().getBankIINs().size(); i++){
                            BankNameModel bankNameModel = new BankNameModel();
                            BankListsSetGet.BankIINs innerObj = response.body().getBankIINs().get(i);
                            String iin = innerObj.getIIN();
                            String bankName = innerObj.getBANKNAME();
                            bankNameModel.setIin(iin);
                            SdkConstants.bankIIN = iin;
                            bankNameModel.setBankName(bankName.trim());
                            bankNamesArrayList.add(bankNameModel);
                        }
                        banknameView.hideLoader();
                        banknameView.bankNameListReady(bankNamesArrayList);
                        banknameView.showBankNames();
                    }
                }

                @Override
                public void onFailure(Call<BankListsSetGet> call, Throwable t) {
                    banknameView.hideLoader();
                }
            });
        }
        else{
            try {
                ArrayList<BankNameModel> bankNamesArrayList = new ArrayList<BankNameModel>();
                JSONObject obj = new JSONObject(Constants.BANK_NAME);
                JSONArray jarray = obj.getJSONArray("bankIINs");
                for (int i = 0; i < jarray.length(); i++) {
                    BankNameModel bankNameModel = new BankNameModel();
                    JSONObject innerObj = jarray.getJSONObject(i);
                    String iin = innerObj.getString("IIN");
                    String bankName = innerObj.getString("BANKNAME");
                    bankNameModel.setIin(iin);
                    bankNameModel.setBankName(bankName);
                    bankNamesArrayList.add(bankNameModel);
                }

                banknameView.hideLoader();
                banknameView.bankNameListReady(bankNamesArrayList);
                banknameView.showBankNames();
            } catch (JSONException e) {
                e.printStackTrace();
                banknameView.hideLoader();
            }
        }
    }

}
