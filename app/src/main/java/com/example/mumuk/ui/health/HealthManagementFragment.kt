package com.example.mumuk.ui.health

import android.animation.ObjectAnimator
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AnimationUtils
import android.view.animation.DecelerateInterpolator
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.viewpager2.widget.ViewPager2
import com.example.mumuk.R
import com.example.mumuk.databinding.FragmentHealthManagementBinding

class HealthManagementFragment : Fragment() {

    private var _binding: FragmentHealthManagementBinding? = null
    private val binding get() = _binding!!

    private lateinit var viewPager: ViewPager2
    private lateinit var adapter: HealthOnboardingAdapter
    private val numPages = 3

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentHealthManagementBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.btnBack.setOnClickListener {
            findNavController().navigateUp()
        }

        binding.btnStart.setOnClickListener {
            animateToOnboarding()
        }

        binding.btnOnboardingNext.setOnClickListener {
            if (::viewPager.isInitialized) {
                if (viewPager.currentItem < numPages - 1) {
                    viewPager.currentItem += 1
                } else {
                    navigateToComplete()
                }
            }
        }

        binding.btnOnboardingBack.setOnClickListener {
            if (::viewPager.isInitialized) {
                val currentItem = viewPager.currentItem
                if (currentItem > 0) {
                    viewPager.currentItem = currentItem - 1
                } else {
                    animateToStart()
                }
            }
        }
    }

    private fun animateToOnboarding() {
        val fadeOut = AnimationUtils.loadAnimation(requireContext(), android.R.anim.fade_out)
        val fadeIn = AnimationUtils.loadAnimation(requireContext(), android.R.anim.fade_in)

        binding.tvTitle.startAnimation(fadeOut)
        binding.tvSubtitle.startAnimation(fadeOut)
        binding.ivHealthIcons.startAnimation(fadeOut)
        binding.btnStart.startAnimation(fadeOut)

        fadeOut.setAnimationListener(object : android.view.animation.Animation.AnimationListener {
            override fun onAnimationStart(animation: android.view.animation.Animation?) {}
            override fun onAnimationRepeat(animation: android.view.animation.Animation?) {}
            override fun onAnimationEnd(animation: android.view.animation.Animation?) {
                binding.groupStart.isVisible = false
                binding.groupOnboarding.isVisible = true
                setupViewPager()

                binding.progressBar.startAnimation(fadeIn)
                binding.viewPager.startAnimation(fadeIn)
                binding.btnOnboardingBack.startAnimation(fadeIn)
                binding.btnOnboardingNext.startAnimation(fadeIn)
            }
        })
    }

    private fun animateToStart() {
        val fadeOut = AnimationUtils.loadAnimation(requireContext(), android.R.anim.fade_out)
        val fadeIn = AnimationUtils.loadAnimation(requireContext(), android.R.anim.fade_in)

        binding.progressBar.startAnimation(fadeOut)
        binding.viewPager.startAnimation(fadeOut)
        binding.btnOnboardingBack.startAnimation(fadeOut)
        binding.btnOnboardingNext.startAnimation(fadeOut)

        fadeOut.setAnimationListener(object : android.view.animation.Animation.AnimationListener {
            override fun onAnimationStart(animation: android.view.animation.Animation?) {}
            override fun onAnimationRepeat(animation: android.view.animation.Animation?) {}
            override fun onAnimationEnd(animation: android.view.animation.Animation?) {
                binding.groupOnboarding.isVisible = false
                binding.groupStart.isVisible = true

                binding.tvTitle.startAnimation(fadeIn)
                binding.tvSubtitle.startAnimation(fadeIn)
                binding.ivHealthIcons.startAnimation(fadeIn)
                binding.btnStart.startAnimation(fadeIn)
            }
        })
    }

    private fun setupViewPager() {
        viewPager = binding.viewPager
        adapter = HealthOnboardingAdapter(this)
        viewPager.adapter = adapter
        viewPager.isUserInputEnabled = false

        binding.progressBar.progress = 0

        viewPager.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
            override fun onPageSelected(position: Int) {
                super.onPageSelected(position)

                val progressValue = when (position) {
                    0 -> 0
                    1 -> 50
                    2 -> 100
                    else -> 0
                }
                animateProgress(progressValue)
            }
        })
    }

    private fun animateProgress(targetProgress: Int) {
        val currentProgress = binding.progressBar.progress

        val animator = ObjectAnimator.ofInt(binding.progressBar, "progress", currentProgress, targetProgress)
        animator.duration = 500
        animator.interpolator = DecelerateInterpolator()
        animator.start()
    }


    private fun navigateToComplete() {
        val currentFragment = adapter.getFragmentAt(viewPager.currentItem)
        if (currentFragment is Step2Fragment) {
            currentFragment.saveHealthData()
        }

        findNavController().navigate(R.id.action_healthManagement_to_healthComplete)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}