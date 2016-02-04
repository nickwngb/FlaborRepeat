package com.example.user.repeat.Activity;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.res.Resources;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import com.example.user.repeat.Adapter.PAListAdapter;
import com.example.user.repeat.Other.AnnouncementRecord;
import com.example.user.repeat.Other.Code;
import com.example.user.repeat.Other.HttpConnection;
import com.example.user.repeat.Other.Net;
import com.example.user.repeat.Other.PARecord;
import com.example.user.repeat.Other.ProblemRecord;
import com.example.user.repeat.Other.URLs;
import com.example.user.repeat.Other.User;
import com.example.user.repeat.Other.Uti;
import com.example.user.repeat.R;
import com.example.user.repeat.Receiver.RefreshReceiver;
import com.example.user.repeat.UseForGCM.GoldBrotherGCM;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Date;

public class Act_MainScreen extends Activity implements GoldBrotherGCM.MagicLenGCMListener, RefreshReceiver.OnrefreshListener {
    //
    private Context ctxt = Act_MainScreen.this;
    private Resources res;
    private HttpConnection conn;
    private GoldBrotherGCM mGBGCM;
    private RefreshReceiver mRefreshReceiver;

    private User user;
    private final int AddAct = 0;
    // UI
    private Button bt_repeat;
    private ListView list_reaprethistory;
    // Other
    private List<ProblemRecord> problemlist;
    private List<AnnouncementRecord> announcementlist;
    private List<PARecord> palist;
    private PAListAdapter pa_adapter;

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
        private String regid = "";

