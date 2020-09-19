package com.josh.scan.adapter

import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.viewholder.BaseViewHolder
import com.inuker.bluetooth.library.search.SearchResult
import com.josh.scan.R

/**
 * description: 蓝牙条目适配器
 *
 * author: josh.lu
 * created: 2020/9/19 10:46 AM
 * email:  1113799552@qq.com
 * version: v1.0
 */
class BluetoothItemAdapter :
    BaseQuickAdapter<SearchResult?, BaseViewHolder>(R.layout.layout_device_list_item) {
    override fun convert(holder: BaseViewHolder, item: SearchResult?) {
        var name = ""
        if(item?.name == "NULL" ){
            name = "未知蓝牙设备"
        }else{
            name = item?.name.toString()
        }
        holder.setText(R.id.deviceNameTv, name)
        holder.setText(R.id.deviceRssiTv, String.format("Rssi: %d", item?.rssi))
        holder.setText(R.id.deviceMacTv, item?.address)

    }
}