package com.acme.model.dto.sms;

/**
 * Created by nikolay on 07.05.17.
 */
public class SendDto {

    private int code;
    private String smsIds;
    private String balance;

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public String getSmsIds() {
        return smsIds;
    }

    public void setSmsIds(String smsIds) {
        this.smsIds = smsIds;
    }

    public String getBalance() {
        return balance;
    }

    public void setBalance(String balance) {
        this.balance = balance;
    }

    @Override
    public String toString() {
        return "SendDto{" +
                "code=" + code +
                ", smsIds='" + smsIds + '\'' +
                ", balance='" + balance + '\'' +
                '}';
    }
}
