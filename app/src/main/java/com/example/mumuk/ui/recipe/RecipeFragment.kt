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
import com.example.mumuk.databinding.FragmentRecipeBinding
import com.example.mumuk.data.api.RetrofitClient
import com.example.mumuk.data.model.Blog
import com.example.mumuk.data.model.Recipe
import com.example.mumuk.data.model.recipe.ClickLikeRequest
import com.example.mumuk.data.model.recipe.ClickLikeResponse
import com.example.mumuk.data.repository.BlogRepository
import com.google.android.flexbox.FlexDirection
import com.google.android.flexbox.FlexWrap
import com.google.android.flexbox.FlexboxLayoutManager
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class RecipeFragment : Fragment() {
    private var _binding: FragmentRecipeBinding? = null
    private val binding get() = _binding!!

    private val recipeViewModel: RecipeViewModel by viewModels {
        RecipeViewModel.Factory(requireContext())
    }

    private var currentRecipe: Recipe? = null

    private var isBlogExpanded = false
    private lateinit var fullBlogList: List<Blog>

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentRecipeBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.backBtn.setOnClickListener {
            findNavController().navigateUp()
        }

        val ingredientRV = binding.ingredientRV
        val flexboxLayoutManager = FlexboxLayoutManager(requireContext()).apply {
            flexDirection = FlexDirection.ROW
            flexWrap = FlexWrap.WRAP
        }
        ingredientRV.layoutManager = flexboxLayoutManager

        recipeViewModel.allIngredients.observe(viewLifecycleOwner) { ingredients ->
            ingredientRV.adapter = IngredientAdapter(ingredients)
            Log.d("RecipeFragment", "Ingredients updated: $ingredients")
        }

        fullBlogList = BlogRepository.getBlogList()
        val blogAdapter = BlogAdapter(emptyList())
        binding.blogRV.adapter = blogAdapter
        binding.blogRV.layoutManager = LinearLayoutManager(context)
        setBlogListAndButton(blogAdapter)

        binding.plusBtn.setOnClickListener {
            isBlogExpanded = true
            setBlogListAndButton(blogAdapter)
        }

        val shopAdapter = ShopAdapter()
        binding.shopRV.apply {
            adapter = shopAdapter
            layoutManager = GridLayoutManager(context, 2)
        }

        recipeViewModel.shopItemList.observe(viewLifecycleOwner) { shopList ->
            shopAdapter.submitList(shopList)
            Log.d("RecipeFragment", "Shop items updated: $shopList")
        }

        recipeViewModel.userRecipeDetail.observe(viewLifecycleOwner) { detail ->
            Log.d("RecipeFragment", "UserRecipeDetail 업데이트: $detail")
            binding.recipeTitle.text = detail.title
        }

        val recipeId = arguments?.getLong("recipeId") ?: 4L
        Log.d("RecipeFragment", "onViewCreated() - recipeId: $recipeId")
        recipeViewModel.fetchRecipeDetail(recipeId)

        recipeViewModel.selectedRecipe.observe(viewLifecycleOwner) { recipe ->
            currentRecipe = recipe
            binding.recipeTitle.text = recipe.title
            binding.imageView7.setImageResource(
                if (recipe.isLiked) com.example.mumuk.R.drawable.btn_heart_fill
                else com.example.mumuk.R.drawable.btn_heart_blank
            )
        }

        binding.likeBtn.setOnClickListener {
            currentRecipe?.let { recipe ->
                val context = requireContext()
                recipe.isLiked = !recipe.isLiked
                binding.imageView7.setImageResource(
                    if (recipe.isLiked) com.example.mumuk.R.drawable.btn_heart_fill
                    else com.example.mumuk.R.drawable.btn_heart_blank
                )

                recipeViewModel.updateRecipeLike(recipe.id, recipe.isLiked)

                val api = RetrofitClient.getUserRecipeApi(context)
                val request = ClickLikeRequest(recipeId = recipe.id)
                api.clickLike(request).enqueue(object : Callback<ClickLikeResponse> {
                    override fun onResponse(
                        call: Call<ClickLikeResponse>,
                        response: Response<ClickLikeResponse>
                    ) {
                        Log.d("RecipeFragment", "Like API success: ${response.body()}")
                    }

                    override fun onFailure(
                        call: Call<ClickLikeResponse>,
                        t: Throwable
                    ) {
                        Log.e("RecipeFragment", "Like API error: ${t.localizedMessage}", t)
                    }
                })
            }
        }
    }

    private fun setBlogListAndButton(blogAdapter: BlogAdapter) {
        if (fullBlogList.size < 5) {
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

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}