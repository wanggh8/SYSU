package com.example.wang.webapp;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;
import rx.Observable;

public interface GitHubService {
    @GET("/users/{user_name}/repos")
        // 这里的List<Repo>即为最终返回的类型，需要保持一致才可解析
        // 之所以使用一个List包裹是因为该接口返回的最外层是一个数组
    //Call<List<Repo>> getRepo(@Path("user_name") String user_name);
    // 特别地，使用rxJava时为
    Observable<List<Repo>> getRepo(@Path("user_name") String user_name);

    @GET("/repos/{user}/{repo}/issues")
    Observable<List<Issue>> getIssues(@Path("user") String user, @Path("repo")String repo);
}
