package com.example.mumuk.Login

import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.InputType
import android.text.TextWatcher
import android.view.View
import android.widget.EditText
import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.LinearLayout
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.AppCompatButton
import androidx.core.content.ContextCompat
import androidx.fragment.app.commit
import android.widget.TextView
import com.example.mumuk.R


class LoginActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        val etId = findViewById<EditText>(R.id.et_id)
        val etPassword = findViewById<EditText>(R.id.et_password)
        val btnLogin = findViewById<AppCompatButton>(R.id.btn_login)

        val ivTogglePw = findViewById<ImageView>(R.id.iv_toggle_pw)

        var isPasswordVisible = false

        val activeDrawable = ContextCompat.getDrawable(this, R.drawable.btn_login_active)
        val inactiveDrawable = ContextCompat.getDrawable(this, R.drawable.logintext_border)
        val whiteTextColor = ContextCompat.getColor(this, android.R.color.white)


        val btnBack = findViewById<ImageView>(R.id.btn_back)
        btnBack.setOnClickListener {
            startActivity(Intent(this, LoginIntroActivity::class.java))
            finish()
        }

        ivTogglePw.setOnClickListener {
            isPasswordVisible = !isPasswordVisible

            if (isPasswordVisible) {
                etPassword.inputType = InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD
                ivTogglePw.setImageResource(R.drawable.ic_eyeopened)
            } else {
                etPassword.inputType =
                    InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_VARIATION_PASSWORD
                ivTogglePw.setImageResource(R.drawable.ic_eyeclosed)
            }

            etPassword.setSelection(etPassword.text?.length ?: 0)
        }

        val watcher = object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}

            override fun afterTextChanged(s: Editable?) {
                val isIdFilled = etId.text?.isNotEmpty() == true
                val isPasswordFilled = etPassword.text?.isNotEmpty() == true

                if (isIdFilled && isPasswordFilled) {
                    btnLogin.isEnabled = true
                    btnLogin.background = activeDrawable
                    btnLogin.setTextColor(whiteTextColor)
                } else {
                    btnLogin.isEnabled = false
                    btnLogin.background = inactiveDrawable
                }
            }
        }
        etId.addTextChangedListener(watcher)
        etPassword.addTextChangedListener(watcher)

        val loginLayout = findViewById<LinearLayout>(R.id.login_layout)
        val fragmentContainer = findViewById<FrameLayout>(R.id.login_fragment_container)
        val tvFindAccount = findViewById<TextView>(R.id.tv_find_account)

        tvFindAccount.setOnClickListener {
            loginLayout.visibility = View.GONE
            fragmentContainer.visibility = View.VISIBLE

            supportFragmentManager.commit {
                setReorderingAllowed(true)
                replace(R.id.login_fragment_container, FindIdFragment())
                addToBackStack(null)
            }
        }


    }

}
