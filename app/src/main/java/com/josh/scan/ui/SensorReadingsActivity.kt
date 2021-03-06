package com.josh.scan.ui

import android.bluetooth.BluetoothGattCharacteristic
import android.bluetooth.BluetoothGattService
import android.content.Intent
import android.util.Log
import android.view.MenuItem
import android.view.View
import androidx.core.content.ContextCompat
import androidx.fragment.app.FragmentActivity
import androidx.recyclerview.widget.GridLayoutManager
import cn.com.heaton.blelibrary.ble.Ble
import cn.com.heaton.blelibrary.ble.callback.BleNotifyCallback
import cn.com.heaton.blelibrary.ble.callback.BleReadCallback
import cn.com.heaton.blelibrary.ble.callback.BleWriteCallback
import cn.com.heaton.blelibrary.ble.model.BleDevice
import cn.com.heaton.blelibrary.ble.utils.ByteUtils
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.animation.SlideInBottomAnimation
import com.chad.library.adapter.base.listener.OnItemChildClickListener
import com.josh.scan.R
import com.josh.scan.adapter.SensorReadingAdapter
import com.josh.scan.base.BaseActivity
import com.josh.scan.entity.ReadingsEntity
import com.josh.scan.entity.SensorTable
import com.josh.scan.utils.DateUtils
import com.josh.scan.utils.DbUtils
import com.josh.scan.utils.StatusBarUtil
import com.josh.scan.utils.ToastUtils
import kotlinx.android.synthetic.main.activity_sensor_readings.*
import org.litepal.LitePal
import java.math.RoundingMode
import java.text.NumberFormat
import java.util.*
import kotlin.collections.ArrayList
import kotlin.concurrent.fixedRateTimer
import kotlin.math.abs
import kotlin.math.pow

/**
 * description:
 *
 * author: josh.lu
 * created: 2020/9/19 10:47 PM
 * email:  1113799552@qq.com
 * version: v1.0
 */
class SensorReadingsActivity : BaseActivity(), OnItemChildClickListener {


    private var readingList = arrayListOf<ReadingsEntity>()

    private var gattServices: ArrayList<BluetoothGattService> = ArrayList()


    private var timer: Timer? = null

    private lateinit var mDevice : BleDevice

    companion object {
        const val GATT_SERVICE = "GATT_SERVICE"
        const val BLE_DEVICE = "BLE_DEVICE"

    }

    private val mAdapter by lazy {
        SensorReadingAdapter()
    }


    override fun getLayout() = R.layout.activity_sensor_readings

    override fun initView() {
        StatusBarUtil.setColor(this, ContextCompat.getColor(this, R.color.color_0A5566), 0)
        supportActionBar?.title = "Perspiration Analysis"
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        //初始化列表
        mSensorRecyclerView.layoutManager = GridLayoutManager(this, 2)
        mSensorRecyclerView.adapter = mAdapter
        mAdapter.adapterAnimation = SlideInBottomAnimation()
        mAdapter.setNewInstance(readingList)
        mAdapter.addChildClickViewIds(R.id.sensorCl)
        enableNotify();
        startTimer()
    }

    override fun initData() {
        mDevice = intent.getParcelableExtra(BLE_DEVICE)
        gattServices = intent.getParcelableArrayListExtra(GATT_SERVICE) ?: arrayListOf()
        val time = DateUtils.getTimeStamp("yyyy-MM-dd",DateUtils.getDateNow("yyyy-MM-dd"))
        LitePal.deleteAll(
            SensorTable::class.java,
            "create_time  < ? ",
            "$time}"
        )
        Log.e("lanzhu", "time is : $time")
        readingList.add(ReadingsEntity("Sodium", "Not yet～"))
        readingList.add(ReadingsEntity("Potassium", "Not yet～"))
        readingList.add(ReadingsEntity("Calcium", "Not yet～"))
        readingList.add(ReadingsEntity("Glucose", "Not yet～"))
        readingList.add(ReadingsEntity("Lactate", "Not yet～"))
        readingList.add(ReadingsEntity("pH", "Not yet～"))
    }


    override fun initListener() {
        mAdapter.setOnItemChildClickListener(this)
        //读取数据开始点击事件
        mSensorStartBtn.setOnClickListener {
            getSensorReadings()
        }
        mSensorTrackBtn.setOnClickListener {
            startTimer()
        }
        mSensorPauseBtn.setOnClickListener {
            stopTimer()
        }
    }


