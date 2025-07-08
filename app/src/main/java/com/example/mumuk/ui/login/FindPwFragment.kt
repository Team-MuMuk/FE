package com.example.mumuk.ui.login

import android.app.Dialog
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.*
import android.widget.TextView
import androidx.appcompat.widget.AppCompatButton
import androidx.fragment.app.Fragment
import androidx.fragment.app.commit
import com.example.mumuk.ChangePwFragment
import com.example.mumuk.R
import com.example.mumuk.databinding.FragmentFindPwBinding

class FindPwFragment : Fragment() {

    private var _binding: FragmentFindPwBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentFindPwBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.btnBack.setOnClickListener {
            startActivity(Intent(requireContext(), LoginActivity::class.java))
            requireActivity().finish()
        }

        binding.tabFindId.setOnClickListener {
            parentFragmentManager.commit {
                replace(R.id.login_fragment_container, FindIdFragment())
                addToBackStack(null)
            }
        }

        binding.tabFindPw.setOnClickListener {
            parentFragmentManager.commit {
                replace(R.id.login_fragment_container, FindPwFragment())
                addToBackStack(null)
            }
        }

        binding.btnSendCode.setOnClickListener {
            showCustomDialog("인증코드가 발송되었습니다.", R.layout.dialog_code_sent)
        }

        binding.btnConfirmCode.setOnClickListener {
            showCustomDialog("인증코드가 확인되었습니다.", R.layout.dialog_confirm)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun showCustomDialog(message: String, layoutResId: Int) {
        val dialog = Dialog(requireContext())
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.setContentView(layoutResId)

        dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        dialog.window?.setLayout(
            ViewGroup.LayoutParams.WRAP_CONTENT,
            ViewGroup.LayoutParams.WRAP_CONTENT
        )
        dialog.window?.setGravity(Gravity.CENTER)

        val tvMessage = dialog.findViewById<TextView>(R.id.tv_dialog_message)
        val btnOk = dialog.findViewById<TextView>(R.id.btn_dialog_ok)

        tvMessage.text = message
        btnOk.setOnClickListener {
            dialog.dismiss()

            if (layoutResId == R.layout.dialog_confirm) {
                parentFragmentManager.commit {
                    replace(R.id.login_fragment_container, ChangePwFragment())
                    addToBackStack(null)
                }
            }
        }

        dialog.show()
    }
}
