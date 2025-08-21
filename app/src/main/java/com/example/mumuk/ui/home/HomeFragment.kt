package com.example.mumuk.ui.home

import android.animation.ObjectAnimator
import android.content.Context
import android.content.res.Resources
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.DisplayMetrics
import android.util.TypedValue
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.view.animation.LinearInterpolator
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.PopupWindow
import android.widget.Toast
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.LinearSmoothScroller
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager2.widget.ViewPager2
import com.example.mumuk.R
import com.example.mumuk.data.api.RetrofitClient
import com.example.mumuk.data.api.TokenManager
import com.example.mumuk.data.model.Banner
import com.example.mumuk.data.model.Recipe
import com.example.mumuk.data.model.RecipeRank
import com.example.mumuk.data.model.category.RandomRecipeResponse
import com.example.mumuk.data.model.mypage.UserProfileResponse
import com.example.mumuk.data.model.recipe.ClickLikeRequest
import com.example.mumuk.data.model.recipe.ClickLikeResponse
import com.example.mumuk.data.repository.RecipeTrendRepository
import com.example.mumuk.databinding.FragmentHomeBinding
import com.google.android.material.card.MaterialCardView
import kotlinx.coroutines.launch
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import kotlin.math.abs
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.TimeZone

class HomeFragment : Fragment() {
    interface BottomNavSelector {
        fun selectBottomNavItem(itemId: Int)
    }

    private var bottomNavSelector: BottomNavSelector? = null
    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!

    private val recipeTrendRepository = RecipeTrendRepository()
    private lateinit var recipeRankAdapter: RecipeRankAdapter
    private var rankList: MutableList<RecipeRank> = mutableListOf()
    private val inFlight: MutableSet<Long> = mutableSetOf()

    private var randomRecipeList: MutableList<Recipe>? = null

    private var isRefreshing = false
    private var initialTouchY = 0f
    private val refreshThreshold = 300f
    private val loadingIndicatorHeight = 150f
    private var rotationAnimator: ObjectAnimator? = null

    private var maxPullDistance = 0f

    private lateinit var bannerAdapter: HomeBannerAdapter

    private var autoScrollHandler: Handler? = null
    private var autoScrollRunnable: Runnable? = null
    private val autoScrollInterval: Long = 6000 // ms

    private var isBannerTouched = false
    private var isBannerScrolling = false
    private val bannerScrollDurationMs = 200

    private var scrollPosition = 0

    val Int.dp: Int
        get() = TypedValue.applyDimension(
            TypedValue.COMPLEX_UNIT_DIP, this.toFloat(), Resources.getSystem().displayMetrics
        ).toInt()

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

        binding.homeScrollView.setVerticalScrollableViews(
            listOf(binding.rankRV, binding.todayRV)
        )

        setupFragmentResultListener() // ++ 찜 상태 업데이트 리스너 설정
        setupCustomPullToRefresh()
        setupRotationAnimator()
        loadUserNicknameForHome()
        setupBanner()
        setupBannerTouchPauseResume()
        setupBannerScrollListener()
        startAutoScrollBanner()

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

