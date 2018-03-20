package com.zyelite.kghub.http.api

import com.zyelite.kghub.model.UserModel
import io.reactivex.Observable
import okhttp3.ResponseBody
import retrofit2.Response
import retrofit2.http.*
import java.util.*

/**
 * @author zy
 * @date 2018/3/8
 * @des UserService
 */
interface UserService {

    /**
     * 获取登录用户信息
     */

    @GET("user")
    fun getUserInfo(
            @Header("forceNetWork") forceNetWork: Boolean
    ): Observable<Response<UserModel>>


    @GET("/users/{username}")
    fun getUser(): Observable<Response<UserModel>>


    @GET("user/following/{user}")
    fun checkFollowing(
            @Path("user") user: String
    ): Observable<Response<ResponseBody>>

    /**
     * Check if one user follows another
     */
    @GET("users/{user}/following/{targetUser}")
    fun checkFollowing(
            @Path("user") user: String,
            @Path("targetUser") targetUser: String
    ): Observable<Response<ResponseBody>>

    @PUT("user/following/{user}")
    fun followUser(
            @Path("user") user: String
    ): Observable<Response<ResponseBody>>

    @DELETE("user/following/{user}")
    fun unfollowUser(
            @Path("user") user: String
    ): Observable<Response<ResponseBody>>

    @GET("users/{user}/followers")
    fun getFollowers(
            @Header("forceNetWork") forceNetWork: Boolean,
            @Path("user") user: String,
            @Query("page") page: Int
    ): Observable<Response<ArrayList<UserModel>>>

    @GET("users/{user}/following")
    fun getFollowing(
            @Header("forceNetWork") forceNetWork: Boolean,
            @Path("user") user: String,
            @Query("page") page: Int
    ): Observable<Response<ArrayList<UserModel>>>

    /**
     * List events performed by a user
     */
//    @GET("users/{user}/events")
//     fun getUserEvents(
//            @Header("forceNetWork") forceNetWork: Boolean,
//            @Path("user") user: String,
//            @Query("page") page: Int
//    ): Observable<Response<ArrayList<Event>>>

    /**
     * List github public events
     */
//    @GET("events")
//     fun getPublicEvent(
//            @Header("forceNetWork") forceNetWork: Boolean,
//            @Query("page") page: Int
//    ): Observable<Response<ArrayList<Event>>>

//    @GET("users/{user}/received_events")
//     fun getNewsEvent(
//            @Header("forceNetWork") forceNetWork: Boolean,
//            @Path("user") user: String,
//            @Query("page") page: Int
//    ): Observable<Response<ArrayList<Event>>>

    @GET("orgs/{org}/members")
    fun getOrgMembers(
            @Header("forceNetWork") forceNetWork: Boolean,
            @Path("org") org: String,
            @Query("page") page: Int
    ): Observable<Response<ArrayList<UserModel>>>

    @GET("users/{user}/orgs")
    fun getUserOrgs(
            @Header("forceNetWork") forceNetWork: Boolean,
            @Path("user") user: String
    ): Observable<Response<ArrayList<UserModel>>>


}