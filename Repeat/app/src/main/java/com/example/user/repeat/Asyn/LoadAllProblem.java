package com.example.user.repeat.Asyn;

import android.os.AsyncTask;

import com.example.user.repeat.Other.Code;
import com.example.user.repeat.Other.HttpConnection;
import com.example.user.repeat.Other.ProblemRecord;
import com.example.user.repeat.Other.URLs;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by v on 2016/2/17.
 */
public class LoadAllProblem extends AsyncTask<String, Integer, Integer> {
    public interface OnLoadAllProblemListener {
        void finish(Integer result,List<ProblemRecord> list);
    }

    private final HttpConnection conn;
    private final OnLoadAllProblemListener mListener;
    private List<ProblemRecord> list;
    private String fNo;
    private String cNo;

    public LoadAllProblem(HttpConnection conn, OnLoadAllProblemListener mListener) {
        this.conn = conn;
        this.mListener = mListener;
        this.list = new ArrayList<>();
    }

    @Override
    protected Integer doInBackground(String... datas) {
        Integer result = Code.ConnectTimeOut;
        fNo = datas[0];
        cNo = datas[1];
        try {
            // put "phone" post out, get json
            List<NameValuePair> postFields = new ArrayList<>();
			postFields.add(new BasicNameValuePair("role", Code.Flabor));
            postFields.add(new BasicNameValuePair("FLaborNo", fNo));
            postFields.add(new BasicNameValuePair("CustomerNo", cNo));
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
                        list.add(fproblem);
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
        super.onPostExecute(result);
        mListener.finish(result,list);
    }
}
