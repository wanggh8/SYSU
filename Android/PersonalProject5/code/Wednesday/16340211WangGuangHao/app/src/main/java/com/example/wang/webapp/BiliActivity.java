package com.example.wang.webapp;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.example.wang.webapp.MyAdapter;
import com.example.wang.webapp.R;
import com.example.wang.webapp.RecyclerObj;
import com.google.gson.Gson;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.List;

import rx.Observable;
import rx.Observer;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

public class BiliActivity extends AppCompatActivity {
    EditText input;
    Button fbt;
    ProgressBar pro;
    ListView list;
    String base = "https://space.bilibili.com/ajax/top/showTop?mid=";
    String info = "";
    List<RecyclerObj> mList = new ArrayList<>();
    MyAdapter adapter;

    public static boolean isConn(Context context){
        ConnectivityManager connectivityManager = (ConnectivityManager) context
                .getSystemService(Context.CONNECTIVITY_SERVICE);
        if (connectivityManager != null) {
            NetworkInfo[] networkInfo = connectivityManager.getAllNetworkInfo();
            if (networkInfo != null&&networkInfo.length>0) {
                for (int i = 0; i < networkInfo.length; i++) {
                    if (networkInfo[i].getState() == NetworkInfo.State.CONNECTED) {
                        return true;
                    }
                }
            }
        }
        return false;
    }

    public String streamToString(InputStream is) {
        try {
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            byte[] buffer = new byte[1024];
            int len = 0;
            while ((len = is.read(buffer)) != -1) {
                baos.write(buffer, 0, len);
            }
            baos.close();
            is.close();
            byte[] byteArray = baos.toByteArray();
            return new String(byteArray);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.bilibili);
        input = findViewById(R.id.key);
        fbt = findViewById(R.id.search);
        pro = findViewById(R.id.progress);
        list = findViewById(R.id.list);
        adapter = new MyAdapter(com.example.wang.webapp.BiliActivity.this, mList);
        list.setAdapter(adapter);
        fbt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final String key = input.getText().toString();
                if(isConn(com.example.wang.webapp.BiliActivity.this) == false) {
                    Toast.makeText(com.example.wang.webapp.BiliActivity.this, "网络连接失败", Toast.LENGTH_SHORT).show();
                }
                else if (key.isEmpty() || key.matches("^[0-9]*$") == false) {
                    Toast.makeText(com.example.wang.webapp.BiliActivity.this, "需要整数类型数据", Toast.LENGTH_SHORT).show();
                }
                else if (key.isEmpty() || key.equals("0")) {
                    Toast.makeText(com.example.wang.webapp.BiliActivity.this, "需要（0,40）的数据", Toast.LENGTH_SHORT).show();
                }
                else {
                    pro.setVisibility(View.VISIBLE);

                    Observer<Bundle> observer = new Observer<Bundle>() {
                        @Override
                        public void onNext(Bundle s) {
                            pro.setVisibility(View.GONE);
                            //传递成功
                            adapter.refresh(mList);
                        }

                        @Override
                        public void onCompleted() {
                            pro.setVisibility(View.GONE);
                        }

                        @Override
                        public void onError(Throwable e) {
                            pro.setVisibility(View.GONE);
                            Toast.makeText(com.example.wang.webapp.BiliActivity.this, "数据库中不存在记录", Toast.LENGTH_SHORT).show();
                        }
                    };

                    Observable observable = Observable.create(new Observable.OnSubscribe<Bundle>() {
                        @Override
                        public void call(Subscriber<? super Bundle> subscriber) {
                            get_info(key);
                            //通过第10个字符是不是t即是否为true，判断是否存在
                            if (info.charAt(10) != 't') {
                                Toast.makeText(com.example.wang.webapp.BiliActivity.this, "数据库中不存在记录", Toast.LENGTH_SHORT).show();
                            }
                            else {
                                RecyclerObj recyclerObj = new Gson().fromJson(info, RecyclerObj.class);
                                try{
                                    URL imgUrl = new URL(recyclerObj.getData().getCover());
                                    HttpURLConnection urlConn = (HttpURLConnection )imgUrl.openConnection();
                                    //urlConn.setDoInput(true);
                                    urlConn.setRequestMethod("GET");
                                    urlConn.connect();
                                    // 将得到的数据转化成InputStream
                                    InputStream is = urlConn.getInputStream();
                                    // 将InputStream转换成Bitmap
                                    Bitmap bitmap = BitmapFactory.decodeStream(is);
                                    recyclerObj.setBmp(bitmap);
                                    Bundle bundle = new Bundle();
                                    bundle.putParcelable("bitmap", bitmap);
                                    subscriber.onNext(bundle);
                                    is.close();
                                }catch (IOException e){
                                    e.printStackTrace();
                                }
                                mList.add(recyclerObj);
                            }
                            //subscriber.onNext(info);
                        }
                    })
                            .subscribeOn(Schedulers.computation())
                            .observeOn(AndroidSchedulers.mainThread());
                    observable.subscribe(observer);


                }
            }
        });



    }

    private void get_info(String key) {
        try {
            URL myURL = new URL(base+key);
            // 打开URL链接
            HttpURLConnection urlConn = (HttpURLConnection )myURL.openConnection();
            urlConn.setRequestMethod("GET");
            urlConn.connect();

            if (urlConn.getResponseCode() == 200) {
                // 获取返回的数据
                info = streamToString(urlConn.getInputStream());
            }
            urlConn.disconnect();
            if (info.equals("")) {
                Toast.makeText(com.example.wang.webapp.BiliActivity.this, "数据库中不存在记录", Toast.LENGTH_SHORT).show();
            }
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }
}
