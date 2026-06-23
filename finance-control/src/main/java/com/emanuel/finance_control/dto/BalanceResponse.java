package com.emanuel.finance_control.dto;

public class BalanceResponse {

    private double balance;

    public BalanceResponse(double balance) {
        this.balance = balance;
    }

    public double getBalance() {
        return balance;
    }

    public void setBalance(double balance) {
        this.balance = balance;
    }
}

