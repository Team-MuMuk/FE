package com.example.mumuk.ui.health

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import com.example.mumuk.R
import com.example.mumuk.databinding.FragmentHealthCompleteBinding

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

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}