package com.example.mumuk.ui.health

import androidx.fragment.app.Fragment
import androidx.viewpager2.adapter.FragmentStateAdapter

const val NUM_PAGES = 3 // 총 페이지 수 (STEP 0, 1, 2)

class HealthOnboardingAdapter(fragment: Fragment) : FragmentStateAdapter(fragment) {

    private val fragments = mutableMapOf<Int, Fragment>()

    override fun getItemCount(): Int = NUM_PAGES

    override fun createFragment(position: Int): Fragment {
        val fragment = when (position) {
            0 -> Step0Fragment()
            1 -> Step1Fragment()
            2 -> Step2Fragment()
            else -> throw IllegalStateException("Invalid position $position")
        }
        fragments[position] = fragment
        return fragment
    }

    // Fragment를 외부에서 접근할 수 있도록 하는 메서드
    fun getFragmentAt(position: Int): Fragment? {
        return fragments[position]
    }
}