    override fun onItemChildClick(adapter: BaseQuickAdapter<*, *>, view: View, position: Int) {
        startActivity(Intent(this, AnalyseTrendsActivity::class.java).apply {
            putExtra(AnalyseTrendsActivity.SENSOR_TYPE,position)
        })
    }


    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            android.R.id.home -> {
                finish()
            }
        }
        return true
    }


    private fun showSensorReadingData(data: String) {
        val a = 6.5
        val b = 0.3
        if (data.length == 24) {
            readingList.forEachIndexed { index, readingsEntity ->
                val parseInt =
                    Integer.parseInt(data.substring(4 * (index), 4 * (index) + 4), 16).toFloat() / 100
                val pow = abs((parseInt -a) /b)
                val reading = 10.toDouble().pow(pow)
                val nf: NumberFormat = NumberFormat.getNumberInstance()

                nf.maximumFractionDigits = 2
                nf.roundingMode = RoundingMode.UP
                val format: String = nf.format(reading)
                readingsEntity.readings = parseInt.toString()
                DbUtils.insertSensor(SensorTable().apply {
                    type = index
                    value = nf.format(parseInt)
                    create_time = System.currentTimeMillis()
                })
            }
        }
        mAdapter.setNewInstance(readingList)
        mAdapter.notifyDataSetChanged()
    }

    private fun startTimer() {
        timer = fixedRateTimer("", false, 0, 10000) {
            runOnUiThread {
                ToastUtils.showToast("Start")
                getSensorReadings()
            }
        }
    }

    private fun stopTimer() {
        timer?.cancel()
        runOnUiThread {
            ToastUtils.showToast("Pause")
        }
    }

    private fun enableNotify(){
        val connectDevices = Ble.getInstance<BleDevice>().connectedDevices
        if (!connectDevices.isNullOrEmpty()) {
            gattServices.forEach { gattService ->
                gattService.characteristics.forEach { characteristic ->
                    val serviceUuid: UUID = characteristic.service.uuid
                    val characteristicUuid: UUID = characteristic.uuid
                    val charaProp = characteristic.properties
                    //读操作
                    if ((charaProp and BluetoothGattCharacteristic.PROPERTY_NOTIFY) != 0) {
                        Ble.getInstance<BleDevice>().enableNotifyByUuid(
                            connectDevices[0],true, serviceUuid, characteristicUuid,
                            object : BleNotifyCallback<BleDevice?>() {

                                override fun onChanged(
                                    device: BleDevice?,
                                    characteristic: BluetoothGattCharacteristic?
                                ) {
                                    runOnUiThread {
                                        ToastUtils.showToast("Receive data is :${ByteUtils.bytes2HexStr(characteristic?.value)}")
                                        showSensorReadingData(ByteUtils.bytes2HexStr(characteristic?.value))
                                    }

                                }

                                override fun onNotifySuccess(device: BleDevice?) {
                                    super.onNotifySuccess(device)
                                    runOnUiThread {
                                        ToastUtils.showToast("Receive data success from  :${device?.bleName}")
                                    }
                                }
                            })
                    }
                }
            }
        }
    }

    private fun getSensorReadings() {
        val connectDevices = Ble.getInstance<BleDevice>().connectedDevices
        if (!connectDevices.isNullOrEmpty()) {
            gattServices.forEach { gattService ->
                gattService.characteristics.forEach { characteristic ->
                    val serviceUuid: UUID = characteristic.service.uuid
                    val characteristicUuid: UUID = characteristic.uuid
                    val charaProp = characteristic.properties
                    //写操作
                    if ((charaProp and BluetoothGattCharacteristic.PROPERTY_WRITE) != 0 && (charaProp and BluetoothGattCharacteristic.PROPERTY_WRITE_NO_RESPONSE) != 0) {
                        Ble.getInstance<BleDevice>().writeByUuid(
                            connectDevices[0],
                            ByteUtils.hexStr2Bytes("FEFF"),
                            serviceUuid,
                            characteristicUuid,
                            object : BleWriteCallback<BleDevice?>() {
                                override fun onWriteSuccess(
                                    device: BleDevice?,
                                    characteristic: BluetoothGattCharacteristic
                                ) {
                                    runOnUiThread{
                                        ToastUtils.showToast("Writing feature succeeded")
                                    }
                                }

                                override fun onWriteFailed(device: BleDevice?, failedCode: Int) {
                                    super.onWriteFailed(device, failedCode)
                                    runOnUiThread {
                                        ToastUtils.showToast("Write feature failure:$failedCode")
                                    }
                                }
                            })
                    }

                }
            }
        }

    }

}