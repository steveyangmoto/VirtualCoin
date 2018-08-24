package com.elitemobiletechnology.jobcoin.model;

import android.os.Handler;
import android.os.HandlerThread;
import android.os.Looper;
import android.util.Log;

import com.elitemobiletechnology.jobcoin.model.network.HttpRequest;
import com.google.gson.Gson;

import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class JobCoinApiImpl implements CoinApi{
    private static final String FROM_ADDRESS = "fromAddress";
    private static final String TO_ADDRES = "toAddress";
    private static final String AMOUNT = "amount";
    private static final String coinApiUrl = "http://jobcoin.gemini.com/retread/api/";
    private Gson gson;
    private Handler workerHandler;
    private Handler mainHandler;


    public JobCoinApiImpl(){
        gson = new Gson();
        HandlerThread handlerThread = new HandlerThread("MyHandlerThread");
        handlerThread.start();
        Looper looper = handlerThread.getLooper();
        workerHandler = new Handler(looper);
        mainHandler = new Handler(Looper.getMainLooper());
    }

    @Override
    public void getAddressInfo(final String address,final OnGetAddressInfoFinishListener listener){
        final String url = coinApiUrl + "addresses/"+address;
        workerHandler.post(new Runnable() {
            @Override
            public void run() {
                try {
                    HttpRequest request = HttpRequest.get(url);
                    if (request.ok()) {
                        String body = request.body();
                        final UserInfo userInfo = gson.fromJson(body, UserInfo.class);
                        final float balance = Float.valueOf(userInfo.getBalance());
                        final Transaction[] transactions = userInfo.getTransactions();
                        mainHandler.post(new Runnable() {
                            @Override
                            public void run() {
                                if (balance == 0 && transactions.length == 0) {
                                    listener.onError(GetAddressInfoError.ADDRESS_ERROR);
                                } else {
                                    userInfo.setUserId(address);
                                    listener.onSuccess(userInfo);
                                }
                            }
                        });
                    }
                }catch (HttpRequest.HttpRequestException httpEx){
                    mainHandler.post(new Runnable() {
                        @Override
                        public void run() {
                            listener.onError(GetAddressInfoError.NETWORK_ERROR);
                        }
                    });
                }catch (Exception ex){
                    mainHandler.post(new Runnable() {
                        @Override
                        public void run() {
                            listener.onError(GetAddressInfoError.UNKNOWN_ERROR);
                        }
                    });
                }
            }
        });
    }

    @Override
    public void send(final String from,final String to,final String amount,final OnCoinTransferListener listener){
        final String url = coinApiUrl + "transactions";
        workerHandler.post(new Runnable() {
            @Override
            public void run() {
                try {
                    Map<String, String> values = new HashMap();
                    values.put(FROM_ADDRESS, from);
                    values.put(TO_ADDRES, to);
                    values.put(AMOUNT, amount);
                    final HttpRequest request = HttpRequest.post(url, values, true);
                    final int responseCode = request.code();
                    mainHandler.post(new Runnable() {
                        @Override
                        public void run() {
                            if(responseCode == 200){
                                listener.onCoinTransferSuccess();
                            }else if(responseCode==422){
                                listener.onCoinTransferError(CoinTransferError.INSUFFICIENT_FUND_ERROR);
                            }else{
                                listener.onCoinTransferError(CoinTransferError.UNKNOWN_ERROR);
                            }
                        }
                    });
                }catch (HttpRequest.HttpRequestException ex){
                    mainHandler.post(new Runnable() {
                        @Override
                        public void run() {
                            listener.onCoinTransferError(CoinTransferError.NETWORK_ERROR);
                        }
                    });
                }catch (Exception ex){
                    mainHandler.post(new Runnable() {
                        @Override
                        public void run() {
                            listener.onCoinTransferError(CoinTransferError.UNKNOWN_ERROR);
                        }
                    });
                }
            }
        });
    }

    @Override
    public void destroy(){
        workerHandler.removeCallbacksAndMessages(null);
        mainHandler.removeCallbacksAndMessages(null);
    }

}
