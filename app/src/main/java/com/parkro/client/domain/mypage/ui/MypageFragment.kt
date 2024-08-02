package com.parkro.client.domain.mypage.ui

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
import androidx.lifecycle.ViewModelProvider
import com.parkro.client.MainActivity
import com.parkro.client.R
import com.parkro.client.databinding.FragmentMypageBinding
import com.parkro.client.domain.login.ui.LoginActivity
import com.parkro.client.domain.logout.data.LogoutRepository
import com.parkro.client.domain.mypage.data.MypageRepository
import com.parkro.client.util.PreferencesUtil

class MypageFragment : Fragment() {
    private lateinit var mypageViewModel: MypageViewModel
    private var _binding: FragmentMypageBinding? = null

    // This property is only valid between onCreateView and onDestroyView.
    private val binding get() = _binding!!
    private val logoutRepository = LogoutRepository()
    private val mypageRepository = MypageRepository()
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        mypageViewModel = ViewModelProvider(this).get(MypageViewModel::class.java)

        _binding = FragmentMypageBinding.inflate(inflater, container, false)
        val root: View = binding.root

        // Update toolbar title from fragment
        (activity as? MainActivity)?.updateToolbarTitle(getString(R.string.title_mypage), false, false)

        // 수정 버튼
        val modifyBtn = binding.btnMypageUserProfileModify
        
        // 차량 등록 버튼
        val addCarBtn = binding.btnMypageAddCar

        // 차량 삭제 버튼
        val deleteCarBtn = binding.btnMypageCancel

        // 로그 아웃 버튼
        val logoutBtn = binding.tvMypageLogout
        
        // 회원탈퇴 버튼
        val deleteUserBtn = binding.tvMypageDeleteUser

        modifyBtn.setOnClickListener{
            val intent = Intent(requireContext(), MypageModifyUserActivity::class.java)
            startActivity(intent)
        }

        logoutBtn.setOnClickListener {
            showLogoutDialog(getString(R.string.title_logout_modal))
        }

        deleteUserBtn.setOnClickListener {
            showDeleteUserDialog(getString(R.string.title_delete_user_modal))
        }

        return root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun showLogoutDialog(message: String) {
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
                        PreferencesUtil.clear()
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
        }

        dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        dialog.show()

        dialog.window?.setLayout(
            (resources.displayMetrics.widthPixels * 0.8).toInt(),
            ViewGroup.LayoutParams.WRAP_CONTENT
        )
    }

    private fun showDeleteUserDialog(message: String) {
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
            mypageRepository.patchUserDetails{ result ->
                result.fold(
                    onSuccess = {
                        PreferencesUtil.clear()
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
        }

        dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        dialog.show()

        dialog.window?.setLayout(
            (resources.displayMetrics.widthPixels * 0.8).toInt(),
            ViewGroup.LayoutParams.WRAP_CONTENT
        )
    }
}
