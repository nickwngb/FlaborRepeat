package com.example.user.repeat.Asyn;

import android.os.AsyncTask;
import android.util.Log;

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
 * Created by v on 2016/2/17.
 */
public class RegisterGCM extends AsyncTask<String, Integer, Integer> {
    public interface OnRegisterGCMListener {
        void finish(Integer result);
    }

    private HttpConnection conn;
    private OnRegisterGCMListener mListener;
    private String id;
    private String fNo;
    private String cNo;

    public RegisterGCM(HttpConnection conn, OnRegisterGCMListener mListener) {
        this.conn = conn;
        this.mListener = mListener;
    }

    @Override
    protected Integer doInBackground(String... datas) {
        Integer result = Code.ConnectTimeOut;
        try {
            id = datas[0];
            fNo = datas[1];
            cNo = datas[2];
            List<NameValuePair> params = new ArrayList<>();
            params.add(new BasicNameValuePair("FLaborNo", id));
            params.add(new BasicNameValuePair("CustomerNo", fNo));
            params.add(new BasicNameValuePair("FLaborNo", cNo));
            JSONObject jobj = conn.PostGetJson(URLs.url_gcm_register, params);
            if (jobj != null) {
                result = jobj.getInt("success");
            }
        } catch (JSONException e) {
            e.printStackTrace();
            Log.i("RegisterGCM", e.toString());
        }
        return result;
    }

    @Override
    protected void onPostExecute(Integer result) {
        super.onPostExecute(result);
        mListener.finish(result);
    }
}
