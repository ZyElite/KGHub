package com.zyelite.kghub.http.api

import io.reactivex.Observable
import okhttp3.ResponseBody
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Path
import retrofit2.http.Query

/**
 * Created by ThirtyDegreesRay on 2017/12/25 15:30:56
 */

interface GitHubWebPageService {

//    @get:Headers("Cache-Control: public, max-age=86400")
//    @get:GET("trending")
//    val trendingLanguages: Observable<Response<ResponseBody>>

    //    @NonNull
    //    @GET("{owner}/{repo}/wiki.atom")
    //    Observable<Response<WikiFeedModel>> getWiki(
    //            @Header("forceNetWork") boolean forceNetWork,
    //            @Path("owner") String owner,
    //            @Path("repo") String repo
    //    );

    @GET("collections")
    fun getCollections(
            @Header("forceNetWork") forceNetWork: Boolean
    ): Observable<Response<ResponseBody>>

    @GET("collections/{collectionId}")
    fun getCollectionInfo(
            @Header("forceNetWork") forceNetWork: Boolean,
            @Path("collectionId") collectionId: String
    ): Observable<Response<ResponseBody>>

    @GET("topics")
    fun getTopics(
            @Header("forceNetWork") forceNetWork: Boolean
    ): Observable<Response<ResponseBody>>

    @GET("trending/{language}")
    fun getTrendingRepos(
            @Header("forceNetWork") forceNetWork: Boolean,
            @Path("language") language: String,
            @Query("since") since: String
    ): Observable<Response<ResponseBody>>

}
