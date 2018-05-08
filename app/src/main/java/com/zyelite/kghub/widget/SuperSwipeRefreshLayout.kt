package com.zyelite.kghub.widget

import android.content.Context
import android.support.annotation.ColorInt
import android.support.annotation.ColorRes
import android.support.annotation.VisibleForTesting
import android.support.v4.content.ContextCompat
import android.support.v4.view.*
import android.support.v4.widget.CircularProgressDrawable
import android.support.v4.widget.ListViewCompat
import android.util.AttributeSet
import android.util.Log
import android.view.MotionEvent
import android.view.View
import android.view.ViewConfiguration
import android.view.ViewGroup
import android.view.animation.Animation
import android.view.animation.DecelerateInterpolator
import android.view.animation.Transformation
import android.widget.AbsListView
import android.widget.ListView
import com.zyelite.kghub.utils.LogUtil

/**
 * @author ZyElite
 * @create 2018/5/8
 * @description SuperSwipeRefreshLayout
 */
class SuperSwipeRefreshLayout : ViewGroup, NestedScrollingParent, NestedScrollingChild {
    // Maps to ProgressBar.Large style
    val LARGE = CircularProgressDrawable.LARGE
    // Maps to ProgressBar default style
    val DEFAULT = CircularProgressDrawable.DEFAULT

    @VisibleForTesting
    internal val CIRCLE_DIAMETER = 40
    @VisibleForTesting
    internal val CIRCLE_DIAMETER_LARGE = 56

    private val LOG_TAG = SuperSwipeRefreshLayout::class.java.simpleName

    private val MAX_ALPHA = 255
    private val STARTING_PROGRESS_ALPHA = (.3f * MAX_ALPHA).toInt()

    private val DECELERATE_INTERPOLATION_FACTOR = 2f
    private val INVALID_POINTER = -1
    private val DRAG_RATE = .5f

    // Max amount of circle that can be filled by progress during swipe gesture,
    // where 1.0 is a full circle
    private val MAX_PROGRESS_ANGLE = .8f

    private val SCALE_DOWN_DURATION = 150

    private val ALPHA_ANIMATION_DURATION = 300

    private val ANIMATE_TO_TRIGGER_DURATION = 200

    private val ANIMATE_TO_START_DURATION = 200

    // Default background for the progress spinner
    private val CIRCLE_BG_LIGHT = -0x50506
    // Default offset in dips from the top of the view to where the progress spinner should stop
    private val DEFAULT_CIRCLE_TARGET = 64
    private val LAYOUT_ATTRS = intArrayOf(android.R.attr.enabled)

    private var mContentView: View? = null // the target of the gesture
    private var mListener: SuperSwipeRefreshLayout.OnRefreshListener? = null
    //是否刷新
    private var mRefreshing = false
    //加载更多
    private var isLoadingMore = false
    private var mTouchSlop: Int
    private var mTotalDragDistance = -1f

    private var mTotalUnconsumed: Float = 0F
    private val mNestedScrollingParentHelper: NestedScrollingParentHelper
    private val mNestedScrollingChildHelper: NestedScrollingChildHelper
    private val mParentScrollConsumed = IntArray(2)
    private val mParentOffsetInWindow = IntArray(2)
    private var mNestedScrollInProgress: Boolean = false

    private val mMediumAnimationDuration: Int
    private var mCurrentTargetOffsetTop: Int = 0

    //上拉加载的view高度
    private var mInitialScrollUpY = 0
    //加载更多
    private lateinit var mFooterView: SuperSwipeRefreshLayoutFootView


    private var mInitialMotionY: Float = 0F
    private var mInitialDownY: Float = 0F
    private var mIsBeingDragged: Boolean = false
    private var mActivePointerId = INVALID_POINTER
    // Whether this item is scaled up rather than clipped
    private var mScale: Boolean = false

    // Target is returning to its start offset because it was cancelled or a
    // refresh was triggered.
    private var mReturningToStart: Boolean = false
    private val mDecelerateInterpolator: DecelerateInterpolator

    private lateinit var mCircleView: CircleImageView
    private var mCircleViewIndex = -1

    private var mFrom: Int = 0

    private var mStartingScale: Float = 0F

    //进度微调器应该在此视图顶部的像素偏移量出现
    private var progressViewStartOffset: Int = 0

    private var progressViewEndOffset: Int = 0

    private lateinit var mProgress: CircularProgressDrawable

    private var mScaleAnimation: Animation? = null

    private var mScaleDownAnimation: Animation? = null

    private var mAlphaStartAnimation: Animation? = null

    private var mAlphaMaxAnimation: Animation? = null

    private var mScaleDownToStartAnimation: Animation? = null

    private var mNotify: Boolean = false

    private var progressCircleDiameter: Int = 0

