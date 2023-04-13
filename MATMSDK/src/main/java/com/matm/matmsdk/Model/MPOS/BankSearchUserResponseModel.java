package com.matm.matmsdk.Model.MPOS;

import com.google.gson.annotations.SerializedName;

public class BankSearchUserResponseModel {

	@SerializedName("hello")
	private String hello;

	public void setHello(String hello){
		this.hello = hello;
	}

	public String getHello(){
		return hello;
	}
}