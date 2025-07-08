package com.example.mumuk.ui.mypage

import android.app.AlertDialog
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.util.TypedValue
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.example.mumuk.R
import com.example.mumuk.databinding.DialogDeleteAccountBinding
import com.example.mumuk.databinding.DialogLogoutBinding
import com.example.mumuk.databinding.FragmentMyPageBinding

class MyPageFragment : Fragment() {
    private var _binding: FragmentMyPageBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentMyPageBinding.inflate(inflater, container, false)

        binding.btnSettings.setOnClickListener {
            findNavController().navigate(R.id.action_myPage_to_settings)
        }

        binding.btnProfile.setOnClickListener {
            findNavController().navigate(R.id.action_myPage_to_profile)
        }

        binding.btnFavorites.setOnClickListener {
            findNavController().navigate(R.id.action_myPage_to_favorites)
        }

        binding.itemLogout.setOnClickListener {
            val logoutBinding = DialogLogoutBinding.inflate(layoutInflater)

            val dialog = AlertDialog.Builder(requireContext())
                .setView(logoutBinding.root)
                .create()

            dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
            dialog.show()

            logoutBinding.btnDialogOk.setOnClickListener {
                Toast.makeText(requireContext(), "로그아웃 처리 진행", Toast.LENGTH_SHORT).show()
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

            val dialog = AlertDialog.Builder(requireContext())
                .setView(deleteBinding.root)
                .create()

            dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
            dialog.show()

            deleteBinding.btnDialogCancel.setOnClickListener {
                dialog.dismiss()
            }

            deleteBinding.btnDialogLogin.setOnClickListener {
                Toast.makeText(requireContext(), "탈퇴 처리 진행", Toast.LENGTH_SHORT).show()
                dialog.dismiss()
            }

            val widthInPx = TypedValue.applyDimension(
                TypedValue.COMPLEX_UNIT_DIP,
                220f,
                resources.displayMetrics
            ).toInt()

            dialog.window?.setLayout(widthInPx, ViewGroup.LayoutParams.WRAP_CONTENT)
        }

        return binding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
