package com.example.user.repeat.Activity;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
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

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;


public class Act_Addwindow extends Activity {
    //
    private Context ctxt = Act_Addwindow.this;
    private Resources res;
    private HttpConnection conn;
    private User user;
    // UI
    private Button bt_submit;
    private EditText edit_problemcontent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.act_addwindow);
        InitialSomething();
        InitialUI();
        InitialAction();
    }

    private void AddProblem(String... datas) {
        if (Net.isNetWork(ctxt)) {
            new AddProblemTask().execute(datas);
        } else {
            Uti.t(ctxt, res.getString(R.string.msg_err_network));
        }
    }

    class AddProblemTask extends AsyncTask<String, Integer, Integer> {
        private ProgressDialog pDialog;
        private String content;

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
            content = datas[0];
            try {
                // put "phone" post out, get json
                List<NameValuePair> postFields = new ArrayList<>();
                postFields.add(new BasicNameValuePair("CustomerNo", user.getCustomerNo()));
                postFields.add(new BasicNameValuePair("FLaborNo", user.getFLaborNo()));
                postFields.add(new BasicNameValuePair("ProblemDescription", content));
                postFields.add(new BasicNameValuePair("CreateProblemDate", getCurrentDateTime()));
                postFields.add(new BasicNameValuePair("ProblemStatus", Code.Untreated));

                JSONObject jobj = conn.PostGetJson(URLs.url_addproblem, postFields);
                if (jobj != null) {
                    result = jobj.getInt("success");
                }

            } catch (JSONException e) {
                e.printStackTrace();
            }
            return result;
        }

        @Override
        protected void onPostExecute(Integer result) {
            pDialog.dismiss();
            switch (result) {
                case Code.Success:
                    // put a boolean , request refresh
                    Intent i = new Intent();
                    i.putExtra("SUCCESS", true);
                    setResult(RESULT_OK, i);
                    finish();
                    break;
                case Code.ResultEmpty:
                    Uti.t(ctxt, "Add Fail");
                    break;
                case Code.ConnectTimeOut:
                    Uti.t(ctxt, "Connection Fail");
                    break;
                default:
                    Uti.t(ctxt, "Error : " + result);
            }
        }
    }

    private String getCurrentDateTime() {
        DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
        Date date = new Date();
        return dateFormat.format(date);
    }

    private void InitialSomething() {
        res = getResources();
        conn = new HttpConnection();
        user = User.getUser();
    }

    private void InitialUI() {
        bt_submit = (Button) findViewById(R.id.bt_submit);
        edit_problemcontent = (EditText) findViewById(R.id.edit_problemcontent);
    }

    private void InitialAction() {
        bt_submit.setOnClickListener(onclicklistener);
    }


    private View.OnClickListener onclicklistener = new View.OnClickListener() {

        public void onClick(View v) {
            // get Content
            String content = edit_problemcontent.getText().toString();
            if (content.isEmpty()) {
                Uti.t(ctxt, res.getString(R.string.msg_err_empty));
                return;
            }
            // Add Problem Task
            AddProblem(content);
        }
    };

}
