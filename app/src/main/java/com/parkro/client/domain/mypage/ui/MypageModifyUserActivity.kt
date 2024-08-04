package com.parkro.client.domain.mypage.ui

import android.app.AlertDialog
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.util.TypedValue
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import com.parkro.client.R
import com.parkro.client.domain.login.ui.LoginActivity
import com.parkro.client.domain.mypage.api.PutModifiedUserDetailsReq
import com.parkro.client.domain.mypage.data.MypageRepository
import com.parkro.client.domain.signup.api.PostSignUpReq
import com.parkro.client.util.PreferencesUtil
import org.json.JSONException
import org.json.JSONObject
import java.util.regex.Pattern

class MypageModifyUserActivity : AppCompatActivity() {
    private lateinit var passwordText: EditText
    private lateinit var checkPasswordText: EditText
    private lateinit var nicknameText: EditText
    private lateinit var phoneNumberText: EditText

    private lateinit var fePasswordError: TextView
    private lateinit var feCheckPasswordError: TextView
    private lateinit var feNickNameError: TextView
    private lateinit var fePhoneNumberError: TextView

    private lateinit var bePhoneNumberError: TextView

    private lateinit var modifiedBtn: Button


    private val mypageRepository = MypageRepository()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_mypage_modify_user)

        var carProfile = PreferencesUtil.getCarProfile()

        val checkedCar1:ImageView = findViewById(R.id.img_modify_user_checked_car_1)
        val checkedCar2:ImageView = findViewById(R.id.img_modify_user_checked_car_2)
        val checkedCar3:ImageView = findViewById(R.id.img_modify_user_checked_car_3)
        val checkedCar4:ImageView = findViewById(R.id.img_modify_user_checked_car_4)
        val checkedCar5:ImageView = findViewById(R.id.img_modify_user_checked_car_5)
        val allCars = listOf(checkedCar1, checkedCar2, checkedCar3, checkedCar4, checkedCar5)
        allCars.forEach { it.visibility = View.INVISIBLE }

        when (carProfile) {
            1 -> checkedCar1.visibility = View.VISIBLE
            2 -> checkedCar2.visibility = View.VISIBLE
            3 -> checkedCar3.visibility = View.VISIBLE
            4 -> checkedCar4.visibility = View.VISIBLE
            5 -> checkedCar5.visibility = View.VISIBLE
        }

        val car1: ImageView = findViewById(R.id.img_modify_user_car_1)
        val car2: ImageView = findViewById(R.id.img_modify_user_car_2)
        val car3: ImageView = findViewById(R.id.img_modify_user_car_3)
        val car4: ImageView = findViewById(R.id.img_modify_user_car_4)
        val car5: ImageView = findViewById(R.id.img_modify_user_car_5)

        val allCheckedCars = listOf(checkedCar1, checkedCar2, checkedCar3, checkedCar4, checkedCar5)

        fun setCheckedCarVisibility(index: Int) {
            allCheckedCars.forEachIndexed { i, imageView ->
                imageView.visibility = if (i == index) View.VISIBLE else View.INVISIBLE
                PreferencesUtil.setCarProfile(index + 1)
            }
        }

        car1.setOnClickListener { setCheckedCarVisibility(0) }
        car2.setOnClickListener { setCheckedCarVisibility(1) }
        car3.setOnClickListener { setCheckedCarVisibility(2) }
        car4.setOnClickListener { setCheckedCarVisibility(3) }
        car5.setOnClickListener { setCheckedCarVisibility(4) }

        passwordText = findViewById(R.id.edt_modify_user_password)
        checkPasswordText = findViewById(R.id.edt_modify_user_check_password)
        nicknameText = findViewById(R.id.edt_modify_user_nickname)
        phoneNumberText = findViewById(R.id.edt_modify_user_phone_number)

        fePasswordError = findViewById(R.id.tv_modify_user_password_error_fe)
        feCheckPasswordError = findViewById(R.id.tv_modify_user_check_password_error_fe)
        feNickNameError = findViewById(R.id.tv_modify_user_nickname_error_fe)
        fePhoneNumberError = findViewById(R.id.tv_modify_user_phone_number_error_fe)

        bePhoneNumberError = findViewById(R.id.tv_modify_user_phone_number_error_be)

        modifiedBtn = findViewById(R.id.btn_modify_user)


        setupTextWatchers()

        passwordText.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
                val password = passwordText.text.toString()
                val checkPassword = checkPasswordText.text.toString()
                if(password.length == 0){
                    fePasswordError.visibility = TextView.GONE
                    setEditTextMarginTop(checkPasswordText, 18)
                }
                if(checkPassword.length == 0){
                    feCheckPasswordError.visibility = TextView.GONE
                    setEditTextMarginTop(checkPasswordText, 18)
                }
                else{
                    if (isValidCheckPassword(password, checkPassword)) {
                        feCheckPasswordError.visibility = TextView.GONE
                        setEditTextMarginTop(checkPasswordText, 18)
                    } else {
                        feCheckPasswordError.visibility = TextView.VISIBLE
                        setEditTextMarginTop(checkPasswordText, 35)
                    }
                }
                if (isValidPassword(password)) {
                    fePasswordError.visibility = TextView.GONE
                    setEditTextMarginTop(checkPasswordText, 18)
                } else {
                    fePasswordError.visibility = TextView.VISIBLE
                    setEditTextMarginTop(checkPasswordText, 35)
                }
            }

            override fun afterTextChanged(s: Editable) {}
        })

        checkPasswordText.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
                val password = passwordText.text.toString()
                val checkPassword = checkPasswordText.text.toString()
                if(checkPassword.length == 0){
                    feCheckPasswordError.visibility = TextView.GONE
                    setEditTextMarginTop(nicknameText, 18)
                }
                else{
                    if (isValidCheckPassword(password, checkPassword)) {
                        feCheckPasswordError.visibility = TextView.GONE
                        setEditTextMarginTop(nicknameText, 18)
                    } else {
                        feCheckPasswordError.visibility = TextView.VISIBLE
                        setEditTextMarginTop(nicknameText, 35)
                    }
                }

            }

            override fun afterTextChanged(s: Editable) {}
        })

        nicknameText.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
                val nickname = nicknameText.text.toString()
                if(nickname.length == 0){
                    feNickNameError.visibility = TextView.VISIBLE
                    setEditTextMarginTop(phoneNumberText, 35)
                }
                else{
                    feNickNameError.visibility = TextView.GONE
                    setEditTextMarginTop(phoneNumberText, 18)
                }
            }

            override fun afterTextChanged(s: Editable) {}
        })

        phoneNumberText.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
                val phoneNumber = phoneNumberText.text.toString()
                if(phoneNumber.length == 0){
                    fePhoneNumberError.visibility = TextView.GONE
                    setButtonMarginTop(modifiedBtn, 18)
                }
                if (isValidPhoneNumber(phoneNumber)) {
                    fePhoneNumberError.visibility = TextView.GONE
                    setButtonMarginTop(modifiedBtn, 18)
                } else {
                    fePhoneNumberError.visibility = TextView.VISIBLE
                    setButtonMarginTop(modifiedBtn, 35)
                }
            }

            override fun afterTextChanged(s: Editable) {
                bePhoneNumberError.visibility = TextView.GONE
            }
        })

        modifiedBtn.setOnClickListener {
            modifiedBtn.isEnabled=false
            val username = PreferencesUtil.getUsername(null)

            val password = passwordText.text.toString()
            val nickname = nicknameText.text.toString()
            val phoneNumber = phoneNumberText.text.toString()

            carProfile = PreferencesUtil.getCarProfile()

            Log.d("username","username+$username")
            Log.d("username","password+$password")
            Log.d("username","nickname+$nickname")
            Log.d("username","phoneNumber+$phoneNumber")
            Log.d("username","carProfile+$carProfile")
            val currentCarProfile = PreferencesUtil.getCarProfile()
            if (username != null) {
                mypageRepository.putModifiedUserDetails(username, password, nickname, phoneNumber, currentCarProfile) { result ->
                    Log.d("result", "result+$result ")
                    result.fold(
                        onSuccess = { response ->
                            // Navigate to LoginActivity on successful sign-up
                            response?.let {
                                showCustomDialog("회원 정보가 수정되었습니다.")
                            }
                        },
                        onFailure = { error ->
                            modifiedBtn.isEnabled=true
                            runOnUiThread {
                                handleSignUpError(error)
                            }
                        }
                    )
                }
            }
        }

    }

    private fun showCustomDialog(message: String) {
        // Inflate the custom layout for the dialog
        val dialogView = LayoutInflater.from(this).inflate(R.layout.custom_dialog, null)
        val messageTextView = dialogView.findViewById<TextView>(R.id.text_dialog_message)
        val confirmButton = dialogView.findViewById<ImageButton>(R.id.btn_dialog_check)

        // Set the message text
        messageTextView.text = message

        // Build the dialog
        val dialog = AlertDialog.Builder(this)
            .setView(dialogView)
            .setCancelable(false)
            .create()

        // Set up the confirm button click listener
        confirmButton.setOnClickListener {
            dialog.dismiss()
            onBackPressed()
        }


        dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        dialog.show()

        // Customize the dialog window
        dialog.window?.setLayout(
            (resources.displayMetrics.widthPixels * 0.8).toInt(),
            ViewGroup.LayoutParams.WRAP_CONTENT
        )

        // Show the dialog
        dialog.show()
    }



    private fun setupTextWatchers() {
        val textWatcher = object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
                validateInputs()
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                validateInputs()
            }

            override fun afterTextChanged(s: Editable?) {
                validateInputs()
            }
        }

        passwordText.addTextChangedListener(textWatcher)
        checkPasswordText.addTextChangedListener(textWatcher)
        nicknameText.addTextChangedListener(textWatcher)
        phoneNumberText.addTextChangedListener(textWatcher)
        feCheckPasswordError.addTextChangedListener(textWatcher)
        fePasswordError.addTextChangedListener(textWatcher)
        feNickNameError.addTextChangedListener(textWatcher)
        fePhoneNumberError.addTextChangedListener(textWatcher)
        bePhoneNumberError.addTextChangedListener(textWatcher)
    }

    private fun validateInputs() {

        val isPasswordValid = passwordText.text.length > 0
        Log.d("Validation", "Password valid: $isPasswordValid")

        val isCheckPasswordValid = checkPasswordText.text.length > 0
        Log.d("Validation", "CheckPassword valid: $isCheckPasswordValid")

        val isNicknameValid = nicknameText.text.length > 0
        Log.d("Validation", "Nickname valid: $isNicknameValid")

        val isPhoneNumberValid = phoneNumberText.text.length > 0
        Log.d("Validation", "PhoneNumber valid: $isPhoneNumberValid")

        val isfePasswordErrorValid = fePasswordError.visibility == View.GONE || fePasswordError.visibility == View.INVISIBLE
        Log.d("Validation", "fePasswordError valid: $isfePasswordErrorValid")

        val isfeCheckPasswordErrorValid = feCheckPasswordError.visibility == View.GONE || feCheckPasswordError.visibility == View.INVISIBLE
        Log.d("Validation", "feCheckPasswordError valid: $isfeCheckPasswordErrorValid")

        val isfeNicknameErrorValid = feNickNameError.visibility == View.GONE || feNickNameError.visibility == View.INVISIBLE
        Log.d("Validation", "feNickNameError valid: $isfeNicknameErrorValid")

        val isfePhoneNumberErrorValid = fePhoneNumberError.visibility == View.GONE || fePhoneNumberError.visibility == View.INVISIBLE
        Log.d("Validation", "fePhoneNumberError valid: $isfePhoneNumberErrorValid")

        val isbePhoneNumberErrorValid = bePhoneNumberError.visibility == View.GONE || bePhoneNumberError.visibility == View.INVISIBLE

        val allFieldsValid =
                isPasswordValid &&
                isCheckPasswordValid &&
//                isCarNumberValid &&
//                isNameValid &&
                isNicknameValid &&
                isPhoneNumberValid &&
//                isVerifyCarBtnValid &&
//                isVerifyIdBtnValid &&
                isfePasswordErrorValid &&
                isfeCheckPasswordErrorValid &&
                isfePhoneNumberErrorValid &&
                isfeNicknameErrorValid &&
//                isbeInvalidCarErrorValid &&
//                isbeDuplicatedCarErrorValid &&
                isbePhoneNumberErrorValid

        modifiedBtn.isEnabled = allFieldsValid
    }


    private fun isValidPassword(password: String): Boolean {
        // 비밀번호 정규 표현식 (예: aaaaaaaA1!)
        val passwordPattern = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[@$!%*?&])[A-Za-z\\d@$!%*?&]{8,12}$"
        return Pattern.matches(passwordPattern, password)
    }

    private fun isValidCheckPassword(password: String, checkPassword: String): Boolean {
        // 비밀번호 확인 정규 표현식
        return password == checkPassword
    }

    private fun isValidPhoneNumber(phoneNumber: String): Boolean {
        // 전화번호 정규 표현식 (예: 010-1234-5678 형식)
        val phoneNumberPattern = "^(010|011|016|017|018|019)-\\d{3,4}-\\d{4}$"
        return Pattern.matches(phoneNumberPattern, phoneNumber)
    }

    private fun setEditTextMarginTop(editText: EditText, marginDp: Int) {
        val layoutParams = editText.layoutParams as ViewGroup.MarginLayoutParams
        val marginPx = dpToPx(marginDp)
        layoutParams.topMargin = marginPx
        editText.layoutParams = layoutParams
    }

    private fun setButtonMarginTop(button: Button, marginDp: Int) {
        val layoutParams = button.layoutParams as ViewGroup.MarginLayoutParams
        val marginPx = dpToPx(marginDp)
        layoutParams.topMargin = marginPx
        button.layoutParams = layoutParams
    }

    private fun handleSignUpError(error: Throwable) {
        try {
            // If an error occurs, make the TextView visible and adjust the button margin
            bePhoneNumberError.apply {
                visibility = TextView.VISIBLE
                // Assuming `setButtonMarginTop` is a custom method you have defined
                setButtonMarginTop(modifiedBtn, 35)
            }
        } catch (e: JSONException) {
            Log.e("JSONError", "Failed to parse JSON error: ${e.message}", e)
        } catch (e: Exception) {
            Log.e("GeneralError", "An unexpected error occurred: ${e.message}", e)
        }
    }

    private fun dpToPx(dp: Int): Int {
        return TypedValue.applyDimension(
            TypedValue.COMPLEX_UNIT_DIP,
            dp.toFloat(),
            resources.displayMetrics
        ).toInt()
    }

}