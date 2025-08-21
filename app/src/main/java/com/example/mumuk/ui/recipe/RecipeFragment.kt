package com.example.mumuk.ui.recipe

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.example.mumuk.data.api.RetrofitClient
import com.example.mumuk.data.model.recipe.ClickLikeRequest
import com.example.mumuk.data.model.recipe.ClickLikeResponse
import com.example.mumuk.data.model.recipe.NaverShoppingItem
import com.example.mumuk.data.model.recipe.SearchedBlog
import com.example.mumuk.databinding.FragmentRecipeBinding
import com.google.android.flexbox.FlexDirection
import com.google.android.flexbox.FlexWrap
import com.google.android.flexbox.FlexboxLayoutManager
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class RecipeFragment : Fragment() {
    private var _binding: FragmentRecipeBinding? = null
    private val binding get() = _binding!!

    private var isRecipeLoaded = false
    private var isBlogLoaded = false

    private val recipeViewModel: RecipeViewModel by viewModels {
        RecipeViewModel.Factory(requireContext())
    }

    private var currentRecipeId: Long? = null
    private var isCurrentlyLiked: Boolean = false

    private var isBlogExpanded = false
    private var fullBlogList: List<SearchedBlog> = emptyList()

    private var isShopExpanded = false
    private var fullShopList: List<NaverShoppingItem> = emptyList()


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentRecipeBinding.inflate(inflater, container, false)
        Log.d("RecipeFragment", "onCreateView: View has been created.")
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        Log.d("RecipeFragment", "onViewCreated: Lifecycle method started.")

        setupClickListeners()
        setupRecyclerViews()
        observeViewModel()

        binding.loadingOverlay.show()
        isRecipeLoaded = false
        isBlogLoaded = false

        val recipeId = requireArguments().getLong("recipeId")
        currentRecipeId = recipeId
        Log.d("RecipeFragment", "onViewCreated: Starting to fetch details for recipeId: $recipeId")
        recipeViewModel.fetchRecipeDetail(recipeId)
        recipeViewModel.fetchShopItems(recipeId)
        recipeViewModel.fetchIngredientMatch(recipeId)

    }

    private fun setupClickListeners() {
        Log.d("RecipeFragment", "setupClickListeners: Initializing.")
        binding.backBtn.setOnClickListener {
            Log.d("RecipeFragment", "Back button clicked.")
            findNavController().navigateUp()
        }

        binding.likeBtn.setOnClickListener {
            Log.d("RecipeFragment", "Like button clicked.")
            currentRecipeId?.let { id ->
                val newLikedState = !isCurrentlyLiked
                updateLikeButton(newLikedState)

                Log.d("RecipeFragment", "Calling 'clickLike' API for recipeId: $id")
                val api = RetrofitClient.getUserRecipeApi(requireContext())
                val request = ClickLikeRequest(recipeId = id)
                api.clickLike(request).enqueue(object : Callback<ClickLikeResponse> {
                    override fun onResponse(call: Call<ClickLikeResponse>, response: Response<ClickLikeResponse>) {
                        if (response.isSuccessful) {
                            isCurrentlyLiked = newLikedState
                            Log.d("RecipeFragment", "Like API call successful. Response: ${response.body()}")
                            val result = Bundle().apply {
                                putLong("recipeId", id)
                                putBoolean("isLiked", newLikedState)
                            }
                            parentFragmentManager.setFragmentResult("likeResult", result)

                        } else {
                            updateLikeButton(isCurrentlyLiked)
                            val errorBody = response.errorBody()?.string() ?: "No error body"
                            Log.e("RecipeFragment", "Like API call failed. Code: ${response.code()}, ErrorBody: $errorBody")
                        }
                    }

                    override fun onFailure(call: Call<ClickLikeResponse>, t: Throwable) {
                        updateLikeButton(isCurrentlyLiked)
                        Log.e("RecipeFragment", "Like API call failure (Network/Exception). Message: ${t.localizedMessage}", t)
                    }
                })
            }
        }

        binding.plusBtn.setOnClickListener {
            isBlogExpanded = true
            updateBlogList()
        }

        binding.shopPlusBtn.setOnClickListener {
            isShopExpanded = true
            updateShopList()
        }
    }

    private fun setupRecyclerViews() {
        Log.d("RecipeFragment", "setupRecyclerViews: Initializing.")
        // Ingredient RecyclerView
        binding.ingredientRV.layoutManager = FlexboxLayoutManager(requireContext()).apply {
            flexDirection = FlexDirection.ROW
            flexWrap = FlexWrap.WRAP
        }
        binding.ingredientRV.adapter = IngredientAdapter(emptyList())

        // Blog RecyclerView
        binding.blogRV.adapter = BlogAdapter(emptyList())
        binding.blogRV.layoutManager = LinearLayoutManager(context)

        // Shop RecyclerView
        binding.shopRV.adapter = ShopAdapter()
        binding.shopRV.layoutManager = GridLayoutManager(context, 2)
    }

    private fun observeViewModel() {
        Log.d("RecipeFragment", "observeViewModel: Setting up observers.")
        // 레시피 상세 정보 관찰
        recipeViewModel.userRecipeDetail.observe(viewLifecycleOwner) { detail ->
            Log.i("RecipeFragment", "userRecipeDetail observer triggered. Data received: $detail")
            isCurrentlyLiked = detail.liked
            binding.apply {
                Log.d("RecipeFragment", "Applying data to UI elements.")
                // 기본 정보
                recipeTitle.text = detail.title
                textView9.text = detail.description
                textView10.text = "소요시간 : ${detail.cookingTime}분"
                textView12.text = "칼로리 : ${detail.calories} kcal"
                updateLikeButton(detail.liked)

                // 영양 정보
                carbText.text = "${detail.carbohydrate} / 158g"
                protText.text = "${detail.protein} / 63g"
                fatText.text = "${detail.fat} / 32g"

                // Glide를 사용한 이미지 로딩
                Glide.with(this@RecipeFragment)
                    .load(detail.recipeImage)
                    .error(com.example.mumuk.R.drawable.bg_mosaic)
                    .into(binding.imageView4)

                Glide.with(this@RecipeFragment)
                    .load(detail.recipeImage)
                    .error(com.example.mumuk.R.drawable.bg_mosaic)
                    .into(binding.recipeImg)

                recipeTitle2.text = "#${detail.title}"
                shopRecipeTitle.text = "#${detail.title}"
            }

            isRecipeLoaded = true
            dismissLoadingIfReady()
        }

        // 블로그 리스트 관찰
        recipeViewModel.blogList.observe(viewLifecycleOwner) { blogs ->
            Log.d("RecipeFragment", "blogList observer triggered. Item count: ${blogs.size}")
            this.fullBlogList = blogs
            updateBlogList()

            isBlogLoaded = true
            dismissLoadingIfReady()
        }

        // shopItemList 관찰
        recipeViewModel.shopItemList.observe(viewLifecycleOwner) { shopList ->
            Log.d("RecipeFragment", "shopItemList observer triggered. Item count: ${shopList.size}")
            this.fullShopList = shopList
            updateShopList()
        }

        recipeViewModel.ingredientMatchData.observe(viewLifecycleOwner) { matchData ->
            val ingredientItems = mutableListOf<com.example.mumuk.data.model.RecipeIngredient>()
            matchData.match.forEach { name ->
                ingredientItems.add(com.example.mumuk.data.model.RecipeIngredient(name = name, isAvailable = true))
            }
            matchData.mismatch.forEach { name ->
                ingredientItems.add(com.example.mumuk.data.model.RecipeIngredient(name = name, isAvailable = false))
            }
            matchData.replaceable.forEach { item ->
                ingredientItems.add(
                    com.example.mumuk.data.model.RecipeIngredient(
                        name = "${item.recipeIngredient} (대체: ${item.userIngredient})",
                        isAvailable = false
                    )
                )
            }
            (binding.ingredientRV.adapter as? IngredientAdapter)?.updateData(ingredientItems)
        }
    }

    private fun updateLikeButton(isLiked: Boolean) {
        binding.imageView7.setImageResource(
            if (isLiked) com.example.mumuk.R.drawable.btn_heart_fill
            else com.example.mumuk.R.drawable.btn_heart_blank
        )
    }

    private fun updateBlogList() {
        val blogAdapter = binding.blogRV.adapter as? BlogAdapter ?: return

        if (fullBlogList.size <= 5) {
            blogAdapter.submitList(fullBlogList)
            binding.plusBtn.visibility = View.GONE
        } else {
            if (isBlogExpanded) {
                blogAdapter.submitList(fullBlogList)
                binding.plusBtn.visibility = View.GONE
            } else {
                blogAdapter.submitList(fullBlogList.take(5))
                binding.plusBtn.visibility = View.VISIBLE
            }
        }
    }

    private fun updateShopList() {
        val shopAdapter = binding.shopRV.adapter as? ShopAdapter ?: return

        if (fullShopList.size <= 4) {
            shopAdapter.submitList(fullShopList)
            binding.shopPlusBtn.visibility = View.INVISIBLE
        } else {
            if (isShopExpanded) {
                shopAdapter.submitList(fullShopList)
                binding.shopPlusBtn.visibility = View.INVISIBLE
            } else {
                shopAdapter.submitList(fullShopList.take(4))
                binding.shopPlusBtn.visibility = View.VISIBLE
            }
        }
    }

    private fun dismissLoadingIfReady() {
        if (isRecipeLoaded && isBlogLoaded) {
            binding.loadingOverlay.hide()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        binding.loadingOverlay.hide()
        _binding = null
    }
}