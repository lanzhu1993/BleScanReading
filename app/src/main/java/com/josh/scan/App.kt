package com.josh.scan

import android.app.Application
import cn.com.heaton.blelibrary.ble.Ble
import cn.com.heaton.blelibrary.ble.Ble.InitCallback
import cn.com.heaton.blelibrary.ble.BleLog
import cn.com.heaton.blelibrary.ble.model.BleDevice
import cn.com.heaton.blelibrary.ble.model.BleFactory
import cn.com.heaton.blelibrary.ble.utils.UuidUtils
import com.josh.scan.callback.MyBleWrapperCallback
import com.josh.scan.entity.BleRssiDevice
import com.josh.scan.utils.BleFactoryUtils
import org.litepal.LitePal
import java.util.*

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
        LitePal.initialize(this)
        initBle()
    }

    //初始化蓝牙
    private fun initBle() {
        Ble.options()
            .setLogBleEnable(true) //设置是否输出打印蓝牙日志
            .setThrowBleException(true) //设置是否抛出蓝牙异常
            .setLogTAG("AndroidBLE") //设置全局蓝牙操作日志TAG
            .setAutoConnect(false) //设置是否自动连接
            .setIgnoreRepeat(false) //设置是否过滤扫描到的设备(已扫描到的不会再次扫描)
            .setConnectFailedRetryCount(3) //连接异常时（如蓝牙协议栈错误）,重新连接次数
            .setConnectTimeout(10 * 1000.toLong()) //设置连接超时时长
            .setScanPeriod(12 * 1000.toLong()) //设置扫描时长
            .setMaxConnectNum(7) //最大连接数量
            //                .setScanFilter(scanFilter)
            .setUuidService(UUID.fromString(UuidUtils.uuid16To128("fd00"))) //设置主服务的uuid
            .setUuidWriteCha(UUID.fromString(UuidUtils.uuid16To128("fd01"))) //设置可写特征的uuid
            //                .setUuidReadCha(UUID.fromString(UuidUtils.uuid16To128("fd02")))//设置可读特征的uuid （选填）
            .setUuidNotifyCha(UUID.fromString(UuidUtils.uuid16To128("fd03"))) //设置可通知特征的uuid （选填，库中默认已匹配可通知特征的uuid）
            .setFactory(BleFactoryUtils.getBleFactory())
            .setBleWrapperCallback(MyBleWrapperCallback())
            .create<BleDevice>(
                this,
                object : InitCallback {
                    override fun success() {
                        BleLog.e("MainApplication", "初始化成功")
                    }

                    override fun failed(failedCode: Int) {
                        BleLog.e("MainApplication", "初始化失败：$failedCode")
                    }
                })
    }


}