package com.matm.matmsdk.aepsmodule.network.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class CashwithdrawalResponse {

@SerializedName("hello")
@Expose
private String hello;

    public String getHello() {
return hello;
}

public void setHello(String hello) {
this.hello = hello;
}

}