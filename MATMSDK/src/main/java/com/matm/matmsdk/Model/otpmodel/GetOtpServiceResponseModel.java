package com.matm.matmsdk.Model.otpmodel;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public  class GetOtpServiceResponseModel {

    @Expose
    @SerializedName("status")
    private int status;
    @Expose
    @SerializedName("message")
    private String message;

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
