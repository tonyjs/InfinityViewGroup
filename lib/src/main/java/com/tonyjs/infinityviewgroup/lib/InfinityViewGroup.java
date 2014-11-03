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
import android.widget.Toast;

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
        DisplayMetrics metrics = getContext().getResources().getDisplayMetrics();
        mDpi = metrics.density;
        mScroller = new Scroller(getContext());
    }

    int mScrollX;
    float mFirstX = 0;
    float mFirstY = 0;
    float mLastX = 0;
    int mDistanceX = 0;
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
                break;
            case MotionEvent.ACTION_MOVE:
                float lastX = event.getX();
                float lastY = event.getY();

                mDistanceX = (int) (lastX - mLastX);
                if (Math.abs(mDistanceX) > 5 * mDpi) {
                    int max = mViewArray.size();
                    for (int i = 0; i < max; i++) {
                        ViewSpec viewSpec = mViewArray.get(i);
                        int left = viewSpec.getLeft() + mDistanceX;
                        viewSpec.setLeft(left);
                        viewSpec.setRight(left + mWidth);
                        layoutFromViewSpec(viewSpec);
                    }
                    toLeft = mDistanceX >= 0;
                    mOccurredScroll = true;
                }

                mLastX = lastX;
                break;
            case MotionEvent.ACTION_UP:
            case MotionEvent.ACTION_CANCEL:
                if (mOccurredScroll) {
                    int max = mDistanceArr.length;
                    if (toLeft) {
                        for (int i = 0; i < max; i++) {
                            int target = mDistanceArr[i] + mWidth;
                            View v = getNearView(target);
                            ViewSpec spec = (ViewSpec) v.getTag();
                            if (target == mTotalWidth) {
                                spec.setLeft(0);
                                spec.setRight(mWidth);
                            } else {
                                spec.setLeft(target);
                                spec.setRight(target + mWidth);
                            }
                            layoutFromViewSpec(spec);
                        }
                    } else {
                        for (int i = 0; i < max; i++) {
                            int target = mDistanceArr[i];
                            View v = getNearViewFromRight(target);
                            ViewSpec spec = (ViewSpec) v.getTag();
                            if (target == 0) {
                                spec.setLeft(mTotalWidth - mWidth);
                                spec.setRight(mTotalWidth);
                            } else {
                                spec.setLeft(target - mWidth);
                                spec.setRight(target);
                            }
                            layoutFromViewSpec(spec);
                        }
                    }
                }
                mOccurredScroll = false;
                break;
        }

        return true;
    }

    private View getNearView(int target) {
        View view = null;
        int temp = -1;
        for (int i = 0; i < getChildCount(); i++) {
            View v = getChildAt(i);
            int left = v.getLeft();
            int distance = Math.abs(target - left);
            if (temp == -1) {
                temp = distance;
                view = v;
            } else {
                if (distance < temp) {
                    temp = distance;
                    view = v;
                }
            }
        }
        return view;
    }

    private View getNearViewFromRight(int target) {
        View view = null;
        int temp = -1;
        for (int i = 0; i < getChildCount(); i++) {
            View v = getChildAt(i);
            int right = v.getRight();
            int distance = Math.abs(right - target);
            if (temp == -1) {
                temp = distance;
                view = v;
            } else {
                if (distance < temp) {
                    temp = distance;
                    view = v;
                }
            }
        }
        return view;
    }

    private void layoutFromViewSpec(ViewSpec viewSpec) {
        View view = viewSpec.getView();
        view.layout(viewSpec.getLeft(), viewSpec.getTop(), viewSpec.getRight(), viewSpec.getBottom());
    }

    private int mTotalWidth = 0;
    private int mWidth = 0;
    private int mHeight = 0;

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int max = getChildCount();
        if (mWidth == 0) {
            mWidth = MeasureSpec.getSize(widthMeasureSpec);
            mHeight = MeasureSpec.getSize(heightMeasureSpec);
            mTotalWidth = max * mWidth;
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

            int max = getChildCount();

            int left = mWidth * 2;

            mDistanceArr = new int[max];

            for (int i = 0; i < max; i++) {
                View view = getChildAt(i);

                if (left >= mTotalWidth) {
                    left = 0;
                }
                mDistanceArr[i] = left;

//                Log.w("jsp", "left = " + left);
                int right = left + mWidth;
                view.layout(left, getTop(), right, getBottom());
                mViewArray.put(i, new ViewSpec(i, left, getTop(), right, getBottom(), view));
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
        int originPosition;
        int left;
        int top;
        int right;
        int bottom;
        View view;

        private ViewSpec(int originPosition, int left, int top, int right, int bottom, View view) {
            this.originPosition = originPosition;
            this.left = left;
            this.top = top;
            this.right = right;
            this.bottom = bottom;
            this.view = view;
        }

        public int getOriginPosition() {
            return originPosition;
        }

        public void setOriginPosition(int originPosition) {
            this.originPosition = originPosition;
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
