package com.example.esberksafak.bankalarim;

import io.realm.RealmObject;

/**
 * Created by EsberkSafak on 3.8.2015.
 */
public class Account extends RealmObject {
    private String bankName;
    private String customerNo;

    public Account(){

    }

    public Account(String bankName, String customerNo) {
        this.bankName = bankName;
        this.customerNo = customerNo;

    }

    public String getBankName() {
        return bankName;
    }

    public void setBankName(String bankName) {
        this.bankName = bankName;
    }

    public String getCustomerNo() {
        return customerNo;
    }

    public void setCustomerNo(String customerNo) {
        this.customerNo = customerNo;
    }


}
