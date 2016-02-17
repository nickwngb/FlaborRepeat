package com.example.user.repeat.Activity;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Toast;

import com.example.user.repeat.Adapter.PAListAdapter;
import com.example.user.repeat.Asyn.RegisterGCM;
import com.example.user.repeat.Asyn.UploadPhoto;
import com.example.user.repeat.Other.AnnouncementRecord;
import com.example.user.repeat.Other.BitmapTransformer;
import com.example.user.repeat.Other.Code;
import com.example.user.repeat.Other.FakeData;
import com.example.user.repeat.Other.Hardware;
import com.example.user.repeat.Other.HttpConnection;
import com.example.user.repeat.Other.Net;
import com.example.user.repeat.Other.PARecord;
import com.example.user.repeat.Other.ProblemRecord;
import com.example.user.repeat.Other.ProblemResponse;
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

    //
    private User user;
    private static final int AddAct = 0;
    private static final int PICK_PICTURE = 1;
    private static final int TAKE_PICTURE = 2;
    private static final int TRIM_PICTURE = 3;
    // UI
    private Button bt_repeat, bt_image;
    private ListView list_reaprethistory;
    // Other
    private List<ProblemRecord> problemlist;
    private List<ProblemResponse> responselist;
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
        //LoadingAllProblem();
    }

    // implements methods
    public void gcmRegistered(boolean successfull, String regID) {
        Log.i("RegId Listener!!", successfull + " " + regID);
        if (successfull) {
            RegisterGCMTask(regID);
        }
    }
    public boolean gcmSendRegistrationIdToAppServer(String regID) {
        return true;
    }

    // implements methods end
    private void RegisterGCMTask(String id) {
        if (Net.isNetWork(ctxt)) {
            RegisterGCM task = new RegisterGCM(conn, new RegisterGCM.OnRegisterGCMListener() {
                public void finish(Integer result) {
                    Log.i("RegisterGCM ", "Result : " + result);
                }
            });
            task.execute(id,user.getFLaborNo(),user.getCustomerNo());

        } else {
            Uti.t(ctxt, res.getString(R.string.msg_err_network));
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
                    LoadAllResponse();
                    break;
                case Code.ResultEmpty:

                    break;
                case Code.ConnectTimeOut:
                    break;
                default:
                    Uti.t(ctxt, "Loading Problem Error : " + result);
            }
        }
    }

    private void LoadAllResponse() {
        if (Net.isNetWork(ctxt)) {
            new LoadAllResponse().execute();
        } else {
            Uti.t(ctxt, res.getString(R.string.msg_err_network));
        }
    }

    class LoadAllResponse extends AsyncTask<String, Integer, Integer> {
        private ProgressDialog pDialog;
        private List<Integer> PRSNos;

        protected void onPreExecute() {
            pDialog = new ProgressDialog(ctxt);
            pDialog.setMessage("Loading...");
            pDialog.setIndeterminate(false);
            pDialog.setCancelable(true);
            pDialog.show();
            PRSNos = new ArrayList<>();
            for (ProblemRecord pr : problemlist) {
                PRSNos.add(pr.getPRSNo());
            }
        }

        @Override
        protected Integer doInBackground(String... strings) {
            Integer result = Code.ConnectTimeOut;
            try {
                responselist.clear();
                List<NameValuePair> postFields = new ArrayList<>();
                for (Integer PRSNo : PRSNos) {
                    postFields.add(new BasicNameValuePair("PRSNos[]", PRSNo + ""));
                }
                JSONObject jobj = conn.PostGetJson(URLs.url_response, postFields);
                if (jobj != null) {
                    result = jobj.getInt("success");
                    if (result == Code.Success) {
                        JSONArray array = jobj.getJSONArray("fannouncements");
                        for (int i = 0; i < array.length(); i++) {
                            JSONObject ajobj = array.getJSONObject(i);
                            ProblemResponse rs = new ProblemResponse();
                            rs.setPRSNo(ajobj.getInt("PRSNo"));
                            rs.setResponseContent(ajobj.getString("ResponseContent"));
                            rs.setResponseDate(ajobj.getString("ResponseDate"));
                            rs.setResponseID(ajobj.getString("ResponseID"));
                            rs.setResponseRole(ajobj.getString("ResponseRole"));
                            responselist.add(rs);
                        }
                    }
                }

            } catch (JSONException e) {
                Log.i("LoadAllResponse", e.toString());
            }
            return result;
        }

        @Override
        protected void onPostExecute(Integer result) {
            pDialog.dismiss();
            Log.i("LoadAllResponse", "Result " + result);
            switch (result) {
                case Code.Success:
                case Code.ResultEmpty:
                    PairWithRecord();
                    LoadingAllAnnouncement();
                    break;
                case Code.ConnectTimeOut:
                    break;
                default:
                    Uti.t(ctxt, "error : " + result);
            }
        }

        private void PairWithRecord() {
            for (ProblemResponse rs : responselist) {
                int prsno = rs.getPRSNo();
                for (ProblemRecord pr : problemlist) {
                    if (prsno == pr.getPRSNo()) {
                        pr.setResponseContent(rs.getResponseContent());
                        pr.setResponseDate(rs.getResponseDate());
                        pr.setResponseID(rs.getResponseID());
                        pr.setResponseRole(rs.getResponseRole());
                        break;
                    }
                }
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
                Log.i("LoadingAllAnnouncement", e.toString());
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
                    Uti.t(ctxt, "error : " + result);
            }
        }
    }

    private void UploadPhoto(String photo) {
        if (Net.isNetWork(ctxt)) {
            UploadPhoto task = new UploadPhoto(conn,new UploadPhoto.OnUpdatePhotoListener() {
                public void finish(Integer result) {
                    switch (result) {
                        case Code.Success:
                            break;
                        case Code.ResultEmpty:
                            break;
                        case Code.ConnectTimeOut:
                            break;
                    }
                }
            });
            task.execute(Code.Flabor, photo, user.getFLaborNo(), user.getCustomerNo());
        } else {
            Toast.makeText(ctxt, getResources().getString(R.string.msg_err_network), Toast.LENGTH_SHORT).show();
        }
    }

    private void InitialSomething() {
        user = User.getUser();
        res = getResources();
        // http
        conn = new HttpConnection();
        // receiver
//        mGBGCM = new GoldBrotherGCM(this);
//        mRefreshReceiver = new RefreshReceiver();
        // List
        palist = new ArrayList<>();
        announcementlist = new ArrayList<>();
        problemlist = new ArrayList<>();
        responselist = new ArrayList<>();

        palist = FakeData.getPARecord();

        pa_adapter = new PAListAdapter(this, palist);
    }

    private void InitialUI() {
        bt_repeat = (Button) findViewById(R.id.bt_repeat);
        bt_image = (Button) findViewById(R.id.bt_image);
        list_reaprethistory = (ListView) findViewById(R.id.list_repeathistory);
    }

    private void InitialAction() {
        // Other
//        if (mGBGCM.getRegistrationId().isEmpty()) {
//            mGBGCM.setMagicLenGCMListener(this);
//            mGBGCM.openGCM();
//        } else {
//            Log.i("RegId is Exsit:", mGBGCM.getRegistrationId());
//            AddRegIdToAPPServer(mGBGCM.getRegistrationId());
//        }
        // UI
        bt_repeat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent();
                i.setClass(ctxt, Act_Addwindow.class);
                startActivityForResult(i, AddAct);
            }
        });
        bt_image.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Hardware.closeKeyBoard(ctxt, v);
                Intent iPickPicture = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                startActivityForResult(iPickPicture, PICK_PICTURE);
            }
        });
        list_reaprethistory.setOnItemClickListener(onitemclicklistener);
        list_reaprethistory.setAdapter(pa_adapter);
        //GCM Refresh
