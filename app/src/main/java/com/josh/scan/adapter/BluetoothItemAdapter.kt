package com.josh.scan.adapter

import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.viewholder.BaseViewHolder
import com.josh.scan.R
import com.josh.scan.entity.BleRssiDevice

/**
 * description: 蓝牙条目适配器
 *
 * author: josh.lu
 * created: 2020/9/19 10:46 AM
 * email:  1113799552@qq.com
 * version: v1.0
 */
class BluetoothItemAdapter :
    BaseQuickAdapter<BleRssiDevice?, BaseViewHolder>(R.layout.layout_device_list_item) {
    override fun convert(holder: BaseViewHolder, item: BleRssiDevice?) {
        var name = ""
        if(item?.bleName.isNullOrEmpty() ){
            name = "未知蓝牙设备"
        }else{
            name = item?.bleName.toString()
        }
        holder.setText(R.id.deviceNameTv, name)
        holder.setText(R.id.deviceRssiTv, String.format("%ddBm", item?.rssi))
        holder.setText(R.id.deviceMacTv, item?.bleAddress)

    }
}