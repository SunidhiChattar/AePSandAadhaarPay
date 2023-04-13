package com.matm.matmsdk.Model.MPOS;

import com.google.gson.annotations.SerializedName;

public class CompleteTranResponseModel{

	@SerializedName("statusDesc")
	private String statusDesc;

	@SerializedName("status")
	private int status;

	public void setStatusDesc(String statusDesc){
		this.statusDesc = statusDesc;
	}

	public String getStatusDesc(){
		return statusDesc;
	}

	public void setStatus(int status){
		this.status = status;
	}

	public int getStatus(){
		return status;
	}
}