package com.josh.scan.ui

import android.Manifest
import android.bluetooth.BluetoothGatt
import android.bluetooth.BluetoothGattCharacteristic
import android.bluetooth.BluetoothGattService
import android.content.Intent
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View
import androidx.core.content.ContextCompat
import cn.com.heaton.blelibrary.ble.Ble
import cn.com.heaton.blelibrary.ble.callback.BleConnectCallback
import cn.com.heaton.blelibrary.ble.callback.BleNotifyCallback
import cn.com.heaton.blelibrary.ble.model.BleDevice
import cn.com.heaton.blelibrary.ble.utils.ByteUtils
import com.josh.scan.R
import com.josh.scan.base.BaseActivity
import com.josh.scan.ui.BluetoothScanActivity.Companion.EXTRA_TAG
import com.josh.scan.ui.SensorReadingsActivity.Companion.GATT_SERVICE
import com.josh.scan.utils.StatusBarUtil
import com.josh.scan.utils.ToastUtils
import com.tbruyelle.rxpermissions3.RxPermissions
import kotlinx.android.synthetic.main.activity_main.*


class MainActivity : BaseActivity()  {

    private var hasBluetoothPermission = false
    private var bleDevice: BleDevice? = null
    private var gattServices: ArrayList<BluetoothGattService> = ArrayList()


    private val ble = Ble.getInstance<BleDevice>()


    companion object{
        const val DEVICE_REQUEST_CODE = 100
        const val DEVICE_RESULT_CODE = 101

    }


    override fun getLayout() = R.layout.activity_main

    override fun initView() {
        StatusBarUtil.setColor(this, ContextCompat.getColor(this, R.color.color_0A5566), 0)
        supportActionBar?.title = "首页"
    }

    override fun initData() {
        RxPermissions(this)
            .request( Manifest.permission.ACCESS_COARSE_LOCATION,Manifest.permission.ACCESS_FINE_LOCATION)
            .subscribe { granted ->
                hasBluetoothPermission = granted
                ble.turnOnBlueToothNo()
            }
    }

    override fun initListener() {
        mainDisConnectBtn.setOnClickListener {
            if (null != bleDevice){
                ble.disconnect(bleDevice)
            }
        }
        mainViewReadingsBtn.setOnClickListener {
            if (gattServices.isEmpty()){
                return@setOnClickListener
            }
            startActivity(Intent(this,SensorReadingsActivity::class.java).apply {
                putExtra(GATT_SERVICE,gattServices)
            })
        }
    }


    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.main_menu, menu);
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.action_connect -> {
                startActivityForResult(Intent(this, BluetoothScanActivity::class.java),DEVICE_REQUEST_CODE)
            }
        }
        return true
    }

    private val connectCallback = object : BleConnectCallback<BleDevice>(){

        override fun onConnectTimeOut(device: BleDevice?) {
            ToastUtils.showToast("连接超时:" + device!!.bleName)

        }

        override fun onServicesDiscovered(device: BleDevice?, gatt: BluetoothGatt?) {
            runOnUiThread {
                gattServices.clear()
                gatt?.let {
                    gattServices.addAll(gatt.services)
                }
            }
        }

        override fun onReady(device: BleDevice?) {
            super.onReady(device)
            //连接成功后，设置通知
            ble.enableNotify(device, true, object : BleNotifyCallback<BleDevice>() {
                override fun onChanged(device: BleDevice, characteristic: BluetoothGattCharacteristic) {
                    val uuid = characteristic.uuid
                    Log.e("lanzhu", "onChanged==uuid:$uuid")
                    Log.e("lanzhu", "onChanged==data:" + ByteUtils.toHexString(characteristic.value))
                    runOnUiThread {
                        ToastUtils.showToast(
                            String.format("收到设备通知数据: %s", ByteUtils.toHexString(characteristic.value))
                        )
                    }
                }

                override fun onNotifySuccess(device: BleDevice) {
                    super.onNotifySuccess(device)
                    Log.e("lanzhu", "onNotifySuccess: " + device.bleName)
                }
            })
        }
        override fun onConnectionChanged(device: BleDevice?) {
            device?.let {
                when {
                    device.isConnected -> {
                        mainConnectStateTv.text = "状态：已连接"
                        mainDisConnectBtn.visibility = View.VISIBLE
                    }
                    device.isConnecting -> {
                        mainConnectStateTv.text = "状态：连接中..."
                        mainDisConnectBtn.visibility = View.INVISIBLE
                    }
                    device.isDisconnected -> {
                        mainConnectStateTv.text = "状态：未连接"
                        mainDisConnectBtn.visibility = View.INVISIBLE
                    }
                    device.isDisconnected ->{
                        mainConnectStateTv.text = "状态：断开连接"
                        mainDisConnectBtn.visibility = View.INVISIBLE
                    }
                }
            }

        }


        override fun onConnectException(device: BleDevice?, errorCode: Int) {
            ToastUtils.showToast("连接异常，异常状态码:$errorCode")

        }

    }


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (requestCode == DEVICE_REQUEST_CODE && resultCode == DEVICE_RESULT_CODE && null != data){
            bleDevice = data.getParcelableExtra(EXTRA_TAG)
            if (null != bleDevice){
                ble.connect(bleDevice, connectCallback)
            }
        }
        super.onActivityResult(requestCode, resultCode, data)
    }



    override fun onDestroy() {
        super.onDestroy()
        if (null != bleDevice){
            ble.disconnect(bleDevice)
        }
    }
}



