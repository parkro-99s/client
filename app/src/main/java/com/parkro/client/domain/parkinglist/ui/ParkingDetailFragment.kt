package com.parkro.client.domain.parkinglist.ui

import android.app.AlertDialog
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.navigation.fragment.NavHostFragment
import com.parkro.client.MainActivity
import com.parkro.client.R
import com.parkro.client.databinding.FragmentParkingDetailBinding
import com.parkro.client.util.DateFormatUtil

class ParkingDetailFragment : Fragment() {

    private var _binding: FragmentParkingDetailBinding? = null
    private val binding get() = _binding!!
    private val viewModel: ParkingDetailViewModel by viewModels()
    private var parkingId: Int = -1

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentParkingDetailBinding.inflate(inflater, container, false)
        (activity as? MainActivity)?.updateToolbarTitle(getString(R.string.title_parking_detail), true, false, true)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        parkingId = arguments?.getInt("parkingId") ?: -1
        if (parkingId != -1) {
            viewModel.fetchParkingDetail(parkingId)
        } else {
            Toast.makeText(requireContext(), "주차 상세 내역이 없습니다.", Toast.LENGTH_SHORT).show()
            parentFragmentManager.popBackStack()
        }

        observeViewModel()

        // 주차 내역 삭제 다이얼로그
        val toolbarDelete: ImageButton = requireActivity().findViewById(R.id.toolbar_delete)
        toolbarDelete.setOnClickListener {
            showDeleteDialog(parkingId)
        }
    }

    private fun observeViewModel() {
        viewModel.parkingDetail.observe(viewLifecycleOwner, Observer { detail ->
            binding.textParkingDetailStore.text = detail.storeName ?: "-"
            binding.textParkingDetailParkinglot.text = detail.parkingLotName ?: "-"
            binding.textCarNumber.text = detail.carNumber ?: "-"
            binding.textParkingDetailEntranedate.text = DateFormatUtil.formatDate(detail.entranceDate)
            binding.textParkingDetailPaydate.text = DateFormatUtil.formatDate(detail.paymentTime)
            binding.textParkingDetailExitdate.text = DateFormatUtil.formatDate(detail.exitDate)

            // 총 주차 시간 계산
            binding.textPaymentValueTotalTime.text =
                DateFormatUtil.formatHourMinuteToTime(detail.parkingTimeHour, detail.parkingTimeMinute)

            // 쿠폰 할인 시간
            binding.textPaymentValueDiscountCoupon.text =
                DateFormatUtil.formatHourToTime(detail.couponDiscountTime)

            // 영수증 할인 시간
            binding.textPaymentValueDiscountReceipt.text =
                DateFormatUtil.formatHourToTime(detail.receiptDiscountTime)

            // 정산 필요 시간
            binding.textParkingDetailValuePaymentTime.text = DateFormatUtil.formatMinuteToTime(detail.totalParkingTime)

            // 총 정산 금액 (예시로 paymentId 사용)
            binding.textParkingDetailAmountPayment.text = detail.totalPrice.toString()
            binding.textParkingDetailPayby.text = detail.card ?: "-"
        })

        viewModel.errorState.observe(viewLifecycleOwner, Observer { errorMessage ->
            errorMessage?.let {
                Toast.makeText(requireContext(), "해당 주차ID의 상세 내역이 여러 개입니다.", Toast.LENGTH_SHORT).show()
            }
        })

        viewModel.deleteState.observe(viewLifecycleOwner, Observer { isDeleted ->
            if (isDeleted) {
                Toast.makeText(requireContext(), "주차 내역이 삭제되었습니다.", Toast.LENGTH_SHORT).show()
                val navController = NavHostFragment.findNavController(this@ParkingDetailFragment)
                navController.popBackStack()
                navController.navigate(R.id.navigation_parkinglist)  // parkingListFragment로 네비게이트
            }
        })

    }

    // 주차 내역 삭제 다이얼로그
    private fun showDeleteDialog(parkingId: Int) {
        val dialogView = LayoutInflater.from(requireContext()).inflate(R.layout.custom_dialog_two_btns, null)
        val messageTextView = dialogView.findViewById<TextView>(R.id.text_dialog_message)
        val cancelBtn = dialogView.findViewById<ImageButton>(R.id.btn_dialog_cancel)
        val confirmBtn = dialogView.findViewById<ImageButton>(R.id.btn_dialog_check)

        messageTextView.text = "주차 내역을 삭제하시겠습니까?"

        val dialog = AlertDialog.Builder(requireContext())
            .setView(dialogView)
            .setCancelable(false)
            .create()

        // 다이얼로그 배경을 투명하게 설정
        dialog.setOnShowListener {
            dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        }

        cancelBtn.setOnClickListener {
            dialog.dismiss()
        }

        confirmBtn.setOnClickListener {
            viewModel.deleteParkingDetail(parkingId)
            dialog.dismiss()
        }

        dialog.show()

        dialog.window?.setLayout(
            (resources.displayMetrics.widthPixels * 0.8).toInt(),
            ViewGroup.LayoutParams.WRAP_CONTENT
        )
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
