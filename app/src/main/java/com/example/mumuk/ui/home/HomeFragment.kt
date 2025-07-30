package com.example.mumuk.ui.home

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.PopupWindow
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.mumuk.R
import com.example.mumuk.data.api.RetrofitClient
import com.example.mumuk.data.api.TokenManager
import com.example.mumuk.data.model.Recipe
import com.example.mumuk.data.model.mypage.UserProfileResponse
import com.example.mumuk.data.repository.RecipeRankRepository
import com.example.mumuk.databinding.FragmentHomeBinding
import com.example.mumuk.utils.JwtUtils
import com.google.android.material.card.MaterialCardView
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
    private val recipeRepository = HomeRecipeRepository()

    // Rank 관련 변수
    private val recipeRankRepository = RecipeRankRepository()
    private lateinit var recipeRankAdapter: RecipeRankAdapter

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

        setupRecyclerView(binding.todayRV, recipeRepository.getTodayRecipes())
        setupRankRecyclerView()
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
            adapter = HomeRecipeAdapter(recipeList) {
                findNavController().navigate(R.id.action_navigation_home_to_recipeFragment)
            }
        }
    }

    // 랭크 목록만 보여줌
    private fun setupRankRecyclerView() {
        recipeRankAdapter = RecipeRankAdapter {
            findNavController().navigate(R.id.action_navigation_home_to_recipeFragment)
        }
        binding.rankRV.apply {
            layoutManager = LinearLayoutManager(context)
            adapter = recipeRankAdapter
        }
        recipeRankAdapter.submitList(recipeRankRepository.getHealthRankRecipes())
    }

    private fun loadUserNicknameForHome() {
        val loginType = TokenManager.getLoginType(requireContext()) ?: "LOCAL"

        if (loginType == "KAKAO" || loginType == "NAVER") {
            val savedNickname = TokenManager.getNickName(requireContext())
            val nickname = if (!savedNickname.isNullOrBlank()) savedNickname else "사용자"
            binding.textView15.text = "${nickname}님, 오늘은 뭐 해먹을까요?"
        } else {
            val accessToken = TokenManager.getAccessToken(requireContext())
            val userId = JwtUtils.getUserIdFromToken(accessToken ?: "")
            if (userId == null) {
                binding.textView15.text = "사용자님, 오늘은 뭐 해먹을까요?"
                return
            }

            RetrofitClient.getUserApi(requireContext()).getUserProfile(userId)
                .enqueue(object : Callback<UserProfileResponse> {
                    override fun onResponse(
                        call: Call<UserProfileResponse>,
                        response: Response<UserProfileResponse>
                    ) {
                        if (response.isSuccessful) {
                            val profile = response.body()?.data
                            val nickname = profile?.nickName ?: "사용자"
                            binding.textView15.text = "${nickname}님, 오늘은 뭐 해먹을까요?"
                        } else {
                            binding.textView15.text = "사용자님, 오늘은 뭐 해먹을까요?"
                        }
                    }

                    override fun onFailure(call: Call<UserProfileResponse>, t: Throwable) {
                        binding.textView15.text = "사용자님, 오늘은 뭐 해먹을까요?"
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