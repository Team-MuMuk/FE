package com.example.mumuk.ui.home

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.PopupWindow
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.mumuk.R
import com.example.mumuk.data.model.Recipe
import com.example.mumuk.data.repository.RecipeRankRepository
import com.example.mumuk.databinding.FragmentHomeBinding
import com.example.mumuk.ui.bookmark.BookmarkRecipeAdapter
import com.example.mumuk.ui.bookmark.BookmarkRecipeViewModel
import com.google.android.material.card.MaterialCardView

class HomeFragment : Fragment() {
    interface BottomNavSelector {
        fun selectBottomNavItem(itemId: Int)
    }

    private var bottomNavSelector: BottomNavSelector? = null
    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!
    private val recipeRepository = HomeRecipeRepository()

    private val bookmarkViewModel: BookmarkRecipeViewModel by viewModels()
    private lateinit var bookmarkAdapter: BookmarkRecipeAdapter

    // Rank 관련 추가 변수
    private val recipeRankRepository = RecipeRankRepository()
    private lateinit var recipeRankAdapter: RecipeRankAdapter

    enum class TabType { HEALTH, INGREDIENT, RECENT }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        if (context is BottomNavSelector) {
            bottomNavSelector = context
        } else {
            throw ClassCastException("$context must implement BottomNavSelector")
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.infoBtn.setOnClickListener {
            showInfoPopup(it)
        }

        binding.healthBtn.setOnClickListener {
            findNavController().navigate(R.id.action_navigation_home_to_healthManagementFragment)
        }

        binding.bookmarkBtn.setOnClickListener {
            findNavController().navigate(R.id.action_navigation_home_to_bookmarkRecipeFragment)
        }

        binding.addBtn.setOnClickListener {
            findNavController().navigate(R.id.action_navigation_home_to_addIngredientFragment)
        }

        setupRecyclerView(binding.todayRV, recipeRepository.getTodayRecipes())
        setupRecyclerView(binding.recentRV, recipeRepository.getRecentRecipes())
        setupBookmarkRecyclerView()

        // Rank RecyclerView 및 탭 연결
        setupRankRecyclerViewAndTabs()
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
            adapter = HomeRecipeAdapter(recipeList) {
                findNavController().navigate(R.id.action_navigation_home_to_recipeFragment)
            }
        }
    }

    private fun setupBookmarkRecyclerView() {
        bookmarkAdapter = BookmarkRecipeAdapter().apply {
            onItemClick = { recipe ->
                findNavController().navigate(R.id.action_navigation_home_to_recipeFragment)
            }
        }

        binding.bookmarkRV.apply {
            layoutManager = LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
            adapter = bookmarkAdapter
        }

        bookmarkViewModel.recipes.observe(viewLifecycleOwner, Observer { recipes ->
            bookmarkAdapter.submitList(recipes)
        })
    }

    private fun setupRankRecyclerViewAndTabs() {
        recipeRankAdapter = RecipeRankAdapter {
            findNavController().navigate(R.id.action_navigation_home_to_recipeFragment)
        }
        binding.rankRV.apply {
            layoutManager = LinearLayoutManager(context)
            adapter = recipeRankAdapter
        }

        binding.tabHealth.setOnClickListener {
            setRankTabSelected(TabType.HEALTH)
            recipeRankAdapter.submitList(recipeRankRepository.getHealthRankRecipes())
        }
        binding.tabIngredient.setOnClickListener {
            setRankTabSelected(TabType.INGREDIENT)
            recipeRankAdapter.submitList(recipeRankRepository.getIngredientRankRecipes())
        }
        binding.tabRecent.setOnClickListener {
            setRankTabSelected(TabType.RECENT)
            recipeRankAdapter.submitList(recipeRankRepository.getRecentRankRecipes())
        }

        setRankTabSelected(TabType.HEALTH)
        recipeRankAdapter.submitList(recipeRankRepository.getHealthRankRecipes())
    }

    private fun setRankTabSelected(tab: TabType) {
        val selectedColor = requireContext().getColor(R.color.green_500)
        val unselectedColor = requireContext().getColor(R.color.black_200)

        binding.tabHealthText.setTextColor(if (tab == TabType.HEALTH) selectedColor else unselectedColor)
        binding.tabIngredientText.setTextColor(if (tab == TabType.INGREDIENT) selectedColor else unselectedColor)
        binding.tabRecentText.setTextColor(if (tab == TabType.RECENT) selectedColor else unselectedColor)

        fun setIndicator(view: View, selected: Boolean) {
            view.layoutParams.height = if (selected) dpToPx(2) else dpToPx(1)
            view.setBackgroundColor(if (selected) selectedColor else unselectedColor)
            view.requestLayout()
        }
        setIndicator(binding.tabHealthIndicator, tab == TabType.HEALTH)
        setIndicator(binding.tabIngredientIndicator, tab == TabType.INGREDIENT)
        setIndicator(binding.tabRecentIndicator, tab == TabType.RECENT)
    }

    private fun dpToPx(dp: Int): Int =
        (dp * resources.displayMetrics.density).toInt()

    override fun onDetach() {
        super.onDetach()
        bottomNavSelector = null
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}