package com.matm.matmsdk.posverification.responsemodel;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class BankDetailsEntity {

    @Expose
    @SerializedName("user_id")
    private Integer user_id;
    @Expose
    @SerializedName("accountnumber")
    private String accountnumber;
    @Expose
    @SerializedName("ifsccode")
    private String ifsccode;
    @Expose
    @SerializedName("bankname")
    private String bankname;
    @Expose
    @SerializedName("accountholdername")
    private String accountholdername;
    @Expose
    @SerializedName("mobileno")
    private Long mobileno;
    @Expose
    @SerializedName("email")
    private String email;
    @Expose
    @SerializedName("relationship")
    private String relationship;
    @Expose
    @SerializedName("Is_verified")
    private Boolean Is_verified;
    @Expose
    @SerializedName("Is_active")
    private Boolean Is_active;

    public Integer getUser_id() {
        return user_id;
    }

    public void setUser_id(Integer user_id) {
        this.user_id = user_id;
    }

    public String getAccountnumber() {
        return accountnumber;
    }

    public void setAccountnumber(String accountnumber) {
        this.accountnumber = accountnumber;
    }

    public String getIfsccode() {
        return ifsccode;
    }

    public void setIfsccode(String ifsccode) {
        this.ifsccode = ifsccode;
    }

    public String getBankname() {
        return bankname;
    }

    public void setBankname(String bankname) {
        this.bankname = bankname;
    }

    public String getAccountholdername() {
        return accountholdername;
    }

    public void setAccountholdername(String accountholdername) {
        this.accountholdername = accountholdername;
    }

    public Long getMobileno() {
        return mobileno;
    }

    public void setMobileno(Long mobileno) {
        this.mobileno = mobileno;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getRelationship() {
        return relationship;
    }

    public void setRelationship(String relationship) {
        this.relationship = relationship;
    }

    public Boolean getIs_verified() {
        return Is_verified;
    }

    public void setIs_verified(Boolean is_verified) {
        Is_verified = is_verified;
    }

    public Boolean getIs_active() {
        return Is_active;
    }

    public void setIs_active(Boolean is_active) {
        Is_active = is_active;
    }


}
