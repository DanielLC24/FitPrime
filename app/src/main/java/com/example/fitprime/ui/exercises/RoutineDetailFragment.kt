package com.example.fitprime.ui.exercises

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.fitprime.R
import com.example.fitprime.databinding.FragmentRoutineDetailBinding

class RoutineDetailFragment : Fragment() {

    private var _binding: FragmentRoutineDetailBinding? = null
    private val binding get() = _binding!!
    
    // Asumiendo que usaremos Safe Args o Bundle
    private var workout: Workout? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentRoutineDetailBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        
        // Obtener datos del bundle
        workout = arguments?.getParcelable("workout")
        
        setupUI()
        setupRecyclerView()
        
        binding.btnBack.setOnClickListener {
            parentFragmentManager.popBackStack()
        }
        
        binding.btnStartRoutine.setOnClickListener {
            val bundle = Bundle().apply {
                putParcelable("workout", workout)
            }
            // Navegar al TimerFragment pasándole la rutina
            findNavController().navigate(R.id.navigation_timer, bundle)
        }
    }

    private fun setupUI() {
        workout?.let {
            binding.tvRoutineName.text = it.title
            binding.tvRoutineDescription.text = it.description
            binding.tvDetailTag1.text = it.tag1.uppercase()
            binding.tvDetailDurationTop.text = it.duration.uppercase()
        }
    }

    private fun setupRecyclerView() {
        workout?.let {
            binding.rvSequence.apply {
                layoutManager = LinearLayoutManager(context)
                adapter = ExerciseSequenceAdapter(it.exercises)
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}