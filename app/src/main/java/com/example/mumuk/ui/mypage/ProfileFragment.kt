package com.example.mumuk.ui.mypage

import android.app.Dialog
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.example.mumuk.R
import com.example.mumuk.data.api.RetrofitClient
import com.example.mumuk.data.api.TokenManager
import com.example.mumuk.data.model.auth.CommonResponse
import com.example.mumuk.data.model.mypage.UserProfileResponse
import com.example.mumuk.data.model.mypage.UserProfileUpdateRequest
import com.example.mumuk.databinding.FragmentProfileBinding
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import android.util.Log

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

        // accessToken 체크(선택)
        val accessToken = TokenManager.getAccessToken(requireContext())
        if (accessToken.isNullOrBlank()) {
            Log.e("Profile", "accessToken 없음")
            return
        }

        // 프로필 로드
        RetrofitClient.getUserApi(requireContext()).getUserProfile()
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

                            val profileRes = when (profile.profileImage?.ifBlank { "orange" }) {
                                "orange" -> R.drawable.ic_user_profile_orange
                                "white"  -> R.drawable.ic_user_profile_white
                                "green"  -> R.drawable.ic_user_profile_green
                                else     -> R.drawable.ic_user_profile_orange
                            }
                            binding.icProfile.setImageResource(profileRes)
                            selectedProfileImageResId = profileRes
                        }
                    } else {
                        Log.e("Profile", "프로필 응답 실패: ${response.code()} / ${response.message()}")
                    }
                }

                override fun onFailure(call: Call<UserProfileResponse>, t: Throwable) {
                    Log.e("Profile", "프로필 API 호출 실패", t)
                }
            })

        // 편집 모드 초기화
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
            isEditingProfileImage = true
            binding.btnEditProfileImage.setImageResource(R.drawable.ic_check)
            showProfileImageDialog()
        }

        binding.btnBack.setOnClickListener { findNavController().navigateUp() }
        binding.btnCancel.setOnClickListener { findNavController().navigateUp() }

        binding.btnDone.setOnClickListener {
            val name = binding.editName.text.toString()
            val nickname = binding.editNickname.text.toString()
            val status = binding.editStatus.text.toString()
            val profileImageKey = when (selectedProfileImageResId) {
                R.drawable.ic_user_profile_orange -> "orange"
                R.drawable.ic_user_profile_white  -> "white"
                R.drawable.ic_user_profile_green  -> "green"
                else -> "orange"
            }

            val request = UserProfileUpdateRequest(
                name = name,
                nickName = nickname,
                profileImage = profileImageKey,
                statusMessage = status
            )

            RetrofitClient.getUserApi(requireContext()).updateUserProfile(request)
                .enqueue(object : Callback<CommonResponse> {
                    override fun onResponse(call: Call<CommonResponse>, response: Response<CommonResponse>) {
                        if (response.isSuccessful && response.body()?.message?.contains("성공") == true) {
                            Log.d("Profile", "프로필 수정 성공: ${response.body()}")
                            showConfirmDialogAndBack()
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

    private fun showConfirmDialogAndBack() {
        val dialog = Dialog(requireContext())
        dialog.setContentView(R.layout.dialog_confirm)
        dialog.window?.setBackgroundDrawable(ColorDrawable(0x00000000)) // transparent
        dialog.window?.setDimAmount(0f)
        dialog.window?.setLayout(
            ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.MATCH_PARENT
        )

        val tvMessage = dialog.findViewById<TextView>(R.id.tv_dialog_message)
        val btnOk = dialog.findViewById<TextView>(R.id.btn_dialog_ok)

        tvMessage.text = "프로필이 수정되었습니다."
        btnOk.text = "확인"

        btnOk.setOnClickListener {
            dialog.dismiss()
            if (!isAdded) return@setOnClickListener
            findNavController().navigate(R.id.action_profile_to_myPage_with_updated_profile)

        }

        dialog.show()
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

        dialog.window?.setBackgroundDrawable(ColorDrawable(0x00000000))
        dialog.window?.setDimAmount(0f)
        dialog.window?.setLayout(
            ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.MATCH_PARENT
        )

        val window = dialog.window
        val params = window?.attributes
        params?.gravity = Gravity.TOP or Gravity.CENTER_HORIZONTAL
        params?.y = 640
        if (params != null) window?.attributes = params

        dialog.show()

        dialog.setOnDismissListener { resetProfileEditPen() }
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
            penIcon.setImageResource(R.drawable.ic_check)
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
