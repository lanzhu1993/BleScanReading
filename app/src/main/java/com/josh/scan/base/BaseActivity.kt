package com.josh.scan.base

import android.os.Bundle
import androidx.annotation.LayoutRes
import androidx.appcompat.app.AppCompatActivity

/**
 * description:
 *
 * author: josh.lu
 * created: 2020/9/19 10:19 PM
 * email:  1113799552@qq.com
 * version: v1.0
 */
abstract class BaseActivity : AppCompatActivity() {


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(getLayout())
        initData()
        initView()
        initListener()
    }

    @LayoutRes
    abstract fun getLayout(): Int

    abstract fun initView()

    abstract fun initData()

    open fun initListener() {

    }

}