package com.parkro.client.domain.admin_parkinglist.ui

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.NavHostFragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.parkro.client.MainActivity
import com.parkro.client.R
import com.parkro.client.databinding.FragmentAdminParkingListBinding
import com.parkro.client.domain.admin.ui.AdminActivity
import java.util.*

/**
 * 관리자 주차 목록 Fragment
 *
 * @author 김지수
 * @since 2024.08.05
 *
 * <pre>
 * 수정일자       수정자        수정내용
 * ------------ --------    ---------------------------
 * 2024.08.05   김지수      최초 생성
 * 2024.08.05   김지수      주차 내역 전체 페이징 조회
 * </pre>
 */
class AdminParkingListFragment : Fragment() {
    private lateinit var adminParkingListViewModel: AdminParkingListViewModel
    private var _binding: FragmentAdminParkingListBinding? = null
    private lateinit var adapter: AdminParkingListRecyclerAdapter

    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentAdminParkingListBinding.inflate(inflater, container, false)
        adminParkingListViewModel = ViewModelProvider(requireActivity()).get(AdminParkingListViewModel::class.java)

        // 오늘 날짜를 기본값으로 설정
        val calendar = Calendar.getInstance()
        val year = adminParkingListViewModel.selectedYear.value ?: calendar.get(Calendar.YEAR)
        val month = adminParkingListViewModel.selectedMonth.value ?: calendar.get(Calendar.MONTH) + 1
        val day = adminParkingListViewModel.selectedDay.value ?: calendar.get(Calendar.DAY_OF_MONTH)

        setupRecyclerView()
        setupListener()
        observeViewModel()

        adminParkingListViewModel.updateSelectedDate(year, month, day)
        refreshParkingList()
//        adminParkingListViewModel.resetData()
//        adminParkingListViewModel.fetchAdminParkingList(1, "$todayYear-${String.format("%02d", todayMonth)}-${String.format("%02d", todayDay)}")

