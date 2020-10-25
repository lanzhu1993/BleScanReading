package com.josh.scan.ui

import android.Manifest
import android.content.Intent
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View
import androidx.core.content.ContextCompat
import com.inuker.bluetooth.library.Constants
import com.inuker.bluetooth.library.connect.listener.BleConnectStatusListener
import com.josh.scan.R
import com.josh.scan.base.BaseActivity
import com.josh.scan.manager.ClientManager
import com.josh.scan.utils.StatusBarUtil
import com.tbruyelle.rxpermissions3.RxPermissions
import kotlinx.android.synthetic.main.activity_main.*


class MainActivity : BaseActivity() {

    private var hasBluetoothPermission = false
    private var mDeviceMac = ""


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
            .request(Manifest.permission.BLUETOOTH, Manifest.permission.ACCESS_COARSE_LOCATION)
            .subscribe { granted ->
                hasBluetoothPermission = granted
            }

        ClientManager.instance.getClient().openBluetooth()
    }

    override fun initListener() {
        mainDisConnectBtn.setOnClickListener {
            ClientManager.instance.getClient().disconnect(mDeviceMac)
        }
        mainViewReadingsBtn.setOnClickListener {
            if (mDeviceMac.isEmpty()){
                return@setOnClickListener
            }
            startActivity(Intent(this,SensorReadingsActivity::class.java).apply {
                putExtra("MAC",mDeviceMac)
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


    private val mConnectStatusListener: BleConnectStatusListener =
        object : BleConnectStatusListener() {
            override fun onConnectStatusChanged(mac: String, status: Int) {
                when (status) {
                    Constants.STATUS_DISCONNECTED -> {
                        mainConnectStateTv.text = "状态：断开连接"
                        mainDisConnectBtn.visibility = View.INVISIBLE
                    }
                    Constants.STATUS_CONNECTED -> {
                        mainConnectStateTv.text = "状态：已连接"
                        mainDisConnectBtn.visibility = View.VISIBLE
                    }
                }

            }
        }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (requestCode == DEVICE_REQUEST_CODE && resultCode == DEVICE_RESULT_CODE && null != data){
            mDeviceMac = data.getStringExtra("MAC")?:""
            if (mDeviceMac.isNotEmpty()){
                ClientManager.instance.getClient().connect(mDeviceMac) { code, data ->
                    Log.e("lanzhu", "connect info code is $code , data is $data")
                }
                ClientManager.instance.getClient().registerConnectStatusListener(mDeviceMac,mConnectStatusListener)
            }
        }
        super.onActivityResult(requestCode, resultCode, data)
    }



    override fun onDestroy() {
        super.onDestroy()
        if (mDeviceMac.isNotEmpty()){
            ClientManager.instance.getClient().unregisterConnectStatusListener(mDeviceMac,mConnectStatusListener)
        }
    }
}



