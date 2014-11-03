package com.tonyjs.infinityviewgroup.lib;

import android.content.Context;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.util.Log;
import android.util.SparseArray;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Scroller;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/**
 * Created by im026 on 14. 11. 3..
 */
public class InfinityViewGroup extends ViewGroup {
    public InfinityViewGroup(Context context) {
        super(context);
        init();
    }

    public InfinityViewGroup(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public InfinityViewGroup(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private float mDpi;
    private Scroller mScroller;
    private void init() {
//        setOrientation(HORIZONTAL);
        DisplayMetrics metrics = getContext().getResources().getDisplayMetrics();
        mDpi = metrics.density;
        mScroller = new Scroller(getContext());
    }

    int mScrollX;
    float mFirstX = 0;
    float mFirstY = 0;
    float mLastX = 0;
    boolean mOccurredScroll = false;
    boolean toLeft = true;
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        int action = event.getAction();
        switch (action) {
            case MotionEvent.ACTION_DOWN:
                mFirstX = event.getX();
                mFirstY = event.getY();
                mLastX = event.getX();
//                mScrollX = event.getX();
                break;
            case MotionEvent.ACTION_MOVE:
//                Log.e("jsp", "width = " + mHeight + "\nheight = " + mHeight);
                float lastX = event.getX();
//                Log.e("jsp", "lastX = " + lastX);
                float lastY = event.getY();

                int distance = (int) (lastX - mLastX);

                if (Math.abs(distance) > 5 * mDpi) {
                    int max = mViewArray.size();
                    if (distance >= 0) {
                        toLeft = true;
                        for (int i = 0; i < max; i++) {
                            ViewSpec viewSpec = mViewArray.get(i);
                            View view = viewSpec.getView();
                            int right = viewSpec.getRight() + distance;
                            if (right >= mTotalWidth) {
                                right = 0;
                            }
                            viewSpec.setRight(right);
                            int left = viewSpec.getRight() - mWidth;
                            viewSpec.setLeft(left);
                            view.layout(
                                    viewSpec.getLeft(), viewSpec.getTop(), viewSpec.getRight(), viewSpec.getBottom());
                            view.setTag(viewSpec);
//                            Log.e("jsp", "left = " + viewSpec.getLeft()
//                                            + " right = " + viewSpec.getRight());
                        }
                    } else {
                        toLeft = false;
                        for (int i = 0; i < max; i++) {
                            ViewSpec viewSpec = mViewArray.get(i);
                            View view = viewSpec.getView();
                            int left = viewSpec.getLeft() + distance;
                            if (left <= 0) {
                                left = mTotalWidth;
                            }
                            viewSpec.setLeft(left);
                            int right = viewSpec.getLeft() + mWidth;
                            viewSpec.setRight(right);
                            view.layout(
                                    viewSpec.getLeft(), viewSpec.getTop(), viewSpec.getRight(), viewSpec.getBottom());
                            view.setTag(viewSpec);
//                            Log.e("jsp", "left = " + viewSpec.getLeft()
//                                    + " right = " + viewSpec.getRight());
                        }
                    }
                    mOccurredScroll = true;
                }
                mLastX = lastX;
                break;
            case MotionEvent.ACTION_UP:
            case MotionEvent.ACTION_CANCEL:
                if (mOccurredScroll) {
                    int max = mViewArray.size();
                    for (int i = 0; i < max; i++) {
                        ViewSpec viewSpec = mViewArray.get(i);
                        int left = viewSpec.getLeft();
                        int temp = -1;
                        int tagetPoint = 0;
                        for (int j = 0; j < mDistanceArr.length; j++) {
                            int point = mDistanceArr[j];
                            if (temp == -1) {
                                temp = Math.abs(point - left);
                                tagetPoint = point;
                            } else {
                                int a = Math.abs(point - left);
                                if (a <= temp) {
                                    temp = a;
                                    tagetPoint = point;
                                }
                            }
                        }
                        viewSpec.setLeft(tagetPoint);
                        viewSpec.setRight(viewSpec.getLeft() + mWidth);
                        View view = viewSpec.getView();
                        view.layout(viewSpec.getLeft(), viewSpec.getTop(), viewSpec.getRight(), viewSpec.getBottom());
                    }
//                    scrollToPosition();
                }
                mOccurredScroll = false;
                break;
        }

        return true;
    }

//    int mLastScrollX;
//    private void scrollToPosition() {
//        int left = 0;
//        int position = 0;
//        boolean toLeft = false;
//        int max = getChildCount();
//        for (int i = 0; i < max; i++) {
//            left = mWidth * i;
//            int right = left + mWidth;
//            if (mScrollX >= left && mScrollX <= right) {
//                int differenceLeft = Math.abs(mScrollX - left);
//                int differenceRight = Math.abs(mScrollX - right);
//
//                if (differenceRight >= differenceLeft) {
//                    position = i;
//                    scrollTo(left, 0);
//                    mLastScrollX = mScrollX = left;
//                    toLeft = true;
//                } else {
//                    position = i + 1;
//                    scrollTo(right, 0);
//                    mLastScrollX = mScrollX = right;
//                    toLeft = false;
//                }
//                break;
//            }
//        }
//
//        int adapterCount = mAdapter.getCount() + 2;
////        Log.e("jsp", "position = " + position + " max = " + max);
//        if (position < max / 2) {
//            if (toLeft) {
//                View removeView = getChildAt(max - 1);
//                int itemPosition = (Integer) removeView.getTag();
//                removeView(removeView);
//
//                View v = (View) mAdapter.instantiateItem(this, itemPosition, true);
//                v.setTag(itemPosition);
//                LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(mWidth, mHeight);
//                v.setLayoutParams(params);
//            } else {
//                View removeView = getChildAt(0);
//                int itemPosition = (Integer) removeView.getTag();
//                removeView(removeView);
//
//                View v = (View) mAdapter.instantiateItem(this, itemPosition);
//                v.setTag(itemPosition);
//                LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(mWidth, mHeight);
//                v.setLayoutParams(params);
//            }
//        }
//
//        invalidate();
//    }

    private int mTotalWidth = 0;
    private int mWidth = 0;
    private int mHeight = 0;

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int max = getChildCount();
        if (mWidth == 0) {
            mWidth = MeasureSpec.getSize(widthMeasureSpec);
            mHeight = MeasureSpec.getSize(heightMeasureSpec);
//            Log.i("jsp", "onMeasure - " + mWidth);
            mTotalWidth = max * mWidth;

//            Log.e("jsp", "onMeasure - " + mTotalWidth + " height = " + mHeight);
        }

        for (int i = 0; i < max; i++) {
            View child = getChildAt(i);

            int widthSpec = getChildMeasureSpec(widthMeasureSpec, 0, mWidth);
            int heightSpec = getChildMeasureSpec(heightMeasureSpec, 0, mHeight);

            child.measure(widthSpec, heightSpec);
        }

        setMeasuredDimension(mTotalWidth, mHeight);
    }

