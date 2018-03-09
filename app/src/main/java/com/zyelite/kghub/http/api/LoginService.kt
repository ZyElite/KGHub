package com.zyelite.kghub.http.api

import com.zyelite.kghub.model.AuthReqModel
import com.zyelite.kghub.model.AuthResModel
import com.zyelite.kghub.model.OauthResToken
import io.reactivex.Observable
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.Headers
import retrofit2.http.POST
import retrofit2.http.Query

/**
 * @author zy
 * @date 2018/2/28
 * @des LoginService
 */
interface LoginService {

    /**
     * 登录授权
     */
    @POST("authorizations")
    @Headers("Accept: application/json")
    fun authorizations(
            @Body authRequestModel: AuthReqModel
    ): Observable<Response<AuthResModel>>


    /**
     * 获取访问令牌
     */
    @POST("login/oauth/access_token")
    @Headers("Accept: application/json")
    fun getAccessToken(
            @Query("client_id") clientId: String,
            @Query("client_secret") clientSecret: String,
            @Query("code") code: String,
            @Query("state") state: String
    ): Observable<Response<OauthResToken>>

}