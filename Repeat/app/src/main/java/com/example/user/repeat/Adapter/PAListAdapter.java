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

import com.example.user.repeat.Asyn.LoadPhoto;
import com.example.user.repeat.Other.BitmapTransformer;
import com.example.user.repeat.Other.Code;
import com.example.user.repeat.Other.HttpConnection;
import com.example.user.repeat.Other.Net;
import com.example.user.repeat.Other.PARecord;
import com.example.user.repeat.Other.URLs;
import com.example.user.repeat.Other.Uti;
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
            switch (par.getProblemStatus()) {
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
            // photo
            if (par.getResponseRole().equals(Code.Flabor)) {
                LoadPhoto(tag.photo, par.getResponseRole(), par.getFLaborNo(), par.getCustomerNo());
            } else {
                LoadPhoto(tag.photo, par.getResponseRole(), par.getResponseID());
            }
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

    private void LoadPhoto(CircleImageView circleImageView, String... datas) {
        if (Net.isNetWork(getContext())) {
            LoadPhoto task = new LoadPhoto(circleImageView, new HttpConnection(), new LoadPhoto.OnLoadPhotoListener() {
                public void finish() {

                }
            });
            task.execute(datas);
        }
    }
}
