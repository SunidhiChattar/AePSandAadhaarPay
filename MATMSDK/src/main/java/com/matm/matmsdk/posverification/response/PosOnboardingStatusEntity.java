package com.matm.matmsdk.posverification.response;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class PosOnboardingStatusEntity {
    @Expose
    @SerializedName("status_desc")
    private String statusDesc;
    @Expose
    @SerializedName("sub_status")
    private int subStatus;
    @Expose
    @SerializedName("status")
    private String status;

    public String getStatusDesc() {
        return statusDesc;
    }

    public void setStatusDesc(String statusDesc) {
        this.statusDesc = statusDesc;
    }

    public int getSubStatus() {
        return subStatus;
    }

    public void setSubStatus(int subStatus) {
        this.subStatus = subStatus;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}
