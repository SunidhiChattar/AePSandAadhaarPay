package com.matm.matmsdk.aepsmodule.network.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class BioAuthResponse {

@SerializedName("bioRespCode")
@Expose
private String bioRespCode;
@SerializedName("respCode")
@Expose
private String respCode;
@SerializedName("uAuthCode")
@Expose
private String uAuthCode;
@SerializedName("balance")
@Expose
private String balance;
@SerializedName("message")
@Expose
private String message;
@SerializedName("cbsauthValue")
@Expose
private String cbsauthValue;
@SerializedName("cauth")
@Expose
private String cauth;
@SerializedName("rname")
@Expose
private String rname;
@SerializedName("bname")
@Expose
private String bname;
@SerializedName("date")
@Expose
private String date;
@SerializedName("uidaiToken")
@Expose
private String uidaiToken;
@SerializedName("status")
@Expose
private String status;
@SerializedName("miniStat")
@Expose
private String miniStat;
@SerializedName("reversalMessage")
@Expose
private String reversalMessage;
@SerializedName("rrn")
@Expose
private String rrn;
@SerializedName("bankRrn")
@Expose
private String bankRrn;
@SerializedName("errors")
@Expose
private String errors;

    public String getBioRespCode() {
        return bioRespCode;
    }

    public String getRespCode() {
        return respCode;
    }

    public String getuAuthCode() {
        return uAuthCode;
    }

    public String getBalance() {
        return balance;
    }

    public String getMessage() {
        return message;
    }

    public String getCbsauthValue() {
        return cbsauthValue;
    }

    public String getCauth() {
        return cauth;
    }

    public String getRname() {
        return rname;
    }

    public String getBname() {
        return bname;
    }

    public String getDate() {
        return date;
    }

    public String getUidaiToken() {
        return uidaiToken;
    }

    public String getStatus() {
        return status;
    }

    public String getMiniStat() {
        return miniStat;
    }

    public String getReversalMessage() {
        return reversalMessage;
    }

    public String getRrn() {
        return rrn;
    }

    public String getBankRrn() {
        return bankRrn;
    }

    public String getErrors() {
        return errors;
    }
}