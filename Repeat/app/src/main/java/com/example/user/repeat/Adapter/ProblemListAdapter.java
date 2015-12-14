package com.example.user.repeat.Adapter;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.example.user.repeat.Other.Code;
import com.example.user.repeat.Other.ProblemRecord;
import com.example.user.repeat.R;

import java.util.List;

/**
 * Created by user on 2015/12/10.
 */
public class ProblemListAdapter extends BaseAdapter {
    private Context ctxt;
    private LayoutInflater myInflater;
    private Resources res;
    private List<ProblemRecord> list;

    public ProblemListAdapter(Context context, List<ProblemRecord> list) {
        ctxt = context;
        myInflater = LayoutInflater.from(context);
        res = ctxt.getResources();
        this.list = list;
    }

    public int getCount() {
        return list.size();
    }

    @Override
    public ProblemRecord getItem(int position) {
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
            tag = new ViewTag();
            v = myInflater.inflate(R.layout.item_problemlist, null);
            tag.CreateProblemDate = (TextView) v.findViewById(R.id.txt_item_createproblemdate);
            tag.ProblemContent = (TextView) v.findViewById(R.id.txt_problemcontent);
            tag.ProblemStatus = (TextView) v.findViewById(R.id.txt_problemstatus);
            v.setTag(tag);
        } else {
            tag = (ViewTag) v.getTag();
        }
        // setText
        tag.CreateProblemDate.setText(getItem(position).getCreateProblemDate());
        tag.ProblemContent.setText(getItem(position).getProblemDescription());
        tag.ProblemStatus.setText(getItem(position).getProblemStatus());
        // setStatus Color
        int statusColor = 0;
        switch (getItem(position).getProblemStatus()) {
            case Code.Completed:
                statusColor = res.getColor(R.color.green);
                break;
            case Code.Processing:
                statusColor = res.getColor(R.color.red);
                break;
            case Code.Untreated:
                statusColor = res.getColor(R.color.yellow);
                break;
        }
        tag.ProblemStatus.setTextColor(statusColor);
        return v;
    }

    class ViewTag {
        TextView CreateProblemDate;
        TextView ProblemContent;
        TextView ProblemStatus;
    }
}
