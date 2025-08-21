package com.example.mumuk.ui.health

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import com.example.mumuk.R
import com.example.mumuk.databinding.FragmentHealthCompleteBinding
import com.example.mumuk.data.api.TokenManager
import com.example.mumuk.data.api.RetrofitClient
import com.example.mumuk.data.model.mypage.UserProfileResponse
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class HealthCompleteFragment : Fragment() {

    private var _binding: FragmentHealthCompleteBinding? = null
    private val binding get() = _binding!!
    
    private val healthViewModel: HealthViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentHealthCompleteBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupGenderImage()
        setupClickListeners()
        setNicknameSubtitle()
    }

    private fun setupGenderImage() {
        val selectedGender = healthViewModel.gender.value ?: "male"

        val imageResource = when (selectedGender) {
            "female" -> R.drawable.img_female
            "male" -> R.drawable.img_male
            else -> R.drawable.img_male
        }

        binding.ivCompleteIllustration.setImageResource(imageResource)
    }

    private fun setupClickListeners() {
        binding.btnGoHome.setOnClickListener {
            findNavController().navigate(R.id.action_healthComplete_to_home)
        }
    }

    private fun setNicknameSubtitle() {
        val loginType = TokenManager.getLoginType(requireContext()) ?: "LOCAL"

        // 소셜 로그인 케이스
        if (loginType == "KAKAO" || loginType == "NAVER") {
            val savedNickname = TokenManager.getNickName(requireContext())
            val nickname = if (!savedNickname.isNullOrBlank()) savedNickname else "사용자"
            binding.tvSubtitle.text = "이제 조금 더 ${nickname}님께 맞춤 정보를 제공할게요!"
        } else {
            // 서버에서 닉네임 받아오기
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
                            binding.tvSubtitle.text = "이제 조금 더 ${nickname}님께 맞춤 정보를 제공할게요!"
                        } else {
                            binding.tvSubtitle.text = "이제 조금 더 뭐먹님께 맞춤 정보를 제공할게요!"
                        }
                    }

                    override fun onFailure(call: Call<UserProfileResponse>, t: Throwable) {
                        if (!isAdded || _binding == null) return
                        binding.tvSubtitle.text = "이제 조금 더 뭐먹님께 맞춤 정보를 제공할게요!"
                    }
                })
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}