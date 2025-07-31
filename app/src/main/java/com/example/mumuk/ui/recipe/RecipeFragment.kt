package com.example.mumuk.ui.recipe

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.mumuk.databinding.FragmentRecipeBinding
import com.example.mumuk.data.api.RetrofitClient
import com.example.mumuk.data.model.Recipe
import com.example.mumuk.data.model.recipe.ClickLikeRequest
import com.example.mumuk.data.model.recipe.ClickLikeResponse
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class RecipeFragment : Fragment() {
    private var _binding: FragmentRecipeBinding? = null
    private val binding get() = _binding!!

    private val recipeViewModel: RecipeViewModel by viewModels()

    private var currentRecipe: Recipe? = null

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

        val nutritionInfoAdapter = NutritionInfoAdapter()
        binding.infoRV.apply {
            adapter = nutritionInfoAdapter
            layoutManager = LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
        }

        recipeViewModel.nutritionInfoList.observe(viewLifecycleOwner) { infoList ->
            nutritionInfoAdapter.submitList(infoList)
        }

        val shopAdapter = ShopAdapter()
        binding.shopRV.apply {
            adapter = shopAdapter
            layoutManager = GridLayoutManager(context, 2)
        }

        recipeViewModel.shopItemList.observe(viewLifecycleOwner) { shopList ->
            shopAdapter.submitList(shopList)
        }

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
                    }

                    override fun onFailure(
                        call: Call<ClickLikeResponse>,
                        t: Throwable
                    ) {
                    }
                })
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}