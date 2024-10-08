package com.parkro.client.domain.payment.ui

import android.app.AlertDialog
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.NavHostFragment.findNavController
import com.journeyapps.barcodescanner.BarcodeCallback
import com.journeyapps.barcodescanner.BarcodeResult
import com.journeyapps.barcodescanner.CaptureManager
import com.parkro.client.MainActivity
import com.parkro.client.R
import com.parkro.client.common.data.ErrorRes
import com.parkro.client.databinding.FragmentBarcodeScanBinding

/**
 * 바코드 스캔 Fragment
 *
 * @author 김지수
 * @since 2024.08.01
 *
 * <pre>
 * 수정일자       수정자        수정내용
 * ------------ --------    ---------------------------
 * 2024.08.01   김지수      최초 생성
 * 2024.08.01   김지수      영수증 바코드 스캔 기능
 * </pre>
 */
class BarcodeScanFragment : Fragment() {

    private var _binding: FragmentBarcodeScanBinding? = null
    private val binding get() = _binding!!
    private lateinit var captureManager: CaptureManager
    private lateinit var receiptViewModel: ReceiptViewModel
    private var isProcessingError = false

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentBarcodeScanBinding.inflate(inflater, container, false)
        receiptViewModel = ViewModelProvider(requireActivity()).get(ReceiptViewModel::class.java)

        captureManager = CaptureManager(requireActivity(), binding.zxingBarcodeScanner)
        captureManager.initializeFromIntent(requireActivity().intent, savedInstanceState)
        binding.zxingBarcodeScanner.decodeContinuous(callback)

        receiptViewModel.resetReceiptData()

        (activity as? MainActivity)?.updateToolbarTitle(getString(R.string.title_payment), true, false)

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        observeViewModel()
    }

    override fun onResume() {
        super.onResume()
        captureManager.onResume()
    }

    override fun onPause() {
        super.onPause()
        captureManager.onPause()
    }

    override fun onDestroy() {
        super.onDestroy()
        captureManager.onDestroy()
        _binding = null
    }

    private val callback = BarcodeCallback { result: BarcodeResult ->
        activity?.runOnUiThread {
            val receiptId = try {
                result.text.toInt()
            } catch (e: NumberFormatException) {
                return@runOnUiThread
            }
            receiptViewModel.fetchMemberReceiptInfo(receiptId)
        }
    }

    private fun observeViewModel() {
        receiptViewModel.receiptData.observe(viewLifecycleOwner, { result ->
            result?.let {
                // 성공적으로 데이터를 가져오면 영수증 화면으로 이동
                findNavController(this@BarcodeScanFragment).navigate(R.id.navigation_receipt)
            }
        })

        receiptViewModel.errorMessage.observe(viewLifecycleOwner, { error ->
            if (error != null && !isProcessingError) {
                isProcessingError = true
                stopScanning()

                showCustomErrorDialog(error)
            }
        })
    }

    private fun stopScanning() {
        captureManager.onPause()
    }

    private fun resumeScanning() {
        captureManager.onResume()
    }

    private fun showCustomErrorDialog(error: ErrorRes) {
        val dialogView = LayoutInflater.from(requireContext()).inflate(R.layout.custom_dialog, null)
        val messageTextView = dialogView.findViewById<TextView>(R.id.text_dialog_message)
        val confirmButton = dialogView.findViewById<ImageButton>(R.id.btn_dialog_check)

        messageTextView.text = error.message

        val dialog = AlertDialog.Builder(requireContext())
            .setView(dialogView)
            .setCancelable(false)
            .create()

        confirmButton.setOnClickListener {
            dialog.dismiss()
        }

        dialog.setOnDismissListener {
            isProcessingError = false
            resumeScanning()
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
