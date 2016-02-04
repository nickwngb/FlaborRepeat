package com.example.user.repeat.Activity;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RatingBar;
import android.widget.TextView;

import com.example.user.repeat.Other.Code;
import com.example.user.repeat.Other.HttpConnection;
import com.example.user.repeat.Other.Net;
import com.example.user.repeat.Other.PARecord;
import com.example.user.repeat.Other.ProblemRecord;
import com.example.user.repeat.Other.URLs;
import com.example.user.repeat.Other.Uti;
import com.example.user.repeat.R;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;


public class Act_Problem extends Activity {
    //
    private Context ctxt = Act_Problem.this;
    private HttpConnection conn;
    private Resources res;
    private PARecord par;
    // UI
    private TextView txt_problem_createdate, txt_customercontent, txt_managercontent, txt_problem_repeatedate;
    private LinearLayout ll_givestart;
    private Button bt_givestart;
    private RatingBar rb_score;
    // other


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.act_problem);

        getExtras();
        InitialSomething();
        InitialUI();
        InitialAction();
    }

    private void RatingTask(String... datas) {
        if (Net.isNetWork(ctxt)) {
            new RatingTask().execute(datas);
        } else {
            Uti.t(ctxt, res.getString(R.string.msg_err_network));
        }
    }

    class RatingTask extends AsyncTask<String, Integer, Integer> {
        private ProgressDialog pDialog;
        private String rating;

        @Override
        protected void onPreExecute() {
            pDialog = new ProgressDialog(ctxt);
            pDialog.setMessage("Rating...");
            pDialog.setIndeterminate(false);
            pDialog.setCancelable(true);
            pDialog.show();
        }

        @Override
        protected Integer doInBackground(String... datas) {
            Integer result = Code.ConnectTimeOut;
            rating = datas[0];
            try {
                // put "phone" post out, get json
                List<NameValuePair> postFields = new ArrayList<>();
                postFields.add(new BasicNameValuePair("PRSNo", String.valueOf(par.getPRSNo())));
                postFields.add(new BasicNameValuePair("star", rating));

                JSONObject jobj = conn.PostGetJson(URLs.url_updatestart, postFields);
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
                    Uti.t(ctxt, "Rating Success");
                    break;
                case Code.ResultEmpty:
                    Uti.t(ctxt, "Rating Fail");
                    break;
                case Code.ConnectTimeOut:
                    Uti.t(ctxt, "Connection Fail");
                    break;
                default:
                    Uti.t(ctxt, "Error : " + result);
            }
        }
    }

    private void InitialSomething() {
        res = getResources();
        conn = new HttpConnection();
    }

    private void InitialUI() {
        ll_givestart = (LinearLayout) findViewById(R.id.ll_givestart);
        bt_givestart = (Button) findViewById(R.id.bt_givestart);
        rb_score = (RatingBar) findViewById(R.id.rb_score);
        txt_problem_createdate = (TextView) findViewById(R.id.txt_problem_createdate);
        txt_customercontent = (TextView) findViewById(R.id.txt_customercontent);
        txt_problem_repeatedate = (TextView) findViewById(R.id.txt_problem_repeatedate);
        txt_managercontent = (TextView) findViewById(R.id.txt_managercontent);
    }

    private void InitialAction() {
        txt_problem_createdate.setText(par.getCreateProblemDate());
        txt_customercontent.setText(par.getProblemDescription());
        txt_problem_repeatedate.setText(par.getResponseDate());
        txt_managercontent.setText(par.getResponseResult());

        if (par.getProblemStatus().equals(Code.Completed)) {
            ll_givestart.setVisibility(View.VISIBLE);
            Uti.t(ctxt, "Rating me !");
        }

        bt_givestart.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                String start = String.valueOf((int) rb_score.getRating());
                RatingTask(start);
            }
        });
    }

    private void getExtras() {
        Bundle b = getIntent().getExtras();
        par = (PARecord) b.getSerializable("ProblemRecord");
    }
}
