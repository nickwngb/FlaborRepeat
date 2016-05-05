package com.example.user.repeat.Activity;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.net.Uri;
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
import com.example.user.repeat.Asyn.LoadAllAnnouncement;
import com.example.user.repeat.Asyn.LoadAllLastestResponse;
import com.example.user.repeat.Asyn.LoadAllProblem;
import com.example.user.repeat.Asyn.RegisterGCM;
import com.example.user.repeat.Asyn.UploadPhoto;
import com.example.user.repeat.Other.AnnouncementRecord;
import com.example.user.repeat.Other.Code;
import com.example.user.repeat.Other.MyDialog;
import com.example.user.repeat.Other.Hardware;
import com.example.user.repeat.Other.HttpConnection;
import com.example.user.repeat.Other.Net;
import com.example.user.repeat.Other.PARecord;
import com.example.user.repeat.Other.ProblemRecord;
import com.example.user.repeat.Other.ProblemResponse;
import com.example.user.repeat.Other.User;
import com.example.user.repeat.Other.Uti;
import com.example.user.repeat.R;
import com.example.user.repeat.Receiver.RefreshReceiver;
import com.example.user.repeat.UseForGCM.GoldBrotherGCM;

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
    private GoldBrotherGCM mGBGCM;
    private RefreshReceiver mRefreshReceiver;

    //
    private User user;
    private static final int AddAct = 0;
    private static final int PICK_PICTURE = 1;
    private static final int TAKE_PICTURE = 2;
    private static final int TRIM_PICTURE = 3;
    private static final int ResponseAct = 4;
    // UI
    private Button bt_repeat;
    private ImageView bt_image;
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
        LoadingAllProblem();
    }

    // implements methods
    public void gcmRegistered(boolean successfull, String regID) {
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
            final ProgressDialog fd = MyDialog.getProgressDialog(ctxt, "Loading...");
            RegisterGCM task = new RegisterGCM(new RegisterGCM.OnRegisterGCMListener() {
                public void finish(Integer result) {
                    fd.dismiss();
                    Log.i("RegisterGCM ", "Result : " + result);
                }
            });
            task.execute(id, user.getFLaborNo(), user.getCustomerNo());
        } else {
            Uti.t(ctxt, res.getString(R.string.msg_err_network));
        }
    }

    @Override
    public void setRefresh(String text) {
        //Toast.makeText(ctxt, text, Toast.LENGTH_SHORT).show();
        LoadingAllProblem();
    }

    private void LoadingAllProblem() {
        if (Net.isNetWork(ctxt)) {
            final ProgressDialog fd = MyDialog.getProgressDialog(ctxt, "Loading...");
            LoadAllProblem task = new LoadAllProblem(new LoadAllProblem.OnLoadAllProblemListener() {
                public void finish(Integer result, List<ProblemRecord> list) {
                    fd.dismiss();
                    problemlist.clear();
                    problemlist.addAll(list);
                    switch (result) {
                        case Code.Success:
                            LoadAllResponse();
                            break;
                        case Code.ResultEmpty:
                            LoadingAllAnnouncement();
                            break;
                        case Code.ConnectTimeOut:
                            Toast.makeText(ctxt, getResources().getString(R.string.msg_err_noresponse), Toast.LENGTH_SHORT).show();
                            break;
                        default:
                            Uti.t(ctxt, "Loading Problem Error : " + result);
                    }
                }
            });
            task.execute(user.getFLaborNo(), user.getCustomerNo());
        } else {
            Uti.t(ctxt, res.getString(R.string.msg_err_network));
        }
    }

    private void LoadAllResponse() {
        if (Net.isNetWork(ctxt)) {
            final ProgressDialog fd = MyDialog.getProgressDialog(ctxt, "Loading...");
            LoadAllLastestResponse task = new LoadAllLastestResponse(new LoadAllLastestResponse.OnLoadAllResponseListener() {
                public void finish(Integer result, List<ProblemResponse> list) {
                    fd.dismiss();
                    responselist.clear();
                    responselist.addAll(list);
                    switch (result) {
                        case Code.Success:
                        case Code.ResultEmpty:
                            PairWithRecord();
                            LoadingAllAnnouncement();
                            break;
                        case Code.ConnectTimeOut:
                            Toast.makeText(ctxt, getResources().getString(R.string.msg_err_noresponse), Toast.LENGTH_SHORT).show();
                            break;
                        default:
                            Uti.t(ctxt, "error : " + result);
                    }
                }
            });
            List<Integer> PRSNos = new ArrayList<>();
            for (ProblemRecord pr : problemlist) {
                PRSNos.add(pr.getPRSNo());
            }
            task.execute(PRSNos);

        } else {
            Uti.t(ctxt, res.getString(R.string.msg_err_network));
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

    private void LoadingAllAnnouncement() {
        if (Net.isNetWork(ctxt)) {
            final ProgressDialog fd = MyDialog.getProgressDialog(ctxt, "Loading...");
            LoadAllAnnouncement task = new LoadAllAnnouncement(new LoadAllAnnouncement.OnLoadAllAnnouncementListener() {
                public void finish(Integer result, List<AnnouncementRecord> list) {
                    fd.dismiss();
                    announcementlist.clear();
                    announcementlist.addAll(list);
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
                            Toast.makeText(ctxt, getResources().getString(R.string.msg_err_noresponse), Toast.LENGTH_SHORT).show();
                            break;
                        default:
                            Uti.t(ctxt, "error : " + result);
                    }
                }
            });
            task.execute(user.getFLaborNo(), user.getCustomerNo());
        } else {
            Uti.t(ctxt, res.getString(R.string.msg_err_network));
        }
    }

    private void UploadPhoto(Bitmap photo) {
        if (Net.isNetWork(ctxt)) {
            final ProgressDialog fd = MyDialog.getProgressDialog(ctxt, "Loading...");
            UploadPhoto task = new UploadPhoto(new UploadPhoto.OnUpdatePhotoListener() {
                public void finish(Integer result) {
                    fd.dismiss();
                    Log.i("UploadPhoto", "Result " + result);
                    switch (result) {
                        case Code.Success:
                            Toast.makeText(ctxt, "Upload Success", Toast.LENGTH_SHORT).show();
                            break;
                        case Code.ResultEmpty:
                            Toast.makeText(ctxt, "Upload Fail", Toast.LENGTH_SHORT).show();
                            break;
                        case Code.ConnectTimeOut:
                            Toast.makeText(ctxt, getResources().getString(R.string.msg_err_noresponse), Toast.LENGTH_SHORT).show();
                            break;
                    }
                }
            }, photo);
            task.execute(Code.Flabor, user.getFLaborNo(), user.getCustomerNo());
        } else {
            Toast.makeText(ctxt, getResources().getString(R.string.msg_err_network), Toast.LENGTH_SHORT).show();
        }
    }

    private void InitialSomething() {
        user = User.getUser();
        res = getResources();
        // receiver
        mGBGCM = new GoldBrotherGCM(this, this);
        mRefreshReceiver = new RefreshReceiver();
        // List
        palist = new ArrayList<>();
        announcementlist = new ArrayList<>();
        problemlist = new ArrayList<>();
        responselist = new ArrayList<>();
        pa_adapter = new PAListAdapter(this, palist);
    }

    private void InitialUI() {
        bt_repeat = (Button) findViewById(R.id.bt_repeat);
        bt_image = (ImageView) findViewById(R.id.bt_main_image);
        list_reaprethistory = (ListView) findViewById(R.id.list_repeathistory);
    }

    private void InitialAction() {
        // Other
        if (mGBGCM.getRegistrationId().isEmpty()) {
            mGBGCM.setMagicLenGCMListener(this);
            mGBGCM.openGCM();
        } else {
            Log.i("GCM", mGBGCM.getRegistrationId());
            RegisterGCMTask(mGBGCM.getRegistrationId());
        }
        //GCM Refresh
        mRefreshReceiver.setOnrefreshListener(this);

        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction("Refresh");
        registerReceiver(mRefreshReceiver, intentFilter);
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
        list_reaprethistory.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int pos, long l) {
                PARecord par = palist.get(pos);
                if (par.tag.equals(PARecord.TAG_Problem)) {
                    Intent i = new Intent(ctxt, Act_Responses.class);
                    i.putExtra("PAR", par);
                    i.putExtra("PRSNo", par.getPRSNo());
                    i.putExtra("Status", par.getProblemStatus());
                    i.putExtra("Rate", par.getSatisfactionDegree());
                    startActivityForResult(i, ResponseAct);
                } else {
                    //Toast.makeText(ctxt, "MPSNo = " + par.getMPSNo(), Toast.LENGTH_SHORT).show();
//                    Intent i = new Intent(ctxt, Act_Announcement.class);
//                    i.putExtra("MPSNo", par.getMPSNo());
//                    startActivity(i);
                }
            }
        });
        list_reaprethistory.setAdapter(pa_adapter);

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
            par.tag = PARecord.TAG_Problem;
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
            par.tag = PARecord.TAG_Announcement;
            par.setMPSNo(ar.getMPSNo());
            par.setPushContent(ar.getPushContent());
            par.setCreateID(ar.getCreateID());
            par.setCreateDate(ar.getCreateDate());
            palist.add(par);
        }

        for (int i = 0; i < palist.size() - 1; i++) {
            boolean swapped = false;
            for (int j = 0; j < palist.size() - i - 1; j++) {
                if (palist.get(j).tag.equals(PARecord.TAG_Problem)) {
                    if (palist.get(j + 1).tag.equals(PARecord.TAG_Problem)) {
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
                    if (palist.get(j + 1).tag.equals(PARecord.TAG_Problem)) {
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

    public void onDestroy() {
        unregisterReceiver(mRefreshReceiver);
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
                case ResponseAct:
                    LoadingAllProblem();
                    break;
                case PICK_PICTURE:
                    Uri uri = data.getData();
                    doCropPhoto(uri);
                    break;
                case TRIM_PICTURE:
                    final Bitmap result = data.getParcelableExtra("data");
                    ImageView iv = new ImageView(ctxt);
                    iv.setImageBitmap(result);
                    new AlertDialog.Builder(ctxt, AlertDialog.THEME_HOLO_LIGHT).setView(iv).setPositiveButton("OK", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialogInterface, int i) {
                            UploadPhoto(result);
                        }
                    }).setNegativeButton("Cancel", null).show();
                    break;

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
