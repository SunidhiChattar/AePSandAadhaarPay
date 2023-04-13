package com.matm.matmsdk.Model.MPOS;

import com.google.gson.annotations.SerializedName;

public class CompleteTranRequestModel{

	@SerializedName("id")
	private String id;

	@SerializedName("status")
	private String status;

	@SerializedName("statusCode")
	private int statusCode;

	public CompleteTranRequestModel(String id, String status, int statusCode) {
		this.id = id;
		this.status = status;
		this.statusCode = statusCode;
	}

	public void setId(String id){
		this.id = id;
	}

	public String getId(){
		return id;
	}

	public void setStatus(String status){
		this.status = status;
	}

	public String getStatus(){
		return status;
	}

	public void setStatusCode(int statusCode){
		this.statusCode = statusCode;
	}

	public int getStatusCode(){
		return statusCode;
	}
}