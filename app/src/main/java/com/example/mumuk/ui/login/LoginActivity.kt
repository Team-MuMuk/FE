package com.example.mumuk.ui.login

import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.InputType
import android.text.TextWatcher
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.fragment.app.commit
import com.example.mumuk.R
import com.example.mumuk.databinding.ActivityLoginBinding
import com.example.mumuk.ui.MainActivity

class LoginActivity : AppCompatActivity() {

    private lateinit var binding: ActivityLoginBinding
    private var isPasswordVisible = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val activeDrawable = ContextCompat.getDrawable(this, R.drawable.btn_login_active)
        val inactiveDrawable = ContextCompat.getDrawable(this, R.drawable.logintext_border)
        val whiteTextColor = ContextCompat.getColor(this, android.R.color.white)

        binding.btnBack.setOnClickListener {
            startActivity(Intent(this, LoginIntroActivity::class.java))
            finish()
        }

        binding.btnLogin.setOnClickListener {
            val intent = Intent(this, MainActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            startActivity(intent)
        }


        binding.ivTogglePw.setOnClickListener {
            isPasswordVisible = !isPasswordVisible
            if (isPasswordVisible) {
                binding.etPassword.inputType = InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD
                binding.ivTogglePw.setImageResource(R.drawable.ic_eyeopened)
            } else {
                binding.etPassword.inputType =
                    InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_VARIATION_PASSWORD
                binding.ivTogglePw.setImageResource(R.drawable.ic_eyeclosed)
            }
            binding.etPassword.setSelection(binding.etPassword.text?.length ?: 0)
        }

        val watcher = object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}

            override fun afterTextChanged(s: Editable?) {
                val isIdFilled = binding.etId.text?.isNotEmpty() == true
                val isPasswordFilled = binding.etPassword.text?.isNotEmpty() == true

                if (isIdFilled && isPasswordFilled) {
                    binding.btnLogin.isEnabled = true
                    binding.btnLogin.background = activeDrawable
                    binding.btnLogin.setTextColor(whiteTextColor)
                } else {
                    binding.btnLogin.isEnabled = false
                    binding.btnLogin.background = inactiveDrawable
                }
            }
        }

        binding.etId.addTextChangedListener(watcher)
        binding.etPassword.addTextChangedListener(watcher)

        binding.tvFindAccount.setOnClickListener {
            binding.loginLayout.visibility = View.GONE
            binding.loginFragmentContainer.visibility = View.VISIBLE

            supportFragmentManager.commit {
                setReorderingAllowed(true)
                replace(R.id.login_fragment_container, FindAccountFragment())
                addToBackStack(null)
            }
        }
    }
}
