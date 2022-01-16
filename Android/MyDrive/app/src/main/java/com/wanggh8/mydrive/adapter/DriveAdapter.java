package com.wanggh8.mydrive.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.wanggh8.mydrive.R;
import com.wanggh8.mydrive.base.BaseAdapter;
import com.wanggh8.mydrive.base.BaseViewHolder;
import com.wanggh8.mydrive.bean.DriveBean;

import java.util.List;

/**
 * 云盘列表Adapter
 *
 * @author wanggh8
 * @version V1.0
 * @date 2020/10/10
 */
public class DriveAdapter extends BaseAdapter<DriveBean> {

    public DriveAdapter(Context context, List<DriveBean> list) {
        super(context, list);
    }

    public DriveAdapter(Context context) {
        super(context);
    }

    @Override
    public BaseViewHolder<DriveBean> onCreateViewHolder(LayoutInflater inflater, ViewGroup parent, int viewType) {
        return new DriveItemHolder(inflater.inflate(R.layout.item_drive, parent, false));
    }

    class DriveItemHolder extends BaseViewHolder<DriveBean> {

        private ImageView ivDriveItem;
        private TextView tvDriveItem;
        private ImageView ivDriveArrow;

        public DriveItemHolder(View itemView) {
            super(itemView);
            ivDriveItem = (ImageView) findViewById(R.id.iv_drive_item);
            tvDriveItem = (TextView) findViewById(R.id.tv_drive_item);
            ivDriveArrow = (ImageView) findViewById(R.id.iv_drive_arrow);
        }

        @Override
        public void onBind(DriveBean bean, int position) {
            ivDriveItem.setImageResource(bean.getIconId());
            if (bean.getMyName() != null) {
                tvDriveItem.setText(bean.getMyName());
            }
            else {
                tvDriveItem.setText(bean.getName());
            }
        }
    }
}
