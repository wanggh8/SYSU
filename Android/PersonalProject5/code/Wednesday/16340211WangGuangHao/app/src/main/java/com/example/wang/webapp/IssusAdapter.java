package com.example.wang.webapp;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.v7.widget.CardView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewDebug;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;

import java.util.List;
import java.util.Map;

public class IssusAdapter extends BaseAdapter {
    private Context mContext;
    private List<Issue> mList;
    public IssusAdapter(Context context, List<Issue> List) {
        mContext = context;
        mList = List;
    }

    class ViewHolder {
        TextView name;
        TextView date;
        TextView state;
        TextView detail;
    }

    @Override
    public int getCount() {
        return mList.size();
    }

    @Override
    public Object getItem(int i) {
        return mList.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(final int i, View view, ViewGroup parent) {
        final ViewHolder viewHolder;
        if (view == null) {
            viewHolder = new ViewHolder();
            view =  LayoutInflater.from(mContext).inflate(R.layout.issue_item,parent,false);
            viewHolder.name = view.findViewById(R.id.name2);
            viewHolder.date = view.findViewById(R.id.date2);
            viewHolder.state = view.findViewById(R.id.state2);
            viewHolder.detail = view.findViewById(R.id.detai2);
            view.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) view.getTag();
        }
        viewHolder.name.setText("Title: "+ mList.get(i).getTitle());
        viewHolder.date.setText("创建时间: "+ mList.get(i).getCreated_at());
        viewHolder.state.setText("问题状态: "+ mList.get(i).getState());
        viewHolder.detail.setText("问题描述: "+ mList.get(i).getBody());

        return view;
    }



    public void refresh( List<Issue> list) {
        mList = list;
        notifyDataSetChanged();
    }

}
