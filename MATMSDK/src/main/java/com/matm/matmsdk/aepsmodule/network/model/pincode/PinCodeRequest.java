package com.matm.matmsdk.aepsmodule.network.model.pincode;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class PinCodeRequest {

@SerializedName("pin")
@Expose
private Integer pin;

    public PinCodeRequest(Integer pin) {
        this.pin = pin;
    }

    public Integer getPin() {
return pin;
}

public void setPin(Integer pin) {
this.pin = pin;
}

}