package com.matm.matmsdk.posverification.responsemodel;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import com.matm.matmsdk.posverification.response.DataEntity;

public class MerchantOnboardResponseModel {

    @Expose
    @SerializedName("message")
    private String message;

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    @Expose
    @SerializedName("status")
    private int status;
}
