package com.parkro.client.domain.payment.ui

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.TextView
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

    private val idleTimeLimit = 60000L // 1분
    private val handler = Handler(Looper.getMainLooper())
    private val idleRunnable = Runnable {
        if (isAdded) {
            showAlertAndRefreshPage()
        }
    }
    private var isDialogVisible = false // 팝업 상태를 관리하는 변수

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentPaymentBinding.inflate(inflater, container, false)
        paymentViewModel = ViewModelProvider(requireActivity()).get(PaymentViewModel::class.java)

        setupToolbar()
        setupListeners()
        observeViewModel()

        paymentViewModel.fetchParkingInfo("here12314")

        startIdleTimer()

        return binding.root
    }
    private fun setupToolbar() {
        (activity as? MainActivity)?.updateToolbarTitle(getString(R.string.title_payment), false, false)
    }

    private fun startIdleTimer() {
        handler.postDelayed(idleRunnable, idleTimeLimit)
    }

    private fun resetIdleTimer() {
        handler.removeCallbacks(idleRunnable)
        startIdleTimer()
    }

    private fun showAlertAndRefreshPage() {
        if (isDialogVisible) return

        context?.let {
            val dialogView = LayoutInflater.from(requireContext()).inflate(R.layout.custom_dialog, null)
            val messageTextView = dialogView.findViewById<TextView>(R.id.text_dialog_message)
            val confirmBtn = dialogView.findViewById<ImageButton>(R.id.btn_dialog_check)

            messageTextView.text = "1분 이상 동작이 없어 새로고침합니다." // 수정 필요

            val dialog = AlertDialog.Builder(it)
                .setView(dialogView)
                .setCancelable(false)
                .create()

            confirmBtn.setOnClickListener {
                dialog.dismiss()
                paymentViewModel.resetDiscounted()
                isDialogVisible = false
                resetIdleTimer()
                refreshPage()
            }

            dialog.window?.setLayout(
                (resources.displayMetrics.widthPixels * 0.9).toInt(),
                ViewGroup.LayoutParams.WRAP_CONTENT
            )

            dialog.show()

        }
    }

    private fun refreshPage() {
        paymentViewModel.fetchParkingInfo("here12314")
    }

    private fun setupListeners() {
        binding.btnPaymentToPayment.setOnClickListener {
            paymentViewModel.currentParkingInfo.value?.let { parkingInfo ->
                val amount = paymentViewModel.totalAmountToPay.value.toString()
                val orderId = "order_" + parkingInfo.parkingId
                val orderName = "${parkingInfo.parkingLotName} 주차 정산"
                val customerName = "here12314"
              
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

//        binding.btnPaymentCoupon.setOnClickListener {
//            findNavController(this@PaymentFragment).navigate(R.id.navigation_coupon)
//        }
    }

    private fun observeViewModel() {
        paymentViewModel.currentParkingInfo.observe(viewLifecycleOwner, Observer { parking ->
            if (parking != null) {
                updateUI(parking) // UI 업데이트
            } else {
                showEmptyState()
            }
            paymentViewModel.calculatePaymentTotalTime()
            resetIdleTimer()
        })

        paymentViewModel.discountReceiptHours.observe(viewLifecycleOwner, Observer { hours ->
            updateDiscountReceiptHours(hours ?: 0)
            resetIdleTimer()
        })

        paymentViewModel.totalTimeToPay.observe(viewLifecycleOwner, Observer { totalTime ->
            updatePaymentTime(totalTime) // UI 업데이트
            paymentViewModel.calculateAmountToPay() // 정산 필요 시간 업데이트 후 정산 금액 적용
            resetIdleTimer()
        })

        paymentViewModel.totalAmountToPay.observe(viewLifecycleOwner, Observer { totalTime ->
            updateAmountPayment() // UI 업데이트
            resetIdleTimer()
        })
    }

    @SuppressLint("SetTextI18n")
    private fun updateUI(currentParking: GetCurrentParkingInfo) {
        binding.apply {
            imgPaymentCar.visibility = View.GONE
            textPaymentEmptyMessage.visibility = View.GONE

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

            val totalTimeToPay: Int? = paymentViewModel.totalTimeToPay.value
            textPaymentValueTotalTime.text = getString(R.string.formatted_payment_time, currentParking.parkingTimeHour, currentParking.parkingTimeMinute)
            textPaymentValuePaymentTime.text = getString(R.string.formatted_payment_time, totalTimeToPay ?: 0 / 60, totalTimeToPay ?: 0 % 60)
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

    private fun updateAmountPayment() {
        val amount = paymentViewModel.totalAmountToPay.value ?: 0
        binding.textPaymentValueAmountPayment.text = getString(R.string.formatted_amount_payment, amount)
    }

    private fun updateDiscountReceiptHours(hours: Int) {
        binding.textPaymentValueDiscountReceipt.text = getString(R.string.formatted_discount_time, hours, 0)
    }

    private fun updatePaymentTime(totalTime: Int?) {
        val totalMinutes = totalTime ?: 0
        val resultHours = totalMinutes / 60
        val resultMinutes = totalMinutes % 60
        binding.textPaymentValuePaymentTime.text = getString(R.string.formatted_payment_time, resultHours, resultMinutes)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
