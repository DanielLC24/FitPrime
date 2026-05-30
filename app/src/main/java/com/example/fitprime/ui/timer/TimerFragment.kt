package com.example.fitprime.ui.timer

import android.os.Bundle
import android.os.CountDownTimer
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.example.fitprime.R
import com.example.fitprime.databinding.FragmentTimerBinding
import java.util.Locale

import android.os.Handler
import android.os.Looper
import androidx.navigation.fragment.findNavController
import com.example.fitprime.data.DatabaseHelper
import com.example.fitprime.ui.exercises.Exercise
import com.example.fitprime.ui.exercises.Workout

class TimerFragment : Fragment() {

    private var _binding: FragmentTimerBinding? = null
    private val binding get() = _binding!!

    private var countDownTimer: CountDownTimer? = null
    private var isTimerRunning = false
    private var timeLeftInMillis: Long = 45000L
    private var timerDurationInMillis: Long = 45000L
    
    // Nueva lógica de rutina
    private var workout: Workout? = null
    private var currentExerciseIndex = 0
    private var currentSet = 1
    private var isRoutineMode = false
    private var totalRealTimeSeconds = 0
    private lateinit var dbHelper: DatabaseHelper

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentTimerBinding.inflate(inflater, container, false)
        dbHelper = DatabaseHelper(requireContext())
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Verificar si venimos de una rutina
        workout = arguments?.getParcelable("workout")
        isRoutineMode = workout != null

        setupUI()
        setupListeners()
        
