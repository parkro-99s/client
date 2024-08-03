package com.parkro.client.domain.payment.ui

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.NavOptions
import androidx.navigation.fragment.NavHostFragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.parkro.client.MainActivity
import com.parkro.client.R
import com.parkro.client.databinding.FragmentCouponBinding
import com.parkro.client.databinding.FragmentReceiptBinding
import com.parkro.client.domain.payment.api.GetMemberCouponListItem

class CouponFragment : Fragment() {

    private var _binding: FragmentCouponBinding? = null
    private val binding get() = _binding!!
    private lateinit var couponViewModel: CouponViewModel
    private lateinit var paymentViewModel: PaymentViewModel
    private lateinit var adapter: CouponRecyclerAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentCouponBinding.inflate(inflater, container, false)
        couponViewModel = ViewModelProvider(requireActivity()).get(CouponViewModel::class.java)
        paymentViewModel = ViewModelProvider(requireActivity()).get(PaymentViewModel::class.java)

        binding.recyclerviewCouponList.layoutManager = LinearLayoutManager(context)

        setupRecyclerView()
        setupListeners()
        observeViewModel()

        couponViewModel.fetchMemberCouponList("here12314")

        (activity as? MainActivity)?.updateToolbarTitle(getString(R.string.title_coupon), true, false)

        return binding.root
    }

    private fun setupRecyclerView() {
        adapter = CouponRecyclerAdapter(emptyList(), object : CouponRecyclerAdapter.OnItemClickListener {
            override fun onItemClick(coupon: GetMemberCouponListItem, position: Int) {
                onCouponClicked(coupon, position)
                couponViewModel.selectCoupon(coupon)
                Log.d("CouponFragment", "selected coupon: ${couponViewModel.selectedCoupon.value}")
            }
        })
        binding.recyclerviewCouponList.layoutManager = LinearLayoutManager(requireContext())
        binding.recyclerviewCouponList.adapter = adapter
    }

    private fun setupListeners() {
        binding.btnCouponUse.setOnClickListener {
            onCouponUseClicked()
            NavHostFragment.findNavController(this@CouponFragment)
                .navigate(R.id.navigation_payment, null, NavOptions.Builder().setLaunchSingleTop(true).build())

            // 정산 페이지로 이동할 때 선택된 쿠폰이 없다면 쿠폰으로 인한 할인 시간 0으로 초기화
            if (couponViewModel.selectedCoupon.value == null) {
                paymentViewModel.setDiscountCouponHours(0)
            }
            else {
                couponViewModel.selectedCoupon.value?.discountHour?.let { hours ->
                    paymentViewModel.setDiscountCouponHours(hours)
                }
            }
        }
    }

    private fun observeViewModel() {
        couponViewModel.couponList.observe(viewLifecycleOwner, Observer { couponList ->
            couponList?.let {
                adapter.updateCouponList(it)
                adapter.updateSelectedCoupon(couponViewModel.selectedCoupon.value)
            }
        })

        couponViewModel.selectedCoupon.observe(viewLifecycleOwner, Observer { selectedCoupon ->
            adapter.updateSelectedCoupon(selectedCoupon)
        })
    }

    // position: recycler view 에서의 순서
    private fun onCouponClicked(coupon: GetMemberCouponListItem, position: Int) {
//        Toast.makeText(requireContext(), "Selected Coupon: ${coupon.discountHour}시간 무료 주차권", Toast.LENGTH_SHORT).show()
        Log.d("CouponFragment", "Selected Coupon: ${coupon.discountHour}시간 무료 주차권")
        couponViewModel.selectCoupon(coupon)
    }

    private fun onCouponUseClicked() {
        couponViewModel.selectedCoupon.observe(viewLifecycleOwner, Observer { selectedCoupon ->
            selectedCoupon?.let {
//                Toast.makeText(requireContext(), "사용할 쿠폰: ${it.discountHour}시간 무료 주차권", Toast.LENGTH_SHORT).show()
                Log.d("CouponFragment", "사용할 쿠폰: ${it.discountHour}시간 무료 주차권")
            } ?: run {
                Log.d("CouponFragment", "선택된 쿠폰이 없습니다.")
            }
        })
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}