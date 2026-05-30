package com.example.fitprime.ui.reports

import android.content.res.ColorStateList
import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.example.fitprime.R
import com.example.fitprime.databinding.ItemWorkoutReportBinding
import com.google.android.material.chip.Chip
import java.text.SimpleDateFormat
import java.util.Locale

class ReportAdapter(private var reports: List<WorkoutReport>) :
    RecyclerView.Adapter<ReportAdapter.ReportViewHolder>() {

    class ReportViewHolder(val binding: ItemWorkoutReportBinding) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ReportViewHolder {
        val binding = ItemWorkoutReportBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ReportViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ReportViewHolder, position: Int) {
        val report = reports[position]
        val context = holder.itemView.context
        
        holder.binding.apply {
            tvReportRoutineName.text = report.routineName
            
            val dateFormat = SimpleDateFormat("MMM dd, hh:mm a", Locale("es", "ES"))
            tvReportDate.text = "${dateFormat.format(report.date)} • ${report.scheduledDurationMinutes} mins"
            
            val minutes = report.realDurationSeconds / 60
            val seconds = report.realDurationSeconds % 60
            tvRealTime.text = String.format(Locale.getDefault(), "%02d:%02d", minutes, seconds)
            
            // Status colors: Green for complete, Pinkish-Red for incomplete
            val statusColor = if (report.isCompleted) {
                ContextCompat.getColor(context, R.color.lime_green)
            } else {
                Color.parseColor("#FF6B6B")
            }
            
            vStatusIndicator.setBackgroundColor(statusColor)
            ivStatusIcon.imageTintList = ColorStateList.valueOf(statusColor)
            ivStatusIcon.setImageResource(if (report.isCompleted) R.drawable.ic_check_circle else R.drawable.ic_stop)
            
            // Clear and add exercise chips
            cgReportExercises.removeAllViews()
            report.exercises.take(3).forEach { exerciseName ->
                val chip = Chip(context).apply {
                    text = exerciseName
                    textSize = 10f
                    setTextColor(Color.WHITE)
                    setChipBackgroundColorResource(R.color.input_background)
                    chipStrokeWidth = 0f
                    isClickable = false
                    isCheckable = false
                    alpha = 0.8f
                    setPadding(0,0,0,0)
                }
                cgReportExercises.addView(chip)
            }
        }
    }

    override fun getItemCount() = reports.size

    fun updateData(newReports: List<WorkoutReport>) {
        reports = newReports
        notifyDataSetChanged()
    }
}