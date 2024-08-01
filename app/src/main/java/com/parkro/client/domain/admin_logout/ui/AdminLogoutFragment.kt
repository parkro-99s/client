package com.parkro.client.domain.admin_logout.ui

import android.app.AlertDialog
import android.content.Intent
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
import com.parkro.client.R
import com.parkro.client.Utils
import com.parkro.client.databinding.FragmentAdminLogoutBinding
import com.parkro.client.domain.login.ui.LoginActivity
import com.parkro.client.domain.logout.data.LogoutRepository

class AdminLogoutFragment : Fragment() {
    private var _binding: FragmentAdminLogoutBinding? = null
    private val binding get() = _binding!!

    private val logoutRepository = LogoutRepository()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentAdminLogoutBinding.inflate(inflater, container, false)
        val root: View = binding.root

        showCustomDialog(getString(R.string.title_logout_modal))

        return root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun showCustomDialog(message: String) {
        val dialogView = LayoutInflater.from(requireContext()).inflate(R.layout.custom_dialog_two_btns, null)
        val messageTextView = dialogView.findViewById<TextView>(R.id.text_dialog_message)
        val confirmButton = dialogView.findViewById<ImageButton>(R.id.btn_dialog_check)
        val cancelButton = dialogView.findViewById<ImageButton>(R.id.btn_dialog_cancel)

        messageTextView.text = message

        val dialog = AlertDialog.Builder(requireContext())
            .setView(dialogView)
            .setCancelable(false)
            .create()

        confirmButton.setOnClickListener {
            logoutRepository.postLogout { result ->
                result.fold(
                    onSuccess = {
                        Utils.clear()
                        activity?.let { ctx ->
                            val intent = Intent(ctx, LoginActivity::class.java)
                            dialog.dismiss()
                            startActivity(intent)
                        }
                    },
                    onFailure = { error ->
                        Log.d("error", "error: $error")
                    }
                )
            }
        }

        cancelButton.setOnClickListener {
            dialog.dismiss()
            activity?.onBackPressed()
        }

        dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        dialog.show()

        dialog.window?.setLayout(
            (resources.displayMetrics.widthPixels * 0.8).toInt(),
            ViewGroup.LayoutParams.WRAP_CONTENT
        )
    }
}
