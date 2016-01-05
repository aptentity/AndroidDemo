package com.borg.mvp.view.widget;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;

import com.borg.androidemo.R;
import com.borg.mvp.utils.CommonActivitiesUtils;
import com.borg.mvp.utils.CommonUtil;

/**
 * Created by Gulliver(feilong) on 16/1/5.
 */
public class NetworkTipDlg {
    public static void show(final Activity activity){
        AlertDialog dlg = new AlertDialog.Builder(activity).setMessage(activity.getString(R.string.action_scan))
                .setNegativeButton("quite", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                        CommonUtil.exitProcess();
                    }
                }).setPositiveButton("setting", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        CommonActivitiesUtils.showNetWorkSettingActivity(activity);
                        dialog.dismiss();
                    }
                }).setCancelable(false)
                .create();
        dlg.show();
    }
}