        updatePopularKeywordsTime()
        fetchRandomRecipes()
        setupRankRecyclerView()
    }

    // ++ RecipeFragment로부터 결과를 수신하는 리스너 설정
    private fun setupFragmentResultListener() {
        parentFragmentManager.setFragmentResultListener("likeResult", this) { _, bundle ->
            val recipeId = bundle.getLong("recipeId")
            val isLiked = bundle.getBoolean("isLiked")
            updateLikeStatus(recipeId, isLiked)
        }
    }

    // ++ 전달받은 정보로 찜 상태를 업데이트하는 함수
    private fun updateLikeStatus(recipeId: Long, isLiked: Boolean) {
        // 인기 레시피 목록 업데이트
        val rankIndex = rankList.indexOfFirst { it.recipeId?.toLong() == recipeId }
        if (rankIndex != -1) {
            rankList[rankIndex] = rankList[rankIndex].copy(isLiked = isLiked)
            recipeRankAdapter.submitList(rankList.toList())
        }

        // 오늘의 레시피 목록 업데이트
        randomRecipeList?.let { list ->
            val todayIndex = list.indexOfFirst { it.id.toLong() == recipeId }
            if (todayIndex != -1) {
                list[todayIndex] = list[todayIndex].copy(isLiked = isLiked)
                (binding.todayRV.adapter as? HomeRecipeAdapter)?.updateLikeAt(todayIndex, isLiked)
            }
        }
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

                        val translationY = (pullDistance / 2).coerceAtMost(maxPullDistance)

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
            }
        }, 1000)
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

        rankList.clear()
        viewLifecycleOwner.lifecycleScope.launch {
            val rank = recipeTrendRepository.getRecipeTrendRank(requireContext())
            rankList.addAll(rank)
            recipeRankAdapter.submitList(rankList.toList())
        }

        loadUserNicknameForHome()
        updatePopularKeywordsTime()
    }

    private fun handleRankHeartClick(item: RecipeRank) {
        val idLong = item.recipeId?.toLong() ?: run {
            Toast.makeText(requireContext(), "잘못된 레시피 ID", Toast.LENGTH_SHORT).show()
            return
        }
        if (inFlight.contains(idLong)) return
        inFlight.add(idLong)

        val idx = rankList.indexOfFirst { it.recipeId == item.recipeId }
        if (idx == -1) {
            inFlight.remove(idLong)
            return
        }

        val old = rankList[idx]
        val optimistic = old.copy(isLiked = !old.isLiked)

        rankList[idx] = optimistic
        recipeRankAdapter.submitList(rankList.toList())

        RetrofitClient.getUserRecipeApi(requireContext())
            .clickLike(ClickLikeRequest(idLong))
            .enqueue(object : Callback<ClickLikeResponse> {
                override fun onResponse(
                    call: Call<ClickLikeResponse>,
                    response: Response<ClickLikeResponse>
                ) {
                    inFlight.remove(idLong)
                    val ok = response.isSuccessful && (response.body()?.status == "OK")
                    if (!ok) rollback()
                }

                override fun onFailure(call: Call<ClickLikeResponse>, t: Throwable) {
                    inFlight.remove(idLong)
                    rollback()
                }

                private fun rollback() {
                    val i2 = rankList.indexOfFirst { it.recipeId == item.recipeId }
                    if (i2 != -1) {
                        rankList[i2] = old
                        recipeRankAdapter.submitList(rankList.toList())
                    }
                    Toast.makeText(requireContext(), "찜 실패", Toast.LENGTH_SHORT).show()
                }
            })
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
            adapter = HomeRecipeAdapter(
                recipeList.toMutableList(),
                onItemClick = { clickedRecipe ->
                    val bundle = bundleOf("recipeId" to clickedRecipe.id.toLong())
                    findNavController().navigate(
                        R.id.action_navigation_home_to_recipeFragment,
                        bundle
                    )
                },
                onHeartClick = { item, pos ->
                    handleTodayHeartClick(item, pos) // ← 하트 클릭 처리(낙관적 업데이트 + 서버 호출)
                }
            )
        }
    }

    private val todayInFlight: MutableSet<Long> = mutableSetOf()

    private fun handleTodayHeartClick(item: Recipe, pos: Int) {
        val recipeIdInt = item.id
        val idLong = recipeIdInt.toLong()

        if (todayInFlight.contains(idLong)) return
        todayInFlight.add(idLong)

        val oldLiked = item.isLiked
        val newLiked = !oldLiked

        (binding.todayRV.adapter as? HomeRecipeAdapter)?.updateLikeAt(pos, newLiked)

        randomRecipeList?.let { full ->
            val i = full.indexOfFirst { it.id == recipeIdInt }
            if (i != -1) full[i] = full[i].copy(isLiked = newLiked)
        }

        RetrofitClient.getUserRecipeApi(requireContext())
            .clickLike(ClickLikeRequest(idLong))
            .enqueue(object : Callback<ClickLikeResponse> {
                override fun onResponse(
                    call: Call<ClickLikeResponse>,
                    response: Response<ClickLikeResponse>
                ) {
                    todayInFlight.remove(idLong)
                    val ok = response.isSuccessful && (response.body()?.status == "OK")
                    if (!ok) rollback()
                    else {
                        val idx = rankList.indexOfFirst { it.recipeId?.toLong() == idLong }
                        if (idx != -1) {
                            rankList[idx] = rankList[idx].copy(isLiked = newLiked)
                            recipeRankAdapter.submitList(rankList.toList())
                        }
                    }
                }

                override fun onFailure(call: Call<ClickLikeResponse>, t: Throwable) {
                    todayInFlight.remove(idLong)
                    rollback()
                }

                fun rollback() {
                    (binding.todayRV.adapter as? HomeRecipeAdapter)?.updateLikeAt(pos, oldLiked)
                    randomRecipeList?.let { full ->
                        val i = full.indexOfFirst { it.id == recipeIdInt }
                        if (i != -1) full[i] = full[i].copy(isLiked = oldLiked)
                    }
                    Toast.makeText(requireContext(), "찜 실패", Toast.LENGTH_SHORT).show()
                }
            })
    }


    private fun setupRankRecyclerView() {
        recipeRankAdapter = RecipeRankAdapter(
            onItemClick = { recipeRank ->
                val idLong = recipeRank.recipeId?.toLong() ?: run {
                    Toast.makeText(requireContext(), "잘못된 레시피 ID", Toast.LENGTH_SHORT).show()
                    return@RecipeRankAdapter
                }
                val bundle = bundleOf("recipeId" to idLong)
                findNavController().navigate(R.id.action_navigation_home_to_recipeFragment, bundle)
            },
            onHeartClick = { recipeRank, _ ->
                handleRankHeartClick(recipeRank)
            }
        )

        binding.rankRV.apply {
            layoutManager = LinearLayoutManager(context)
            adapter = recipeRankAdapter
        }

        if (rankList.isNotEmpty()) {
            binding.rankRV.visibility = View.VISIBLE
            binding.rankEmptyTv.visibility = View.GONE
            recipeRankAdapter.submitList(rankList.take(5)) // CHANGED
        } else {
            viewLifecycleOwner.lifecycleScope.launch {
                val rank = recipeTrendRepository.getRecipeTrendRank(requireContext())
                rankList.clear()
                rankList.addAll(rank)
                if (rankList.isNotEmpty()) {
                    binding.rankRV.visibility = View.VISIBLE
                    binding.rankEmptyTv.visibility = View.GONE
                    recipeRankAdapter.submitList(rankList.take(5)) // CHANGED
                } else {
                    binding.rankRV.visibility = View.GONE
                    binding.rankEmptyTv.visibility = View.VISIBLE
                }
            }
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

    private fun setupBanner() {
        val banners = listOf(
            Banner(R.drawable.img_banner_sample, "푸들리에가 추천하는\nAI 추천 레시피 보고가세요!"),
            Banner(R.drawable.img_banner_sample_2, "나만의 건강맞춤 레시피\n보러가기"),
            Banner(R.drawable.img_banner_sample_3, "재료등록 하러가기\n→")
        )
        bannerAdapter = HomeBannerAdapter(banners) { clickedPosition ->
            val realPosition = when (clickedPosition) {
                0 -> banners.size - 1
                bannerAdapter.itemCount - 1 -> 0
                else -> clickedPosition - 1
            }
            when (realPosition) {
                1 -> findNavController().navigate(R.id.action_navigation_home_to_healthRecommendFragment)
                2 -> findNavController().navigate(R.id.action_navigation_home_to_addIngredientFragment)
            }
        }
        binding.bannerViewPager.adapter = bannerAdapter
        binding.bannerViewPager.offscreenPageLimit = 1

        binding.bannerViewPager.setCurrentItem(1, false)

        // 커스텀 인디케이터 첫 셋팅
        binding.bannerViewPager.post {
            setupCustomIndicator(banners.size, binding.bannerViewPager.currentItem - 1)
        }

        binding.bannerViewPager.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
            override fun onPageSelected(position: Int) {
                // 인디케이터는 실제 페이지(1~items.size)만 표시
                val actualPos = when (position) {
                    0 -> banners.size - 1
                    bannerAdapter.itemCount - 1 -> 0
                    else -> position - 1
                }
                setupCustomIndicator(banners.size, actualPos)
            }

            override fun onPageScrollStateChanged(state: Int) {
                if (state == ViewPager2.SCROLL_STATE_IDLE) {
                    val position = binding.bannerViewPager.currentItem
                    when (position) {
                        0 -> {
                            binding.bannerViewPager.setCurrentItem(bannerAdapter.itemCount - 2, false)
                            binding.bannerViewPager.post {
                                setupCustomIndicator(banners.size, banners.size - 1)
                            }
                        }
                        bannerAdapter.itemCount - 1 -> {
                            binding.bannerViewPager.setCurrentItem(1, false)
                            binding.bannerViewPager.post {
                                setupCustomIndicator(banners.size, 0)
                            }
                        }
                    }
                }
            }
        })
    }

    private fun setupCustomIndicator(pageCount: Int, currentPos: Int = 0) {
        val indicatorLayout = binding.customIndicator
        indicatorLayout.visibility = View.VISIBLE

        // 최초 생성 시 dot 개수 맞춰서 addView
        if (indicatorLayout.childCount != pageCount) {
            indicatorLayout.removeAllViews()
            for (i in 0 until pageCount) {
                val dot = ImageView(requireContext())
                val params = LinearLayout.LayoutParams(10.dp, 10.dp)
                params.setMargins(2.dp, 0, 2.dp, 0)
                dot.layoutParams = params
                dot.setImageResource(R.drawable.dot_unselected)
                indicatorLayout.addView(dot)
            }
        }

        // 선택상태만 갱신
        for (i in 0 until pageCount) {
            val dot = indicatorLayout.getChildAt(i) as ImageView
            dot.setImageResource(if (i == currentPos) R.drawable.dot_selected else R.drawable.dot_unselected)
        }
    }

    private fun startAutoScrollBanner() {
        if (autoScrollHandler == null) autoScrollHandler = Handler(Looper.getMainLooper())
        if (autoScrollRunnable == null) {
            autoScrollRunnable = object : Runnable {
                override fun run() {
                    val viewPager = binding.bannerViewPager
                    val adapter = bannerAdapter
                    if (!isBannerTouched && !isBannerScrolling) {
                        val nextItem = viewPager.currentItem + 1
                        if (nextItem >= adapter.itemCount) {
                            // 점프 후 바로 다음 배너로 스크롤 예약
                            viewPager.setCurrentItem(1, false)
                            // 바로 다음 배너로 스크롤 (6초 기다리지 않음)
                            autoScrollHandler?.postDelayed({
                                smoothScrollToBanner(2) // 두 번째 배너로 이동
                                autoScrollHandler?.postDelayed(this, autoScrollInterval)
                            }, 500) // 점프 후 0.5초 뒤에 바로 스크롤
                            return
                        } else {
                            smoothScrollToBanner(nextItem)
                        }
                    }
                    autoScrollHandler?.postDelayed(this, autoScrollInterval)
                }
            }
        }
        autoScrollHandler?.postDelayed(autoScrollRunnable!!, autoScrollInterval)
    }

    private fun stopAutoScrollBanner() {
        autoScrollHandler?.removeCallbacks(autoScrollRunnable!!)
    }

    private fun setupBannerTouchPauseResume() {
        val viewPager = binding.bannerViewPager
        viewPager.getChildAt(0)?.setOnTouchListener { _, event ->
            when (event.action) {
                MotionEvent.ACTION_DOWN, MotionEvent.ACTION_MOVE -> {
                    isBannerTouched = true
                    stopAutoScrollBanner()
                }
                MotionEvent.ACTION_UP, MotionEvent.ACTION_CANCEL -> {
                    isBannerTouched = false
                    startAutoScrollBanner()
                }
            }
            false
        }
    }

    private fun setupBannerScrollListener() {
        binding.bannerViewPager.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
            override fun onPageScrollStateChanged(state: Int) {
                if (state == ViewPager2.SCROLL_STATE_IDLE) {
                    val position = binding.bannerViewPager.currentItem
                    when (position) {
                        0 -> binding.bannerViewPager.setCurrentItem(bannerAdapter.itemCount - 2, false) // 0 → 마지막
                        bannerAdapter.itemCount - 1 -> binding.bannerViewPager.setCurrentItem(1, false) // 마지막 → 첫 번째
                    }
                }
            }
        })
    }

    private fun smoothScrollToBanner(targetItem: Int) {
        if (_binding == null) return // View가 이미 파괴됐으면 아무 것도 하지 않음

        val viewPager = binding.bannerViewPager
        try {
            val recyclerViewField = ViewPager2::class.java.getDeclaredField("mRecyclerView")
            recyclerViewField.isAccessible = true
            val recyclerView = recyclerViewField.get(viewPager) as RecyclerView

            val layoutManager = recyclerView.layoutManager
            if (layoutManager != null) {
                val smoothScroller = object : LinearSmoothScroller(requireContext()) {
                    override fun calculateSpeedPerPixel(displayMetrics: DisplayMetrics): Float {
                        return bannerScrollDurationMs / displayMetrics.densityDpi.toFloat()
                    }
                }
                smoothScroller.targetPosition = targetItem
                layoutManager.startSmoothScroll(smoothScroller)
            }
        } catch (e: Exception) {
            if (_binding != null) {
                viewPager.setCurrentItem(targetItem, true)
            }
        }
    }

    private fun updatePopularKeywordsTime() {
        val now = Date()
        val sdf = SimpleDateFormat("yyyy.MM.dd HH:00", Locale.getDefault())
        // 시간대를 한국 시간으로 설정합니다.
        sdf.timeZone = TimeZone.getTimeZone("Asia/Seoul")
        val formattedTime = sdf.format(now)
        binding.rankTime.text = "$formattedTime 기준"
    }

    override fun onDetach() {
        super.onDetach()
        bottomNavSelector = null
    }

    override fun onResume() {
        super.onResume()
        binding.homeScrollView.post {
            binding.homeScrollView.scrollTo(0, scrollPosition)
        }
        if (_binding != null) {
            binding.bannerViewPager.setCurrentItem(1, false)
        }
    }

    override fun onPause() {
        super.onPause()
        if (_binding != null) {
            scrollPosition = binding.homeScrollView.scrollY
        }
    }

    override fun onDestroyView() {
        stopAutoScrollBanner()
        rotationAnimator?.cancel()
        rotationAnimator = null
        super.onDestroyView()
        _binding = null
    }
}