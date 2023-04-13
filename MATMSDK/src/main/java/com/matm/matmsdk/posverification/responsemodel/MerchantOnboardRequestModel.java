package com.matm.matmsdk.posverification.responsemodel;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public  class MerchantOnboardRequestModel {
    @Expose
    @SerializedName("bene_name")
    private String beneName;
    @Expose
    @SerializedName("bankCode")
    private String bankcode;
    @SerializedName("ifsc_code")
    private String ifscCode;
    @Expose
    @SerializedName("bank_name")
    private String bankName;
    @Expose
    @SerializedName("account_number")
    private String accountNumber;
    @Expose
    @SerializedName("user_name")
    private String userName;
    @Expose
    @SerializedName("otp")
    private String otp;
    @Expose
    @SerializedName("bank_pincode")
    private String bank_pincode;

    public String getOtp() {
        return otp;
    }

    public void setOtp(String otp) {
        this.otp = otp;
    }

    public String getBank_pincode() {
        return bank_pincode;
    }

    public void setBank_pincode(String bank_pincode) {
        this.bank_pincode = bank_pincode;
    }

    public String getParams() {
        return params;
    }

    public void setParams(String params) {
        this.params = params;
    }

    @Expose
    @SerializedName("params")
    private String params;


    public String getBeneName() {
        return beneName;
    }

    public void setBeneName(String beneName) {
        this.beneName = beneName;
    }

    public String getAccountType() {
        return accountType;
    }

    public void setAccountType(String accountType) {
        this.accountType = accountType;
    }

    @Expose
    @SerializedName("accountType")
    private String accountType;


    public MerchantOnboardRequestModel(String beneName, String bankcode, String ifscCode, String bankName, String accountNumber, String userName, String accountType,String otp,String pincode,String params) {
        this.bankcode = bankcode;
        this.beneName = beneName;
        this.ifscCode = ifscCode;
        this.bankName = bankName;
        this.accountNumber = accountNumber;
        this.accountType = accountType;
        this.userName = userName;
        this.otp = otp;
        this.bank_pincode = pincode;
        this.params = params;
    }

    public String getBankcode() {
        return bankcode;
    }

    public void setBankcode(String bankcode) {
        this.bankcode = bankcode;
    }

    public String getIfscCode() {
        return ifscCode;
    }

    public void setIfscCode(String ifscCode) {
        this.ifscCode = ifscCode;
    }

    public String getBankName() {
        return bankName;
    }

    public void setBankName(String bankName) {
        this.bankName = bankName;
    }

    public String getAccountNumber() {
        return accountNumber;
    }

    public void setAccountNumber(String accountNumber) {
        this.accountNumber = accountNumber;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

}
