package com.example.user.repeat.Adapter;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.TextView;

import com.example.user.repeat.Other.ProblemResponse;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

/**
 * Created by v on 2016/2/16.
 */
public class ResponseListAdapter extends MyBaseAdapter {
    private List<ProblemResponse> list;

    public ResponseListAdapter(Context context, List<ProblemResponse> list) {
        super(context);
        this.list = list;
    }

    @Override
    public int getCount() {
        return list.size();
    }

    @Override
    public Object getItem(int position) {
        return list.get(position);
    }

    @Override
    public View getView(int position, View v, ViewGroup parent) {
        ProblemResponse item = (ProblemResponse) getItem(position);
        ViewHolder tag = null;
        if (v == null) {
            v = getInflater().inflate(0, null);
            tag = new ViewHolder();
            //tag
            v.setTag(tag);
        } else {
            tag = (ViewHolder) v.getTag();
        }


        return v;
    }

    public static class ViewHolder {
        public TextView content;
        public CircleImageView photo;
        public TextView name;
        public TextView datetime;
    }
}
