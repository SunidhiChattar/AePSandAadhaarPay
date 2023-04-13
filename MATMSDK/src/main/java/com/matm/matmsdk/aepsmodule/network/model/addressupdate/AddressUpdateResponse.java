package com.matm.matmsdk.aepsmodule.network.model.addressupdate;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class AddressUpdateResponse {

@SerializedName("status")
@Expose
private String status;
@SerializedName("statusDesc")
@Expose
private String statusDesc;
@SerializedName("response")
@Expose
private Object response;

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

public Object getResponse() {
return response;
}

public void setResponse(Object response) {
this.response = response;
}

}