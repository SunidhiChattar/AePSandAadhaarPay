package com.matm.matmsdk.aepsmodule.network.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class AadharVerificationResponse {

@SerializedName("status")
@Expose
private String status;
@SerializedName("statusDesc")
@Expose
private String statusDesc;
@SerializedName("response")
@Expose
private Response response;

public String getStatus() {
return status;
}

public void setStatus(String status) {
this.status = status;
}

public String getStatusDesc() {
return statusDesc;
}

public void setStatusDesc(String statusDesc) {
this.statusDesc = statusDesc;
}

public Response getResponse() {
return response;
}

public void setResponse(Response response) {
this.response = response;
}

}