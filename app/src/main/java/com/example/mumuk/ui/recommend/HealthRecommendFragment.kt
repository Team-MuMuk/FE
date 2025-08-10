package com.example.mumuk.ui.recommend

import android.animation.ObjectAnimator
import android.app.Dialog
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.net.Uri
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import com.example.mumuk.R
import com.example.mumuk.databinding.FragmentHealthRecommendBinding
import com.example.mumuk.data.repository.HealthAiRepository
import com.example.mumuk.data.model.Recipe
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.os.bundleOf

class HealthRecommendFragment : Fragment() {
    private var _binding: FragmentHealthRecommendBinding? = null
    private val binding get() = _binding!!

    private lateinit var aiRecipeList: List<Recipe>
    private lateinit var aiRecipeAdapter: HealthAiAdapter
    private var isExpanded = false

    private val pickImageLauncher = registerForActivityResult(
        ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        uri?.let {
            uploadImageToServer(it)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentHealthRecommendBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.backBtn.setOnClickListener {
            findNavController().popBackStack()
        }

        aiRecipeList = HealthAiRepository().getAiRecipes()
        aiRecipeAdapter = HealthAiAdapter(
            emptyList(),
            onItemClick = { recipe ->
                Log.d("IngredientRecommend", "Recipe clicked. ID: ${recipe.id}")
                val bundle = bundleOf("recipeId" to recipe.id)
                findNavController().navigate(R.id.action_ingredientRecommendFragment_to_recipeFragment, bundle)
            },
            onHeartClick = { recipe, position ->
                // 여기에서 찜 상태가 변경될 때 필요한 동작 추가 가능
            }
        )

        binding.aiRecipeRV.layoutManager = GridLayoutManager(requireContext(), 2)
        binding.aiRecipeRV.adapter = aiRecipeAdapter

        updateAiRecipeList()

        binding.plusBtn.setOnClickListener {
            isExpanded = true
            updateAiRecipeList()
        }

        binding.addImg.setOnClickListener {
            pickImageLauncher.launch("image/*")
        }
    }

    private fun updateAiRecipeList() {
        if (aiRecipeList.size > 6 && !isExpanded) {
            aiRecipeAdapter.updateList(aiRecipeList.take(6))
            binding.plusBtn.visibility = View.VISIBLE
        } else {
            aiRecipeAdapter.updateList(aiRecipeList)
            binding.plusBtn.visibility = View.GONE
        }
    }

    private fun uploadImageToServer(uri: Uri) {
        // TODO: 서버로 이미지를 업로드하는 코드 구현

        showAiRecommendDialog()
    }

    private fun showAiRecommendDialog() {
        val dialog = Dialog(requireContext())
        dialog.setContentView(R.layout.dialog_ai_recommend)
        dialog.setCancelable(false)

        dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        dialog.window?.setDimAmount(0f)
        dialog.window?.setLayout(
            ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.MATCH_PARENT
        )

        val loadingImage = dialog.findViewById<ImageView>(R.id.loadingImage)
        loadingImage?.let {
            val animator = ObjectAnimator.ofFloat(it, "rotation", 0f, 360f)
            animator.duration = 1000
            animator.repeatCount = ObjectAnimator.INFINITE
            animator.start()
            dialog.setOnDismissListener { animator.cancel() }
        }

        dialog.show()

        Handler(Looper.getMainLooper()).postDelayed({
            if (dialog.isShowing) {
                dialog.dismiss()
            }
        }, 3000)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}