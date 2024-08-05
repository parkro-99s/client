package com.parkro.client.domain.admin_parkinglist.ui

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.fragment.NavHostFragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.parkro.client.MainActivity
import com.parkro.client.R
import com.parkro.client.databinding.FragmentAdminParkingListBinding
import com.parkro.client.domain.admin.ui.AdminActivity

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
        adminParkingListViewModel = ViewModelProvider(this).get(AdminParkingListViewModel::class.java)

        setupRecyclerView()
        observeViewModel()

        adminParkingListViewModel.fetchAdminParkingList(1, "2024-07-30")
        adminParkingListViewModel.resetSelectedParkingDetail()
//        adminParkingListViewModel.fetchAdminParkingList(2, "2024-07-30") // 데이터 X

        (activity as? MainActivity)?.updateToolbarTitle(getString(R.string.title_parkinglist), false, true)

        return binding.root
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
                    adminParkingListViewModel.fetchAdminParkingList(1, "2024-07-30")
//                    adminParkingListViewModel.fetchAdminParkingList(2, "2024-07-30")  // 데이터 X
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
                Log.d("AdminParkingListFragment", "주차 정보 업데이트됨 ${adapter.itemCount}")
            } else {
                showEmptyView("주차 정보를 찾을 수 없습니다.")
            }
        })

        adminParkingListViewModel.errorState.observe(viewLifecycleOwner, Observer { error ->
            error?.let {
                Log.e("AdminParkingListFragment", "Error: $it")
            }
        })

        adminParkingListViewModel.adminParkingListSelectedParkingId.observe(viewLifecycleOwner, Observer { parkingId ->
            Log.d("AdminParkingListFragment", "주차 정보 상세 보기 클릭: $parkingId")
        })
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
