package com.josh.scan

import android.app.Application

/**
 * description:
 *
 * author: josh.lu
 * created: 2020/9/20 12:11 AM
 * email:  1113799552@qq.com
 * version: v1.0
 */
class App :Application() {

    companion object {
        //获取单例
        @JvmStatic lateinit  var instance: Application
            private set
    }
    override fun onCreate() {
        super.onCreate()
        instance = this
    }

}