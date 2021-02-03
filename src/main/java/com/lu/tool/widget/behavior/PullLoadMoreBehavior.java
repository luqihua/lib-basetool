package com.lu.tool.widget.behavior;

import android.content.Context;
import android.os.Handler;
import android.os.Message;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import androidx.core.view.ViewCompat;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.DecelerateInterpolator;
import android.view.animation.LinearInterpolator;

/**
 * 下拉刷新(仿微信朋友圈)
 * Created by lqh on 2016/9/1.
 */
public class PullLoadMoreBehavior extends CoordinatorLayout.Behavior<View> {

    public static final int GET_DATA_SUCCESS = 0x111;
    public static final int GET_DATA_FAIL = 0x112;

    private final float MAX_TRANSLATE_RATIO = 0.5f;
    private float target_child_offset;

    private boolean mIsAnimating;
    private boolean mRefreshSuccess = false;
    private boolean mDoRefresh;

    private OnPullDownListener mListener;

    private View mChild;
    private int mHeight;

    public void setOnPullDownListener(OnPullDownListener listener) {
        this.mListener = listener;
    }

    public PullLoadMoreBehavior(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    public boolean onLayoutChild(CoordinatorLayout parent, View child, int layoutDirection) {
        if (mChild == null) {
            mChild = child;
            mHeight = child.getMeasuredHeight();
            ViewCompat.setTranslationY(child, -mHeight * 1.2f);
            target_child_offset = Math.abs(child.getTranslationY()) - mHeight;
        }
        return super.onLayoutChild(parent, child, layoutDirection);
    }

    @Override
    public boolean onStartNestedScroll(CoordinatorLayout coordinatorLayout, View child, View directTargetChild, View target, int nestedScrollAxes) {
        return (nestedScrollAxes & ViewCompat.SCROLL_AXIS_VERTICAL) != 0 && !mDoRefresh;
    }

    @Override
    public void onNestedPreScroll(CoordinatorLayout coordinatorLayout, View child, View target, int dx, int dy, int[] consumed) {
        if (mIsAnimating && dy > 0) {

            boolean shouldTranslateY = target.getTranslationY() <
                    mHeight * (MAX_TRANSLATE_RATIO + 1) + target_child_offset;

            animView(target, child, dy, shouldTranslateY);
            consumed[1] = dy;
        }
        super.onNestedPreScroll(coordinatorLayout, child, target, dx, dy, consumed);
    }

    @Override
    public void onNestedScroll(CoordinatorLayout coordinatorLayout, View child, View target, int dxConsumed, int dyConsumed, int dxUnconsumed, int dyUnconsumed) {
        if (dyUnconsumed < 0) {
            mIsAnimating = true;
            boolean shouldTranslateY = child.getTranslationY() < mHeight * MAX_TRANSLATE_RATIO;
            animView(target, child, dyUnconsumed, shouldTranslateY);
        }
    }

    @Override
    public void onStopNestedScroll(CoordinatorLayout coordinatorLayout, final View child, final View target) {
        if (mIsAnimating) {
            ViewCompat.animate(target)
                    .translationY(0)
                    .setDuration(400)
                    .setInterpolator(new DecelerateInterpolator())
                    .start();

            if (child.getTranslationY() < mHeight * MAX_TRANSLATE_RATIO) {
                animFinish();
            } else {
                doRefresh();
            }
        }

    }

    private void doRefresh() {
        ViewCompat.animate(mChild)
                .rotationBy(500)
                .setDuration(1000)
                .withStartAction(new Runnable() {
                    @Override
                    public void run() {
                        mDoRefresh = true;
                        // TODO: 2016/11/10
//                        mListener.getData(mHandler);
                    }
                })
                .setInterpolator(new LinearInterpolator())
                .withEndAction(new Runnable() {
                    @Override
                    public void run() {
                        // TODO: 2016/11/10
                        if (mRefreshSuccess) {
//                            mListener.updateView();
                            Log.d("PullLoadMoreBehavior", "刷新成功");
                        } else {
                            Log.d("PullLoadMoreBehavior", "刷新失败");
                        }
                        animFinish();
                    }
                })
                .start();
    }

    private void animFinish() {
        mChild.setRotation(0);
        ViewCompat.animate(mChild)
                .translationY(-mHeight - target_child_offset)
                .setInterpolator(new DecelerateInterpolator())
                .setDuration(500)
                .withEndAction(new Runnable() {
                    @Override
                    public void run() {
                        mRefreshSuccess = false;
                        mIsAnimating = false;
                        mDoRefresh = false;
                    }
                })
                .start();

    }

    private void animView(View target, View child, int distance, boolean shouldTranslateY) {
        if (shouldTranslateY) {
            ViewCompat.animate(child)
                    .translationYBy(-distance * 0.4f)
                    .rotationBy(distance * 5)
                    .setDuration(0)
                    .start();
        } else {
            ViewCompat.animate(child)
                    .rotationBy(distance * 5)
                    .setDuration(0)
                    .start();
        }

        ViewCompat.animate(target)
                .translationYBy(-distance * 0.4f)
                .setDuration(0)
                .start();
    }

    public static <V extends View> PullLoadMoreBehavior from(V view) {
        ViewGroup.LayoutParams params = view.getLayoutParams();
        if (!(params instanceof CoordinatorLayout.LayoutParams)) {
            throw new IllegalArgumentException("The view is not a child of CoordinatorLayout");
        }
        CoordinatorLayout.Behavior behavior = ((CoordinatorLayout.LayoutParams) params).getBehavior();
        if (!(behavior instanceof PullLoadMoreBehavior)) {
            throw new IllegalArgumentException("The view is not associated with ScaleDownShowBehavior");
        }
        return (PullLoadMoreBehavior) behavior;
    }

    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case GET_DATA_SUCCESS:
                    mRefreshSuccess = true;
                    break;
                case GET_DATA_FAIL:
                    mRefreshSuccess = false;
                    break;
            }
        }
    };


    public interface OnPullDownListener {
        void getData(Handler handler);

        void updateView();
    }
}
