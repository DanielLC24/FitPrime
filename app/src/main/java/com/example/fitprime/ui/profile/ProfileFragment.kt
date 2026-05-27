package com.example.fitprime.ui.profile

import android.content.res.ColorStateList
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.example.fitprime.R
import com.example.fitprime.databinding.FragmentProfileBinding
import java.util.Locale

class ProfileFragment : Fragment() {

    private var _binding: FragmentProfileBinding? = null
    private val binding get() = _binding!!

    private var isMale = true

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentProfileBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupGenderButtons()
        setupTextWatchers()
    }

    private fun setupGenderButtons() {
        binding.btnMale.setOnClickListener {
            isMale = true
            updateGenderUI()
        }
        binding.btnFemale.setOnClickListener {
            isMale = false
            updateGenderUI()
        }
        updateGenderUI() // Estado inicial
    }

    private fun updateGenderUI() {
        val activeBg = ColorStateList.valueOf(ContextCompat.getColor(requireContext(), R.color.lime_green))
        val inactiveBg = ColorStateList.valueOf(ContextCompat.getColor(requireContext(), android.R.color.transparent))
        val activeText = ContextCompat.getColor(requireContext(), R.color.black)
        val inactiveText = ContextCompat.getColor(requireContext(), R.color.white)

        if (isMale) {
            binding.btnMale.backgroundTintList = activeBg
            binding.btnMale.setTextColor(activeText)
            binding.btnMale.iconTint = ColorStateList.valueOf(activeText)

            binding.btnFemale.backgroundTintList = inactiveBg
            binding.btnFemale.setTextColor(inactiveText)
            binding.btnFemale.iconTint = ColorStateList.valueOf(inactiveText)
            binding.btnFemale.strokeColor = ColorStateList.valueOf(ContextCompat.getColor(requireContext(), R.color.timer_grey))
        } else {
            binding.btnMale.backgroundTintList = inactiveBg
            binding.btnMale.setTextColor(inactiveText)
            binding.btnMale.iconTint = ColorStateList.valueOf(inactiveText)
            binding.btnMale.strokeColor = ColorStateList.valueOf(ContextCompat.getColor(requireContext(), R.color.timer_grey))

            binding.btnFemale.backgroundTintList = activeBg
            binding.btnFemale.setTextColor(activeText)
            binding.btnFemale.iconTint = ColorStateList.valueOf(activeText)
        }
    }

    private fun setupTextWatchers() {
        val watcher = object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                calculateBMI()
            }
            override fun afterTextChanged(s: Editable?) {}
        }
        binding.etHeight.addTextChangedListener(watcher)
        binding.etWeight.addTextChangedListener(watcher)
    }

    private fun calculateBMI() {
        val heightStr = binding.etHeight.text.toString()
        val weightStr = binding.etWeight.text.toString()

        if (heightStr.isNotEmpty() && weightStr.isNotEmpty()) {
            val h = heightStr.toDoubleOrNull() ?: 0.0
            val w = weightStr.toDoubleOrNull() ?: 0.0

            if (h > 0 && w > 0) {
                val bmi = w / ((h / 100) * (h / 100))
                displayBMI(bmi)
            }
        } else {
            binding.tvBmiValue.text = "--"
            binding.tvBmiCategory.text = getString(R.string.profile_enter_data)
            binding.cardRecommendation.visibility = View.GONE
        }
    }

    private fun displayBMI(bmi: Double) {
        binding.tvBmiValue.text = String.format(Locale.US, "%.1f", bmi)
        binding.cardRecommendation.visibility = View.VISIBLE

        val (categoryRes, colorRes, recommendationRes) = when {
            bmi < 18.5 -> Triple(R.string.bmi_underweight, R.color.bmi_underweight, R.string.rec_underweight)
            bmi < 25.0 -> Triple(R.string.bmi_normal, R.color.bmi_normal, R.string.rec_normal)
            bmi < 30.0 -> Triple(R.string.bmi_overweight, R.color.bmi_overweight, R.string.rec_overweight)
            else -> Triple(R.string.bmi_obesity, R.color.bmi_obesity, R.string.rec_obesity)
        }

        val color = ContextCompat.getColor(requireContext(), colorRes)
        binding.tvBmiCategory.text = getString(categoryRes)
        binding.tvBmiCategory.setTextColor(color)
        binding.tvBmiValue.setTextColor(color)
        binding.ivRecIcon.imageTintList = ColorStateList.valueOf(color)
        binding.tvRecommendation.text = getString(recommendationRes)
        
        updateIndicator(bmi)
    }

    private fun updateIndicator(bmi: Double) {
        val minBmi = 10.0
        val maxBmi = 40.0
        val coercedBmi = bmi.coerceIn(minBmi, maxBmi)
        val bias = ((coercedBmi - minBmi) / (maxBmi - minBmi)).toFloat()

        val params = binding.ivBmiIndicator.layoutParams as androidx.constraintlayout.widget.ConstraintLayout.LayoutParams
        params.horizontalBias = bias
        binding.ivBmiIndicator.layoutParams = params
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}