package com.example.user.repeat.Activity;


import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.example.user.repeat.Other.HttpConnection;
import com.example.user.repeat.Other.Net;
import com.example.user.repeat.Other.URLs;
import com.example.user.repeat.Other.User;
import com.example.user.repeat.Other.Uti;
import com.example.user.repeat.R;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class Act_Login extends AppCompatActivity {
    //
    private Context ctxt = Act_Login.this;
    public static User user;
    private HttpConnection conn;
    private Resources res;
    // UI
    private Button bt_login;
    private EditText edit_loginphone;
    // Other

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.act_login);
        //InitialToolBar();
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
        private final int CONNECT_FAIL = -1;
        private final int SUCCESS = 1;

        @Override
        protected void onPreExecute() {

        }

        @Override
        protected Integer doInBackground(String... datas) {
            Integer result = CONNECT_FAIL;
            mPhone = datas[0];
            try {
                // put "phone" post out, get json
                List<NameValuePair> postFields = new ArrayList<>();
                postFields.add(new BasicNameValuePair("phone", mPhone));
                JSONObject jobj = conn.PostGetJson(URLs.url_login, postFields);
                if (jobj != null) {
                    result = jobj.getInt("success");
                    if (result == SUCCESS) {
                        JSONArray array = jobj.getJSONArray("finfo");
                        JSONObject finfo = array.getJSONObject(0);
                        user.setCustomerNo(finfo.getString("CustomerNo"));
                        user.setFLaborNo(finfo.getString("FLaborNo"));
                        user.setCellPhone(finfo.getString("CellPhone"));
                    }
                }

            } catch (JSONException e) {
                e.printStackTrace();
            }
            return result;
        }

        @Override
        protected void onPostExecute(Integer result) {
            switch (result) {
                case SUCCESS:
                    Intent i = new Intent(ctxt, Act_MainScreen.class);
                    startActivity(i);
                    finish();
                    break;
                case CONNECT_FAIL:
                    break;
                default:
                    Uti.t(ctxt, "Error : " + result);
            }


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
        conn = new HttpConnection();
        user = new User();
    }

    private void InitialToolBar() {
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        ActionBar actionbar = getSupportActionBar();
        actionbar.setTitle("Hello");
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
