package com.matm.matmsdk.aepsmodule.network.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Response {

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
@SerializedName("bioauth")
@Expose
private Boolean bioauth;

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

public Boolean getBioauth() {
return bioauth;
}

public void setBioauth(Boolean bioauth) {
this.bioauth = bioauth;
}

}