package com.example.wang.project2;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.LinkedList;

public class MyAdapter extends BaseAdapter {

    private LinkedList<CommentInfo> mData;
    private Context mContext;

    public MyAdapter(LinkedList<CommentInfo> mData, Context mContext) {
        this.mData = mData;
        this.mContext = mContext;
    }

    @Override
    public int getCount() {
        return mData.size();
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    public void add(CommentInfo data) {
        if (mData == null) {
            mData = new LinkedList<>();
        }
        mData.add(data);
        notifyDataSetChanged();
    }
    public void remove(int position) {
        if(mData != null) {
            mData.remove(position);
        }
        notifyDataSetChanged();
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        convertView = LayoutInflater.from(mContext).inflate(R.layout.item,parent,false);
        ImageView img_icon = (ImageView) convertView.findViewById(R.id.myicon);
        ImageView img_zan = (ImageView) convertView.findViewById(R.id.zan);
        TextView txt_aNum = (TextView) convertView.findViewById(R.id.num);
        TextView txt_aDate = (TextView) convertView.findViewById(R.id.date);
        TextView txt_aName = (TextView) convertView.findViewById(R.id.name);
        TextView txt_aSpeak = (TextView) convertView.findViewById(R.id.says);
        img_icon.setImageBitmap(mData.get(position).getIcon());
        txt_aName.setText(mData.get(position).getName());
        txt_aSpeak.setText(mData.get(position).getSpeak());
        txt_aDate.setText(mData.get(position).getDate());
        txt_aNum.setText(String.valueOf(mData.get(position).getNum()));
        img_zan.setImageResource(mData.get(position).getZan());
        return convertView;
    }

    @Override
    public Object getItem(int i) {
        return i;
    }
}
