package com.matm.matmsdk.posverification.response;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class CommonOnboardingStatusEntity {
    @Expose
    @SerializedName("common_onboarding")
    private boolean commonOnboarding;
    @Expose
    @SerializedName("statusDesc")
    private String statusdesc;
    @Expose
    @SerializedName("status")
    private int status;

    public boolean getCommonOnboarding() {
        return commonOnboarding;
    }

    public void setCommonOnboarding(boolean commonOnboarding) {
        this.commonOnboarding = commonOnboarding;
    }

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
