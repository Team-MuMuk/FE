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
import android.view.Gravity

class ProfileFragment : Fragment() {

    private var _binding: FragmentProfileBinding? = null
    private val binding get() = _binding!!

    // 편집 모드 상태 저장용 플래그
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


        // 기본 정보
        binding.editName.setText("김뭐먹")
        binding.editNickname.setText("김자취")
        binding.editStatus.setText("건강해지자!")


        setEditMode(binding.editName, binding.editNamePen, false, "")
        setEditMode(binding.editNickname, binding.editNicknamePen, false, "")
        setEditMode(binding.editStatus, binding.editStatusPen, false, "")

        binding.editNamePen.setOnClickListener {
            isEditingName = !isEditingName
            setEditMode(
                binding.editName,
                binding.editNamePen,
                isEditingName,
                "이름을 입력하세요"
            )
        }

        binding.editNicknamePen.setOnClickListener {
            isEditingNickname = !isEditingNickname
            setEditMode(
                binding.editNickname,
                binding.editNicknamePen,
                isEditingNickname,
                "닉네임을 입력하세요"
            )
        }

        binding.editStatusPen.setOnClickListener {
            isEditingStatus = !isEditingStatus
            setEditMode(
                binding.editStatus,
                binding.editStatusPen,
                isEditingStatus,
                "상태 메시지를 입력하세요"
            )
        }

        binding.btnEditProfileImage.setOnClickListener {
            binding.btnEditProfileImage.setImageResource(R.drawable.ic_profile_pen_blue)
            showProfileImageDialog()
        }



        binding.btnBack.setOnClickListener {
            findNavController().navigateUp()
        }
        binding.btnCancel.setOnClickListener {
            findNavController().navigateUp()
        }

        binding.btnDone.setOnClickListener {
            val name = binding.editName.text.toString()
            val nickname = binding.editNickname.text.toString()
            val status = binding.editStatus.text.toString()

            val bundle = Bundle().apply {
                putString("name", name)
                putString("nickname", nickname)
                putString("status", status)
                putInt("profileImageResId", selectedProfileImageResId)
            }

            findNavController().navigate(R.id.action_profile_to_myPage_with_updated_profile, bundle)
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
        params?.y = 640 // 값 줄이면 더 위로 감
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
