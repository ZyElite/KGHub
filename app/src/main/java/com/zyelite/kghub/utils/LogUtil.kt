package com.zyelite.kghub.utils

import android.util.Log
import com.zyelite.kghub.App
import com.zyelite.kghub.BuildConfig
import com.zyelite.kghub.R

/**
 * @author ZyElite
 * @create 2018/5/2
 * @description LogUtil
 */
object LogUtil {

    private val tag = App.instance.getString(R.string.app_name)
    private val isOpen = BuildConfig.DEBUG

    fun e(msg: String) {
        if (isOpen) Log.e(tag, msg)
    }

    fun d(msg: String) {
        if (isOpen) Log.d(tag, msg)
    }

    fun v(msg: String) {
        if (isOpen) Log.v(tag, msg)
    }
}