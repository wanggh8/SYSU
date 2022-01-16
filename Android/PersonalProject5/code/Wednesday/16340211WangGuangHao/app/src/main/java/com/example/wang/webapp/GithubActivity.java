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

public class GithubActivity extends AppCompatActivity {
    EditText input;
    Button fbt;
    ProgressBar pro;
    ListView list;
    List<Repo> mList = new ArrayList<>();
    GitAdapter adapter;
    String key;

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
        setContentView(R.layout.github);

        input = findViewById(R.id.key1);
        fbt = findViewById(R.id.search1);
        pro = findViewById(R.id.progress1);
        list = findViewById(R.id.list1);


        adapter = new GitAdapter(com.example.wang.webapp.GithubActivity.this,mList);
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

        fbt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                key = input.getText().toString();
                if (isConn(com.example.wang.webapp.GithubActivity.this) == false) {
                    Toast.makeText(com.example.wang.webapp.GithubActivity.this, "网络连接失败", Toast.LENGTH_SHORT).show();
                } else if (key.isEmpty()) {
                    Toast.makeText(com.example.wang.webapp.GithubActivity.this, "用户名为空", Toast.LENGTH_SHORT).show();
                } else {
                    pro.setVisibility(View.VISIBLE);

                    GitHubService repo = retrofit.create(GitHubService.class);
                    repo.getRepo(key).subscribeOn(Schedulers.io())
                            .observeOn(AndroidSchedulers.mainThread())
                            .subscribe(new Subscriber<List<Repo>>() {
                                @Override
                                public void onCompleted() {
                                    pro.setVisibility(View.GONE);
                                    Toast.makeText(GithubActivity.this, "Get Completed", Toast.LENGTH_SHORT).show();
                                }

                                @Override
                                public void onError(Throwable e) {
                                    pro.setVisibility(View.GONE);
                                    Toast.makeText(GithubActivity.this, "HTTP 404", Toast.LENGTH_SHORT).show();
                                }


                                @Override
                                public void onNext(List<Repo> repos) {
                                    mList.clear();
                                    for (int i =0;i < repos.size();i++){
                                        if (repos.get(i).has_issues == true){
                                            mList.add(repos.get(i));
                                        }
                                    }
                                    if (mList.size() == 0){
                                        Toast.makeText(GithubActivity.this, "用户没有任何repo", Toast.LENGTH_SHORT).show();
                                    }
                                    adapter.refresh(mList);
                                    pro.setVisibility(View.GONE);
                                }

                            });
                }
            }
        });
        list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Intent intent1 = new Intent(GithubActivity.this,IssueActivity.class);
                Bundle bundle= new Bundle();
                bundle.putString("user",key);
                bundle.putString("name",mList.get(i).name);
                intent1.putExtras(bundle);
                startActivity(intent1);
            }
        });

    }

}
