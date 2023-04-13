package com.matm.matmsdk.aepsmodule.network.model.balanceEnquiry;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class BalanceEnquiryResponse {

@SerializedName("txId")
@Expose
private String txId;
@SerializedName("transactionMode")
@Expose
private String transactionMode;
@SerializedName("status")
@Expose
private String status;
@SerializedName("iin")
@Expose
private String iin;
@SerializedName("gateway")
@Expose
private Integer gateway;
@SerializedName("errors")
@Expose
private Object errors;
@SerializedName("createdDate")
@Expose
private String createdDate;
@SerializedName("updatedDate")
@Expose
private String updatedDate;
@SerializedName("bankName")
@Expose
private String bankName;
@SerializedName("apiTid")
@Expose
private String apiTid;
@SerializedName("origin_identifier")
@Expose
private String originIdentifier;
@SerializedName("apiComment")
@Expose
private String apiComment;
@SerializedName("balance")
@Expose
private String balance;
@SerializedName("ministatement")
@Expose
private Object ministatement;
@SerializedName("isRetriable")
@Expose
private Boolean isRetriable;

public String getTxId() {
return txId;
}

public void setTxId(String txId) {
this.txId = txId;
}

public String getTransactionMode() {
return transactionMode;
}

public void setTransactionMode(String transactionMode) {
this.transactionMode = transactionMode;
}

public String getStatus() {
return status;
}

public void setStatus(String status) {
this.status = status;
}

public String getIin() {
return iin;
}

public void setIin(String iin) {
this.iin = iin;
}

public Integer getGateway() {
return gateway;
}

public void setGateway(Integer gateway) {
this.gateway = gateway;
}

public Object getErrors() {
return errors;
}

public void setErrors(Object errors) {
this.errors = errors;
}

public String getCreatedDate() {
return createdDate;
}

public void setCreatedDate(String createdDate) {
this.createdDate = createdDate;
}

public String getUpdatedDate() {
return updatedDate;
}

public void setUpdatedDate(String updatedDate) {
this.updatedDate = updatedDate;
}

public String getBankName() {
return bankName;
}

public void setBankName(String bankName) {
this.bankName = bankName;
}

public String getApiTid() {
return apiTid;
}

public void setApiTid(String apiTid) {
this.apiTid = apiTid;
}

public String getOriginIdentifier() {
return originIdentifier;
}

public void setOriginIdentifier(String originIdentifier) {
this.originIdentifier = originIdentifier;
}

public String getApiComment() {
return apiComment;
}

public void setApiComment(String apiComment) {
this.apiComment = apiComment;
}

public String getBalance() {
return balance;
}

public void setBalance(String balance) {
this.balance = balance;
}

public Object getMinistatement() {
return ministatement;
}

public void setMinistatement(Object ministatement) {
this.ministatement = ministatement;
}

public Boolean getIsRetriable() {
return isRetriable;
}

public void setIsRetriable(Boolean isRetriable) {
this.isRetriable = isRetriable;
}

}