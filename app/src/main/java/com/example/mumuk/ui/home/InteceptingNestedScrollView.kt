package com.example.mumuk.ui.home

import android.content.Context
import android.graphics.Rect
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.View
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

    // 1. 예외 처리할 뷰들의 리스트
    private var verticalScrollableViews: List<View> = emptyList()

    // 2. 프래그먼트에서 예외 뷰들을 전달받는 함수
    fun setVerticalScrollableViews(views: List<View>) {
        this.verticalScrollableViews = views
    }

    override fun onInterceptTouchEvent(ev: MotionEvent): Boolean {
        if (scrollY == 0) {
            when (ev.action) {
                MotionEvent.ACTION_DOWN -> {
                    initialX = ev.x
                    initialY = ev.y
                }
                MotionEvent.ACTION_MOVE -> {
                    val dx = abs(ev.x - initialX)
                    val dy = abs(ev.y - initialY)

                    if (dy > touchSlop && dy > dx) {
                        // 3. 터치된 위치가 예외 처리할 뷰의 내부에 있는지 확인
                        if (isTouchInsideAnyView(ev, verticalScrollableViews)) {
                            // 예외 뷰 위에서는 가로채지 않음 (자체 스크롤 허용)
                            return false
                        } else {
                            // 그 외의 뷰들에서는 가로챔 (당겨서 새로고침)
                            return true
                        }
                    }
                }
            }
        }
        return super.onInterceptTouchEvent(ev)
    }

    // 4. 터치 좌표가 주어진 뷰들 중 하나의 내부에 있는지 확인하는 헬퍼 함수
    private fun isTouchInsideAnyView(ev: MotionEvent, views: List<View>): Boolean {
        val touchX = ev.rawX
        val touchY = ev.rawY
        val rect = Rect()

        for (view in views) {
            // 뷰의 화면상 절대 좌표를 얻음
            view.getGlobalVisibleRect(rect)
            // 터치 좌표가 뷰의 영역에 포함되는지 확인
            if (rect.contains(touchX.toInt(), touchY.toInt())) {
                return true
            }
        }
        return false
    }
}