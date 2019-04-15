package com.zechadrian.worktimemanager.database

import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper

class DatabaseHelper(context: Context) : SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION) {

    private val SQL_CREATE_ENTRIES = "CREATE TABLE " +
            "${Table.TABLE_NAME} (" +
            "${Table.ID} INTEGER PRIMARY KEY AUTOINCREMENT, " +
            "${Table.DATE} TEXT, " +
            "${Table.START_TIME} TEXT, " +
            "${Table.END_TIME} TEXT, " +
            "${Table.BREAK_TIME} TEXT, " +
            "${Table.COMMENT} TEXT, " +
            "${Table.TOTAL_TIME} TEXT" +
            ")"

    private val SQL_DELETE_ENTRIES = "DROP TABLE IF EXISTS ${Table.TABLE_NAME}"

    override fun onCreate(db: SQLiteDatabase) {
        db.execSQL(SQL_CREATE_ENTRIES)
    }

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        db.execSQL(SQL_DELETE_ENTRIES)
        onCreate(db)
    }
}