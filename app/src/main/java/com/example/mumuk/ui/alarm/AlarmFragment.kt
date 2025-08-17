package com.example.mumuk.ui.alarm

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.mumuk.R
import com.example.mumuk.data.api.RetrofitClient
import com.example.mumuk.data.model.alarm.AlarmItem
import com.example.mumuk.data.model.alarm.AlarmResponse
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class AlarmFragment : Fragment() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var emptyText: TextView
    private lateinit var adapter: AlarmAdapter
    private var alarmList: List<AlarmItem> = listOf()

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

        fetchAlarms()

        return view
    }

    private fun fetchAlarms() {
        Log.d("AlarmFragment", "API 호출 시작!")
        val alarmApi = RetrofitClient.getAlarmApi(requireContext())
        alarmApi.getRecentAlarms(200).enqueue(object : Callback<AlarmResponse> {
            override fun onResponse(
                call: Call<AlarmResponse>,
                response: Response<AlarmResponse>
            ) {
                Log.d("AlarmFragment", "API 응답 성공! 코드: ${response.code()}")
                if (response.isSuccessful && response.body() != null) {
                    alarmList = response.body()!!.data
                    Log.d("AlarmFragment", "알림 데이터 개수: ${alarmList.size}")
                    adapter.updateList(alarmList)
                } else {
                    Log.d("AlarmFragment", "응답은 성공했지만 데이터가 없음 또는 에러: ${response.errorBody()?.string()}")
                    alarmList = listOf()
                    adapter.updateList(alarmList)
                }
                updateEmptyView()
            }

            override fun onFailure(call: Call<AlarmResponse>, t: Throwable) {
                Log.e("AlarmFragment", "API 응답 실패: ${t.localizedMessage}")
                alarmList = listOf()
                adapter.updateList(alarmList)
                updateEmptyView()
            }
        })
    }

    private fun updateEmptyView() {
        if (alarmList.isEmpty()) {
            recyclerView.visibility = View.GONE
            emptyText.visibility = View.VISIBLE
            Log.d("AlarmFragment", "알림 데이터 없음 (empty view 표시)")
        } else {
            recyclerView.visibility = View.VISIBLE
            emptyText.visibility = View.GONE
            Log.d("AlarmFragment", "알림 데이터 있음 (리스트 표시)")
        }
    }
}