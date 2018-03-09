package com.zyelite.kghub.model

import com.google.gson.annotations.SerializedName
import com.zyelite.kghub.BuildConfig
import com.zyelite.kghub.KConfig
import java.util.*

/**
 * @author zy
 * @date 2018/2/28
 * @des AuthReqModel
 */
class AuthReqModel {

    private var scopes: List<String>? = null
    private var note: String? = null
    private var noteUrl: String? = null
    @SerializedName("client_id")
    private var clientId: String? = null
    @SerializedName("client_secret")
    private var clientSecret: String? = null

    fun generate(): AuthReqModel {
        this.scopes = Arrays.asList("user", "repo", "gist", "notifications")
        this.note = BuildConfig.APPLICATION_ID
        this.clientId = KConfig.KGHUB_CLIENT_ID
        this.clientSecret = KConfig.KGHUB_CLIENT_SECRET
        this.noteUrl = KConfig.KGHUB_DESC
        return this
    }

    fun getScopes(): List<String>? {
        return scopes
    }

    fun getNote(): String? {
        return note
    }

    fun getNoteUrl(): String? {
        return noteUrl
    }

    fun getClientId(): String? {
        return clientId
    }

    fun getClientSecret(): String? {
        return clientSecret
    }
}