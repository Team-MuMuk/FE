package com.example.mumuk.ui.mypage

import android.app.Dialog
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
import com.example.mumuk.R
import com.example.mumuk.data.api.RetrofitClient
import com.example.mumuk.data.api.TokenManager
import com.example.mumuk.data.model.auth.CommonResponse
import com.example.mumuk.databinding.DialogDeleteAccountBinding
import com.example.mumuk.databinding.DialogLogoutBinding
import com.example.mumuk.databinding.FragmentMyPageBinding
import com.example.mumuk.ui.login.LoginActivity
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
            findNavController().navigate(R.id.action_myPage_to_profile)
        }

        binding.btnFavorites.setOnClickListener {
            findNavController().navigate(R.id.bookmarkRecipeFragment)
        }


        binding.itemLogout.setOnClickListener {
            val logoutBinding = DialogLogoutBinding.inflate(layoutInflater)

            val dialog = Dialog(requireContext())
            dialog.setContentView(logoutBinding.root)
            dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
            dialog.show()

            logoutBinding.btnDialogOk.setOnClickListener {
                val refreshToken = TokenManager.getRefreshToken(requireContext()) ?: ""
                val loginType = "LOCAL"

                Log.d("LogoutRequest", "Sending logout with refreshToken: $refreshToken, loginType: $loginType")

                RetrofitClient.getAuthApi(requireContext()).logout(refreshToken, loginType)
                    .enqueue(object : Callback<CommonResponse> {
                        override fun onResponse(
                            call: Call<CommonResponse>,
                            response: Response<CommonResponse>
                        ) {
                            val body = response.body()

                            Log.d("LogoutResponse", "isSuccessful: ${response.isSuccessful}")
                            Log.d("LogoutResponse", "code: ${response.code()}, message: ${response.message()}")
                            Log.d("LogoutResponse", "body: $body")

                            if (response.isSuccessful && body?.message?.contains("성공") == true) {
                                TokenManager.clearTokens(requireContext())

                                Log.d("Logout", "AccessToken after logout: ${TokenManager.getAccessToken(requireContext())}")

                                val intent = Intent(requireContext(), LoginActivity::class.java)
                                intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                                startActivity(intent)
                            } else {
                                Toast.makeText(requireContext(), "로그아웃 실패: ${body?.message ?: "알 수 없는 오류"}", Toast.LENGTH_SHORT).show()
                            }
                        }

                        override fun onFailure(call: Call<CommonResponse>, t: Throwable) {
                            Log.e("LogoutFailure", "네트워크 오류", t)
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
                            Log.d("Withdraw", "response: $result")

                            if (response.isSuccessful && result?.message?.contains("성공") == true) {
                                Toast.makeText(requireContext(), "회원탈퇴가 완료되었습니다.", Toast.LENGTH_SHORT).show()

                                TokenManager.clearTokens(requireContext())

                                val intent = Intent(requireContext(), LoginActivity::class.java)
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
                replace(R.id.signup_container, SubChangePw1Fragment())
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

        val recentList = listOf(
            RecentRecipe("연어 포케", R.drawable.bg_mosaic, liked = true),
            RecentRecipe("훈제오리 포케", R.drawable.bg_mosaic, liked = false),
            RecentRecipe("그린포케", R.drawable.bg_mosaic, liked = true),
            RecentRecipe("플레인 포케", R.drawable.bg_mosaic, liked = false),
            RecentRecipe("참치 포케", R.drawable.bg_mosaic, liked = true),
            RecentRecipe("스테이크 포케", R.drawable.bg_mosaic, liked = true),
            RecentRecipe("아보카도 포케", R.drawable.bg_mosaic, liked = false)
        )

        binding.rvRecentRecipes.apply {
            adapter = RecentRecipeAdapter(recentList) { recipe ->
                findNavController().navigate(R.id.recipeFragment)
            }
            layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
        }

        // 서버에서 프로필 정보 불러오기
        loadUserProfile()
    }

    private fun loadUserProfile() {
        val accessToken = TokenManager.getAccessToken(requireContext())
        val userId = com.example.mumuk.utils.JwtUtils.getUserIdFromToken(accessToken ?: "")

        if (userId == null) {
            Log.e("MyPage", "userId 추출 실패")
            return
        }

        RetrofitClient.getUserApi(requireContext()).getUserProfile(userId)
            .enqueue(object : Callback<com.example.mumuk.data.model.mypage.UserProfileResponse> {
                override fun onResponse(
                    call: Call<com.example.mumuk.data.model.mypage.UserProfileResponse>,
                    response: Response<com.example.mumuk.data.model.mypage.UserProfileResponse>
                ) {
                    if (response.isSuccessful) {
                        val profile = response.body()?.data ?: return
                        Log.d("MyPage", "✅ 프로필 불러오기 성공: $profile")

                        binding.tvNickname.text = "${profile.nickName}님!"
                        binding.tvSubtitle.text = profile.statusMessage

                        val profileRes = when (profile.profileImage ?: "orange") {
                            "orange" -> R.drawable.ic_user_profile_orange
                            "white" -> R.drawable.ic_user_profile_white
                            "green" -> R.drawable.ic_user_profile_green
                            else -> R.drawable.ic_user_profile_orange
                        }
                        binding.imgProfile.setImageResource(profileRes)

                    } else {
                        Log.e("MyPage", "프로필 API 실패: ${response.code()}")
                    }
                }

                override fun onFailure(call: Call<com.example.mumuk.data.model.mypage.UserProfileResponse>, t: Throwable) {
                    Log.e("MyPage", "네트워크 오류", t)
                }
            })
    }



}
