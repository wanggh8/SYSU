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

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.fragment.NavHostFragment;

import com.wanggh8.puremvvm.PureApplication;

/**
 * @author wanggh8
 * @version V1.0
 * @date 2020/11/30
 */
public abstract class PureFragment extends Fragment {

    private ViewModelProvider mActivityProvider;
    private ViewModelProvider mApplicationProvider;
    private ViewModelProvider mFragmentProvider;

    // 所属Activity
    protected AppCompatActivity mActivity;
    // 所属Activity上下文
    protected Context mContext;
    // 根View
    protected View mRootView;
    // 当前Fragment文件名，调试使用
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
     * 获取布局的layout
     */
    public abstract int getContentLayout();

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

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        mContext = context;
        mActivity = (AppCompatActivity) context;
    }

    /**
     * 初始化除View外的逻辑
     *
     * @param savedInstanceState
     */
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // 方便调试，添加当前Fragment名
        Log.d("onCreate mFragment ：", getClass().getSimpleName());

    }

    /**
     * 每次创建、绘制该Fragment的View组件时回调该方法，Fragment将会显示该方法返回的View组件
     * 初始化静态View
     *
     * @param inflater
     * @param container
     * @param savedInstanceState
     * @return View
     */
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        if (mRootView == null) {
            mRootView = inflater.inflate(getContentLayout(), container, false);
            // 页面接受的参数方法
            initParam();
            // 页面数据初始化方法
            beforeInitView();
            initView();
        }
        return mRootView;
    }

    /**
     * 当Fragment所在的Activity被启动完成后回调该方法
     * 添加动态View
     * 访问父Activity的View层
     * @param savedInstanceState
     */
    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        afterInitView();
        // 页面事件监听的方法，一般用于ViewModel层转到View层的事件注册
        initViewObservable();
    }


    @Nullable
    @Override
    public Context getContext() {
        return mContext;
    }

    /**
     * 获取Activity作用域的ViewModel
     *
     * @param modelClass ViewModel实现类
     * @param <T> ViewModel类型
     * @return ViewModel子类
     */
    protected <T extends ViewModel> T getActivityScopeViewModel(@NonNull Class<T> modelClass) {
        if (mActivityProvider == null) {
            mActivityProvider = getActivityViewModelProvider(mActivity);
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
     * 获取Fragment作用域的ViewModel
     *
     * @param modelClass ViewModel实现类
     * @param <T> ViewModel类型
     * @return ViewModel子类
     */
    protected <T extends ViewModel> T getFragmentScopeViewModel(@NonNull Class<T> modelClass) {
        if (mFragmentProvider == null) {
            mFragmentProvider = getFragmentViewModelProvider(this);
        }
        return mFragmentProvider.get(modelClass);
    }


    // 给当前BaseFragment用的
    protected ViewModelProvider getAppViewModelProvider() {
        return ((PureApplication) mActivity.getApplicationContext()).getAppViewModelProvider(mActivity);
    }

    // 给所有的fragment提供的函数，可以顺利的拿到 ViewModel
    protected ViewModelProvider getFragmentViewModelProvider(Fragment fragment) {
        return new ViewModelProvider(fragment, fragment.getDefaultViewModelProviderFactory());
    }

    // 给所有的fragment提供的函数，可以顺利的拿到 ViewModel
    protected ViewModelProvider getActivityViewModelProvider(AppCompatActivity activity) {
        return new ViewModelProvider(activity, activity.getDefaultViewModelProviderFactory());
    }

    /**
     * 为了给所有的fragment，导航跳转fragment的
     * @return
     */
    protected NavController nav() {
        return NavHostFragment.findNavController(this);
    }


    /**
     * 跳转页面
     *
     * @param toActivity 所跳转的目的Activity类
     */
    public void startActivity(Class<?> toActivity) {
        startActivity(new Intent(mContext, toActivity));
    }

    /**
     * 清空历史栈并跳转页面
     *
     * @param toActivity 所跳转的目的Activity类
     */
    public void startActivityClearTop(Class<?> toActivity) {
        Intent intent = new Intent(mContext, toActivity);
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
        Intent intent = new Intent(mContext, toActivity);
        if (bundle != null) {
            intent.putExtras(bundle);
        }
        startActivity(intent);
    }

}
