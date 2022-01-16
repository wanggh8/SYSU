package com.wanggh8.mydrive.base;

import android.app.Activity;
import android.app.Application;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.os.Handler;
import android.os.Process;
import android.text.TextUtils;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.hjq.toast.ToastUtils;
import com.hjq.toast.style.ToastWhiteStyle;
import com.microsoft.identity.client.IMultipleAccountPublicClientApplication;
import com.microsoft.identity.client.exception.MsalException;
import com.tencent.smtt.sdk.QbSdk;
import com.tencent.smtt.sdk.TbsDownloader;
import com.tencent.smtt.sdk.TbsListener;
import com.wanggh8.mydrive.config.Constant;
import com.wanggh8.mydrive.db.DaoMaster;
import com.wanggh8.mydrive.db.DaoSession;
import com.wanggh8.mydrive.utils.AuthenticationHelper;
import com.wanggh8.mydrive.utils.CrashHandler;
import com.wanggh8.netcore.logger.Logger;
import com.wanggh8.netcore.net.NetHttpOperater;
import com.wanggh8.netcore.service.CoreNetService;

import java.util.ArrayList;
import java.util.List;

/**
 * @author wanggh8
 * @version V1.0
 * @date 2020/9/23
 */
public class BaseApplication extends Application implements Application.ActivityLifecycleCallbacks {

    // 单例实例
    private static BaseApplication mInstance;
    // 活动列表
    private List<Activity> mActivityList;
    // 全局APP上下文
    private static Context mAppContext;
    // 前台Activity数目
    private int foregroundActivityCount = 0;
    // 应用是否在前台
    private static boolean isForeground;
    // X5内核是否加载成功
    private boolean initX5 = false;

    private DaoMaster.DevOpenHelper mHelper;
    private SQLiteDatabase db;
    private DaoMaster mDaoMaster;
    private DaoSession mDaoSession;


    public BaseApplication() {
        super();
        mInstance = this;
    }

    public static BaseApplication getInstance() {
        return mInstance;
    }

    public static Context getAppContext() {
        if (mAppContext == null) {
            mAppContext = mInstance.getApplicationContext();
        }
        return mAppContext;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        catchCrashException();
        mActivityList = new ArrayList<>();
        // 全局管理Activity生命周期
        registerActivityLifecycleCallbacks(this);
        init();
    }



    private void init() {
        // Application中初始化Toast
        ToastUtils.init(this, new ToastWhiteStyle(this));
        // 初始化X5内核
        initX5();
        setDatabase();
        // 初始化网络
        initNetCore();
    }

    /**
     * 运行时异常处理
     */
    private void catchCrashException() {
        CrashHandler crashHandler = CrashHandler.getInstance();
        crashHandler.init(getApplicationContext());
    }

    private void initNetCore() {
        // 初始日志工具
//        Logger.initLogger(true, BuildConfig.ISLOGGERTOFILE);
//        Constant.BASE_HOST_URL = BuildConfig.baseUrl;

        // 配置网络基本请求参数,支持配置URL、超时时间、HTTP HEADER
        CoreNetService.getInstance().build(new NetHttpOperater.Builder()
                .readTimeout(Constant.READ_TIME_OUT)
                .connectTimeout(Constant.REQUEST_TIME_OUT)
                .writeTimeout(Constant.WRITE_TIME_OUT)
                .addHeader("Content-Type", "application/json; charset=UTF-8")
                .addHeader("Accept-Encoding", "gzip, deflate")
                .addHeader("Connection", "keep-alive")
                .addHeader("Accept", "*/*")
                .baseUrl(Constant.BASE_HOST_URL)
                .encrypt(false)
        );
        // 添加token
        // if (!TextUtils.isEmpty(getToken()))
        // CoreNetService.getInstance().build().addHeader("token", getToken());
    }

    /**
     * 初始化数据库
     */
    private void setDatabase() {
        // 通过 DaoMaster 的内部类 DevOpenHelper，你可以得到一个便利的 SQLiteOpenHelper 对象。
        // 可能你已经注意到了，你并不需要去编写「CREATE TABLE」这样的 SQL 语句，因为 greenDAO 已经帮你做了。
        // 注意：默认的 DaoMaster.DevOpenHelper 会在数据库升级时，删除所有的表，意味着这将导致数据的丢失。
        // 所以，在正式的项目中，你还应该做一层封装，来实现数据库的安全升级。
        mHelper = new DaoMaster.DevOpenHelper(this, "notes-db", null);
        db = mHelper.getWritableDatabase();
        // 注意：该数据库连接属于 DaoMaster，所以多个 Session 指的是相同的数据库连接。
        mDaoMaster = new DaoMaster(db);
        mDaoSession = mDaoMaster.newSession();
    }

