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

package com.wanggh8.puremvvm;

import android.app.Activity;
import android.app.Application;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.lifecycle.ViewModelStore;
import androidx.lifecycle.ViewModelStoreOwner;

import com.wanggh8.puremvvm.util.Log.PureLogger;

/**
 * @author wanggh8
 * @version V1.0
 * @date 2020/11/30
 */
public class PureApplication extends Application implements ViewModelStoreOwner {

    private ViewModelStore mAppViewModelStore;
    private ViewModelProvider.Factory mFactory;
    private boolean isLog = true;
    private boolean isLogToFile = false;

    @Override
    public void onCreate() {
        super.onCreate();
        mAppViewModelStore = new ViewModelStore();
    }

    /**
     * 设置logger
     *
     * @param enableLogger 是否启用PureLogger
     * @param isLog 是否在控制台显示Log
     * @param isLogToFile 是否输出Log到文件
     */
    public void setPureLogger(boolean enableLogger, boolean isLog, boolean isLogToFile) {
        this.isLog = isLog;
        this.isLogToFile = isLogToFile;
        if (enableLogger) {
            initLogger();
        }
    }

    /**
     * 初始化日志框架
     *
     * 使用：
     * Logger.d("debug");
     * Logger.e("error");
     * Logger.w("warning");
     * Logger.v("verbose");
     * Logger.i("information");
     * Logger.wtf("What a Terrible Failure");
     * Logger.json(JSON_CONTENT);
     * Logger.xml(XML_CONTENT);
     * Logger.d(MAP);
     * Logger.d(SET);
     * Logger.d(LIST);
     * Logger.d(ARRAY);
     */
    private void initLogger() {
        PureLogger.initLogger(isLog, isLogToFile);
    }

    public ViewModelProvider getAppViewModelProvider(Activity activity) {
        return new ViewModelProvider((PureApplication) activity.getApplicationContext(),
                ((PureApplication) activity.getApplicationContext()).getAppFactory(activity));
    }

    private ViewModelProvider.Factory getAppFactory(Activity activity) {
        Application application = checkApplication(activity);
        if (mFactory == null) {
            mFactory = ViewModelProvider.AndroidViewModelFactory.getInstance(application);
        }
        return mFactory;
    }

    private Application checkApplication(Activity activity) {
        Application application = activity.getApplication();
        if (application == null) {
            throw new IllegalStateException("Your activity/fragment is not yet attached to "
                    + "Application. You can't request ViewModel before onCreate call.");
        }
        return application;
    }

    private Activity checkActivity(Fragment fragment) {
        Activity activity = fragment.getActivity();
        if (activity == null) {
            throw new IllegalStateException("Can't create ViewModelProvider for detached fragment");
        }
        return activity;
    }

    @NonNull
    @Override
    public ViewModelStore getViewModelStore() {
        return mAppViewModelStore;
    }
}
