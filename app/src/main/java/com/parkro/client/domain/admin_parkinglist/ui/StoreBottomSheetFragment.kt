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
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.parkro.client.R
import com.parkro.client.databinding.FragmentStoreBottomSheetBinding

/**
 * 백화점 지점 선택하는 Bottom Sheet 관련 Fragment
 *
 * @author 김지수
 * @since 2024.08.06
 *
 * <pre>
 * 수정일자       수정자        수정내용
 * ------------ --------    ---------------------------
 * 2024.08.06   김지수      최초 생성
 * </pre>
 */
class StoreBottomSheetFragment : BottomSheetDialogFragment() {

    private var _binding: FragmentStoreBottomSheetBinding? = null
    private val binding get() = _binding!!
    private lateinit var adminParkingListViewModel: AdminParkingListViewModel

    private val storeMap: Map<String, Int> = mapOf(
        "현대아울렛 스페이스원" to 1,
        "현대아울렛 동대문점" to 2,
        "현대백화점 판교점" to 3,
        "더현대 대구" to 4
    )

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentStoreBottomSheetBinding.inflate(inflater, container, false)
        adminParkingListViewModel = ViewModelProvider(requireActivity()).get(AdminParkingListViewModel::class.java)

        setupRecyclerView()

        return binding.root
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val dialog = super.onCreateDialog(savedInstanceState)
        dialog.setOnShowListener {
            val bottomSheet = dialog.findViewById<FrameLayout>(com.google.android.material.R.id.design_bottom_sheet)
            bottomSheet?.let {
                bottomSheet.setBackgroundColor(ContextCompat.getColor(requireContext(), android.R.color.transparent))
                bottomSheet.background = ContextCompat.getDrawable(requireContext(), R.drawable.shape_selection_parkinglot_oval)
            }
        }
        return dialog
    }

    private fun setupRecyclerView() {
        val recyclerView = binding.recyclerViewStoreBottomSheet
        recyclerView.layoutManager = LinearLayoutManager(context)

        val adapter = StoreRecyclerAdapter(storeMap.keys.toList()) { selectedStore ->
            val selectedStoreId = storeMap[selectedStore] ?: return@StoreRecyclerAdapter
            val parkingLotBottomSheet = ParkingLotBottomSheetFragment.newInstance(selectedStoreId)

            adminParkingListViewModel.updateSelectedStore(selectedStoreId)

            Log.d("StoreBottomSheetFragment", "지점 선택 후, 주차장 지정")

            dismiss()
            parkingLotBottomSheet.show(parentFragmentManager, parkingLotBottomSheet.tag)
        }

        recyclerView.adapter = adapter
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
