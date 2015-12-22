package com.example.user.repeat.Activity;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Resources;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
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
import com.example.user.repeat.UseForGCM.GoldBrotherGCM;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class Act_MainScreen extends Activity implements GoldBrotherGCM.MagicLenGCMListener {
    //
    private Context ctxt = Act_MainScreen.this;
    private Resources res;
    private HttpConnection conn;
    private GoldBrotherGCM mGBGCM;
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

    // implements methods
    public void gcmRegistered(boolean successfull, String regID) {
        Log.i("RegId Listener!!", successfull + " " + regID);
        if (successfull) {
            Log.i("AddRegIdToAPPServer!", "Execute");
            AddRegIdToAPPServer(regID);
        }
    }

    public boolean gcmSendRegistrationIdToAppServer(String regID) {
        return true;
    }

    // implements methods end
    private void AddRegIdToAPPServer(String... datas) {
        if (Net.isNetWork(ctxt)) {
            new AddRegIdToAPPServer().execute(datas);
        } else {
            Uti.t(ctxt, res.getString(R.string.msg_err_network));
        }
    }


    class AddRegIdToAPPServer extends AsyncTask<String, Integer, Integer> {
        private final int CONNECT_FAIL = -1;
        private final int REGID_ISEMPTY = -2;
        private final int SUCCESS = 1;
        private final int FAIL = 0;
        private String regid = "";

        @Override
        protected Integer doInBackground(String... datas) {
            Integer result = CONNECT_FAIL;
            regid = datas[0];
            if (regid.isEmpty()) {
                return REGID_ISEMPTY;
            }
            try {
                // put "phone" post out, get json
                List<NameValuePair> postFields = new ArrayList<>();
                postFields.add(new BasicNameValuePair("RegId", regid));
                postFields.add(new BasicNameValuePair("CustomerNo", user.getCustomerNo()));
                postFields.add(new BasicNameValuePair("FLaborNo", user.getFLaborNo()));

                JSONObject jobj = conn.PostGetJson(URLs.url_gcm_register, postFields);
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
            Log.i("AddRegIdToAPPServer ", "Result : " + result);
            switch (result) {
                case SUCCESS:
                    Uti.t(ctxt, "Set Push notification Success");
                    break;
                case REGID_ISEMPTY:
                    Uti.t(ctxt, "RegId is Empty");
                    break;
                case CONNECT_FAIL:
                    break;
                case FAIL:
                    Uti.t(ctxt, "Push notification Fail\n" + "Insert Error");
                    break;
            }

        }
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
        private final int NODATA = 0;

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
            Log.i("LoadingAllProblemTask ", "Result " + result);
            switch (result) {
                case SUCCESS:
                case NODATA:
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
        mGBGCM = new GoldBrotherGCM(this);
        problemlist = new ArrayList<ProblemRecord>();
        pl_adapter = new ProblemListAdapter(this, problemlist);
    }

    private void InitialUI() {
        bt_repeat = (Button) findViewById(R.id.bt_repeat);
        list_reaprethistory = (ListView) findViewById(R.id.list_repeathistory);
    }

    private void InitialAction() {
        // Other
        if (mGBGCM.getRegistrationId().isEmpty()) {
            mGBGCM.setMagicLenGCMListener(this);
            mGBGCM.openGCM();
        } else {
            Log.i("RegId is Exsit:", mGBGCM.getRegistrationId());
        }
        // UI
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
