package com.matm.matmsdk.Model.otpmodel;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public  class GetOtpServiceRequestModel {
    @Expose
    @SerializedName("messageData")
    private MessagedataEntity messagedata;
    @Expose
    @SerializedName("params")
    private String params;
    @Expose
    @SerializedName("WhatsappMobile")
    private String whatsappmobile;
    @Expose
    @SerializedName("Email")
    private String email;
    @Expose
    @SerializedName("MobileNumber")
    private String mobilenumber;
    @Expose
    @SerializedName("operation_performed")
    private String operationPerformed;
    @Expose
    @SerializedName("feature_name")
    private String featureName;
    @Expose
    @SerializedName("admin_name")
    private String adminName;
    @Expose
    @SerializedName("user_name")
    private String userName;

    public GetOtpServiceRequestModel(MessagedataEntity messagedata, String params, String whatsappmobile, String email, String mobilenumber, String operationPerformed, String featureName, String adminName, String userName) {
        this.messagedata = messagedata;
        this.params = params;
        this.whatsappmobile = whatsappmobile;
        this.email = email;
        this.mobilenumber = mobilenumber;
        this.operationPerformed = operationPerformed;
        this.featureName = featureName;
        this.adminName = adminName;
        this.userName = userName;
    }


    public MessagedataEntity getMessagedata() {
        return messagedata;
    }

    public void setMessagedata(MessagedataEntity messagedata) {
        this.messagedata = messagedata;
    }

    public String getParams() {
        return params;
    }

    public void setParams(String params) {
        this.params = params;
    }

    public String getWhatsappmobile() {
        return whatsappmobile;
    }

    public void setWhatsappmobile(String whatsappmobile) {
        this.whatsappmobile = whatsappmobile;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getMobilenumber() {
        return mobilenumber;
    }

    public void setMobilenumber(String mobilenumber) {
        this.mobilenumber = mobilenumber;
    }

    public String getOperationPerformed() {
        return operationPerformed;
    }

    public void setOperationPerformed(String operationPerformed) {
        this.operationPerformed = operationPerformed;
    }

    public String getFeatureName() {
        return featureName;
    }

    public void setFeatureName(String featureName) {
        this.featureName = featureName;
    }

    public String getAdminName() {
        return adminName;
    }

    public void setAdminName(String adminName) {
        this.adminName = adminName;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }
}