    // Whether the client has set a custom starting position;
    private var mUsingCustomStart: Boolean = false

    private var mChildScrollUpCallback: SuperSwipeRefreshLayout.OnChildScrollUpCallback? = null

    private val mRefreshListener = object : Animation.AnimationListener {
        override fun onAnimationStart(animation: Animation?) {}

        override fun onAnimationRepeat(animation: Animation?) {}

        override fun onAnimationEnd(animation: Animation?) {
            if (mRefreshing) {
                // Make sure the progress view is fully visible
                mProgress.alpha = MAX_ALPHA
                mProgress.start()
                if (mNotify) {
                    if (mListener != null) {
                        mListener!!.onRefresh()
                    }
                }
                mCurrentTargetOffsetTop = mCircleView.top
            } else {
                reset()
            }
        }
    }

    /**
     * 是否刷新
     */
    var isRefreshing: Boolean
        get() = mRefreshing
        set(refreshing) = if (refreshing && mRefreshing != refreshing) {
            mRefreshing = refreshing
            val endTarget: Int = if (!mUsingCustomStart) {
                progressViewEndOffset + progressViewStartOffset
            } else {
                progressViewEndOffset
            }
            setTargetOffsetTopAndBottom(endTarget - mCurrentTargetOffsetTop)
            mNotify = false
            startScaleUpAnimation(mRefreshListener)
        } else {
            setRefreshing(refreshing, false)
        }

    private val mAnimateToCorrectPosition = object : Animation() {
        public override fun applyTransformation(interpolatedTime: Float, t: Transformation) {
            var targetTop = 0
            var endTarget = 0
            if (!mUsingCustomStart) {
                endTarget = progressViewEndOffset - Math.abs(progressViewStartOffset)
            } else {
                endTarget = progressViewEndOffset
            }
            targetTop = mFrom + ((endTarget - mFrom) * interpolatedTime).toInt()
            val offset = targetTop - mCircleView.top
            setTargetOffsetTopAndBottom(offset)
            mProgress.arrowScale = 1 - interpolatedTime
        }
    }

    private val mAnimateToStartPosition = object : Animation() {
        public override fun applyTransformation(interpolatedTime: Float, t: Transformation) {
            moveToStart(interpolatedTime)
        }
    }

    internal fun reset() {
        mCircleView.clearAnimation()
        mProgress.stop()
        mCircleView.visibility = View.GONE
        setColorViewAlpha(MAX_ALPHA)
        // Return the circle to its start position
        if (mScale) {
            setAnimationProgress(0f /* animation complete and view is hidden */)
        } else {
            setTargetOffsetTopAndBottom(progressViewStartOffset - mCurrentTargetOffsetTop)
        }
        mCurrentTargetOffsetTop = mCircleView.top
    }

    override fun setEnabled(enabled: Boolean) {
        super.setEnabled(enabled)
        if (!enabled) {
            reset()
        }
    }

    override fun onDetachedFromWindow() {
        super.onDetachedFromWindow()
        reset()
    }

    private fun setColorViewAlpha(targetAlpha: Int) {
        mCircleView.background.alpha = targetAlpha
        mProgress.alpha = targetAlpha
    }

    fun setProgressViewOffset(scale: Boolean, start: Int, end: Int) {
        mScale = scale
        progressViewStartOffset = start
        progressViewEndOffset = end
        mUsingCustomStart = true
        reset()
        mRefreshing = false
    }

    fun setProgressViewEndTarget(scale: Boolean, end: Int) {
        progressViewEndOffset = end
        mScale = scale
        mCircleView.invalidate()
    }

    fun setSize(size: Int) {
        if (size != CircularProgressDrawable.LARGE && size != CircularProgressDrawable.DEFAULT) {
            return
        }
        val metrics = resources.displayMetrics
        if (size == CircularProgressDrawable.LARGE) {
            progressCircleDiameter = (CIRCLE_DIAMETER_LARGE * metrics.density).toInt()
        } else {
            progressCircleDiameter = (CIRCLE_DIAMETER * metrics.density).toInt()
        }
        // force the bounds of the progress circle inside the circle view to
        // update by setting it to null before updating its size and then
        // re-setting it
        mCircleView.setImageDrawable(null)
        mProgress.setStyle(size)
        mCircleView.setImageDrawable(mProgress)
    }


    constructor(context: Context) : this(context, null)

    constructor(context: Context, attributeSet: AttributeSet?) : this(context, attributeSet, 0)

