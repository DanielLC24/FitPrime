package com.example.fitprime.ui.reports

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.fitprime.R
import com.example.fitprime.databinding.FragmentReportsBinding
import com.google.android.material.chip.Chip
import java.util.*

import com.example.fitprime.data.DatabaseHelper

class ReportsFragment : Fragment() {

    private var _binding: FragmentReportsBinding? = null
    private val binding get() = _binding!!
    private lateinit var reportAdapter: ReportAdapter
    private var allReports = mutableListOf<WorkoutReport>()
    private lateinit var dbHelper: DatabaseHelper

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentReportsBinding.inflate(inflater, container, false)
        dbHelper = DatabaseHelper(requireContext())
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        
        loadReports()
        setupRecyclerView()
        setupFilters()
        updateUI("day")
    }

    private fun loadReports() {
        allReports = dbHelper.getAllReports().toMutableList()
    }

    private fun setupRecyclerView() {
        reportAdapter = ReportAdapter(emptyList())
        binding.rvReports.apply {
            layoutManager = LinearLayoutManager(context)
            adapter = reportAdapter
        }
    }

    private fun setupFilters() {
        binding.cgTimeFilter.setOnCheckedStateChangeListener { group, checkedIds ->
            val checkedId = checkedIds.firstOrNull() ?: return@setOnCheckedStateChangeListener
            
            // Reset all chips to unselected style
            for (i in 0 until group.childCount) {
                val chip = group.getChildAt(i) as Chip
                chip.setChipBackgroundColorResource(R.color.input_background)
                chip.setTextColor(ContextCompat.getColor(requireContext(), R.color.white))
            }

            // Style selected chip
            val selectedChip = group.findViewById<Chip>(checkedId)
            selectedChip.setChipBackgroundColorResource(R.color.turquoise) // Using turquoise as requested
            selectedChip.setTextColor(ContextCompat.getColor(requireContext(), R.color.black))

            val filter = when (checkedId) {
                R.id.chip_day -> "day"
                R.id.chip_week -> "week"
                R.id.chip_month -> "month"
                R.id.chip_year -> "year"
                else -> "day"
            }
            updateUI(filter)
        }
        
        // Initial style for day chip
        binding.chipDay.setChipBackgroundColorResource(R.color.turquoise)
        binding.chipDay.setTextColor(ContextCompat.getColor(requireContext(), R.color.black))
    }

    private fun updateUI(filter: String) {
        val calendar = Calendar.getInstance()
        val now = calendar.timeInMillis
        
        // Reset calendar to start of day for accurate comparison
        calendar.set(Calendar.HOUR_OF_DAY, 0)
        calendar.set(Calendar.MINUTE, 0)
        calendar.set(Calendar.SECOND, 0)
        calendar.set(Calendar.MILLISECOND, 0)
        val startOfDay = calendar.timeInMillis

        val filtered = when (filter) {
            "day" -> allReports.filter { it.date.time >= startOfDay }
            "week" -> {
                calendar.set(Calendar.DAY_OF_WEEK, calendar.firstDayOfWeek)
                val startOfWeek = calendar.timeInMillis
                allReports.filter { it.date.time >= startOfWeek }
            }
            "month" -> {
                calendar.set(Calendar.DAY_OF_MONTH, 1)
                val startOfMonth = calendar.timeInMillis
                allReports.filter { it.date.time >= startOfMonth }
            }
            "year" -> {
                calendar.set(Calendar.DAY_OF_YEAR, 1)
                val startOfYear = calendar.timeInMillis
                allReports.filter { it.date.time >= startOfYear }
            }
            else -> allReports
        }

        reportAdapter.updateData(filtered)

        // Update Stats
        val totalSessions = filtered.size
        val completedSessions = filtered.count { it.isCompleted }
        val totalSeconds = filtered.sumOf { it.realDurationSeconds }
        val completionRate = if (totalSessions > 0) (completedSessions * 100 / totalSessions) else 0

        binding.tvTotalSessions.text = totalSessions.toString()
        binding.tvTotalTime.text = "${totalSeconds / 3600}h ${(totalSeconds % 3600) / 60}m"
        binding.tvCompletionPercent.text = "$completionRate%"
        binding.pbTimeGoal.progress = completionRate
        
        binding.tvRecentSessionsTitle.text = "Sesiones Recientes (${filtered.size} items)"
        
        binding.tvTotalTime.setTextColor(ContextCompat.getColor(requireContext(), R.color.turquoise))
        binding.tvCompletionPercent.setTextColor(ContextCompat.getColor(requireContext(), R.color.turquoise))
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}