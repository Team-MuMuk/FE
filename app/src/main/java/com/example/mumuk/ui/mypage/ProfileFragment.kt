package com.example.mumuk.ui.mypage

import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.ImageView
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.example.mumuk.R
import com.example.mumuk.databinding.FragmentProfileBinding
import android.graphics.Color
import android.util.Log
import android.view.Gravity
import com.example.mumuk.data.api.RetrofitClient
import com.example.mumuk.data.api.TokenManager
import com.example.mumuk.data.model.auth.CommonResponse
import com.example.mumuk.data.model.mypage.UserProfileResponse
import com.example.mumuk.data.model.mypage.UserProfileUpdateRequest
import com.example.mumuk.utils.JwtUtils
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class ProfileFragment : Fragment() {

    private var _binding: FragmentProfileBinding? = null
    private val binding get() = _binding!!

    private var isEditingName = false
    private var isEditingNickname = false
    private var isEditingStatus = false
    private var isEditingProfileImage = false
    private var selectedProfileImageResId: Int = R.drawable.ic_user_profile_orange

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentProfileBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val accessToken = TokenManager.getAccessToken(requireContext())
        if (accessToken.isNullOrBlank()) {
            Log.e("Profile", "accessToken 없음")
            return
        }

        val userId = JwtUtils.getUserIdFromToken(accessToken)

        if (userId == null) {
            Log.e("Profile", "accessToken에서 userId 추출 실패")
        } else {
            Log.d("Profile", "추출된 userId: $userId")

            RetrofitClient.getUserApi(requireContext()).getUserProfile(userId)
                .enqueue(object : Callback<UserProfileResponse> {
                    override fun onResponse(
                        call: Call<UserProfileResponse>,
                        response: Response<UserProfileResponse>
                    ) {
                        Log.d("Profile", "프로필 API 응답 코드: ${response.code()}")

                        if (response.isSuccessful) {
                            response.body()?.data?.let { profile ->
                                Log.d("Profile", "프로필 정보 수신 성공: $profile")

                                if (profile.name.isNullOrBlank()) {
                                    binding.editName.setText("")
                                    binding.editName.hint = "이름을 입력하세요"
                                } else {
                                    binding.editName.setText(profile.name)
                                }

                                if (profile.nickName.isNullOrBlank()) {
                                    binding.editNickname.setText("")
                                    binding.editNickname.hint = "닉네임을 입력하세요"
                                } else {
                                    binding.editNickname.setText(profile.nickName)
                                }

                                if (profile.statusMessage.isNullOrBlank()) {
                                    binding.editStatus.setText("")
                                    binding.editStatus.hint = "상태 메시지를 입력하세요"
                                } else {
                                    binding.editStatus.setText(profile.statusMessage)
                                }
                            }
                        } else {
                            Log.e("Profile", "프로필 응답 실패: ${response.code()} / ${response.message()}")
                        }
                    }

                    override fun onFailure(call: Call<UserProfileResponse>, t: Throwable) {
                        Log.e("Profile", "프로필 API 호출 실패", t)
                    }
                })
        }

        setEditMode(binding.editName, binding.editNamePen, false, "")
        setEditMode(binding.editNickname, binding.editNicknamePen, false, "")
        setEditMode(binding.editStatus, binding.editStatusPen, false, "")

        binding.editNamePen.setOnClickListener {
            isEditingName = !isEditingName
            setEditMode(binding.editName, binding.editNamePen, isEditingName, "이름을 입력하세요")
        }

        binding.editNicknamePen.setOnClickListener {
            isEditingNickname = !isEditingNickname
            setEditMode(binding.editNickname, binding.editNicknamePen, isEditingNickname, "닉네임을 입력하세요")
        }

        binding.editStatusPen.setOnClickListener {
            isEditingStatus = !isEditingStatus
            setEditMode(binding.editStatus, binding.editStatusPen, isEditingStatus, "상태 메시지를 입력하세요")
        }

        binding.btnEditProfileImage.setOnClickListener {
            binding.btnEditProfileImage.setImageResource(R.drawable.ic_profile_pen_blue)
            showProfileImageDialog()
        }

        binding.btnBack.setOnClickListener { findNavController().navigateUp() }
        binding.btnCancel.setOnClickListener { findNavController().navigateUp() }

        binding.btnDone.setOnClickListener {
            val name = binding.editName.text.toString()
            val nickname = binding.editNickname.text.toString()
            val status = binding.editStatus.text.toString()
            val profileImageUrl = when (selectedProfileImageResId) {
                R.drawable.ic_user_profile_orange -> "orange"
                R.drawable.ic_user_profile_white -> "white"
                R.drawable.ic_user_profile_green -> "green"
                else -> "orange"
            }

            val accessToken = TokenManager.getAccessToken(requireContext())
            val userId = JwtUtils.getUserIdFromToken(accessToken ?: "")

            if (userId == null) {
                Log.e("Profile", "userId 추출 실패 (수정 요청 취소)")
                return@setOnClickListener
            }

            val request = UserProfileUpdateRequest(
                name = name,
                nickName = nickname,
                profileImage = profileImageUrl,
                statusMessage = status
            )

            RetrofitClient.getUserApi(requireContext()).updateUserProfile(userId, request)
                .enqueue(object : Callback<CommonResponse> {
                    override fun onResponse(call: Call<CommonResponse>, response: Response<CommonResponse>) {
                        if (response.isSuccessful && response.body()?.message?.contains("성공") == true) {
                            Log.d("Profile", "프로필 수정 성공: ${response.body()}")
                            findNavController().navigate(R.id.action_profile_to_myPage_with_updated_profile)
                        } else {
                            Log.e("Profile", "프로필 수정 실패: ${response.code()} / ${response.body()?.message}")
                        }
                    }

                    override fun onFailure(call: Call<CommonResponse>, t: Throwable) {
                        Log.e("Profile", "프로필 수정 API 호출 실패", t)
                    }
                })
        }
    }

    private fun showProfileImageDialog() {
        val dialogView = layoutInflater.inflate(R.layout.dialog_select_profile_image, null)
        val dialog = android.app.AlertDialog.Builder(requireContext())
            .setView(dialogView)
            .create()

        val ivOrange = dialogView.findViewById<ImageView>(R.id.iv_profile_orange)
        val ivWhite = dialogView.findViewById<ImageView>(R.id.iv_profile_white)
        val ivGreen = dialogView.findViewById<ImageView>(R.id.iv_profile_green)

        ivOrange.setOnClickListener {
            selectedProfileImageResId = R.drawable.ic_user_profile_orange
            binding.icProfile.setImageResource(selectedProfileImageResId)
            resetProfileEditPen()
            dialog.dismiss()
        }

        ivWhite.setOnClickListener {
            selectedProfileImageResId = R.drawable.ic_user_profile_white
            binding.icProfile.setImageResource(selectedProfileImageResId)
            resetProfileEditPen()
            dialog.dismiss()
        }

        ivGreen.setOnClickListener {
            selectedProfileImageResId = R.drawable.ic_user_profile_green
            binding.icProfile.setImageResource(selectedProfileImageResId)
            resetProfileEditPen()
            dialog.dismiss()
        }

        val window = dialog.window
        window?.setLayout(
            ViewGroup.LayoutParams.WRAP_CONTENT,
            ViewGroup.LayoutParams.WRAP_CONTENT
        )
        dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        dialog.window?.setDimAmount(0f)

        val params = window?.attributes
        params?.gravity = Gravity.TOP or Gravity.CENTER_HORIZONTAL
        params?.y = 640
        window?.attributes = params

        dialog.show()

        dialog.setOnDismissListener {
            resetProfileEditPen()
        }
    }

    private fun resetProfileEditPen() {
        isEditingProfileImage = false
        binding.btnEditProfileImage.setImageResource(R.drawable.ic_profile_pen)
    }

    private fun setEditMode(
        editText: EditText,
        penIcon: ImageView,
        isEditing: Boolean,
        hintText: String
    ) {
        if (isEditing) {
            penIcon.setImageResource(R.drawable.ic_profile_pen_blue)
            editText.apply {
                isEnabled = true
                hint = hintText
                setText("")
                requestFocus()
            }
        } else {
            penIcon.setImageResource(R.drawable.ic_profile_pen)
            editText.apply {
                isEnabled = false
                hint = ""
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
