package com.example.mumuk.ui.ingredient

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.mumuk.data.repository.IngredientRepository
import com.example.mumuk.databinding.FragmentIngredientListBinding
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.time.temporal.ChronoUnit

class IngredientListFragment : Fragment() {
    private var _binding: FragmentIngredientListBinding? = null
    private val binding get() = _binding!!

    private val ingredientRepository = IngredientRepository()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentIngredientListBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val ingredientList = ingredientRepository.getIngredients()
        binding.ingredientRV.layoutManager = LinearLayoutManager(requireContext())
        binding.ingredientRV.adapter = IngredientAdapter(ingredientList)

        val allIngredients = ingredientRepository.getIngredients()
        val expiringList = allIngredients.filter {
            val today = LocalDate.now()
            val expiry = LocalDate.parse(it.expiryDate, DateTimeFormatter.ofPattern("yyyy-MM-dd"))
            ChronoUnit.DAYS.between(today, expiry) in 0..3
        }
        binding.expiringRV.layoutManager = LinearLayoutManager(requireContext())
        binding.expiringRV.adapter = ExpiringIngredientAdapter(expiringList)

        binding.backBtn.setOnClickListener {
            requireActivity().onBackPressedDispatcher.onBackPressed()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}