package com.elitemobiletechnology.jobcoin;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Build;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import com.elitemobiletechnology.jobcoin.model.UserInfo;
import com.elitemobiletechnology.jobcoin.presenter.MainPresenter;
import com.elitemobiletechnology.jobcoin.presenter.MainPresenterImpl;
import com.elitemobiletechnology.jobcoin.view.MainView;

/**
 * Steve Yang
 */
public class MainActivity extends AppCompatActivity implements MainView{
    private Button loginButton;
    private EditText inputBox;
    private MainPresenter presenter;
    private ProgressBar progressBar;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        presenter = new MainPresenterImpl(this);
        loginButton = findViewById(R.id.btLogin);
        progressBar = findViewById(R.id.progressBar);
        inputBox = findViewById(R.id.etAddress);
        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                presenter.onLoginButtonClick(inputBox.getText().toString());
            }
        });
        inputBox.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) { }
            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) { }
            @Override
            public void afterTextChanged(Editable editable) {
                loginButton.setEnabled(editable.length()>0);
            }
        });
    }

    @Override
    public void openDashBoard(UserInfo userInfo) {
        Intent intent = new Intent(this,DashBoardActivity.class);
        intent.putExtra(CoinConstants.KEY_USER_INFO,userInfo);
        startActivity(intent);
    }

    @Override
    public void showAddressError() {
        inputBox.setError(getString(R.string.login_fail));
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
    public void showProgressBar(boolean show){
        this.progressBar.setVisibility(show?View.VISIBLE:View.GONE);
        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        if(show){
            imm.hideSoftInputFromWindow(inputBox.getApplicationWindowToken(), 0);
        }else{
            imm.showSoftInput(inputBox,InputMethodManager.SHOW_IMPLICIT);
        }
    }

    @Override
    protected void onDestroy(){
        super.onDestroy();
        presenter.destroy();
    }


}
