package com.example.mumuk.ui.signup

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.mumuk.R

class SignupActivity : AppCompatActivity() {
    var name: String = ""
    var nickname: String = ""
    var phoneNumber: String = ""
    var loginId: String = ""
    var password: String = ""
    var confirmPassword: String = ""


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_signup)

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        if(savedInstanceState==null){
            supportFragmentManager.beginTransaction()
                .replace(R.id.signup_container, SignupStep0Fragment())
                .commit()
        }
    }
}