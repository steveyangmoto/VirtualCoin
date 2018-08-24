package com.elitemobiletechnology.jobcoin.presenter;

import android.content.Intent;

public interface DashBoardPresenter {
    void onSendButtonClick(String to,String amount);
    void onActivityCreate(Intent intent);
    void onActivityResume();
    void onActivityStop();
    void onDestroy();
}
