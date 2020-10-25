package com.josh.scan.ui

import android.view.MenuItem
import androidx.core.content.ContextCompat
import com.anychart.AnyChart
import com.anychart.chart.common.dataentry.DataEntry
import com.anychart.chart.common.dataentry.ValueDataEntry
import com.anychart.data.Set
import com.anychart.enums.Anchor
import com.anychart.enums.MarkerType
import com.anychart.enums.TooltipPositionMode
import com.anychart.graphics.vector.Stroke
import com.josh.scan.R
import com.josh.scan.base.BaseActivity
import com.josh.scan.utils.StatusBarUtil
import kotlinx.android.synthetic.main.activity_analyse_trends.*
import java.util.ArrayList


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
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        val cartesian = AnyChart.line()
        cartesian.animation(true)

        cartesian.padding(10.0, 20.0, 5.0, 20.0)

        cartesian.crosshair().enabled(false)
        cartesian.crosshair()
            .yLabel(false) // TODO ystroke
            .yStroke(
                null as Stroke?,
                null,
                null,
                null as String?,
                null as String?
            )

        cartesian.tooltip().positionMode(TooltipPositionMode.POINT)

        val seriesData: MutableList<DataEntry> = ArrayList()
        seriesData.add(ValueDataEntry("1986", 3.6))
        seriesData.add(ValueDataEntry("1987", 7.1))
        seriesData.add(ValueDataEntry("1988", 8.5))
        seriesData.add(ValueDataEntry("1989", 9.2))
        seriesData.add(ValueDataEntry("1990", 10.1))
        seriesData.add(ValueDataEntry("1991", 11.6))
        seriesData.add(ValueDataEntry("1992", 16.4))
        seriesData.add(ValueDataEntry("1993", 18.0))
        seriesData.add(ValueDataEntry("1994", 13.2))
        seriesData.add(ValueDataEntry("1995", 12.0))
        seriesData.add(ValueDataEntry("1996", 3.2))
        seriesData.add(ValueDataEntry("1997", 4.1))
        seriesData.add(ValueDataEntry("1998", 6.3))
        seriesData.add(ValueDataEntry("1999", 9.4))
        seriesData.add(ValueDataEntry("2000", 11.5))
        seriesData.add(ValueDataEntry("2001", 13.5))
        seriesData.add(ValueDataEntry("2002", 14.8))
        seriesData.add(ValueDataEntry("2003", 16.6))
        seriesData.add(ValueDataEntry("2004", 18.1))
        seriesData.add(ValueDataEntry("2005", 17.0))
        seriesData.add(ValueDataEntry("2006", 16.6))
        seriesData.add(ValueDataEntry("2007", 14.1))
        seriesData.add(ValueDataEntry("2008", 15.7))
        seriesData.add(ValueDataEntry("2009", 12.0))

        val set = Set.instantiate()
        set.data(seriesData)
        val series1Mapping = set.mapAs("{ x: 'x', value: 'value' }")

        val series1 = cartesian.line(series1Mapping)
        series1.name("Brandy")
        series1.hovered().markers().enabled(false)
        series1.hovered().markers()
            .type(MarkerType.CIRCLE)
            .size(4.0)
        series1.tooltip()
            .position("right")
            .anchor(Anchor.LEFT_CENTER)
            .offsetX(5.0)
            .offsetY(5.0)



        cartesian.legend().enabled(true)
        cartesian.legend().fontSize(13.0)
        cartesian.legend().padding(0.0, 0.0, 10.0, 0.0)
        cartesian.credits().enabled(false)
        cartesian.credits().text("")
        anyChartView.setChart(cartesian)
    }


    override fun initData() {
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