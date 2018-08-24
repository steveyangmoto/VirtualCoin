package com.elitemobiletechnology.jobcoin.presenter;

import android.content.Intent;
import android.os.Handler;

import com.elitemobiletechnology.jobcoin.model.CoinApi;
import com.elitemobiletechnology.jobcoin.model.JobCoinApiImpl;
import com.elitemobiletechnology.jobcoin.model.UserInfo;
import com.elitemobiletechnology.jobcoin.model.network.HttpRequest;
import com.elitemobiletechnology.jobcoin.view.MainView;

public class MainPresenterImpl implements MainPresenter {
    private MainView mainView;
    private CoinApi jobCoinApi;

    public MainPresenterImpl(MainView mainView){
        this.mainView = mainView;
        jobCoinApi = new JobCoinApiImpl();
    }

    @Override
    public void onLoginButtonClick(final String address) {
        mainView.showProgressBar(true);
       jobCoinApi.getAddressInfo(address, new CoinApi.OnGetAddressInfoFinishListener() {
           @Override
           public void onSuccess(UserInfo userInfo) {
               mainView.openDashBoard(userInfo);
               mainView.showProgressBar(false);
           }

           @Override
           public void onError(CoinApi.GetAddressInfoError error) {
                if(error == CoinApi.GetAddressInfoError.NETWORK_ERROR){
                    mainView.showNetworkError();
                }else if(error == CoinApi.GetAddressInfoError.ADDRESS_ERROR){
                    mainView.showAddressError();
                }else{
                    mainView.showAddressError();
                }
               mainView.showProgressBar(false);
           }
       });
    }

    @Override
    public void destroy() {
        jobCoinApi.destroy();
    }
}
