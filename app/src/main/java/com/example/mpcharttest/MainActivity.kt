package com.example.mpcharttest

import android.graphics.Color
import android.graphics.DashPathEffect
import android.os.Bundle
import android.view.ViewGroup
import android.widget.LinearLayout
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.animation.Crossfade
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.content.ContextCompat
import com.example.mpcharttest.ui.theme.MPChartTestTheme
import com.github.mikephil.charting.charts.BarChart
import com.github.mikephil.charting.charts.LineChart
import com.github.mikephil.charting.components.XAxis.XAxisPosition
import com.github.mikephil.charting.data.*
import com.github.mikephil.charting.formatter.IFillFormatter
import com.github.mikephil.charting.interfaces.datasets.IBarDataSet
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet
import com.github.mikephil.charting.utils.Utils

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MPChartTestTheme {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colors.background
                ) {
                    Column {
                        TrainBarChart()
                        TrainLineChart()
                    }
                }
            }
        }
    }

    @Composable
    fun TrainBarChart() {
        Crossfade(targetState = barChartData) { barChartData ->
            AndroidView(factory = { context ->
                BarChart(context).apply {
                    layoutParams = LinearLayout.LayoutParams(
                        ViewGroup.LayoutParams.MATCH_PARENT,
                        500
                    )
                }
            },
                modifier = Modifier
                    .wrapContentSize()
                    .padding(5.dp), update = {
                    updateBarChartWithData(it,barChartData)
                })
        }

    }

    @Composable
    fun TrainLineChart() {
        Crossfade(targetState = barChartData) { barChartData ->
            AndroidView(factory = { context ->
                LineChart(context).apply {
                    layoutParams = LinearLayout.LayoutParams(
                        ViewGroup.LayoutParams.MATCH_PARENT,
                        500
                    )
                }
            },
                modifier = Modifier
                    .wrapContentSize()
                    .padding(5.dp), update = {
                    updateLineChartWithData(it, barChartData)
                })
        }
    }

    fun updateLineChartWithData(
        chart: LineChart,
        data: List<BarChartData>
    ) {
        chart.setScaleEnabled(false)
        chart.xAxis.setDrawGridLines(false)
        chart.xAxis.position = XAxisPosition.BOTTOM
        chart.setDrawGridBackground(false)
        chart.axisLeft.isEnabled = false
        setLineChartData(chart)
        chart.invalidate()
    }

    fun updateBarChartWithData(
        chart: BarChart,
        barChartData: List<BarChartData>
    ) {
        val barChartRender = CustomBarChartRender(chart, chart.animator, chart.viewPortHandler)
        barChartRender.setRadius(Utils.convertDpToPixel(6F).toInt())
        chart.renderer = barChartRender
        chart.setScaleEnabled(false)
        chart.xAxis.setDrawGridLines(false)
        chart.xAxis.position = XAxisPosition.BOTTOM
        chart.setDrawGridBackground(false)
        chart.axisLeft.isEnabled = false

        setData(chart)
        chart.invalidate()
    }

    private fun setLineChartData(chart: LineChart) {
        val values = java.util.ArrayList<Entry>()

        for (i in 0 until 6) {
            val value: Float = (Math.random() * 5).toFloat() - 30
            values.add(Entry(i.toFloat(), value))
        }

        val set1: LineDataSet

        if (chart.data != null &&
            chart.data.dataSetCount > 0
        ) {
            set1 = chart.data.getDataSetByIndex(0) as LineDataSet
            set1.values = values
            set1.notifyDataSetChanged()
            chart.data.notifyDataChanged()
            chart.notifyDataSetChanged()
        } else {
            // create a dataset and give it a type
            set1 = LineDataSet(values, null)
            set1.setDrawIcons(false)

            // black lines and points
            set1.color = Color.parseColor("#33AED8")
            set1.setCircleColor(Color.parseColor("#33AED8"))

            // line thickness and point size
            set1.lineWidth = 3f
            set1.circleRadius = 5f

            // draw points as solid circles
            set1.setDrawCircleHole(false)

            // text size of values
            set1.valueTextSize = 0f

            // set the filled area
            set1.setDrawFilled(true)
            set1.fillFormatter =
                IFillFormatter { dataSet, dataProvider -> chart.axisLeft.axisMinimum }

            // set color of filled area
            if (Utils.getSDKInt() >= 18) {
                // drawables only supported on api level 18 and above
                val drawable = ContextCompat.getDrawable(this, R.drawable.fade_red)
                set1.fillDrawable = drawable
            } else {
                set1.fillColor = Color.BLACK
            }
            val dataSets = java.util.ArrayList<ILineDataSet>()
            dataSets.add(set1) // add the data sets

            // create a data object with the data sets
            val data = LineData(dataSets)

            // set data
            chart.data = data
        }
    }

    private fun setData(chart: BarChart) {
        val values: List<BarEntry> = barChartData.mapIndexed { index, barChartData ->
            BarEntry(index.toFloat(), barChartData.value ?: 0F, barChartData.workouts)
        }

        val set1: BarDataSet

        if (chart.data != null &&
            chart.data.dataSetCount > 0
        ) {
            set1 = chart.data.getDataSetByIndex(0) as BarDataSet
            set1.values = values
            chart.data.notifyDataChanged()
            chart.notifyDataSetChanged()
        } else {
            set1 = BarDataSet(values, null)
            set1.setDrawIcons(false)
            set1.setGradientColor(Color.parseColor("#20D36D"), Color.parseColor("#009ACE"))
            val dataSets = ArrayList<IBarDataSet>()
            dataSets.add(set1)
            val data = BarData(dataSets)
            data.setValueTextSize(0F)
            data.barWidth = 0.13f
            chart.data = data
        }
    }

    private val barChartData = listOf(
        BarChartData("14/7", 2.5F),
        BarChartData("21/4", 4F),
        BarChartData("28/7", 0.5F),
        BarChartData("4/8", 2F),
        BarChartData("11/8", 2.5F),
    )
}

data class BarChartData(
    var workouts: String?,
    var value: Float?
)

