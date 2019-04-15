package com.zechadrian.worktimemanager.fragments

import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import androidx.fragment.app.Fragment
import androidx.navigation.findNavController
import androidx.preference.PreferenceManager
import com.zechadrian.worktimemanager.R
import com.zechadrian.worktimemanager.WorkTime
import com.zechadrian.worktimemanager.database.DatabaseTable
import kotlinx.android.synthetic.main.fragment_add_data.*
import java.text.DateFormat
import java.text.SimpleDateFormat
import java.util.*

class AddDataFragment : Fragment() {

    private val calendar = Calendar.getInstance()
    private val dateFormat = SimpleDateFormat.getDateInstance(DateFormat.LONG)
    private val timeFormat = SimpleDateFormat.getTimeInstance(DateFormat.SHORT)

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_add_data, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val currentDate = dateFormat.format(calendar.time)
        val currentTime = timeFormat.format(calendar.time)

        val sharedPreference = PreferenceManager.getDefaultSharedPreferences(view.context)
        // TODO: Better default values
        val startTime = sharedPreference.getString("start_time", "08:00")
        val breakTime = sharedPreference.getString("break_time", "01:00")

        editText_date.setText(currentDate)
        editText_date.setOnClickListener { datePicker(editText_date) }

        editText_startTime.setText(startTime)
        editText_startTime.setOnClickListener { timePicker(editText_startTime) }

        editText_endTime.setText(currentTime)
        editText_endTime.setOnClickListener { timePicker(editText_endTime) }

        // TODO: Remove AM/PM
        editText_breakTime.setText(breakTime)
        editText_breakTime.setOnClickListener { timePicker(editText_breakTime) }

        fab_add.setOnClickListener { addData(view) }
    }

    private fun addData(view: View) {
        val date = editText_date.text.toString()
        val startTime = editText_startTime.text.toString()
        val endTime = editText_endTime.text.toString()
        val breakTime = editText_breakTime.text.toString()
        val comment = editText_comment.text.toString()

        val workTime = WorkTime(0, date, startTime, endTime, breakTime, comment, calculateTotalTime(startTime, endTime, breakTime))
        DatabaseTable(view.context).addData(workTime)

        val action = AddDataFragmentDirections.actionListData(getString(R.string.work_time_added))
        view.findNavController().navigate(action)
    }

    private fun calculateTotalTime(startTime: String, endTime: String, breakTime: String): String {
        timeFormat.timeZone = TimeZone.getTimeZone("GMT")

        val startTimeParsed = timeFormat.parse(startTime)
        val endTimeParsed = timeFormat.parse(endTime)
        val breakTimeParsed = timeFormat.parse(breakTime)

        val difference = endTimeParsed.time - startTimeParsed.time - breakTimeParsed.time
        return timeFormat.format(difference)
    }

    private fun datePicker(editText: EditText) {
        val dateSetListener = DatePickerDialog.OnDateSetListener { _, year, month, dayOfMonth ->
            calendar.set(Calendar.YEAR, year)
            calendar.set(Calendar.MONTH, month)
            calendar.set(Calendar.DAY_OF_MONTH, dayOfMonth)

            editText.setText(dateFormat.format(calendar.time))
        }
        context?.let {
            DatePickerDialog(
                it, dateSetListener,
                calendar.get(Calendar.YEAR),
                calendar.get(Calendar.MONTH),
                calendar.get(Calendar.DAY_OF_MONTH)
            ).show()
        }
    }

    private fun timePicker(editText: EditText) {
        val timeSetListener = TimePickerDialog.OnTimeSetListener { _, hour, minute ->
            calendar.set(Calendar.HOUR_OF_DAY, hour)
            calendar.set(Calendar.MINUTE, minute)

            editText.setText(timeFormat.format(calendar.time))
        }
        context?.let {
            TimePickerDialog(
                it,
                timeSetListener,
                calendar.get(Calendar.HOUR_OF_DAY),
                calendar.get(Calendar.MINUTE),
                true
            ).show()
        }
    }
}