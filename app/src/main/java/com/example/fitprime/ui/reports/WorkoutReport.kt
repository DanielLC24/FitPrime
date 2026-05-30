package com.example.fitprime.ui.reports

import java.util.Date

data class WorkoutReport(
    val id: String,
    val routineName: String,
    val date: Date,
    val scheduledDurationMinutes: Int,
    val realDurationSeconds: Int,
    val isCompleted: Boolean,
    val exercises: List<String>
)