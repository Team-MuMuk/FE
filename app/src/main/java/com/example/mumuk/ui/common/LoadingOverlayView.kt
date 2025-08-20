package com.example.mumuk.ui.common

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.TextView
import com.example.mumuk.R
import android.os.Handler
import android.os.Looper

class LoadingOverlayView @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null
) : FrameLayout(context, attrs) {

    private var loadingTextView: TextView? = null
    private val loadingTexts = arrayOf("로딩중.", "로딩중..", "로딩중...")
    private var textIndex = 0
    private var handler: Handler? = null

    init {
        LayoutInflater.from(context).inflate(R.layout.view_loading_overlay, this, true)
        visibility = GONE
        loadingTextView = findViewById(R.id.loadingTextView)
    }

    fun show() {
        if (visibility != VISIBLE) {
            visibility = VISIBLE
            startTextAnimation()
        }
    }

    fun hide() {
        if (visibility != GONE) {
            visibility = GONE
            stopTextAnimation()
        }
    }

    private fun startTextAnimation() {
        handler = Handler(Looper.getMainLooper())
        handler?.post(object : Runnable {
            override fun run() {
                loadingTextView?.text = loadingTexts[textIndex % loadingTexts.size]
                textIndex++
                handler?.postDelayed(this, 500)
            }
        })
    }

    private fun stopTextAnimation() {
        handler?.removeCallbacksAndMessages(null)
        handler = null
        textIndex = 0
    }
}