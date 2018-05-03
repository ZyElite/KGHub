package com.zyelite.kghub.widget

import android.content.Context
import android.os.Build
import android.support.annotation.RequiresApi
import android.support.annotation.VisibleForTesting
import android.support.v4.view.NestedScrollingChild
import android.support.v4.view.NestedScrollingParent
import android.support.v4.view.ViewCompat
import android.support.v4.widget.CircularProgressDrawable
import android.support.v4.widget.ListViewCompat
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.View
import android.view.ViewConfiguration
import android.view.ViewGroup
import android.view.animation.DecelerateInterpolator
import android.widget.ListView
import com.zyelite.kghub.utils.LogUtil


/**
 * @author ZyElite
 * @create 2018/4/24
 * @description SuperSwipeRefreshLayout add load more data
 */

class SuperSwipeRefreshLayout : ViewGroup, NestedScrollingParent, NestedScrollingChild {
    // Default background for the progress spinner
    private val CIRCLE_BG_LIGHT = -0x50506
    private var mContentView: View? = null
    private var mProgress: CircularProgressDrawable? = null
    private var mCircleView: CircleImageView? = null
    private var mCurrentTargetOffsetTop: Int = 0
    private var mCircleDiameter: Int = 0
    private var mOriginalOffsetTop: Int = 0

    @VisibleForTesting
    private val CIRCLE_DIAMETER = 40
    @VisibleForTesting
    private val CIRCLE_DIAMETER_LARGE = 56

    private var mDecelerateInterpolator: DecelerateInterpolator? = null
    private val DECELERATE_INTERPOLATION_FACTOR = 2f
    //是否开始下拉
    private var mIsBeingDragged: Boolean = false
    // Target is returning to its start offset because it was cancelled or a refresh was triggered
    private var mReturningToStart: Boolean = false
    //触发移动事件的最短距离
    private var mTouchSlop: Int = 0

    private var mInitialMotionY: Float = 0F

    //手指第一次按下的位置
    private var mInitialDownY: Float = 0F

    private var mRefreshing = false

    private var isLoadingMore = false


    private var mActivePointerId: Int = 0

    constructor(context: Context) : this(context, null)

    constructor(context: Context, attributeSet: AttributeSet?) : this(context, attributeSet, 0)

    constructor(context: Context, attributeSet: AttributeSet?, defStyleAttr: Int) : super(context, attributeSet, defStyleAttr) {
        val metrics = resources.displayMetrics

        /**触发移动事件的最小距离，自定义View处理touch事件的时候，有的时候需要判断用户是否真的存在movie，
         * 系统提供了这样的方法。表示滑动的时候，手的移动要大于这个返回的距离值才开始移动控件。*/
        mTouchSlop = ViewConfiguration.get(context).scaledTouchSlop * 2
        mCircleDiameter = (CIRCLE_DIAMETER * metrics.density).toInt()
        mCurrentTargetOffsetTop = -mCircleDiameter
        mOriginalOffsetTop = mCurrentTargetOffsetTop

        //获取移动动画的差值器
        mDecelerateInterpolator = DecelerateInterpolator(DECELERATE_INTERPOLATION_FACTOR);

        createProgressView()
    }

    @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
    constructor(context: Context, attributeSet: AttributeSet, defStyleAttr: Int, defStyleRes: Int) : super(context, attributeSet, defStyleAttr, defStyleRes)

    private fun createProgressView() {
        mCircleView = CircleImageView(context, CIRCLE_BG_LIGHT)
        mProgress = CircularProgressDrawable(context)
        mProgress?.setStyle(CircularProgressDrawable.DEFAULT)
        mCircleView?.setImageDrawable(mProgress)
        //mCircleView?.visibility = View.GONE
        addView(mCircleView)
    }

    override fun onLayout(changed: Boolean, left: Int, top: Int, right: Int, bottom: Int) {
        mContentView?.let {
            val childLeft = paddingLeft
            val childTop = paddingTop
            val childWidth = measuredWidth - paddingLeft - paddingRight
            val childHeight = measuredHeight - paddingTop - paddingBottom
            it.layout(childLeft, childTop, childLeft + childWidth, childTop + childHeight)
        }
        mCircleView?.let {
            val circleWidth = it.measuredWidth
            val circleHeight = it.measuredHeight
            it.layout(width / 2 - circleWidth / 2, mCurrentTargetOffsetTop,
                    width / 2 + circleWidth / 2, mCurrentTargetOffsetTop + circleHeight)
        }
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)
        if (mContentView == null) {
            ensureTarget()
        }
        if (mContentView == null) return

        mContentView?.measure(View.MeasureSpec.makeMeasureSpec(
                measuredWidth - paddingLeft - paddingRight,
                View.MeasureSpec.EXACTLY), View.MeasureSpec.makeMeasureSpec(
                measuredHeight - paddingTop - paddingBottom, View.MeasureSpec.EXACTLY))

