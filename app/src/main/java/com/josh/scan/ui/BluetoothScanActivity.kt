package com.josh.scan.ui

import android.util.Log
import android.view.MenuItem
import android.view.View
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.animation.SlideInBottomAnimation
import com.chad.library.adapter.base.listener.OnItemChildClickListener
import com.inuker.bluetooth.library.search.SearchRequest
import com.inuker.bluetooth.library.search.SearchResult
import com.inuker.bluetooth.library.search.response.SearchResponse
import com.josh.scan.R
import com.josh.scan.adapter.BluetoothItemAdapter
import com.josh.scan.base.BaseActivity
import com.josh.scan.manager.ClientManager
import com.josh.scan.utils.StatusBarUtil
import kotlinx.android.synthetic.main.activity_bluetooth_scan.*

/**
 * description:
 *
 * author: josh.lu
 * created: 2020/9/19 10:47 PM
 * email:  1113799552@qq.com
 * version: v1.0
 */
class BluetoothScanActivity : BaseActivity(), SearchResponse, OnItemChildClickListener {


    private val mAdapter by lazy {
        BluetoothItemAdapter()
    }

    private var mDeviceList = arrayListOf<SearchResult?>()

    override fun getLayout() = R.layout.activity_bluetooth_scan

    override fun initView() {
        StatusBarUtil.setColor(this, ContextCompat.getColor(this, R.color.color_0A5566), 0)
        supportActionBar?.title = "设备连接"
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        //初始化列表
        mScanRecyclerView.layoutManager = LinearLayoutManager(this)
        mScanRecyclerView.adapter = mAdapter
        mAdapter.adapterAnimation = SlideInBottomAnimation()
        mAdapter.addChildClickViewIds(R.id.deviceCl)
        searchDevice()

    }

    override fun initData() {
    }

    private fun searchDevice() {
        val request = SearchRequest.Builder()
            .searchBluetoothLeDevice(5000, 2).build()
        ClientManager.instance.getClient().search(request, this)
    }

    override fun initListener() {
        mAdapter.setOnItemChildClickListener(this)
    }

    override fun onSearchStopped() {

    }

    override fun onSearchStarted() {
        mDeviceList.clear();
        mAdapter.setNewInstance(mDeviceList)
    }

    override fun onDeviceFounded(device: SearchResult?) {
        runOnUiThread {
            if (!mDeviceList.contains(device)) {
                mDeviceList.add(device)
                mAdapter.setNewInstance(mDeviceList)
                mAdapter.notifyDataSetChanged()
            }

        }
    }

    override fun onSearchCanceled() {
        TODO("Not yet implemented")
    }

    override fun onPause() {
        super.onPause()
        ClientManager.instance.getClient().stopSearch()
    }

    override fun onItemChildClick(adapter: BaseQuickAdapter<*, *>, view: View, position: Int) {
        intent.putExtra("MAC",mDeviceList[position]?.address)
        setResult(MainActivity.DEVICE_RESULT_CODE,intent)
        finish()
    }


    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when(item.itemId){
            android.R.id.home ->{
                finish()
            }
        }
        return true
    }
}