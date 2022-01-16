package com.example.password_keeper.ui;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.Window;
import android.view.WindowManager;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.example.password_keeper.R;
import com.example.password_keeper.constants.Constants;
import com.example.password_keeper.storage.MySP;

import java.util.Timer;
import java.util.TimerTask;

import butterknife.ButterKnife;

/**
 *开机画面
 */
public class SplashActivity extends Activity {
    private MySP mMySP;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mMySP = new MySP(this).getmMySP();//获取本地存储记录
        requestWindowFeature(Window.FEATURE_NO_TITLE);// 隐藏标题
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);// 设置全屏
        setContentView(R.layout.activity_splash);
        WebView webView = (WebView)findViewById(R.id.webview);
        String url = "file:///android_asset/rain.html";
        //此方法可以在webview中打开链接而不会跳转到外部浏览器
        webView.setWebViewClient(new WebViewClient());
        //此方法可以启用html5页面的javascript
        webView.getSettings().setJavaScriptEnabled(true);
        webView.loadUrl(url);
        Timer timer = new Timer();
        //开机画面设置为三秒
        TimerTask timerTask = new TimerTask() {
            @Override
            public void run() {
                boolean flag = mMySP.load(Constants.IS_HAS_USER, false);
                if(flag) {
                    Intent intent = new Intent(SplashActivity.this, LoginActivity.class);
                    startActivity(intent);
                }
                else{
                    Intent intent = new Intent(SplashActivity.this, RegisterActivity.class);
                    startActivity(intent);
                }
                finish();
            }
        };
        timer.schedule(timerTask, 3200);
    }

}
