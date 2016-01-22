package com.borg.mvp.view;

import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

import com.borg.androidemo.R;
import com.borg.mvp.utils.LogHelper;

public class AsyncTaskTestActivity extends AppCompatActivity implements View.OnClickListener{
    private final String TAG = AsyncTaskTestActivity.class.getSimpleName();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_async_task_test);
        findViewById(R.id.btn1).setOnClickListener(this);
        findViewById(R.id.btn2).setOnClickListener(this);
        findViewById(R.id.btn_cancel).setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.btn1:
                new MyAsyncTask("MyAsyncTask1").execute("");
                new MyAsyncTask("MyAsyncTask2").execute("");
                new MyAsyncTask("MyAsyncTask3").execute("");
                new MyAsyncTask("MyAsyncTask4").execute("");
                break;
            case R.id.btn2:
                new MyAsyncTask("MyAsyncTask1").executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR,"");
                new MyAsyncTask("MyAsyncTask2").executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, "");
                new MyAsyncTask("MyAsyncTask3").executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, "");
                new MyAsyncTask("MyAsyncTask4").executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR,"");
                break;
            case R.id.btn_cancel:
                LogHelper.d(TAG," test cancel");
                MyAsyncTask task = new MyAsyncTask("cancel");
                task.execute("");
                try{
                    Thread.sleep(1000);
                    task.cancel(false);
                }catch (InterruptedException e){}
//                task.cancel(true);//不调用onPostExecute
//                task.cancel(false);
                break;
        }
    }

    private static class  MyAsyncTask extends AsyncTask<String,Integer,String>{
        private String name = "MyAsyncTask";
        public MyAsyncTask(String name){
            super();
            this.name = name;
        }

        @Override
        protected String doInBackground(String... params) {
            LogHelper.d(name+" doInBackground");
            try{
                Thread.sleep(3000);
            }catch (InterruptedException e){}
            LogHelper.d(name+" doInBackground finish");
            return name;
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            LogHelper.d(name+" onPostExecute");
        }

        @Override
        protected void onCancelled() {
            super.onCancelled();
            LogHelper.d(name + " onCancelled");
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        LogHelper.d(TAG,"onDestroy");
    }
}
