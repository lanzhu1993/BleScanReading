package com.josh.scan.manager

import com.inuker.bluetooth.library.BluetoothClient
import com.josh.scan.App

/**
 * description:
 *
 * author: josh.lu
 * created: 2020/9/20 12:09 AM
 * email:  1113799552@qq.com
 * version: v1.0
 */
class ClientManager private constructor(){

    private var mClient: BluetoothClient = BluetoothClient(App.instance)


    fun getClient():BluetoothClient{
        return mClient
    }


    companion object {
        val instance = SingletonHolder.holder
    }

    private object SingletonHolder {
        val holder = ClientManager()
    }


}