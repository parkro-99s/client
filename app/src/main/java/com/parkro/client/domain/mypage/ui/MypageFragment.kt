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
import android.widget.*
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentTransaction
import androidx.lifecycle.ViewModelProvider
import com.parkro.client.MainActivity
import com.parkro.client.R
import com.parkro.client.databinding.FragmentMypageBinding
import com.parkro.client.domain.login.ui.LoginActivity
import com.parkro.client.domain.logout.data.LogoutRepository
import com.parkro.client.domain.mypage.data.MypageRepository
import com.parkro.client.util.PreferencesUtil
import androidx.fragment.app.FragmentActivity




class MypageFragment : Fragment() {
    private lateinit var mypageViewModel: MypageViewModel
    private var _binding: FragmentMypageBinding? = null
    private val binding get() = _binding!!
    private val logoutRepository = LogoutRepository()
    private val mypageRepository = MypageRepository()

    private var username: String? = null
    private var nickname: String? = null
    private var carNumber: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // ViewModel 초기화
        mypageViewModel = ViewModelProvider(this).get(MypageViewModel::class.java)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentMypageBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Update toolbar title from fragment
        (activity as? MainActivity)?.updateToolbarTitle(getString(R.string.title_mypage), false, false)

        val carProfile = PreferencesUtil.getCarProfile()

        setCarProfile(carProfile)
        setFrameCarProfile(carProfile)

        mypageRepository.getUserDetails { result ->
            result.fold(
                onSuccess = { userDetails ->
                    activity?.runOnUiThread {
                        username = userDetails.username
                        nickname = userDetails.nickname
                        carNumber = userDetails.carNumber

                        setUsername(username)
                        setNickName(nickname)
                        setCarNumber(carNumber)

                        binding.tvMypageInfoTitle.visibility = View.VISIBLE
                        binding.tvMypageCarTitle.visibility = View.VISIBLE
                        binding.btnMypageUserProfileModify.visibility = View.VISIBLE
                        binding.layoutMypageLine1.visibility = View.VISIBLE
                        binding.layoutMypageFooter.visibility = View.VISIBLE
                    }
                },
                onFailure = { error ->
                    Log.e("MypageFragment", "Error fetching user details: $error")
                    activity?.runOnUiThread {
                        Toast.makeText(requireContext(), "Failed to fetch user details", Toast.LENGTH_SHORT).show()
                    }
                }
            )
        }

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

        modifyBtn.setOnClickListener {
            val intent = Intent(requireContext(), MypageModifyUserActivity::class.java)
            startActivity(intent)
        }

        addCarBtn.setOnClickListener {
            val intent = Intent(requireContext(), MypageAddCarActivity::class.java)
            startActivity(intent)
        }

        deleteCarBtn.setOnClickListener {
            showDeleteCarDialog(getString(R.string.title_delete_car_modal))
        }

        logoutBtn.setOnClickListener {
            showLogoutDialog(getString(R.string.title_logout_modal))
        }

