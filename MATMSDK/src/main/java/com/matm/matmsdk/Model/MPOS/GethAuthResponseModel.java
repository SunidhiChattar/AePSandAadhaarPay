package com.matm.matmsdk.Model.MPOS;

import com.google.gson.annotations.SerializedName;

public class GethAuthResponseModel{

	@SerializedName("status")
	private String status;

	@SerializedName("usertoken")
	private String userToken;

	@SerializedName("username")
	private String username;

	public String getStatus() {
		return status;
	}

	public String getUserToken() {
		return userToken;
	}

	public String getUsername() {
		return username;
	}
}