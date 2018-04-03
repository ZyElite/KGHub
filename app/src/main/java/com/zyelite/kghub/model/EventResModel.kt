package com.zyelite.kghub.model

import com.google.gson.annotations.SerializedName


/**
 * @author ZyElite
 * @create 2018/4/3
 * @description EventResModelF
 */


data class EventResModel(
        @SerializedName("type") var type: String = "",
        @SerializedName("public") var public: Boolean = false,
        @SerializedName("payload") var payload: Payload = Payload(),
        @SerializedName("repo") var repo: Repo = Repo(),
        @SerializedName("actor") var actor: Actor = Actor(),
        @SerializedName("org") var org: Org = Org(),
        @SerializedName("created_at") var createdAt: String = "",
        @SerializedName("id") var id: String = ""
)

data class Org(
        @SerializedName("id") var id: Int = 0,
        @SerializedName("login") var login: String = "",
        @SerializedName("gravatar_id") var gravatarId: String = "",
        @SerializedName("url") var url: String = "",
        @SerializedName("avatar_url") var avatarUrl: String = ""
)

class Payload

data class Repo(
        @SerializedName("id") var id: Int = 0,
        @SerializedName("name") var name: String = "",
        @SerializedName("url") var url: String = ""
)

data class Actor(
        @SerializedName("id") var id: Int = 0,
        @SerializedName("login") var login: String = "",
        @SerializedName("gravatar_id") var gravatarId: String = "",
        @SerializedName("avatar_url") var avatarUrl: String = "",
        @SerializedName("url") var url: String = ""
)