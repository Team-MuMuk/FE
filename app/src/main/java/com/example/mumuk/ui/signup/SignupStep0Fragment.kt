package com.example.mumuk.ui.signup

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import androidx.fragment.app.Fragment
import com.example.mumuk.R

class SignupStep0Fragment : Fragment() {
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_signup_step0, container, false)

        val btnNext = view.findViewById<ImageButton>(R.id.btn_next)
        btnNext.setOnClickListener {
            parentFragmentManager.beginTransaction()
                .replace(R.id.signup_container, SignupStep1Fragment())
                .addToBackStack(null)
                .commit()
        }

        return view
    }
}
