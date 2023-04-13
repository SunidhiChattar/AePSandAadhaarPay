package com.matm.matmsdk.posverification.response;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public  class CheckKycStatusRequestModel {
    @Expose
    @SerializedName("user_name")
    private String userName;

    public String getUserName() {
        return userName;
    }

    public CheckKycStatusRequestModel(String userName) {
        this.userName = userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }
}
