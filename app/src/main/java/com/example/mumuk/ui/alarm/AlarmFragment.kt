package com.example.mumuk.ui.alarm

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.mumuk.R

class AlarmFragment : Fragment() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var emptyText: TextView
    private lateinit var adapter: AlarmAdapter
    private var alarmList: List<AlarmItem> = listOf(
        AlarmItem(
            content = "2025.08.01에 등록한 (재료이름)의 \n유통기한이 1일 남았습니다.",
            expiryLabel = "유통기한",
            expiryDate = "2025.08.10"
        ),
        AlarmItem(
            content = "2025.08.01에 등록한 (재료이름)의 \n유통기한이 1일 남았습니다.",
            expiryLabel = "유통기한",
            expiryDate = "2025.08.10"
        )
    )

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_alarm, container, false)
        recyclerView = view.findViewById(R.id.recycler_alarm_list)
        emptyText = view.findViewById(R.id.text_empty)

        val backBtn = view.findViewById<ImageView>(R.id.category_back_btn)
        backBtn?.setOnClickListener {
            requireActivity().onBackPressedDispatcher.onBackPressed()
        }

        recyclerView.layoutManager = LinearLayoutManager(requireContext())
        adapter = AlarmAdapter(alarmList)
        recyclerView.adapter = adapter

        updateEmptyView()

        return view
    }

    private fun updateEmptyView() {
        if (alarmList.isEmpty()) {
            recyclerView.visibility = View.GONE
            emptyText.visibility = View.VISIBLE
        } else {
            recyclerView.visibility = View.VISIBLE
            emptyText.visibility = View.GONE
        }
    }
}