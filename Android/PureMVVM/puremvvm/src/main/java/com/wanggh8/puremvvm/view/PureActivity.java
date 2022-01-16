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

package com.wanggh8.puremvvm.view;

import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.os.Bundle;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;

import com.wanggh8.puremvvm.PureApplication;

/**
 * activity基类
 * @author wanggh8
 * @version V1.0
 * @date 2020/11/30
 */
public abstract class PureActivity extends AppCompatActivity {

    private ViewModelProvider mActivityProvider;
    private ViewModelProvider mApplicationProvider;

    // 当前活动名，调试使用
    public String TAG = getClass().getSimpleName();

    /**
     * 初始化页面参数
     */
    public abstract void initParam();

    /**
     * 在实例化布局前处理的逻辑
     **/
    public abstract void beforeInitView();

    /**
     * 实例化布局文件/组件
     **/
    public abstract void initView();

    /**
     * 在实例化布局后处理的逻辑
     **/
    public abstract void afterInitView();

    /**
     * 页面事件监听, 事件注册
     */
    public abstract void initViewObservable();

    /**
     * 点击事件处理
     *
     * 负责没有业务逻辑和model层的UI点击事件响应
     * 如：页面跳转、UI页面调整显示
     */
//    public class EventHandler {}

    /**
     * 绑定 ViewModel
     * 设置观察 LiveData 对象
     *
     * @param savedInstanceState
     */
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // 页面接受的参数方法
        initParam();
        // 初始化ViewModel
//        initViewModel();

        beforeInitView();
        // 初始化页面
        initView();
        afterInitView();
        // 页面事件监听的方法，一般用于ViewModel层转到View层的事件注册
        initViewObservable();
    }



    @Override
    protected void onStart() {
        super.onStart();
        Log.v("Lifecycle", "onStart mActivity ：" + getClass().getSimpleName());
    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.v("Lifecycle", "onResume mActivity ：" + getClass().getSimpleName());
    }

    @Override
    protected void onPause() {
        super.onPause();
        Log.v("Lifecycle", "onPause mActivity ：" + getClass().getSimpleName());
    }

    @Override
    protected void onStop() {
        super.onStop();
        Log.v("Lifecycle", "onStop mActivity ：" + getClass().getSimpleName());
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    // 框架中提供了 Application、Activity、Fragment 三个级别的作用域，
    // 通过不同作用域的 Provider 获得的 ViewModel 实例不是同一个，

    /**
     * 获取Activity作用域的ViewModel
     *
     * @param modelClass ViewModel实现类
     * @param <T> ViewModel类型
     * @return ViewModel子类
     */
    protected <T extends ViewModel> T getActivityScopeViewModel(@NonNull Class<T> modelClass) {
        if (mActivityProvider == null) {
            mActivityProvider = getActivityViewModelProvider(this);
        }
        return mActivityProvider.get(modelClass);
    }

    /**
     * 获取Application作用域的ViewModel
     *
     * @param modelClass ViewModel实现类
     * @param <T> ViewModel类型
     * @return ViewModel子类
     */
    protected <T extends ViewModel> T getApplicationScopeViewModel(@NonNull Class<T> modelClass) {
        if (mApplicationProvider == null) {
            mApplicationProvider = getAppViewModelProvider();
        }
        return mApplicationProvider.get(modelClass);
    }

    /**
     * 获取Application作用域的ViewModelProvider
     *
     * @return ViewModelProvider
     */
    protected ViewModelProvider getAppViewModelProvider() {
        return ((PureApplication) getApplicationContext()).getAppViewModelProvider(this);
    }

    /**
     * 获取Activity作用域的ViewModelProvider
     *
     * @return ViewModelProvider
     */
    protected ViewModelProvider getActivityViewModelProvider(AppCompatActivity activity) {
        return new ViewModelProvider(activity, activity.getDefaultViewModelProviderFactory());
    }

    /**
     * 跳转页面
     *
     * @param toActivity 所跳转的目的Activity类
     */
    public void startActivity(Class<?> toActivity) {
        startActivity(new Intent(this, toActivity));
    }

    /**
     * 清空历史栈并跳转页面
     *
     * @param toActivity 所跳转的目的Activity类
     */
    public void startActivityClearTop(Class<?> toActivity) {
        Intent intent = new Intent(this, toActivity);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
    }



    /**
     * 跳转页面
     *
     * @param toActivity 所跳转的目的Activity类
     * @param bundle 跳转所携带的信息
     */
    public void startActivity(Class<?> toActivity, Bundle bundle) {
        Intent intent = new Intent(this, toActivity);
        if (bundle != null) {
            intent.putExtras(bundle);
        }
        startActivity(intent);
    }

    /**
     * 判断是否debug模式
     *
     * @return boolean
     */
    public boolean isDebug() {
        return getApplicationContext().getApplicationInfo() != null &&
                (getApplicationContext().getApplicationInfo().flags & ApplicationInfo.FLAG_DEBUGGABLE) != 0;
    }


}
