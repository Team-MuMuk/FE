package com.example.mumuk.ui.ingredient

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.navigation.fragment.findNavController
import com.example.mumuk.R
import com.example.mumuk.databinding.FragmentIngredientDetailBinding
import com.example.mumuk.data.model.Ingredient

class IngredientDetailFragment : Fragment() {
    private var _binding: FragmentIngredientDetailBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentIngredientDetailBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val ingredient = arguments?.getSerializable("ingredient") as? Ingredient
        ingredient?.let {
            binding.name.text = it.name
            val expiryDate = it.expiryDate // 예: "2025-10-10"
            expiryDate?.let { dateStr ->
                val parts = dateStr.split("-")
                if (parts.size == 3) {
                    binding.year.setText(parts[0])
                    binding.month.setText(parts[1])
                    binding.day.setText(parts[2])
                }
            }
        }

        // 알림 카드뷰 선택 UI 적용
        val cardViews = listOf(
            binding.d3 to binding.textView43,
            binding.d7 to binding.textView45,
            binding.d10 to binding.textView46,
            binding.d31 to binding.textView47,
            binding.none to binding.textView48
        )

        val selectedCardColor = ContextCompat.getColor(requireContext(), R.color.beige_600)
        val defaultCardColor = ContextCompat.getColor(requireContext(), R.color.beige_100)
        val selectedTextColor = ContextCompat.getColor(requireContext(), R.color.white)
        val defaultTextColor = ContextCompat.getColor(requireContext(), R.color.black_400)

        cardViews.forEach { (card, text) ->
            card.setCardBackgroundColor(defaultCardColor)
            text.setTextColor(defaultTextColor)
        }

        fun selectCard(selectedIdx: Int) {
            cardViews.forEachIndexed { idx, pair ->
                val (card, text) = pair
                if (idx == selectedIdx) {
                    card.setCardBackgroundColor(selectedCardColor)
                    text.setTextColor(selectedTextColor)
                } else {
                    card.setCardBackgroundColor(defaultCardColor)
                    text.setTextColor(defaultTextColor)
                }
            }
        }

        cardViews.forEachIndexed { idx, pair ->
            pair.first.setOnClickListener {
                selectCard(idx)
            }
        }

        binding.backBtn.setOnClickListener {
            findNavController().popBackStack()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}