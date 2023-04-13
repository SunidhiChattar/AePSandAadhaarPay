package com.matm.matmsdk.aepsmodule.network.model.pincode;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Data__1 {

@SerializedName("pincode")
@Expose
private Integer pincode;
@SerializedName("state")
@Expose
private String state;
@SerializedName("city")
@Expose
private String city;

public Integer getPincode() {
return pincode;
}

public void setPincode(Integer pincode) {
this.pincode = pincode;
}

public String getState() {
return state;
}

public void setState(String state) {
this.state = state;
}

public String getCity() {
return city;
}

public void setCity(String city) {
this.city = city;
}

}