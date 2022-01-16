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

import java.io.Serializable;

/**
 * @author wanggh8
 * @version V1.0
 * @date 2020/12/7
 */
public interface PureItem extends Serializable {

    /**
     * item类别
     * <p>
     * RecyclerView多布局使用
     * 使用时，可返回item布局用于区分
     * 例如:
     * return R.layout.item_fruit;
     * 同一个实体类实现多布局时
     * 也可以自定义标识
     *
     * @return 布局类别标识，默认为0
     */
    default int getItemType() {
        return 0;
    }
}
