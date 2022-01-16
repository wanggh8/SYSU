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
 * 单布局 数据绑定 Adapter基类
 *
 * @author wanggh8
 * @version V1.0
 * @date 2020/12/8
 */
public abstract class PureSimpleDataBindingAdapter<T extends PureItem, B extends ViewDataBinding> extends PureDataBindingAdapter<T, B>{

    protected int layout = 0;

    public PureSimpleDataBindingAdapter(Context context, int layout) {
        super(context);
        this.layout = layout;
    }

    public int getLayout() {
        return layout;
    }

    public void setLayout(int layout) {
        this.layout = layout;
    }

    @Override
    protected int getLayoutResId(int viewType) {
        return layout;
    }
}