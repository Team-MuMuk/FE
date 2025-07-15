package com.example.mumuk.ui.home

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.PopupWindow
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.mumuk.R
import com.example.mumuk.data.model.Recipe
import com.example.mumuk.databinding.FragmentHomeBinding
import com.google.android.material.card.MaterialCardView

class HomeFragment : Fragment() {
    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!

    private val recipeRepository = HomeRecipeRepository()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.imageView3.setOnClickListener {
            findNavController().navigate(R.id.action_navigation_home_to_recommendFragment)
        }

        binding.infoBtn.setOnClickListener {
            showInfoPopup(it)
        }

        binding.healthBtn.setOnClickListener {
            findNavController().navigate(R.id.action_navigation_home_to_healthManagementFragment)
        }

        binding.bookmarkBtn.setOnClickListener {
            findNavController().navigate(R.id.action_navigation_home_to_bookmarkRecipeFragment)
        }

        setupRecyclerView(binding.todayRV, recipeRepository.getTodayRecipes())
        setupRecyclerView(binding.recentRV, recipeRepository.getRecentRecipes())
        setupRecyclerView(binding.healthRV, recipeRepository.getHealthRecipes())
        setupRecyclerView(binding.recommendedRV, recipeRepository.getRecommendedRecipes())
    }

    private fun showInfoPopup(anchorView: View) {
        val inflater = LayoutInflater.from(requireContext())
        val popupView = inflater.inflate(R.layout.popup_info, null)

        val popupWindow = PopupWindow(
            popupView,
            LinearLayout.LayoutParams.WRAP_CONTENT,
            LinearLayout.LayoutParams.WRAP_CONTENT,
            false
        )

        val okButton = popupView.findViewById<MaterialCardView>(R.id.ok_card_button)
        okButton.setOnClickListener {
            popupWindow.dismiss()
        }

        popupView.measure(View.MeasureSpec.UNSPECIFIED, View.MeasureSpec.UNSPECIFIED)
        val popupWidth = popupView.measuredWidth
        val xOffset = -popupWidth + 30
        val yOffset = -anchorView.height

        popupWindow.showAsDropDown(anchorView, xOffset, yOffset)
    }

    private fun setupRecyclerView(recyclerView: RecyclerView, recipeList: List<Recipe>) {
        recyclerView.apply {
            layoutManager = LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
            adapter = RecipeAdapter(recipeList) {
                findNavController().navigate(R.id.action_navigation_home_to_recipeFragment)
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}