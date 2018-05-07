package com.zyelite.kghub.widget

import android.content.Context
import android.os.Build
import android.support.annotation.RequiresApi
import android.support.annotation.VisibleForTesting
import android.support.v4.view.*
import android.support.v4.widget.CircularProgressDrawable
import android.support.v4.widget.ListViewCompat
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.View
import android.view.ViewConfiguration
import android.view.ViewGroup
import android.view.animation.Animation
import android.view.animation.Animation.AnimationListener
import android.view.animation.DecelerateInterpolator
import android.view.animation.Transformation
import android.widget.AbsListView
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
    private var mSpinnerOffsetEnd: Int = 0

    // Max amount of circle that can be filled by progress during swipe gesture,
    // where 1.0 is a full circle
    private val MAX_PROGRESS_ANGLE = .8f

    @VisibleForTesting
    private val CIRCLE_DIAMETER = 40
    @VisibleForTesting
    private val CIRCLE_DIAMETER_LARGE = 56
    private val INVALID_POINTER = -1
    private var mTotalDragDistance = -1f
    // Whether this item is scaled up rather than clipped
    private var mScale: Boolean = false
    // Whether the client has set a custom starting position;
    private var mUsingCustomStart: Boolean = false
    private var mDecelerateInterpolator: DecelerateInterpolator? = null
    private val DECELERATE_INTERPOLATION_FACTOR = 2f
    private val DRAG_RATE = .5f
    //是否开始下拉
    private var mIsBeingDragged: Boolean = false
    // Target is returning to its start offset because it was cancelled or a refresh was triggered
    private var mReturningToStart: Boolean = false
    //触发移动事件的最短距离
    private var mTouchSlop: Int = 0

    private var mInitialMotionY: Float = 0F

    //上拉加载的view高度
    private var mInitialScrollUpY = 0
    //手指第一次按下的位置
    private var mInitialDownY: Float = 0F

    //是否刷新
    private var mRefreshing = false

    private var isLoadingMore = false

    private var mActivePointerId: Int = 0

    private val MAX_ALPHA = 255
    private val STARTING_PROGRESS_ALPHA = (.3f * MAX_ALPHA).toInt()
    private var mAlphaStartAnimation: Animation? = null
    private var mAlphaMaxAnimation: Animation? = null
    private val ALPHA_ANIMATION_DURATION = 300L
    private var mNotify: Boolean = false
    private var mScaleAnimation: Animation? = null
    private var mScaleDownAnimation: Animation? = null
    private var mScaleDownToStartAnimation: Animation? = null
    private var mFrom: Int = 0
    private val SCALE_DOWN_DURATION = 150L
    private val mMediumAnimationDuration: Int = 0
    private val ANIMATE_TO_TRIGGER_DURATION = 200L
    private val ANIMATE_TO_START_DURATION = 200L
    private var mStartingScale: Float = 0F
    private val LAYOUT_ATTRS = intArrayOf(android.R.attr.enabled)

    // If nested scrolling is enabled, the total amount that needed to be
    // consumed by this as the nested scrolling parent is used in place of the
    // overscroll determined by MOVE events in the onTouch handler
    private var mTotalUnconsumed: Float = 0F
    private var mNestedScrollingParentHelper: NestedScrollingParentHelper? = null
    private var mNestedScrollingChildHelper: NestedScrollingChildHelper? = null
    private val mParentScrollConsumed = IntArray(2)
    private val mParentOffsetInWindow = IntArray(2)
    private var mNestedScrollInProgress: Boolean = false
    private val DEFAULT_CIRCLE_TARGET = 64


    // Whether the client has set a custom starting position;
    private val mRefreshListener = object : Animation.AnimationListener {
        override fun onAnimationStart(animation: Animation) {}
        override fun onAnimationRepeat(animation: Animation) {}
        override fun onAnimationEnd(animation: Animation) {
            LogUtil.e("走了吗")
            if (mRefreshing) {
                // Make sure the progress view is fully visible
                mProgress?.alpha = MAX_ALPHA
                mProgress!!.start()
                if (mNotify) {
                    //刷新回调
                    LogUtil.e("走了吗")
                }
                mCurrentTargetOffsetTop = mCircleView!!.top
            } else {
                reset()
            }
        }
    }

    private fun reset() {
        mCircleView!!.clearAnimation()
        mProgress!!.stop()
        mCircleView!!.visibility = View.GONE
        setColorViewAlpha(MAX_ALPHA)
        // Return the circle to its start position
        if (mScale) {
            setAnimationProgress(0f /* animation complete and view is hidden */)
        } else {
            setTargetOffsetTopAndBottom(mOriginalOffsetTop - mCurrentTargetOffsetTop)
        }
        mCurrentTargetOffsetTop = mCircleView!!.top
    }


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
        mDecelerateInterpolator = DecelerateInterpolator(DECELERATE_INTERPOLATION_FACTOR)

        createProgressView()

        isChildrenDrawingOrderEnabled = true
        // the absolute offset has to take into account that the circle starts at an offset
        mSpinnerOffsetEnd = (DEFAULT_CIRCLE_TARGET * metrics.density).toInt()
        mTotalDragDistance = mSpinnerOffsetEnd.toFloat()

        mNestedScrollingParentHelper = NestedScrollingParentHelper(this)
        mNestedScrollingChildHelper = NestedScrollingChildHelper(this)
        isNestedScrollingEnabled = true

        moveToStart(1.0f)
        val a = context.obtainStyledAttributes(attributeSet, LAYOUT_ATTRS)
        isEnabled = a.getBoolean(0, true)
        a.recycle()
    }

    @RequiresApi(Build.VERSION_CODES.LOLLIPOP)
    constructor(context: Context, attributeSet: AttributeSet, defStyleAttr: Int, defStyleRes: Int) : super(context, attributeSet, defStyleAttr, defStyleRes)

    private fun createProgressView() {
        mCircleView = CircleImageView(context, CIRCLE_BG_LIGHT)
        mProgress = CircularProgressDrawable(context)
        mProgress?.setStyle(CircularProgressDrawable.DEFAULT)
        mCircleView?.setImageDrawable(mProgress)
        mCircleView?.visibility = View.GONE
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

    override fun onDetachedFromWindow() {
        super.onDetachedFromWindow()
        reset()
    }

    fun setProgressViewOffset(scale: Boolean, start: Int, end: Int) {
        mScale = scale
        mOriginalOffsetTop = start
        mSpinnerOffsetEnd = end
        mUsingCustomStart = true
        reset()
        mRefreshing = false
    }

    fun getProgressViewStartOffset(): Int {
        return mOriginalOffsetTop
    }

    fun getProgressViewEndOffset(): Int {
        return mSpinnerOffsetEnd
    }

    fun setProgressViewEndTarget(scale: Boolean, end: Int) {
        mSpinnerOffsetEnd = end
        mScale = scale
        mCircleView?.invalidate()
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

        if (mReturningToStart && action == MotionEvent.ACTION_DOWN) {
            mReturningToStart = false
        }

        val b = canChildScrollUp() && canChildScrollDown()
        if (!isEnabled || b || mReturningToStart
                || mRefreshing || mNestedScrollInProgress) {
            // Fail fast if we're not in a state where a swipe is possible
            LogUtil.e("onInterceptTouchEvent mRefreshing:$mRefreshing")
            LogUtil.e("onInterceptTouchEvent canChildScrollUpcan:$b")
            LogUtil.e("onInterceptTouchEvent mRefreshing:$mReturningToStart")
            LogUtil.e("onInterceptTouchEvent mRefreshing:$mNestedScrollInProgress")
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
                if (mActivePointerId == INVALID_POINTER) {
                    LogUtil.e("Got ACTION_MOVE event but don't have an active pointer id.")
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
            MotionEvent.ACTION_POINTER_UP -> {
                //有手指抬起
                onSecondaryPointerUp(ev)
            }
            MotionEvent.ACTION_UP, MotionEvent.ACTION_CANCEL -> {
                mIsBeingDragged = false
                mActivePointerId = INVALID_POINTER
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

    override fun onTouchEvent(ev: MotionEvent): Boolean {
        //获取手势动作
        val action = ev.actionMasked
        val pointerIndex: Int
//        if (!isEnabled || (canChildScrollUp() && canChildScrollDown())
//                || mRefreshing || isLoadingMore) {
//            return false
//        }


        if (mReturningToStart && action == MotionEvent.ACTION_DOWN) {
            mReturningToStart = false
        }

        val b = canChildScrollUp() && canChildScrollDown()
        if (!isEnabled || b || mReturningToStart
                || mRefreshing || mNestedScrollInProgress) {
            // Fail fast if we're not in a state where a swipe is possible
            LogUtil.e("mRefreshing:$mRefreshing")
            LogUtil.e("canChildScrollUpcan:$b")
            LogUtil.e("mRefreshing:$mReturningToStart")
            LogUtil.e("mRefreshing:$mNestedScrollInProgress")
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
                if (offsetY > mTouchSlop && !mIsBeingDragged) {
                    startDragging(offsetY)
                }
                if (mIsBeingDragged) {
                    /**滑动的距离，向下滑动为正，向下滑动为负 */
                    val overScrollTop = (ev.getY(pointerIndex) - mInitialMotionY) * 0.5f
                    if (overScrollTop > 0 && !canChildScrollUp()) {
                        //加载更多
                        moveSpinner(overScrollTop)
//                        LogUtil.e("下拉刷新")
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
                    return false
                }
                mActivePointerId = ev.getPointerId(pointerIndex)
            }

            MotionEvent.ACTION_POINTER_UP -> onSecondaryPointerUp(ev)

            MotionEvent.ACTION_UP -> {
                pointerIndex = ev.findPointerIndex(mActivePointerId)
                if (pointerIndex < 0) {
                    return false
                }
                if (mIsBeingDragged) {

                    val overScrollTop = (ev.getY(pointerIndex) - mInitialMotionY) * 0.5f
                    mIsBeingDragged = false
                    LogUtil.e("Super onTouchEvent:$overScrollTop")
                    finishSpinner(overScrollTop)
                }
                mActivePointerId = INVALID_POINTER
                return false
            }
            MotionEvent.ACTION_CANCEL -> return false
        }
        return true
    }

    /**
     * 开始拖动
     */
    private fun startDragging(offsetY: Float) {
        if (offsetY > mTouchSlop && !mIsBeingDragged) {
            mInitialMotionY = mInitialDownY + mTouchSlop
            mIsBeingDragged = true
            mProgress?.alpha = STARTING_PROGRESS_ALPHA
        }
    }


    /**
     *下拉刷新
     */
    private fun moveSpinner(overScrollTop: Float) {
        mProgress?.arrowEnabled = true
        val originalDragPercent = overScrollTop / mTotalDragDistance
        val dragPercent = Math.min(1f, Math.abs(originalDragPercent))
        val adjustedPercent = Math.max(dragPercent - .4, 0.0).toFloat() * 5 / 3
        val extraOS = Math.abs(overScrollTop) - mTotalDragDistance
        val slingshotDist = (if (mUsingCustomStart) mSpinnerOffsetEnd - mOriginalOffsetTop else
            mSpinnerOffsetEnd).toFloat()
        val tensionSlingshotPercent = Math.max(0f, Math.min(extraOS, slingshotDist * 2) / slingshotDist)
        val tensionPercent = (tensionSlingshotPercent / 4 - Math.pow(
                (tensionSlingshotPercent / 4).toDouble(), 2.0)).toFloat() * 2f
        val extraMove = slingshotDist * tensionPercent * 2f

        val targetY = mOriginalOffsetTop + (slingshotDist * dragPercent + extraMove).toInt()
        // where 1.0f is a full circle
        if (mCircleView?.visibility != View.VISIBLE) {
            mCircleView?.visibility = View.VISIBLE
        }
        if (!mScale) {
            mCircleView?.scaleX = 1f
            mCircleView?.scaleY = 1f
        }
        if (mScale) {
            setAnimationProgress(Math.min(1f, overScrollTop / mTotalDragDistance))
        }
        if (overScrollTop < mTotalDragDistance) {
            if (mProgress?.alpha!! > STARTING_PROGRESS_ALPHA && !isAnimationRunning(mAlphaStartAnimation)) {
                LogUtil.e("Super startProgressAlphaStartAnimation:${mProgress!!.alpha}")
                startProgressAlphaStartAnimation()
            }
        } else {
            if (mProgress!!.alpha < MAX_ALPHA && !isAnimationRunning(mAlphaMaxAnimation)) {
                LogUtil.e("Super startProgressAlphaMaxAnimation:${mProgress!!.alpha}")
                startProgressAlphaMaxAnimation()
            }
        }
        val strokeStart = adjustedPercent * .8f
        mProgress?.setStartEndTrim(0f, Math.min(MAX_PROGRESS_ANGLE, strokeStart))
        mProgress?.arrowScale = Math.min(1f, adjustedPercent)
        val rotation = (-0.25f + .4f * adjustedPercent + tensionPercent * 2) * .5f
        mProgress?.progressRotation = rotation
        setTargetOffsetTopAndBottom(targetY - mCurrentTargetOffsetTop)
    }

    private fun setColorViewAlpha(targetAlpha: Int) {
        mCircleView!!.background.alpha = targetAlpha
        mProgress!!.alpha = targetAlpha
    }

    /**
     * Notify the widget that refresh state has changed. Do not call this when
     * refresh is triggered by a swipe gesture.
     *
     * @param refreshing Whether or not the view should show refresh progress.
     */
    fun setRefreshing(refreshing: Boolean) {
        if (refreshing && mRefreshing != refreshing) {
            // scale and show
            mRefreshing = refreshing
            val endTarget: Int = if (!mUsingCustomStart) {
                mSpinnerOffsetEnd + mOriginalOffsetTop
            } else {
                mSpinnerOffsetEnd
            }
            setTargetOffsetTopAndBottom(endTarget - mCurrentTargetOffsetTop)
            mNotify = false
            startScaleUpAnimation(mRefreshListener)
        } else {
            setRefreshing(refreshing, false /* notify */)
        }
    }

    private fun setRefreshing(refreshing: Boolean, notify: Boolean) {
        if (mRefreshing != refreshing) {
            LogUtil.e("Super setRefreshing refreshing $refreshing")
            LogUtil.e("Super setRefreshing mRefreshing $refreshing")
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
        (mScaleDownAnimation as Animation).duration = SCALE_DOWN_DURATION
        mCircleView!!.setAnimationListener(listener)
        mCircleView!!.clearAnimation()
        mCircleView!!.startAnimation(mScaleDownAnimation)
    }

    private val mAnimateToCorrectPosition = object : Animation() {
        public override fun applyTransformation(interpolatedTime: Float, t: Transformation) {
            val targetTop: Int
            val endTarget = if (!mUsingCustomStart) {
                mSpinnerOffsetEnd - Math.abs(mOriginalOffsetTop)
            } else {
                mSpinnerOffsetEnd
            }
            targetTop = mFrom + ((endTarget - mFrom) * interpolatedTime).toInt()
            val offset = targetTop - mCircleView!!.top
            setTargetOffsetTopAndBottom(offset)
            mProgress!!.arrowScale = 1 - interpolatedTime
        }
    }

    private fun animateOffsetToCorrectPosition(from: Int, listener: AnimationListener?) {
        mFrom = from
        mAnimateToCorrectPosition.reset()
        mAnimateToCorrectPosition.duration = ANIMATE_TO_TRIGGER_DURATION
        mAnimateToCorrectPosition.interpolator = mDecelerateInterpolator
        if (listener != null) {
            LogUtil.e("Super listener 不为空")
            mCircleView!!.setAnimationListener(listener)
        }
        mCircleView!!.clearAnimation()
        mCircleView!!.startAnimation(mAnimateToCorrectPosition)
    }

    private fun startScaleUpAnimation(listener: AnimationListener) {
        mCircleView!!.visibility = View.VISIBLE
        mProgress!!.alpha = MAX_ALPHA
        mScaleAnimation = object : Animation() {
            public override fun applyTransformation(interpolatedTime: Float, t: Transformation) {
                setAnimationProgress(interpolatedTime)
            }
        }
        (mScaleAnimation as Animation).duration = mMediumAnimationDuration.toLong()
        mCircleView?.setAnimationListener(listener)
        mCircleView!!.clearAnimation()
        mCircleView!!.startAnimation(mScaleAnimation)
    }

    private fun startProgressAlphaMaxAnimation() {
        mAlphaMaxAnimation = startAlphaAnimation(mProgress?.alpha!!, MAX_ALPHA)
    }

    private fun startProgressAlphaStartAnimation() {
        mAlphaStartAnimation = startAlphaAnimation(mProgress!!.alpha, STARTING_PROGRESS_ALPHA)
    }

    private fun startAlphaAnimation(startingAlpha: Int, endingAlpha: Int): Animation {
        val alpha = object : Animation() {
            public override fun applyTransformation(interpolatedTime: Float, t: Transformation) {
                mProgress?.alpha = (startingAlpha + (endingAlpha - startingAlpha) * interpolatedTime).toInt()
            }
        }
        alpha.duration = ALPHA_ANIMATION_DURATION
        // Clear out the previous animation listeners.
        mCircleView?.setAnimationListener(null)
        mCircleView?.clearAnimation()
        mCircleView?.startAnimation(alpha)
        return alpha
    }

    private fun isAnimationRunning(animation: Animation?): Boolean {
        return animation != null && animation.hasStarted() && !animation.hasEnded()
    }

    /**
     * Pre API 11, this does an alpha animation.
     * @param progress
     */
    private fun setAnimationProgress(progress: Float) {
        mCircleView?.scaleX = progress
        mCircleView?.scaleY = progress
    }

    /**
     * 回弹效果
     */
    private fun finishSpinner(overScrollTop: Float) {
        LogUtil.e("Super onStopNestedScroll:$mTotalDragDistance")
        if (overScrollTop > mTotalDragDistance) {
            setRefreshing(true, true /* notify */)
        } else {
            LogUtil.e("finishSpinner   else")
            // cancel refresh
            mRefreshing = false
            mProgress?.setStartEndTrim(0f, 0f)
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
            mProgress?.arrowEnabled = false
        }
    }

    private fun animateOffsetToStartPosition(from: Int, listener: AnimationListener?) {
        LogUtil.e("animateOffsetToStartPosition ")
        if (mScale) {
            // Scale the item back down
            startScaleDownReturnToStartAnimation(from, listener)
        } else {
            mFrom = from
            mAnimateToStartPosition.reset()
            mAnimateToStartPosition.duration = ANIMATE_TO_START_DURATION
            mAnimateToStartPosition.interpolator = mDecelerateInterpolator
            if (listener != null) {
                mCircleView!!.setAnimationListener(listener)
            }
            mCircleView!!.clearAnimation()
            mCircleView!!.startAnimation(mAnimateToStartPosition)
        }
    }

    private val mAnimateToStartPosition = object : Animation() {
        public override fun applyTransformation(interpolatedTime: Float, t: Transformation) {
            moveToStart(interpolatedTime)
        }
    }

    private fun moveToStart(interpolatedTime: Float) {
        val targetTop = mFrom + ((mOriginalOffsetTop - mFrom) * interpolatedTime).toInt()
        val offset = targetTop - mCircleView!!.top
        setTargetOffsetTopAndBottom(offset)
    }

    private fun startScaleDownReturnToStartAnimation(from: Int, listener: Animation.AnimationListener?) {
        mFrom = from
        mStartingScale = mCircleView!!.scaleX
        mScaleDownToStartAnimation = object : Animation() {
            public override fun applyTransformation(interpolatedTime: Float, t: Transformation) {
                val targetScale = mStartingScale + -mStartingScale * interpolatedTime
                setAnimationProgress(targetScale)
                moveToStart(interpolatedTime)
            }
        }
        (mScaleDownToStartAnimation as Animation).duration = SCALE_DOWN_DURATION
        if (listener != null) {
            mCircleView!!.setAnimationListener(listener)
        }
        LogUtil.e("startScaleDownReturnToStartAnimation ")
        mCircleView!!.clearAnimation()
        mCircleView!!.startAnimation(mScaleDownToStartAnimation)
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
    private fun animateOffsetFromToTarget(fromPosition: Int, targetPosition: Int, listener: AnimationListener?) {
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


    override fun onStartNestedScroll(child: View, target: View, nestedScrollAxes: Int): Boolean {
        return (isEnabled && !mReturningToStart && !mRefreshing
                && nestedScrollAxes and ViewCompat.SCROLL_AXIS_VERTICAL != 0)
    }

    override fun onNestedScrollAccepted(child: View, target: View, axes: Int) {
        // Reset the counter of how much leftover scroll needs to be consumed.
        mNestedScrollingParentHelper?.onNestedScrollAccepted(child, target, axes)
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
            mCircleView?.visibility = View.GONE
        }

        // Now let our nested parent consume the leftovers
        val parentConsumed = mParentScrollConsumed
        if (dispatchNestedPreScroll(dx - consumed[0], dy - consumed[1], parentConsumed, null)) {
            consumed[0] += parentConsumed[0]
            consumed[1] += parentConsumed[1]
        }
    }

    override fun getNestedScrollAxes(): Int {
        return mNestedScrollingParentHelper!!.nestedScrollAxes
    }

    override fun onStopNestedScroll(target: View) {
        mNestedScrollingParentHelper?.onStopNestedScroll(target)
        mNestedScrollInProgress = false
        // Finish the spinner for nested scrolling if we ever consumed any
        // unconsumed nested scroll
        if (mTotalUnconsumed > 0) {
            LogUtil.e("Super onStopNestedScroll:$mTotalUnconsumed")
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
        LogUtil.e("onNestedScroll：$dy")
        LogUtil.e("onNestedScroll：$dyUnconsumed")
        if (dy < 0 && !canChildScrollUp()) {
            mTotalUnconsumed += Math.abs(dy).toFloat()
            moveSpinner(mTotalUnconsumed)
        }

        if (dyUnconsumed > 0 && !canChildScrollDown()) {
            loadMore()
        }
    }

    // NestedScrollingChild

    override fun setNestedScrollingEnabled(enabled: Boolean) {
        mNestedScrollingChildHelper?.isNestedScrollingEnabled = enabled
    }

    override fun isNestedScrollingEnabled(): Boolean {
        return mNestedScrollingChildHelper!!.isNestedScrollingEnabled
    }

    override fun startNestedScroll(axes: Int): Boolean {
        return mNestedScrollingChildHelper!!.startNestedScroll(axes)
    }

    override fun stopNestedScroll() {
        mNestedScrollingChildHelper!!.stopNestedScroll()
    }

    override fun hasNestedScrollingParent(): Boolean {
        return mNestedScrollingChildHelper!!.hasNestedScrollingParent()
    }

    override fun dispatchNestedScroll(dxConsumed: Int, dyConsumed: Int, dxUnconsumed: Int,
                                      dyUnconsumed: Int, offsetInWindow: IntArray?): Boolean {
        return mNestedScrollingChildHelper!!.dispatchNestedScroll(dxConsumed, dyConsumed,
                dxUnconsumed, dyUnconsumed, offsetInWindow)
    }

    override fun dispatchNestedPreScroll(dx: Int, dy: Int, consumed: IntArray?, offsetInWindow: IntArray?): Boolean {
        return mNestedScrollingChildHelper!!.dispatchNestedPreScroll(
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
        return mNestedScrollingChildHelper!!.dispatchNestedFling(velocityX, velocityY, consumed)
    }

    override fun dispatchNestedPreFling(velocityX: Float, velocityY: Float): Boolean {
        return mNestedScrollingChildHelper!!.dispatchNestedPreFling(velocityX, velocityY)
    }


}