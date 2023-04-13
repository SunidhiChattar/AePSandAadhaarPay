package com.matm.matmsdk.Model.MPOS;

import com.google.gson.annotations.SerializedName;

public class GetTransDetailsRequestModel{

	@SerializedName("clientUniqueId")
	private String clientUniqueId;

	public void setClientUniqueId(String clientUniqueId){
		this.clientUniqueId = clientUniqueId;
	}

	public String getClientUniqueId(){
		return clientUniqueId;
	}

	public GetTransDetailsRequestModel(String clientUniqueId) {
		this.clientUniqueId = clientUniqueId;
	}
}