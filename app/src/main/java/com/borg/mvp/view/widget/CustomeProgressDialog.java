package com.borg.mvp.view.widget;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.Bundle;

import com.borg.androidemo.R;

/**
 * Created by Gulliver(feilong) on 15/12/31.
 */
public class CustomeProgressDialog extends ProgressDialog {
    private Context mContent ;

    public CustomeProgressDialog(Context context){
        super(context, R.style.theme_customer_progress_dialog) ;
        mContent = context ;
    }

    public CustomeProgressDialog(Context context, int theme) {
        super(context, theme);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.setIndeterminateDrawable(mContent.getResources().getDrawable(R.drawable.myprogressbar)) ;
        this.setCanceledOnTouchOutside(false) ;
    }


    public static CustomeProgressDialog show(Context context,String message){
        CustomeProgressDialog dialog = new CustomeProgressDialog(context) ;
        dialog.setMessage(message) ;
        dialog.show() ;
        return dialog ;
    }
}
