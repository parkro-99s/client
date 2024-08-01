package com.parkro.client.domain.payment.ui

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
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
        binding.btnToPayment.setOnClickListener {
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
    }

    private fun observeViewModel() {
        paymentViewModel.currentParkingInfo.observe(viewLifecycleOwner, Observer { parking ->
            println("parking: " + parking)
            if (parking != null) {
                updateUI(parking)
            } else {
                showEmptyState()
            }
        })
    }

    @SuppressLint("StringFormatMatches")
    private fun updateUI(currentParking: GetCurrentParkingInfo) {
        binding.apply {
            // Hide empty state views
            icCarEmptyPayment.visibility = View.GONE
            contentEmptyPayment.visibility = View.GONE

            // Show payment details
            paymentStoreNameText.visibility = View.VISIBLE
            paymentParkingLotNameText.visibility = View.VISIBLE
            paymentEntranceDateText.visibility = View.VISIBLE
            useCouponFrame.visibility = View.VISIBLE
            useReceiptFrame.visibility = View.VISIBLE
            carNumberFrame.visibility = View.VISIBLE
            paymentFrame.visibility = View.VISIBLE
            textTitleAmountPayment.visibility = View.VISIBLE
            textValueAmountPayment.visibility = View.VISIBLE
            moveToPaymentFrame.visibility = View.VISIBLE

            paymentStoreNameText.text = currentParking.storeName
            paymentParkingLotNameText.text = currentParking.parkingLotName
            paymentEntranceDateText.text = formatDateTime(currentParking.entranceDate)
            parkingCarNumberText.text = currentParking.carNumber

            textValueParkingTime.text = getString(R.string.formatted_payment_time, currentParking.parkingTimeHour, currentParking.parkingTimeMinute)
            textValuePaymentTime.text = getString(R.string.formatted_payment_time, currentParking.parkingTimeHour, currentParking.parkingTimeMinute)

            textValueAmountPayment.text = getString(R.string.formatted_amount_payment, calTotalAmountPayment(currentParking.parkingTimeHour, currentParking.parkingTimeMinute, currentParking.perPrice))
        }
    }

    private fun showEmptyState() {
        binding.apply {
            // Show empty state views
            icCarEmptyPayment.visibility = View.VISIBLE
            contentEmptyPayment.visibility = View.VISIBLE

            // Hide payment details
            paymentStoreNameText.visibility = View.GONE
            paymentParkingLotNameText.visibility = View.GONE
            paymentEntranceDateText.visibility = View.GONE
            btnUseCoupon.visibility = View.GONE
            btnUseReceipt.visibility = View.GONE
            carNumberFrame.visibility = View.GONE
            paymentFrame.visibility = View.GONE
            textTitleAmountPayment.visibility = View.GONE
            textValueAmountPayment.visibility = View.GONE
            moveToPaymentFrame.visibility = View.GONE
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
