package com.example.user.repeat.Activity;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.res.Resources;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import com.example.user.repeat.Adapter.ResponseListAdapter;
import com.example.user.repeat.Asyn.LoadAllResponse;
import com.example.user.repeat.Asyn.SendResponse;
import com.example.user.repeat.Other.Code;
import com.example.user.repeat.Other.FakeData;
import com.example.user.repeat.Other.FreeDialog;
import com.example.user.repeat.Other.Hardware;
import com.example.user.repeat.Other.HttpConnection;
import com.example.user.repeat.Other.Net;
import com.example.user.repeat.Other.PARecord;
import com.example.user.repeat.Other.ProblemResponse;
import com.example.user.repeat.Other.URLs;
import com.example.user.repeat.Other.User;
import com.example.user.repeat.Other.Uti;
import com.example.user.repeat.R;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.logging.SimpleFormatter;


public class Act_Responses extends Activity {
    //
    private Context ctxt = Act_Responses.this;
    private HttpConnection conn;
    private User user;
    private Resources res;
    private PARecord par;
    // UI
    private ListView lv_responses;
    private EditText et_content;
    private Button bt_send;
    // adapter
    private ResponseListAdapter adapter;
    // other
    private int PRSNo;
    private List<ProblemResponse> responses;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.act_responses);
        InitialSomething();
        InitialUI();
        InitialAction();
        getExtrasAndExecute();
    }

    private void LoadAllResponse(String PRSNo) {
        if (Net.isNetWork(ctxt)) {
            final ProgressDialog pd = FreeDialog.getProgressDialog(ctxt, "Loading...");
            LoadAllResponse task = new LoadAllResponse(conn, new LoadAllResponse.OnLoadAllResponseListener() {
                public void finish(Integer result, List<ProblemResponse> list) {
                    pd.dismiss();
                    switch (result) {
                        case Code.Success:
                            responses.clear();
                            responses.addAll(list);
                            refreshResponseContent();
                            break;
                        case Code.ResultEmpty:
                            Toast.makeText(ctxt, "Empty", Toast.LENGTH_SHORT).show();
                            break;
                        case Code.ConnectTimeOut:
                            Toast.makeText(ctxt, getResources().getString(R.string.msg_err_noresponse), Toast.LENGTH_SHORT).show();
                            break;
                        default:
                            Toast.makeText(ctxt, "Error : " + result, Toast.LENGTH_SHORT).show();
                    }
                }
            });
            task.execute(PRSNo);
        } else {
            Uti.t(ctxt, res.getString(R.string.msg_err_network));
        }
    }

    private void refreshResponseContent() {
        if (adapter != null) {
            adapter.notifyDataSetChanged();
        }
    }

    private void SendResponse(String... params) {
        if (Net.isNetWork(ctxt)) {
            final ProgressDialog pd = FreeDialog.getProgressDialog(ctxt, "Loading...");
            SendResponse task = new SendResponse(conn, new SendResponse.OnSendResponseListener() {
                public void finish(Integer result) {
                    pd.dismiss();
                    switch (result) {
                        case Code.Success:
                            LoadAllResponse(PRSNo + "");
                            break;
                        case Code.Fail:
                            break;
                    }
                }
            });
            task.execute(params);
        } else {
            Uti.t(ctxt, res.getString(R.string.msg_err_network));
        }
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

    private String getCurrentDateTime() {
        DateFormat df = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
        return df.format(new Date());
    }

    private void InitialSomething() {
        res = getResources();
        conn = new HttpConnection();
        user = User.getUser();
        responses = new ArrayList<>();
        adapter = new ResponseListAdapter(ctxt, responses);
    }

    private void InitialUI() {
        lv_responses = (ListView) findViewById(R.id.lv_responses);
        et_content = (EditText) findViewById(R.id.et_responses_content);
        bt_send = (Button) findViewById(R.id.bt_responses_send);
    }

    private void InitialAction() {
        lv_responses.setAdapter(adapter);
        bt_send.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                Hardware.closeKeyBoard(ctxt, view);
                String content = et_content.getText().toString();
                String datetime = getCurrentDateTime();
                String id = user.getChineseName();
                SendResponse(PRSNo + "", content, datetime, id);
            }
        });
    }

    private void getExtrasAndExecute() {
        PRSNo = getIntent().getIntExtra("PRSNo", 0);
        LoadAllResponse(PRSNo + "");
    }
}
