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
import androidx.navigation.fragment.navArgs
import com.zechadrian.worktimemanager.R
import com.zechadrian.worktimemanager.WorkTime
import com.zechadrian.worktimemanager.database.DatabaseTable
import kotlinx.android.synthetic.main.fragment_edit_data.*
import java.text.DateFormat
import java.text.SimpleDateFormat
import java.util.*

class EditDataFragment : Fragment() {

    private val args: EditDataFragmentArgs by navArgs()
    private val calendar = Calendar.getInstance()
    private val dateFormat = SimpleDateFormat.getDateInstance(DateFormat.LONG)
    private val timeFormat = SimpleDateFormat.getTimeInstance(DateFormat.SHORT)

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_edit_data, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        editText_date.setOnClickListener { datePicker(editText_date) }
        editText_date.setText(args.date)

        editText_startTime.setOnClickListener { timePicker(editText_startTime) }
        editText_startTime.setText(args.startTime)

        editText_endTime.setOnClickListener { timePicker(editText_endTime) }
        editText_endTime.setText(args.endTime)

        editText_breakTime.setOnClickListener { timePicker(editText_breakTime) }
        editText_breakTime.setText(args.breakTime)

        editText_comment.setText(args.comment)

        fab_save.setOnClickListener { updateData(view) }
    }

    private fun updateData(view: View) {
        val date = editText_date.text.toString()
        val startTime = editText_startTime.text.toString()
        val endTime = editText_endTime.text.toString()
        val breakTime = editText_breakTime.text.toString()
        val comment = editText_comment.text.toString()

        val workTime = WorkTime(args.id, date, startTime, endTime, breakTime, comment, calculateTotalTime(startTime, endTime, breakTime))

        DatabaseTable(view.context).updateData(workTime)

        val action = AddDataFragmentDirections.actionListData(getString(R.string.work_time_edited))
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
                it,
                dateSetListener,
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
