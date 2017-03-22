package com.scroller.wzf.scrollsample.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.VelocityTracker;
import android.view.ViewConfiguration;
import android.widget.LinearLayout;
import android.widget.OverScroller;

/**
 * ================================================
 * 描    述：Scroller拖动View
 * 作    者：王智凡
 * 创建日期：2017/3/17
 * 版    本：1.0
 * 修订历史：
 * ================================================
 */
public class OverFlingScrollView extends LinearLayout {

    private int lastX, lastY;
    private OverScroller scroller;
    // 计算滑动速度
    private VelocityTracker velocityTracker;
    private int mScaledMaximumFlingVelocity;
    private int mScaledMinimumFlingVelocity;

    private static final int max = 4300;
    private int mOverScrollDistance;
    private int mOverFlingDistance;

    public OverFlingScrollView(Context context) {
        super(context);
        init();
    }

    public OverFlingScrollView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public OverFlingScrollView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    public OverFlingScrollView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        init();
    }

    private void init() {
        scroller = new OverScroller(getContext());
        ViewConfiguration configuration = ViewConfiguration.get(getContext());
        mScaledMaximumFlingVelocity = configuration.getScaledMaximumFlingVelocity();
        mScaledMinimumFlingVelocity = configuration.getScaledMinimumFlingVelocity();
        mOverScrollDistance = configuration.getScaledOverscrollDistance();
        mOverFlingDistance = configuration.getScaledOverflingDistance();
        //一般来说mOverScrollDistance为0，OverFlingDistance不一致，这里为了整强显示效果
        mOverFlingDistance = 50;

        setOverScrollMode(OVER_SCROLL_ALWAYS);
        // 这里还是需要的。overScrollBy中会使用到
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        int x = (int) event.getX();
        int y = (int) event.getY();
        int index = event.getActionIndex();
        if (velocityTracker == null)
            velocityTracker = VelocityTracker.obtain();
        velocityTracker.addMovement(event);
        switch (event.getActionMasked()) {
            case MotionEvent.ACTION_DOWN:
                if (!scroller.isFinished()) {
                    scroller.abortAnimation();
                }
                break;
            case MotionEvent.ACTION_MOVE:
                if (event.getPointerId(index) == 0) { // 解决多点触控位置跳动的情况
                    overScrollBy(lastX - x, 0, getScrollX(), 0, getScrollRange(), 0, mOverScrollDistance, 0, true);
//                    scrollBy(lastX - x, 0);
                }
                break;
            case MotionEvent.ACTION_UP:
                // 解决多点触控位置跳动的情况
                if (event.getPointerId(index) == 0) {
                    //求伪瞬时速度
                    velocityTracker.computeCurrentVelocity(1000, mScaledMaximumFlingVelocity);
                    int velocityX = (int) velocityTracker.getXVelocity();
                    if (velocityX > mScaledMinimumFlingVelocity || velocityX < -mScaledMinimumFlingVelocity) {
                        // 由于坐标轴正方向问题，要加负号。
                        doFling(-velocityX);
                    }
                }
                break;
        }
        this.lastX = x;
        this.lastY = y;
        return true;
    }

    /**
     * startX 滚动起始点X坐标
     * 　　startY 滚动起始点Y坐标
     * 　　velocityX 当滑动屏幕时X方向初速度，以每秒像素数计算
     * 　　velocityY 当滑动屏幕时Y方向初速度，以每秒像素数计算
     * 　　minX X方向的最小值，scroller不会滚过此点。
     * 　　maxX X方向的最大值，scroller不会滚过此点。
     * 　　minY Y方向的最小值，scroller不会滚过此点。
     * 　　maxY Y方向的最大值，scroller不会滚过此点。
     *
     * @param speed
     */
    private void doFling(int speed) {
        scroller.fling(getScrollX(), getScrollY(), speed, 0, 0, max, 0, 0);
        invalidate();
    }

    @Override
    protected void onOverScrolled(int scrollX, int scrollY, boolean clampedX, boolean clampedY) {
        if (!scroller.isFinished()) {
            int oldX = getScrollX();
            int oldY = getScrollY();
            scrollTo(scrollX, scrollY);
            onScrollChanged(scrollX, scrollY, oldX, oldY);
            if (clampedY) {
                scroller.springBack(getScrollX(), getScrollY(), 0, getScrollRange(), 0, 0);
            }
        } else {
            // TouchEvent中的overScroll调用
            super.scrollTo(scrollX, scrollY);
        }
    }

    @Override
    public void computeScroll() {
        if (scroller.computeScrollOffset()) {
//            scrollTo(scroller.getCurrX(), scroller.getCurrY());
//            postInvalidate();

            int oldX = getScrollX();
            int oldY = getScrollY();
            int x = scroller.getCurrX();
            int y = scroller.getCurrY();

            int range = getScrollRange();
            if (oldX != x || oldY != y) {
                Log.e("TEST", "computeScroll value is" + (y - oldY) + "oldY" + oldY);
                overScrollBy(x - oldX, y - oldY, oldX, oldY, range, 0, mOverFlingDistance, 0, false);
            }
        }
    }

    private int getScrollRange() {
        int scrollRange = 0;
        if (getChildCount() > 0) {
            int totalWidth = 0;
            for (int i = 0; i < getChildCount(); i++) {
                totalWidth += getChildAt(i).getWidth();
                //先假设没有margin的情况
            }
            scrollRange = Math.max(0, totalWidth - getWidth());
        }
        return scrollRange;
    }
}
