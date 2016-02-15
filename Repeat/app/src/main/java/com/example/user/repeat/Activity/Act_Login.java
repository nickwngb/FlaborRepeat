package com.example.user.repeat.Activity;


import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;

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
    private User user;
    private HttpConnection conn;
    private Resources res;
    //
    private SharedPreferences settings;
    private static final String data = "DATA";
    private static final String phoneField = "PHONE";
    // UI
    private Button bt_login;
    private EditText edit_loginphone;
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
                        JSONArray jArray = jobj.getJSONArray("finfo");
                        if (jArray != null) {
                            JSONObject finfo = jArray.getJSONObject(0);
                            if (finfo != null) {
                                user.setCustomerNo(finfo.getString("CustomerNo"));
                                user.setFLaborNo(finfo.getString("FLaborNo"));
                                user.setCellPhone(mPhone);
                                user.setChineseName(finfo.getString("ChineseName"));
                                user.setLaborPhoto(finfo.getString("LaborPhoto"));
                            }
                        }
                    }
                }

            } catch (JSONException e) {
                e.printStackTrace();
            }
            return result;
        }

        protected void onPostExecute(Integer result) {
            pDialog.dismiss();
            Log.i("LoginTask ", "Result " + result);
            switch (result) {
                case Code.Success:
                    phone = mPhone;
                    saveData();
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
        user = User.getUser();
    }

    private View.OnClickListener onclicklistener = new View.OnClickListener() {

        public void onClick(View v) {
            String phone = edit_loginphone.getText().toString();
            if (!phone.isEmpty()) {
                LoginTask(phone);
            }

        }
    };

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
}