    constructor(context: Context, attributeSet: AttributeSet?, defStyleAttr: Int) : super(context, attributeSet, defStyleAttr) {

        /**触发移动事件的最小距离，自定义View处理touch事件的时候，有的时候需要判断用户是否真的存在movie，
         * 系统提供了这样的方法。表示滑动的时候，手的移动要大于这个返回的距离值才开始移动控件。*/
        mTouchSlop = ViewConfiguration.get(context).scaledTouchSlop
        mMediumAnimationDuration = resources.getInteger(
                android.R.integer.config_mediumAnimTime)
        setWillNotDraw(false)
        //获取移动动画的差值器
        mDecelerateInterpolator = DecelerateInterpolator(DECELERATE_INTERPOLATION_FACTOR)
        val metrics = resources.displayMetrics
        progressCircleDiameter = (CIRCLE_DIAMETER * metrics.density).toInt()
        createProgressView()
        isChildrenDrawingOrderEnabled = true
        // the absolute offset has to take into account that the circle starts at an offset
        progressViewEndOffset = (DEFAULT_CIRCLE_TARGET * metrics.density).toInt()
        mTotalDragDistance = progressViewEndOffset.toFloat()
        mNestedScrollingParentHelper = NestedScrollingParentHelper(this)

        mNestedScrollingChildHelper = NestedScrollingChildHelper(this)
        isNestedScrollingEnabled = true
        mCurrentTargetOffsetTop = -progressCircleDiameter
        progressViewStartOffset = mCurrentTargetOffsetTop
        moveToStart(1.0f)
        val a = context.obtainStyledAttributes(attributeSet, LAYOUT_ATTRS)
        isEnabled = a.getBoolean(0, true)
        a.recycle()
    }


    override fun getChildDrawingOrder(childCount: Int, i: Int): Int {
        return when {
            mCircleViewIndex < 0 -> i
            i == childCount - 1 -> // Draw the selected child last
                mCircleViewIndex
            i >= mCircleViewIndex -> // Move the children after the selected child earlier one
                i + 1
            else -> // Keep the children before the selected child the same
                i
        }
    }

    private fun createProgressView() {
        mCircleView = CircleImageView(context, CIRCLE_BG_LIGHT)
        mFooterView = SuperSwipeRefreshLayoutFootView(context)
        mProgress = CircularProgressDrawable(context)
        mProgress.setStyle(CircularProgressDrawable.DEFAULT)
        mCircleView.setImageDrawable(mProgress)
        mCircleView.visibility = View.GONE
        addView(mCircleView)
        addView(mFooterView)
    }

    fun setOnRefreshListener(listener: SuperSwipeRefreshLayout.OnRefreshListener?) {
        mListener = listener
    }

    private fun startScaleUpAnimation(listener: Animation.AnimationListener?) {
        mCircleView.visibility = View.VISIBLE
        mProgress.alpha = MAX_ALPHA
        mScaleAnimation = object : Animation() {
            public override fun applyTransformation(interpolatedTime: Float, t: Transformation) {
                setAnimationProgress(interpolatedTime)
            }
        }
        mScaleAnimation!!.duration = mMediumAnimationDuration.toLong()
        if (listener != null) {
            mCircleView.setAnimationListener(listener)
        }
        mCircleView.clearAnimation()
        mCircleView.startAnimation(mScaleAnimation)
    }

    private fun setAnimationProgress(progress: Float) {
        mCircleView.scaleX = progress
        mCircleView.scaleY = progress
    }

    private fun setRefreshing(refreshing: Boolean, notify: Boolean) {
        if (mRefreshing != refreshing) {
            mNotify = notify
            ensureTarget()
            mRefreshing = refreshing
            if (mRefreshing) {
                animateOffsetToCorrectPosition(mCurrentTargetOffsetTop, mRefreshListener)
            } else {
                startScaleDownAnimation(mRefreshListener)
            }
        }
    }

    private fun startScaleDownAnimation(listener: Animation.AnimationListener?) {
        mScaleDownAnimation = object : Animation() {
            public override fun applyTransformation(interpolatedTime: Float, t: Transformation) {
                setAnimationProgress(1 - interpolatedTime)
            }
        }
        mScaleDownAnimation!!.duration = SCALE_DOWN_DURATION.toLong()
        mCircleView.setAnimationListener(listener)
        mCircleView.clearAnimation()
        mCircleView.startAnimation(mScaleDownAnimation)
    }

    private fun startProgressAlphaStartAnimation() {
        mAlphaStartAnimation = startAlphaAnimation(mProgress.alpha, STARTING_PROGRESS_ALPHA)
    }

    private fun startProgressAlphaMaxAnimation() {
        mAlphaMaxAnimation = startAlphaAnimation(mProgress.alpha, MAX_ALPHA)
    }

    private fun startAlphaAnimation(startingAlpha: Int, endingAlpha: Int): Animation {
        val alpha = object : Animation() {
            public override fun applyTransformation(interpolatedTime: Float, t: Transformation) {
                mProgress.alpha = (startingAlpha + (endingAlpha - startingAlpha) * interpolatedTime).toInt()
            }
        }
        alpha.duration = ALPHA_ANIMATION_DURATION.toLong()
        // Clear out the previous animation listeners.
        mCircleView.setAnimationListener(null)
        mCircleView.clearAnimation()
        mCircleView.startAnimation(alpha)
        return alpha
    }


