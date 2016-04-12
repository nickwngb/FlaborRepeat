package com.example.user.repeat.Activity;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.res.Resources;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.RatingBar;
import android.widget.Toast;

import com.example.user.repeat.Adapter.ResponseListAdapter;
import com.example.user.repeat.Asyn.LoadAllResponse;
import com.example.user.repeat.Asyn.SendResponse;
import com.example.user.repeat.Asyn.UpdateRating;
import com.example.user.repeat.Other.Code;
import com.example.user.repeat.Other.MyDialog;
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


public class Act_Responses extends Activity {
    //
    private Context ctxt = Act_Responses.this;
    private User user;
    private Resources res;
    // UI
    private ListView lv_responses;
    private RatingBar rb_satisfyrate;
    private EditText et_content;
    private Button bt_send;
    // adapter
    private ResponseListAdapter adapter;
    // other
    private PARecord par;
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
            final ProgressDialog pd = MyDialog.getProgressDialog(ctxt, "Loading...");
            LoadAllResponse task = new LoadAllResponse(new LoadAllResponse.OnLoadAllResponseListener() {
                public void finish(Integer result, List<ProblemResponse> list) {
                    pd.dismiss();
                    switch (result) {
                        case Code.Success:
                            responses.clear();
                            responses.addAll(list);
                            refreshResponseContent();
                            scrollMyListViewToBottom();
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

    private void scrollMyListViewToBottom() {
        lv_responses.post(new Runnable() {
            public void run() {
                // Select the last row so it will scroll into view...
                lv_responses.setSelection(lv_responses.getCount() - 1);
            }
        });
    }

    private void SendResponse(String... params) {
        if (Net.isNetWork(ctxt)) {
            final ProgressDialog pd = MyDialog.getProgressDialog(ctxt, "Loading...");
            SendResponse task = new SendResponse(new SendResponse.OnSendResponseListener() {
                public void finish(Integer result) {
                    pd.dismiss();
                    switch (result) {
                        case Code.Success:
                            et_content.setText("");
                            LoadAllResponse(par.getPRSNo() + "");
                            break;
                        case Code.Fail:
                            break;
                        case Code.ConnectTimeOut:
                            Uti.t(ctxt, "Server no response");
                            break;
                    }
                }
            });
            task.execute(params);
        } else {
            Uti.t(ctxt, res.getString(R.string.msg_err_network));
        }
    }

    private void RatingTask(String rating) {
        if (Net.isNetWork(ctxt)) {
            final ProgressDialog pd = MyDialog.getProgressDialog(ctxt, "Loading...");
            UpdateRating task = new UpdateRating(new UpdateRating.OnUpdateStatusListener() {
                public void finish(Integer result) {
                    pd.dismiss();
                    switch (result) {
                        case Code.Success:
                            Uti.t(ctxt, "Rating Success");
                            break;
                        case Code.ResultEmpty:
                            Uti.t(ctxt, "Rating Fail");
                            break;
                        case Code.ConnectTimeOut:
                            Uti.t(ctxt, "Server no response");
                            break;
                    }
                }
            });
            task.execute(par.getPRSNo() + "", rating);
        } else {
            Uti.t(ctxt, res.getString(R.string.msg_err_network));
        }
    }

    private String getCurrentDateTime() {
        DateFormat df = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
        return df.format(new Date());
    }

    private void InitialSomething() {
        res = getResources();
        user = User.getUser();
        responses = new ArrayList<>();
        adapter = new ResponseListAdapter(ctxt, responses);
    }

    private void InitialUI() {
        lv_responses = (ListView) findViewById(R.id.lv_responses);
        rb_satisfyrate = (RatingBar) findViewById(R.id.rb_satisfyrate);
        et_content = (EditText) findViewById(R.id.et_responses_content);
        bt_send = (Button) findViewById(R.id.bt_responses_send);
    }

    private void InitialAction() {
        bt_send.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                Hardware.closeKeyBoard(ctxt, view);
                String content = et_content.getText().toString();
                String datetime = getCurrentDateTime();
                String id = user.getChineseName();
                SendResponse(par.getPRSNo() + "", content, datetime, id);
            }
        });
        lv_responses.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                MyDialog.showTextDialog(ctxt, responses.get(position).getResponseContent());
            }
        });
        lv_responses.setAdapter(adapter);
        rb_satisfyrate.setOnRatingBarChangeListener(new RatingBar.OnRatingBarChangeListener() {
            @Override
            public void onRatingChanged(RatingBar ratingBar, final float rating, boolean fromUser) {
                AlertDialog.Builder b = new AlertDialog.Builder(ctxt);
                b.setTitle("Rating?");
                b.setMessage("Rating " + rating + " star?");
                b.setPositiveButton("YES", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        RatingTask(Integer.toString((int) rating));
                    }
                });
                b.setNegativeButton("CANCEL", null);
                b.show();
            }
        });
    }

    private void getExtrasAndExecute() {
        par = (PARecord) getIntent().getSerializableExtra("PAR");
        LoadAllResponse(par.getPRSNo() + "");

        if (par.getProblemStatus() != Code.Untreated) {
            rb_satisfyrate.setVisibility(View.VISIBLE);
            rb_satisfyrate.setRating(par.getSatisfactionDegree() != null ? Float.valueOf(par.getSatisfactionDegree()) : 0);
        }
    }
}
