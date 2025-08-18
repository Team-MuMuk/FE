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
import androidx.navigation.fragment.findNavController
import android.widget.Toast
import androidx.lifecycle.lifecycleScope
import com.example.mumuk.R
import com.example.mumuk.databinding.FragmentIngredientExpiringBinding
import kotlinx.coroutines.launch

class IngredientExpiringFragment : Fragment() {
    private var _binding: FragmentIngredientExpiringBinding? = null
    private val binding get() = _binding!!

    private val ingredientRepository by lazy { IngredientRepository(requireContext()) }

    private var expiringAdapter: ExpiringIngredientAdapter? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentIngredientExpiringBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupExpiringRV()
        binding.backBtn.setOnClickListener {
            requireActivity().onBackPressedDispatcher.onBackPressed()
        }
    }

    private fun setupExpiringRV() {
        viewLifecycleOwner.lifecycleScope.launch {
            val ingredientList = ingredientRepository.getIngredients()
            val expiringList = ingredientList.filter {
                val today = LocalDate.now()
                val expiry = LocalDate.parse(it.expiryDate, DateTimeFormatter.ofPattern("yyyy-MM-dd"))
                ChronoUnit.DAYS.between(today, expiry) in 0..7
            }.toMutableList()

            val expiringAdapter = ExpiringIngredientAdapter(
                expiringList,
                onItemClick = { ingredient ->
                    val bundle = Bundle().apply {
                        putSerializable("ingredient", ingredient)
                    }
                    findNavController().navigate(
                        R.id.action_ingredientExpiringFragment_to_ingredientDetailFragment,
                        bundle
                    )
                },
                onDeleteClick = { ingredient ->
                    viewLifecycleOwner.lifecycleScope.launch {
                        try {
                            val response = ingredientRepository.deleteIngredient(ingredient.id)
                            if (response.isSuccessful && response.body()?.code == "INGREDIENT_200") {
                                val newList = ingredientRepository.getIngredients()
                                val newExpiringList = newList.filter {
                                    val today = LocalDate.now()
                                    val expiry = LocalDate.parse(it.expiryDate, DateTimeFormatter.ofPattern("yyyy-MM-dd"))
                                    ChronoUnit.DAYS.between(today, expiry) in 0..7
                                }.toMutableList()
                                expiringAdapter?.submitList(newExpiringList)
                            } else {
                                Toast.makeText(requireContext(), "삭제 실패: ${response.body()?.message ?: response.message()}", Toast.LENGTH_SHORT).show()
                            }
                        } catch (e: Exception) {
                            Toast.makeText(requireContext(), "네트워크 오류: ${e.localizedMessage}", Toast.LENGTH_SHORT).show()
                        }
                    }
                }
            )
            binding.expiringRV.layoutManager = LinearLayoutManager(requireContext())
            binding.expiringRV.adapter = expiringAdapter
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}