package com.zechadrian.worktimemanager.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import androidx.navigation.findNavController
import androidx.navigation.fragment.navArgs
import com.zechadrian.worktimemanager.R
import com.zechadrian.worktimemanager.database.DatabaseTable
import kotlinx.android.synthetic.main.fragment_view_data.*

class ViewDataFragment : Fragment() {

    private val args: ViewDataFragmentArgs by navArgs()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_view_data, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        editText_date.setText(args.date)
        editText_startTime.setText(args.startTime)
        editText_endTime.setText(args.endTime)
        editText_breakTime.setText(args.breakTime)
        editText_totalTime.setText(args.totalTime)
        editText_comment.setText(args.comment)

        fab_edit.setOnClickListener {
            val action = ViewDataFragmentDirections.actionEditData(args.id, args.date, args.startTime, args.endTime, args.breakTime, args.totalTime, args.comment)
            view.findNavController().navigate(action)
        }

        button_delete.setOnClickListener {
            val builder = AlertDialog.Builder(view.context)
            builder.setTitle(R.string.delete_title)
            builder.setMessage(getString(R.string.are_you_sure, args.date))

            builder.setPositiveButton(R.string.yes) { _, _ ->
                deleteData(it)
            }
            builder.setNegativeButton(R.string.no) { dialog, _ ->
                dialog.dismiss()
            }

            val dialog: AlertDialog = builder.create()
            dialog.show()
        }
    }

    private fun deleteData(view: View) {
        DatabaseTable(view.context).deleteData(args.id)

        val action = AddDataFragmentDirections.actionListData(getString(R.string.work_time_deleted))
        view.findNavController().navigate(action)
    }
}