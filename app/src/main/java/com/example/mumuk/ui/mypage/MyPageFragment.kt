package com.example.mumuk.ui.mypage

import android.app.Dialog
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.util.Log
import android.util.TypedValue
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.example.mumuk.R
import com.example.mumuk.data.api.RetrofitClient
import com.example.mumuk.data.api.TokenManager
import com.example.mumuk.data.model.auth.CommonResponse
import com.example.mumuk.data.model.mypage.RecentRecipe
import com.example.mumuk.data.model.search.RecentRecipeResponse
import com.example.mumuk.data.model.mypage.UserProfileData
import com.example.mumuk.data.model.mypage.UserProfileResponse
import com.example.mumuk.databinding.DialogDeleteAccountBinding
import com.example.mumuk.databinding.FragmentMyPageBinding
import com.example.mumuk.ui.login.LoginIntroActivity
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import com.example.mumuk.data.model.mypage.RecentRecipeAdapter
import com.example.mumuk.data.model.recipe.ClickLikeRequest
import com.example.mumuk.data.model.recipe.ClickLikeResponse
import androidx.core.view.isVisible
import androidx.recyclerview.widget.RecyclerView

class MyPageFragment : Fragment() {
    private var _binding: FragmentMyPageBinding? = null
    private val binding get() = _binding!!
    private lateinit var recentAdapter: RecentRecipeAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentMyPageBinding.inflate(inflater, container, false)

        binding.btnProfile.setOnClickListener {
            val loginType = TokenManager.getLoginType(requireContext()) ?: "LOCAL"

            if (loginType == "NAVER" || loginType == "KAKAO") {
                showSimpleConfirmDialog(
                    message = "소셜로그인 이용자는\n프로필 수정이 불가합니다.",
                    buttonText = "확인"
                )
            } else {
                findNavController().navigate(R.id.action_myPage_to_profile)
            }
        }

        binding.btnFavorites.setOnClickListener {
            findNavController().navigate(R.id.action_myPage_to_bookmarkRecipe)
        }

        binding.itemLogout.setOnClickListener {
            showSimpleConfirmDialog(
                message = "로그아웃되었습니다.",
                buttonText = "확인"
            ) {
                val refreshToken = TokenManager.getRefreshToken(requireContext()) ?: ""
                val loginType = TokenManager.getLoginType(requireContext()) ?: "LOCAL"

                RetrofitClient.getAuthApi(requireContext()).logout(refreshToken, loginType)
                    .enqueue(object : Callback<CommonResponse> {
                        override fun onResponse(
                            call: Call<CommonResponse>,
                            response: Response<CommonResponse>
                        ) {
                            // 401도 성공처럼 처리
                            if (response.code() == 401 ||
                                (response.isSuccessful && response.body()?.message?.contains("성공") == true)
                            ) {
                                TokenManager.clearTokens(requireContext())
                                val prefs = requireContext().getSharedPreferences("auth", Context.MODE_PRIVATE)
                                prefs.edit().clear().apply()
                                val intent = Intent(requireContext(), LoginIntroActivity::class.java)
                                intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                                startActivity(intent)
                            } else {
                                Toast.makeText(
                                    requireContext(),
                                    "로그아웃 실패: ${response.body()?.message ?: "알 수 없는 오류"}",
                                    Toast.LENGTH_SHORT
                                ).show()
                            }
                        }

                        override fun onFailure(call: Call<CommonResponse>, t: Throwable) {
                            Toast.makeText(
                                requireContext(),
                                "네트워크 오류: ${t.message}",
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    })
            }
        }


        binding.itemDeleteAccount.setOnClickListener {
            val deleteBinding = DialogDeleteAccountBinding.inflate(layoutInflater)
            val dialog = Dialog(requireContext())
            dialog.setContentView(deleteBinding.root)
            dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))

            dialog.window?.setDimAmount(0f)
            dialog.window?.setLayout(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT
            )

            dialog.show()

            deleteBinding.btnDialogCancel.setOnClickListener {
                dialog.dismiss()
            }

            deleteBinding.btnDialogLogin.setOnClickListener {
                RetrofitClient.getAuthApi(requireContext()).withdraw()
                    .enqueue(object : Callback<CommonResponse> {
                        override fun onResponse(
                            call: Call<CommonResponse>,
                            response: Response<CommonResponse>
                        ) {
                            val result = response.body()
                            if (response.isSuccessful && result?.message?.contains("성공") == true) {
                                Toast.makeText(requireContext(), "회원탈퇴가 완료되었습니다.", Toast.LENGTH_SHORT).show()
                                TokenManager.clearTokens(requireContext())
                                val prefs = requireContext().getSharedPreferences("auth", Context.MODE_PRIVATE)
                                prefs.edit().clear().apply()
                                val intent = Intent(requireContext(), LoginIntroActivity::class.java)
                                intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                                startActivity(intent)
                            } else {
                                Toast.makeText(
                                    requireContext(),
                                    "회원탈퇴 실패: ${result?.message ?: "서버 오류"}",
                                    Toast.LENGTH_SHORT
                                ).show()
                            }
                        }
                        override fun onFailure(call: Call<CommonResponse>, t: Throwable) {
                            Toast.makeText(
                                requireContext(),
                                "네트워크 오류: ${t.message}",
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    })
                dialog.dismiss()
            }

            val widthInPx = TypedValue.applyDimension(
                TypedValue.COMPLEX_UNIT_DIP,
                220f,
                resources.displayMetrics
            ).toInt()
        }

        binding.itemVersion.setOnClickListener {
            showSimpleConfirmDialog(
                message = "현재 서비스의\n버전은 V.1.0.3 입니다",
                buttonText = "확인"
            )
        }

        binding.itemNotification.setOnClickListener {
            showSimpleConfirmDialog(
                message = "푸시알림 설정을\n하시겠습니까?",
                buttonText = "동의"
            ) {
                Toast.makeText(requireContext(), "푸시알림 설정에 동의하셨습니다", Toast.LENGTH_SHORT).show()
            }
        }

        binding.itemPwChange.setOnClickListener {
            val loginType = TokenManager.getLoginType(requireContext()) ?: "LOCAL"
            if (loginType == "KAKAO" || loginType == "NAVER") {
                showSimpleConfirmDialog(
                    message = "소셜로그인 이용자는\n비밀번호 변경이 불가합니다.",
                    buttonText = "확인"
                )
            } else {
                findNavController().navigate(R.id.action_myPage_to_subChangePw1)

            }
        }

        return binding.root
    }

    private fun showSimpleConfirmDialog(
        message: String,
        buttonText: String = "확인",
        onButtonClick: (() -> Unit)? = null
    ) {
        val dialog = Dialog(requireContext())
        dialog.setContentView(R.layout.dialog_confirm)
        dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))

