package com.zyelite.kghub.http.api

import io.reactivex.Observable
import io.reactivex.annotations.NonNull
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

/**
 * @author ZyElite
 * @create 2018/4/3
 * @description CommentsService
 */

interface CommentsService {
    /**
     * 获取提交的主分支
     */
    @NonNull
    @GET("/repos/{owner}/{repo}/comments")
    fun getCommitComments(
            @Path("owner") owner: String,
            @Path("repo") repo: String,
            @Query("page") page: Int,
            @Query("rel") rel: String
    ): Observable<ArrayList<String>>
}