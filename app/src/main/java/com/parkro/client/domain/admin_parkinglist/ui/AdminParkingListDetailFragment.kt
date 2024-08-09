package com.parkro.client.domain.admin_parkinglist.ui

import android.app.AlertDialog
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.TextView
import android.widget.Toast
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.NavOptions
import androidx.navigation.fragment.NavHostFragment
import com.parkro.client.MainActivity
import com.parkro.client.R
import com.parkro.client.common.data.ErrorRes
import com.parkro.client.databinding.FragmentAdminParkingListBinding
import com.parkro.client.databinding.FragmentAdminParkingListDetailBinding
import com.parkro.client.domain.admin.ui.AdminActivity
import com.parkro.client.domain.admin_parkinglist.api.GetAdminParkingDetailRes
import com.parkro.client.util.DateFormatUtil
import java.util.concurrent.TimeUnit

/**
 * 관리자 주차 상세 내역 Fragment
 *
 * @author 김지수
 * @since 2024.08.05
 *
 * <pre>
 * 수정일자       수정자        수정내용
 * ------------ --------    ---------------------------
 * 2024.08.05   김지수      최초 생성
 * 2024.08.05   김지수      관리자 주차 목록 전체 조회
 * 2024.08.06   김지수      모달창 안내 추가
 * </pre>
 */
class AdminParkingListDetailFragment : Fragment() {

    private lateinit var adminParkingListDetailViewModel: AdminParkingListDetailViewModel
    private lateinit var adminParkingListViewModel: AdminParkingListViewModel
    private var _binding: FragmentAdminParkingListDetailBinding? = null
    private var parkingId: Int = -1

    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        _binding = FragmentAdminParkingListDetailBinding.inflate(inflater, container, false)
        adminParkingListDetailViewModel = ViewModelProvider(this).get(AdminParkingListDetailViewModel::class.java)
        adminParkingListViewModel = ViewModelProvider(this).get(AdminParkingListViewModel::class.java)

        parkingId = arguments?.getInt("parkingId") ?: -1

        if (parkingId == -1) {
            Toast.makeText(requireContext(), "주차 상세 내역이 없습니다.", Toast.LENGTH_SHORT).show()
            parentFragmentManager.popBackStack()
        } else {
            adminParkingListDetailViewModel.fetchAdminParkingListDetail(parkingId)
        }

        (activity as? AdminActivity)?.updateToolbarTitle(getString(R.string.title_parking_detail), true, false)

        setupListeners()
        observeViewModel()

        Log.d("AdminParkingListDetailFragment", "주차 상세 정보: ${adminParkingListDetailViewModel.adminParkingListDetail.value}")

