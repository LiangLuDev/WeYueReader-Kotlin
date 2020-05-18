package com.page.view.anim

import android.graphics.Canvas
import android.graphics.Rect
import android.view.View

/**
 *
 */
class SlidePageAnim(w: Int, h: Int, view: View, listener: PageAnimation.OnPageChangeListener) :
    HorizonPageAnim(w, h, view, listener) {
    private val mSrcRect: Rect = Rect(0, 0, mViewWidth, mViewHeight)
    private val mDestRect: Rect = Rect(0, 0, mViewWidth, mViewHeight)
    private val mNextSrcRect: Rect = Rect(0, 0, mViewWidth, mViewHeight)
    private val mNextDestRect: Rect = Rect(0, 0, mViewWidth, mViewHeight)

    override fun drawStatic(canvas: Canvas) {
        if (isCancel) {
            canvas.drawBitmap(mCurBitmap!!, 0f, 0f, null)
        } else {
            canvas.drawBitmap(nextBitmap, 0f, 0f, null)
        }
    }

    override fun drawMove(canvas: Canvas) {
        var dis = 0
        when (direction) {
            PageAnimation.Direction.NEXT -> {
                //左半边的剩余区域
                dis = (mScreenWidth - mStartX + mTouchX).toInt()
                if (dis > mScreenWidth) {
                    dis = mScreenWidth
                }
                //计算bitmap截取的区域
                mSrcRect.left = mScreenWidth - dis
                //计算bitmap在canvas显示的区域
                mDestRect.right = dis
                //计算下一页截取的区域
                mNextSrcRect.right = mScreenWidth - dis
                //计算下一页在canvas显示的区域
                mNextDestRect.left = dis

                canvas.drawBitmap(nextBitmap!!, mNextSrcRect, mNextDestRect, null)
                canvas.drawBitmap(mCurBitmap!!, mSrcRect, mDestRect, null)
            }
            else -> {
                dis = (mTouchX - mStartX).toInt()
                if (dis < 0) {
                    dis = 0
                    mStartX = mTouchX
                }
                mSrcRect.left = mScreenWidth - dis
                mDestRect.right = dis

                //计算下一页截取的区域
                mNextSrcRect.right = mScreenWidth - dis
                //计算下一页在canvas显示的区域
                mNextDestRect.left = dis

                canvas.drawBitmap(mCurBitmap!!, mNextSrcRect, mNextDestRect, null)
                canvas.drawBitmap(nextBitmap!!, mSrcRect, mDestRect, null)
            }
        }
    }

    override fun startAnim() {
        val dx: Int
        when (direction) {
            PageAnimation.Direction.NEXT -> if (isCancel) {
                var dis = (mScreenWidth - mStartX + mTouchX).toInt()
                if (dis > mScreenWidth) {
                    dis = mScreenWidth
                }
                dx = mScreenWidth - dis
            } else {
                dx = (-(mTouchX + (mScreenWidth - mStartX))).toInt()
            }
            else -> if (isCancel) {
                dx = (-Math.abs(mTouchX - mStartX)).toInt()
            } else {
                dx = (mScreenWidth - (mTouchX - mStartX)).toInt()
            }
        }
        //滑动速度保持一致
        val duration = 400 * Math.abs(dx) / mScreenWidth
        mScroller.startScroll(mTouchX.toInt(), 0, dx, 0, duration)
    }
}
