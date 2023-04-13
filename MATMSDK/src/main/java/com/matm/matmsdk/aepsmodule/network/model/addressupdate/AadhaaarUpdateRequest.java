package com.matm.matmsdk.aepsmodule.network.model.addressupdate;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class AadhaaarUpdateRequest {

@SerializedName("number")
@Expose
private String number;

    public AadhaaarUpdateRequest(String number, String state, String pincode, String city, String latLong, String apiUserName) {
        this.number = number;
        this.state = state;
        this.pincode = pincode;
        this.city = city;
        this.latLong = latLong;
        this.apiUserName = apiUserName;
    }

    @SerializedName("state")
@Expose
private String state;
@SerializedName("pincode")
@Expose
private String pincode;
@SerializedName("city")
@Expose
private String city;
@SerializedName("latLong")
@Expose
private String latLong;
@SerializedName("apiUserName")
@Expose
private String apiUserName;

public String getNumber() {
return number;
}

public void setNumber(String number) {
this.number = number;
}

public String getState() {
return state;
}

public void setState(String state) {
this.state = state;
}

public String getPincode() {
return pincode;
}

public void setPincode(String pincode) {
this.pincode = pincode;
}

public String getCity() {
return city;
}

public void setCity(String city) {
this.city = city;
}

public String getLatLong() {
return latLong;
}

public void setLatLong(String latLong) {
this.latLong = latLong;
}

public String getApiUserName() {
return apiUserName;
}

public void setApiUserName(String apiUserName) {
this.apiUserName = apiUserName;
}

}