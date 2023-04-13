package com.matm.matmsdk.posverification.responsemodel;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;

public class PosFetchAccountResponseModel {
    @Expose
    @SerializedName("result")
    private ResultEntity result;

    public ArrayList<BankDetailsEntity> getBankDetailsEntityArrayList() {
        return bankDetailsEntityArrayList;
    }

    public void setBankDetailsEntityArrayList(ArrayList<BankDetailsEntity> bankDetailsEntityArrayList) {
        this.bankDetailsEntityArrayList = bankDetailsEntityArrayList;
    }

    @Expose
    @SerializedName("bankdetails")

    private ArrayList<BankDetailsEntity> bankDetailsEntityArrayList;

    public ResultEntity getResult() {
        return result;
    }

    public void setResult(ResultEntity result) {
        this.result = result;
    }


}
