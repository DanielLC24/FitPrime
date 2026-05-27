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

class TimerFragment : Fragment() {

    private var _binding: FragmentTimerBinding? = null
    private val binding get() = _binding!!

    private var countDownTimer: CountDownTimer? = null
    private var isTimerRunning = false
    private var timeLeftInMillis: Long = 45000L
    private var timerDurationInMillis: Long = 45000L
    
    private var currentSet = 3
    private val totalSets = 8

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentTimerBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupUI()
        setupListeners()
        updateTimerText()
        updateProgress()
        updateSetsText()
    }

    private fun setupUI() {
        // Inicializar el valor del slider y el texto correspondiente
        binding.sliderWorkTime.value = (timerDurationInMillis / 1000).toFloat()
        binding.tvWorkTimeValue.text = "${(timerDurationInMillis / 1000)}s"
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
            skipSet()
        }

        binding.sliderWorkTime.addOnChangeListener { _, value, fromUser ->
            if (fromUser) {
                timerDurationInMillis = value.toLong() * 1000
                if (!isTimerRunning) {
                    timeLeftInMillis = timerDurationInMillis
                    updateTimerText()
                    updateProgress()
                }
                binding.tvWorkTimeValue.text = "${value.toInt()}s"
            }
        }
    }

    private fun startTimer() {
        countDownTimer = object : CountDownTimer(timeLeftInMillis, 10) {
            override fun onTick(millisUntilFinished: Long) {
                timeLeftInMillis = millisUntilFinished
                updateTimerText()
                updateProgress()
            }

            override fun onFinish() {
                isTimerRunning = false
                binding.btnPlayPause.setImageResource(R.drawable.ic_play)
                timeLeftInMillis = 0
                updateTimerText()
                updateProgress()
            }
        }.start()

        isTimerRunning = true
        binding.btnPlayPause.setImageResource(R.drawable.ic_pause)
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
        if (currentSet < totalSets) {
            currentSet++
        } else {
            currentSet = 1
        }
        updateSetsText()
        resetTimer()
    }

    private fun updateTimerText() {
        val totalSeconds = (timeLeftInMillis + 999) / 1000 // Redondear hacia arriba para que el 00:00 sea al final
        val minutes = totalSeconds / 60
        val seconds = totalSeconds % 60
        val timeFormatted = String.format(Locale.getDefault(), "%02d:%02d", minutes, seconds)
        binding.tvTimer.text = timeFormatted
    }

    private fun updateProgress() {
        val progress = (timeLeftInMillis.toFloat() / timerDurationInMillis.toFloat() * 100).toInt()
        binding.progressTimer.progress = progress
    }

    private fun updateSetsText() {
        binding.tvSets.text = "Set $currentSet / $totalSets"
    }

    override fun onDestroyView() {
        super.onDestroyView()
        countDownTimer?.cancel()
        _binding = null
    }
}