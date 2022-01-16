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

import androidx.databinding.BindingAdapter;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

/**
 * RecyclerView绑定适配器
 *
 * @author wanggh8
 * @version V1.0
 * @date 2020/12/7
 */
public class PureRecyclerViewBindingAdapter {

    @BindingAdapter(value = {"adapter"}, requireAll = true)
    public static void setAdapter(RecyclerView recyclerView, PureDataBindingAdapter adapter) {
        if (recyclerView == null) {
            return;
        }
        recyclerView.setAdapter(adapter);
    }

    @BindingAdapter(value = {"list"}, requireAll = true)
    public static void setList(RecyclerView recyclerView, List list) {
        if (recyclerView == null) {
            return;
        }
        if (recyclerView.getAdapter() == null) {
            return;
        }

        if (recyclerView.getAdapter() instanceof PureDataBindingAdapter) {
            PureDataBindingAdapter adapter = (PureDataBindingAdapter) recyclerView.getAdapter();
            adapter.setList(list);
        }
    }

    @BindingAdapter(value = {"itemClick"}, requireAll = false)
    public static void itemClick(RecyclerView recyclerView, PureDataBindingAdapter.ItemClickListener listener) {
        if (recyclerView == null) {
            return;
        }
        if (recyclerView.getAdapter() == null) {
            return;
        }

        if (recyclerView.getAdapter() instanceof PureDataBindingAdapter) {
            PureDataBindingAdapter adapter = (PureDataBindingAdapter) recyclerView.getAdapter();
            adapter.setSimpleOnItemClickListener(listener);
        }
    }

    @BindingAdapter(value = {"itemNoDoubleClick"}, requireAll = false)
    public static void itemNoDoubleClick(RecyclerView recyclerView, PureDataBindingAdapter.ItemNoDoubleClickListener listener) {
        if (recyclerView == null) {
            return;
        }
        if (recyclerView.getAdapter() == null) {
            return;
        }

        if (recyclerView.getAdapter() instanceof PureDataBindingAdapter) {
            PureDataBindingAdapter adapter = (PureDataBindingAdapter) recyclerView.getAdapter();
            adapter.setSimpleOnItemNoDoubleClickListener(listener);
        }
    }

    @BindingAdapter(value = {"itemLongClick"}, requireAll = false)
    public static void itemLongClick(RecyclerView recyclerView, PureDataBindingAdapter.ItemLongClickListener listener) {
        if (recyclerView == null) {
            return;
        }
        if (recyclerView.getAdapter() == null) {
            return;
        }

        if (recyclerView.getAdapter() instanceof PureDataBindingAdapter) {
            PureDataBindingAdapter adapter = (PureDataBindingAdapter) recyclerView.getAdapter();
            adapter.setSimpleOnItemLongClickListener(listener);
        }
    }

}
