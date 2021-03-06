package com.example.user.repeat.Activity;


import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.example.user.repeat.Asyn.Login;
import com.example.user.repeat.Other.Code;
import com.example.user.repeat.Other.MyDialog;
import com.example.user.repeat.Other.HttpConnection;
import com.example.user.repeat.Other.Net;
import com.example.user.repeat.Other.User;
import com.example.user.repeat.Other.Uti;
import com.example.user.repeat.Other.Vaild;
import com.example.user.repeat.R;

public class Act_Login extends AppCompatActivity {
    //
    private Context ctxt = Act_Login.this;
    private User user;
    private HttpConnection conn;
    private Resources res;
    //
    private SharedPreferences settings;
    private static final String data = "DATA";
    private static final String phoneField = "PHONE";
    // UI
    private Button bt_login;
    private EditText et_phont;
    // Other
    private String phone; // for preferences

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.act_login);
        InitialSomething();
        InitialUI();
        InitialAction();
        readData();
    }

    private void LoginTask(String phoneL) {
        if (Net.isNetWork(ctxt)) {
            final ProgressDialog pd = MyDialog.getProgressDialog(ctxt, "Loading...");
            Login task = new Login(conn, new Login.OnLoginListener() {
                public void finish(Integer result, String CustomerNo, String FLaborNo, String ChineseName, String LaborPhoto,String phones) {
                    pd.dismiss();
                    switch (result){
                        case Code.Success:
                            phone = phones;
                            user.setLaborPhoto(LaborPhoto);
                            user.setChineseName(ChineseName);
                            user.setCustomerNo(CustomerNo);
                            user.setFLaborNo(FLaborNo);
                            user.setCellPhone(phones);
                            saveData();
                            Intent i = new Intent(ctxt, Act_MainScreen.class);
                            startActivity(i);
                            finishActivity();
                            break;
                        case Code.ResultEmpty:
                            Uti.t(ctxt, "Phone number does not exist");
                            break;
                        case Code.ConnectTimeOut:
                            Uti.t(ctxt, "Server no response");
                            break;
                        default:
                            Uti.t(ctxt, "Error : " + result);
                    }
                }
            });
            task.execute(phoneL);
        } else {
            Uti.t(ctxt, res.getString(R.string.msg_err_network));
        }
    }
    private void InitialAction() {
        bt_login.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                String phone = et_phont.getText().toString();
                if (Vaild.login(ctxt, phone)) {
                    LoginTask(phone);
                }
            }
        });
    }

    private void InitialUI() {
        bt_login = (Button) findViewById(R.id.bt_login);
        et_phont = (EditText) findViewById(R.id.edit_loginphone);
    }

    private void InitialSomething() {
        res = getResources();
        conn = new HttpConnection();
        user = User.getUser();
    }

    public void readData() {
        settings = getSharedPreferences(data, 0);
        phone = settings.getString(phoneField, "");
        if (!phone.isEmpty()) {
            LoginTask(phone);
        }
    }

    public void saveData() {
        settings = getSharedPreferences(data, 0);
        settings.edit()
                .putString(phoneField, phone)
                .commit();
    }
    private void finishActivity(){
        this.finish();
    }
}
