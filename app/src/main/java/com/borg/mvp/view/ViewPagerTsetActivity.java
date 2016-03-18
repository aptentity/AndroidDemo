package com.borg.mvp.view;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.borg.androidemo.R;
import com.borg.mvp.view.fragment.PlanetFragment;
import com.borg.mvp.view.fragment.TestFragment;

import java.util.ArrayList;
import java.util.List;

import me.relex.circleindicator.CircleIndicator;

public class ViewPagerTsetActivity extends AppCompatActivity {
    private ViewPager mViewPager;
    private List<Fragment> mFragments = new ArrayList<Fragment>();
    private FragmentPagerAdapter mAdapter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_pager_tset);
        mViewPager = (ViewPager) findViewById(R.id.id_viewpager);
        //设置卡片之间的偏移量
        mViewPager.setPageMargin(getResources().getDimensionPixelOffset(R.dimen.viewpager_margin));
        initView();

        mAdapter = new FragmentPagerAdapter(getSupportFragmentManager())
        {

            @Override
            public int getCount()
            {
                return mFragments.size();
            }

            @Override
            public Fragment getItem(int arg0)
            {
                return mFragments.get(arg0);
            }
        };

        mViewPager.setAdapter(mAdapter);
        mViewPager.setOffscreenPageLimit(mFragments.size());//缓存的page数量
        mViewPager.addOnPageChangeListener(listener);

        initCirclePoint();
        CircleIndicator indicator = (CircleIndicator)findViewById(R.id.indicator);
        indicator.setViewPager(mViewPager);

    }

    /**
     * 滑动回调
     */
    private ViewPager.OnPageChangeListener listener = new ViewPager.OnPageChangeListener() {
        @Override
        public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

        }

        @Override
        public void onPageSelected(int position) {
            //重新设置原点布局集合
            for (int i = 0; i < imageViews.length; i++) {
                imageViews[position]
                        .setBackgroundResource(R.drawable.point_focused);
                if (position != i) {
                    imageViews[i]
                            .setBackgroundResource(R.drawable.point_unfocused);
                }
            }
        }

        @Override
        public void onPageScrollStateChanged(int state) {

        }
    };

    /**
     * 添加fragment
     */
    private void initView()
    {
        PlanetFragment tab01 = new PlanetFragment();
        TestFragment tab02 = new TestFragment();
        PlanetFragment tab03 = new PlanetFragment();
        TestFragment tab04 = new TestFragment();
        mFragments.add(tab01);
        mFragments.add(tab02);
        mFragments.add(tab03);
        mFragments.add(tab04);
    }
    private ImageView[] imageViews;
    private ImageView imageView;
    private void initCirclePoint(){
        ViewGroup group = (ViewGroup) findViewById(R.id.viewGroup);
        imageViews = new ImageView[mFragments.size()];
        //广告栏的小圆点图标
        for (int i = 0; i < mFragments.size(); i++) {
            //创建一个ImageView, 并设置宽高. 将该对象放入到数组中
            imageView = new ImageView(this);
            imageView.setLayoutParams(new ViewGroup.LayoutParams(10,10));
            imageViews[i] = imageView;

            //初始值, 默认第0个选中
            if (i == 0) {
                imageViews[i]
                        .setBackgroundResource(R.drawable.point_focused);
            } else {
                imageViews[i]
                        .setBackgroundResource(R.drawable.point_unfocused);
            }
            //将小圆点放入到布局中
            group.addView(imageViews[i]);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mViewPager.removeOnPageChangeListener(listener);
    }
}
