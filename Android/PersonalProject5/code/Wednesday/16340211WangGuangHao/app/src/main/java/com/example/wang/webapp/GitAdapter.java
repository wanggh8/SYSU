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

public class GitAdapter extends BaseAdapter {
    private Context mContext;
    private List<Repo> mList;
    public GitAdapter(Context context, List<Repo> List) {
        mContext = context;
        mList = List;
    }

    class ViewHolder {
        TextView name;
        TextView id;
        TextView issue;
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
            view =  LayoutInflater.from(mContext).inflate(R.layout.git_item,parent,false);
            viewHolder.name = view.findViewById(R.id.name1);
            viewHolder.id = view.findViewById(R.id.id1);
            viewHolder.issue = view.findViewById(R.id.issue1);
            viewHolder.detail = view.findViewById(R.id.detail);
            view.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) view.getTag();
        }
        if (mList.get(i).getHas_issues() == true){
            viewHolder.name.setText("项目名: "+ mList.get(i).getName());
            viewHolder.id.setText("项目id: "+ mList.get(i).getId());
            viewHolder.issue.setText("存在问题: "+ String.valueOf(mList.get(i).getOpen_issues()));
            viewHolder.detail.setText("项目描述: "+ mList.get(i).getDescription());
        }

        return view;
    }



    public void refresh( List<Repo> list) {
        mList = list;
        notifyDataSetChanged();
    }

}
