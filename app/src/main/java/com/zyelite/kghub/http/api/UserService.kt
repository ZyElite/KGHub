package com.zyelite.kghub.http.api

import com.zyelite.kghub.model.User
import io.reactivex.Observable
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Header

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
    ): Observable<Response<User>>

}