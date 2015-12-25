package com.example.user.repeat.Adapter;

import android.content.Context;
import android.content.res.Resources;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
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
            tag.linear_back = (LinearLayout) v.findViewById(R.id.linear_back);
            v.setTag(tag);
        } else {
            tag = (ViewTag) v.getTag();
        }
        ProblemRecord pr = getItem(position);
        // setText
        tag.CreateProblemDate.setText(pr.getCreateProblemDate());
        tag.ProblemContent.setText(pr.getProblemDescription());
        // set status
        switch (getItem(position).getProblemStatus()) {
            case Code.Untreated:
                tag.linear_back.setBackground(res.getDrawable(R.drawable.item_bg_untreated));
                break;
            case Code.Processing:
                tag.linear_back.setBackground(res.getDrawable(R.drawable.item_bg_processing));
                break;
            case Code.Completed:
                tag.linear_back.setBackground(res.getDrawable(R.drawable.item_bg_completed));
                break;
        }
        return v;
    }

    class ViewTag {
        TextView CreateProblemDate;
        TextView ProblemContent;
        LinearLayout linear_back;
    }
}
