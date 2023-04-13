package com.matm.matmsdk.Model.MPOS;

import com.google.gson.annotations.SerializedName;

public class GethAuthRequestModel{

	@SerializedName("encryptedData")
	private String encryptedData;

	@SerializedName("retailerUserName")
	private String retailerUserName;

	public void setEncryptedData(String encryptedData){
		this.encryptedData = encryptedData;
	}

	public String getEncryptedData(){
		return encryptedData;
	}

	public void setRetailerUserName(String retailerUserName){
		this.retailerUserName = retailerUserName;
	}

	public GethAuthRequestModel(String encryptedData, String retailerUserName) {
		this.encryptedData = encryptedData;
		this.retailerUserName = retailerUserName;
	}

	public String getRetailerUserName(){
		return retailerUserName;
	}
}