        return binding.root
    }

    private fun setupBtnTextAsDate() {
        val year = adminParkingListViewModel.selectedYear.value ?: Calendar.getInstance().get(Calendar.YEAR)
        val month = adminParkingListViewModel.selectedMonth.value ?: Calendar.getInstance().get(Calendar.MONTH) + 1
        val day = adminParkingListViewModel.selectedDay.value ?: Calendar.getInstance().get(Calendar.DAY_OF_MONTH)
        binding.textBtnAdminParkingSelectDate.text = String.format("%02d.%02d.%02d", year % 100, month, day)
    }

    private fun setupListener() {
        binding.btnAdminParkingSelectDate.setOnClickListener {
            val bottomSheet = DateBottomSheetFragment()
            bottomSheet.show(parentFragmentManager, bottomSheet.tag)
        }

        binding.btnAdminParkingSelectParkinglot.setOnClickListener {
            val bottomSheet = StoreBottomSheetFragment()
            bottomSheet.show(parentFragmentManager, bottomSheet.tag)
        }

        binding.edtAdminParkingListCar.setOnEditorActionListener { v, actionId, event ->
            var handled = false
            if (actionId == EditorInfo.IME_ACTION_DONE) {
                Log.d("AdminParkingListFragment", "text: ${v.text.toString().trim()}")
                adminParkingListViewModel.updateSelectedCarNumber(v.text.toString().trim())

                // Hide the keyboard
                val imm = requireContext().getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
                imm.hideSoftInputFromWindow(v.windowToken, 0)

                handled = true
            }
            handled
        }
    }

    private fun setupRecyclerView() {
        adapter = AdminParkingListRecyclerAdapter(mutableListOf()) { parkingId ->
            openAdminParkingDetailFragment(parkingId)
        }
        binding.recyclerviewAdminParkingList.layoutManager = LinearLayoutManager(context)
        binding.recyclerviewAdminParkingList.adapter = adapter

        binding.recyclerviewAdminParkingList.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)
                val layoutManager = recyclerView.layoutManager as LinearLayoutManager
                val totalItemCount = layoutManager.itemCount
                val lastVisibleItem = layoutManager.findLastVisibleItemPosition()

                if (lastVisibleItem + 2 >= totalItemCount) {
                    val year = adminParkingListViewModel.selectedYear.value ?: Calendar.getInstance().get(Calendar.YEAR)
                    val month = adminParkingListViewModel.selectedMonth.value ?: Calendar.getInstance().get(Calendar.MONTH) + 1
                    val day = adminParkingListViewModel.selectedDay.value ?: Calendar.getInstance().get(Calendar.DAY_OF_MONTH)

                    val storeId: Int = if (adminParkingListViewModel.selectedStore.value == null) 1 else adminParkingListViewModel.selectedStore.value!!
                    val parkingLotId: Int = if (adminParkingListViewModel.selectedParkingLot.value == null) 1 else adminParkingListViewModel.selectedParkingLot.value!!

                    adminParkingListViewModel.fetchAdminParkingList(storeId = storeId, parkingLotId = parkingLotId, String.format("%d-%02d-%02d", year, month, day))
                }
            }
        })
    }

    private fun observeViewModel() {
        adminParkingListViewModel.adminParkingList.observe(viewLifecycleOwner, Observer { adminParkingList ->
            if (adminParkingList.isNotEmpty()) {
                binding.recyclerviewAdminParkingList.visibility = View.VISIBLE
                binding.layoutAdminParkingListNotfound.visibility = View.GONE
                adapter.setItems(adminParkingList)
            } else {
                showEmptyView("주차 정보를 찾을 수 없습니다.")
            }
        })

        adminParkingListViewModel.errorState.observe(viewLifecycleOwner, { error ->
            error?.let {
                Log.e("AdminParkingListFragment", "Error: $it")
            }
        })

        adminParkingListViewModel.selectedCarNumber.observe(viewLifecycleOwner, Observer { car ->
            Log.d("AdminParkingListFragment", "차번호 필터링: $car")
            refreshParkingList()
//            adminParkingListViewModel.resetData()
//            adminParkingListViewModel.fetchAdminParkingList(1, date = "${adminParkingListViewModel.selectedYear.value}-${String.format("%02d", adminParkingListViewModel.selectedMonth.value)}-${String.format("%02d", adminParkingListViewModel.selectedDay.value)}")
        })

        adminParkingListViewModel.selectedYear.observe(viewLifecycleOwner, Observer {
            setupBtnTextAsDate()
        })

        adminParkingListViewModel.selectedMonth.observe(viewLifecycleOwner, Observer {
            setupBtnTextAsDate()
        })

        adminParkingListViewModel.selectedDay.observe(viewLifecycleOwner, Observer {
            setupBtnTextAsDate()
        })

        adminParkingListViewModel.selectedParkingLot.observe(viewLifecycleOwner, Observer { parkingLotId ->
            Log.d("AdminParkingListFragment", "주차장 id: $parkingLotId")
            refreshParkingList()
        })
    }

    private fun refreshParkingList() {
        val year = adminParkingListViewModel.selectedYear.value ?: Calendar.getInstance().get(Calendar.YEAR)
        val month = adminParkingListViewModel.selectedMonth.value ?: Calendar.getInstance().get(Calendar.MONTH) + 1
        val day = adminParkingListViewModel.selectedDay.value ?: Calendar.getInstance().get(Calendar.DAY_OF_MONTH)

        adminParkingListViewModel.resetData()
        Log.d("AdminParkingListFragment", "recycler view reload: $year $month $day")
        val storeId: Int = if (adminParkingListViewModel.selectedStore.value == null) 1 else adminParkingListViewModel.selectedStore.value!!
        val parkingLotId: Int = if (adminParkingListViewModel.selectedParkingLot.value == null) 1 else adminParkingListViewModel.selectedParkingLot.value!!

        adminParkingListViewModel.fetchAdminParkingList(storeId = storeId, parkingLotId = parkingLotId, date = String.format("%d-%02d-%02d", year, month, day))
    }

    private fun showEmptyView(errorMessage: String) {
        binding.recyclerviewAdminParkingList.visibility = View.GONE
        binding.layoutAdminParkingListNotfound.visibility = View.VISIBLE
        binding.textAdminParkingListNotfound.text = errorMessage
    }

    private fun openAdminParkingDetailFragment(parkingId: Int) {
        val bundle = Bundle().apply {
            putInt("parkingId", parkingId)
        }
        findNavController(this@AdminParkingListFragment).navigate(R.id.navigation_parkingdetail_admin, bundle)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
