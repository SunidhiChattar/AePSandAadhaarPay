package com.matm.matmsdk.posverification.responsemodel;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class ResultEntity {

    @Expose
    @SerializedName("statusDesc")
    private String statusdesc;

    @Expose
    @SerializedName("status")
    private int status;

    public String getStatusdesc() {
        return statusdesc;
    }

    public void setStatusdesc(String statusdesc) {
        this.statusdesc = statusdesc;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }
}
