package com.borg.mvp.view.widget;

import android.app.Activity;
import android.app.ProgressDialog;

/**
 * Created by Gulliver(feilong) on 15/12/14.
 */
public class WaitDlg {
    //private ProgressDialog m_pDialog;
    private CustomeProgressDialog m_pDialog;
    public WaitDlg(Activity activity){
        m_pDialog = new CustomeProgressDialog(activity);
        m_pDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);

        m_pDialog.setIndeterminate(false);
        m_pDialog.setCancelable(false);
    }

    public void show(String msg){
        m_pDialog.setMessage(msg);
        m_pDialog.show();
    }

    public void hide(){
        m_pDialog.hide();
    }
}
