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
import com.example.mumuk.R
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.launch
import android.widget.Toast

class IngredientListFragment : Fragment() {
    private var _binding: FragmentIngredientListBinding? = null
    private val binding get() = _binding!!

    private val ingredientRepository by lazy { IngredientRepository(requireContext()) }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentIngredientListBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupIngredientRV()

        binding.backBtn.setOnClickListener {
            requireActivity().onBackPressedDispatcher.onBackPressed()
        }

        binding.plusBtn.setOnClickListener {
            findNavController().navigate(R.id.action_ingredientListFragment_to_ingredientExpiringFragment)
        }
    }

    private fun setupIngredientRV() {
        viewLifecycleOwner.lifecycleScope.launch {
            val ingredientList = ingredientRepository.getIngredients()

            lateinit var ingredientAdapter: IngredientAdapter
            lateinit var expiringAdapter: ExpiringIngredientAdapter
            
            ingredientAdapter = IngredientAdapter(
                ingredientList,
                onItemClick = { ingredient ->
                    val bundle = Bundle().apply {
                        putSerializable("ingredient", ingredient)
                    }
                    findNavController().navigate(
                        R.id.action_ingredientListFragment_to_ingredientDetailFragment,
                        bundle
                    )
                },
                onDeleteClick = { ingredient ->
                    viewLifecycleOwner.lifecycleScope.launch {
                        try {
                            val response = ingredientRepository.deleteIngredient(ingredient.id)
                            if (response.isSuccessful && response.body()?.code == "INGREDIENT_200") {
                                val newList = ingredientRepository.getIngredients()
                                ingredientAdapter.submitList(newList)
                                val newExpiringList = newList.filter {
                                    val today = LocalDate.now()
                                    val expiry = LocalDate.parse(it.expiryDate, DateTimeFormatter.ofPattern("yyyy-MM-dd"))
                                    ChronoUnit.DAYS.between(today, expiry) in 0..3
                                }.toMutableList()
                                expiringAdapter.submitList(newExpiringList)
                            } else {
                                Toast.makeText(requireContext(), "삭제 실패: ${response.body()?.message ?: response.message()}", Toast.LENGTH_SHORT).show()
                            }
                        } catch (e: Exception) {
                            Toast.makeText(requireContext(), "네트워크 오류: ${e.localizedMessage}", Toast.LENGTH_SHORT).show()
                        }
                    }
                }
            )

            binding.ingredientRV.layoutManager = LinearLayoutManager(requireContext())
            binding.ingredientRV.adapter = ingredientAdapter

            val expiringList = ingredientList.filter {
                val today = LocalDate.now()
                val expiry = LocalDate.parse(it.expiryDate, DateTimeFormatter.ofPattern("yyyy-MM-dd"))
                ChronoUnit.DAYS.between(today, expiry) in 0..7
            }.toMutableList()
            expiringAdapter = ExpiringIngredientAdapter(
                expiringList,
                onItemClick = { ingredient ->
                    val bundle = Bundle().apply {
                        putSerializable("ingredient", ingredient)
                    }
                    findNavController().navigate(
                        R.id.action_ingredientListFragment_to_ingredientDetailFragment,
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
                                    ChronoUnit.DAYS.between(today, expiry) in 0..3
                                }.toMutableList()
                                expiringAdapter.submitList(newExpiringList)
                                ingredientAdapter.submitList(newList)
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