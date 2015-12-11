package com.example.user.repeat.Adapter;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.example.user.repeat.Other.ProblemRecord;
import com.example.user.repeat.R;

import java.util.List;

/**
 * Created by user on 2015/12/10.
 */
public class ProblemList extends BaseAdapter {
    List<ProblemRecord> list;
    private LayoutInflater myInflater;

    public ProblemList(Context context, List<ProblemRecord> list){
        myInflater = LayoutInflater.from(context);
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
    public View getView(int position, View view, ViewGroup parent) {
        ViewTag tag;
        if(view == null){
            tag = new ViewTag();
            view = myInflater.inflate(R.layout.item_problemlist, null);
            tag.CreateProblemDate= (TextView) view.findViewById(R.id.txt_item_createproblemdate);
            tag.ProblemContent = (TextView) view.findViewById(R.id.txt_problemcontent);
            tag.ProblemStatus = (TextView) view.findViewById(R.id.txt_problemstatus);
            view.setTag(tag);
        }else{
            tag = (ViewTag) view.getTag();
        }
        tag.CreateProblemDate.setText(getItem(position).createproblemdate);
        tag.ProblemContent.setText(getItem(position).problemdescription);
        tag.ProblemStatus.setText(getItem(position).problemstatus);
        if(tag.ProblemStatus.getText().equals("Complete")){
            tag.ProblemStatus.setBackgroundColor(Color.parseColor("#00FF00"));
        }else if(tag.ProblemStatus.getText().equals("Processing")){
            tag.ProblemStatus.setBackgroundColor(Color.parseColor("#FF0000"));
        }else{
            tag.ProblemStatus.setBackgroundColor(Color.parseColor("#FFFF00"));
        }
        return view;
    }
    class ViewTag {
        TextView CreateProblemDate;
        TextView ProblemContent;
        TextView ProblemStatus;
    }
}
