package com.example.fitprime.ui.exercises

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.fitprime.databinding.ItemExerciseSequenceBinding

class ExerciseSequenceAdapter(private val exercises: List<Exercise>) :
    RecyclerView.Adapter<ExerciseSequenceAdapter.ExerciseViewHolder>() {

    class ExerciseViewHolder(val binding: ItemExerciseSequenceBinding) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ExerciseViewHolder {
        val binding = ItemExerciseSequenceBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ExerciseViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ExerciseViewHolder, position: Int) {
        val exercise = exercises[position]
        holder.binding.apply {
            tvExerciseName.text = exercise.name
            
            if (exercise.isRest) {
                tvExerciseSets.text = "1 Set"
                tvExerciseTime.text = "${exercise.durationSeconds}s Rest"
            } else {
                tvExerciseSets.text = "${exercise.sets} Sets"
                val minutes = exercise.durationSeconds / 60
                val seconds = exercise.durationSeconds % 60
                val timeStr = if (minutes > 0) {
                    if (seconds > 0) "${minutes}m ${seconds}s" else "${minutes}m"
                } else {
                    "${seconds}s"
                }
                tvExerciseTime.text = "$timeStr Work"
            }
        }
    }

    override fun getItemCount() = exercises.size
}