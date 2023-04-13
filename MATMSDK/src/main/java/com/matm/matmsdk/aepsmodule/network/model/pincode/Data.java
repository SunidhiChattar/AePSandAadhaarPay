package com.matm.matmsdk.aepsmodule.network.model.pincode;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Data {

@SerializedName("status")
@Expose
private String status;
@SerializedName("statusDesc")
@Expose
private String statusDesc;
@SerializedName("data")
@Expose
private Data__1 data;

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

public Data__1 getData() {
return data;
}

public void setData(Data__1 data) {
this.data = data;
}

}