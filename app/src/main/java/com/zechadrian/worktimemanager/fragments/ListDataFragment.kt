package com.zechadrian.worktimemanager.fragments

import android.graphics.Canvas
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.navigation.Navigation
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.snackbar.Snackbar
import com.zechadrian.worktimemanager.R
import com.zechadrian.worktimemanager.WorkTimeAdapter
import com.zechadrian.worktimemanager.database.DatabaseTable
import kotlinx.android.synthetic.main.fragment_list_data.*

class ListDataFragment : Fragment() {

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_list_data, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setHasOptionsMenu(true)

        arguments?.let {
            val safeArgs = ListDataFragmentArgs.fromBundle(it)
            Snackbar.make(view, safeArgs.message.toString(), Snackbar.LENGTH_LONG).show()
        }

        fab_new.setOnClickListener(Navigation.createNavigateOnClickListener(R.id.action_addData))

        val adapter = WorkTimeAdapter(DatabaseTable(view.context).getData())

        recyclerView.setHasFixedSize(true)
        recyclerView.layoutManager = LinearLayoutManager(context)
        recyclerView.adapter = adapter
        recyclerView.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                if (dy > 0) {
                    fab_new.hide()
                } else {
                    fab_new.show()
                }
                super.onScrolled(recyclerView, dx, dy)
            }
        })

        val deleteIcon: Drawable = ContextCompat.getDrawable(view.context, R.drawable.ic_delete)!!
        val swipeBackground = ColorDrawable(Color.parseColor("#D32F2F"))

        val itemTouchHelperCallback =
            object : ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT or ItemTouchHelper.RIGHT) {
                override fun onMove(recyclerView: RecyclerView, viewHolder: RecyclerView.ViewHolder, target: RecyclerView.ViewHolder): Boolean {
                    return false
                }

                override fun onSwiped(viewHolder: RecyclerView.ViewHolder, position: Int) {
                    adapter.removeItem(viewHolder)
                }

                override fun onChildDraw(c: Canvas, recyclerView: RecyclerView, viewHolder: RecyclerView.ViewHolder, dX: Float, dY: Float, actionState: Int, isCurrentlyActive: Boolean) {
                    val itemView = viewHolder.itemView
                    val iconMargin = (itemView.height - deleteIcon.intrinsicHeight * 2) / 2

                    if (dX > 0) {
                        swipeBackground.setBounds(itemView.left, itemView.top, dX.toInt(), itemView.bottom)
                        deleteIcon.setBounds(itemView.left + iconMargin, itemView.top + iconMargin, itemView.left + iconMargin + deleteIcon.intrinsicWidth * 2, itemView.bottom - iconMargin)
                    } else {
                        swipeBackground.setBounds(itemView.right + dX.toInt(), itemView.top, itemView.right, itemView.bottom)
                        deleteIcon.setBounds(itemView.right - iconMargin - deleteIcon.intrinsicWidth * 2, itemView.top + iconMargin, itemView.right - iconMargin, itemView.bottom - iconMargin)
                    }

                    swipeBackground.draw(c)
                    deleteIcon.draw(c)

                    super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive)
                }
            }

        val itemTouchHelper = ItemTouchHelper(itemTouchHelperCallback)
        itemTouchHelper.attachToRecyclerView(recyclerView)
    }
}