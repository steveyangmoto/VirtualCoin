package com.elitemobiletechnology.jobcoin.model;

public interface CoinApi {
    enum GetAddressInfoError{
        NETWORK_ERROR,ADDRESS_ERROR,UNKNOWN_ERROR
    }

    enum CoinTransferError{
        NETWORK_ERROR,UNKNOWN_ERROR,INSUFFICIENT_FUND_ERROR
    }


    interface OnGetAddressInfoFinishListener{
        void onSuccess(UserInfo userInfo);
        void onError(GetAddressInfoError error);
    }

    interface OnCoinTransferListener{
        void onCoinTransferSuccess();
        void onCoinTransferError(CoinTransferError error);
    }

    void getAddressInfo(final String address,final OnGetAddressInfoFinishListener listener);
    void send(final String from,final String to,final String amount,final JobCoinApiImpl.OnCoinTransferListener listener);
    void destroy();
}
