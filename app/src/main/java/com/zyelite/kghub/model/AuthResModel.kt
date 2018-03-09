package com.zyelite.kghub.model

import com.google.gson.annotations.SerializedName
import java.util.*

/**
 * @author zy
 * @date 2018/2/28
 * @des AuthResModel 认证返回数据model
 */
class AuthResModel {

    private var id: Int = 0
    private var url: String? = null
    private var token: String? = null
    @SerializedName("created_at")
    private var createdAt: Date? = null
    @SerializedName("updated_at")
    private var updatedAt: Date? = null
    private var scopes: List<String>? = null

    fun getId(): Int {
        return id
    }

    fun setId(id: Int) {
        this.id = id
    }

    fun getUrl(): String? {
        return url
    }

    fun setUrl(url: String) {
        this.url = url
    }

    fun getToken(): String? {
        return token
    }

    fun setToken(token: String) {
        this.token = token
    }

    fun getCreatedAt(): Date? {
        return createdAt
    }

    fun setCreatedAt(createdAt: Date) {
        this.createdAt = createdAt
    }

    fun getUpdatedAt(): Date? {
        return updatedAt
    }

    fun setUpdatedAt(updatedAt: Date) {
        this.updatedAt = updatedAt
    }

    fun getScopes(): List<String>? {
        return scopes
    }

    fun setScopes(scopes: List<String>) {
        this.scopes = scopes
    }

    override fun toString(): String {
        return "AuthResModel(id=$id, url=$url, token=$token, createdAt=$createdAt, updatedAt=$updatedAt, scopes=$scopes)"
    }


//    {
//        "id": 1,
//        "url": "https://api.github.com/authorizations/1",
//        "scopes": [
//        "public_repo"
//        ],
//        "token": "abcdefgh12345678",
//        "token_last_eight": "12345678",
//        "hashed_token": "25f94a2a5c7fbaf499c665bc73d67c1c87e496da8985131633ee0a95819db2e8",
//        "app": {
//        "url": "http://my-github-app.com",
//        "name": "my github app",
//        "client_id": "abcde12345fghij67890"
//    },
//        "note": "optional note",
//        "note_url": "http://optional/note/url",
//        "updated_at": "2011-09-06T20:39:23Z",
//        "created_at": "2011-09-06T17:26:27Z",
//        "fingerprint": ""
//    }
}