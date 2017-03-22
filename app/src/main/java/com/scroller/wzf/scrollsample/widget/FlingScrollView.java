package com.scroller.wzf.scrollsample.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.VelocityTracker;
import android.view.ViewConfiguration;
import android.widget.LinearLayout;
import android.widget.Scroller;

/**
 * ================================================
 * 描    述：Scroller拖动View
 * 作    者：王智凡
 * 创建日期：2017/3/17
 * 版    本：1.0
 * 修订历史：
 * ================================================
 */
public class FlingScrollView extends LinearLayout {

    private int lastX, lastY;
    private Scroller scroller;
    // 计算滑动速度
    private VelocityTracker velocityTracker;
    private int mScaledMaximumFlingVelocity;
    private int mScaledMinimumFlingVelocity;

    private static final int max = 4300;

    public FlingScrollView(Context context) {
        super(context);
        init();
    }

    public FlingScrollView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public FlingScrollView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    public FlingScrollView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        init();
    }

    private void init() {
        scroller = new Scroller(getContext());
        ViewConfiguration configuration = ViewConfiguration.get(getContext());
        mScaledMaximumFlingVelocity = configuration.getScaledMaximumFlingVelocity();
        mScaledMinimumFlingVelocity = configuration.getScaledMinimumFlingVelocity();
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
                    scrollBy(lastX - x, 0);
                }
                break;
            case MotionEvent.ACTION_UP:
                if (event.getPointerId(index) == 0) { // 解决多点触控位置跳动的情况
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
    public void computeScroll() {
        if (scroller.computeScrollOffset()) {
            scrollTo(scroller.getCurrX(), scroller.getCurrY());
            postInvalidate();
        }
    }
}