        mCircleView?.measure(View.MeasureSpec.makeMeasureSpec(mCircleDiameter, View.MeasureSpec.EXACTLY),
                View.MeasureSpec.makeMeasureSpec(mCircleDiameter, View.MeasureSpec.EXACTLY))

    }


    /**
     * One of DEFAULT, or LARGE.
     *
     */
    fun setSize(size: Int) {
        if (size != CircularProgressDrawable.LARGE && size != CircularProgressDrawable.DEFAULT) {
            return
        }
        val metrics = resources.displayMetrics
        mCircleDiameter = if (size == CircularProgressDrawable.LARGE) {
            (CIRCLE_DIAMETER_LARGE * metrics.density).toInt()
        } else {
            (CIRCLE_DIAMETER * metrics.density).toInt()
        }
        // force the bounds of the progress circle inside the circle view to
        // update by setting it to null before updating its size and then
        // re-setting it
        mCircleView?.setImageDrawable(null)
        mProgress?.setStyle(size)
        mCircleView?.setImageDrawable(mProgress)
    }

    override fun dispatchTouchEvent(ev: MotionEvent?): Boolean {
        return super.dispatchTouchEvent(ev)
    }


    override fun onInterceptTouchEvent(ev: MotionEvent): Boolean {
        //获取手势动作
        val action = ev.actionMasked
        val pointerIndex: Int
        if (!isEnabled || canChildScrollUp() && canChildScrollDown() || mRefreshing || isLoadingMore) {
            return false
        }
        when (action) {
            MotionEvent.ACTION_DOWN -> {
                moveToStart()
                //得到第一个手指
                mActivePointerId = ev.getPointerId(0)
                pointerIndex = ev.findPointerIndex(mActivePointerId)
                if (pointerIndex < 0) {
                    return false
                }
                mInitialDownY = ev.getY(pointerIndex)
            }
            MotionEvent.ACTION_MOVE -> {
                if (mActivePointerId == -1) {
                    LogUtil.e("Got ACTION_MOVE event but don't have an active pointer id.")
                    return false
                }
                pointerIndex = ev.findPointerIndex(mActivePointerId)
                if (pointerIndex < 0) {
                    return false
                }
                val offsetY = ev.getY(pointerIndex) - mInitialDownY
                //若是顶部不能滑动，则offsetY是正值，直接与mTouchSlop做比较。
                //若是底部不能滑动，则offsetY是负值，取反后与mTouchSlop做比较。
                if (Math.abs(offsetY) > mTouchSlop && !mIsBeingDragged) {
                    mInitialMotionY = mInitialDownY + mTouchSlop
                    mIsBeingDragged = true
                    return true
                }
            }
            MotionEvent.ACTION_POINTER_UP -> {
                //有手指抬起
                onSecondaryPointerUp(ev)
            }
            MotionEvent.ACTION_UP, MotionEvent.ACTION_CANCEL -> {
                mIsBeingDragged = false
            }
        }
        return mIsBeingDragged
    }

    /**
     * 第二个手指抬起 出入 SwipeRefreshLayout
     */
    private fun onSecondaryPointerUp(ev: MotionEvent) {
        val pointerIndex = ev.actionIndex
        val pointerId = ev.getPointerId(pointerIndex)
        if (pointerId == mActivePointerId) {
            // This was our active pointer going up. Choose a new
            // active pointer and adjust accordingly.
            val newPointerIndex = if (pointerIndex == 0) 1 else 0
            mActivePointerId = ev.getPointerId(newPointerIndex)
        }
    }

    /**
     * 移动目标view到初始位置
     */
    private fun moveToStart() {
        val offset = mOriginalOffsetTop - mCircleView!!.top
        setTargetOffsetTopAndBottom(offset)
    }

    /**
     * 移动某个view，移动的距离。此时移动的是mRefreshView，由于此时改变了mCurrentTargetOffsetTop的值，
     * 而且onMeasure方法和onLayout方法会执行，所以其他view也会移动
     */
    private fun setTargetOffsetTopAndBottom(offset: Int) {
        mCircleView?.bringToFront()
        ViewCompat.offsetTopAndBottom(mCircleView, offset)
        mCurrentTargetOffsetTop = mCircleView!!.top
    }

    override fun onTouchEvent(event: MotionEvent?): Boolean {
        return super.onTouchEvent(event)
    }

    /**
     * 判断view向上是否可以滑动
     */
    private fun canChildScrollUp(): Boolean {
        return if (mContentView is ListView) {
            ListViewCompat.canScrollList(mContentView as ListView, -1)
        } else mContentView!!.canScrollVertically(-1)
    }

    /**
     * 判断view向下是否可以滑动
     */
    private fun canChildScrollDown(): Boolean {
        return mContentView!!.canScrollVertically(1)
    }

    /**
     * 完成绘制时
     */
    override fun onFinishInflate() {
        super.onFinishInflate()
    }

    /**
     * 找到内容view 被包裹的内容
     */
    private fun ensureTarget() {
        // Don't bother getting the parent height if the parent hasn't been laid out yet.
        if (mContentView == null) {
            for (i in 0 until childCount) {
                val child = getChildAt(i)
                if (child != mCircleView) {
                    mContentView = child
                    break
                }
            }
        }
    }
}