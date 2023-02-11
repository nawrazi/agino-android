package com.enterprise.agino.ui.home.dashboard

import android.os.Bundle
import android.text.InputType
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.util.component1
import androidx.core.util.component2
import androidx.fragment.app.Fragment
import com.enterprise.agino.R
import com.enterprise.agino.databinding.FragmentGraphScreenBinding
import com.enterprise.agino.utils.millisToDateString
import com.github.mikephil.charting.charts.BarChart
import com.github.mikephil.charting.charts.LineChart
import com.github.mikephil.charting.components.Legend
import com.github.mikephil.charting.data.BarData
import com.github.mikephil.charting.data.BarDataSet
import com.github.mikephil.charting.data.BarEntry
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter
import com.google.android.material.datepicker.*
import com.google.android.material.textfield.TextInputEditText
import java.util.*

class GraphFragment : Fragment() {
    private var _binding: FragmentGraphScreenBinding? = null
    private val binding get() = _binding!!
    
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentGraphScreenBinding.inflate(inflater, container, false)

        binding.dateInput.apply {
            inputType = InputType.TYPE_NULL
            setOnClickListener { showDatePicker(this) }
        }

        setGraphData(binding.chart1)
        setGraphData(binding.chart2)
        return binding.root
    }

    private fun setGraphData(chartLayout: Any) {
        val entries = mutableListOf<BarEntry>()
        val days = mutableListOf<String>()
        val daysOfWeek = listOf("Mon", "Tue", "Wed", "Thu", "Fri", "Sat", "Sun")

        val chart = when(chartLayout) {
            is BarChart -> chartLayout
            else -> chartLayout as LineChart
        }

        // create a dummy list of entries for each day of the week
        for (i in 0..100) {
            days.add(daysOfWeek[i % 7])
            entries.add(BarEntry(i.toFloat(), (Math.random() * 100).toFloat()))
        }

        // a dataset for our bar chart
        // TODO: using string resources for the label
        val set1 = BarDataSet(entries, "Measured Precipitation")
        // create a bar for each day of the week
        set1.setDrawValues(false)  // remove the actual values being displayed on top of the bars

        // TODO: change the color of the bars based on the value of the bar
        set1.setColors(
            resources.getColor(R.color.navy_blue, null),
            resources.getColor(R.color.light_grey, null)
        )

        // add the name of the days on top of x-axis
        chart.xAxis.valueFormatter = IndexAxisValueFormatter(days)

        // create a data object with the data sets
        val data = BarData(set1)

        chart.apply {
            xAxis.setDrawGridLines(false)
            xAxis.textSize = 15f
            extraTopOffset = 20f
            extraBottomOffset = 20f
            axisLeft.axisLineColor = resources.getColor(R.color.light_grey, null)
            xAxis.setDrawAxisLine(false)
            axisLeft.setDrawAxisLine(false)
            axisRight.isEnabled = false
            axisLeft.textColor = resources.getColor(android.R.color.darker_gray, null)
            description.isEnabled = false
            legend.horizontalAlignment = Legend.LegendHorizontalAlignment.CENTER
            xAxis.granularity = 1f  // set the minimum distance between labels
            legend.yOffset = 10f  // move the legend up a bit
        }

        // set data
        chart.data = data
        chart.setVisibleXRangeMaximum(7f)
    }

    private fun showDatePicker(editText: TextInputEditText) {
        val constraintsBuilder =
            CalendarConstraints.Builder()
                .setValidator(
                    CompositeDateValidator.allOf(
                        listOf(
                            DateValidatorPointForward.now(),
                            DateValidatorPointBackward.before(
                                Calendar.getInstance().also {
                                    it.add(Calendar.MONTH, 3)
                                }.timeInMillis
                            )
                        )
                    )
                )
                .build()

        val datePicker = MaterialDatePicker.Builder.dateRangePicker()
            .setCalendarConstraints(constraintsBuilder)
            .build()

        datePicker.addOnPositiveButtonClickListener {
            val (startDate, endDate) = it
            editText.setText(
                resources.getString(
                    R.string.date_range, millisToDateString(startDate), millisToDateString(endDate)
                )
            )
        }

        datePicker.show(requireActivity().supportFragmentManager, null)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}