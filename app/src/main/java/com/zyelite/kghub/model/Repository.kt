package com.zyelite.kghub.model

import com.google.gson.annotations.SerializedName


/**
 * @author ZyElite
 * @create 2018/4/11
 * @description Repository
 */


data class Repository(
        @SerializedName("id") var id: Int = 0,
        @SerializedName("name") var name: String = "",
        @SerializedName("full_name") var fullName: String = "",
        @SerializedName("owner") var owner: User = User()
)
