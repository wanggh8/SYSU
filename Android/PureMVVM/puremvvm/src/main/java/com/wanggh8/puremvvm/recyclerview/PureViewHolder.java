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

import android.view.View;

import androidx.annotation.IdRes;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

/**
 * @author wanggh8
 * @version V1.0
 * @date 2020/12/7
 */
public abstract class PureViewHolder<T extends PureItem> extends RecyclerView.ViewHolder {

    /**
     * 构造方法
     * 初始化父类成员变量
     * public final View itemView;
     *
     * @param itemView View
     */
    public PureViewHolder(@NonNull View itemView) {
        super(itemView);
    }

    /**
     * 数据绑定抽象方法
     *
     * @param t 数据对象
     * @param position 位置
     */
    public abstract void onBind(T t, int position);

    /**
     * 封装findViewById
     *
     * @param id View id
     * @param <T> View或其子类
     * @return T
     */
    public <T extends View> T findViewById(@IdRes int id) {
        return (T) itemView.findViewById(id);
    }
}


