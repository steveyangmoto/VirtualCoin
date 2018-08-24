package com.elitemobiletechnology.jobcoin;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.elitemobiletechnology.jobcoin.model.AccountBalance;
import com.elitemobiletechnology.jobcoin.model.UserInfo;
import com.elitemobiletechnology.jobcoin.presenter.DashBoardPresenter;
import com.elitemobiletechnology.jobcoin.presenter.DashBoardPresenterImpl;
import com.elitemobiletechnology.jobcoin.view.DashBoardView;
import com.jjoe64.graphview.GraphView;
import com.jjoe64.graphview.helper.DateAsXAxisLabelFormatter;
import com.jjoe64.graphview.series.DataPoint;
import com.jjoe64.graphview.series.LineGraphSeries;

import org.w3c.dom.Text;

import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

public class DashBoardActivity extends Activity implements DashBoardView{
    private static final String TAG = "DashBoardActivity";
    private TextView tvBalance;
    private TextView tvMyAddress;
    private TextView logout;
    private TextView tvError;
    private EditText addressTo;
    private EditText amount;
    private ProgressBar progressBar;
    private Button sendButton;
    private GraphView graph;
    private DashBoardPresenter presenter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dashboard);
        addressTo = findViewById(R.id.etAddressTo);
        tvMyAddress = findViewById(R.id.tvMyAddress);
        graph = findViewById(R.id.graph);
        progressBar = findViewById(R.id.progressBar);
        tvError = findViewById(R.id.tvError);
        amount = findViewById(R.id.etAmount);
        logout = findViewById(R.id.tvLogout);
        sendButton = findViewById(R.id.btSend);
        tvBalance = findViewById(R.id.tvBalance);
        presenter = new DashBoardPresenterImpl(this);
        Intent intent = getIntent();
        if(intent!=null){
            presenter.onActivityCreate(intent);
        }
        sendButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                presenter.onSendButtonClick(addressTo.getText().toString(),amount.getText().toString());
            }
        });
        logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                DashBoardActivity.this.onBackPressed();
            }
        });
    }

    @Override
    protected void onResume(){
        super.onResume();
        presenter.onActivityResume();
    }

    @Override
    protected void onStop(){
        super.onStop();
        presenter.onActivityStop();
    }
    @Override
    public void showWelcomeMessage(String address) {
        tvMyAddress.setText("Hello, "+address);
    }

    @Override
    public void showBalance(String balance) {
        tvBalance.setText("$"+balance);
    }

    @Override
    public void showCoinSendError(String error) {
        tvError.setText(error);
    }

    @Override
    public void clearSendError(){
        tvError.setText("");
    }

    @Override
    public void showCoinSendSuccess(String to,String amount) {
        AlertDialog.Builder builder;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            builder = new AlertDialog.Builder(this, android.R.style.Theme_Material_Dialog_Alert);
        } else {
            builder = new AlertDialog.Builder(this);
        }
        builder.setTitle(DashBoardActivity.this.getString(R.string.send_success_title))
                .setMessage("$"+amount+" "+DashBoardActivity.this.getString(R.string.send_success_message)+" "+to)
                .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                })
                .setIcon(android.R.drawable.ic_dialog_alert)
                .show();
    }

    @Override
    public void showNetworkError() {
        AlertDialog.Builder builder;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            builder = new AlertDialog.Builder(this, android.R.style.Theme_Material_Dialog_Alert);
        } else {
            builder = new AlertDialog.Builder(this);
        }
        builder.setTitle(getString(R.string.network_error_alert_title))
                .setMessage(getString(R.string.network_error_alert_message))
                .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                })
                .setIcon(android.R.drawable.ic_dialog_alert)
                .show();
    }


    @Override
    public void graphAccountBalanceRecords(List<AccountBalance> accountBalanceHistory){
        if(accountBalanceHistory==null||accountBalanceHistory.size()<=0){
            return;
        }
        try {
            graph.removeAllSeries();
            DataPoint[] dataPoints = new DataPoint[accountBalanceHistory.size()];
            for(int i=0;i<dataPoints.length;i++){
                AccountBalance accountBalance = accountBalanceHistory.get(i);
                DataPoint dataPoint = new DataPoint(accountBalance.getDate(),accountBalance.getCoinBalance());
                dataPoints[i] = dataPoint;
            }
            LineGraphSeries<DataPoint> series = new LineGraphSeries<>(dataPoints);
            graph.addSeries(series);
            graph.getGridLabelRenderer().setLabelFormatter(new DateAsXAxisLabelFormatter(this));
            graph.getGridLabelRenderer().invalidate(false,false);
            graph.getGridLabelRenderer().setNumHorizontalLabels(accountBalanceHistory.size()); // only 4 because of the space

            graph.getViewport().setMinX(accountBalanceHistory.get(0).getDate().getTime());
            graph.getViewport().setMaxX(accountBalanceHistory.get(accountBalanceHistory.size()-1).getDate().getTime());
            graph.getViewport().setXAxisBoundsManual(true);
            graph.getGridLabelRenderer().setHumanRounding(false);
        }catch (Exception ex){
            Log.e(TAG,"exception occured while graphing " + ex.getMessage());
        }
    }

    @Override
    public void showProgressBar(boolean show){
        this.progressBar.setVisibility(show?View.VISIBLE:View.GONE);
        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        if(show){
            imm.hideSoftInputFromWindow(addressTo.getApplicationWindowToken(), 0);
        }
    }

    @Override
    public void onDestroy(){
        super.onDestroy();
        presenter.onDestroy();
    }
}
