package com.josh.scan.utils;

import com.josh.scan.entity.BleRssiDevice;

import cn.com.heaton.blelibrary.ble.model.BleDevice;
import cn.com.heaton.blelibrary.ble.model.BleFactory;

/**
 * description:
 * author:         josh.lu
 * email:          1113799552@qq.com
 * createDate:     2020/10/23 12:01 PM
 * version:        v1.0
 */
public class BleFactoryUtils {

    public static BleFactory getBleFactory(){
        return new BleFactory() {
            @Override
            public BleDevice create(String address, String name) {
                return new BleRssiDevice(address, name);
            }
        };
    }
}
