package com.example.mumuk.ui.ingredient

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.mumuk.R
import com.example.mumuk.data.repository.IngredientRepository
import com.example.mumuk.databinding.FragmentAddIngredientBinding

class AddIngredientFragment : Fragment() {
    private var _binding: FragmentAddIngredientBinding? = null
    private val binding get() = _binding!!

    private val ingredientRepository = IngredientRepository()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentAddIngredientBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.backBtn.setOnClickListener {
            requireActivity().onBackPressedDispatcher.onBackPressed()
        }

        // RecyclerView 연결
        val ingredientList = ingredientRepository.getIngredients()
        binding.ingredientRV.layoutManager = LinearLayoutManager(requireContext())
        binding.ingredientRV.adapter = IngredientAdapter(ingredientList)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}