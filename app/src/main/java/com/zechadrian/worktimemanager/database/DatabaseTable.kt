package com.zechadrian.worktimemanager.database

import android.content.ContentValues
import android.content.Context
import android.database.Cursor
import androidx.core.database.sqlite.transaction
import com.zechadrian.worktimemanager.WorkTime

class DatabaseTable(context: Context) {

    private val databaseHelper = DatabaseHelper(context)

    fun addData(workTime: WorkTime) {
        val db = databaseHelper.writableDatabase
        db.disableWriteAheadLogging()

        val values = ContentValues()
        with(values) {
            put(Table.DATE, workTime.date)
            put(Table.START_TIME, workTime.startTime)
            put(Table.END_TIME, workTime.endTime)
            put(Table.BREAK_TIME, workTime.breakTime)
            put(Table.COMMENT, workTime.comment)
            put(Table.TOTAL_TIME, workTime.totalTime)
        }

        return db.transaction {
            insert(Table.TABLE_NAME, null, values)
        }
    }

    fun updateData(workTime: WorkTime) {
        val db = databaseHelper.writableDatabase
        db.disableWriteAheadLogging()

        val values = ContentValues()
        with(values) {
            put(Table.DATE, workTime.date)
            put(Table.START_TIME, workTime.startTime)
            put(Table.END_TIME, workTime.endTime)
            put(Table.BREAK_TIME, workTime.breakTime)
            put(Table.COMMENT, workTime.comment)
            put(Table.TOTAL_TIME, workTime.totalTime)
        }

        return db.transaction {
            update(Table.TABLE_NAME, values, "ID=${workTime.id}", null)
        }
    }

    fun deleteData(id: Long?) {
        val db = databaseHelper.writableDatabase
        db.disableWriteAheadLogging()
        db.delete(Table.TABLE_NAME, "ID=$id", null)
    }

    fun getData(): MutableList<WorkTime> {
        val columns = arrayOf(
            Table.ID,
            Table.DATE,
            Table.START_TIME,
            Table.END_TIME,
            Table.BREAK_TIME,
            Table.COMMENT,
            Table.TOTAL_TIME
        )

        val db = databaseHelper.readableDatabase
        db.disableWriteAheadLogging()

        val cursor = db.query(Table.TABLE_NAME, columns, null, null, null, null, Table.DATE + " DESC")

        return parseDataFrom(cursor)
    }

    private fun parseDataFrom(cursor: Cursor): MutableList<WorkTime> {
        val workTimes = mutableListOf<WorkTime>()

        while (cursor.moveToNext()) {
            val id = cursor.getLong(Table.ID)
            val date = cursor.getString(Table.DATE)
            val startTime = cursor.getString(Table.START_TIME)
            val endTime = cursor.getString(Table.END_TIME)
            val breakTime = cursor.getString(Table.BREAK_TIME)
            val comment = cursor.getString(Table.COMMENT)
            val workTime = cursor.getString(Table.TOTAL_TIME)

            workTimes.add(WorkTime(id, date, startTime, endTime, breakTime, comment, workTime))
        }
        cursor.close()
        return workTimes
    }
}

private fun Cursor.getString(columnName: String) = getString(getColumnIndex(columnName))
private fun Cursor.getLong(columnName: String) = getLong(getColumnIndex(columnName))