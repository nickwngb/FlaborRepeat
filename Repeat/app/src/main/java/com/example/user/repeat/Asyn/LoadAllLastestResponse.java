package com.example.user.repeat.Asyn;

import android.os.AsyncTask;
import android.util.Log;

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
public class LoadAllLastestResponse extends AsyncTask<List<Integer>, Integer, Integer> {
    public interface OnLoadAllResponseListener {
        void finish(Integer result, List<ProblemResponse> list);
    }

    private final OnLoadAllResponseListener mListener;
    private List<ProblemResponse> list;
    private List<Integer> PRSNos;

    public LoadAllLastestResponse(OnLoadAllResponseListener mListener) {
        this.mListener = mListener;
        this.list = new ArrayList<>();
    }

    @Override
    protected Integer doInBackground(List<Integer>... datas) {
        Integer result = Code.ConnectTimeOut;
        PRSNos = datas[0];
        try {
            List<NameValuePair> postFields = new ArrayList<>();
            for (Integer PRSNo : PRSNos) {
                Log.d("LoadAllLastestResponse", PRSNo + "");
                postFields.add(new BasicNameValuePair("PRSNos", PRSNo + ""));
            }
            JSONObject jobj = new HttpConnection().PostGetJson(URLs.url_response, postFields);
            if (jobj != null) {
                result = jobj.getInt("success");
                if (result == Code.Success) {
                    JSONArray array = jobj.getJSONArray("fresponse");
                    for (int i = 0; i < array.length(); i++) {
                        JSONObject ajobj = array.getJSONObject(i);
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
            Log.i("LoadAllLastestResponse", e.toString());
        }
        return result;
    }

    @Override
    protected void onPostExecute(Integer result) {
        super.onPostExecute(result);
        mListener.finish(result, list);
    }
}
