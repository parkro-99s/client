package com.parkro.client.domain.admin_parkinglist.ui

import android.app.Dialog
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModelProvider
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.parkro.client.R
import com.parkro.client.databinding.DateBottomSheetLayoutBinding
import java.util.*

class DateBottomSheetFragment : BottomSheetDialogFragment() {

    private var _binding: DateBottomSheetLayoutBinding? = null
    private val binding get() = _binding!!
    private lateinit var adminParkingListViewModel: AdminParkingListViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        super.onCreateView(inflater, container, savedInstanceState)
        adminParkingListViewModel = ViewModelProvider(requireActivity()).get(AdminParkingListViewModel::class.java)
        _binding = DateBottomSheetLayoutBinding.inflate(inflater, container, false)

        // NumberPicker 설정
        setupNumberPickers()

        // 버튼 클릭 리스너
        binding.btnBottomSheetConfirm.setOnClickListener {
            val year = binding.npYear.value
            val month = binding.npMonth.value
            val day = binding.npDay.value

            // 날짜 선택을 ViewModel에 업데이트
            adminParkingListViewModel.updateSelectedDate(year, month, day)
            Log.d("BottomSheetFragment", "update selected date at bottom sheet: $year $month $day")

            val storeId: Int = if (adminParkingListViewModel.selectedStore.value == null) 1 else adminParkingListViewModel.selectedStore.value!!
            val parkingLotId: Int = if (adminParkingListViewModel.selectedParkingLot.value == null) 1 else adminParkingListViewModel.selectedParkingLot.value!!

            adminParkingListViewModel.resetData()
            adminParkingListViewModel.fetchAdminParkingList(storeId = storeId, parkingLotId = parkingLotId, date = String.format("%d-%02d-%02d", year, month, day))

            // BottomSheet 닫기
            dismiss()
        }

        return binding.root
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val dialog = super.onCreateDialog(savedInstanceState)
        dialog.setOnShowListener {
            val bottomSheet = dialog.findViewById<FrameLayout>(com.google.android.material.R.id.design_bottom_sheet)
            bottomSheet?.let {
                // 배경을 투명하게 설정
                bottomSheet.setBackgroundColor(ContextCompat.getColor(requireContext(), android.R.color.transparent))
                // 커스텀 배경 drawable 설정
                bottomSheet.background = ContextCompat.getDrawable(requireContext(), R.drawable.shape_selection_date_oval)
            }
        }
        return dialog
    }

    private fun setupNumberPickers() {
        // NumberPicker 설정
        binding.npYear.minValue = 2000
        binding.npYear.maxValue = 2024
        binding.npYear.value = if (adminParkingListViewModel.getSelectedYear() == null) Calendar.getInstance().get(Calendar.YEAR) else adminParkingListViewModel.getSelectedYear()!!

        binding.npMonth.minValue = 1
        binding.npMonth.maxValue = 12
        binding.npMonth.value = if (adminParkingListViewModel.getSelectedMonth() == null) Calendar.getInstance().get(Calendar.MONTH) + 1 else adminParkingListViewModel.getSelectedMonth()!!

        binding.npDay.minValue = 1
        binding.npDay.maxValue = 31
        binding.npDay.value = if (adminParkingListViewModel.getSelectedDay() == null) Calendar.getInstance().get(Calendar.DAY_OF_MONTH) else adminParkingListViewModel.getSelectedDay()!!

        // 월과 일의 범위를 동적으로 설정할 수 있도록 추가 로직 필요
        binding.npMonth.setOnValueChangedListener { _, _, newMonth ->
            updateDayRange(newMonth)
        }

        binding.npYear.setOnValueChangedListener { _, _, newYear ->
            updateDayRange(binding.npMonth.value)
        }
    }

    private fun updateDayRange(month: Int) {
        val daysInMonth = getDaysInMonth(binding.npYear.value, month)
        binding.npDay.maxValue = daysInMonth
        if (binding.npDay.value > daysInMonth) {
            binding.npDay.value = daysInMonth
        }
    }

    private fun getDaysInMonth(year: Int, month: Int): Int {
        val calendar = Calendar.getInstance()
        calendar.set(year, month - 1, 1) // 월은 0부터 시작하므로 -1
        return calendar.getActualMaximum(Calendar.DAY_OF_MONTH)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