        deleteUserBtn.setOnClickListener {
            showDeleteUserDialog(getString(R.string.title_delete_user_modal))
        }
    }

    override fun onResume() {
        super.onResume()
        refreshData()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun refreshData() {
        val carProfile = PreferencesUtil.getCarProfile()

        setCarProfile(carProfile)
        setFrameCarProfile(carProfile)

        mypageRepository.getUserDetails { result ->
            result.fold(
                onSuccess = { userDetails ->
                    activity?.runOnUiThread {
                        username = userDetails.username
                        nickname = userDetails.nickname
                        carNumber = userDetails.carNumber

                        setUsername(username)
                        setNickName(nickname)
                        setCarNumber(carNumber)

                        binding.tvMypageInfoTitle.visibility = View.VISIBLE
                        binding.tvMypageCarTitle.visibility = View.VISIBLE
                        binding.btnMypageUserProfileModify.visibility = View.VISIBLE
                        binding.layoutMypageLine1.visibility = View.VISIBLE
                        binding.layoutMypageFooter.visibility = View.VISIBLE
                    }
                },
                onFailure = { error ->
                    Log.e("MypageFragment", "Error fetching user details: $error")
                    activity?.runOnUiThread {
                        Toast.makeText(requireContext(), "Failed to fetch user details", Toast.LENGTH_SHORT).show()
                    }
                }
            )
        }
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
                        Log.e("MypageFragment", "Error logging out: $error")
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
            mypageRepository.patchUserDetails { result ->
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
                        Log.e("MypageFragment", "Error deleting user: $error")
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

    private fun showDeleteCarDialog(message: String) {
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
            mypageRepository.patchCarDetails { result ->
                result.fold(
                    onSuccess = {
                        activity?.let { ctx ->
                            dialog.dismiss()
                            mypageRepository.getUserDetails { result ->
                                result.fold(
                                    onSuccess = { userDetails ->
                                        activity?.runOnUiThread {
                                            username = userDetails.username
                                            nickname = userDetails.nickname
                                            carNumber = userDetails.carNumber

                                            setUsername(username)
                                            setNickName(nickname)
                                            setCarNumber(carNumber)

                                            // Make UI elements visible
                                            binding.tvMypageInfoTitle.visibility = View.VISIBLE
                                            binding.tvMypageCarTitle.visibility = View.VISIBLE
                                            binding.btnMypageUserProfileModify.visibility = View.VISIBLE
                                            binding.layoutMypageLine1.visibility = View.VISIBLE
                                            binding.layoutMypageFooter.visibility = View.VISIBLE
                                        }
                                    },
                                    onFailure = { error ->
                                        Log.e("MypageFragment", "Error fetching user details: $error")
                                        // Optional: Show an error message to the user
                                        activity?.runOnUiThread {
                                            // Show a Toast or AlertDialog with the error message
                                            Toast.makeText(requireContext(), "Failed to fetch user details", Toast.LENGTH_SHORT).show()
                                        }
                                    }
                                )
                            }
                        }
                    },
                    onFailure = { error ->
                        Log.e("MypageFragment", "Error deleting car: $error")
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

    private fun setCarProfile(carProfile: Int) {
        val imgCar1 = binding.imgMypageCar1
        val imgCar2 = binding.imgMypageCar2
        val imgCar3 = binding.imgMypageCar3
        val imgCar4 = binding.imgMypageCar4
        val imgCar5 = binding.imgMypageCar5

        val imageViews = listOf(imgCar1, imgCar2, imgCar3, imgCar4, imgCar5)
        for ((index, imageView) in imageViews.withIndex()) {
            imageView.visibility = if (index == carProfile - 1) View.VISIBLE else View.INVISIBLE
        }
    }

    private fun setFrameCarProfile(carProfile: Int) {
        val imgCar1 = binding.imgMypageInfoCar1
        val imgCar2 = binding.imgMypageInfoCar2
        val imgCar3 = binding.imgMypageInfoCar3
        val imgCar4 = binding.imgMypageInfoCar4
        val imgCar5 = binding.imgMypageInfoCar5

        val imageViews = listOf(imgCar1, imgCar2, imgCar3, imgCar4, imgCar5)
        for ((index, imageView) in imageViews.withIndex()) {
            imageView.visibility = if (index == carProfile - 1) View.VISIBLE else View.INVISIBLE
        }
    }

    private fun setNickName(nickname: String?) {
        val nicknameText = binding.tvMypageNickname
        nicknameText.text = nickname
        nicknameText.visibility = View.VISIBLE
    }

    private fun setUsername(username: String?) {
        val usernameText = binding.tvMypageUsername
        usernameText.text = username
        usernameText.visibility = View.VISIBLE

    }

    private fun setCarNumber(carNumber: String?) {
        val carNumberText = binding.tvMypageCarNumber
        carNumberText.text = carNumber

        if (carNumber == null) {
            val addBtn = binding.layoutMypageAddCarBtn
            val carFrame = binding.layoutMypageCarFrame
            addBtn.visibility = View.VISIBLE
            carFrame.visibility = View.INVISIBLE
        } else {
            val addBtn = binding.layoutMypageAddCarBtn
            val carFrame = binding.layoutMypageCarFrame
            addBtn.visibility = View.INVISIBLE
            carFrame.visibility = View.VISIBLE
        }
    }

}
