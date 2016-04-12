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
 * Created by asus on 2016/4/9.
 */
public class UpdateRating extends AsyncTask<String, Integer, Integer> {

    public interface OnUpdateStatusListener {
        void finish(Integer result);
    }

    private final OnUpdateStatusListener mListener;
    private String PRSNo;
    private String rating;

    public UpdateRating(OnUpdateStatusListener mListener) {
        this.mListener = mListener;
    }
    protected Integer doInBackground(String... datas) {
        Integer result = Code.ConnectTimeOut;
        PRSNo = datas[0];
        rating = datas[1];
        try {
            Log.d("UpdateRating",PRSNo);
            Log.d("UpdateRating",rating);
            List<NameValuePair> postFields = new ArrayList<>();
            postFields.add(new BasicNameValuePair("PRSNo", PRSNo));
            postFields.add(new BasicNameValuePair("Star", rating));
            JSONObject jobj = new HttpConnection().PostGetJson(URLs.url_updatestart, postFields);
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
