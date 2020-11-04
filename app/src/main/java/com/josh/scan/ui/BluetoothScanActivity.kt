package com.josh.scan.ui

import android.text.TextUtils
import android.util.Log
import android.view.MenuItem
import android.view.View
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import cn.com.heaton.blelibrary.ble.Ble
import cn.com.heaton.blelibrary.ble.callback.BleScanCallback
import cn.com.heaton.blelibrary.ble.model.ScanRecord
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.animation.SlideInBottomAnimation
import com.chad.library.adapter.base.listener.OnItemChildClickListener
import com.josh.scan.R
import com.josh.scan.adapter.BluetoothItemAdapter
import com.josh.scan.base.BaseActivity
import com.josh.scan.entity.BleRssiDevice
import com.josh.scan.utils.StatusBarUtil
import kotlinx.android.synthetic.main.activity_bluetooth_scan.*

/**
 * description:
 *
 * author: josh.lu
 * created: 2020/9/19 10:47 PM
 * email:  1113799552@qq.com
 * version: v1.0
 */
class BluetoothScanActivity : BaseActivity(), OnItemChildClickListener {


    private val ble = Ble.getInstance<BleRssiDevice>()


    private val mAdapter by lazy {
        BluetoothItemAdapter()
    }

    private var mDeviceList = arrayListOf<BleRssiDevice>()

    companion object {
        const val EXTRA_TAG = "device"
    }


    override fun getLayout() = R.layout.activity_bluetooth_scan

    override fun initView() {
        StatusBarUtil.setColor(this, ContextCompat.getColor(this, R.color.color_0A5566), 0)
        supportActionBar?.title = "Device connection"
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        //初始化列表
        mScanRecyclerView.layoutManager = LinearLayoutManager(this)
        mScanRecyclerView.adapter = mAdapter
        mAdapter.adapterAnimation = SlideInBottomAnimation()
        mAdapter.addChildClickViewIds(R.id.deviceCl)
        searchDevice()
    }

    override fun initData() {
    }

    private fun searchDevice() {
        if (ble != null && !ble.isScanning) {
            ble.startScan(scanCallback)

        }
    }

    override fun initListener() {
        mAdapter.setOnItemChildClickListener(this)
    }


    override fun onPause() {
        super.onPause()
        if (ble.isScanning) {
            ble.stopScan()
        }
    }

    override fun onItemChildClick(adapter: BaseQuickAdapter<*, *>, view: View, position: Int) {
        intent.putExtra(EXTRA_TAG, mDeviceList[position])
        setResult(MainActivity.DEVICE_RESULT_CODE, intent)
        finish()
    }


    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            android.R.id.home -> {
                finish()
            }
        }
        return true
    }

    private val scanCallback = object : BleScanCallback<BleRssiDevice>() {

        override fun onStart() {
            Log.e("lanzhu", "onStart: ")
        }

        override fun onStop() {
            Log.e("lanzhu", "onStop: " )

        }

        override fun onLeScan(device: BleRssiDevice?, rssi: Int, scanRecord: ByteArray?) {
            if (TextUtils.isEmpty(device!!.bleName)) return

            synchronized(ble.locker) {
                for (i in mDeviceList.indices) {
                    val rssiDevice: BleRssiDevice = mDeviceList.get(i)
                    if (TextUtils.equals(rssiDevice.bleAddress, device.bleAddress)) {
                        if (rssiDevice.rssi !== rssi && System.currentTimeMillis() - rssiDevice.rssiUpdateTime > 1000L) {
                            rssiDevice.rssiUpdateTime = System.currentTimeMillis()
                            rssiDevice.rssi = rssi
                            mAdapter.notifyItemChanged(i)
                        }
                        return
                    }
                }
                device.scanRecord = ScanRecord.parseFromBytes(scanRecord)
                device.rssi = rssi
                mDeviceList.add(device)
                mAdapter.setNewInstance(mDeviceList.toMutableList())
                mAdapter.notifyDataSetChanged()
            }

        }
    }
}