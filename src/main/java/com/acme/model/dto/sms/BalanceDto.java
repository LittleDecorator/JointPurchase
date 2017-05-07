package com.acme.model.dto.sms;

/**
 * Created by nikolay on 07.05.17.
 */
public class BalanceDto {

    private int code;
    private double balance;

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public double getBalance() {
        return balance;
    }

    public void setBalance(double balance) {
        this.balance = balance;
    }

    @Override
    public String toString() {
        return "BalanceDto{" +
                "code=" + code +
                ", balance=" + balance +
                '}';
    }
}
