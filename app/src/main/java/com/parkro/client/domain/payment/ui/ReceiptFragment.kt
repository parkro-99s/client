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

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentReceiptBinding.inflate(inflater, container, false)
        receiptViewModel = ViewModelProvider(requireActivity()).get(ReceiptViewModel::class.java)

        receiptViewModel.receiptData.observe(viewLifecycleOwner, Observer { receiptData ->
            receiptData?.let {
                binding.textReceiptValueTotalPrice.text = getString(R.string.formatted_amount_payment, it.totalPrice.toString())
                binding.textReceiptValueReceiptDiscount.text = calculateDiscountTime(it.totalPrice)
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
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun calculateDiscountTime(totalPrice: Int): String {
        return when {
            totalPrice >= 60000 -> "5시간"
            totalPrice >= 40000 -> "3시간"
            totalPrice >= 30000 -> "2시간"
            totalPrice >= 20000 -> "1시간"
            else -> "0시간"
        }
    }
}
