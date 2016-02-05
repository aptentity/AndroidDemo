package com.borg.mvp.view.widget;


import android.content.Context;
import android.util.AttributeSet;
import android.widget.ScrollView;

public class PullToRefreshScrollView extends PullToRefreshBase<ScrollView> {
    public PullToRefreshScrollView(Context context) {
        super(context);
    }

    public PullToRefreshScrollView(Context context, int mode) {
        super(context, mode);
    }

    public PullToRefreshScrollView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    protected ScrollView createRefreshableView(Context context, AttributeSet attrs) {
        ScrollView webView = new ScrollView(context,attrs);
        webView.setVerticalScrollBarEnabled(false);
        webView.setHorizontalScrollBarEnabled(false);
        return webView;
    }

    @Override
    protected boolean isReadyForPullDown() {
        return refreshableView.getScrollY() == 0;
    }

    @Override
    protected boolean isReadyForPullUp() {
        return false;
    }
}
