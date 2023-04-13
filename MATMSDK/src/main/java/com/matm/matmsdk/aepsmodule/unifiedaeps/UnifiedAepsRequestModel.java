package com.matm.matmsdk.aepsmodule.unifiedaeps;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class UnifiedAepsRequestModel {

    @SerializedName("latLong")
    @Expose
    private String latLong;

    @SerializedName("aadharNo")
    @Expose
    private String aadharNo;

    @SerializedName("amount")
    @Expose
    private String amount;

    @SerializedName("iin")
    @Expose
    private String iin;

    @SerializedName("mobileNumber")
    @Expose
    private String mobileNumber;

    @SerializedName("pidData")
    @Expose
    private String pidData;

    @SerializedName("bankName")
    @Expose
    private String bankName;

    @SerializedName("apiUser")
    @Expose
    private String apiUser;

    @SerializedName("paramA")
    @Expose
    private String paramA;

    @SerializedName("paramB")
    @Expose
    private String paramB;
    @SerializedName("paramC")
    @Expose
    private String paramC;

    @SerializedName("retailer")
    @Expose
    private String retailer;

    @SerializedName("apiUserName")
    @Expose
    private static String apiUserName;

    @SerializedName("gatewayPriority")
    @Expose
    private  static String gatewayPriority;



        public UnifiedAepsRequestModel(String amount, String aadharNo, String iin, String mobileNumber, String apiUser, String bankName, String pidData, String latLong, String paramA, String paramB, String paramC, String apiUserName,String retailer, String gatewayPriority) {
        this.amount = amount;
        this.aadharNo = aadharNo;
        this.iin = iin;
        this.mobileNumber = mobileNumber;
        this.apiUser = apiUser;
        this.latLong = latLong;
        this.bankName = bankName;
        this.pidData = pidData;
        this.retailer = retailer;
        this.paramC = paramC;
        this.paramB = paramB;
        this.paramA = paramA;
        this.apiUserName=apiUserName;
        this.gatewayPriority = gatewayPriority;

    }
    public UnifiedAepsRequestModel(String amount, String aadharNo, String iin, String mobileNumber, String apiUser, String bankName, String pidData, String latLong, String paramA, String paramB, String paramC, String apiUserName,String retailer) {
        this.amount = amount;
        this.aadharNo = aadharNo;
        this.iin = iin;
        this.mobileNumber = mobileNumber;
        this.apiUser = apiUser;
        this.latLong = latLong;
        this.bankName = bankName;
        this.pidData = pidData;
        this.retailer = retailer;
        this.paramC = paramC;
        this.paramB = paramB;
        this.paramA = paramA;
        this.apiUserName=apiUserName;


    }

    public String getLatLong() {
        return latLong;
    }

    public void setLatLong(String latLong) {
        this.latLong = latLong;
    }


    public UnifiedAepsRequestModel() {

    }

    public String getPidData() {
        return pidData;
    }

    public void setPidData(String pidData) {
        this.pidData = pidData;
    }

    public String getBankName() {
        return bankName;
    }

    public void setBankName(String bankName) {
        this.bankName = bankName;
    }

    public String getAmount() {
        return amount;
    }

    public void setAmount(String amount) {
        this.amount = amount;
    }

    public String getAadharNo() {
        return aadharNo;
    }

    public void setAadharNo(String aadharNo) {
        this.aadharNo = aadharNo;
    }

    public String getIin() {
        return iin;
    }

    public void setIin(String iin) {
        this.iin = iin;
    }

    public String getMobileNumber() {
        return mobileNumber;
    }

    public void setMobileNumber(String mobileNumber) {
        this.mobileNumber = mobileNumber;
    }

    public String getApiUser() {
        return apiUser;
    }

    public void setApiUser(String apiUser) {
        this.apiUser = apiUser;
    }

    public String getParamA() {
        return paramA;
    }

    public void setParamA(String paramA) {
        this.paramA = paramA;
    }

    public String getParamB() {
        return paramB;
    }

    public void setParamB(String paramB) {
        this.paramB = paramB;
    }

    public String getParamC() {
        return paramC;
    }

    public void setParamC(String paramC) {
        this.paramC = paramC;
    }

    public String getRetailer() {
        return retailer;
    }

    public void setRetailer(String retailer) {
        this.retailer = retailer;
    }
    public static String getApiUserName() {
        return apiUserName;
    }

    public void setApiUserName(String apiUserName) {
        this.apiUserName = apiUserName;
    }


}
