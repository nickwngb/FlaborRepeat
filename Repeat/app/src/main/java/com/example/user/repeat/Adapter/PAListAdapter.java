package com.example.user.repeat.Adapter;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.user.repeat.Other.Code;
import com.example.user.repeat.Other.PARecord;
import com.example.user.repeat.R;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

/**
 * Created by user on 2015/12/10.
 */
public class PAListAdapter extends BaseAdapter {
    private Context ctxt;
    private LayoutInflater myInflater;
    private Resources res;
    private List<PARecord> list;

    public PAListAdapter(Context context, List<PARecord> list) {
        this.ctxt = context;
        this.myInflater = LayoutInflater.from(context);
        this.res = ctxt.getResources();
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
            v = myInflater.inflate(R.layout.item_problemlist, null);
            tag = new ViewTag();
            tag.CreateProblemDate = (TextView) v.findViewById(R.id.tv_item_createproblemdate);
            tag.ProblemContent = (TextView) v.findViewById(R.id.tv_item_problemcontent);
            tag.Status = (ImageView) v.findViewById(R.id.img_item_status);
            v.setTag(tag);
        } else {
            tag = (ViewTag) v.getTag();
        }
        PARecord par = getItem(position);
        if (par.tag.equals(PARecord.TAG_PRecord)) {
            // setText
            tag.CreateProblemDate.setText(par.getCreateProblemDate());
            tag.ProblemContent.setText(par.getProblemDescription());
            // set status
            switch (getItem(position).getProblemStatus()) {
                case Code.Untreated:
                    tag.Status.setBackground(res.getDrawable(R.drawable.item_bg_untreated));
                    break;
                case Code.Processing:
                    tag.Status.setBackground(res.getDrawable(R.drawable.item_bg_processing));
                    break;
                case Code.Completed:
                    tag.Status.setBackground(res.getDrawable(R.drawable.item_bg_completed));
                    break;
            }
        } else {
            tag.CreateProblemDate.setText(par.getCreateDate());
            tag.ProblemContent.setText(par.getPushContent());
            tag.Status.setBackgroundColor(Color.TRANSPARENT);
        }
        return v;
    }

    static class ViewTag {
        public CircleImageView c;
        public TextView CreateProblemDate;
        public TextView ProblemContent;
        public ImageView Status;

    }
}
