package com.matm.matmsdk.aepsmodule.utils.aadharpay.Model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class GetAuthenticationRequest {

@SerializedName("encryptedData")
@Expose
private String encryptedData;
@SerializedName("retailerUserName")
@Expose
private String retailerUserName;

    public GetAuthenticationRequest(String encryptedData, String retailerUserName) {
        this.encryptedData = encryptedData;
        this.retailerUserName = retailerUserName;
    }
}