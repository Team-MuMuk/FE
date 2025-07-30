package com.example.mumuk.ui.health

import android.animation.ObjectAnimator
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AnimationUtils
import android.view.animation.DecelerateInterpolator
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import androidx.viewpager2.widget.ViewPager2
import com.example.mumuk.R
import com.example.mumuk.databinding.FragmentHealthManagementBinding
import com.example.mumuk.data.api.RetrofitClient
import com.example.mumuk.data.model.allergy.ToggleAllergyRequest
import com.example.mumuk.data.model.allergy.ToggleAllergyResponse
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class HealthManagementFragment : Fragment() {

    private var _binding: FragmentHealthManagementBinding? = null
    private val binding get() = _binding!!

    private val healthViewModel: HealthViewModel by activityViewModels()

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

        setupNextButton()
        setupBackButton()
        observeViewModel()
    }

    private fun setupNextButton() {
        binding.btnOnboardingNext.setOnClickListener {
            if (!::viewPager.isInitialized || !isNextButtonEnabled()) return@setOnClickListener

            if (viewPager.currentItem < numPages - 1) {
                viewPager.currentItem += 1
            } else {
                // 온보딩 마지막 단계에서 알러지 정보 서버로 전송
                sendAllergiesToServerAndNavigate()
            }
        }
    }

    private fun isNextButtonEnabled(): Boolean {
        return when (viewPager.currentItem) {
            0 -> healthViewModel.isStep0Complete.value ?: false
            1 -> healthViewModel.isStep1Complete.value ?: false
            2 -> healthViewModel.isStep2Complete.value ?: false
            else -> true
        }
    }

    private fun setupBackButton() {
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

    private fun observeViewModel() {
        val observer: (Boolean) -> Unit = {
            if (::viewPager.isInitialized) {
                updateNextButtonState(isNextButtonEnabled())
            }
        }
        healthViewModel.isStep0Complete.observe(viewLifecycleOwner, observer)
        healthViewModel.isStep1Complete.observe(viewLifecycleOwner, observer)
        healthViewModel.isStep2Complete.observe(viewLifecycleOwner, observer)
    }

    private fun updateNextButtonState(isComplete: Boolean) {
        val context = requireContext()
        if (isComplete) {
            binding.btnOnboardingNext.setCardBackgroundColor(ContextCompat.getColor(context, R.color.beige_500))
            binding.tvNext.setTextColor(ContextCompat.getColor(context, R.color.white))
            binding.ivNextArrow.setColorFilter(ContextCompat.getColor(context, R.color.white))
        } else {
            binding.btnOnboardingNext.setCardBackgroundColor(ContextCompat.getColor(context, R.color.black_100))
            binding.tvNext.setTextColor(ContextCompat.getColor(context, R.color.black_300))
            binding.ivNextArrow.setColorFilter(ContextCompat.getColor(context, R.color.black_300))
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
                updateNextButtonState(isNextButtonEnabled())
            }
        })
        updateNextButtonState(isNextButtonEnabled())
    }

    private fun animateProgress(targetProgress: Int) {
        val currentProgress = binding.progressBar.progress

        val animator = ObjectAnimator.ofInt(binding.progressBar, "progress", currentProgress, targetProgress)
        animator.duration = 500
        animator.interpolator = DecelerateInterpolator()
        animator.start()
    }

    // 알러지 정보 서버로 전송 후 온보딩 완료 네비게이션
    private fun sendAllergiesToServerAndNavigate() {
        val allergies = healthViewModel.allergies.value ?: emptySet()
        val customAllergy = healthViewModel.customAllergy.value?.takeIf { !it.isNullOrBlank() }
        val allergyTypeList = allergies.toMutableList().apply {
            customAllergy?.let { add(it) }
        }

        val allergyApi = RetrofitClient.getAllergyApi(requireContext())
        val request = ToggleAllergyRequest(allergyTypeList = allergyTypeList)

        allergyApi.toggleAllergies(request).enqueue(object : Callback<ToggleAllergyResponse> {
            override fun onResponse(call: Call<ToggleAllergyResponse>, response: Response<ToggleAllergyResponse>) {
                if (response.isSuccessful) {
                    // 성공시 온보딩 완료 화면 이동
                    findNavController().navigate(R.id.action_healthManagement_to_healthComplete)
                } else {
                    // 실패시에도 일단 이동(원하면 에러처리)
                    findNavController().navigate(R.id.action_healthManagement_to_healthComplete)
                }
            }

            override fun onFailure(call: Call<ToggleAllergyResponse>, t: Throwable) {
                // 네트워크 에러 처리(원하면 사용자 알림)
                findNavController().navigate(R.id.action_healthManagement_to_healthComplete)
            }
        })
    }

    // 더 이상 사용하지 않음: navigateToComplete()
    // private fun navigateToComplete() {
    //     val currentFragment = adapter.getFragmentAt(viewPager.currentItem)
    //     if (currentFragment is HealthStep2Fragment) {
    //         currentFragment.saveHealthData()
    //     }
    //     findNavController().navigate(R.id.action_healthManagement_to_healthComplete)
    // }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}