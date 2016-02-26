package com.example.user.repeat.Asyn;

import android.os.AsyncTask;
import android.util.Log;

import com.example.user.repeat.Other.Code;
import com.example.user.repeat.Other.HttpConnection;
import com.example.user.repeat.Other.URLs;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by hao_jun on 2016/2/18.
 */
public class Login extends AsyncTask<String, String, Integer> {
    public interface OnLoginListener {
        void finish(Integer result, String CustomerNo, String FLaborNo, String ChineseName, String LaborPhoto, String phone);
    }

    private final HttpConnection conn;
    private final OnLoginListener mListener;
    private String phone;
    private String CustomerNo;
    private String FLaborNo;
    private String ChineseName;
    private String LaborPhoto;


    public Login(HttpConnection conn, OnLoginListener mListener) {
        this.conn = conn;
        this.mListener = mListener;
    }

    @Override
    protected Integer doInBackground(String... datas) {
        Integer result = Code.ConnectTimeOut;
        phone = datas[0];
        try {
            // put "phone" post out, get json
            List<NameValuePair> postFields = new ArrayList<>();
            postFields.add(new BasicNameValuePair("phone", phone));
            JSONObject jobj = conn.PostGetJson(URLs.url_login, postFields);
            if (jobj != null) {
                result = jobj.getInt("success");
                if (result == Code.Success) {
                    JSONArray jArray = jobj.getJSONArray("finfo");
                    if (jArray != null) {
                        JSONObject finfo = jArray.getJSONObject(0);
                        if (finfo != null) {
                            CustomerNo = finfo.getString("CustomerNo");
                            FLaborNo = finfo.getString("FLaborNo");
                            ChineseName = finfo.getString("ChineseName");
                            LaborPhoto = finfo.getString("LaborPhoto");
                            Log.d("GET", CustomerNo + " " + FLaborNo);
                        }
                    }
                }
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }
        return result;
    }

    @Override
    protected void onPostExecute(Integer integer) {
        super.onPostExecute(integer);
        mListener.finish(integer, CustomerNo, FLaborNo, ChineseName, LaborPhoto, phone);
    }
}
