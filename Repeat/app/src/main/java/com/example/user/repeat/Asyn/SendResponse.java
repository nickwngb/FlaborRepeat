package com.example.user.repeat.Asyn;

import android.os.AsyncTask;
import android.util.Log;

import com.example.user.repeat.Other.Code;
import com.example.user.repeat.Other.HttpConnection;
import com.example.user.repeat.Other.ProblemResponse;
import com.example.user.repeat.Other.URLs;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by hao_jun on 2016/2/18.
 */
public class SendResponse extends AsyncTask<String, Integer, Integer> {
    public interface OnSendResponseListener {
        void finish(Integer result);
    }

    private final OnSendResponseListener mListener;
    private String PRSNo;
    private String ResponseContent;
    private String ResponseDate;
    private String ResponseID;

    public SendResponse(OnSendResponseListener mListener) {
        this.mListener = mListener;
    }

    @Override
    protected Integer doInBackground(String... datas) {
        Integer result = Code.ConnectTimeOut;
        PRSNo = datas[0];
        ResponseContent = datas[1];
        ResponseDate = datas[2];
        ResponseID = datas[3];
        try {
            List<NameValuePair> params = new ArrayList<>();
            params.add(new BasicNameValuePair("PRSNo", PRSNo));
            params.add(new BasicNameValuePair("ResponseContent", ResponseContent));
            params.add(new BasicNameValuePair("ResponseDate", ResponseDate));
            params.add(new BasicNameValuePair("ResponseID", ResponseID));
            params.add(new BasicNameValuePair("ResponseRole", Code.Flabor));
            Log.d("SendResponse", PRSNo + " " + ResponseContent + " " + ResponseDate + " " + ResponseID);
            JSONObject jobj = new HttpConnection().PostGetJson(URLs.url_addresponse, params);
            if (jobj != null) {
                result = jobj.getInt("success");
            }
        } catch (JSONException e) {
            Log.i("SendResponse", e.toString());
        }
        return result;
    }

    @Override
    protected void onPostExecute(Integer result) {
        super.onPostExecute(result);
        mListener.finish(result);
    }
}
