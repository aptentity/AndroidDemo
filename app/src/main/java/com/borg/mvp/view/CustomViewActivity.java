package com.borg.mvp.view;

import android.app.Activity;
import android.app.WallpaperManager;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.support.v7.app.AppCompatActivity;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.TextPaint;
import android.text.TextUtils;
import android.text.method.LinkMovementMethod;
import android.text.style.ImageSpan;
import android.view.View;
import android.view.ViewTreeObserver;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.borg.androidemo.R;
import com.borg.mvp.model.Network.INetworkCallback;
import com.borg.mvp.model.Network.QrNetworkHelper;
import com.borg.mvp.model.Thread.TestThread;
import com.borg.mvp.model.entities.QrLoginResult;
import com.borg.mvp.model.entities.QrResult;
import com.borg.mvp.utils.ImageUtil;
import com.borg.mvp.utils.LogHelper;
import com.borg.mvp.utils.QRCodeUtils;
import com.borg.mvp.utils.SmileyParser;
import com.borg.mvp.utils.ToastUtil;

import java.io.File;
import java.net.URL;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.FutureTask;

public class CustomViewActivity extends AppCompatActivity {
    private final String TAG = CustomViewActivity.class.getSimpleName();
    private View mCircleView;
    private ImageView mImageView;
    private TextView mTvTest;
    SmileyParser smileyParser;
    EditText mEtTest;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //FLAG_TURN_SCREEN_ON使Activity启动的时候能够点亮屏幕。FLAG_SHOW_WHEN_LOCKED能够在屏幕锁定的时候，也能显示
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED
                | WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON);

        setContentView(R.layout.activity_custom_view);
        mCircleView = findViewById(R.id.cv_test);
        mImageView = (ImageView)findViewById(R.id.imageView2);
        mTvTest = (TextView)findViewById(R.id.tv_test);
        mEtTest = (EditText)findViewById(R.id.et_test);
        SmileyParser.init(this);
        smileyParser = SmileyParser.getInstance();//进行初始化

        String newContent = "哈哈" + "[XX]";
        CharSequence replace = smileyParser.strToSmiley(newContent);
        mEtTest.setText(replace);
        mEtTest.setSelection(replace.length());

        setWallpaper();
        toggleEllipsize(mTvTest,"啦啦啦啦啦啦啦啦老我亟待解决桑德菲杰老实交代傅雷家书了肯德基傅雷家书风口浪尖就急急急急急急急急急急急急急急急急急急");

        //设置图片
        Drawable drawable = getResources().getDrawable(R.drawable.head2_img);
        //将Drawable转化为Bitmap
        Bitmap bitmap = ImageUtil.drawableToBitmap(drawable);
        //缩放图片
        Bitmap zoomBitmap = ImageUtil.zoomBitmap(bitmap, 100, 100);
        //获取圆角图片
        Bitmap roundBitmap = ImageUtil.getRoundedCornerBitmap(zoomBitmap, 10.0f);
        //获取倒影图片
        Bitmap reflectBitmap = ImageUtil.createReflectionImageWithOrigin(zoomBitmap);
        ImageView mImageView01 = (ImageView)findViewById(R.id.iv_test1);
        ImageView mImageView02 = (ImageView)findViewById(R.id.iv_test2);
        mImageView01.setImageBitmap(roundBitmap);
        mImageView02.setImageBitmap(reflectBitmap);
    }

    /**
     * 将桌面图片设为背景
     */
    private void setWallpaper(){
        //获取WallpaperManager 壁纸管理器
        WallpaperManager wallpaperManager = WallpaperManager.getInstance(this); // 获取壁纸管理器
        // 获取当前壁纸
        Drawable wallpaperDrawable = wallpaperManager.getDrawable();
        //将Drawable,转成Bitmap
        Bitmap bm = ((BitmapDrawable) wallpaperDrawable).getBitmap();

        //获取布局
        LinearLayout layout = (LinearLayout) this.findViewById(R.id.ll_main);
        //设置 背景
        layout.setBackgroundDrawable(new BitmapDrawable(bm));
    }

    /**
     * 用于演示
     * @param view
     */
    public void onClick(View view){
        switch (view.getId()){
            case R.id.btn_scrollby:
                scrollBy();
                break;
            case R.id.btn_scrollto:
                LogHelper.d(TAG,"scroll to");
                ToastUtil.showShort("scroll to");
                scrollTo();
                break;
            case R.id.cv_test:
                LogHelper.d(TAG,"click circle view");
                break;
            //线程测试
            case R.id.btn_thread_start://在子线程中
                Thread thread = new ThreadTest();
                thread.start();
                LogHelper.d(TAG, "thread start");
                //并不是马上停止
                //thread.stop();
                mThread.start();
                break;
            case R.id.btn_thread_run://在调用线程中执行
                LogHelper.d(TAG, "thread run");
                //暂停或继续线程
                if (mThread.isRunning()){
                    mThread.onPause();
                }else {
                    mThread.onResume();
                }

                Thread thread1 = new ThreadTest();
                thread1.run();

                break;
            case R.id.btn_get_qr://网络获取url，生成二维码
                getQrCode();
                break;
            case R.id.btn_get_contact:
                Intent intent = new Intent(Intent.ACTION_PICK, ContactsContract.Contacts.CONTENT_URI);
                startActivityForResult(intent,100);
                break;
        }
    }

    private static final int CODE = 100;

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch(requestCode)
        {
            case (CODE) :
            {
                if (resultCode == Activity.RESULT_OK)
                {
                    Uri contactData = data.getData();
                    Cursor c = managedQuery(contactData, null, null, null, null);
                    c.moveToFirst();
                    String phoneNum=this.getContactPhone(c);
                    LogHelper.d(TAG,"get phone num:"+phoneNum);
                }
                break;

            }

        }
    }


    //获取联系人电话
    private String getContactPhone(Cursor cursor)
    {

        int phoneColumn = cursor.getColumnIndex(ContactsContract.Contacts.HAS_PHONE_NUMBER);
        int phoneNum = cursor.getInt(phoneColumn);
        String phoneResult="";
        //System.out.print(phoneNum);
        if (phoneNum > 0)
        {
            // 获得联系人的ID号
            int idColumn = cursor.getColumnIndex(ContactsContract.Contacts._ID);
            String contactId = cursor.getString(idColumn);
            // 获得联系人的电话号码的cursor;
            Cursor phones = getContentResolver().query(
                    ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
                    null,
                    ContactsContract.CommonDataKinds.Phone.CONTACT_ID+ " = " + contactId,
                    null, null);
            //int phoneCount = phones.getCount();
            //allPhoneNum = new ArrayList<String>(phoneCount);
            if (phones.moveToFirst())
            {
                // 遍历所有的电话号码
                for (;!phones.isAfterLast();phones.moveToNext())
                {
                    int index = phones.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER);
                    int typeindex = phones.getColumnIndex(ContactsContract.CommonDataKinds.Phone.TYPE);
                    int phone_type = phones.getInt(typeindex);
                    String phoneNumber = phones.getString(index);
                    switch(phone_type)
                    {
                        case 2:
                            phoneResult=phoneNumber;
                            break;
                    }
                    //allPhoneNum.add(phoneNumber);
                }
                if (!phones.isClosed())
                {
                    phones.close();
                }
            }
        }
        return phoneResult;
    }

    /**
     * 网络获取url，生成二维码
     */
    private void getQrCode(){
        QrNetworkHelper.getQrUrl(new INetworkCallback() {
            @Override
            public void onSuccess(String result) {
                LogHelper.d(TAG,"getQrUrl onSuccess:"+result);
                mQrResult = new QrResult(result);
                if (mQrResult.isSuccess()){
                    ToastUtil.showShort("getQrUrl success");
                    //生成二维码并显示
                    File file = new File(getBaseContext().getFilesDir(), "qr.jpg");
                    QRCodeUtils.createQRImage(mQrResult.getUrl(), 300, 300, null, file.getPath());
                    final Bitmap bMap = BitmapFactory.decodeFile(file.getPath());
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            mImageView.setImageBitmap(bMap);
                        }
                    });
                    check();
                }else {
                    LogHelper.d(TAG,"getQrUrl onFail");
                    ToastUtil.showShort("getQrUrl onFail");
                }
            }

            @Override
            public void onFail(int code, String result) {
                LogHelper.d(TAG,"getQrUrl onFail:"+code+":"+result);
                ToastUtil.showShort("getQrUrl onFail:" + code + ":"+result);
            }
        });
    }

    QrResult mQrResult;
    Timer timer;

    /**
     * 检查登录结果
     */
    private void check(){
        if (timer==null){
            timer = new Timer(true);
            timer.schedule(new MyTimerTask(),1000,1000);
        }
    }

    class MyTimerTask extends TimerTask{
        @Override
        public void run() {
            LogHelper.d(TAG,"qrresult...");
            QrNetworkHelper.getToken(mQrResult.getTime(), mQrResult.getAt(), new INetworkCallback() {
                @Override
                public void onSuccess(String result) {
                    LogHelper.d(TAG,"onSuccess:"+result);
                    QrLoginResult qrLoginResult = new QrLoginResult(result);
                    if (qrLoginResult.isSuccess()){
                        if (qrLoginResult.getCode().equals(QrLoginResult.LOGIN_SUCCESS)){
                            ToastUtil.showShort("login success");
                            if (timer!=null){
                                timer.cancel();
                                timer = null;
                            }
                        }else if (qrLoginResult.getCode().equals(QrLoginResult.LOGIN_EXPIRED)){

                        }
                    }
                }

                @Override
                public void onFail(int code, String result) {
                    LogHelper.d(TAG,"onFail:"+result);
                }
            });
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (timer!=null){
            timer.cancel();
        }
    }

    private TestThread mThread = new TestThread();
    private class ThreadTest extends Thread{
        @Override
        public void run() {
            for (int i=0;i<100;i++){
                LogHelper.d(TAG,""+i);
            }
        }
    }

    /**
     * 演示scrollBy、scrollTo
     * 操作简单，适合对View内容的滑动
     * 计算方法：View的边缘减去View内容边缘，由左向右是负数，由上到下是负数
     */
    private void scrollBy(){
        mCircleView.scrollBy(100,0);
    }

    private void scrollTo(){
        mCircleView.scrollTo(100, 0);
    }

    private void getQrUrl(){
        try{
            URL url = new URL("http://www.baidu.com");
        }catch (Exception e){

        }
    }


    //使用Callable+Future获取执行结果
    private void test(){
        ExecutorService executor = Executors.newCachedThreadPool();
        Task task = new Task();
        Future<Integer> result = executor.submit(task);
        executor.shutdown();

        try {
            Thread.sleep(1000);
        } catch (InterruptedException e1) {
            e1.printStackTrace();
        }

        System.out.println("主线程在执行任务");

        try {
            System.out.println("task运行结果"+result.get());
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }

        System.out.println("所有任务执行完毕");
    }

    class Task implements Callable<Integer> {
        @Override
        public Integer call() throws Exception {
            System.out.println("子线程在进行计算");
            Thread.sleep(3000);
            int sum = 0;
            for(int i=0;i<100;i++)
                sum += i;
            return sum;
        }
    }

    //使用Callable+FutureTask获取执行结果
    private void test2(){
        ExecutorService executor = Executors.newCachedThreadPool();
        Task task = new Task();
        FutureTask<Integer> futureTask = new FutureTask<Integer>(task);
        executor.submit(futureTask);
        executor.shutdown();
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e1) {
            e1.printStackTrace();
        }

        System.out.println("主线程在执行任务");

        try {
            System.out.println("task运行结果"+futureTask.get());
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }

        System.out.println("所有任务执行完毕");
    }

    /**
     * 多余两行，两行最后是省略号，省略号后面是下拉更多
     * @param tv
     * @param desc
     */
    private void toggleEllipsize(final TextView tv,final String desc){
        if(desc == null){
            return;
        }
        tv.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {

            @Override
            public void onGlobalLayout() {
                boolean isEllipsized = (tv.getTag() == null || tv.getTag().equals(false)) ? false : (Boolean) tv.getTag();
                if (isEllipsized) {
                    tv.setTag(false);
                    tv.setText(desc);
                } else {
                    tv.setTag(true);
                    int paddingLeft = tv.getPaddingLeft();
                    int paddingRight = tv.getPaddingRight();
                    TextPaint paint = tv.getPaint();
                    float moreText = tv.getTextSize() * 3;
                    float availableTextWidth = (tv.getWidth() - paddingLeft - paddingRight) * 2 - moreText;

                    CharSequence ellipsizeStr = TextUtils.ellipsize(desc, paint, availableTextWidth, TextUtils.TruncateAt.END);
                    if (ellipsizeStr.length() < desc.length()) {
					/*String html = "<img src='game_info_lookmore'/>";
					CharSequence charSequence = Html.fromHtml(html, new ImageGetter() {

						@Override
						public Drawable getDrawable(String source) {
							Drawable drawable = getResources().getDrawable(
									getResourceId(source));
							drawable.setBounds(
									0,
									0,
									drawable.getIntrinsicWidth()
											- DensityUtil.dip2px(GridGameInfoActivity.this, 3),
									drawable.getIntrinsicHeight()
											- DensityUtil.dip2px(GridGameInfoActivity.this, 1));
							return drawable;
						}
					}, null);
					ellipsizeStr = ellipsizeStr.toString() + charSequence.toString();*/


                        CharSequence temp = ellipsizeStr + ".";
                        SpannableStringBuilder ssb = new SpannableStringBuilder(temp);
                        Drawable dd = getResources().getDrawable(R.mipmap.ic_clear);
                        dd.setBounds(0, 0, dd.getIntrinsicWidth(), dd.getIntrinsicHeight());
                        ImageSpan is = new ImageSpan(dd, ImageSpan.ALIGN_BASELINE);
                        ssb.setSpan(is, temp.length() - 1, temp.length(), Spannable.SPAN_INCLUSIVE_EXCLUSIVE);

//					int yellow = getResources().getColor(R.color.red);
//					ssb.setSpan(new ForegroundColorSpan(yellow),ssb.length()-2,ssb.length(),Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                        tv.setText(ssb);
                        tv.setMovementMethod(LinkMovementMethod.getInstance());
                    } else {
                        tv.setText(desc);
                    }
                }
                if (Build.VERSION.SDK_INT >= 16) {
                    tv.getViewTreeObserver().removeOnGlobalLayoutListener(this);
                } else {
                    tv.getViewTreeObserver().removeGlobalOnLayoutListener(this);
                }
            }
        });
    }
}
