package com.zyelite.kghub.http.api

import com.zyelite.kghub.model.EventResModel
import io.reactivex.Observable
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Path
import retrofit2.http.Query
import java.util.*

/**
 * @author ZyElite
 * @create 2018/4/3
 * @description ActivityService
 */

interface ActivityService {
    /**
     *
     * 自己提交的所有事件
     */
    @GET("users/{user}/events")
    fun getUserEvents(
            @Header("forceNetWork") forceNetWork: Boolean,
            @Path("user") user: String,
            @Query("page") page: Int
    ): Observable<Response<ArrayList<EventResModel>>>
}