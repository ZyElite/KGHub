package com.zyelite.kghub.widget

import android.content.Context
import android.support.v4.view.NestedScrollingChild
import android.support.v4.view.NestedScrollingParent
import android.support.v4.widget.CircularProgressDrawable
import android.util.AttributeSet
import android.view.View
import android.view.ViewGroup
import com.zyelite.kghub.utils.LogUtil

/**
 * @author ZyElite
 * @create 2018/4/24
 * @description SuperSwipeRefreshLayout add load more data
 */

class SuperSwipeRefreshLayout : ViewGroup, NestedScrollingParent, NestedScrollingChild {
    // Default background for the progress spinner
    private val CIRCLE_BG_LIGHT = -0x50506
    private lateinit var mContentView: View
    private lateinit var mProgress: CircularProgressDrawable
    private lateinit var mCircleView: CircleImageView
    constructor(context: Context) : super(context)

    constructor(context: Context, attributeSet: AttributeSet) : super(context, attributeSet)

    override fun onLayout(changed: Boolean, left: Int, top: Int, right: Int, bottom: Int) {
        mContentView.let {
            val childLeft = paddingLeft
            val childTop = paddingTop
            val childWidth = measuredWidth - paddingLeft - paddingRight
            val childHeight = measuredHeight - paddingTop - paddingBottom
            it.layout(childLeft, childTop, childLeft + childWidth, childTop + childHeight)
        }
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)
        LogUtil.e(heightMeasureSpec.toString())
        mContentView.measure(widthMeasureSpec, heightMeasureSpec)
    }

    private fun createProgressView() {
        mCircleView = CircleImageView(context, CIRCLE_BG_LIGHT)
        mProgress = CircularProgressDrawable(context)
        mProgress.setStyle(CircularProgressDrawable.DEFAULT)
        mCircleView.setImageDrawable(mProgress)
        mCircleView.setVisibility(View.GONE)
        addView(mCircleView)
    }

    /**
     * 完成绘制时
     */
    override fun onFinishInflate() {
        super.onFinishInflate()
        if (childCount == 1) {
            mContentView = getChildAt(0)
        }
    }

}