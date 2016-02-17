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
public class AddProblem extends AsyncTask<String, Integer, Integer> {
    public interface OnAddProblemListener {
        void finish(Integer result);
    }

    private final HttpConnection conn;
    private final OnAddProblemListener mListener;


    public AddProblem(HttpConnection conn, OnAddProblemListener mListener) {
        this.conn = conn;
        this.mListener = mListener;
    }

    @Override
    protected Integer doInBackground(String... datas) {
        Integer result = Code.ConnectTimeOut;
        try {
            // put "phone" post out, get json
            List<NameValuePair> postFields = new ArrayList<>();
            postFields.add(new BasicNameValuePair("FLaborNo", datas[0]));
            postFields.add(new BasicNameValuePair("CustomerNo", datas[1]));
            postFields.add(new BasicNameValuePair("ProblemDescription", datas[2]));
            postFields.add(new BasicNameValuePair("CreateProblemDate", datas[3]));
            postFields.add(new BasicNameValuePair("ProblemStatus", datas[4]));
            postFields.add(new BasicNameValuePair("ResponseID", datas[5]));
            postFields.add(new BasicNameValuePair("ResponseRole", datas[6]));
            JSONObject jobj = conn.PostGetJson(URLs.url_addproblem, postFields);
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
