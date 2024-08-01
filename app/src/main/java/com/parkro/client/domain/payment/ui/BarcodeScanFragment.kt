package com.parkro.client.domain.payment.ui

import android.app.AlertDialog
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
import com.parkro.client.R
import com.parkro.client.databinding.FragmentBarcodeScanBinding
import com.parkro.client.common.data.ErrorRes

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

        dialog.show()

        dialog.setOnDismissListener {
            isProcessingError = false
            resumeScanning()
        }
    }
}
