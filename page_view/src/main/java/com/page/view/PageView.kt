package com.page.view

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.RectF
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.View
import android.view.ViewConfiguration
import com.page.view.anim.*
import com.page.view.utils.ScreenUtils

/**
 * 绘制页面显示内容的类
 */
class PageView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : View(context, attrs, defStyleAttr) {

    private var mViewWidth = 0 // 当前View的宽
    private var mViewHeight = 0 // 当前View的高

    private var mStartX = 0
    private var mStartY = 0
    private var isMove = false
    //初始化参数
    private var mBgColor = -0x313d64
    private var mPageMode = PAGE_MODE_COVER

    //是否允许点击
    private var canTouch = true
    //判断是否初始化完成
    var isPrepare = false
        private set
    //唤醒菜单的区域
    private var mCenterRect: RectF? = null

    //动画类
    private var mPageAnim: PageAnimation? = null
    //动画监听类
    private val mPageAnimListener = object : PageAnimation.OnPageChangeListener {
        override fun hasPrev(): Boolean {
            return this@PageView.hasPrev()
        }

        override operator fun hasNext(): Boolean {
            return this@PageView.hasNext()
        }

        override fun pageCancel() {
            mTouchListener!!.cancel()
            mPageLoader!!.pageCancel()
        }
    }

    //点击监听
    private var mTouchListener: TouchListener? = null
    //内容加载器
    private var mPageLoader: PageLoader? = null

    private val nextPage: Bitmap?
        get() = mPageAnim?.nextBitmap
    val bgBitmap: Bitmap?
        get() = mPageAnim?.bgBitmap

