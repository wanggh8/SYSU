package com.example.wang.webapp;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;

import java.util.List;
import java.util.Map;

public class MyAdapter extends BaseAdapter {
    private Context mContext;
    private List<RecyclerObj> mList;

    public List<RecyclerObj> getmList() {
        return mList;
    }

    public void setmList(List<RecyclerObj> mList) {
        this.mList = mList;
    }


    public MyAdapter(Context context, List<RecyclerObj> List) {
        mContext = context;
        mList = List;
    }

    class ViewHolder {
        ImageView preview;
        SeekBar time;
        TextView info1;
        TextView info2;
        TextView info3;
        TextView author;
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
            view =  LayoutInflater.from(mContext).inflate(R.layout.item,parent,false);
            viewHolder.preview = view.findViewById(R.id.preview);
            viewHolder.time = view.findViewById(R.id.time);
            viewHolder.info1 = view.findViewById(R.id.info1);
            viewHolder.info2 = view.findViewById(R.id.info2);
            viewHolder.info3 = view.findViewById(R.id.info3);
            viewHolder.author = view.findViewById(R.id.author);
            view.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) view.getTag();
        }

        viewHolder.preview.setImageBitmap(mList.get(i).getBmp());

        viewHolder.info1.setText(mList.get(i).getData().get_info1());
        viewHolder.info2.setText(mList.get(i).getData().get_info2());
        viewHolder.info3.setText(mList.get(i).getData().getTitle());
        viewHolder.author.setText(mList.get(i).getData().getContent());
        return view;
    }


    public void refresh( List<RecyclerObj> list) {
        mList = list;
        notifyDataSetChanged();
    }

}
