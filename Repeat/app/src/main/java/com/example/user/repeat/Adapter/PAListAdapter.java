package com.example.user.repeat.Adapter;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.AsyncTask;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.user.repeat.Other.BitmapTransformer;
import com.example.user.repeat.Other.Code;
import com.example.user.repeat.Other.HttpConnection;
import com.example.user.repeat.Other.Net;
import com.example.user.repeat.Other.PARecord;
import com.example.user.repeat.Other.URLs;
import com.example.user.repeat.R;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONException;
import org.json.JSONObject;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

/**
 * Created by user on 2015/12/10.
 */
public class PAListAdapter extends MyBaseAdapter {

    private List<PARecord> list;

    public PAListAdapter(Context context, List<PARecord> list) {
        super(context);
        this.list = list;
    }

    public int getCount() {
        return list.size();
    }

    @Override
    public PARecord getItem(int position) {
        return list.get(position);
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View v, ViewGroup parent) {
        ViewTag tag;
        if (v == null) {
            v = getInflater().inflate(R.layout.item_problemlist, null);
            tag = new ViewTag();
            tag.photo = (CircleImageView) v.findViewById(R.id.img_item_photo);
            tag.name = (TextView) v.findViewById(R.id.tv_item_name);
            tag.datetime = (TextView) v.findViewById(R.id.tv_item_createproblemdate);
            tag.content = (TextView) v.findViewById(R.id.tv_item_problemcontent);
            tag.status = (ImageView) v.findViewById(R.id.img_item_status);
            v.setTag(tag);
        } else {
            tag = (ViewTag) v.getTag();
        }
        PARecord par = getItem(position);
        if (par.tag.equals(PARecord.TAG_Problem)) {
            // setText
            tag.name.setText(par.getResponseID());
            tag.datetime.setText(par.getResponseDate());
            tag.content.setText(par.getResponseContent());
            // set status
            switch (getItem(position).getProblemStatus()) {
                case Code.Untreated:
                    tag.status.setBackground(getResources().getDrawable(R.drawable.item_bg_untreated));
                    break;
                case Code.Processing:
                    tag.status.setBackground(getResources().getDrawable(R.drawable.item_bg_processing));
                    break;
                case Code.Completed:
                    tag.status.setBackground(getResources().getDrawable(R.drawable.item_bg_completed));
                    break;
            }
            //LoadImage(tag.Photo, par.getResponseRole());
            Bitmap bm = BitmapFactory.decodeResource(getResources(), R.drawable.test2);
            tag.photo.setImageBitmap(bm);
        } else {
            tag.name.setText(par.getCreateID());
            tag.datetime.setText(par.getCreateDate());
            tag.content.setText(par.getPushContent());
            tag.status.setBackgroundColor(Color.TRANSPARENT);
        }


        return v;
    }

    static class ViewTag {
        public CircleImageView photo;
        public TextView name;
        public TextView datetime;
        public TextView content;
        public ImageView status;
    }

    private static final String F = "0";
    private static final String M = "1";

    private void LoadImage(CircleImageView circleImageView, String... params) {
        if (Net.isNetWork(getContext())) {
            if (params.length == 2) {
                new LoadImage(circleImageView, params[0], params[1]).execute();
            } else if (params.length == 1) {
                new LoadImage(circleImageView, params[0]).execute();
            }
        }
    }

    class LoadImage extends AsyncTask<String, Integer, Bitmap> {

        private WeakReference<CircleImageView> rf_circleImg;
        private String role;
        private String CustomerNo;
        private String FLaborNo;

        private String UserID;

        public LoadImage(CircleImageView circleImageView, String CustomerNo, String FLaborNo) {
            rf_circleImg = new WeakReference<>(circleImageView);
            this.CustomerNo = CustomerNo;
            this.FLaborNo = FLaborNo;
            this.role = F;
        }

        public LoadImage(CircleImageView circleImageView, String UserID) {
            rf_circleImg = new WeakReference<>(circleImageView);
            this.UserID = UserID;
            this.role = M;
        }

        @Override
        protected Bitmap doInBackground(String... datas) {
            try {
                List<NameValuePair> params = new ArrayList<>();
                if (role.equals(F)) {
                    params.add(new BasicNameValuePair("CustomerNo", CustomerNo));
                    params.add(new BasicNameValuePair("FLaborNo", FLaborNo));
                } else if (role.equals(M)) {
                    params.add(new BasicNameValuePair("UserID", UserID));
                }
                HttpConnection conn = new HttpConnection();
                JSONObject jobj = conn.PostGetJson(URLs.url_loadimage, params);
                if (jobj == null)
                    return null;
                int result = jobj.getInt("success");
                if (result == Code.Success) {
                    String base64 = jobj.getString("photo");
                    return BitmapTransformer.Base64ToBitmap(base64);
                }
            } catch (JSONException e) {
                Log.i("JSONException", e.toString());
            }
            return null;
        }

        @Override
        protected void onPostExecute(Bitmap bitmap) {
            super.onPostExecute(bitmap);
            if (rf_circleImg != null) {
                ImageView imageView = rf_circleImg.get();
                if (imageView != null) {
                    if (bitmap != null) {
                        imageView.setImageBitmap(bitmap);
                    } else {
//                        Drawable placeholder = imageView.getContext().getResources().getDrawable(R.drawable.placeholder);
//                        imageView.setImageDrawable(placeholder);
                    }
                }
            }
        }
    }
}