    private boolean mFirstLayout = true;
    private int mDistanceArr[];
    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        if(mFirstLayout){
//            Log.d("jsp", "onLayout - " + mWidth);

            int max = getChildCount();

            int left = mWidth * 2;

            mDistanceArr = new int[max];

            for (int i = 0; i < max; i++) {
                mDistanceArr[i] = mWidth * i;
                View view = getChildAt(i);

                if (left >= mTotalWidth) {
                    left = 0;
                }

//                Log.w("jsp", "left = " + left);
                int right = left + mWidth;
                view.layout(left, getTop(), right, getBottom());
                mViewArray.put(i, new ViewSpec(left, getTop(), right, getBottom(), view));
                view.setTag(mViewArray.get(i));
                left += mWidth;
            }

            scrollTo(mWidth * 2, 0);
            mScrollX = mWidth * 2;

            mFirstLayout = false;
        }
    }

    private InfinityPagerAdapter mAdapter;
    public void setAdapter(InfinityPagerAdapter pagerAdapter) {
        mAdapter = pagerAdapter;

        ViewGroup.LayoutParams params = new ViewGroup.LayoutParams(mWidth, mHeight);
        int max = mAdapter.getCount();
        for (int i = 0; i < max; i++) {
            View view = (View) mAdapter.instantiateItem(this, i);
            view.setTag(i);
            view.setLayoutParams(params);
        }
        postInvalidate();
    }

    private SparseArray<ViewSpec> mViewArray = new SparseArray<ViewSpec>();

    private class ViewSpec {
        int left;
        int top;
        int right;
        int bottom;
        View view;

        private ViewSpec(int left, int top, int right, int bottom, View view) {
            this.left = left;
            this.top = top;
            this.right = right;
            this.bottom = bottom;
            this.view = view;
        }

        public int getLeft() {
            return left;
        }

        public void setLeft(int left) {
            this.left = left;
        }

        public int getTop() {
            return top;
        }

        public void setTop(int top) {
            this.top = top;
        }

        public int getRight() {
            return right;
        }

        public void setRight(int right) {
            this.right = right;
        }

        public int getBottom() {
            return bottom;
        }

        public void setBottom(int bottom) {
            this.bottom = bottom;
        }

        public View getView() {
            return view;
        }

        public void setView(View view) {
            this.view = view;
        }
    }

}
