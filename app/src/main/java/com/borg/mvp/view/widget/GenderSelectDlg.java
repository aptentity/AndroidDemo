package com.borg.mvp.view.widget;

import android.app.AlertDialog;
import android.content.Context;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;

import com.borg.androidemo.R;

/**
 * Created by Gulliver(feilong) on 15/12/15.
 */
public class GenderSelectDlg {
    AlertDialog dlg;
    onGenderSelectListener listener;
    public GenderSelectDlg(Context context){
        dlg = new AlertDialog.Builder(context).create();
        dlg.show();
        Window mWindow = dlg.getWindow();
        mWindow.setContentView(R.layout.gender_select_dialog);
        mWindow.getDecorView().setPadding(0, 0, 0, 0);
        WindowManager.LayoutParams lp = mWindow.getAttributes();
        lp.width = WindowManager.LayoutParams.MATCH_PARENT;
        lp.height = WindowManager.LayoutParams.WRAP_CONTENT;
        mWindow.setAttributes(lp);
        mWindow.setGravity(Gravity.BOTTOM);

        mWindow.findViewById(R.id.btn_boy).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (listener != null) {
                    listener.select(onGenderSelectListener.BOY);
                }
                hide();
            }
        });

        mWindow.findViewById(R.id.btn_girl).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (listener != null) {
                    listener.select(onGenderSelectListener.GIRL);
                }
                hide();
            }
        });

        mWindow.findViewById(R.id.btn_cancel).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (listener != null) {
                    listener.select(onGenderSelectListener.UNSELECT);
                }
                hide();
            }
        });
    }

    public void setGenderSelectListener(onGenderSelectListener listener){
        this.listener = listener;
    }

    public void show(){

    }

    public void hide(){
        dlg.dismiss();
    }

    public interface onGenderSelectListener{
        /**
         * 0-取消,1-男孩,2-女孩
         * @param i
         */
        public void select(int i);
        int BOY=1;
        int GIRL=2;
        int UNSELECT=0;
    }
}
