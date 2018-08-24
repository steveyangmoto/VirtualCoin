package com.elitemobiletechnology.jobcoin.view;

import com.elitemobiletechnology.jobcoin.model.AccountBalance;

import java.util.List;

public interface DashBoardView {
    void showWelcomeMessage(String address);
    void showBalance(String balance);
    void showCoinSendError(String error);
    void showCoinSendSuccess(String to,String amount);
    void showNetworkError();
    void showProgressBar(boolean show);
    void graphAccountBalanceRecords(List<AccountBalance> accountBalanceHistory);
    void clearSendError();
}
