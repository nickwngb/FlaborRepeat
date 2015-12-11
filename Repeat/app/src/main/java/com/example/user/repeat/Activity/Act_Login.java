package com.example.user.repeat.Activity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.example.user.repeat.Other.Net;
import com.example.user.repeat.Other.Uti;
import com.example.user.repeat.R;

public class Act_Login extends Activity {
    //
    private Context ctxt = Act_Login.this;
    private Resources res;
    // UI
    private Button bt_login;
    private EditText edit_loginphone;
    // Other

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.act_login);
        InitialSomething();
        InitialUI();
        InitialAction();
    }

    private void LoginTask(String... datas) {
        if (Net.isNetWork(ctxt)) {
            new LoginTask().execute(datas);
        } else {
            Uti.t(ctxt, res.getString(R.string.msg_err_network));
        }
    }

    class LoginTask extends AsyncTask<String, String, Integer> {
        private String mPhone;

        @Override
        protected void onPreExecute() {

        }

        @Override
        protected Integer doInBackground(String... datas) {
            Integer result = -1;
            mPhone = datas[0];
            return result;
        }

        @Override
        protected void onPostExecute(Integer result) {
            if(result>0){

            }else{
                Uti.t(ctxt,"Error:"+result);
            }


            Intent i = new Intent();
            i.setClass(ctxt, Act_MainScreen.class);
            //phone = edit_loginphone.getText().toString();
            startActivity(i);
            finish();
        }
    }

    private void InitialAction() {
        bt_login.setOnClickListener(onclicklistener);
    }

    private void InitialUI() {
        bt_login = (Button) findViewById(R.id.bt_login);
        edit_loginphone = (EditText) findViewById(R.id.edit_loginphone);
    }

    private void InitialSomething() {
        res = getResources();
    }

    private View.OnClickListener onclicklistener = new View.OnClickListener() {

        public void onClick(View v) {
            String phone = edit_loginphone.getText().toString();
            if (phone.length() > 0) {
                LoginTask(phone);
            }

        }
    };
}
