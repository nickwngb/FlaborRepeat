package com.example.user.repeat.Activity;


import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.example.user.repeat.Other.Code;
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
        private ProgressDialog pDialog;
        private String mPhone;

        @Override
        protected void onPreExecute() {
            pDialog = new ProgressDialog(ctxt);
            pDialog.setMessage("Login...");
            pDialog.setIndeterminate(false);
            pDialog.setCancelable(true);
            pDialog.show();
        }

        @Override
        protected Integer doInBackground(String... datas) {
            Integer result = Code.ConnectTimeOut;
            mPhone = datas[0];
            try {
                // put "phone" post out, get json
                List<NameValuePair> postFields = new ArrayList<>();
                postFields.add(new BasicNameValuePair("phone", mPhone));
                JSONObject jobj = conn.PostGetJson(URLs.url_login, postFields);
                if (jobj != null) {
                    result = jobj.getInt("success");
                    if (result == Code.Success) {
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
            pDialog.dismiss();
            Log.i("LoginTask ", "Result " + result);
            switch (result) {
                case Code.Success:
                    Intent i = new Intent(ctxt, Act_MainScreen.class);
                    startActivity(i);
                    finish();
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


    private View.OnClickListener onclicklistener = new View.OnClickListener() {

        public void onClick(View v) {
            String phone = edit_loginphone.getText().toString();
            if (phone.length() > 0) {
                LoginTask(phone);
            }

        }
    };
}
