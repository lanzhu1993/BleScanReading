package com.josh.scan.ui

import android.bluetooth.BluetoothGattCharacteristic
import android.bluetooth.BluetoothGattService
import android.content.Intent
import android.util.Log
import android.view.MenuItem
import android.view.View
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.GridLayoutManager
import cn.com.heaton.blelibrary.ble.Ble
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
import org.litepal.Operator.deleteAll
import org.litepal.crud.LitePalSupport
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

    companion object {
        const val GATT_SERVICE = "GATT_SERVICE"
    }

    private val mAdapter by lazy {
        SensorReadingAdapter()
    }


    override fun getLayout() = R.layout.activity_sensor_readings

    override fun initView() {
        StatusBarUtil.setColor(this, ContextCompat.getColor(this, R.color.color_0A5566), 0)
        supportActionBar?.title = "查看数值"
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        //初始化列表
        mSensorRecyclerView.layoutManager = GridLayoutManager(this, 2)
        mSensorRecyclerView.adapter = mAdapter
        mAdapter.adapterAnimation = SlideInBottomAnimation()
        mAdapter.setNewInstance(readingList)
        mAdapter.addChildClickViewIds(R.id.sensorCl)
        startTimer()
    }

    override fun initData() {
        gattServices = intent.getParcelableArrayListExtra(GATT_SERVICE) ?: arrayListOf()
        val time = DateUtils.getTimeStamp("yyyy-MM-dd",DateUtils.getDateNow("yyyy-MM-dd"))
        LitePal.deleteAll(
            SensorTable::class.java,
            "create_time  < ? ",
            "$time}"
        )
        Log.e("lanzhu", "time is : $time")
        readingList.add(ReadingsEntity("钠", "暂无～"))
        readingList.add(ReadingsEntity("钾", "暂无～"))
        readingList.add(ReadingsEntity("钙", "暂无～"))
        readingList.add(ReadingsEntity("葡萄糖", "暂无～"))
        readingList.add(ReadingsEntity("乳酸", "暂无～"))
        readingList.add(ReadingsEntity("pH值", "暂无～"))
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
        if (data.length == 14) {
            readingList.forEachIndexed { index, readingsEntity ->
                val parseInt =
                    Integer.parseInt(data.substring(2 * (index+1), 2 * (index+1) + 2), 16).toFloat() / 100
                val pow = abs((parseInt -a) /b)
                val reading = 10.toDouble().pow(pow)
                val nf: NumberFormat = NumberFormat.getNumberInstance()

                nf.maximumFractionDigits = 2
                nf.roundingMode = RoundingMode.UP
                val format: String = nf.format(reading)
                readingsEntity.readings = parseInt.toString()
                mAdapter.notifyItemChanged(index)
                DbUtils.insertSensor(SensorTable().apply {
                    type = index
                    value = nf.format(parseInt)
                    create_time = System.currentTimeMillis()
                })
            }
        }
    }

    private fun startTimer() {
        timer = fixedRateTimer("", false, 0, 10000) {
            runOnUiThread {
                ToastUtils.showToast("开始追踪")
            }
            getSensorReadings()
        }
    }

    private fun stopTimer() {
        timer?.cancel()
        runOnUiThread {
            ToastUtils.showToast("结束追踪")
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
                    //读操作
                    if ((charaProp and BluetoothGattCharacteristic.PROPERTY_READ) != 0) {
                        Ble.getInstance<BleDevice>().readByUuid(
                            connectDevices[0], serviceUuid, characteristicUuid,
                            object : BleReadCallback<BleDevice?>() {
                                override fun onReadSuccess(
                                    dedvice: BleDevice?,
                                    characteristic: BluetoothGattCharacteristic
                                ) {
                                    super.onReadSuccess(dedvice, characteristic)
                                    runOnUiThread {
                                        showSensorReadingData(ByteUtils.bytes2HexStr(characteristic.value))
                                    }
                                }

                                override fun onReadFailed(device: BleDevice?, failedCode: Int) {
                                    super.onReadFailed(device, failedCode)
                                    ToastUtils.showToast("读取特征失败:$failedCode")
                                }
                            })
                    }
                    //写操作
                    if ((charaProp and BluetoothGattCharacteristic.PROPERTY_WRITE) != 0 || (charaProp and BluetoothGattCharacteristic.PROPERTY_WRITE_NO_RESPONSE) != 0) {
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
                                    ToastUtils.showToast("写入特征成功")
                                }

                                override fun onWriteFailed(device: BleDevice?, failedCode: Int) {
                                    super.onWriteFailed(device, failedCode)
                                    ToastUtils.showToast("写入特征失败:$failedCode")
                                }
                            })
                    }

                }
            }
        }

    }

}