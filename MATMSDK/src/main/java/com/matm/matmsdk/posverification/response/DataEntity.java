package com.matm.matmsdk.posverification.response;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class DataEntity {
    @Expose
    @SerializedName("pos_onboarding_status")
    private PosOnboardingStatusEntity posOnboardingStatus;
    @Expose
    @SerializedName("common_onboarding_status")
    private CommonOnboardingStatusEntity commonOnboardingStatus;

    public PosOnboardingStatusEntity getPosOnboardingStatus() {
        return posOnboardingStatus;
    }

    public void setPosOnboardingStatus(PosOnboardingStatusEntity posOnboardingStatus) {
        this.posOnboardingStatus = posOnboardingStatus;
    }

    public CommonOnboardingStatusEntity getCommonOnboardingStatus() {
        return commonOnboardingStatus;
    }

    public void setCommonOnboardingStatus(CommonOnboardingStatusEntity commonOnboardingStatus) {
        this.commonOnboardingStatus = commonOnboardingStatus;
    }
}
