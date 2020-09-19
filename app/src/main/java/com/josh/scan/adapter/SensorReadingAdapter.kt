package com.josh.scan.adapter

import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.viewholder.BaseViewHolder
import com.josh.scan.R
import com.josh.scan.entity.ReadingsEntity

/**
 * description:
 *
 * author: josh.lu
 * created: 2020/9/20 2:15 AM
 * email:  1113799552@qq.com
 * version: v1.0
 */
class SensorReadingAdapter :
    BaseQuickAdapter<ReadingsEntity, BaseViewHolder>(R.layout.layout_sensor_readings_item) {
    override fun convert(holder: BaseViewHolder, item: ReadingsEntity) {
        holder.setText(R.id.sensorReadingsTv, item.readings)
        holder.setText(R.id.sensorNameTv, item.name)
    }
}