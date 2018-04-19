package com.zyelite.kghub.utils

import android.content.Context
import com.zyelite.kghub.App

/**
 * @author ZyElite
 * @create 2018/4/10
 * @description Constant
 */
object Constant {
    var CURRENT_LOGIN = "CURRENT_LOGIN"
    var TOKEN = "TOKEN"
    var NAME = App.instance.getSharedPreferences("KGHub", Context.MODE_PRIVATE).getString(Constant.CURRENT_LOGIN, "")
}