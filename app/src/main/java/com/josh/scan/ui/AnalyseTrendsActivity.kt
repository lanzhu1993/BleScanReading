package com.josh.scan.ui

import android.graphics.Color
import androidx.core.content.ContextCompat
import com.github.mikephil.charting.components.Legend
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.components.YAxis
import com.github.mikephil.charting.components.YAxis.AxisDependency
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.data.LineData
import com.github.mikephil.charting.data.LineDataSet
import com.github.mikephil.charting.formatter.ValueFormatter
import com.github.mikephil.charting.utils.ColorTemplate
import com.josh.scan.R
import com.josh.scan.base.BaseActivity
import com.josh.scan.utils.StatusBarUtil
import kotlinx.android.synthetic.main.activity_analyse_trends.*
import java.text.SimpleDateFormat
import java.util.*
import java.util.concurrent.TimeUnit
import kotlin.collections.ArrayList
import kotlin.collections.MutableMap.MutableEntry


/**
 * description:
 *
 * author: josh.lu
 * created: 2020/9/20 2:42 AM
 * email:  1113799552@qq.com
 * version: v1.0
 */
class AnalyseTrendsActivity :BaseActivity() {
    override fun getLayout() = R.layout.activity_analyse_trends

    override fun initView() {
        StatusBarUtil.setColor(this, ContextCompat.getColor(this, R.color.color_0A5566), 0)
        supportActionBar?.title = "数据分析"
        initLineChart()
        setData(100,50f)
    }

    override fun initData() {
    }


    private fun initLineChart() {
        // no description text
        analyseLineChart.getDescription().setEnabled(false)
        // enable touch gestures
        analyseLineChart.setTouchEnabled(true)

        analyseLineChart.setDragDecelerationFrictionCoef(0.9f)

        // enable scaling and dragging

        // enable scaling and dragging
        analyseLineChart.setDragEnabled(true)
        analyseLineChart.setScaleEnabled(true)
        analyseLineChart.setDrawGridBackground(false)
        analyseLineChart.setHighlightPerDragEnabled(true)

        // set an alternative background color

        // set an alternative background color
        analyseLineChart.setBackgroundColor(Color.WHITE)
        analyseLineChart.setViewPortOffsets(0f, 0f, 0f, 0f)

        // add data

        // add data
//        seekBarX.setProgress(100)

        // get the legend (only possible after setting data)

        // get the legend (only possible after setting data)
        val l: Legend = analyseLineChart.getLegend()
        l.isEnabled = false

        val xAxis: XAxis = analyseLineChart.getXAxis()
        xAxis.position = XAxis.XAxisPosition.TOP_INSIDE
//        xAxis.typeface = Typeface.tfLight
        xAxis.textSize = 10f
        xAxis.textColor = Color.WHITE
        xAxis.setDrawAxisLine(false)
        xAxis.setDrawGridLines(true)
        xAxis.textColor = Color.rgb(255, 192, 56)
        xAxis.setCenterAxisLabels(true)
        xAxis.granularity = 1f // one hour

        xAxis.valueFormatter = object : ValueFormatter() {
            private val mFormat: SimpleDateFormat = SimpleDateFormat("dd MMM HH:mm", Locale.CHINA)
            override fun getFormattedValue(value: Float): String {
                val millis: Long = TimeUnit.HOURS.toMillis(value.toLong())
                return mFormat.format(Date(millis))
            }
        }

        val leftAxis: YAxis = analyseLineChart.getAxisLeft()
        leftAxis.setPosition(YAxis.YAxisLabelPosition.INSIDE_CHART)
//        leftAxis.typeface = tfLight
        leftAxis.textColor = ColorTemplate.getHoloBlue()
        leftAxis.setDrawGridLines(true)
        leftAxis.isGranularityEnabled = true
        leftAxis.axisMinimum = 0f
        leftAxis.axisMaximum = 170f
        leftAxis.yOffset = -9f
        leftAxis.textColor = Color.rgb(255, 192, 56)

        val rightAxis: YAxis = analyseLineChart.getAxisRight()
        rightAxis.isEnabled = false
    }


    private fun setData(count: Int, range: Float) {

        // now in hours
        val now = TimeUnit.MILLISECONDS.toHours(System.currentTimeMillis())
        val values =  ArrayList<Entry>()

        // count = hours
        val to = now + count.toFloat()

        // increment by 1 hour
        var x = now.toFloat()
        while (x < to) {
            val y: Float = getRandom(range, 50f)
            values.add(Entry(x, y)) // add one entry per hour
            x++
        }

        // create a dataset and give it a type
        val set1 = LineDataSet(values, "DataSet 1")
        set1.axisDependency = AxisDependency.LEFT
        set1.color = ColorTemplate.getHoloBlue()
        set1.valueTextColor = ColorTemplate.getHoloBlue()
        set1.lineWidth = 1.5f
        set1.setDrawCircles(false)
        set1.setDrawValues(false)
        set1.fillAlpha = 65
        set1.fillColor = ColorTemplate.getHoloBlue()
        set1.highLightColor = Color.rgb(244, 117, 117)
        set1.setDrawCircleHole(false)

        // create a data object with the data sets
        val data = LineData(set1)
        data.setValueTextColor(Color.WHITE)
        data.setValueTextSize(9f)

        // set data
        analyseLineChart.setData(data)
    }

    fun getRandom(range: Float, start: Float): Float {
        return (Math.random() * range).toFloat() + start
    }
}