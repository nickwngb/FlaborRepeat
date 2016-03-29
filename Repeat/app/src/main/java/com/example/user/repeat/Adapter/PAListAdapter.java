package com.example.user.repeat.Adapter;

import android.content.Context;
import android.graphics.Color;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.user.repeat.ImageLoadHelp.ImageLoader;
import com.example.user.repeat.Other.BitmapTransformer;
import com.example.user.repeat.Other.Code;
import com.example.user.repeat.Other.MyTime;
import com.example.user.repeat.Other.PARecord;
import com.example.user.repeat.Other.User;
import com.example.user.repeat.R;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

/**
 * Created by user on 2015/12/10.
 */
public class PAListAdapter extends MyBaseAdapter {

    private List<PARecord> list;
    private ImageLoader imageLoader;

    public PAListAdapter(Context context, List<PARecord> list) {
        super(context);
        this.list = list;
        this.imageLoader = new ImageLoader(context);
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
        final PARecord par = getItem(position);
        if (par.tag.equals(PARecord.TAG_Problem)) {
            // setText
            tag.name.setText(par.getResponseID());
            tag.datetime.setText(MyTime.convertTime(par.getResponseDate()));
            tag.content.setText(par.getResponseContent());
            // set status
            if (par.getProblemStatus() != null) {
                switch (par.getProblemStatus()) {
                    case Code.Untreated:
                        tag.status.setBackground(getResources().getDrawable(R.drawable.status_untreated));
                        break;
                    case Code.Processing:
                        //tag.status.setBackground(getResources().getDrawable(R.drawable.item_bg_processing));
                        break;
                    case Code.Completed:
                        tag.status.setBackground(getResources().getDrawable(R.drawable.status_completed));
                        break;
                }
            }
            // photo
            if (par.getResponseRole() != null) {
                if (par.getResponseRole().equals(Code.Flabor)) {
                    tag.photo.setImageBitmap(BitmapTransformer.Base64ToBitmap(User.getUser().getLaborPhoto()));
                } else {

                    imageLoader.DisplayImage(par.getResponseID(), tag.photo);
                }
            }
        } else {
            tag.name.setText(par.getCreateID());
            tag.datetime.setText(MyTime.convertTime(par.getCreateDate()));
            tag.content.setText(par.getPushContent());
            tag.status.setBackgroundColor(Color.TRANSPARENT);
            imageLoader.DisplayImage(par.getCreateID(), tag.photo);
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
}
