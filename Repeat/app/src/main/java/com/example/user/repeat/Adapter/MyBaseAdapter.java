package com.example.user.repeat.Adapter;

import android.content.Context;
import android.content.res.Resources;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

/**
 * Created by v on 2016/2/16.
 */
public abstract class MyBaseAdapter extends BaseAdapter {
    private final Context context;
    private final Resources resources;
    private final LayoutInflater inflater;

    public MyBaseAdapter(Context context) {
        this.context = context;
        this.resources = context.getResources();
        this.inflater = LayoutInflater.from(context);
    }

    public Context getContext() {
        return context;
    }

    public Resources getResources() {
        return resources;
    }

    public LayoutInflater getInflater() {
        return inflater;
    }

    public abstract int getCount();


    public abstract Object getItem(int position);

    public long getItemId(int position) {
        return 0;
    }

    public abstract View getView(int position, View convertView, ViewGroup parent);
}
