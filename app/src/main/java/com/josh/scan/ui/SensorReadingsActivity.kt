package com.josh.scan.ui

import android.content.Intent
import android.view.MenuItem
import android.view.View
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.GridLayoutManager
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.animation.SlideInBottomAnimation
import com.chad.library.adapter.base.listener.OnItemChildClickListener
import com.josh.scan.R
import com.josh.scan.adapter.SensorReadingAdapter
import com.josh.scan.base.BaseActivity
import com.josh.scan.entity.ReadingsEntity
import com.josh.scan.utils.StatusBarUtil
import kotlinx.android.synthetic.main.activity_sensor_readings.*

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
        readingList.add(ReadingsEntity("钠", "暂无～"))
        readingList.add(ReadingsEntity("钾", "暂无～"))
        readingList.add(ReadingsEntity("钙", "暂无～"))
        readingList.add(ReadingsEntity("葡萄糖", "暂无～"))
        readingList.add(ReadingsEntity("乳酸", "暂无～"))
        readingList.add(ReadingsEntity("pH值", "暂无～"))
    }


    override fun initListener() {
        mAdapter.setOnItemChildClickListener(this)
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
}