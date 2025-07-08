package com.example.mumuk.ui.mypage

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import com.example.mumuk.R
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

        binding.itemLogout.setOnClickListener{
            val dialogView = layoutInflater.inflate(R.layout.dialog_logout, null)

            val dialog = androidx.appcompat.app.AlertDialog.Builder(requireContext())
                .setView(dialogView)
                .setPositiveButton("로그아웃") { _, _ ->
                }
                .setNegativeButton("취소", null)
                .create()

            dialog.show()

        }

        binding.itemDeleteAccount.setOnClickListener{

        }

        return binding.root



    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
