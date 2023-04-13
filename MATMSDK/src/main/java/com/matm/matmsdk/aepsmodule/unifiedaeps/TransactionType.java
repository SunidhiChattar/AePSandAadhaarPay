package com.matm.matmsdk.aepsmodule.unifiedaeps;


 enum DebitCredit{
    DEBIT,CREDIT,D,C;
}

public class TransactionType {
    public DebitCredit debitCredit;

    public TransactionType(DebitCredit debitCredit) {
        this.debitCredit = debitCredit;
    }
    public String  transactionType(){
        switch (debitCredit){
            case DEBIT:
                return "Dr ";
            case D:
                return "Dr ";
            case CREDIT:
                return "Cr ";
            case C:
                return "Cr ";
            default:
                return "";
        }
    }
}
