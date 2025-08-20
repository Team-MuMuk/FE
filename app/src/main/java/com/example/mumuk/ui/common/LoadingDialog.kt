package com.example.mumuk.ui.common

import android.app.Dialog
import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Handler
import android.os.Looper
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import com.example.mumuk.R

class LoadingDialog(context: Context) {
    private val dialog: Dialog = Dialog(context)
    private var handler: Handler? = null
    private var loadingTextView: TextView? = null
    private val loadingTexts = arrayOf("로딩중.", "로딩중..", "로딩중...")
    private var textIndex = 0

    init {
        dialog.setContentView(R.layout.dialog_loading)
        dialog.setCancelable(false)
        dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        dialog.window?.setDimAmount(0.5f)
        dialog.window?.setLayout(
            ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.MATCH_PARENT
        )

        loadingTextView = dialog.findViewById(R.id.loadingTextView)
    }

    fun show() {
        if (!dialog.isShowing) {
            dialog.show()
            startTextAnimation()
        }
    }

    fun dismiss() {
        if (dialog.isShowing) {
            dialog.dismiss()
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