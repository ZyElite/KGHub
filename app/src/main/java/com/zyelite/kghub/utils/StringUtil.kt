package com.zyelite.kghub.utils

import com.zyelite.kghub.App

/**
 * @author ZyElite
 * @create 2018/4/11
 * @description StringUtil
 */
object StringUtil {

    fun getString(id: Int): String {
        return App.instance.getString(id)
    }
}