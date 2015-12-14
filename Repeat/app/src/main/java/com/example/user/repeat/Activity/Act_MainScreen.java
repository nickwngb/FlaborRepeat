package com.example.user.repeat.Activity;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Resources;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;

import com.example.user.repeat.Adapter.ProblemListAdapter;
import com.example.user.repeat.Other.HttpConnection;
import com.example.user.repeat.Other.Net;
import com.example.user.repeat.Other.ProblemRecord;
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

public class Act_MainScreen extends Activity {
    //
    private Context ctxt = Act_MainScreen.this;
    private Resources res;
    private HttpConnection conn;
    public static User user = Act_Login.user;
    private final int AddAct = 0;
    // UI
    private Button bt_repeat;
    private ListView list_reaprethistory;
    // Other
    private List<ProblemRecord> problemlist;
    private ProblemListAdapter pl_adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.act_mainscreen);
        InitialSomething();
        InitialUI();
        InitialAction();
        LoadingAllProblem();
    }


    private void LoadingAllProblem() {
        if (Net.isNetWork(ctxt)) {
            new LoadingAllProblemTask().execute();
        } else {
            Uti.t(ctxt, res.getString(R.string.msg_err_network));
        }
    }

    class LoadingAllProblemTask extends AsyncTask<String, Integer, Integer> {
        private final int CONNECT_FAIL = -1;
        private final int SUCCESS = 1;

        protected void onPreExecute() {

        }

        @Override
        protected Integer doInBackground(String... strings) {
            Integer result = CONNECT_FAIL;
            try {
                problemlist.clear();
                // put "phone" post out, get json
                List<NameValuePair> postFields = new ArrayList<>();
                postFields.add(new BasicNameValuePair("CustomerNo", user.getCustomerNo()));
                postFields.add(new BasicNameValuePair("FLaborNo", user.getFLaborNo()));
                JSONObject jobj = conn.PostGetJson(URLs.url_allproblem, postFields);
                if (jobj != null) {
                    result = jobj.getInt("success");
                    if (result == SUCCESS) {
                        JSONArray array = jobj.getJSONArray("fproblems");
                        for (int i = 0; i < array.length(); i++) {
                            JSONObject ajobj = array.getJSONObject(i);
                            ProblemRecord fproblem = new ProblemRecord();
                            fproblem.setPRSNo(ajobj.getInt("PRSNo"));
                            fproblem.setCustomerNo(ajobj.getString("CustomerNo"));
                            fproblem.setFLaborNo(ajobj.getString("FLaborNo"));
                            fproblem.setProblemDescription(ajobj.getString("ProblemDescription"));
                            fproblem.setCreateProblemDate(ajobj.getString("CreateProblemDate"));
                            fproblem.setResponseResult(ajobj.getString("ResponseResult"));
                            fproblem.setResponseDate(ajobj.getString("ResponseDate"));
                            fproblem.setResponseID(ajobj.getString("ResponseID"));
                            fproblem.setProblemStatus(ajobj.getString("ProblemStatus"));
                            fproblem.setSatisfactionDegree(ajobj.getString("SatisfactionDegree"));
                            problemlist.add(fproblem);
                        }
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
                    if (!problemlist.isEmpty()) {
                        refreshProblemList();
                    } else {
                        Uti.t(ctxt, "Empty");
                    }
                    break;
                case CONNECT_FAIL:
                    break;
                default:
                    Uti.t(ctxt, "Error : " + result);
            }
        }
    }

    private void InitialSomething() {
        res = getResources();
        conn = new HttpConnection();
        problemlist = new ArrayList<ProblemRecord>();
        pl_adapter = new ProblemListAdapter(this, problemlist);
    }

    private void InitialUI() {
        bt_repeat = (Button) findViewById(R.id.bt_repeat);
        list_reaprethistory = (ListView) findViewById(R.id.list_repeathistory);
    }

    private void InitialAction() {
        bt_repeat.setOnClickListener(onclicklistener);
        list_reaprethistory.setOnItemClickListener(onitemclicklistener);
        list_reaprethistory.setAdapter(pl_adapter);
    }

    private void refreshProblemList() {
        if (pl_adapter != null) {
            pl_adapter.notifyDataSetChanged();
        }
    }

    private View.OnClickListener onclicklistener = new View.OnClickListener() {

        @Override
        public void onClick(View v) {
            Intent i = new Intent();
            i.setClass(ctxt, Act_Addwindow.class);
            startActivity(i);
            startActivityForResult(i, AddAct);
        }
    };
    private AdapterView.OnItemClickListener onitemclicklistener = new AdapterView.OnItemClickListener() {

        @Override
        public void onItemClick(AdapterView<?> parent, View view, int pos, long id) { //按一次Item事件
            Intent i = new Intent(ctxt, Act_Problem.class);
            Bundle b = new Bundle();
            b.putSerializable("ProblemRecord", problemlist.get(pos));
            i.putExtras(b);
            startActivity(i);
        }
    };

    public void onBackPressed() {
        new AlertDialog.Builder(ctxt)
                .setTitle("Exit?")
                .setMessage("Are you sure to Exit?")
                .setPositiveButton("No", null
                ).setNegativeButton("Yes", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog,
                                int which) {
                Act_MainScreen.super.onBackPressed();
            }
        }).show();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == AddAct) {
            if (resultCode == RESULT_OK) {
                boolean Success = data.getBooleanExtra("SUCCESS", false);
                if (Success) { // if success , refresh
                    LoadingAllProblem();
                }
            }
        }
    }
}