        @Override
        protected Integer doInBackground(String... datas) {
            Integer result = Code.ConnectTimeOut;
            regid = datas[0];
            if (regid.isEmpty()) {
                return Code.RegIdEmpty;
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
                case Code.Success:
                    //Uti.t(ctxt, "Set Push notification Success");
                    break;
                case Code.RegIdEmpty:
                    //Uti.t(ctxt, "RegId is Empty");
                    break;
                case Code.ConnectTimeOut:
                    break;
                case Code.RegIdFail:
                    //Uti.t(ctxt, "Push notification Fail\n" + "Insert Error");
                    break;
            }

        }
    }

    @Override
    public void setRefresh(String text) {
        Toast.makeText(ctxt, text, Toast.LENGTH_SHORT).show();
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
        private ProgressDialog pDialog;

        protected void onPreExecute() {
            pDialog = new ProgressDialog(ctxt);
            pDialog.setMessage("Loading...");
            pDialog.setIndeterminate(false);
            pDialog.setCancelable(true);
            pDialog.show();
        }

        @Override
        protected Integer doInBackground(String... strings) {
            Integer result = Code.ConnectTimeOut;
            try {
                problemlist.clear();
                // put "phone" post out, get json
                List<NameValuePair> postFields = new ArrayList<>();
                postFields.add(new BasicNameValuePair("CustomerNo", user.getCustomerNo()));
                postFields.add(new BasicNameValuePair("FLaborNo", user.getFLaborNo()));
                JSONObject jobj = conn.PostGetJson(URLs.url_allproblem, postFields);
                if (jobj != null) {
                    result = jobj.getInt("success");
                    if (result == Code.Success) {
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
            pDialog.dismiss();
            Log.i("LoadingAllProblemTask ", "Result " + result);
            switch (result) {
                case Code.Success:
                case Code.ResultEmpty:
                    LoadingAllAnnouncement();
                    break;
                case Code.ConnectTimeOut:
                    break;
                default:
                    Uti.t(ctxt, "Loading Problem Error : " + result);
            }
        }
    }

    private void LoadingAllAnnouncement() {
        if (Net.isNetWork(ctxt)) {
            new LoadingAllAnnouncementTask().execute();
        } else {
            Uti.t(ctxt, res.getString(R.string.msg_err_network));
        }
    }

    class LoadingAllAnnouncementTask extends AsyncTask<String, Integer, Integer> {
        private ProgressDialog pDialog;

        protected void onPreExecute() {
            pDialog = new ProgressDialog(ctxt);
            pDialog.setMessage("Loading...");
            pDialog.setIndeterminate(false);
            pDialog.setCancelable(true);
            pDialog.show();
        }

        @Override
        protected Integer doInBackground(String... strings) {
            Integer result = Code.ConnectTimeOut;
            try {
                announcementlist.clear();
                List<NameValuePair> postFields = new ArrayList<>();
                postFields.add(new BasicNameValuePair("CustomerNo", user.getCustomerNo()));
                postFields.add(new BasicNameValuePair("FLaborNo", user.getFLaborNo()));
                JSONObject jobj = conn.PostGetJson(URLs.url_allannouncement, postFields);
                if (jobj != null) {
                    result = jobj.getInt("success");
                    if (result == Code.Success) {
                        JSONArray array = jobj.getJSONArray("fannouncements");
                        for (int i = 0; i < array.length(); i++) {
                            JSONObject ajobj = array.getJSONObject(i);
                            AnnouncementRecord fproblem = new AnnouncementRecord();
                            fproblem.setMPSNo(ajobj.getInt("MPSNo"));
                            fproblem.setPushContent(ajobj.getString("PushContent"));
                            fproblem.setCreateID(ajobj.getString("CreateID"));
                            fproblem.setCreateDate(ajobj.getString("CreateDate"));
                            announcementlist.add(fproblem);
                        }
                    }
                }

            } catch (JSONException e) {
                Log.i("JSONException", e.toString());
            }
            return result;
        }

        @Override
        protected void onPostExecute(Integer result) {
            pDialog.dismiss();
            Log.i("LoadingAllAnnouncement", "Result " + result);
            switch (result) {
                case Code.Success:
                case Code.ResultEmpty:
                    if (problemlist.isEmpty() && announcementlist.isEmpty()) {
                        Uti.t(ctxt, "Empty");
                    } else {
                        refreshPAList();
                    }
                    break;
                case Code.ConnectTimeOut:
                    break;
                default:
                    Uti.t(ctxt, "error : "+result);
            }
        }
    }

    private void InitialSomething() {
        user = User.getUser();
        res = getResources();
        // http
        conn = new HttpConnection();
        // receiver
        mGBGCM = new GoldBrotherGCM(this);
        mRefreshReceiver = new RefreshReceiver();
        // List
        palist = new ArrayList<>();
        announcementlist = new ArrayList<>();
        problemlist = new ArrayList<>();
        pa_adapter = new PAListAdapter(this, palist);

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
            AddRegIdToAPPServer(mGBGCM.getRegistrationId());
        }
        // UI
        bt_repeat.setOnClickListener(onclicklistener);
        list_reaprethistory.setOnItemClickListener(onitemclicklistener);
        list_reaprethistory.setAdapter(pa_adapter);
        //GCM Refresh
        mRefreshReceiver.setOnrefreshListener(this);

        IntentFilter intentFilter = new IntentFilter();

        intentFilter.addAction("Refresh");

        registerReceiver(mRefreshReceiver, intentFilter);
    }

    private void refreshPAList() {
        sortByDate();
        if (pa_adapter != null) {
            pa_adapter.notifyDataSetChanged();
        }
    }

    private void sortByDate() {
        palist.clear();
        for (ProblemRecord pr : problemlist) {
            PARecord par = new PARecord();
            par.tag = PARecord.TAG_PRecord;
            par.setPRSNo(pr.getPRSNo());
            par.setCustomerNo(pr.getCustomerNo());
            par.setFLaborNo(pr.getFLaborNo());
            par.setProblemDescription(pr.getProblemDescription());
            par.setCreateProblemDate(pr.getCreateProblemDate());
            par.setResponseResult(pr.getResponseResult());
            par.setResponseDate(pr.getResponseDate());
            par.setResponseID(pr.getResponseID());
            par.setProblemStatus(pr.getProblemStatus());
            par.setSatisfactionDegree(pr.getSatisfactionDegree());
            palist.add(par);
        }
        for (AnnouncementRecord ar : announcementlist) {
            PARecord par = new PARecord();
            par.tag = PARecord.TAG_ARecord;
            par.setMPSNo(ar.getMPSNo());
            par.setPushContent(ar.getPushContent());
            par.setCreateID(ar.getCreateID());
            par.setCreateDate(ar.getCreateDate());
            palist.add(par);
        }

        for (int i = 0; i < palist.size() - 1; i++) {
            boolean swapped = false;
            for (int j = 0; j < palist.size() - i - 1; j++) {
                if (palist.get(j).tag.equals(PARecord.TAG_PRecord)) {
                    if (palist.get(j + 1).tag.equals(PARecord.TAG_PRecord)) {
                        String a = palist.get(j).getCreateProblemDate();
                        String b = palist.get(j + 1).getCreateProblemDate();
                        //設定日期格式
                        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                        try {
                            Date date1 = sdf.parse(a);
                            Date date2 = sdf.parse(b);
                            if (date1.before(date2)) {
                                Collections.swap(palist, j, j + 1);
                                swapped = true;
                            }
                        } catch (ParseException e) {
                            e.printStackTrace();
                        }
                    } else {
                        String a = palist.get(j).getCreateProblemDate();
                        String b = palist.get(j + 1).getCreateDate();
                        //設定日期格式
                        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                        try {
                            Date date1 = sdf.parse(a);
                            Date date2 = sdf.parse(b);
                            if (date1.before(date2)) {
                                Collections.swap(palist, j, j + 1);
                                swapped = true;
                            }
                        } catch (ParseException e) {
                            e.printStackTrace();
                        }
                    }
                } else {
                    if (palist.get(j + 1).tag.equals(PARecord.TAG_PRecord)) {
                        String a = palist.get(j).getCreateDate();
                        String b = palist.get(j + 1).getCreateProblemDate();
                        //設定日期格式
                        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                        try {
                            Date date1 = sdf.parse(a);
                            Date date2 = sdf.parse(b);
                            if (date1.before(date2)) {
                                Collections.swap(palist, j, j + 1);
                                swapped = true;
                            }
                        } catch (ParseException e) {
                            e.printStackTrace();
                        }
                    } else {
                        String a = palist.get(j).getCreateDate();
                        String b = palist.get(j + 1).getCreateDate();
                        //設定日期格式
                        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                        try {
                            Date date1 = sdf.parse(a);
                            Date date2 = sdf.parse(b);
                            if (date1.before(date2)) {
                                Collections.swap(palist, j, j + 1);
                                swapped = true;
                            }
                        } catch (ParseException e) {
                            e.printStackTrace();
                        }
                    }
                }
            }
            if (!swapped) {
                break;
            }
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
            if (palist.get(pos).tag.equals(PARecord.TAG_PRecord)) {
                Intent i = new Intent(ctxt, Act_Problem.class);
                Bundle b = new Bundle();
                b.putSerializable("ProblemRecord", palist.get(pos));
                i.putExtras(b);
                startActivity(i);
            } else {
                Intent i = new Intent(ctxt, Act_Announcement.class);
                Bundle b = new Bundle();
                b.putSerializable("AnnouncementRecord", palist.get(pos));
                i.putExtras(b);
                startActivity(i);
            }
        }
    };
    public void onDestroy() {
        unregisterReceiver(mRefreshReceiver);
        super.onDestroy();
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
    
    private long lastpresstime = 0;
    @Override
    public void onBackPressed() {
        if (System.currentTimeMillis() - lastpresstime < 2000) {
            super.onBackPressed();
        } else {
            lastpresstime = System.currentTimeMillis();
            Toast.makeText(ctxt, "Press again to leave", Toast.LENGTH_SHORT).show();
        }
    }

}