//        mRefreshReceiver.setOnrefreshListener(this);
//
//        IntentFilter intentFilter = new IntentFilter();
//
//        intentFilter.addAction("Refresh");
//
//        registerReceiver(mRefreshReceiver, intentFilter);
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
            par.setProblemStatus(pr.getProblemStatus());
            par.setSatisfactionDegree(pr.getSatisfactionDegree());
            par.setResponseContent(pr.getResponseContent());
            par.setResponseDate(pr.getResponseDate());
            par.setResponseID(pr.getResponseID());
            par.setResponseRole(pr.getResponseRole());
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
                        String a = palist.get(j).getResponseDate();
                        String b = palist.get(j + 1).getResponseDate();
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
                        String a = palist.get(j).getResponseDate();
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
                        String b = palist.get(j + 1).getResponseDate();
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

    private AdapterView.OnItemClickListener onitemclicklistener = new AdapterView.OnItemClickListener() {
        public void onItemClick(AdapterView<?> parent, View view, int pos, long id) {
            PARecord par = palist.get(pos);
            if (par.tag.equals(PARecord.TAG_PRecord)) {
                Intent i = new Intent(ctxt, Act_Problem.class);
                i.putExtra("PRSNo", par.getPRSNo());
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
        //unregisterReceiver(mRefreshReceiver);
        super.onDestroy();
    }

    protected void doCropPhoto(Uri data) {
        try {
            //進行照片裁剪
            Intent intent = new Intent("com.android.camera.action.CROP");
            intent.setDataAndType(data, "image/*");
//        intent.setType("image/*");
//        intent.putExtra("data", data);
            intent.putExtra("crop", "true");// crop=true 有這句才能叫出裁剪頁面.
            intent.putExtra("aspectX", 1);// 这兩項為裁剪框的比例.
            intent.putExtra("aspectY", 1);// x:y=1:1
            intent.putExtra("outputX", 300);//回傳照片比例X
            intent.putExtra("outputY", 300);//回傳照片比例Y
            intent.putExtra("return-data", true);
            startActivityForResult(intent, TRIM_PICTURE);
        } catch (ActivityNotFoundException anfe) {
            String errorMessage = "Whoops - your device doesn't support the crop action!";
            Toast.makeText(ctxt, errorMessage, Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == RESULT_OK) {
            switch (requestCode) {
                case AddAct:
                    boolean Success = data.getBooleanExtra("SUCCESS", false);
                    if (Success) { // if success , refresh
                        LoadingAllProblem();
                    }
                    break;
                case PICK_PICTURE:
                    Uri uri = data.getData();
                    doCropPhoto(uri);
                    break;
                case TAKE_PICTURE:
                    Bitmap iBitmap = (Bitmap) data.getExtras().get("data");

                    break;
                case TRIM_PICTURE:
                    final Bitmap result = data.getParcelableExtra("data");

                    ImageView iv = new ImageView(ctxt);
                    iv.setImageBitmap(result);
                    new AlertDialog.Builder(ctxt, AlertDialog.THEME_HOLO_LIGHT).setView(iv).setPositiveButton("OK", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialogInterface, int i) {
                            UploadPhoto(BitmapTransformer.BitmapToBase64(result));
                        }
                    }).setNegativeButton("Cancel", null).show();
                    break;
            }


            if (requestCode == AddAct) {

            }
        }
    }

    private long lastpresstime = 0;

    public void onBackPressed() {
        if (System.currentTimeMillis() - lastpresstime < 2000) {
            super.onBackPressed();
        } else {
            lastpresstime = System.currentTimeMillis();
            Toast.makeText(ctxt, "Press again to leave", Toast.LENGTH_SHORT).show();
        }
    }

}
