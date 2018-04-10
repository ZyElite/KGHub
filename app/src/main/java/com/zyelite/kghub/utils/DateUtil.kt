package com.zyelite.kghub.utils

import android.content.Context
import com.zyelite.kghub.R
import java.text.SimpleDateFormat
import java.util.*

/**
 * @author ZyElite
 * @create 2018/4/4
 * @description DateUtil
 */

object DateUtil {

    fun str2Time(context: Context, date: Date): String {
        val leadTime = System.currentTimeMillis() - date.time
        val MILLIS_LIMIT = 1000.0
        val SECONDS_LIMIT = 60 * MILLIS_LIMIT
        val MINUTES_LIMIT = 60 * SECONDS_LIMIT
        val HOURS_LIMIT = 24 * MINUTES_LIMIT
        val DAYS_LIMIT = 30 * HOURS_LIMIT
        return if (leadTime < MILLIS_LIMIT) {
            context.getString(R.string.just_now)
        } else if (leadTime < SECONDS_LIMIT) {
            Math.round(leadTime / MILLIS_LIMIT).toString() + " " + context.getString(R.string.seconds_ago)
        } else if (leadTime < MINUTES_LIMIT) {
            Math.round(leadTime / SECONDS_LIMIT).toString() + " " + context.getString(R.string.minutes_ago)
        } else if (leadTime < HOURS_LIMIT) {
            Math.round(leadTime / MINUTES_LIMIT).toString() + " " + context.getString(R.string.hours_ago)
        } else if (leadTime < DAYS_LIMIT) {
            Math.round(leadTime / HOURS_LIMIT).toString() + " " + context.getString(R.string.days_ago)
        } else {
            getDateStr(date)
        }
    }

    fun getDateStr(date: Date): String {
        val format = SimpleDateFormat("yyyy-MM-dd")
        return format.format(date)
    }
}