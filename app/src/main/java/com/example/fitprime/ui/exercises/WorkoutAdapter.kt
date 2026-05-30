package com.example.fitprime.ui.exercises

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.fitprime.databinding.ItemWorkoutBinding

class WorkoutAdapter(
    private var workouts: List<Workout>,
    private val onWorkoutClick: (Workout) -> Unit
) : RecyclerView.Adapter<WorkoutAdapter.WorkoutViewHolder>() {

    private var filteredWorkouts: List<Workout> = workouts

    class WorkoutViewHolder(val binding: ItemWorkoutBinding) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): WorkoutViewHolder {
        val binding = ItemWorkoutBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return WorkoutViewHolder(binding)
    }

    override fun onBindViewHolder(holder: WorkoutViewHolder, position: Int) {
        val workout = filteredWorkouts[position]
        holder.binding.apply {
            tvWorkoutTitle.text = workout.title
            tvDuration.text = workout.duration
            tvCalories.text = workout.calories
            tvTag1.text = workout.tag1
            tvTag2.text = workout.tag2
            
            btnPlayWorkout.setOnClickListener {
                onWorkoutClick(workout)
            }
            
            root.setOnClickListener {
                onWorkoutClick(workout)
            }
        }
    }

    override fun getItemCount() = filteredWorkouts.size

    fun filter(query: String) {
        filteredWorkouts = if (query.isEmpty()) {
            workouts
        } else {
            workouts.filter { it.title.contains(query, ignoreCase = true) }
        }
        notifyDataSetChanged()
    }
}