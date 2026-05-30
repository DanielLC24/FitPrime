package com.example.fitprime.ui.exercises

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.fitprime.R
import com.example.fitprime.databinding.FragmentExercisesBinding

class ExercisesFragment : Fragment() {

    private var _binding: FragmentExercisesBinding? = null
    private val binding get() = _binding!!
    private lateinit var workoutAdapter: WorkoutAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentExercisesBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupRecyclerView()
        setupSearch()
    }

    private fun setupRecyclerView() {
        val workouts = listOf(
            Workout(
                "Cuerpo Completo", "20 min", "180-260 kcal", "Destacado", "Difícil",
                "Desarrolla fuerza y acondicionamiento total con esta rutina equilibrada centrada en los principales grupos musculares.",
                listOf(
                    Exercise("Sentadillas", 2, 60),
                    Exercise("Descanso", 1, 30, true),
                    Exercise("Flexiones", 2, 45),
                    Exercise("Descanso", 1, 30, true),
                    Exercise("Zancadas", 2, 60),
                    Exercise("Descanso", 1, 30, true),
                    Exercise("Plancha", 2, 45),
                    Exercise("Descanso", 1, 30, true),
                    Exercise("Mountain Climbers", 2, 45),
                    Exercise("Descanso", 1, 30, true)
                )
            ),
            Workout(
                "Pecho y Tríceps", "16 min", "140-220 kcal", "Fuerza", "Intermedio",
                "Concéntrate en tu potencia de empuje con esta rutina intensiva de pecho y tríceps.",
                listOf(
                    Exercise("Flexiones normales", 2, 60),
                    Exercise("Descanso", 1, 30, true),
                    Exercise("Flexiones diamante", 2, 45),
                    Exercise("Descanso", 1, 30, true),
                    Exercise("Fondos en silla", 2, 45),
                    Exercise("Descanso", 1, 30, true),
                    Exercise("Flexiones abiertas", 2, 50),
                    Exercise("Descanso", 1, 30, true)
                )
            ),
            Workout(
                "Pierna", "22 min", "220-340 kcal", "Fuerza", "Difícil",
                "Desarrolla potencia y resistencia en el tren inferior con estos desafiantes ejercicios de pierna.",
                listOf(
                    Exercise("Sentadillas", 2, 75),
                    Exercise("Descanso", 1, 30, true),
                    Exercise("Sentadilla búlgara", 2, 60),
                    Exercise("Descanso", 1, 30, true),
                    Exercise("Zancadas", 2, 60),
                    Exercise("Descanso", 1, 30, true),
                    Exercise("Puente de glúteo", 2, 50),
                    Exercise("Descanso", 1, 30, true),
                    Exercise("Pantorrillas", 2, 60),
                    Exercise("Descanso", 1, 30, true)
                )
            ),
            Workout(
                "Abdomen", "12 min", "90-160 kcal", "Core", "Principiante",
                "Fortalece tu núcleo y define tus abdominales con esta rutina enfocada.",
                listOf(
                    Exercise("Crunches", 2, 45),
                    Exercise("Descanso", 1, 20, true),
                    Exercise("Plancha", 2, 45),
                    Exercise("Descanso", 1, 20, true),
                    Exercise("Elevación de piernas", 2, 45),
                    Exercise("Descanso", 1, 20, true),
                    Exercise("Russian Twists", 2, 45),
                    Exercise("Descanso", 1, 20, true)
                )
            ),
            Workout(
                "Cardio", "25 min", "280-420 kcal", "Cardio", "Intermedio",
                "Quema grasa y mejora tu salud cardiovascular con estos movimientos de alta energía.",
                listOf(
                    Exercise("Jumping Jacks", 3, 60),
                    Exercise("Descanso ligero", 1, 10, true),
                    Exercise("Burpees", 3, 45),
                    Exercise("Descanso ligero", 1, 10, true),
                    Exercise("Mountain Climbers", 3, 60),
                    Exercise("Descanso ligero", 1, 10, true),
                    Exercise("High Knees", 3, 60),
                    Exercise("Descanso ligero", 1, 10, true),
                    Exercise("Jump Squats", 3, 45),
                    Exercise("Descanso ligero", 1, 10, true)
                )
            )
        )

        workoutAdapter = WorkoutAdapter(workouts) { selectedWorkout ->
            val bundle = Bundle().apply {
                putParcelable("workout", selectedWorkout)
            }
            findNavController().navigate(R.id.action_exercises_to_detail, bundle)
        }
        binding.rvWorkouts.apply {
            layoutManager = LinearLayoutManager(context)
            adapter = workoutAdapter
        }
    }

    private fun setupSearch() {
        binding.etSearch.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                workoutAdapter.filter(s.toString())
            }
            override fun afterTextChanged(s: Editable?) {}
        })
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}