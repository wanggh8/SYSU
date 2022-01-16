/*
 * Copyright 2020-present wanggh8
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.wanggh8.puremvvm.recyclerview;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import static com.wanggh8.puremvvm.config.PureConfig.FAST_CLICK_DELAY_TIME;

/**
 * 通用Adapter基类
 *
 * @author wanggh8
 * @version V1.0
 * @date 2020/12/8
 */
public abstract class PureAdapter<T extends PureItem> extends RecyclerView.Adapter<PureViewHolder<T>>{

    protected Context context;
    // 数据集
    protected List<T> list;
    // 上次点击时间
    protected long lastClickTime = 0;
    // 布局解释器
    public LayoutInflater inflater;

    public PureAdapter(Context context, List<T> list) {
        this.context = context;
        this.list = list;
        inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    public PureAdapter(Context context) {
        this.context = context;
        this.list = new ArrayList<>();
        inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public int getItemViewType(int position) {
        return list.get(position).getItemType();
    }

    /* * * * * * * * ViewHolder * * * * * * * */

    /**
     * 有布局服务的ViewHolder抽象构造方法
     *
     * @param inflater 布局服务
     * @param parent 父ViewGroup
     * @param viewType The view type of the new View.
     * @return BaseViewHolder<T>
     */
    public abstract PureViewHolder<T> onCreateViewHolder(LayoutInflater inflater, ViewGroup parent, int viewType);

    /**
     * 无布局服务的ViewHolder构造方法
     *
     * @param parent 父ViewGroup
     * @param viewType The view type of the new View.
     * @return BaseViewHolder<T>
     */
    @NonNull
    @Override
    public PureViewHolder<T> onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if (inflater == null) {
            inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        }
        return onCreateViewHolder(inflater, parent, viewType);
    }

    /**
     * ViewHolder数据绑定回调
     *
     * @param holder 当前ViewHolder
     * @param position 当前数据位置
     */
    @Override
    public void onBindViewHolder(@NonNull PureViewHolder<T> holder, int position) {
        // 调用抽象接口绑定数据
        holder.onBind(getItem(position), position);
        // 使用recyclerview:1.2.0以后需要使用
        // holder.getBindingAdapterPosition();
        // 设置点击侦听事件
        onClickEvent(holder.itemView, holder.getAdapterPosition());
    }



    // ------------------------------------------------------------------------
    // 点击事件侦听
    // ------------------------------------------------------------------------

    protected ItemClickListener<T> onItemClickListener;

    protected ItemLongClickListener<T> onItemLongClickListener;

    public interface ItemClickListener<T> {
        /**
         * 点击事件回调
         * @param position 点击位置
         * @param bean 点击对应的数据实体类
         */
        void onItemClick(int position, T bean);
    }

    public interface ItemLongClickListener<T> {
        /**
         * 长按事件回调
         * @param view itemView
         * @param position 点击位置
         * @param bean 点击对应的数据实体类
         */
        void onItemLongClick(View view, int position, T bean);
    }

    public void setSimpleOnItemClickListener(ItemClickListener<T> itemClickListener) {
        this.onItemClickListener = itemClickListener;
    }

    public void setSimpleOnItemLongClickListener(ItemLongClickListener<T> itemLongClickListener) {
        this.onItemLongClickListener = itemLongClickListener;
    }

    /**
     * 点击事件
     *
     * @param v ViewHolder.itemView
     * @param position 当前数据位置
     */
    public void onClickEvent(View v, int position) {
        v.setOnClickListener(view -> {
            long currentTime = Calendar.getInstance().getTimeInMillis();
            if (currentTime - lastClickTime > FAST_CLICK_DELAY_TIME) {
                lastClickTime = currentTime;
                onItemClickListener.onItemClick(position, getItem(position));
            }
        });

        if (onItemLongClickListener != null) {
            v.setOnLongClickListener(view -> {
                onItemLongClickListener.onItemLongClick(v, position, getItem(position));
                return true;
            });
        }
    }

    // ------------------------------------------------------------------------
    // 数据类
    // ------------------------------------------------------------------------

    /**
     * 获取Item
     *
     * @param position 位置
     * @return
     */
    public T getItem(int position) {
        return list == null ? null : list.get(position);
    }

    /**
     * 获取Item数目
     *
     * @return int 数目
     */
    @Override
    public int getItemCount() {
        return list == null ? 0 : list.size();
    }

    /**
     * 获取数据列表List
     *
     * @return List
     */
    public List<T> getCollection() {
        return list;
    }

    /**
     * 设置数据列表List
     *
     * @param list 数据列表
     */
    public void setCollection(@Nullable List<T> list) {
        if (list == null) {
            list = new ArrayList<>();
        }
        this.list.clear();
        this.list.addAll(list);
        this.notifyDataSetChanged();
    }

    /**
     * 在数据列表后添加list
     * @param list 数据列表
     */
    public void appendCollection(List<T> list) {
        if (this.list == null) {
            this.list = new ArrayList<>();
        }
        this.list.addAll(list);
        this.notifyDataSetChanged();
    }

    /**
     * 在数据列表具体位置添加
     *
     * @param pos 期望添加的数据的在列表中的第一个位置
     * @param list 数据列表
     */
    public void insertCollection(int pos, List<T> list) {
        if (this.list == null) {
            this.list = new ArrayList<>();
        }
        this.list.addAll(pos, list);
        this.notifyDataSetChanged();
    }

}
