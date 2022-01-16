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

import androidx.annotation.LayoutRes;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.databinding.DataBindingUtil;
import androidx.databinding.ViewDataBinding;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import static com.wanggh8.puremvvm.config.PureConfig.FAST_CLICK_DELAY_TIME;

/**
 * Adapter基类
 * 多布局实现
 *
 * @author wanggh8
 * @version V1.0
 * @date 2020/12/7
 */
public abstract class PureDataBindingAdapter<T extends PureItem, B extends ViewDataBinding> extends RecyclerView.Adapter<PureBindingViewHolder>{

    protected Context context;
    // 数据集
    protected List<T> list;
    // 上次点击时间
    protected long lastClickTime = 0;

    public PureDataBindingAdapter(Context context, List<T> list) {
        this.context = context;
        this.list = list;
    }

    public PureDataBindingAdapter(Context context) {
        this.context = context;
        this.list = new ArrayList<>();
    }

    @Override
    public int getItemViewType(int position) {
        return list.get(position).getItemType();
    }

    /* * * * * * * * ViewHolder * * * * * * * */

    /**
     * 无布局服务的ViewHolder构造方法
     *
     * @param parent 父ViewGroup
     * @param viewType The view type of the new View.
     * @return BaseViewHolder<T>
     */
    @NonNull
    @Override
    public PureBindingViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        B binding = DataBindingUtil.inflate(LayoutInflater.from(this.context), this.getLayoutResId(viewType), parent, false);
        PureBindingViewHolder holder = new PureBindingViewHolder(binding.getRoot());
        return holder;
    }

    /**
     * ViewHolder数据绑定回调
     *
     * @param holder 当前ViewHolder
     * @param position 当前数据位置
     */
    @Override
    public void onBindViewHolder(@NonNull PureBindingViewHolder holder, int position) {
        // 调用抽象接口绑定数据
        B binding = DataBindingUtil.getBinding(holder.itemView);
        this.onBindItem(binding, getItem(position), holder);
        if (binding != null) {
            binding.executePendingBindings();
        }
        // 使用recyclerview:1.2.0以后需要使用
        // holder.getBindingAdapterPosition();
        // 设置点击侦听事件
        // 删除条目后可能点击项目无法对应
        onClickEvent(holder.itemView, position);
    }

    /**
     * 获取布局id
     * 实现多布局的适配器重写此方法，返回不同的ViewHolder
     *
     * @param viewType
     * @return @LayoutRes int
     */
    protected abstract @LayoutRes int getLayoutResId(@LayoutRes int viewType);

    /**
     * 注意：
     * RecyclerView 中的数据有位置改变（比如删除）时一般不会重新调用 onBindViewHolder() 方法，除非这个元素不可用。
     * 为了实时获取元素的位置，我们通过 ViewHolder.getBindingAdapterPosition() 方法。
     * 多布局时，通过 itemViewType 绑定不同的item
     *
     * @param binding .
     * @param item    .
     * @param holder  .
     */
    protected abstract void onBindItem(B binding, T item, RecyclerView.ViewHolder holder);


    // ------------------------------------------------------------------------
    // 点击事件侦听
    // ------------------------------------------------------------------------

    protected ItemClickListener<T> onItemClickListener;

    protected ItemLongClickListener<T> onItemLongClickListener;

    protected ItemNoDoubleClickListener<T> onItemNoDoubleClickListener;



    public interface ItemClickListener<T> {
        /**
         * 点击事件回调
         * @param position 点击位置
         * @param item 点击对应的数据实体类
         */
        void onItemClick(int position, T item);
    }

    public interface ItemLongClickListener<T> {
        /**
         * 长按事件回调
         * @param view itemView
         * @param position 点击位置
         * @param item 点击对应的数据实体类
         */
        void onItemLongClick(View view, int position, T item);
    }

    public interface ItemNoDoubleClickListener<T> {
        /**
         * 点击事件回调
         * @param position 点击位置
         * @param item 点击对应的数据实体类
         */
        void onItemNoDoubleClick(int position, T item);
    }


    public void setSimpleOnItemClickListener(ItemClickListener<T> itemClickListener) {
        this.onItemClickListener = itemClickListener;
    }

    public void setSimpleOnItemNoDoubleClickListener(ItemNoDoubleClickListener<T> itemNoDoubleClickListener) {
        this.onItemNoDoubleClickListener = itemNoDoubleClickListener;
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

        if (onItemClickListener != null) {
            v.setOnClickListener(view -> {
                onItemClickListener.onItemClick(position, getItem(position));
            });
        }

        if (onItemNoDoubleClickListener != null) {
            v.setOnClickListener(view -> {
                long currentTime = Calendar.getInstance().getTimeInMillis();
                if (currentTime - lastClickTime > FAST_CLICK_DELAY_TIME) {
                    lastClickTime = currentTime;
                    onItemNoDoubleClickListener.onItemNoDoubleClick(position, getItem(position));
                }
            });
        }

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
    public List<T> getList() {
        return list;
    }

    /**
     * 设置数据列表List
     *
     * @param list 数据列表
     */
    public void setList(@Nullable List<T> list) {
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
    public void appendList(List<T> list) {
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
    public void insertList(int pos, List<T> list) {
        if (this.list == null) {
            this.list = new ArrayList<>();
        }
        this.list.addAll(pos, list);
        this.notifyDataSetChanged();
    }

}
