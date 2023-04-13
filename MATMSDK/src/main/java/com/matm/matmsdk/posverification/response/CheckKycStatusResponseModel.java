package com.matm.matmsdk.posverification.response;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public  class CheckKycStatusResponseModel {
    @Expose
    @SerializedName("data")
    private DataEntity data;
    @Expose
    @SerializedName("message")
    private String message;
    @Expose
    @SerializedName("status")
    private int status;

    public DataEntity getData() {
        return data;
    }

    public void setData(DataEntity data) {
        this.data = data;
    }

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
}
