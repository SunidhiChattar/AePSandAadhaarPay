package com.matm.matmsdk.Model.otpmodel;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class MessagedataEntity {
    @Expose
    @SerializedName("otp")
    private String otp;

    public String getOtp() {
        return otp;
    }

    public void setOtp(String otp) {
        this.otp = otp;
    }
}
