package com.borg.mvp.view.recyclerview;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.TextView;

import com.borg.androidemo.R;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private CircleView mCircleView ;

    private List<String> mList ;

    private TestAdapter mAdapter ;

    private WrapRecyclerView mListView ;

    private Test1Adapter mAdapter1 ;

    private ListView mListView1 ;

    private TextView textView ;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_recycle);
        mList = new ArrayList<>() ;
        for (int i = 0 ; i < 100 ; i ++){
            mList.add("my love "+ i);
        }
//        mAdapter1 = new Test1Adapter(mList) ;
//        mListView1 = (ListView) findViewById(R.id.id_list_view);
//        mListView1.setAdapter(mAdapter1);

        mAdapter = new TestAdapter(mList) ;
        mListView = (WrapRecyclerView) findViewById(R.id.id_recycler_view);
        RecyclerView.LayoutManager manager = new LinearLayoutManager(this) ;
        mListView.setLayoutManager(manager);
        textView = new TextView(this) ;
        ViewGroup.LayoutParams params = new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT) ;
        textView.setLayoutParams(params);
        textView.setText("我是头部View001");
        mListView.addHeaderView(textView);
        mListView.setAdapter(mAdapter);
    }

//    public void startAnimator(View view){
//        ValueAnimator animator = ValueAnimator.ofFloat(0,1,0);
//        animator.setDuration(5000);
//        animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
//            @Override
//            public void onAnimationUpdate(ValueAnimator animation) {
//                float aFloat = (Float) animation.getAnimatedValue() ;
////                if (aFloat < 0.01)
////                    aFloat = 0 ;
//                mCircleView.setPercent(aFloat);
//            }
//        });
//        animator.setRepeatCount(ValueAnimator.INFINITE);
//        animator.setRepeatMode(ValueAnimator.INFINITE);
//        animator.start();
//    }
}
