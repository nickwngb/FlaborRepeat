package com.example.user.repeat.Asyn;

import android.os.AsyncTask;
import android.util.Log;

import com.example.user.repeat.Other.Code;
import com.example.user.repeat.Other.HttpConnection;
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
 * Created by hao_jun on 2016/2/18.
 */
public class LoadAllResponse extends AsyncTask<String, Integer, Integer> {
    public interface OnLoadAllResponseListener {
        void finish(Integer result, List<ProblemResponse> list);
    }

    private final OnLoadAllResponseListener mListener;
    private List<ProblemResponse> list;
    private String PRSNo;

    public LoadAllResponse( OnLoadAllResponseListener mListener) {
        this.mListener = mListener;
        this.list = new ArrayList<>();
    }

    @Override
    protected Integer doInBackground(String... datas) {
        Integer result = Code.ConnectTimeOut;
        PRSNo = datas[0];
        try {
            List<NameValuePair> params = new ArrayList<>();
            params.add(new BasicNameValuePair("PRSNo", PRSNo));
            Log.d("LoadAllResponse", PRSNo + "");
            JSONObject jobj = new HttpConnection().PostGetJson(URLs.url_allresponse, params);
            if (jobj != null) {
                result = jobj.getInt("success");
                if (result == Code.Success) {
                    JSONArray jArray = jobj.getJSONArray("fresponse");
                    for (int i = 0; i < jArray.length(); i++) {
                        JSONObject ajobj = jArray.getJSONObject(i);
                        ProblemResponse rs = new ProblemResponse();
                        rs.setPRSNo(ajobj.getInt("PRSNo"));
                        rs.setResponseContent(ajobj.getString("ResponseContent"));
                        rs.setResponseDate(ajobj.getString("ResponseDate"));
                        rs.setResponseID(ajobj.getString("ResponseID"));
                        rs.setResponseRole(ajobj.getString("ResponseRole"));
                        list.add(rs);
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
        super.onPostExecute(result);
        mListener.finish(result, list);
    }
}
