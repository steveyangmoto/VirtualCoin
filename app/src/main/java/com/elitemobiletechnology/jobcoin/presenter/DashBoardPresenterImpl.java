package com.elitemobiletechnology.jobcoin.presenter;

import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.os.Looper;

import com.elitemobiletechnology.jobcoin.CoinConstants;
import com.elitemobiletechnology.jobcoin.JobCoinApplication;
import com.elitemobiletechnology.jobcoin.R;
import com.elitemobiletechnology.jobcoin.model.AccountBalance;
import com.elitemobiletechnology.jobcoin.model.CoinApi;
import com.elitemobiletechnology.jobcoin.model.JobCoinApiImpl;
import com.elitemobiletechnology.jobcoin.model.Transaction;
import com.elitemobiletechnology.jobcoin.model.UserInfo;
import com.elitemobiletechnology.jobcoin.view.DashBoardView;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class DashBoardPresenterImpl implements DashBoardPresenter {
    private static SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
    private static final int UPDATE_INTERVAL = 3000;
    private DashBoardView dashBoardView;
    private CoinApi jobCoinApi;
    private String myAddress;
    private Context context;
    private Handler mainHandler;

    final Runnable updateTask = new Runnable() {
        @Override
        public void run() {
            jobCoinApi.getAddressInfo(myAddress, new CoinApi.OnGetAddressInfoFinishListener() {
                @Override
                public void onSuccess(UserInfo userInfo) {
                    if(userInfo!=null){
                        presentUserInfo(userInfo);
                    }
                    reschedule();
                }

                @Override
                public void onError(CoinApi.GetAddressInfoError error) {
                    reschedule();
                }
            });
        }

        private void reschedule(){
            mainHandler.postDelayed(this,UPDATE_INTERVAL);
        }
    };

    public DashBoardPresenterImpl(DashBoardView dashBoardView){
        this.dashBoardView = dashBoardView;
        jobCoinApi = new JobCoinApiImpl();
        context = JobCoinApplication.getAppContext();
        mainHandler = new Handler(Looper.getMainLooper());
    }

    @Override
    public void onSendButtonClick(final String to, final String amount) {
        dashBoardView.clearSendError();
        dashBoardView.showProgressBar(true);
        jobCoinApi.send(myAddress, to, amount, new JobCoinApiImpl.OnCoinTransferListener() {
            @Override
            public void onCoinTransferSuccess() {
                dashBoardView.showProgressBar(false);
                dashBoardView.showCoinSendSuccess(to,amount);
            }

            @Override
            public void onCoinTransferError(JobCoinApiImpl.CoinTransferError error) {
                dashBoardView.showProgressBar(false);
                if(error == CoinApi.CoinTransferError.INSUFFICIENT_FUND_ERROR){
                    dashBoardView.showCoinSendError(context.getString(R.string.insufficient_funds_error));
                }else if(error == CoinApi.CoinTransferError.NETWORK_ERROR){
                    dashBoardView.showNetworkError();
                }else {
                    dashBoardView.showCoinSendError(context.getString(R.string.unknown_coin_transfer_error));
                }
            }
        });
    }

    @Override
    public void onActivityCreate(Intent intent) {
        UserInfo userInfo = intent.getParcelableExtra(CoinConstants.KEY_USER_INFO);
        if(userInfo!=null){
           presentUserInfo(userInfo);
        }
    }

    @Override
    public void onActivityResume(){
        mainHandler.postDelayed(updateTask,UPDATE_INTERVAL);
    }

    @Override
    public void onActivityStop(){
        mainHandler.removeCallbacksAndMessages(null);
    }

    private List<AccountBalance> getAccountBalanceHistory(Transaction[] transactions){
        Map<String,AccountBalance> accountBalanceMap = new HashMap<>();
        List<AccountBalance> accountBalances = new ArrayList<>();
        double amount = 0f;
        for(int i=0;i<transactions.length;i++){
            Transaction transaction = transactions[i];
            try {
                double delta = Double.parseDouble(transaction.getAmount());
                if(transaction.getToAddress().equals(myAddress)){
                    amount += delta;
                }else{
                    amount -= delta;
                }
                Date date = format.parse(transaction.getTimestamp());
                accountBalanceMap.put(date.toString(),new AccountBalance(date,amount));
            }catch (Exception ex){}
        }
        for (Map.Entry<String, AccountBalance> entry : accountBalanceMap.entrySet())
        {
            accountBalances.add(entry.getValue());
        }
        Collections.sort(accountBalances);
        return accountBalances;
    }

    private void presentUserInfo(UserInfo userInfo){
        this.myAddress = userInfo.getUserId();
        Transaction[] transactions = userInfo.getTransactions();
        List<AccountBalance> accountBalanceHistory = getAccountBalanceHistory(transactions);
        dashBoardView.graphAccountBalanceRecords(accountBalanceHistory);
        dashBoardView.showWelcomeMessage(myAddress);
        dashBoardView.showBalance(userInfo.getBalance());
    }

    @Override
    public void onDestroy(){
        jobCoinApi.destroy();
    }

}