    @Deprecated("Use {@link #setProgressBackgroundColorSchemeResource(int)}")
    fun setProgressBackgroundColor(colorRes: Int) {
        setProgressBackgroundColorSchemeResource(colorRes)
    }

    fun setProgressBackgroundColorSchemeResource(@ColorRes colorRes: Int) {
        setProgressBackgroundColorSchemeColor(ContextCompat.getColor(context, colorRes))
    }

    fun setProgressBackgroundColorSchemeColor(@ColorInt color: Int) {
        mCircleView.setBackgroundColor(color)
    }

    @Deprecated("")
    fun setColorScheme(@ColorRes vararg colors: Int) {
        setColorSchemeResources(*colors)
    }

    fun setColorSchemeResources(@ColorRes vararg colorResIds: Int) {
        val context = context
        val colorRes = IntArray(colorResIds.size)
        for (i in colorResIds.indices) {
            colorRes[i] = ContextCompat.getColor(context, colorResIds[i])
        }
        setColorSchemeColors(*colorRes)
    }

    fun setColorSchemeColors(@ColorInt vararg colors: Int) {
        ensureTarget()
        mProgress.setColorSchemeColors(*colors)
    }


    /**
     * 找到内容view 被包裹的内容
     */
    private fun ensureTarget() {
        // Don't bother getting the parent height if the parent hasn't been laid
        // out yet.
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

    fun setDistanceToTriggerSync(distance: Int) {
        mTotalDragDistance = distance.toFloat()
    }

    override fun onLayout(changed: Boolean, left: Int, top: Int, right: Int, bottom: Int) {
        if (childCount == 0) {
            return
        }
        if (mContentView == null) {
            ensureTarget()
        }
        if (mContentView == null) {
            return
        }
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

        mFooterView.let {
            val footerViewTop = measuredHeight - paddingTop - paddingBottom + mCurrentTargetOffsetTop
            val footViewLeft = (width - mFooterView.measuredWidth) / 2
            mFooterView.layout(footerViewTop,footerViewTop,footViewLeft + mFooterView.measuredWidth,footerViewTop+mFooterView.measuredHeight)
        }

    }

    public override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)
        if (mContentView == null) {
            ensureTarget()
        }
        if (mContentView == null) {
            return
        }
        mContentView!!.measure(View.MeasureSpec.makeMeasureSpec(
                measuredWidth - paddingLeft - paddingRight,
                View.MeasureSpec.EXACTLY), View.MeasureSpec.makeMeasureSpec(
                measuredHeight - paddingTop - paddingBottom, View.MeasureSpec.EXACTLY))
        mCircleView.measure(View.MeasureSpec.makeMeasureSpec(progressCircleDiameter, View.MeasureSpec.EXACTLY),
                View.MeasureSpec.makeMeasureSpec(progressCircleDiameter, View.MeasureSpec.EXACTLY))
        mFooterView.measure(widthMeasureSpec,heightMeasureSpec)
        mCircleViewIndex = -1
        // Get the index of the circleview.
        for (index in 0 until childCount) {
            if (getChildAt(index) === mCircleView) {
                mCircleViewIndex = index
                break
            }
        }
    }

    /**
     * 判断view向下是否可以滑动
     */
    private fun canChildScrollUp(): Boolean {
        if (mChildScrollUpCallback != null) {
            return mChildScrollUpCallback!!.canChildScrollUp(this, mContentView)
        }
        return if (mContentView is ListView) {
            ListViewCompat.canScrollList((mContentView as ListView?)!!, -1)
        } else mContentView!!.canScrollVertically(-1)
    }

    /**
     * 判断view向下是否可以滑动
     */
    private fun canChildScrollDown(): Boolean {
        return mContentView!!.canScrollVertically(1)
    }


    fun setOnChildScrollUpCallback(callback: SuperSwipeRefreshLayout.OnChildScrollUpCallback?) {
        mChildScrollUpCallback = callback
    }

    override fun onInterceptTouchEvent(ev: MotionEvent): Boolean {
        ensureTarget()

        val action = ev.actionMasked
        val pointerIndex: Int

        if (mReturningToStart && action == MotionEvent.ACTION_DOWN) {
            mReturningToStart = false
        }

        if (!isEnabled || mReturningToStart || (canChildScrollUp() && canChildScrollDown())
                || mRefreshing || mNestedScrollInProgress || isLoadingMore) {
            // Fail fast if we're not in a state where a swipe is possible
            return false
        }

        when (action) {
            MotionEvent.ACTION_DOWN -> {
                setTargetOffsetTopAndBottom(progressViewStartOffset - mCircleView.top)
                //得到第一个手指
                mActivePointerId = ev.getPointerId(0)
                mIsBeingDragged = false

                pointerIndex = ev.findPointerIndex(mActivePointerId)
                if (pointerIndex < 0) {
                    return false
                }
                mInitialDownY = ev.getY(pointerIndex)
            }

            MotionEvent.ACTION_MOVE -> {
                if (mActivePointerId == INVALID_POINTER) {
                    Log.e(LOG_TAG, "Got ACTION_MOVE event but don't have an active pointer id.")
                    return false
                }

                pointerIndex = ev.findPointerIndex(mActivePointerId)
                if (pointerIndex < 0) {
                    return false
                }
                var offsetY = ev.getY(pointerIndex) - mInitialDownY
                if (!canChildScrollUp()) {
                    //若是顶部不能滑动，则offsetY是正值，直接与mTouchSlop做比较。
                }
                if (!canChildScrollDown()) {
                    //若是底部不能滑动，则offsetY是负值，取反后与mTouchSlop做比较。
                    offsetY = -offsetY
                }
                if (offsetY > mTouchSlop && !mIsBeingDragged) {
                    startDragging(offsetY)
                }
            }
        //有手指抬起
            MotionEvent.ACTION_POINTER_UP -> onSecondaryPointerUp(ev)

            MotionEvent.ACTION_UP, MotionEvent.ACTION_CANCEL -> {
                mIsBeingDragged = false
                mActivePointerId = INVALID_POINTER
            }
        }

        return mIsBeingDragged
    }

    override fun requestDisallowInterceptTouchEvent(b: Boolean) {
        // if this is a List < L or another view that doesn't support nested
        // scrolling, ignore this request so that the vertical scroll event
        // isn't stolen
        if (android.os.Build.VERSION.SDK_INT < 21 && mContentView is AbsListView || mContentView != null && !ViewCompat.isNestedScrollingEnabled(mContentView!!)) {
            // Nope.
        } else {
            super.requestDisallowInterceptTouchEvent(b)
        }
    }

    // NestedScrollingParent

    override fun onStartNestedScroll(child: View, target: View, nestedScrollAxes: Int): Boolean {
        return (isEnabled && !mReturningToStart && !mRefreshing
                && nestedScrollAxes and ViewCompat.SCROLL_AXIS_VERTICAL != 0)
    }

    override fun onNestedScrollAccepted(child: View, target: View, axes: Int) {
        // Reset the counter of how much leftover scroll needs to be consumed.
        mNestedScrollingParentHelper.onNestedScrollAccepted(child, target, axes)
        // Dispatch up to the nested parent
        startNestedScroll(axes and ViewCompat.SCROLL_AXIS_VERTICAL)
        mTotalUnconsumed = 0f
        mNestedScrollInProgress = true
    }

    override fun onNestedPreScroll(target: View, dx: Int, dy: Int, consumed: IntArray) {
        // If we are in the middle of consuming, a scroll, then we want to move the spinner back up
        // before allowing the list to scroll
        if (dy > 0 && mTotalUnconsumed > 0) {
            if (dy > mTotalUnconsumed) {
                consumed[1] = dy - mTotalUnconsumed.toInt()
                mTotalUnconsumed = 0f
            } else {
                mTotalUnconsumed -= dy.toFloat()
                consumed[1] = dy
            }
            moveSpinner(mTotalUnconsumed)
        }

        // If a client layout is using a custom start position for the circle
        // view, they mean to hide it again before scrolling the child view
        // If we get back to mTotalUnconsumed == 0 and there is more to go, hide
        // the circle so it isn't exposed if its blocking content is moved
        if (mUsingCustomStart && dy > 0 && mTotalUnconsumed == 0f
                && Math.abs(dy - consumed[1]) > 0) {
            mCircleView.visibility = View.GONE
        }

        // Now let our nested parent consume the leftovers
        val parentConsumed = mParentScrollConsumed
        if (dispatchNestedPreScroll(dx - consumed[0], dy - consumed[1], parentConsumed, null)) {
            consumed[0] += parentConsumed[0]
            consumed[1] += parentConsumed[1]
        }
    }

    override fun getNestedScrollAxes(): Int {
        return mNestedScrollingParentHelper.nestedScrollAxes
    }

    override fun onStopNestedScroll(target: View) {
        mNestedScrollingParentHelper.onStopNestedScroll(target)
        mNestedScrollInProgress = false
        // Finish the spinner for nested scrolling if we ever consumed any
        // unconsumed nested scroll
        if (mTotalUnconsumed > 0) {
            finishSpinner(mTotalUnconsumed)
            mTotalUnconsumed = 0f
        }
        // Dispatch up our nested parent
        stopNestedScroll()
    }

    override fun onNestedScroll(target: View, dxConsumed: Int, dyConsumed: Int,
                                dxUnconsumed: Int, dyUnconsumed: Int) {
        // Dispatch up to the nested parent first
        dispatchNestedScroll(dxConsumed, dyConsumed, dxUnconsumed, dyUnconsumed,
                mParentOffsetInWindow)

        // This is a bit of a hack. Nested scrolling works from the bottom up, and as we are
        // sometimes between two nested scrolling views, we need a way to be able to know when any
        // nested scrolling parent has stopped handling events. We do that by using the
        // 'offset in window 'functionality to see if we have been moved from the event.
        // This is a decent indication of whether we should take over the event stream or not.
        val dy = dyUnconsumed + mParentOffsetInWindow[1]
        if (dy < 0 && !canChildScrollUp()) {
            mTotalUnconsumed += Math.abs(dy).toFloat()
            moveSpinner(mTotalUnconsumed)
        }
        LogUtil.e("dyUnconsumed:  $dyUnconsumed  ")

        val b = !canChildScrollDown()
        LogUtil.e("canChildScrollDown: $b")
        if (dyUnconsumed > 0 && b) {
            loadMore()
        }

    }

    // NestedScrollingChild

    override fun setNestedScrollingEnabled(enabled: Boolean) {
        mNestedScrollingChildHelper.isNestedScrollingEnabled = enabled
    }

    override fun isNestedScrollingEnabled(): Boolean {
        return mNestedScrollingChildHelper.isNestedScrollingEnabled
    }

    override fun startNestedScroll(axes: Int): Boolean {
        return mNestedScrollingChildHelper.startNestedScroll(axes)
    }

    override fun stopNestedScroll() {
        mNestedScrollingChildHelper.stopNestedScroll()
    }

    override fun hasNestedScrollingParent(): Boolean {
        return mNestedScrollingChildHelper.hasNestedScrollingParent()
    }

    override fun dispatchNestedScroll(dxConsumed: Int, dyConsumed: Int, dxUnconsumed: Int,
                                      dyUnconsumed: Int, offsetInWindow: IntArray?): Boolean {
        return mNestedScrollingChildHelper.dispatchNestedScroll(dxConsumed, dyConsumed,
                dxUnconsumed, dyUnconsumed, offsetInWindow)
    }

    override fun dispatchNestedPreScroll(dx: Int, dy: Int, consumed: IntArray?, offsetInWindow: IntArray?): Boolean {
        return mNestedScrollingChildHelper.dispatchNestedPreScroll(
                dx, dy, consumed, offsetInWindow)
    }

    override fun onNestedPreFling(target: View, velocityX: Float,
                                  velocityY: Float): Boolean {
        return dispatchNestedPreFling(velocityX, velocityY)
    }

    override fun onNestedFling(target: View, velocityX: Float, velocityY: Float,
                               consumed: Boolean): Boolean {
        return dispatchNestedFling(velocityX, velocityY, consumed)
    }

    override fun dispatchNestedFling(velocityX: Float, velocityY: Float, consumed: Boolean): Boolean {
        return mNestedScrollingChildHelper.dispatchNestedFling(velocityX, velocityY, consumed)
    }

    override fun dispatchNestedPreFling(velocityX: Float, velocityY: Float): Boolean {
        return mNestedScrollingChildHelper.dispatchNestedPreFling(velocityX, velocityY)
    }

    private fun isAnimationRunning(animation: Animation?): Boolean {
        return animation != null && animation.hasStarted() && !animation.hasEnded()
    }

    private fun moveSpinner(overScrollTop: Float) {
        mProgress.arrowEnabled = true
        val originalDragPercent = overScrollTop / mTotalDragDistance

        val dragPercent = Math.min(1f, Math.abs(originalDragPercent))
        val adjustedPercent = Math.max(dragPercent - .4, 0.0).toFloat() * 5 / 3
        val extraOS = Math.abs(overScrollTop) - mTotalDragDistance
        val slingshotDist = (if (mUsingCustomStart)
            progressViewEndOffset - progressViewStartOffset
        else
            progressViewEndOffset).toFloat()
        val tensionSlingshotPercent = Math.max(0f, Math.min(extraOS, slingshotDist * 2) / slingshotDist)
        val tensionPercent = (tensionSlingshotPercent / 4 - Math.pow(
                (tensionSlingshotPercent / 4).toDouble(), 2.0)).toFloat() * 2f
        val extraMove = slingshotDist * tensionPercent * 2f

        val targetY = progressViewStartOffset + (slingshotDist * dragPercent + extraMove).toInt()
        // where 1.0f is a full circle
        if (mCircleView.visibility != View.VISIBLE) {
            mCircleView.visibility = View.VISIBLE
        }
        if (!mScale) {
            mCircleView.scaleX = 1f
            mCircleView.scaleY = 1f
        }

        if (mScale) {
            setAnimationProgress(Math.min(1f, overScrollTop / mTotalDragDistance))
        }
        if (overScrollTop < mTotalDragDistance) {
            if (mProgress.alpha > STARTING_PROGRESS_ALPHA && !isAnimationRunning(mAlphaStartAnimation)) {
                // Animate the alpha
                startProgressAlphaStartAnimation()
            }
        } else {
            if (mProgress.alpha < MAX_ALPHA && !isAnimationRunning(mAlphaMaxAnimation)) {
                // Animate the alpha
                startProgressAlphaMaxAnimation()
            }
        }
        val strokeStart = adjustedPercent * .8f
        mProgress.setStartEndTrim(0f, Math.min(MAX_PROGRESS_ANGLE, strokeStart))
        mProgress.arrowScale = Math.min(1f, adjustedPercent)

        val rotation = (-0.25f + .4f * adjustedPercent + tensionPercent * 2) * .5f
        mProgress.progressRotation = rotation
        setTargetOffsetTopAndBottom(targetY - mCurrentTargetOffsetTop)
    }

    private fun finishSpinner(overScrollTop: Float) {
        if (overScrollTop > mTotalDragDistance) {
            setRefreshing(true, true /* notify */)
        } else {
            // cancel refresh
            mRefreshing = false
            mProgress.setStartEndTrim(0f, 0f)
            var listener: Animation.AnimationListener? = null
            if (!mScale) {
                listener = object : Animation.AnimationListener {

                    override fun onAnimationStart(animation: Animation) {}

                    override fun onAnimationEnd(animation: Animation) {
                        if (!mScale) {
                            startScaleDownAnimation(null)
                        }
                    }

                    override fun onAnimationRepeat(animation: Animation) {}

                }
            }
            animateOffsetToStartPosition(mCurrentTargetOffsetTop, listener)
            mProgress.arrowEnabled = false
        }
    }

    override fun onTouchEvent(ev: MotionEvent): Boolean {
        val action = ev.actionMasked
        var pointerIndex: Int

        if (mReturningToStart && action == MotionEvent.ACTION_DOWN) {
            mReturningToStart = false
        }

        if (!isEnabled || mReturningToStart || (canChildScrollUp() && canChildScrollDown())
                || mRefreshing || mNestedScrollInProgress || isLoadingMore) {
            // Fail fast if we're not in a state where a swipe is possible
            return false
        }

        when (action) {
            MotionEvent.ACTION_DOWN -> {
                mActivePointerId = ev.getPointerId(0)
                mIsBeingDragged = false
            }

            MotionEvent.ACTION_MOVE -> {
                pointerIndex = ev.findPointerIndex(mActivePointerId)
                if (pointerIndex < 0) {
                    Log.e(LOG_TAG, "Got ACTION_MOVE event but have an invalid active pointer id.")
                    return false
                }
                var offsetY = ev.getY(pointerIndex) - mInitialDownY
                if (!canChildScrollUp()) {
                    //若是顶部不能滑动，则yDiff是正值，直接与mTouchSlop做比较。
                }
                if (!canChildScrollDown()) {
                    //若是底部不能滑动，则yDiff是负值，取反后与mTouchSlop做比较。
                    offsetY = -offsetY
                }
                startDragging(offsetY)
                if (mIsBeingDragged) {
                    /**滑动的距离，向下滑动为正，向下滑动为负 */
                    val overScrollTop = (y - mInitialMotionY) * DRAG_RATE
                    if (overScrollTop > 0 && !canChildScrollUp()) {
                        //加载更多
                        moveSpinner(overScrollTop)
                    } else if (overScrollTop < 0 && !canChildScrollDown()) {
                        //当处于底部，且有滑动的趋势直接加载更多。
                        loadMore()
                    } else {
                        return false
                    }
                }
            }
            MotionEvent.ACTION_POINTER_DOWN -> {
                pointerIndex = ev.actionIndex
                if (pointerIndex < 0) {
                    Log.e(LOG_TAG, "Got ACTION_POINTER_DOWN event but have an invalid action index.")
                    return false
                }
                mActivePointerId = ev.getPointerId(pointerIndex)
            }

            MotionEvent.ACTION_POINTER_UP -> onSecondaryPointerUp(ev)

            MotionEvent.ACTION_UP -> {
                pointerIndex = ev.findPointerIndex(mActivePointerId)
                if (pointerIndex < 0) {
                    Log.e(LOG_TAG, "Got ACTION_UP event but don't have an active pointer id.")
                    return false
                }
                if (mIsBeingDragged) {
                    val y = ev.getY(pointerIndex)
                    val overScrollTop = (y - mInitialMotionY) * DRAG_RATE
                    mIsBeingDragged = false
                    finishSpinner(overScrollTop)
                }
                mActivePointerId = INVALID_POINTER
                return false
            }
            MotionEvent.ACTION_CANCEL -> return false
        }

        return true
    }

    private fun animateOffsetToCorrectPosition(from: Int, listener: Animation.AnimationListener?) {
        mFrom = from
        mAnimateToCorrectPosition.reset()
        mAnimateToCorrectPosition.duration = ANIMATE_TO_TRIGGER_DURATION.toLong()
        mAnimateToCorrectPosition.interpolator = mDecelerateInterpolator
        if (listener != null) {
            mCircleView.setAnimationListener(listener)
        }
        mCircleView.clearAnimation()
        mCircleView.startAnimation(mAnimateToCorrectPosition)
    }

    /**
     * 开始拖动
     */
    private fun startDragging(offsetY: Float) {
        if (offsetY > mTouchSlop && !mIsBeingDragged) {
            mInitialMotionY = mInitialDownY + mTouchSlop
            mIsBeingDragged = true
            mProgress.alpha = STARTING_PROGRESS_ALPHA
        }
    }

    /**
     * 上拉加载更多
     */
    private fun loadMore() {
        if (!isLoadingMore) {
            LogUtil.e("加载更多")
//            if (mLoadMoreListener != null) {
            animateOffsetFromToTarget(mCurrentTargetOffsetTop, mCurrentTargetOffsetTop - mInitialScrollUpY, null)
//                mLoadMoreListener.onLoad()
//                isLoadingMore = true
//            }
        }
    }

    /**
     * 从x位置移动到y位置，并伴随动画监听器
     */
    private fun animateOffsetFromToTarget(fromPosition: Int, targetPosition: Int, listener: Animation.AnimationListener?) {
        val animateFromToTarget = AnimateFromToTarget(fromPosition, targetPosition)
        animateFromToTarget.duration = 200
        animateFromToTarget.interpolator = mDecelerateInterpolator
        if (listener != null) {
            animateFromToTarget.setAnimationListener(listener)
        }
        mContentView?.clearAnimation()
        mContentView?.startAnimation(animateFromToTarget)
    }

    /**
     * 移动动画
     */
    private inner class AnimateFromToTarget(var mFromPosition: Int, var mTargetPosition: Int) : Animation() {
        override fun applyTransformation(interpolatedTime: Float, t: Transformation) {
            val targetTop: Int = mFromPosition + ((mTargetPosition - mFromPosition) * interpolatedTime).toInt()
            val offset = targetTop - mContentView!!.top
            setTargetOffsetTopAndBottom(offset)
        }
    }

    private fun animateOffsetToStartPosition(from: Int, listener: Animation.AnimationListener?) {
        if (mScale) {
            // Scale the item back down
            startScaleDownReturnToStartAnimation(from, listener)
        } else {
            mFrom = from
            mAnimateToStartPosition.reset()
            mAnimateToStartPosition.duration = ANIMATE_TO_START_DURATION.toLong()
            mAnimateToStartPosition.interpolator = mDecelerateInterpolator
            if (listener != null) {
                mCircleView.setAnimationListener(listener)
            }
            mCircleView.clearAnimation()
            mCircleView.startAnimation(mAnimateToStartPosition)
        }
    }

    private fun moveToStart(interpolatedTime: Float) {
        val targetTop = mFrom + ((progressViewStartOffset - mFrom) * interpolatedTime).toInt()
        val offset = targetTop - mCircleView.top
        setTargetOffsetTopAndBottom(offset)
    }

    private fun startScaleDownReturnToStartAnimation(from: Int, listener: Animation.AnimationListener?) {
        mFrom = from
        mStartingScale = mCircleView.scaleX
        mScaleDownToStartAnimation = object : Animation() {
            public override fun applyTransformation(interpolatedTime: Float, t: Transformation) {
                val targetScale = mStartingScale + -mStartingScale * interpolatedTime
                setAnimationProgress(targetScale)
                moveToStart(interpolatedTime)
            }
        }
        mScaleDownToStartAnimation?.duration = SCALE_DOWN_DURATION.toLong()
        if (listener != null) {
            mCircleView.setAnimationListener(listener)
        }
        mCircleView.clearAnimation()
        mCircleView.startAnimation(mScaleDownToStartAnimation)
    }

    internal fun setTargetOffsetTopAndBottom(offset: Int) {
        mCircleView.bringToFront()
        ViewCompat.offsetTopAndBottom(mCircleView, offset)
        mCurrentTargetOffsetTop = mCircleView.top
    }

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

    interface OnRefreshListener {
        fun onRefresh()
    }

    interface OnChildScrollUpCallback {
        fun canChildScrollUp(parent: SuperSwipeRefreshLayout, child: View?): Boolean
    }

}

