package com.example.user.repeat.Adapter;

import android.content.Context;
import android.graphics.Bitmap;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.example.user.repeat.ImageLoadHelp.ImageLoader;
import com.example.user.repeat.Other.BitmapTransformer;
import com.example.user.repeat.Other.Code;
import com.example.user.repeat.Other.MyTime;
import com.example.user.repeat.Other.ProblemResponse;
import com.example.user.repeat.Other.User;
import com.example.user.repeat.R;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

/**
 * Created by v on 2016/2/16.
 */
public class ResponseListAdapter extends MyBaseAdapter {

    private List<ProblemResponse> list;
    private ImageLoader imageLoader;

    public ResponseListAdapter(Context context, List<ProblemResponse> list) {
        super(context);
        this.list = list;
        this.imageLoader = new ImageLoader(context);
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
        ViewHolder tag;
        if (v == null) {
            v = getInflater().inflate(R.layout.block_flabor, null);
            tag = new ViewHolder();
            tag.f_block = (LinearLayout) v.findViewById(R.id.block_flabor);
            tag.f_content = (TextView) v.findViewById(R.id.tv_item_flabor_content);
            tag.f_photo = (CircleImageView) v.findViewById(R.id.img_item_flabor_photo);
            tag.f_name = (TextView) v.findViewById(R.id.tv_item_flabor_name);
            tag.f_datetime = (TextView) v.findViewById(R.id.tv_item_flabor_datetime);
            v.setTag(tag);
        } else {
            tag = (ViewHolder) v.getTag();
        }

        tag.f_content.setText(item.getResponseContent());
        if (item.getResponseRole().equals(Code.Flabor)) {
            Bitmap photo =BitmapTransformer.Base64ToBitmap(User.getUser().getLaborPhoto());
            if(photo != null) {
                tag.f_photo.setImageBitmap(photo);
            }
        } else {
            imageLoader.DisplayImage(item.getResponseID(), tag.f_photo);
        }
        tag.f_name.setText(item.getResponseID());
        tag.f_datetime.setText(MyTime.convertTimeForResponse(item.getResponseDate()));

        return v;
    }

    public static class ViewHolder {
        public LinearLayout f_block;
        public TextView f_content;
        public CircleImageView f_photo;
        public TextView f_name;
        public TextView f_datetime;
    }
}
