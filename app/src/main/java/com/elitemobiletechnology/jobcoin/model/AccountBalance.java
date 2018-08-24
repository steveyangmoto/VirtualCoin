package com.elitemobiletechnology.jobcoin.model;

import android.accounts.Account;
import android.support.annotation.NonNull;

import java.util.Comparator;
import java.util.Date;

public class AccountBalance implements Comparator<AccountBalance>,Comparable<AccountBalance>{
    private double coinBalance;
    private Date date;

    public AccountBalance(Date date,double coinBalance){
        this.coinBalance = coinBalance;
        this.date = date;
    }


    public double getCoinBalance() {
        return coinBalance;
    }

    public Date getDate() {
        return date;
    }

    @Override
    public int compare(AccountBalance a1, AccountBalance t1) {
        return a1.getDate().compareTo(t1.getDate());
    }

    @Override
    public int compareTo(@NonNull AccountBalance a) {
        return this.date.compareTo(a.getDate());
    }
}
