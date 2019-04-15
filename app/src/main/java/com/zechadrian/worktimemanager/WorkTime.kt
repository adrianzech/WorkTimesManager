package com.zechadrian.worktimemanager

data class WorkTime(
    val id: Long,
    val date: String,
    val startTime: String,
    val endTime: String,
    val breakTime: String,
    val comment: String,
    val totalTime: String
)