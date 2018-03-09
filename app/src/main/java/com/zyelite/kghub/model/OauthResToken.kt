package com.zyelite.kghub.model

import com.google.gson.annotations.SerializedName

/**
 * @author zy
 * @date 2018/3/8
 * @des OauthResToken
 */

class OauthResToken {

    @SerializedName("access_token")
    private var accessToken: String? = null

    private var scope: String? = null

    fun getAccessToken(): String? {
        return accessToken
    }

    fun setAccessToken(accessToken: String) {
        this.accessToken = accessToken
    }

    fun getScope(): String? {
        return scope
    }

    fun setScope(scope: String) {
        this.scope = scope
    }
}