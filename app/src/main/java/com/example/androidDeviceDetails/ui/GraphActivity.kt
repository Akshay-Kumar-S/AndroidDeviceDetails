package com.example.androidDeviceDetails.ui

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.example.androidDeviceDetails.R
import com.github.aachartmodel.aainfographics.aachartcreator.AAChartModel
import com.github.aachartmodel.aainfographics.aachartcreator.AAChartType
import com.github.aachartmodel.aainfographics.aachartcreator.AAChartView
import com.github.aachartmodel.aainfographics.aachartcreator.AASeriesElement
import com.github.mikephil.charting.charts.LineChart
import com.github.mikephil.charting.components.AxisBase
import com.github.mikephil.charting.components.LimitLine
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.components.XAxis.XAxisPosition
import com.github.mikephil.charting.components.YAxis
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.data.LineData
import com.github.mikephil.charting.data.LineDataSet
import com.github.mikephil.charting.formatter.ValueFormatter
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.*


class GraphActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_graph)
        val aaChartView = findViewById<AAChartView>(R.id.aa_chart_view)
        val aaChartModel: AAChartModel = AAChartModel()
            .chartType(AAChartType.Line)
            .title("Strength")
            .categories(arrayOf("1","2","3","4","5","6","7","8","10"))
            //  .yAxisLabelsEnabled(false)
            .backgroundColor("#efefef")
            .dataLabelsEnabled(false)
            .series(
                arrayOf(
                    AASeriesElement()
                        .name("Wifi")
                        .data(
                            arrayOf(
                                -83, -85, -81, -82, -80,-83,-84,-80,-81,-81
                            )
                        ),
                    AASeriesElement()
                        .name("Cellular")
                        .data(
                            arrayOf(-75, -70, -71, -72, -79)
                        )
                ),

                )

        aaChartView.aa_drawChartWithChartModel(aaChartModel)

        /*   val lineChart = findViewById<LineChart>(R.id.reportingChart)
           lineChart.setTouchEnabled(true)
           lineChart.setPinchZoom(true)
           val listData = ArrayList<Entry>()
           listData.add(Entry(0f, 81f))
           listData.add(Entry(1f, 82f))
           listData.add(Entry(2f, 83f))
           listData.add(Entry(3f, 81f))
           listData.add(Entry(4f, 84f))
           listData.add(Entry(5f, 82f))
           val lineDataSet = LineDataSet(listData, "bleh")
           lineDataSet.color = ContextCompat.getColor(this, R.color.gray_400)
           lineDataSet.valueTextColor = ContextCompat.getColor(this, android.R.color.white)
           val lineData = LineData(lineDataSet)
           lineChart.data = lineData
           lineChart.invalidate()*/
    }
}