package com.zyelite.kghub.model

import com.google.gson.annotations.SerializedName
import io.realm.RealmObject
import io.realm.annotations.PrimaryKey
import java.util.*


/**
 * @author ZyElite
 * @create 2018/4/8
 * @description User
 */

open class User : RealmObject() {

    @SerializedName("login")
    var login: String = ""

    @PrimaryKey
    @SerializedName("id")
    var id: Int = 0
    @SerializedName("avatar_url")
    var avatarUrl: String = ""
    //创建时间
    @SerializedName("created_at")
    var createdAt: Date? = null
    //更新时间
    @SerializedName("updated_at")
    var updatedAt: Date? = null

    //追随者
    var followers: Int = 0
    //跟随
    var following: Int = 0

    //个人仓库
    @SerializedName("public_repos")
    var publicRepos: Int = 0
    //
    @SerializedName("public_gists")
    var publicGists: Int = 0


    var email: String = ""
}
