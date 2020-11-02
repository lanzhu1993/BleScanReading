package com.josh.scan.ui.dialog

import android.app.Dialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.appcompat.app.AppCompatDialogFragment
import com.josh.scan.R

/**
 * description:
 * author:         josh.lu
 * email:          1113799552@qq.com
 * createDate:     2020/10/12 11:31 AM
 * version:        v1.0
 */
class LoadingDialog:AppCompatDialogFragment() {

    private lateinit var mRootView :View
    private lateinit var mTipMessageTv :TextView


    companion object {
        fun newInstance(): LoadingDialog {
            val meetDialog = LoadingDialog()
            val arguments = Bundle()
            meetDialog.arguments = arguments
            return meetDialog
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setStyle(STYLE_NO_FRAME, R.style.meeting_dialog);
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        mRootView = inflater.inflate(R.layout.dialog_loading_layout, container, false)
        mTipMessageTv = mRootView.findViewById(R.id.tv_dialog_msg)
        return mRootView
    }


}