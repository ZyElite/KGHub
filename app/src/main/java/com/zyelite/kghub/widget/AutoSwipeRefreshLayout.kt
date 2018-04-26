package com.zyelite.kghub.widget

import android.content.Context
import android.support.v4.widget.SwipeRefreshLayout
import android.util.AttributeSet
import android.view.View


/**
 * @author ZyElite
 * @create 2018/4/16
 * @description AutoSwipeRefreshLayout
 */

class AutoSwipeRefreshLayout : SwipeRefreshLayout {

    constructor(context: Context) : super(context)

    constructor(context: Context, attrs: AttributeSet) : super(context, attrs)

    /**
     * 自动刷新
     */
    fun autoRefresh() {
        try {
            val mCircleView = SwipeRefreshLayout::class.java.getDeclaredField("mCircleView")
            mCircleView.isAccessible = true
            val progress = mCircleView.get(this) as View
            progress.visibility = View.VISIBLE
            val setRefreshing = SwipeRefreshLayout::class.java.getDeclaredMethod("setRefreshing", Boolean::class.javaPrimitiveType, Boolean::class.javaPrimitiveType)
            setRefreshing.isAccessible = true
            setRefreshing.invoke(this, true, true)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
}