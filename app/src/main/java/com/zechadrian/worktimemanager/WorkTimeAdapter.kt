package com.zechadrian.worktimemanager

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.Navigation
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.snackbar.Snackbar
import com.zechadrian.worktimemanager.database.DatabaseTable
import com.zechadrian.worktimemanager.fragments.ListDataFragmentDirections
import kotlinx.android.synthetic.main.single_card.view.*

class WorkTimeAdapter(private val workTimes: MutableList<WorkTime>) : RecyclerView.Adapter<WorkTimeAdapter.WorkTimeViewHolder>() {

    private var removedPosition: Int = 0
    private var removedItem: WorkTime? = null

    class WorkTimeViewHolder(val card: View) : RecyclerView.ViewHolder(card)

    override fun onBindViewHolder(holder: WorkTimeViewHolder, index: Int) {
        val workTime = workTimes[index]

        with(holder.card) {
            textView_date.text = workTime.date
            textView_workTime.text = workTime.totalTime
            setOnClickListener {
                val action = ListDataFragmentDirections.actionViewData(workTime.id, workTime.date, workTime.startTime, workTime.endTime, workTime.breakTime, workTime.totalTime, workTime.comment)
                val navController = Navigation.findNavController(it)
                navController.navigate(action)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): WorkTimeViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.single_card, parent, false)
        return WorkTimeViewHolder(view)
    }

    fun removeItem(viewHolder: RecyclerView.ViewHolder) {
        removedPosition = viewHolder.adapterPosition
        removedItem = workTimes[viewHolder.adapterPosition]

        workTimes.removeAt(viewHolder.adapterPosition)
        DatabaseTable(viewHolder.itemView.context).deleteData(removedItem!!.id)
        notifyItemRemoved(viewHolder.adapterPosition)

        Snackbar.make(viewHolder.itemView, R.string.work_time_deleted, Snackbar.LENGTH_LONG).setAction("UNDO") {
            workTimes.add(removedPosition, removedItem!!)
            DatabaseTable(viewHolder.itemView.context).addData(removedItem!!)
            notifyItemInserted(removedPosition)
        }.show()
    }

    override fun getItemCount() = workTimes.size
}