        dialog.window?.setDimAmount(0f)
        dialog.window?.setLayout(
            ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.MATCH_PARENT
        )

        val tvMessage = dialog.findViewById<TextView>(R.id.tv_dialog_message)
        val btnOk = dialog.findViewById<TextView>(R.id.btn_dialog_ok)
        tvMessage.text = message
        btnOk.text = buttonText
        btnOk.setOnClickListener {
            dialog.dismiss()
            onButtonClick?.invoke()
        }
        dialog.show()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupRecentRecycler()
        loadRecentRecipes()
        loadUserProfile()
    }

    private fun setupRecentRecycler() {
        binding.rvRecentRecipes.layoutManager =
            LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
        recentAdapter = RecentRecipeAdapter(
            mutableListOf(),
            onItemClick = { item ->
                val args = bundleOf("recipeId" to item.recipeId)
                findNavController().navigate(R.id.action_myPage_to_recipeFragment, args)
            },

            onHeartClick = { item, pos ->
                val id = item.recipeId
                val old = item.liked
                recentAdapter.updateLikeAt(pos, !old)

                RetrofitClient.getUserRecipeApi(requireContext())
                    .clickLike(ClickLikeRequest(id))
                    .enqueue(object : Callback<ClickLikeResponse> {
                        override fun onResponse(
                            call: Call<ClickLikeResponse>,
                            response: Response<ClickLikeResponse>
                        ) {
                            if (response.isSuccessful && response.body()?.status == "OK") {
                            } else {
                                recentAdapter.updateLikeAt(pos, old)
                                Toast.makeText(requireContext(), "찜 실패 (${response.code()})", Toast.LENGTH_SHORT).show()
                                Log.w("MyPage", "clickLike fail code=${response.code()} body=${response.errorBody()?.string()}")
                            }
                        }
                        override fun onFailure(call: Call<ClickLikeResponse>, t: Throwable) {
                            recentAdapter.updateLikeAt(pos, old)
                            Toast.makeText(requireContext(), "네트워크 오류: ${t.message}", Toast.LENGTH_SHORT).show()
                            Log.e("MyPage","clickLike error", t)
                        }
                    })
            }
        )
        binding.rvRecentRecipes.adapter = recentAdapter
        recentAdapter.registerAdapterDataObserver(object : RecyclerView.AdapterDataObserver() {
            override fun onChanged() = updateRecentEmptyState()
            override fun onItemRangeInserted(positionStart: Int, itemCount: Int) = updateRecentEmptyState()
            override fun onItemRangeRemoved(positionStart: Int, itemCount: Int) = updateRecentEmptyState()
        })
        updateRecentEmptyState()


    }

    private fun loadRecentRecipes() {
        Log.d("MyPage", "[recent] call start")

        RetrofitClient.getRecentRecipeApi(requireContext())
            .getRecentRecipes()
            .enqueue(object : Callback<RecentRecipeResponse> {
                override fun onResponse(
                    call: Call<RecentRecipeResponse>,
                    response: Response<RecentRecipeResponse>
                ) {
                    Log.d("MyPage", "[recent] onResponse code=${response.code()} isSuccessful=${response.isSuccessful}")

                    if (!isAdded) {
                        Log.w("MyPage", "[recent] fragment not added, skip")
                        return
                    }

                    if (response.isSuccessful) {
                        val body: RecentRecipeResponse? = response.body()
                        Log.d("MyPage", "[recent] body.status=${body?.status}, code=${body?.code}, msg=${body?.message}")

                        val items: List<com.example.mumuk.data.model.search.RecentRecipe> =
                            body?.data?.recipeSummaries ?: emptyList()

                        Log.d("MyPage", "[recent] items.size=${items.size}")
                        if (items.isNotEmpty()) {
                            val first = items.first()
                        }

                        val uiList = items.map { dto ->
                            RecentRecipe(
                                recipeId = dto.id,
                                name     = dto.title,
                                image    = dto.imageUrl ?: "",
                                liked    = dto.liked
                            )
                        }

                        if (uiList.isEmpty()) {
                            Log.w("MyPage", "[recent] empty list -> adapter submit empty")
                        }

                        recentAdapter.submitList(uiList)
                        updateRecentEmptyState()

                        Log.d("MyPage", "[recent] adapter submitList done (size=${uiList.size})")

                    } else {
                        val err = response.errorBody()?.string()
                        Log.e("MyPage", "[recent] fail code=${response.code()} body=$err")
                        if (response.code() == 401) {
                            Log.e("MyPage", "[recent] 401 unauthorized -> token may be invalid")
                        }
                    }

                }

                override fun onFailure(call: Call<RecentRecipeResponse>, t: Throwable) {
                    if (!isAdded) return
                    Log.e("MyPage", "[recent] network error: ${t.message}", t)
                }
            })
    }

    private fun loadUserProfile() {
        val loginType = TokenManager.getLoginType(requireContext()) ?: "LOCAL"

        if (loginType == "KAKAO" || loginType == "NAVER") {
            val savedNickname = TokenManager.getNickName(requireContext())
            val nicknameText = if (!savedNickname.isNullOrBlank()) "${savedNickname}님!" else "사용자님!"
            val profileImageUrl = TokenManager.getProfileImage(requireContext())
            if (_binding != null) {
                binding.tvNickname.text = nicknameText
                binding.recipeText.text = "${nicknameText.replace("님!", "")}님이 최근 본 레시피"
                binding.tvSubtitle.text = ""
                Glide.with(this)
                    .load(profileImageUrl)
                    .placeholder(R.drawable.ic_user_profile_orange)
                    .error(R.drawable.ic_user_profile_orange)
                    .circleCrop()
                    .into(binding.imgProfile)
            }
        } else {
            RetrofitClient.getUserApi(requireContext()).getUserProfile()
                .enqueue(object : Callback<UserProfileResponse> {
                    override fun onResponse(
                        call: Call<UserProfileResponse>,
                        response: Response<UserProfileResponse>
                    ) {
                        if (response.isSuccessful) {
                            bindProfile(response.body()?.data)
                        } else {
                            Log.e("MyPage", "프로필 API 실패: ${response.code()}")
                        }
                    }

                    override fun onFailure(call: Call<UserProfileResponse>, t: Throwable) {
                        Log.e("MyPage", "네트워크 오류", t)
                    }
                })
        }
    }

    private fun bindProfile(profile: UserProfileData?) {
        if (profile == null) return

        val nickname = profile.nickName?.takeIf { it.isNotBlank() } ?: "사용자"
        val status   = profile.statusMessage?.takeIf { it.isNotBlank() } ?: ""
        val imageKey = profile.profileImage?.takeIf { it.isNotBlank() } ?: "orange"

        val nicknameText = "${nickname}님!"
        if (_binding != null) {
            binding.tvNickname.text = nicknameText
            binding.recipeText.text = "${nickname}님이 최근 본 레시피"
            binding.tvSubtitle.text = status

            val profileRes = when (imageKey) {
                "orange" -> R.drawable.ic_user_profile_orange
                "white"  -> R.drawable.ic_user_profile_white
                "green"  -> R.drawable.ic_user_profile_green
                else     -> R.drawable.ic_user_profile_orange
            }
            binding.imgProfile.setImageResource(profileRes)
        }
    }

    private fun updateRecentEmptyState() {
        val empty = recentAdapter.itemCount == 0
        binding.rvRecentRecipes.isVisible = !empty
        binding.tvRecentEmpty.isVisible = empty
    }

    override fun onResume() {
        super.onResume()
        if (_binding != null) loadRecentRecipes()
    }
}