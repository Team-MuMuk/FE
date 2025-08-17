package com.example.mumuk.ui.home

import android.animation.ObjectAnimator
import android.content.Context
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.TypedValue
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.view.animation.LinearInterpolator
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
import kotlin.math.abs

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

    private var isRefreshing = false
    private var initialTouchY = 0f
    private val refreshThreshold = 300f
    private val loadingIndicatorHeight = 150f
    private var rotationAnimator: ObjectAnimator? = null

    private var maxPullDistance = 0f

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

        // 로딩 아이콘이 머무는 위치(loadingIndicatorHeight) + 20dp 정도의 여유 공간
        val extraPullDp = 20f
        val extraPullPx = TypedValue.applyDimension(
            TypedValue.COMPLEX_UNIT_DIP,
            extraPullDp,
            resources.displayMetrics
        )
        maxPullDistance = loadingIndicatorHeight + extraPullPx

        setupCustomPullToRefresh()
        setupRotationAnimator()
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

    private fun setupRotationAnimator() {
        rotationAnimator = ObjectAnimator.ofFloat(binding.loadingIndicator, "rotation", 0f, 360f).apply {
            duration = 1000
            repeatCount = ObjectAnimator.INFINITE
            interpolator = LinearInterpolator()
        }
    }

    private fun setupCustomPullToRefresh() {
        binding.homeScrollView.setOnTouchListener { _, event ->
            if (isRefreshing) return@setOnTouchListener true

            when (event.action) {
                MotionEvent.ACTION_DOWN -> {
                    if (binding.homeScrollView.scrollY == 0) {
                        initialTouchY = event.rawY
                    }
                    false
                }
                MotionEvent.ACTION_MOVE -> {
                    if (binding.homeScrollView.scrollY == 0 && event.rawY > initialTouchY) {
                        val pullDistance = event.rawY - initialTouchY

                        var translationY = (pullDistance / 2).coerceAtMost(maxPullDistance)

                        binding.homeScrollView.translationY = translationY
                        binding.loadingIndicator.alpha = (translationY / refreshThreshold).coerceAtMost(1f)
                        binding.loadingIndicator.visibility = View.VISIBLE
                        binding.loadingIndicator.rotation = translationY * 2

                        true
                    } else {
                        false
                    }
                }
                MotionEvent.ACTION_UP, MotionEvent.ACTION_CANCEL -> {
                    val pulledDistance = binding.homeScrollView.translationY
                    if (pulledDistance > refreshThreshold / 2) {
                        startRefresh()
                    } else {
                        animateScrollTo(0f)
                    }
                    false
                }
                else -> false
            }
        }
    }

    private fun startRefresh() {
        isRefreshing = true
        animateScrollTo(loadingIndicatorHeight)
        binding.loadingIndicator.alpha = 1f
        rotationAnimator?.start()

        Handler(Looper.getMainLooper()).postDelayed({
            if (_binding != null) {
                refreshData()
                animateScrollTo(0f)
                isRefreshing = false
                Toast.makeText(context, "새로고침 완료", Toast.LENGTH_SHORT).show()
            }
        }, 3000)
    }

    private fun animateScrollTo(targetY: Float) {
        val animator = ObjectAnimator.ofFloat(binding.homeScrollView, "translationY", targetY)
        animator.duration = 300
        animator.start()

        if (targetY == 0f) {
            rotationAnimator?.cancel()
            binding.loadingIndicator.animate().alpha(0f).setDuration(300).withEndAction {
                if (_binding != null) {
                    binding.loadingIndicator.visibility = View.INVISIBLE
                }
            }.start()
        }
    }

    private fun refreshData() {
        randomRecipeList = null
        fetchRandomRecipes()
        viewLifecycleOwner.lifecycleScope.launch {
            val rankList = recipeTrendRepository.getRecipeTrendRank(requireContext())
            recipeRankAdapter.submitList(rankList)
        }
        loadUserNicknameForHome()
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

    fun forceRefresh() {
        if (!isRefreshing) {
            // 1. 동시에 scrollView 부드럽게 최상단으로 이동
            binding.homeScrollView.smoothScrollTo(0, 0)

            // 2. 동시에 indicator 공간 펼침 애니메이션 시작
            val translationAnimator = ObjectAnimator.ofFloat(
                binding.homeScrollView,
                "translationY",
                0f,
                loadingIndicatorHeight
            ).apply {
                duration = 350 // 원하는 애니메이션 속도
                interpolator = LinearInterpolator()
                addListener(object : android.animation.Animator.AnimatorListener {
                    override fun onAnimationStart(animation: android.animation.Animator) {
                        // 공간 펼침 시작과 동시에 indicator 준비 (숨김→보임)
                        binding.loadingIndicator.visibility = View.VISIBLE
                        binding.loadingIndicator.alpha = 0f
                    }
                    override fun onAnimationEnd(animation: android.animation.Animator) {
                        // 공간 펼침이 끝나면 indicator를 자연스럽게 fade-in + 새로고침 시작
                        binding.loadingIndicator.animate()
                            .alpha(1f)
                            .setDuration(200)
                            .withEndAction {
                                startRefresh()
                            }
                            .start()
                    }
                    override fun onAnimationCancel(animation: android.animation.Animator) {}
                    override fun onAnimationRepeat(animation: android.animation.Animator) {}
                })
            }
            translationAnimator.start()
        }
    }

    override fun onDetach() {
        super.onDetach()
        bottomNavSelector = null
    }

    override fun onDestroyView() {
        rotationAnimator?.cancel()
        rotationAnimator = null
        super.onDestroyView()
        _binding = null
    }
}