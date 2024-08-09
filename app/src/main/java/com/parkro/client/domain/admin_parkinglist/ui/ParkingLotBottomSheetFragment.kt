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
import com.parkro.client.databinding.FragmentParkingLotBottomSheetBinding

/**
 * 주차장 선택 Bottom Sheet 관련 Fragment
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
class ParkingLotBottomSheetFragment : BottomSheetDialogFragment() {

    private var _binding: FragmentParkingLotBottomSheetBinding? = null
    private val binding get() = _binding!!
    private lateinit var adminParkingListViewModel: AdminParkingListViewModel

    private val parkingLotMap: Map<Int, List<Pair<Int, String>>> = mapOf(
        1 to listOf(
            1 to "현대아울렛 스페이스원",
            2 to "스페이스원 주차타워",
            3 to "다산 블루 웨일 1차",
            4 to "다산 현대프리미어 캠퍼스몰"
        ),
        2 to listOf(
            5 to "현대아울렛 동대문점",
            6 to "호텔스카이파크 킹스타운동대문점"
        ),
        3 to listOf(
            7 to "현대백화점 판교점",
            8 to "그레이츠 판교",
            9 to "알파돔타워"
        ),
        4 to listOf(
            10 to "더현대 대구",
            11 to "반월당 메트로 주차장"
        )
    )

    // 주차장 이름을 기반으로 주차장 ID를 찾을 수 있는 Map
    private val parkingLotIdMap: Map<String, Int> = parkingLotMap.flatMap { (storeId, parkingLots) ->
        parkingLots.map { (parkingLotId, parkingLotName) -> parkingLotName to parkingLotId }
    }.toMap()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentParkingLotBottomSheetBinding.inflate(inflater, container, false)
        adminParkingListViewModel = ViewModelProvider(requireActivity()).get(AdminParkingListViewModel::class.java)

        val storeId = arguments?.getInt(ARG_STORE_ID) ?: return binding.root
        setupRecyclerView(storeId)

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

    private fun setupRecyclerView(storeId: Int) {
        val recyclerView = binding.recyclerViewParkingBottomSheet
        recyclerView.layoutManager = LinearLayoutManager(context)

        val adapter = StoreRecyclerAdapter(parkingLotMap[storeId]?.map { it.second } ?: emptyList()) {  selectedParkingLot ->
            val parkingLotId = parkingLotIdMap[selectedParkingLot]
            if (parkingLotId != null) {
                adminParkingListViewModel.updateSelectedParkingLot(parkingLotId)
                dismiss()
                Log.d("ParkingLotBottomSheetFragment", "선택한 parking lot id: $parkingLotId")
            }
        }
        binding.recyclerViewParkingBottomSheet.adapter = adapter
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    companion object {
        private const val ARG_STORE_ID = "store_id"

        fun newInstance(storeId: Int): ParkingLotBottomSheetFragment {
            val fragment = ParkingLotBottomSheetFragment()
            val args = Bundle().apply {
                putInt(ARG_STORE_ID, storeId)
            }
            fragment.arguments = args
            return fragment
        }
    }
}
