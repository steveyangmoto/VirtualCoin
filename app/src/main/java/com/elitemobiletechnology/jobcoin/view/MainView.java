package com.elitemobiletechnology.jobcoin.view;

import com.elitemobiletechnology.jobcoin.model.UserInfo;

public interface MainView {
    void openDashBoard(UserInfo userInfo);
    void showAddressError();
    void showNetworkError();
    void showProgressBar(boolean show);
}
