package com.example.user.repeat.Asyn;

import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.util.Log;

import com.example.user.repeat.Other.BitmapTransformer;
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


public class UploadPhoto extends AsyncTask<String, Integer, Integer> {
    public interface OnUpdatePhotoListener {
        void finish(Integer result);

    }

    private HttpConnection conn;
    private OnUpdatePhotoListener mListener;
    private String role;
    private String photo;
    private String fNo;
    private String cNo;

    public UploadPhoto(HttpConnection conn, OnUpdatePhotoListener mListener, Bitmap photo) {
        this.conn = conn;
        this.mListener = mListener;
        this.photo = BitmapTransformer.BitmapToBase64(photo);
    }

    @Override
    protected Integer doInBackground(String... datas) {
        Integer result = Code.ConnectTimeOut;
        try {
            role = datas[0];
            fNo = datas[1];
            cNo = datas[2];
            List<NameValuePair> params = new ArrayList<>();
            params.add(new BasicNameValuePair("Role", role));
            params.add(new BasicNameValuePair("CustomerNo", cNo));
            params.add(new BasicNameValuePair("FLaborNo", fNo));
            params.add(new BasicNameValuePair("LaborPhoto", photo));
            Log.d("UploadPhoto", role + " " + cNo + " " + fNo);
            Log.d("UploadPhoto", photo);
            // Fake Data Useless
            params.add(new BasicNameValuePair("UserID", "12345"));
            params.add(new BasicNameValuePair("UserPhoto", "12345"));

            JSONObject jobj = conn.PostGetJson(URLs.url_uploadimage, params);
            if (jobj != null) {
                result = jobj.getInt("success");
            }
        } catch (JSONException e) {
            Log.i("UploadPhoto", e.toString());
        }
        return result;
    }

    @Override
    protected void onPostExecute(Integer integer) {
        super.onPostExecute(integer);
        mListener.finish(integer);
    }
}
