package com.example.wang.webapp;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.gson.Gson;

import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.ParameterizedType;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;
import okhttp3.OkHttpClient;
import retrofit2.Call;
import retrofit2.CallAdapter;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava.RxJavaCallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;
import rx.Observable;
import rx.Observer;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.schedulers.Schedulers;

public class IssueActivity extends AppCompatActivity {

    ProgressBar pro;
    ListView list;
    List<Issue> mList = new ArrayList<>();
    IssusAdapter adapter;

    String baseURL = "https://api.github.com/";

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
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.isssue);


        pro = findViewById(R.id.progress2);
        list = findViewById(R.id.list2);


        adapter = new IssusAdapter(com.example.wang.webapp.IssueActivity.this,mList);
        list.setAdapter(adapter);

        OkHttpClient build = new OkHttpClient.Builder()
                .connectTimeout(2, TimeUnit.SECONDS)
                .readTimeout(2, TimeUnit.SECONDS)
                .writeTimeout(2, TimeUnit.SECONDS)
                .build();

        final Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(baseURL)
                .addConverterFactory(GsonConverterFactory.create())
                .addCallAdapterFactory(RxJavaCallAdapterFactory.create())
                .client(build)
                .build();

        pro.setVisibility(View.VISIBLE);

        GitHubService repo = retrofit.create(GitHubService.class);
        Intent intent = getIntent();
        Bundle bundle = intent.getExtras();

        repo.getIssues(bundle.getString("user"),bundle.getString("name")).subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Subscriber<List<Issue>>() {
                    @Override
                    public void onCompleted() {
                        pro.setVisibility(View.GONE);

                    }

                    @Override
                    public void onError(Throwable e) {
                        pro.setVisibility(View.GONE);
                        Toast.makeText(IssueActivity.this, "HTTP 404", Toast.LENGTH_SHORT).show();
                    }


                    @Override
                    public void onNext(List<Issue> repos) {
                        mList.clear();
                        for (int i =0;i < repos.size();i++){

                            mList.add(repos.get(i));

                        }
                        if (mList.size() == 0){
                            Toast.makeText(IssueActivity.this, "没有任何Issue", Toast.LENGTH_SHORT).show();
                        }
                        adapter.refresh(mList);
                        pro.setVisibility(View.GONE);
                    }

                });
;



    }

}
