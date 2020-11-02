package com.josh.scan.utils

import com.josh.scan.entity.SensorTable
import org.litepal.LitePal
import org.litepal.LitePal.findAll

/**
 * description:
 * author:         josh.lu
 * email:          1113799552@qq.com
 * createDate:     2020/9/21 11:59 AM
 * version:        v1.0
 */
object DbUtils {
    /**
     * 插入数据库
     * @param sensorTable
     * @return
     */
    fun insertSensor(sensorTable: SensorTable): Boolean {
        return sensorTable.save()
    }

    /**
     * 按照类型查询
     * @param sensorType 1-钠， 2-钾 ，3-钙，4-葡萄糖，5-乳酸，6-pH值
     * @return
     */
    fun querySensorTableByType(sensorType: Int): List<SensorTable> {
        return LitePal.where("type = ?", "$sensorType").order("create_time").find(SensorTable::class.java)
    }
}