package com.example.user.repeat.Asyn;

import android.os.AsyncTask;
import android.util.Log;

import com.example.user.repeat.Other.AnnouncementRecord;
import com.example.user.repeat.Other.Code;
import com.example.user.repeat.Other.HttpConnection;
import com.example.user.repeat.Other.ProblemRecord;
import com.example.user.repeat.Other.ProblemResponse;
import com.example.user.repeat.Other.URLs;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by hao_jun on 2016/2/17.
 */
public class LoadAllAnnouncement extends AsyncTask<String, Integer, Integer> {
    public interface OnLoadAllAnnouncementListener {
        void finish(Integer result, List<AnnouncementRecord> list);
    }

    private final OnLoadAllAnnouncementListener mListener;
    private List<AnnouncementRecord> list;
    private String fNo;
    private String cNo;

    public LoadAllAnnouncement(OnLoadAllAnnouncementListener mListener) {
        this.mListener = mListener;
        this.list = new ArrayList<>();
    }

    @Override
    protected Integer doInBackground(String... datas) {
        Integer result = Code.ConnectTimeOut;
        fNo = datas[0];
        cNo = datas[1];
        try {
            List<NameValuePair> postFields = new ArrayList<>();
            postFields.add(new BasicNameValuePair("FLaborNo", fNo));
            postFields.add(new BasicNameValuePair("CustomerNo", cNo));
            JSONObject jobj = new HttpConnection().PostGetJson(URLs.url_allannouncement, postFields);
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
                        list.add(fproblem);
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
        super.onPostExecute(result);
        mListener.finish(result, list);
    }
}
