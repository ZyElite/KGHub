package com.zyelite.kghub.http.api

import com.zyelite.kghub.model.CommitsResModel
import io.reactivex.Observable
import io.reactivex.annotations.NonNull
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Path
import retrofit2.http.Query

/**
 * Created by ThirtyDegreesRay on 2017/10/17 13:13:33
 */

interface CommitService {

    /**
     * 获取分支的所有提交
     */
    @NonNull
    @GET("repos/{owner}/{repo}/commits")
    fun getRepoCommits(
            @Header("forceNetWork") forceNetWork: Boolean,
            @Path("owner") owner: String,
            @Path("repo") repo: String,
            @Query("sha") branch: String,
            @Query("page") page: Int
    ): Observable<Response<ArrayList<CommitsResModel>>>


}

//    @NonNull
//    @GET("repos/{owner}/{repo}/commits/{sha}")
//    Observable<Response<RepoCommitExt>> getCommitInfo(
//            @Header("forceNetWork") boolean forceNetWork,
//            @Path("owner") String owner,
//            @Path("repo") String repo,
//            @Path("sha") String sha
//    );
//
//    @NonNull
//    @GET("repos/{owner}/{repo}/commits/{ref}/comments")
//    Observable<Response<ArrayList<RepoCommit>>> getCommitComments(
//            @Header("forceNetWork") boolean forceNetWork,
//            @Path("owner") String owner,
//            @Path("repo") String repo,
//            @Path("ref") String ref,
//            @Query("page") int page
//    );
//
//    @NonNull
//    @GET("repos/{owner}/{repo}/compare/{before}...{head}")
//    Observable<Response<CommitsComparison>> compareTwoCommits(
//            @Header("forceNetWork") boolean forceNetWork,
//            @Path("owner") String owner,
//            @Path("repo") String repo,
//            @Path("before") String before,
//            @Path("head") String head
//    );
