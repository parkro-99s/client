package com.parkro.client.domain.payment.ui

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.NavHostFragment.findNavController
import com.parkro.client.MainActivity
import com.parkro.client.R
import com.parkro.client.databinding.FragmentPaymentBinding
import com.parkro.client.domain.payment.api.GetCurrentParkingInfo
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.*

class PaymentFragment : Fragment() {

    private lateinit var paymentViewModel: PaymentViewModel
    private var _binding: FragmentPaymentBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentPaymentBinding.inflate(inflater, container, false)
        paymentViewModel = ViewModelProvider(this).get(PaymentViewModel::class.java)
        setupToolbar()
        setupListeners()
        observeViewModel()

        paymentViewModel.fetchParkingInfo("here12314")

        return binding.root
    }

    private fun setupToolbar() {
        (activity as? MainActivity)?.updateToolbarTitle(getString(R.string.title_payment), true, false)
    }

    private fun setupListeners() {
        binding.btnPaymentToPayment.setOnClickListener {
            val parkingInfo = paymentViewModel.currentParkingInfo.value
            if (parkingInfo != null) {
                // 정산할 내역이 있으면 PaymentWebViewActivity로 이동
                val amount = calTotalAmountPayment(parkingInfo.parkingTimeHour, parkingInfo.parkingTimeMinute, parkingInfo.perPrice).toString()
                val orderId = "order_" + parkingInfo.parkingId
                val orderName = parkingInfo.parkingLotName + " 주차 정산"
                val customerName = "here12314" // 추후 유저네임으로 변경 필요
                val intent = Intent(activity, PaymentWebViewActivity::class.java).apply {
                    putExtra("amount", amount)
                    putExtra("orderId", orderId)
                    putExtra("orderName", orderName)
                    putExtra("customerName", customerName)
                }
                startActivity(intent)
            }
        }

        binding.btnPaymentReceipt.setOnClickListener {
            findNavController(this@PaymentFragment).navigate(R.id.navigation_receipt)
        }
    }

    private fun observeViewModel() {
        paymentViewModel.currentParkingInfo.observe(viewLifecycleOwner, Observer { parking ->
            if (parking != null) {
                updateUI(parking)
            } else {
                showEmptyState()
            }
        })
    }

    private fun updateUI(currentParking: GetCurrentParkingInfo) {
        binding.apply {
            // Hide empty state views
            imgPaymentCar.visibility = View.GONE
            textPaymentEmptyMessage.visibility = View.GONE

            // Show payment details
            textPaymentStoreName.visibility = View.VISIBLE
            textPaymentParkingLotName.visibility = View.VISIBLE
            textPaymentEntranceDate.visibility = View.VISIBLE
            layoutPaymentBtnCoupon.visibility = View.VISIBLE
            layoutPaymentBtnReceipt.visibility = View.VISIBLE
            imgCarNumberFrame.visibility = View.VISIBLE
            layoutPaymentCurrStatus.visibility = View.VISIBLE
            textPaymentTitleAmountPayment.visibility = View.VISIBLE
            textPaymentValueAmountPayment.visibility = View.VISIBLE
            layoutPaymentBtn.visibility = View.VISIBLE

            textPaymentStoreName.text = currentParking.storeName
            textPaymentParkingLotName.text = currentParking.parkingLotName
            textPaymentEntranceDate.text = formatDateTime(currentParking.entranceDate)
            textCarNumber.text = currentParking.carNumber

            textPaymentValueTotalTime.text = getString(R.string.formatted_payment_time, currentParking.parkingTimeHour, currentParking.parkingTimeMinute)
            textPaymentValuePaymentTime.text = getString(R.string.formatted_payment_time, currentParking.parkingTimeHour, currentParking.parkingTimeMinute)

            textPaymentValueAmountPayment.text = getString(R.string.formatted_amount_payment, calTotalAmountPayment(currentParking.parkingTimeHour, currentParking.parkingTimeMinute, currentParking.perPrice))
        }
    }

    private fun showEmptyState() {
        binding.apply {

            imgPaymentCar.visibility = View.VISIBLE
            textPaymentEmptyMessage.visibility = View.VISIBLE

            textPaymentStoreName.visibility = View.GONE
            textPaymentParkingLotName.visibility = View.GONE
            textPaymentEntranceDate.visibility = View.GONE
            layoutPaymentBtnCoupon.visibility = View.GONE
            layoutPaymentBtnReceipt.visibility = View.GONE
            imgCarNumberFrame.visibility = View.GONE
            layoutPaymentCurrStatus.visibility = View.GONE
            textPaymentTitleAmountPayment.visibility = View.GONE
            textPaymentValueAmountPayment.visibility = View.GONE
            layoutPaymentBtn.visibility = View.GONE
        }
    }

    private fun calTotalAmountPayment(hh: Int?, mm: Int?, perPrice: Int?): Int {
        val hours = hh ?: 0
        val minutes = mm ?: 0
        val pricePerUnit = perPrice ?: 0

        return ((hours * 60 + minutes) / 10) * pricePerUnit
    }

    private fun formatDateTime(inputDate: String): String {
        val inputFormat = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault())
        val outputFormat = SimpleDateFormat(getString(R.string.formatted_parking_entrance_date), Locale.getDefault())

        return try {
            val date = inputFormat.parse(inputDate)
            outputFormat.format(date ?: Date())
        } catch (e: ParseException) {
            "Invalid Date"
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
