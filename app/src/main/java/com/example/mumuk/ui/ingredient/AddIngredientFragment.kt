package com.example.mumuk.ui.ingredient

import android.app.Dialog
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.widget.TextView
import android.widget.PopupWindow
import android.graphics.Color
import android.os.Build
import androidx.core.content.ContextCompat
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.GridLayoutManager
import com.example.mumuk.R
import com.example.mumuk.data.model.DayData
import com.example.mumuk.data.repository.IngredientRepository
import com.example.mumuk.databinding.FragmentAddIngredientBinding
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.YearMonth
import java.time.format.DateTimeFormatter
import com.example.mumuk.data.model.ingredient.IngredientRegisterRequest
import android.widget.Toast

class AddIngredientFragment : Fragment() {
    private var _binding: FragmentAddIngredientBinding? = null
    private val binding get() = _binding!!

    private val ingredientRepository by lazy { IngredientRepository(requireContext()) }

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
            findNavController().navigate(R.id.action_addIngredientFragment_to_ingredientListFragment)
        }

        viewLifecycleOwner.lifecycleScope.launch {
            val ingredientList = ingredientRepository.getIngredients()
            binding.ingredientRV.layoutManager = LinearLayoutManager(requireContext())
            binding.ingredientRV.adapter = IngredientAdapter(
                ingredientList,
                onItemClick = { ingredient ->
                    // 상세보기: IngredientDetailFragment로 이동
                    val bundle = Bundle().apply {
                        putSerializable("ingredient", ingredient)
                    }
                    findNavController().navigate(
                        R.id.action_addIngredientFragment_to_ingredientDetailFragment,
                        bundle
                    )
                },
                onDeleteClick = { ingredient ->
                    // 삭제 API 호출 & RecyclerView 갱신
                    viewLifecycleOwner.lifecycleScope.launch {
                        try {
                            val response = ingredientRepository.deleteIngredient(ingredient.id) // id 필드가 필요!
                            if (response.isSuccessful && response.body()?.code == "INGREDIENT_200") {
                                val newList = ingredientRepository.getIngredients()
                                (binding.ingredientRV.adapter as? IngredientAdapter)?.submitList(newList)
                            } else {
                                // 실패 처리
                            }
                        } catch (e: Exception) {
                            // 네트워크 오류 처리
                        }
                    }
                }
            )
        }

        binding.calendarBtn.setOnClickListener {
            binding.calendarBtn.setColorFilter(ContextCompat.getColor(requireContext(), R.color.blue_default))
            showCalendarPopup(binding.editTextDate)
        }

        fun updateAddButtonState() {
            val ingredientNotEmpty = binding.editTextIngredient.text.toString().trim().isNotEmpty()
            val dateStr = binding.editTextDate.text.toString().trim()
            val dateValid = isValidDateFormat(dateStr)
            val enabled = ingredientNotEmpty && dateValid

            binding.addBtn.isEnabled = enabled
            if (enabled) {
                binding.addBtn.setBackgroundColor(ContextCompat.getColor(requireContext(), R.color.green_500))
                binding.addBtn.setTextColor(ContextCompat.getColor(requireContext(), android.R.color.white))
            } else {
                binding.addBtn.setBackgroundColor(ContextCompat.getColor(requireContext(), R.color.black_100))
                binding.addBtn.setTextColor(ContextCompat.getColor(requireContext(), R.color.black_300))
            }
        }

        // TextWatcher 등록
        val watcher = object : android.text.TextWatcher {
            override fun afterTextChanged(s: android.text.Editable?) = updateAddButtonState()
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
        }

        binding.editTextIngredient.addTextChangedListener(watcher)
        binding.editTextDate.addTextChangedListener(watcher)

        // 초기 상태 세팅
        updateAddButtonState()


        binding.addBtn.setOnClickListener {
            val ingredient = binding.editTextIngredient.text.toString().trim()
            val date = binding.editTextDate.text.toString().trim()

            if (ingredient.isNotEmpty() && date.isNotEmpty()) {
                // API 호출로 등록
                viewLifecycleOwner.lifecycleScope.launch {
                    try {
                        val response = ingredientRepository.registerIngredient(ingredient, date, "D7")
                        val body = response.body()
                        if (response.isSuccessful && body?.code == "INGREDIENT_200") {
                            showIngredientAddedDialog()
                            // 입력값 초기화
                            binding.editTextIngredient.text.clear()
                            binding.editTextDate.text.clear()
                            // 필요시 RecyclerView 갱신
                            val newList = ingredientRepository.getIngredients()
                            (binding.ingredientRV.adapter as? IngredientAdapter)?.submitList(newList)
                        } else {
                            Toast.makeText(requireContext(), "재료 등록 실패: ${response.body()?.message ?: response.message()}", Toast.LENGTH_SHORT).show()
                        }
                    } catch (e: Exception) {
                        Toast.makeText(requireContext(), "네트워크 오류: ${e.localizedMessage}", Toast.LENGTH_SHORT).show()
                    }
                }
            }
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

    private fun showIngredientAddedDialog() {
        val dialog = Dialog(requireContext())
        dialog.setContentView(R.layout.dialog_ingredient_added)
        dialog.setCancelable(false)

        dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        dialog.window?.setDimAmount(0f)
        dialog.window?.setLayout(
            ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.MATCH_PARENT
        )

        val btnOk = dialog.findViewById<TextView>(R.id.btnOk)
        btnOk.setOnClickListener { dialog.dismiss() }
        dialog.show()
    }

    fun isValidDateFormat(date: String): Boolean {
        return try {
            java.time.LocalDate.parse(date)
            true
        } catch (e: Exception) {
            false
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}