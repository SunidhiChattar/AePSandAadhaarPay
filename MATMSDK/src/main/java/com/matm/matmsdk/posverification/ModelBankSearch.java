package com.matm.matmsdk.posverification;


import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class ModelBankSearch {

    @SerializedName("bank_name")
    @Expose
    public String bank_name;

    @SerializedName("bank_code")
    @Expose
    public String bank_code;

    @SerializedName("ifsc_code")
    @Expose
    public String ifsc_code;

    @SerializedName("ifsc_mode")
    @Expose
    public String ifsc_mode;

    @SerializedName("dmt_enable")
    @Expose
    public Boolean dmt_enable;

    @SerializedName("dmt_imps_enable")
    @Expose
    public Boolean dmt_imps_enable;

    @SerializedName("dmt_neft_enable")
    @Expose
    public Boolean dmt_neft_enable;


}
