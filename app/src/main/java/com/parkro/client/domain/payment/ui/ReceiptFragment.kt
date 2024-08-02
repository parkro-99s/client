package com.parkro.client.domain.payment.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.NavOptions
import androidx.navigation.fragment.NavHostFragment.findNavController
import com.parkro.client.MainActivity
import com.parkro.client.R
import com.parkro.client.databinding.FragmentReceiptBinding

class ReceiptFragment : Fragment() {

    private var _binding: FragmentReceiptBinding? = null
    private val binding get() = _binding!!
    private lateinit var receiptViewModel: ReceiptViewModel
    private lateinit var paymentViewModel: PaymentViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentReceiptBinding.inflate(inflater, container, false)
        receiptViewModel = ViewModelProvider(requireActivity()).get(ReceiptViewModel::class.java)
        paymentViewModel = ViewModelProvider(requireActivity()).get(PaymentViewModel::class.java)

        // 영수증 데이터 변경되면 (조회 또는 갱신)
        receiptViewModel.receiptData.observe(viewLifecycleOwner, Observer { receiptData ->
            receiptData?.let {
                val discountHours = receiptViewModel.calculateDiscountTime(it.totalPrice)
                binding.textReceiptValueTotalPrice.text = getString(R.string.formatted_amount_payment, it.totalPrice)
                binding.textReceiptValueReceiptDiscount.text = "${discountHours}시간"

                // payment 에서 관리되는 데이터도 업데이트
                paymentViewModel.setDiscountReceiptHours(discountHours)
            }
        })

        (activity as? MainActivity)?.updateToolbarTitle(getString(R.string.title_payment), true, false)

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.btnReceiptBtnToScanner.setOnClickListener {
            findNavController(this@ReceiptFragment).navigate(R.id.navigation_barcode_scan, null, NavOptions.Builder().setLaunchSingleTop(true).build())
        }

        binding.btnReceiptRegister.setOnClickListener {
            findNavController(this@ReceiptFragment).navigate(R.id.navigation_payment, null, NavOptions.Builder().setLaunchSingleTop(true).build())
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
