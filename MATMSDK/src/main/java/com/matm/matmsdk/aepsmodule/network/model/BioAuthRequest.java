package com.matm.matmsdk.aepsmodule.network.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class BioAuthRequest {

    @SerializedName("aadharNo")
    @Expose
    private String aadharNo;
    @SerializedName("apiUserName")
    @Expose
    private String apiUserName;
    @SerializedName("ci")
    @Expose
    private String ci;
    @SerializedName("dc")
    @Expose
    private String dc;
    @SerializedName("dpId")
    @Expose
    private String dpId;
    @SerializedName("encryptedPID")
    @Expose
    private String encryptedPID;
    @SerializedName("hMac")
    @Expose
    private String hMac;
    @SerializedName("isSL")
    @Expose
    private Boolean isSL;
    @SerializedName("mcData")
    @Expose
    private String mcData;
    @SerializedName("mi")
    @Expose
    private String mi;
    @SerializedName("operation")
    @Expose
    private String operation;
    @SerializedName("rdsId")
    @Expose
    private String rdsId;
    @SerializedName("rdsVer")
    @Expose
    private String rdsVer;
    @SerializedName("retailer")
    @Expose
    private String retailer;
    @SerializedName("sKey")
    @Expose
    private String sKey;

    public BioAuthRequest(String aadharNo, String apiUserName, String ci, String dc, String dpId, String encryptedPID, String hMac, Boolean isSL, String mcData, String mi, String operation, String rdsId, String rdsVer, String retailer, String sKey) {
        this.aadharNo = aadharNo;
        this.apiUserName = apiUserName;
        this.ci = ci;
        this.dc = dc;
        this.dpId = dpId;
        this.encryptedPID = encryptedPID;
        this.hMac = hMac;
        this.isSL = isSL;
        this.mcData = mcData;
        this.mi = mi;
        this.operation = operation;
        this.rdsId = rdsId;
        this.rdsVer = rdsVer;
        this.retailer = retailer;
        this.sKey = sKey;
    }
}