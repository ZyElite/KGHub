package com.zyelite.kghub.model

import com.google.gson.annotations.SerializedName
import io.realm.RealmObject
import java.util.*


/**
 * @author ZyElite
 * @create 2018/4/8
 * @description User
 */

open class User : RealmObject() {
    @SerializedName("login")
    var login: String = ""
    @SerializedName("id")
    var id: Int = 0
    @SerializedName("avatar_url")
    var avatarUrl: String = ""
    //创建时间
    @SerializedName("created_at")
    private var createdAt: Date? = null
    //更新时间
    @SerializedName("updated_at")
    private var updatedAt: Date? = null
}