    public DaoSession getDaoSession() {
        return mDaoSession;
    }
    public SQLiteDatabase getDb() {
        return db;
    }


    // ------------------------------------------------------------------------
    // 腾讯TBS X5内核加载
    // ------------------------------------------------------------------------

    /**
     * 初始化X5内核
     */
    public void initX5() {
        QbSdk.setDownloadWithoutWifi(true);
        QbSdk.initX5Environment(this, new QbSdk.PreInitCallback() {
            @Override
            public void onCoreInitFinished() {

            }

            @Override
            public void onViewInitFinished(boolean res) {
                Log.e("X5", "初始化结果====" + res);
                setInitX5(res);
            }
        });
        if (!isInitX5()) {
            getX5();
        }
    }

    /**
     * 下载X5内核
     */
    private void getX5() {
        //判断是否要自行下载内核
        boolean needDownload = TbsDownloader.needDownload(this, TbsDownloader.DOWNLOAD_OVERSEA_TBS);
        Log.d("X5", needDownload + "");
        // 根据实际的网络情况下，选择是否下载或是其他操作
        // 例如: 只有在wifi状态下，自动下载，否则弹框提示
        if (needDownload) {
            // 启动下载
            TbsDownloader.startDownload(this);
            X5DownloadListen();
        } else {
            setInitX5(true);
        }

    }

    /**
     * X5内核下载侦听
     */
    private void X5DownloadListen() {
        QbSdk.setTbsListener(new TbsListener() {
            @Override
            public void onDownloadFinish(int i) {
                //tbs 内核下载完成回调
                Log.d("X5", "   内核下载完成" );
            }

            @Override
            public void onInstallFinish(int i) {
                //内核安装完成回调，
                Log.d("X5", "   内核安装完成" );
                setInitX5(true);
            }

            @Override
            public void onDownloadProgress(int i) {
                //下载进度监听  百分比 ： i%
                Log.d("X5", "  内核下载进度:" + i );
            }
        });
    }
    
    public boolean isInitX5() {
        return initX5;
    }

    public void setInitX5(boolean initX5) {
        this.initX5 = initX5;
    }
    
    // ------------------------------------------------------------------------
    // Activity管理
    // ------------------------------------------------------------------------

    /**
     * 添加activity
     *
     * @param activity
     */
    public void addActivity(Activity activity) {
        if (mActivityList == null) {
            return;
        }
        if (!mActivityList.contains(activity)) {
            mActivityList.add(activity);
        }
    }

    /**
     * 移除所有activity
     */
    public void removeAllActivity() {
        if (mActivityList == null) {
            return;
        }
        for (Activity activity : mActivityList) {
            activity.finish();
        }
    }


    /**
     * 移除除本身外其他Activity
     *
     * @param self 自身Activity
     */
    public void removeOtherActivity(Activity self) {
        if (mActivityList == null) {
            return;
        }
        for (Activity activity : mActivityList) {
            if (activity != self)
                activity.finish();
        }
    }

    /**
     * 判断应用是否在前台
     */
    public boolean isForeground() {
        isForeground = foregroundActivityCount > 0;
        return isForeground;
    }

    /**
     * 退出APP
     */
    public void onExit() {
        removeAllActivity();
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                Process.killProcess(Process.myPid());
            }
        }, 300);
    }

    /* * * * * * * * LifecycleCallbacks * * * * * * * */

    @Override
    public void onActivityCreated(@NonNull Activity activity, @Nullable Bundle bundle) {
        addActivity(activity);
    }

    @Override
    public void onActivityStarted(@NonNull Activity activity) {
        foregroundActivityCount++;
    }

    @Override
    public void onActivityResumed(@NonNull Activity activity) {

    }

    @Override
    public void onActivityPaused(@NonNull Activity activity) {

    }

    @Override
    public void onActivityStopped(@NonNull Activity activity) {
        foregroundActivityCount--;
    }

    @Override
    public void onActivitySaveInstanceState(@NonNull Activity activity, @NonNull Bundle bundle) {

    }

    @Override
    public void onActivityDestroyed(@NonNull Activity activity) {

    }
}
