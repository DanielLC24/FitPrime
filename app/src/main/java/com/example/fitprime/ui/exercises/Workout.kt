package com.example.fitprime.ui.exercises

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class Workout(
    val title: String,
    val duration: String,
    val calories: String,
    val tag1: String,
    val tag2: String = "Intermedio",
    val description: String = "",
    val exercises: List<Exercise> = emptyList()
) : Parcelable

@Parcelize
data class Exercise(
    val name: String,
    val sets: Int,
    val durationSeconds: Int,
    val isRest: Boolean = false
) : Parcelable