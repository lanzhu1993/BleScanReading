package com.josh.scan.utils;

import android.widget.Toast;

import com.josh.scan.App;

/**
 * description:
 * author:         josh.lu
 * email:          1113799552@qq.com
 * createDate:     2020/10/23 10:45 AM
 * version:        v1.0
 */
public class ToastUtils {

    private static Toast mToast;

    public static void showToast(String text) {
        if (mToast == null) {
            mToast = Toast.makeText(App.getInstance(), text, Toast.LENGTH_SHORT);
        } else {
            mToast.setText(text);
            mToast.setDuration(Toast.LENGTH_SHORT);
        }
        mToast.show();
    }

    public static void showToast( int paramInt) {
        if (mToast == null) {
            mToast = Toast.makeText(App.getInstance(), paramInt, Toast.LENGTH_SHORT);
        } else {
            mToast.setText(paramInt);
            mToast.setDuration(Toast.LENGTH_SHORT);
        }
        mToast.show();
    }
}