        if (isRoutineMode) {
            totalRealTimeSeconds = 0
            loadExercise(0)
        } else {
            updateTimerText()
            updateProgress()
            updateSetsText()
        }
    }

    private fun loadExercise(index: Int, autoStart: Boolean = false) {
        workout?.let { w ->
            if (index < w.exercises.size) {
                currentExerciseIndex = index
                val exercise = w.exercises[index]

                timerDurationInMillis = exercise.durationSeconds.toLong() * 1000
                timeLeftInMillis = timerDurationInMillis
                currentSet = 1

                binding.tvStatus.text = if (exercise.isRest) "Descanso" else exercise.name
                binding.tvWorkTimeValue.text = "${exercise.durationSeconds}s"
                binding.sliderWorkTime.value = exercise.durationSeconds.toFloat()

                updateTimerText()
                updateProgress()
                updateSetsText()

                if (autoStart) {
                    startTimer()
                } else {
                    // No auto start
                }
            } else {
                // Rutina finalizada exitosamente
                binding.tvStatus.text = "¡Rutina Completada!"
                saveRoutineReport(true)
                resetTimer()

                // Regresar a la pestaña de ejercicios después de 2 segundos
                Handler(Looper.getMainLooper()).postDelayed({
                    if (isAdded) {
                        findNavController().popBackStack(R.id.navigation_exercises, false)
                    }
                }, 2000)
            }
        }
    }

    private fun saveRoutineReport(isCompleted: Boolean) {
        workout?.let { w ->
            val scheduledMinutes = w.duration.replace(Regex("[^0-9]"), "").toIntOrNull() ?: 0
            val exerciseNames = w.exercises.filter { !it.isRest }.map { it.name }.distinct()
            
            dbHelper.saveReport(
                routineName = w.title,
                scheduledMin = scheduledMinutes,
                realSec = totalRealTimeSeconds,
                isCompleted = isCompleted,
                exercises = exerciseNames
            )
        }
    }

    private fun setupUI() {
        if (!isRoutineMode) {
            binding.sliderWorkTime.value = (timerDurationInMillis / 1000).toFloat()
            binding.tvWorkTimeValue.text = "${(timerDurationInMillis / 1000)}s"
            binding.tvStatus.text = "Al fallo o muere flaco"
        }
    }

    private fun setupListeners() {
        binding.btnPlayPause.setOnClickListener {
            if (isTimerRunning) {
                pauseTimer()
            } else {
                startTimer()
            }
        }

        binding.btnReset.setOnClickListener {
            resetTimer()
        }

        binding.btnSkip.setOnClickListener {
            if (isRoutineMode) {
                handleSkipRoutine()
            } else {
                skipSet()
            }
        }

        binding.sliderWorkTime.addOnChangeListener { _, value, fromUser ->
            if (fromUser && !isRoutineMode) {
                timerDurationInMillis = value.toLong() * 1000
                timeLeftInMillis = timerDurationInMillis
                updateTimerText()
                updateProgress()
                binding.tvWorkTimeValue.text = "${value.toInt()}s"
            }
        }
    }

    private fun handleSkipRoutine() {
        countDownTimer?.cancel()
        isTimerRunning = false
        binding.btnPlayPause.setImageResource(R.drawable.ic_play)
        
        workout?.let { w ->
            val exercise = w.exercises[currentExerciseIndex]
            if (currentSet < exercise.sets) {
                // Saltar al siguiente set del mismo ejercicio
                currentSet++
                timeLeftInMillis = timerDurationInMillis
                updateTimerText()
                updateProgress()
                updateSetsText()
                startTimer() // Continuar automáticamente
            } else {
                // Saltar al siguiente ejercicio
                loadExercise(currentExerciseIndex + 1, autoStart = true)
            }
        }
    }

    private fun startTimer() {
        countDownTimer = object : CountDownTimer(timeLeftInMillis, 1000) {
            override fun onTick(millisUntilFinished: Long) {
                timeLeftInMillis = millisUntilFinished
                if (isRoutineMode) totalRealTimeSeconds++
                updateTimerText()
                updateProgress()
            }

            override fun onFinish() {
                if (isRoutineMode) totalRealTimeSeconds++
                isTimerRunning = false
                binding.btnPlayPause.setImageResource(R.drawable.ic_play)
                timeLeftInMillis = 0
                updateTimerText()
                updateProgress()
                
                if (isRoutineMode) {
                    onExerciseFinish()
                }
            }
        }.start()

        isTimerRunning = true
        binding.btnPlayPause.setImageResource(R.drawable.ic_pause)
    }

    private fun onExerciseFinish() {
        workout?.let { w ->
            val exercise = w.exercises[currentExerciseIndex]
            if (currentSet < exercise.sets) {
                currentSet++
                timeLeftInMillis = timerDurationInMillis
                updateTimerText()
                updateProgress()
                updateSetsText()
                startTimer() // Continuar automáticamente con el siguiente set
            } else {
                loadExercise(currentExerciseIndex + 1, autoStart = true)
            }
        }
    }

    private fun nextStep() {
        // Esta función ya no es necesaria con handleSkipRoutine y loadExercise(index, true)
        handleSkipRoutine()
    } 

    private fun pauseTimer() {
        countDownTimer?.cancel()
        isTimerRunning = false
        binding.btnPlayPause.setImageResource(R.drawable.ic_play)
    }

    private fun resetTimer() {
        countDownTimer?.cancel()
        isTimerRunning = false
        timeLeftInMillis = timerDurationInMillis
        updateTimerText()
        updateProgress()
        binding.btnPlayPause.setImageResource(R.drawable.ic_play)
    }

    private fun skipSet() {
        currentSet++
        updateSetsText()
        resetTimer()
    }

    private fun updateTimerText() {
        val totalSeconds = (timeLeftInMillis + 999) / 1000
        val minutes = totalSeconds / 60
        val seconds = totalSeconds % 60
        val timeFormatted = String.format(Locale.getDefault(), "%02d:%02d", minutes, seconds)
        binding.tvTimer.text = timeFormatted
    }

    private fun updateProgress() {
        if (timerDurationInMillis > 0) {
            val progress = (timeLeftInMillis.toFloat() / timerDurationInMillis.toFloat() * 100).toInt()
            binding.progressTimer.progress = progress
        }
    }

    private fun updateSetsText() {
        val totalSets = workout?.exercises?.getOrNull(currentExerciseIndex)?.sets ?: 8
        binding.tvSets.text = "Set $currentSet / $totalSets"
    }

    override fun onDestroyView() {
        super.onDestroyView()
        countDownTimer?.cancel()
        _binding = null
    }
}