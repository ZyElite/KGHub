package com.zyelite.kghub.widget

import android.content.Context
import android.view.View
import android.widget.LinearLayout
import android.widget.TextView
import com.zyelite.kghub.R


/**
 * @author ZyElite
 * @create 2018/5/8
 * @description SuperSwipeRefreshLayoutFootView
 */

class SuperSwipeRefreshLayoutFootView(context: Context?) : LinearLayout(context) {
    var footViewHeight: Int = 0
    var textView: TextView

    init {
        val view = View.inflate(getContext(), R.layout.super_swipe_refresh_layout_foot_view, null)
        textView = view.findViewById(R.id.super_easy_refresh_text_view) as TextView
        addView(view)
        val metrics = resources.displayMetrics
        footViewHeight = (50 * metrics.density).toInt()
    }

//    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
//        super.onMeasure(widthMeasureSpec, View.MeasureSpec.makeMeasureSpec(footViewHeight, View.MeasureSpec.EXACTLY))
//    }
}