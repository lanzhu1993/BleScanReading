package com.josh.scan.entity;

import org.litepal.LitePal;
import org.litepal.crud.LitePalSupport;

/**
 * description:    数据库表
 * author:         josh.lu
 * email:          1113799552@qq.com
 * createDate:     2020/9/21 11:27 AM
 * version:        v1.0
 */
public class SensorTable extends LitePalSupport {


    private int type;
    private String value;
    private long create_time;

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public long getCreate_time() {
        return create_time;
    }

    public void setCreate_time(long create_time) {
        this.create_time = create_time;
    }

}
