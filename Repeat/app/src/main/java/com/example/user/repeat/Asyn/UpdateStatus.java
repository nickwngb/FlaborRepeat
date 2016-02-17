package com.example.user.repeat.Asyn;

import android.os.AsyncTask;

import com.example.user.repeat.Other.Code;
import com.example.user.repeat.Other.HttpConnection;
import com.example.user.repeat.Other.URLs;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by hao_jun on 2016/2/17.
 */
public class UpdateStatus extends AsyncTask<String, Integer, Integer> {

    public interface OnUpdateStatusListener {
        void finish(Integer result);
    }

    private final HttpConnection conn;
    private final OnUpdateStatusListener mListener;
    private String status;
    private String PRSNo;

    public UpdateStatus(HttpConnection conn, OnUpdateStatusListener mListener) {
        this.conn = conn;
        this.mListener = mListener;
    }

    @Override
    protected Integer doInBackground(String... datas) {
        Integer result = Code.ConnectTimeOut;
        PRSNo = datas[0];
        status = datas[1];
        try {
            // put "phone" post out, get json
            List<NameValuePair> postFields = new ArrayList<>();
            postFields.add(new BasicNameValuePair("PRSNo", PRSNo));
            postFields.add(new BasicNameValuePair("ProblemStatus", status));
            JSONObject jobj = conn.PostGetJson(URLs.url_updatestatus, postFields);
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
        super.onPostExecute(result);
        mListener.finish(result);
    }
}
