package com.josh.scan.entity

import cn.com.heaton.blelibrary.ble.model.BleDevice
import cn.com.heaton.blelibrary.ble.model.ScanRecord

class BleRssiDevice(address: String?, name: String?) :
    BleDevice(address, name) {
    private var scanRecord: ScanRecord? = null
    var rssi = 0
    var rssiUpdateTime: Long = 0
    override fun getScanRecord(): ScanRecord {
        return scanRecord!!
    }

    override fun setScanRecord(scanRecord: ScanRecord) {
        this.scanRecord = scanRecord
    }

}