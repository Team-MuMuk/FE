package com.example.mumuk.ui.ingredient

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.PopupWindow
import androidx.core.content.ContextCompat
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.GridLayoutManager
import com.example.mumuk.R
import com.example.mumuk.data.model.DayData
import com.example.mumuk.data.repository.IngredientRepository
import com.example.mumuk.databinding.FragmentAddIngredientBinding
import java.time.LocalDate
import java.time.YearMonth
import java.time.format.DateTimeFormatter

class AddIngredientFragment : Fragment() {
    private var _binding: FragmentAddIngredientBinding? = null
    private val binding get() = _binding!!

    private val ingredientRepository = IngredientRepository()

    private var selectedDate: LocalDate = LocalDate.now()
    private var currentMonth: YearMonth = YearMonth.now()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentAddIngredientBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.backBtn.setOnClickListener {
            requireActivity().onBackPressedDispatcher.onBackPressed()
        }

        binding.imageView2.setOnClickListener {
            findNavController().navigate(R.id.action_addIngredientFragment_to_ingredientDetailFragment)
        }

        val ingredientList = ingredientRepository.getIngredients()
        binding.ingredientRV.layoutManager = LinearLayoutManager(requireContext())
        binding.ingredientRV.adapter = IngredientAdapter(ingredientList)

        binding.calendarBtn.setOnClickListener {
            binding.calendarBtn.setColorFilter(ContextCompat.getColor(requireContext(), R.color.blue_default))
            showCalendarPopup(binding.editTextDate)
        }
    }

    private fun showCalendarPopup(anchorView: View) {
        val inflater = LayoutInflater.from(requireContext())
        val popupView = inflater.inflate(R.layout.popup_calendar, null)

        val widthPx = (200 * resources.displayMetrics.density).toInt()
        val heightPx = (240 * resources.displayMetrics.density).toInt()

        val popupWindow = PopupWindow(
            popupView,
            widthPx,
            heightPx,
            true
        )

        val tvMonthYear = popupView.findViewById<TextView>(R.id.tvMonthYear)
        val btnPrevMonth = popupView.findViewById<View>(R.id.btnPrevMonth)
        val btnNextMonth = popupView.findViewById<View>(R.id.btnNextMonth)
        val calendarRecyclerView = popupView.findViewById<androidx.recyclerview.widget.RecyclerView>(R.id.calendarRecyclerView)
        val doneBtn = popupView.findViewById<TextView>(R.id.textView29)

        fun updateMonthYearHeader() {
            val formatter = DateTimeFormatter.ofPattern("MMMM yyyy")
            tvMonthYear.text = currentMonth.format(formatter)
        }

        fun getMonthDays(yearMonth: YearMonth, selectedDate: LocalDate?): List<DayData> {
            val firstDayOfMonth = yearMonth.atDay(1)
            val lastDay = yearMonth.lengthOfMonth()
            val firstDayOfWeek = firstDayOfMonth.dayOfWeek.value % 7 // 일요일=0
            val days = mutableListOf<DayData>()

            repeat(firstDayOfWeek) { days.add(DayData(null, false, false)) }
            for (day in 1..lastDay) {
                val date = yearMonth.atDay(day)
                val isToday = date == LocalDate.now()
                val isSelected = selectedDate?.let { it == date } ?: false
                days.add(DayData(day, isToday, isSelected))
            }
            while (days.size % 7 != 0) { days.add(DayData(null, false, false)) }
            return days
        }

        var calendarAdapter: CalendarAdapter? = null
        fun updateCalendar() {
            updateMonthYearHeader()
            val days = getMonthDays(currentMonth, selectedDate)
            if (calendarAdapter == null) {
                calendarAdapter = CalendarAdapter(days) { clickedDay ->
                    if (clickedDay != null) {
                        selectedDate = currentMonth.atDay(clickedDay)
                        updateCalendar()
                    }
                }
                calendarRecyclerView.layoutManager = GridLayoutManager(requireContext(), 7)
                calendarRecyclerView.adapter = calendarAdapter
            } else {
                calendarAdapter!!.updateDays(days)
            }
        }

        btnPrevMonth.setOnClickListener {
            currentMonth = currentMonth.minusMonths(1)
            updateCalendar()
        }
        btnNextMonth.setOnClickListener {
            currentMonth = currentMonth.plusMonths(1)
            updateCalendar()
        }

        updateCalendar()

        doneBtn.setOnClickListener {
            val dateString = selectedDate.format(DateTimeFormatter.ofPattern("yyyy-MM-dd"))
            binding.editTextDate.setText(dateString)
            popupWindow.dismiss()
        }

        val displayMetrics = resources.displayMetrics
        val screenWidth = displayMetrics.widthPixels
        val popupWidth = widthPx
        val xCenter = (screenWidth - popupWidth) / 2

        popupWindow.elevation = 12f
        popupWindow.showAsDropDown(anchorView, xCenter, 0)

        popupWindow.setOnDismissListener {
            binding.calendarBtn.setColorFilter(ContextCompat.getColor(requireContext(), R.color.black_400))
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}