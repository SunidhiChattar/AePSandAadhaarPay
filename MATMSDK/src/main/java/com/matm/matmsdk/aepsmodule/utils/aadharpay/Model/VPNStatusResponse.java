package com.matm.matmsdk.aepsmodule.utils.aadharpay.Model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class VPNStatusResponse {

@SerializedName("statusDesc")
@Expose
private String statusDesc;
@SerializedName("status")
@Expose
private String status;

public String getStatusDesc() {
return statusDesc;
}

public void setStatusDesc(String statusDesc) {
this.statusDesc = statusDesc;
}

public String getStatus() {
return status;
}

public void setStatus(String status) {
this.status = status;
}

}