    val isRunning: Boolean
        get() = mPageAnim!!.isRunning

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)
        mViewWidth = w
        mViewHeight = h
        //重置图片的大小,由于w,h可能比原始的Bitmap更大，所以如果使用Bitmap.setWidth/Height()是会报错的。
        //所以最终还是创建Bitmap的方式。这种方式比较消耗性能，暂时没有找到更好的方法。
        setPageMode(mPageMode)
        //重置页面加载器的页面
        mPageLoader!!.setDisplaySize(w, h)
        //初始化完成
        isPrepare = true
    }

    //设置翻页的模式
    fun setPageMode(pageMode: Int) {
        mPageMode = pageMode
        //视图未初始化的时候，禁止调用
        if (mViewWidth == 0 || mViewHeight == 0) {
            return
        }

        when (pageMode) {
            PAGE_MODE_SIMULATION -> mPageAnim = SimulationPageAnim(mViewWidth, mViewHeight, this, mPageAnimListener)
            PAGE_MODE_COVER -> mPageAnim = CoverPageAnim(mViewWidth, mViewHeight, this, mPageAnimListener)
            PAGE_MODE_SLIDE -> mPageAnim = SlidePageAnim(mViewWidth, mViewHeight, this, mPageAnimListener)
            PAGE_MODE_NONE -> mPageAnim = NonePageAnim(mViewWidth, mViewHeight, this, mPageAnimListener)
            PAGE_MODE_SCROLL -> mPageAnim = ScrollPageAnim(
                mViewWidth, mViewHeight, 0,
                ScreenUtils.dpToPx(PageLoader.DEFAULT_MARGIN_HEIGHT), this, mPageAnimListener
            )
            else -> mPageAnim = SimulationPageAnim(mViewWidth, mViewHeight, this, mPageAnimListener)
        }
    }


    fun autoPrevPage(): Boolean {
        //滚动暂时不支持自动翻页
        return if (mPageAnim is ScrollPageAnim) {
            false
        } else {
            startPageAnim(PageAnimation.Direction.PRE)
            true
        }
    }

    fun autoNextPage(): Boolean {
        return if (mPageAnim is ScrollPageAnim) {
            false
        } else {
            startPageAnim(PageAnimation.Direction.NEXT)
            true
        }
    }

    private fun startPageAnim(direction: PageAnimation.Direction) {
        if (mTouchListener == null) {
            return
        }
        //是否正在执行动画
        abortAnimation()
        if (direction === PageAnimation.Direction.NEXT) {
            val x = mViewWidth
            val y = mViewHeight
            //设置点击点
            mPageAnim!!.setTouchPoint(x.toFloat(), y.toFloat())
            //初始化动画
            mPageAnim!!.setStartPoint(x.toFloat(), y.toFloat())
            //设置方向
            val hasNext = hasNext()

            mPageAnim!!.direction = direction
            if (!hasNext) {
                return
            }
        } else {
            val x = 0
            val y = mViewHeight
            //初始化动画
            mPageAnim!!.setStartPoint(x.toFloat(), y.toFloat())
            //设置点击点
            mPageAnim!!.setTouchPoint(x.toFloat(), y.toFloat())
            mPageAnim!!.direction = direction
            //设置方向方向
            val hashPrev = hasPrev()
            if (!hashPrev) {
                return
            }
        }
        mPageAnim!!.startAnim()
        this.postInvalidate()
    }

    fun setBgColor(color: Int) {
        mBgColor = color
    }

    fun canTouchable(touchable: Boolean) {
        canTouch = touchable
    }

    override fun onDraw(canvas: Canvas) {
        //绘制背景
        canvas.drawColor(mBgColor)
        //绘制动画
        mPageAnim!!.draw(canvas)
    }

    @SuppressLint("ClickableViewAccessibility")
    override fun onTouchEvent(event: MotionEvent): Boolean {
        super.onTouchEvent(event)

        if (!canTouch && event.action != MotionEvent.ACTION_DOWN) {
            return true
        }

        val x = event.x.toInt()
        val y = event.y.toInt()
        when (event.action) {
            MotionEvent.ACTION_DOWN -> {
                mStartX = x
                mStartY = y
                isMove = false
                canTouch = mTouchListener!!.onTouch()
                mPageAnim!!.onTouchEvent(event)
            }
            MotionEvent.ACTION_MOVE -> {
                //判断是否大于最小滑动值。
                val slop = ViewConfiguration.get(context).scaledTouchSlop
                if (!isMove) {
                    isMove = Math.abs(mStartX - event.x) > slop || Math.abs(mStartY - event.y) > slop
                }

                //如果滑动了，则进行翻页。
                if (isMove) {
                    mPageAnim!!.onTouchEvent(event)
                }
            }
            MotionEvent.ACTION_UP -> {

                if (!isMove) {
                    //设置中间区域范围
                    if (mCenterRect == null) {
                        mCenterRect = RectF(
                            (mViewWidth / 5).toFloat(), (mViewHeight / 3).toFloat(),
                            (mViewWidth * 4 / 5).toFloat(), (mViewHeight * 2 / 3).toFloat()
                        )
                    }

                    //是否点击了中间
                    if (mCenterRect!!.contains(x.toFloat(), y.toFloat())) {
                        if (mTouchListener != null) {
                            mTouchListener!!.center()
                        }
                        return true
                    }
                }
                mPageAnim!!.onTouchEvent(event)
            }
            else -> {
            }
        }
        return true
    }

    //判断是否下一页存在
    private operator fun hasNext(): Boolean {
        var hasNext = false
        if (mTouchListener != null) {
            hasNext = mTouchListener!!.nextPage()
            //加载下一页
            if (hasNext) {
                hasNext = mPageLoader!!.next()
            }
        }
        return hasNext
    }

    //判断是否存在上一页
    private fun hasPrev(): Boolean {
        var hasPrev = false
        if (mTouchListener != null) {
            hasPrev = mTouchListener!!.prePage()
            //加载下一页
            if (hasPrev) {
                hasPrev = mPageLoader!!.prev()
            }
        }
        return hasPrev
    }

    override fun computeScroll() {
        //进行滑动
        mPageAnim!!.scrollAnim()
        super.computeScroll()
    }

    //如果滑动状态没有停止就取消状态，重新设置Anim的触碰点
    fun abortAnimation() {
        mPageAnim!!.abortAnim()
    }

    fun setTouchListener(mTouchListener: TouchListener) {
        this.mTouchListener = mTouchListener
    }

    fun drawNextPage() {
        if (mPageAnim is HorizonPageAnim) {
            (mPageAnim as HorizonPageAnim).changePage()
        }
        mPageLoader!!.onDraw(nextPage!!, false)
    }

    /**
     * 刷新当前页(主要是为了ScrollAnimation)
     */
    fun refreshPage() {
        if (mPageAnim is ScrollPageAnim) {
            (mPageAnim as ScrollPageAnim).refreshBitmap()
        }
        drawCurPage(false)
    }

    //refreshPage和drawCurPage容易造成歧义,后面需要修改

    /**
     * 绘制当前页。
     *
     * @param isUpdate
     */
    fun drawCurPage(isUpdate: Boolean) {
        mPageLoader!!.onDraw(nextPage!!, isUpdate)
    }

    //获取PageLoader
    fun getPageLoader(isLocal: Boolean): PageLoader {
        if (mPageLoader == null) {
            mPageLoader = if (isLocal) {
                LocalPageLoader(this)
            } else {
                NetPageLoader(this)
            }
        }
        return mPageLoader!!
    }

    interface TouchListener {
        fun center()

        fun onTouch(): Boolean

        fun prePage(): Boolean

        fun nextPage(): Boolean

        fun cancel()
    }

    companion object {

        const val PAGE_MODE_SIMULATION = 0
        const val PAGE_MODE_COVER = 1
        const val PAGE_MODE_SLIDE = 2
        const val PAGE_MODE_NONE = 3
        //滚动效果
        const val PAGE_MODE_SCROLL = 4

        private const val TAG = "PageView"
    }
}