        return binding.root
    }

    private fun setupListeners() {
        binding.btnAdminParkingCompletePayment.setOnClickListener {
            Log.d("AdminParkingListDetailFragment", "버튼 클릭! $parkingId")
            adminParkingListDetailViewModel.fetchAdminParking(parkingId)
            showConfirmAlert()

            NavHostFragment.findNavController(this@AdminParkingListDetailFragment).navigate(
                R.id.navigation_parkinglist_admin,
                null,
                NavOptions.Builder().setLaunchSingleTop(true).build()
            )
        }
    }

    private fun observeViewModel() {
        adminParkingListDetailViewModel.adminParkingListDetail.observe(viewLifecycleOwner, Observer { detail ->
            if (detail != null)
            updateUI(detail)
        })
    }

    fun updateUI(detail: GetAdminParkingDetailRes) {
        Log.d("AdminParkingListDetailFragment", "주차 상세 정보 조회: $detail")
        binding.apply {
            textAdminParkingStoreName.text = detail.storeName
            textAdminParkingParkinglotName.text = detail.parkingLotName

            textAdminParkingValueCar.text = detail.carNumber
            textAdminParkingValueEntrance.text = DateFormatUtil.formatDate(detail.entranceDate)

            when (detail.parkingStatus) {
                "ENTRANCE" -> updateUIENTRANCE()
                "PAY" -> updateUIPAY(detail)
                "EXIT" -> updateUIEXIT(detail)
            }

        }
    }

    private fun updateUIENTRANCE() {
        binding.apply {
            textAdminParkingValueStatus.text = "입차 완료"

            textAdminParkingValueExit.text = getString(R.string.default_value_payment)

            textAdminParkingValuePaymentDate.text = getString(R.string.default_value_payment)
            textAdminParkingValuePaymentCoupon.text = getString(R.string.default_value_payment)
            textAdminParkingValuePaymentReceipt.text = getString(R.string.default_value_payment)
            textAdminParkingValuePaymentPaymentTime.text = getString(R.string.default_value_payment)

            textAdminParkingValuePaymentTotalPrice.text = getString(R.string.default_value_payment)

            btnAdminParkingCompletePayment.isEnabled = true
            btnAdminParkingCompletePaymentDisabled.visibility = View.GONE
            btnAdminParkingCompletePayment.visibility = View.VISIBLE
        }
    }

    private fun updateUIPAY(detail: GetAdminParkingDetailRes) {
        binding.apply {
            textAdminParkingValueStatus.text = "결제 완료"

            textAdminParkingValueExit.text = getString(R.string.default_value_payment)

            textAdminParkingValuePaymentDate.text = if (detail.paymentDate == null) getString(R.string.default_value_payment) else detail.paymentDate
            textAdminParkingValuePaymentCoupon.text = getString(R.string.formatted_discount_hour, detail.couponDiscountTime.toString())
            textAdminParkingValuePaymentReceipt.text = getString(R.string.formatted_discount_hour, detail.receiptDiscountTime.toString())

            textAdminParkingValuePaymentPaymentTime.text = if (detail.paymentDate == null) getString(R.string.default_value_payment) else DateFormatUtil.formatMinuteToTime(TimeUnit.MILLISECONDS.toMinutes(DateFormatUtil.parseDate(detail.paymentDate).time - DateFormatUtil.parseDate(detail.entranceDate).time).toInt())

            textAdminParkingValuePaymentTotalPrice.text = getString(R.string.formatted_amount_payment, detail.totalPrice)

            btnAdminParkingCompletePayment.isEnabled = false
            btnAdminParkingCompletePaymentDisabled.visibility = View.VISIBLE
            btnAdminParkingCompletePayment.visibility = View.GONE
        }
    }

    private fun updateUIEXIT(detail: GetAdminParkingDetailRes) {
        binding.apply {
            textAdminParkingValueStatus.text = "출차 완료"

            textAdminParkingValueExit.text = if (detail.exitDate == null) getString(R.string.default_value_payment) else DateFormatUtil.formatDate(detail.exitDate)

            textAdminParkingValuePaymentDate.text = if (detail.paymentDate == null) getString(R.string.default_value_payment) else detail.paymentDate
            textAdminParkingValuePaymentCoupon.text = getString(R.string.formatted_discount_hour, detail.couponDiscountTime.toString())
            textAdminParkingValuePaymentReceipt.text = getString(R.string.formatted_discount_hour, detail.receiptDiscountTime.toString())

            textAdminParkingValuePaymentPaymentTime.text = if (detail.paymentDate == null) getString(R.string.default_value_payment) else DateFormatUtil.formatDifferenceToDateString(detail.entranceDate, detail.paymentDate)
            Log.d("AdminParkingListDetailFragment", "update ui exit $detail")

            textAdminParkingValuePaymentTotalPrice.text = getString(R.string.formatted_amount_payment, detail.totalPrice)

            btnAdminParkingCompletePayment.isEnabled = false
            btnAdminParkingCompletePaymentDisabled.visibility = View.VISIBLE
            btnAdminParkingCompletePayment.visibility = View.GONE
        }
    }

    private fun showConfirmAlert() {
        val dialogView = LayoutInflater.from(requireContext()).inflate(R.layout.custom_dialog, null)
        val messageTextView = dialogView.findViewById<TextView>(R.id.text_dialog_message)
        val confirmButton = dialogView.findViewById<ImageButton>(R.id.btn_dialog_check)

        messageTextView.text = "결제 완료 처리가 되었습니다."

        val dialog = AlertDialog.Builder(requireContext())
            .setView(dialogView)
            .setCancelable(false)
            .create()

        confirmButton.setOnClickListener {
            dialog.dismiss()
        }

        dialog.setOnShowListener {
            dialog.window?.let { window ->
                window.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
                window.setLayout(
                    (resources.displayMetrics.widthPixels * 0.8).toInt(),
                    ViewGroup.LayoutParams.WRAP_CONTENT
                )
            }
        }

        dialog.show()
    }
}