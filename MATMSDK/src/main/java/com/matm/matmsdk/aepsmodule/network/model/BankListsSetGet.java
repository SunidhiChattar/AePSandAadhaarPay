package com.matm.matmsdk.aepsmodule.network.model;

import java.io.Serializable;
import java.util.List;

public class BankListsSetGet implements Serializable {
  private List<BankIINs> bankIINs;

  private Integer statusCode;

  public List< BankIINs> getBankIINs  () {
    return bankIINs;
  }

  public void setBankIINs(List< BankIINs> bankIINs) {
    this.bankIINs = bankIINs;
  }

  public Integer getStatusCode() {
    return this.statusCode;
  }

  public void setStatusCode(Integer statusCode) {
    this.statusCode = statusCode;
  }

  public static class BankIINs implements Serializable {
    private String BANKNAME;

    private String IIN;

    public String getBANKNAME() {
      return this.BANKNAME;
    }

    public void setBANKNAME(String BANKNAME) {
      this.BANKNAME = BANKNAME;
    }

    public String getIIN() {
      return this.IIN;
    }

    public void setIIN(String IIN) {
      this.IIN = IIN;
    }
  }
}
