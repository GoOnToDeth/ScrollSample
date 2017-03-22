package com.scroller.wzf.scrollsample.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
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
public class DragScrollView extends LinearLayout {

    private int lastX, lastY;
    private Scroller scroller;

    public DragScrollView(Context context) {
        super(context);
        init();
    }

    public DragScrollView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public DragScrollView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    public DragScrollView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        init();
    }

    private void init() {
        scroller = new Scroller(getContext());
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        int x = (int) event.getX();
        int y = (int) event.getY();
        int index = event.getActionIndex();
        switch (event.getActionMasked()) {
            case MotionEvent.ACTION_DOWN:
                break;
            case MotionEvent.ACTION_MOVE:
                if (event.getPointerId(index) == 0) { // 解决多点触控位置跳动的情况
                    smoothScrollBy(lastX - x, lastY - y);
                }
                break;
            case MotionEvent.ACTION_UP:
                break;
        }
        this.lastX = x;
        this.lastY = y;
        return true;
    }

    private void smoothScrollTo(int fx, int fy) {
        int dx = fx - scroller.getFinalX();
        int dy = fy - scroller.getFinalY();
        smoothScrollBy(dx, dy);
    }

    private void smoothScrollBy(int dx, int dy) {
        scroller.startScroll(scroller.getFinalX(), scroller.getFinalY(), dx, dy);
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
