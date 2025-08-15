package com.example.mumuk.ui.home

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.PopupWindow
import android.widget.Toast
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.mumuk.R
import com.example.mumuk.data.api.RetrofitClient
import com.example.mumuk.data.api.TokenManager
import com.example.mumuk.data.model.Recipe
import com.example.mumuk.data.model.category.RandomRecipeResponse
import com.example.mumuk.data.model.mypage.UserProfileResponse
import com.example.mumuk.data.repository.RecipeTrendRepository
import com.example.mumuk.databinding.FragmentHomeBinding
import com.google.android.material.card.MaterialCardView
import kotlinx.coroutines.launch
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class HomeFragment : Fragment() {
    interface BottomNavSelector {
        fun selectBottomNavItem(itemId: Int)
    }

    private var bottomNavSelector: BottomNavSelector? = null
    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!

    private val recipeTrendRepository = RecipeTrendRepository()
    private lateinit var recipeRankAdapter: RecipeRankAdapter

    private var randomRecipeList: MutableList<Recipe>? = null

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
        loadUserNicknameForHome()

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

        binding.ingredientBtn.setOnClickListener {
            findNavController().navigate(R.id.action_navigation_home_to_ingredientRecommendFragment)
        }

        binding.personalBtn.setOnClickListener {
            findNavController().navigate(R.id.action_navigation_home_to_healthRecommendFragment)
        }
        binding.imageView12.setOnClickListener {
            findNavController().navigate(R.id.action_navigation_home_to_alarmFragment)
        }

        binding.dateBtn.setOnClickListener {
            findNavController().navigate(R.id.action_navigation_home_to_ingredientExpiringFragment)
        }

        fetchRandomRecipes()

        setupRankRecyclerView()
    }

    private fun fetchRandomRecipes() {
        if (randomRecipeList != null) {
            setupRecyclerView(binding.todayRV, randomRecipeList!!.take(6))
            return
        }
        val api = RetrofitClient.getRandomRecipeApi(requireContext())
        api.getRandomRecipes().enqueue(object : Callback<RandomRecipeResponse> {
            override fun onResponse(
                call: Call<RandomRecipeResponse>,
                response: Response<RandomRecipeResponse>
            ) {
                if (!isAdded || _binding == null) return

                if (response.isSuccessful && response.body()?.data != null) {
                    val items = response.body()!!.data.map {
                        Recipe(
                            id = it.recipeId,
                            img = null,
                            title = it.name ?: "알 수 없음",
                            isLiked = it.liked,
                            recipeImageUrl = it.imageUrl
                        )
                    }.toMutableList()
                    randomRecipeList = items
                    setupRecyclerView(binding.todayRV, items.take(6))
                } else {
                    Toast.makeText(context, "오늘의 레시피 불러오기 실패", Toast.LENGTH_SHORT).show()
                    setupRecyclerView(binding.todayRV, emptyList())
                }
            }

            override fun onFailure(
                call: Call<RandomRecipeResponse>,
                t: Throwable
            ) {
                Toast.makeText(context, "네트워크 오류: ${t.message}", Toast.LENGTH_SHORT).show()
                setupRecyclerView(binding.todayRV, emptyList())
            }
        })
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
            layoutManager = GridLayoutManager(requireContext(), 2)
            adapter = HomeRecipeAdapter(recipeList.toMutableList()) { clickedRecipe ->
                val bundle = bundleOf("recipeId" to clickedRecipe.id)
                findNavController().navigate(R.id.action_navigation_home_to_recipeFragment, bundle)
            }
        }
    }

    private fun setupRankRecyclerView() {
        recipeRankAdapter = RecipeRankAdapter(
            onItemClick = { recipeRank ->
                val bundle = bundleOf("recipeId" to recipeRank.recipeId?.toLong())
                findNavController().navigate(R.id.action_navigation_home_to_recipeFragment, bundle)
            },
            onHeartClick = { recipeRank, position -> }
        )
        binding.rankRV.apply {
            layoutManager = LinearLayoutManager(context)
            adapter = recipeRankAdapter
        }
        viewLifecycleOwner.lifecycleScope.launch {
            val rankList = recipeTrendRepository.getRecipeTrendRank(requireContext())
            recipeRankAdapter.submitList(rankList)
        }
    }

    private fun loadUserNicknameForHome() {
        val loginType = TokenManager.getLoginType(requireContext()) ?: "LOCAL"

        if (loginType == "KAKAO" || loginType == "NAVER") {
            val savedNickname = TokenManager.getNickName(requireContext())
            val nickname = if (!savedNickname.isNullOrBlank()) savedNickname else "사용자"
            _binding?.textView15?.text = "${nickname}님, 오늘은 뭐 해먹을까요?"
        } else {
            RetrofitClient.getUserApi(requireContext()).getUserProfile()
                .enqueue(object : Callback<UserProfileResponse> {
                    override fun onResponse(
                        call: Call<UserProfileResponse>,
                        response: Response<UserProfileResponse>
                    ) {
                        if (!isAdded || _binding == null) return
                        if (response.isSuccessful) {
                            val profile = response.body()?.data
                            val nickname = profile?.nickName?.takeIf { it.isNotBlank() } ?: "사용자"
                            _binding?.textView15?.text = "${nickname}님, 오늘은 뭐 해먹을까요?"
                        } else {
                            _binding?.textView15?.text = "사용자님, 오늘은 뭐 해먹을까요?"
                        }
                    }

                    override fun onFailure(call: Call<UserProfileResponse>, t: Throwable) {
                        if (!isAdded || _binding == null) return
                        _binding?.textView15?.text = "사용자님, 오늘은 뭐 해먹을까요?"
                    }
                })
        }
    }


    override fun onDetach() {
        super.onDetach()
        bottomNavSelector = null
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}