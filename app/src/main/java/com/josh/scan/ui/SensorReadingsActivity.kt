package com.josh.scan.ui

import android.bluetooth.BluetoothDevice
import android.content.Intent
import android.util.Log
import android.view.MenuItem
import android.view.View
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.GridLayoutManager
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.animation.SlideInBottomAnimation
import com.chad.library.adapter.base.listener.OnItemChildClickListener
import com.inuker.bluetooth.library.Constants
import com.inuker.bluetooth.library.Constants.REQUEST_SUCCESS
import com.inuker.bluetooth.library.connect.response.BleNotifyResponse
import com.inuker.bluetooth.library.connect.response.BleReadResponse
import com.inuker.bluetooth.library.connect.response.BleWriteResponse
import com.inuker.bluetooth.library.utils.BluetoothUtils
import com.inuker.bluetooth.library.utils.ByteUtils
import com.josh.scan.R
import com.josh.scan.adapter.SensorReadingAdapter
import com.josh.scan.base.BaseActivity
import com.josh.scan.entity.ReadingsEntity
import com.josh.scan.manager.ClientManager
import com.josh.scan.utils.HexUtils
import com.josh.scan.utils.StatusBarUtil
import kotlinx.android.synthetic.main.activity_sensor_readings.*
import java.util.*

/**
 * description:
 *
 * author: josh.lu
 * created: 2020/9/19 10:47 PM
 * email:  1113799552@qq.com
 * version: v1.0
 */
class SensorReadingsActivity : BaseActivity(), OnItemChildClickListener, BleReadResponse ,
    BleWriteResponse {


    private var readingList = arrayListOf<ReadingsEntity>()
    private var mDeviceMac = ""
    private var mDevice : BluetoothDevice? = null




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

    }

    override fun initData() {
        mDeviceMac = intent.getStringExtra("MAC")?:""
        mDevice = BluetoothUtils.getRemoteDevice(mDeviceMac)

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
            if (null != mDevice){
                ClientManager.instance.getClient().connect(mDeviceMac) { code, data ->
                    if (code == Constants.REQUEST_SUCCESS) {
                        try {
                            ClientManager.instance.getClient().notify(mDeviceMac,data.services[0].uuid,data.services[0].characters[0].uuid, object :
                                BleNotifyResponse{
                                override fun onResponse(code: Int) {
                                    if (code == REQUEST_SUCCESS){
                                        ClientManager.instance.getClient().write(mDeviceMac,data.services[0].uuid,data.services[0].characters[0].uuid,ByteUtils.stringToBytes("FEFF"),this@SensorReadingsActivity)
                                        ClientManager.instance.getClient().read(mDeviceMac,data.services[0].uuid,data.services[0].characters[0].uuid,this@SensorReadingsActivity)

                                    }else{
                                        Toast.makeText(this@SensorReadingsActivity,"配对失败",Toast.LENGTH_SHORT).show()
                                    }
                                }

                                override fun onNotify(
                                    service: UUID?,
                                    character: UUID?,
                                    value: ByteArray?
                                ) {
                                    Toast.makeText(this@SensorReadingsActivity,"通知成功：${ByteUtils.byteToString(value)}",Toast.LENGTH_SHORT).show()

                                }

                            })
                        }catch (e:Exception){
                            e.printStackTrace()
                            Toast.makeText(this,"写数据失败:${e.message}",Toast.LENGTH_SHORT).show()

                        }
//                        data.services.forEach {s->
//                            s.characters.forEach {c->
//                                try {
//                                    ClientManager.instance.getClient().write(mDeviceMac,s.uuid,c.uuid,ByteUtils.stringToBytes("FEFF"),this)
//                                }catch (e:Exception){
//                                    e.printStackTrace()
//                                    Toast.makeText(this,"写数据失败:${e.message}",Toast.LENGTH_SHORT).show()
//
//                                }
////                                ClientManager.instance.getClient().read(mDeviceMac,s.uuid,c.uuid,this)
//                            }
//                        }
                    }
                }
            }
        }
    }


    override fun onItemChildClick(adapter: BaseQuickAdapter<*, *>, view: View, position: Int) {
        startActivity(Intent(this,AnalyseTrendsActivity::class.java))
    }


    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            android.R.id.home -> {
                finish()
            }
        }
        return true
    }

    //获取蓝牙数据
    override fun onResponse(code: Int, data: ByteArray?) {
        if (code == REQUEST_SUCCESS) {
            if (null != data && data.isNotEmpty() && data.size == 12){
                Toast.makeText(this,"读取成功：${ByteUtils.byteToString(data)}",Toast.LENGTH_SHORT).show()
            }
        }else{
            Toast.makeText(this,"获取数据失败code = ${code}",Toast.LENGTH_SHORT).show()
        }
    }

    override fun onResponse(code: Int) {
        if (code == REQUEST_SUCCESS) {
            Toast.makeText(this,"写数据成功",Toast.LENGTH_SHORT).show()
        }else{
            Toast.makeText(this,"写数据失败",Toast.LENGTH_SHORT).show()
        }
    }
}