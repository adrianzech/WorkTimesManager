package com.zechadrian.worktimemanager.database

import android.provider.BaseColumns

const val DATABASE_NAME = "work_times.db"
const val DATABASE_VERSION = 10

object Table : BaseColumns {
    const val TABLE_NAME = "WORK_TIMES"
    const val ID = "ID"
    const val DATE = "DATE"
    const val START_TIME = "START_TIME"
    const val END_TIME = "END_TIME"
    const val BREAK_TIME = "BREAK_TIME"
    const val COMMENT = "COMMENT"
    const val TOTAL_TIME = "TOTAL_TIME"
}