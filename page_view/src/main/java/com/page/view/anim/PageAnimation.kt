package com.page.view.anim

import android.graphics.Bitmap
import android.graphics.Canvas
import android.view.MotionEvent
import android.view.View
import android.view.animation.LinearInterpolator
import android.widget.Scroller

/**
 * 翻页动画抽象类
 */
abstract class PageAnimation(//屏幕的尺寸
    protected var mScreenWidth: Int,
    protected var mScreenHeight: Int, //屏幕的间距
    protected var mMarginWidth: Int,
    protected var mMarginHeight: Int, //正在使用的View
    protected var mView: View, //监听器
    protected var mListener: OnPageChangeListener
) {
    //滑动装置
    protected var mScroller: Scroller
    //移动方向
    open var direction = Direction.NONE

    var isRunning = false
    //视图的尺寸
    protected var mViewWidth: Int = 0
    protected var mViewHeight: Int = 0
    //起始点
    protected var mStartX: Float = 0.toFloat()
    protected var mStartY: Float = 0.toFloat()
    //触碰点
    protected var mTouchX: Float = 0.toFloat()
    protected var mTouchY: Float = 0.toFloat()
    //上一个触碰点
    protected var mLastX: Float = 0.toFloat()
    protected var mLastY: Float = 0.toFloat()

    /**
     * 获取背景板
     * @return
     */
    abstract val bgBitmap: Bitmap?

    /**
     * 获取内容显示版面
     */
    abstract var nextBitmap: Bitmap?

    constructor(w: Int, h: Int, view: View, listener: OnPageChangeListener)
            : this(w, h, 0, 0, view, listener) {
    }

    init {

        mViewWidth = mScreenWidth - mMarginWidth * 2
        mViewHeight = mScreenHeight - mMarginHeight * 2

        mScroller = Scroller(mView.context, LinearInterpolator())
    }

    open fun setStartPoint(x: Float, y: Float) {
        mStartX = x
        mStartY = y

        mLastX = mStartX
        mLastY = mStartY
    }

    open fun setTouchPoint(x: Float, y: Float) {
        mLastX = mTouchX
        mLastY = mTouchY

        mTouchX = x
        mTouchY = y
    }

    /**
     * 点击事件的处理
     * @param event
     */
    abstract fun onTouchEvent(event: MotionEvent): Boolean

    /**
     * 绘制图形
     * @param canvas
     */
    abstract fun draw(canvas: Canvas)

    /**
     * 开启翻页动画
     */
    abstract fun startAnim()

    /**
     * 滚动动画
     * 必须放在computeScroll()方法中执行
     */
    abstract fun scrollAnim()

    /**
     * 取消动画
     */
    abstract fun abortAnim()

    enum class Direction private constructor(val isHorizontal: Boolean) {
        NONE(true), NEXT(true), PRE(true), UP(false), DOWN(false)
    }

    interface OnPageChangeListener {
        fun hasPrev(): Boolean
        operator fun hasNext(): Boolean
        fun pageCancel()
    }

}
