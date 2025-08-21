package com.example.mumuk.ui.home

import android.content.Context
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.ViewConfiguration
import androidx.core.widget.NestedScrollView
import kotlin.math.abs

class InterceptingNestedScrollView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : NestedScrollView(context, attrs, defStyleAttr) {

    private var initialX = 0f
    private var initialY = 0f
    private val touchSlop = ViewConfiguration.get(context).scaledTouchSlop

    override fun onInterceptTouchEvent(ev: MotionEvent): Boolean {
        // 스크롤이 최상단에 있을 때만 터치 가로채기 로직을 실행합니다.
        if (scrollY == 0) {
            when (ev.action) {
                MotionEvent.ACTION_DOWN -> {
                    // 터치 시작 위치를 기록합니다.
                    initialX = ev.x
                    initialY = ev.y
                }
                MotionEvent.ACTION_MOVE -> {
                    val dx = abs(ev.x - initialX) // x축 이동 거리
                    val dy = abs(ev.y - initialY) // y축 이동 거리

                    // y축 이동이 x축 이동보다 크고,
                    // 시스템이 스크롤로 인식하는 최소 거리(touchSlop)를 넘었을 때
                    if (dy > touchSlop && dy > dx) {
                        // 이 스크롤은 '당겨서 새로고침'을 위한 세로 스크롤이므로
                        // 부모 뷰(이 뷰)가 이벤트를 가로챕니다. (true 반환)
                        return true
                    }
                }
            }
        }
        // 그 외의 모든 경우는 기본 동작에 맡깁니다.
        return super.onInterceptTouchEvent(ev)
    }
}