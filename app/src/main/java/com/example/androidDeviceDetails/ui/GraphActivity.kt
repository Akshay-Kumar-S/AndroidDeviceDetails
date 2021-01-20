package com.example.androidDeviceDetails.ui

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.androidDeviceDetails.R
import com.github.aachartmodel.aainfographics.aachartcreator.AAChartModel
import com.github.aachartmodel.aainfographics.aachartcreator.AAChartType
import com.github.aachartmodel.aainfographics.aachartcreator.AAChartView
import com.github.aachartmodel.aainfographics.aachartcreator.AASeriesElement


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

    }
}