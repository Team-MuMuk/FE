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
import androidx.fragment.app.Fragment
import androidx.fragment.app.commit
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.example.mumuk.R
import com.example.mumuk.data.api.RetrofitClient
import com.example.mumuk.data.api.TokenManager
import com.example.mumuk.data.model.auth.CommonResponse
import com.example.mumuk.data.model.mypage.UserProfileData
import com.example.mumuk.data.model.mypage.UserProfileResponse
import com.example.mumuk.databinding.DialogDeleteAccountBinding
import com.example.mumuk.databinding.DialogLogoutBinding
import com.example.mumuk.databinding.FragmentMyPageBinding
import com.example.mumuk.ui.login.LoginIntroActivity
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class MyPageFragment : Fragment() {
    private var _binding: FragmentMyPageBinding? = null
    private val binding get() = _binding!!

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
            findNavController().navigate(R.id.bookmarkRecipeFragment)
        }

        binding.itemLogout.setOnClickListener {
            val logoutBinding = DialogLogoutBinding.inflate(layoutInflater)
            val dialog = Dialog(requireContext())
            dialog.setContentView(logoutBinding.root)
            dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
            dialog.window?.setDimAmount(0.3f)
            dialog.show()

            logoutBinding.btnDialogOk.setOnClickListener {
                val refreshToken = TokenManager.getRefreshToken(requireContext()) ?: ""
                val loginType = "LOCAL"

                RetrofitClient.getAuthApi(requireContext()).logout(refreshToken, loginType)
                    .enqueue(object : Callback<CommonResponse> {
                        override fun onResponse(
                            call: Call<CommonResponse>,
                            response: Response<CommonResponse>
                        ) {
                            if (response.code() == 401) {
                                TokenManager.clearTokens(requireContext())
                                val prefs = requireContext().getSharedPreferences("auth", Context.MODE_PRIVATE)
                                prefs.edit().clear().apply()
                                val intent = Intent(requireContext(), LoginIntroActivity::class.java)
                                intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                                startActivity(intent)
                                return
                            }
                            if (response.isSuccessful && response.body()?.message?.contains("성공") == true) {
                                TokenManager.clearTokens(requireContext())
                                val prefs = requireContext().getSharedPreferences("auth", Context.MODE_PRIVATE)
                                prefs.edit().clear().apply()
                                val intent = Intent(requireContext(), LoginIntroActivity::class.java)
                                intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                                startActivity(intent)
                            } else {
                                Toast.makeText(requireContext(), "로그아웃 실패: ${response.body()?.message ?: "알 수 없는 오류"}", Toast.LENGTH_SHORT).show()
                            }
                        }
                        override fun onFailure(call: Call<CommonResponse>, t: Throwable) {
                            Toast.makeText(requireContext(), "네트워크 오류: ${t.message}", Toast.LENGTH_SHORT).show()
                        }
                    })
                dialog.dismiss()
            }

            val widthInPx = TypedValue.applyDimension(
                TypedValue.COMPLEX_UNIT_DIP,
                220f,
                resources.displayMetrics
            ).toInt()
            dialog.window?.setLayout(widthInPx, ViewGroup.LayoutParams.WRAP_CONTENT)
        }

        binding.itemDeleteAccount.setOnClickListener {
            val deleteBinding = DialogDeleteAccountBinding.inflate(layoutInflater)
            val dialog = Dialog(requireContext())
            dialog.setContentView(deleteBinding.root)
            dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
            dialog.window?.setDimAmount(0.3f)
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
            dialog.window?.setLayout(widthInPx, ViewGroup.LayoutParams.WRAP_CONTENT)
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
            childFragmentManager.commit {
                replace(R.id.mypage_container, SubChangePw1Fragment())
                addToBackStack(null)
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
        dialog.window?.setDimAmount(0.3f)
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

        val recentList = mutableListOf(
            RecentRecipe("연어 포케", R.drawable.bg_mosaic, liked = true),
            RecentRecipe("훈제오리 포케", R.drawable.bg_mosaic, liked = false),
            RecentRecipe("그린포케", R.drawable.bg_mosaic, liked = true),
            RecentRecipe("플레인 포케", R.drawable.bg_mosaic, liked = false),
            RecentRecipe("참치 포케", R.drawable.bg_mosaic, liked = true),
            RecentRecipe("스테이크 포케", R.drawable.bg_mosaic, liked = true),
            RecentRecipe("아보카도 포케", R.drawable.bg_mosaic, liked = false)
        )

        binding.rvRecentRecipes.apply {
            adapter = RecentRecipeAdapter(recentList,
                onItemClick = { recipe ->
                    findNavController().navigate(R.id.recipeFragment)
                },
                onHeartClick = { recipe, position ->
                }
            )
            layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
        }

        loadUserProfile()
    }

    private fun loadUserProfile() {
        val loginType = TokenManager.getLoginType(requireContext()) ?: "LOCAL"

        if (loginType == "KAKAO" || loginType == "NAVER") {
            val savedNickname = TokenManager.getNickName(requireContext())
            val nicknameText = if (!savedNickname.isNullOrBlank()) "${savedNickname}님!" else "사용자님!"
            val profileImageUrl = TokenManager.getProfileImage(requireContext())
            binding.tvNickname.text = nicknameText
            binding.recipeText.text = "${nicknameText.replace("님!", "")}님이 최근 본 레시피"
            binding.tvSubtitle.text = ""
            Glide.with(this)
                .load(profileImageUrl)
                .placeholder(R.drawable.ic_user_profile_orange)
                .error(R.drawable.ic_user_profile_orange)
                .circleCrop()
                .into(binding.imgProfile)
        } else {
            RetrofitClient.getUserApi(requireContext()).getUserProfile()
                .enqueue(object : Callback<UserProfileResponse> {
                    override fun onResponse(
                        call: Call<UserProfileResponse>,
                        response: Response<UserProfileResponse>
                    ) {
                        if (response.isSuccessful) {
                            bindProfile(response.body()?.data) // ✅ UserProfileData
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