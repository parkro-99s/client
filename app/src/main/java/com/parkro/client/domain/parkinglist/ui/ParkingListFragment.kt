package com.parkro.client.domain.parkinglist.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.NavHostFragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.parkro.client.MainActivity
import com.parkro.client.R
import com.parkro.client.databinding.FragmentParkinglistBinding
import com.parkro.client.util.PreferencesUtil

class ParkingListFragment : Fragment() {

    private lateinit var parkingListViewModel: ParkingListViewModel
    private var _binding: FragmentParkinglistBinding? = null
    private val binding get() = _binding!!
    private lateinit var adapter: ParkingRecyclerAdapter
    private lateinit var username: String

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        parkingListViewModel = ViewModelProvider(this).get(ParkingListViewModel::class.java)
        _binding = FragmentParkinglistBinding.inflate(inflater, container, false)
        val root: View = binding.root

        // 툴바 설정
        (activity as? MainActivity)?.updateToolbarTitle(getString(R.string.title_parkinglist), false, true)

        username = PreferencesUtil.getUsername("user2").toString()

        setupRecyclerView()
        observeViewModel()

        // 최초 데이터 로드
        parkingListViewModel.fetchParkingList(username)

        return root
    }

    // RecyclerView 설정
    private fun setupRecyclerView() {

        // 카드 아이템 터치 시, 상세 페이지로 이동
        adapter = ParkingRecyclerAdapter(mutableListOf()) { parkingId ->
            openParkingDetailFragment(parkingId)
        }
        adapter = ParkingRecyclerAdapter(mutableListOf())
        binding.recyclerviewParkingList.adapter = adapter

        // 레이아웃 매니저 - 수직 스크롤
        val layoutManager = LinearLayoutManager(context)
        binding.recyclerviewParkingList.layoutManager = layoutManager

        // 스크롤 리스너 추가
        binding.recyclerviewParkingList.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)
                val totalItemCount = layoutManager.itemCount
                val lastVisibleItem = layoutManager.findLastVisibleItemPosition()

                // 마지막 아이템 근처에 도달하면 추가 데이터 로드
                if (lastVisibleItem + 2 >= totalItemCount) {
                    parkingListViewModel.fetchParkingList(username)
                }
            }
        })
    }

    private fun observeViewModel() {
        // 주차 리스트 관찰
        parkingListViewModel.parkingList.observe(viewLifecycleOwner) { parkingList ->
            if (parkingList.isNotEmpty()) {
                // 리사이클러뷰 visible
                binding.recyclerviewParkingList.visibility = View.VISIBLE
                binding.layoutParkinglistNotfound.visibility = View.GONE
                adapter.addItems(parkingList)
            } else {
                showEmptyView(getString(R.string.error_parkinglist_not_found))
            }
        }

        // 에러 상태 관찰
        parkingListViewModel.errorState.observe(viewLifecycleOwner) { errorCode ->
            errorCode?.let {
                val errorMessage = when (it) {
                    409 -> getString(R.string.error_parkinglist_not_found)
                    400 -> getString(R.string.error_user_not_found)
                    else -> getString(R.string.error_service_unavailable)
                }
                showEmptyView(errorMessage)
            }
        }
    }

    // 빈 화면 표시
    private fun showEmptyView(errorMessage: String) {
        binding.recyclerviewParkingList.visibility = View.GONE
        binding.layoutParkinglistNotfound.visibility = View.VISIBLE
        binding.textParkinglistNotfound.text = errorMessage
    }

    // 주차 내역 상세 페이지
    private fun openParkingDetailFragment(parkingId: Int) {
        val bundle = Bundle().apply {
            putInt("parkingId", parkingId)
        }
        NavHostFragment.findNavController(this).navigate(R.id.action_parkingListFragment_to_parkingDetailFragment, bundle)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}