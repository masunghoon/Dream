/*
 * Copyright (c) 2012 Wireless Designs, LLC
 *
 * Permission is hereby granted, free of charge, to any person obtaining
 * a copy of this software and associated documentation files (the
 * "Software"), to deal in the Software without restriction, including
 * without limitation the rights to use, copy, modify, merge, publish,
 * distribute, sublicense, and/or sell copies of the Software, and to
 * permit persons to whom the Software is furnished to do so, subject to
 * the following conditions:
 *
 * The above copyright notice and this permission notice shall be
 * included in all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND,
 * EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF
 * MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND
 * NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE
 * LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION
 * OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION
 * WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */

package com.vivavu.dream.view;

import android.annotation.TargetApi;
import android.content.Context;
import android.graphics.Point;
import android.os.Build;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.widget.FrameLayout;

import com.vivavu.dream.util.AndroidUtils;

/**
 * PagerContainer: A layout that displays a ViewPager with its children that are outside
 * the typical pager bounds.
 */
public class PagerContainer extends FrameLayout implements ViewPager.OnPageChangeListener{
    private Context mContext;
    private ViewPager mPager;
    boolean mNeedsRedraw = false;

    public PagerContainer(Context context) {
        super(context);
        mContext = context;
        init();
    }

    public PagerContainer(Context context, AttributeSet attrs) {
        super(context, attrs);
        mContext = context;
        init();
    }

    public PagerContainer(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        mContext = context;
        init();
    }

    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    private void init() {
        //Disable clipping of children so non-selected pages are visible
        setClipChildren(false);

        //Child clipping doesn't work with hardware acceleration in Android 3.x/4.x
        //You need to set this value here if using hardware acceleration in an
        // application targeted at these releases.
        if(Build.VERSION.SDK_INT >= 11 && Build.VERSION.SDK_INT < 19){
            setLayerType(View.LAYER_TYPE_SOFTWARE, null);
        }
    }

    @Override
    protected void onFinishInflate() {
        try {

            mPager = new ViewPager(mContext);
            int width = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 120, getResources().getDisplayMetrics());
            int height = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 160, getResources().getDisplayMetrics());
            FrameLayout.LayoutParams layoutParams = new FrameLayout.LayoutParams(width, height, Gravity.CENTER_HORIZONTAL);
            mPager.setLayoutParams(layoutParams);
            mPager.setId(AndroidUtils.generateViewId());
            this.addView(mPager, 0);

            mPager.setOnPageChangeListener(this);
        } catch (Exception e) {
            throw new IllegalStateException("The root child of PagerContainer must be a ViewPager");
        }
    }

    public ViewPager getViewPager() {
        return mPager;
    }

    private Point mCenter = new Point();
    private Point mInitialTouch = new Point();

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        mCenter.x = w / 2;
        mCenter.y = h / 2;
    }

    @Override
    public boolean onTouchEvent(MotionEvent ev) {
        //We capture any touches not already handled by the ViewPager
        // to implement scrolling from a touch outside the pager bounds.
        switch (ev.getAction()) {
            case MotionEvent.ACTION_DOWN:
                mInitialTouch.x = (int)ev.getX();
                mInitialTouch.y = (int)ev.getY();
            default:
                ev.offsetLocation(mCenter.x - mInitialTouch.x, mCenter.y - mInitialTouch.y);
                break;
        }

        return mPager.dispatchTouchEvent(ev);
    }


    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
        //Force the container to redraw on scrolling.
        //Without this the outer pages render initially and then stay static
        if (mNeedsRedraw){
            invalidate();
        }
    }

    @Override
    public void onPageSelected(int position) {

    }

    @Override
    public void onPageScrollStateChanged(int state) {
        mNeedsRedraw = (state != ViewPager.SCROLL_STATE_IDLE);
    }
}
