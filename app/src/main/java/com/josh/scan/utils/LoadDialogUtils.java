package com.josh.scan.utils;

import android.app.Activity;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentManager;

import com.blankj.utilcode.util.ActivityUtils;
import com.josh.scan.ui.dialog.LoadingDialog;


/**
 * description:
 * author:         josh.lu
 * email:          1113799552@qq.com
 * createDate:     2020/9/11 11:32 AM
 * version:        v1.0
 */
public class LoadDialogUtils {


    private static final String LOAD_TIP_DIALOG_TAG = "MeetingLoadingDialog";


    public static void showDialog(FragmentActivity fragmentActivity, String message) {
        Activity activity = ActivityUtils.getTopActivity();
        if (activity instanceof FragmentActivity) {
            FragmentManager manager = fragmentActivity.getSupportFragmentManager();
            LoadingDialog loadTipDialog;
            Fragment fragment = manager.findFragmentByTag(LOAD_TIP_DIALOG_TAG);
            if (fragment instanceof LoadingDialog) {
                loadTipDialog = (LoadingDialog) fragment;
            } else {
                loadTipDialog = LoadingDialog.Companion.newInstance();
            }
            if (loadTipDialog.isAdded()) {
                return;
            }
            manager.beginTransaction()
                    .remove(loadTipDialog)
                    .add(loadTipDialog, LOAD_TIP_DIALOG_TAG)
                    .commitAllowingStateLoss();
        }
    }

    public static void dismissDialog() {
        Activity activity = ActivityUtils.getTopActivity();
        if (activity instanceof FragmentActivity) {
            closeDialog((FragmentActivity) activity);
        }
    }


    private static void closeDialog(FragmentActivity activity) {
        FragmentManager manager = activity.getSupportFragmentManager();
        LoadingDialog loadTipDialog;
        Fragment fragment = manager.findFragmentByTag(LOAD_TIP_DIALOG_TAG);
        if (fragment instanceof LoadingDialog) {
            loadTipDialog = (LoadingDialog) fragment;
            loadTipDialog.dismissAllowingStateLoss();
        }
    }

}
