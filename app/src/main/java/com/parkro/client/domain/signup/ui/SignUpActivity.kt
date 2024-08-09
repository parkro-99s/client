package com.parkro.client.domain.signup.ui

import android.app.AlertDialog
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.text.Editable
import android.text.Layout
import android.text.TextWatcher
import android.util.Log
import android.util.TypedValue
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import com.google.gson.Gson
import com.parkro.client.*
import com.parkro.client.domain.login.ui.LoginActivity
import com.parkro.client.domain.signup.api.PostCarReq
import com.parkro.client.domain.signup.api.PostSignUpReq
import com.parkro.client.domain.signup.data.SignUpRepository
import org.json.JSONException
import org.json.JSONObject
import java.io.BufferedReader
import java.io.InputStreamReader
import java.io.OutputStreamWriter
import java.net.HttpURLConnection
import java.net.URL
import java.util.concurrent.Executors
import java.util.regex.Pattern
import kotlin.properties.Delegates
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.request.target.Target
import android.widget.RelativeLayout
/**
 * 회원 가입 액티비티
 *
 * @author 양재혁
 * @since 2024.08.01
 *
 * <pre>
 * 수정일자       수정자        수정내용
 * ------------ --------    ---------------------------
 * 2024.08.01   양재혁      최초 생성
 * </pre>
 */
class SignUpActivity : AppCompatActivity() {
    private val executor = Executors.newSingleThreadExecutor()
    private lateinit var usernameText: EditText
    private lateinit var passwordText: EditText
    private lateinit var checkPasswordText: EditText
    private lateinit var carNumberText: EditText
    private lateinit var nameText: EditText
    private lateinit var nicknameText: EditText
    private lateinit var phoneNumberText: EditText

    private lateinit var feIdError: TextView
    private lateinit var fePasswordError: TextView
    private lateinit var feCheckPasswordError: TextView
    private lateinit var feNickNameError: TextView
    private lateinit var fePhoneNumberError: TextView

    private lateinit var beIdError: TextView
    private lateinit var beDuplicatedCarError: TextView
    private lateinit var beInvalidCarError: TextView
    private lateinit var bePhoneNumberError: TextView

    private lateinit var serverError: TextView

    private var verifyIdLoading = -1
    private var verifyCarNumberLoading = -1

    private lateinit var verifyIdBtn: Button
    private lateinit var verifyCarBtn: Button
    private lateinit var signUpBtn: Button
    private lateinit var imgGif: ImageView

    private val signUpRepository = SignUpRepository()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_signup)
        initialSet()

        usernameText = findViewById(R.id.edt_signup_username)
        passwordText = findViewById(R.id.edt_signup_password)
        checkPasswordText = findViewById(R.id.edt_signup_check_password)
        carNumberText = findViewById(R.id.edt_signup_car_number)
        nameText = findViewById(R.id.edt_signup_name)
        nicknameText = findViewById(R.id.edt_signup_nickname)
        phoneNumberText = findViewById(R.id.edt_signup_phone_number)

        feIdError = findViewById(R.id.tv_signup_login_error_fe)
        fePasswordError = findViewById(R.id.tv_signup_password_error_fe)
        feCheckPasswordError = findViewById(R.id.tv_signup_check_password_error_fe)
        feNickNameError = findViewById(R.id.tv_signup_nickname_error_fe)
        fePhoneNumberError = findViewById(R.id.tv_signup_phone_number_error_fe)

        beIdError = findViewById(R.id.tv_signup_login_error_be)
        beDuplicatedCarError = findViewById(R.id.tv_signup_duplicated_car_number_error_be)
        bePhoneNumberError = findViewById(R.id.tv_signup_phone_number_error_be)
        beInvalidCarError = findViewById(R.id.tv_signup_invalid_car_number_error_be)
        serverError = findViewById(R.id.tv_signup_server_error)

        verifyIdBtn = findViewById(R.id.btn_signup_verify_username)
        verifyCarBtn = findViewById(R.id.btn_signup_verify_car_number)
        signUpBtn = findViewById(R.id.btn_signup)

        setupTextWatchers()
        usernameText.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
                val username = usernameText.text.toString()
                if (username.length == 0){
                    feIdError.visibility = TextView.GONE
                    setEditTextMarginTop(passwordText, 18)
                    verifyIdBtn.isEnabled = false
                }
                if (isValidId(username)) {
                    feIdError.visibility = TextView.GONE
                    setEditTextMarginTop(passwordText, 18)
                    verifyIdBtn.isEnabled = true
                } else {
                    feIdError.visibility = TextView.VISIBLE
                    setEditTextMarginTop(passwordText, 35)
                    verifyIdBtn.isEnabled = false
                }
            }

            override fun afterTextChanged(s: Editable) {
                beIdError.visibility = TextView.GONE
            }
        })

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
                    setEditTextMarginTop(carNumberText, 18)
                    setEditTextMarginTop(nameText, 18)
                    setButtonMarginTop(verifyCarBtn, 18)
                }
                else{
                    if (isValidCheckPassword(password, checkPassword)) {
                        feCheckPasswordError.visibility = TextView.GONE
                        setEditTextMarginTop(carNumberText, 18)
                        setEditTextMarginTop(nameText, 18)
                        setButtonMarginTop(verifyCarBtn, 18);
                    } else {
                        feCheckPasswordError.visibility = TextView.VISIBLE
                        setEditTextMarginTop(carNumberText, 35)
                        setEditTextMarginTop(nameText, 35)
                        setButtonMarginTop(verifyCarBtn, 35)
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
                    setEditTextMarginTop(carNumberText, 18)
                    setEditTextMarginTop(nameText, 18)
                    setButtonMarginTop(verifyCarBtn, 18);
                }
                else{
                    if (isValidCheckPassword(password, checkPassword)) {
                        feCheckPasswordError.visibility = TextView.GONE
                        setEditTextMarginTop(carNumberText, 18)
                        setEditTextMarginTop(nameText, 18)
                        setButtonMarginTop(verifyCarBtn, 18);
                    } else {
                        feCheckPasswordError.visibility = TextView.VISIBLE
                        setEditTextMarginTop(carNumberText, 35)
                        setEditTextMarginTop(nameText, 35)
                        setButtonMarginTop(verifyCarBtn, 35)
                    }
                }

            }

            override fun afterTextChanged(s: Editable) {}
        })

        carNumberText.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
                val name = nameText.text.toString()
                val carNum = carNumberText.text.toString()
                if(name.length == 0 && carNum.length == 0) verifyCarBtn.isEnabled = false
                else if(name.length > 0 && carNum.length > 0) verifyCarBtn.isEnabled = true
            }
            override fun afterTextChanged(s: Editable) {
                beInvalidCarError.visibility = TextView.GONE
                serverError.visibility = TextView.GONE
                beDuplicatedCarError.visibility = TextView.GONE
                setEditTextMarginTop(nicknameText, 18)
            }
        })

        nameText.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
                val name = nameText.text.toString()
                val carNum = carNumberText.text.toString()
                if(name.length == 0 && carNum.length == 0) verifyCarBtn.isEnabled = false
                else if(name.length > 0 && carNum.length > 0) verifyCarBtn.isEnabled = true

            }
            override fun afterTextChanged(s: Editable) {
                beInvalidCarError.visibility = TextView.GONE
                serverError.visibility = TextView.GONE
                beDuplicatedCarError.visibility = TextView.GONE
                setEditTextMarginTop(nicknameText, 18)
            }
        })

        verifyIdBtn.setOnClickListener({
            verifyIdLoading++
            verifyIdBtn.isEnabled = false
            usernameText.isEnabled = false
            val username = usernameText.text.toString().trim()
            signUpRepository.getUsername(username) { result ->
                result.fold(
                    onSuccess = { response ->
                        response?.let {
                            beIdError.visibility = TextView.GONE
                            verifyIdBtn.isEnabled = false
                            usernameText.isEnabled = false
                        }
                        verifyIdLoading--
                    },
                    onFailure = { error ->
                        verifyIdBtn.isEnabled = true
                        usernameText.isEnabled = true
                        runOnUiThread {
                            beIdError.visibility = TextView.VISIBLE
                            setEditTextMarginTop(passwordText, 35)

                        }
                        verifyIdLoading--
                    }
                )
            }

        })

        signUpBtn.setOnClickListener {
            signUpBtn.isEnabled=false
            val username = usernameText.text.toString()
            val password = passwordText.text.toString()
            val nickname = nicknameText.text.toString()
            val phoneNumber = phoneNumberText.text.toString()
            val carNumber =
                if (!carNumberText.isEnabled && verifyCarNumberLoading == -1 && !verifyCarBtn.isEnabled) {
                    carNumberText.text.toString()
                } else {
                    null
                }
            Log.d("verifyLoading","verifyLoading: $verifyCarNumberLoading")
            Log.d("SignUp", "Username: $username")
            Log.d("SignUp", "Password: $password")
            Log.d("SignUp", "Nickname: $nickname")
            Log.d("SignUp", "PhoneNumber: $phoneNumber")
            Log.d("SignUp", "CarNumber: $carNumber")
        if((carNumberText.isEnabled && nameText.isEnabled) || verifyCarNumberLoading == 0){
                showCustomDialog("차량이 등록되지 않았습니다. \n회원가입 하시겠습니까?")
            }
            else{
                signUpRepository.postSignUp(
                    PostSignUpReq(username, password, carNumber, nickname, phoneNumber)
                ) { result ->
                    Log.d("result", "result+$result ")
                    result.fold(
                        onSuccess = { response ->
                            // Navigate to LoginActivity on successful sign-up
                            response?.let {
                                Intent(this, LoginActivity::class.java).also {
                                    startActivity(it)
                                    finish()
                                }
                            }
                        },
                        onFailure = { error ->
                            signUpBtn.isEnabled=true
                            runOnUiThread {
                                handleSignUpError(error)
                            }
                        }
                    )
                }
            }

        }


        verifyCarBtn.setOnClickListener {
            verifyCarNumberLoading++

            imgGif.setVisibility(View.VISIBLE);
            imgGif.bringToFront();
            imgGif?.let {
                Glide.with(this)
                    .asGif()
                    .load(R.raw.loading_animation)
                    .diskCacheStrategy(DiskCacheStrategy.RESOURCE)
                    .into(imgGif!!)
            }
            verifyCarBtn.isEnabled=false
            nameText.isEnabled=false
            carNumberText.isEnabled=false
            val carNumber = carNumberText.text.toString().trim()
            val name = nameText.text.toString().trim()
            val carToken = BuildConfig.VERIFY_CAR_TOKEN
            val postCarReq = PostCarReq(carNumber, name)
            val queryUrl =
                "https://datahub-dev.scraping.co.kr/assist/common/carzen/CarAllInfoInquiry"
            executor.execute {
                try {
                    val url = URL(queryUrl)
                    val connection = url.openConnection() as HttpURLConnection
                    connection.requestMethod = "POST"
                    connection.setRequestProperty("Content-Type", "application/json; utf-8")
                    connection.setRequestProperty("Accept", "application/json")
                    connection.setRequestProperty("Authorization", carToken)
                    connection.doOutput = true

                    val gson = Gson()
                    val jsonInputString = gson.toJson(postCarReq)

                    connection.outputStream.use { os ->
                        val writer = OutputStreamWriter(os, "UTF-8")
                        writer.write(jsonInputString)
                        writer.flush()
                    }

                    val responseBody = BufferedReader(InputStreamReader(connection.inputStream, "UTF-8")).use { reader ->
                        reader.readText()
                    }

                    val jsonObject = JSONObject(responseBody)

                    val errCode = jsonObject.optString("errCode")

                    runOnUiThread {
                        if (errCode.equals("0000")) {
                            carNumberText.isEnabled = false
                            nameText.isEnabled = false
                            verifyCarBtn.isEnabled = false
                            beInvalidCarError.visibility = TextView.GONE
                            serverError.visibility = TextView.GONE
                        } else if(errCode.equals("6112") || errCode.equals("6114")){
                            verifyCarBtn.isEnabled=true
                            nameText.isEnabled=true
                            carNumberText.isEnabled=true
                            beInvalidCarError.visibility = TextView.VISIBLE
                            serverError.visibility = TextView.GONE
//                            setEditTextMarginTop(nameText, 35)
//                            setEditTextMarginTop(carNumberText, 35)
                            setEditTextMarginTop(nicknameText, 35)
                        } else{
                            verifyCarBtn.isEnabled=true
                            nameText.isEnabled=true
                            carNumberText.isEnabled=true
                            serverError.visibility = View.VISIBLE
                            beInvalidCarError.visibility = TextView.GONE
//                            setEditTextMarginTop(nameText, 35)
//                            setEditTextMarginTop(carNumberText, 35)
                            setEditTextMarginTop(nicknameText, 35)

                        }
                    }
                    verifyCarNumberLoading--
                    imgGif.visibility = View.INVISIBLE
                } catch (e: Exception) {
                    serverError.visibility = View.VISIBLE
                    runOnUiThread {
                        verifyCarBtn.isEnabled=true
                        nameText.isEnabled=true
                        carNumberText.isEnabled=true
//                        setEditTextMarginTop(nameText, 35)
//                        setEditTextMarginTop(carNumberText, 35)
                        setEditTextMarginTop(nicknameText, 35)
                    }
                    verifyCarNumberLoading--
                    imgGif.visibility = View.INVISIBLE
                }
            }
        }

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
                        setButtonMarginTop(signUpBtn, 18)
                    }
                    if (isValidPhoneNumber(phoneNumber)) {
                        fePhoneNumberError.visibility = TextView.GONE
                        setButtonMarginTop(signUpBtn, 18)
                } else {
                    fePhoneNumberError.visibility = TextView.VISIBLE
                    setButtonMarginTop(signUpBtn, 35)
                }
            }

            override fun afterTextChanged(s: Editable) {
                bePhoneNumberError.visibility = TextView.GONE
            }
        })


    }


    // 회원 가입 버튼 enable / disable
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

        usernameText.addTextChangedListener(textWatcher)
        passwordText.addTextChangedListener(textWatcher)
        checkPasswordText.addTextChangedListener(textWatcher)
        nicknameText.addTextChangedListener(textWatcher)
        phoneNumberText.addTextChangedListener(textWatcher)
        feIdError.addTextChangedListener(textWatcher)
        feCheckPasswordError.addTextChangedListener(textWatcher)
        fePasswordError.addTextChangedListener(textWatcher)
        feNickNameError.addTextChangedListener(textWatcher)
        fePhoneNumberError.addTextChangedListener(textWatcher)
        beIdError.addTextChangedListener(textWatcher)
        beInvalidCarError.addTextChangedListener(textWatcher)
        beDuplicatedCarError.addTextChangedListener(textWatcher)
        bePhoneNumberError.addTextChangedListener(textWatcher)
        serverError.addTextChangedListener(textWatcher)
    }

    private fun validateInputs() {
        val isUsernameValid = usernameText.text.length > 0
        Log.d("Validation", "Username valid: $isUsernameValid")

        val isPasswordValid = passwordText.text.length > 0
        Log.d("Validation", "Password valid: $isPasswordValid")

        val isCheckPasswordValid = checkPasswordText.text.length > 0
        Log.d("Validation", "CheckPassword valid: $isCheckPasswordValid")

        val isNicknameValid = nicknameText.text.length > 0
        Log.d("Validation", "Nickname valid: $isNicknameValid")

        val isPhoneNumberValid = phoneNumberText.text.length > 0
        Log.d("Validation", "PhoneNumber valid: $isPhoneNumberValid")

        // Check if the view is either GONE or INVISIBLE
        val isfeIdErrorValid = feIdError.visibility == View.GONE || feIdError.visibility == View.INVISIBLE
        Log.d("Validation", "feIdError valid: $isfeIdErrorValid")

        val isfePasswordErrorValid = fePasswordError.visibility == View.GONE || fePasswordError.visibility == View.INVISIBLE
        Log.d("Validation", "fePasswordError valid: $isfePasswordErrorValid")

        val isfeCheckPasswordErrorValid = feCheckPasswordError.visibility == View.GONE || feCheckPasswordError.visibility == View.INVISIBLE
        Log.d("Validation", "feCheckPasswordError valid: $isfeCheckPasswordErrorValid")

        val isfeNicknameErrorValid = feNickNameError.visibility == View.GONE || feNickNameError.visibility == View.INVISIBLE
        Log.d("Validation", "feNickNameError valid: $isfeNicknameErrorValid")

        val isfePhoneNumberErrorValid = fePhoneNumberError.visibility == View.GONE || fePhoneNumberError.visibility == View.INVISIBLE
        Log.d("Validation", "fePhoneNumberError valid: $isfePhoneNumberErrorValid")

        val isbeIdErrorValid = beIdError.visibility == View.GONE || beIdError.visibility == View.INVISIBLE
        Log.d("Validation", "beIdError valid: $isbeIdErrorValid")

        val isbeInvalidCarErrorValid = beInvalidCarError.visibility == View.GONE || beInvalidCarError.visibility == View.INVISIBLE
        Log.d("Validation", "beCarError valid: $isbeInvalidCarErrorValid")

        val isbeDuplicatedCarErrorValid = beDuplicatedCarError.visibility == View.GONE || beDuplicatedCarError.visibility == View.INVISIBLE

        val isServerErrorValid = serverError.visibility == View.GONE || serverError.visibility == View.INVISIBLE
        Log.d("Validation", "serverError valid: $isServerErrorValid")

        val isbePhoneNumberErrorValid = bePhoneNumberError.visibility == View.GONE || bePhoneNumberError.visibility == View.INVISIBLE

        val allFieldsValid = isUsernameValid &&
                isPasswordValid &&
                isCheckPasswordValid &&
//                isCarNumberValid &&
//                isNameValid &&
                isNicknameValid &&
                isPhoneNumberValid &&
//                isVerifyCarBtnValid &&
//                isVerifyIdBtnValid &&
                isfeIdErrorValid &&
                isfePasswordErrorValid &&
                isfeCheckPasswordErrorValid &&
                isfePhoneNumberErrorValid &&
                isfeNicknameErrorValid &&
                isbeIdErrorValid &&
//                isbeInvalidCarErrorValid &&
//                isbeDuplicatedCarErrorValid &&
                isbePhoneNumberErrorValid &&
                isServerErrorValid &&
                !verifyIdBtn.isEnabled

        signUpBtn.isEnabled = allFieldsValid
    }

    private fun isValidId(Id: String): Boolean {
        // 아이디 정규 표현식 (예: testtest5)
        val idPattern = "^(?=.*[a-zA-Z])(?=.*\\d)[a-zA-Z\\d]{6,12}$"
        return Pattern.matches(idPattern, Id)

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

    // Function to convert dp to px
    private fun dpToPx(dp: Int): Int {
        return TypedValue.applyDimension(
            TypedValue.COMPLEX_UNIT_DIP,
            dp.toFloat(),
            resources.displayMetrics
        ).toInt()
    }

    private fun handleSignUpError(error: Throwable) {
        try {
            val errorString = error.toString()

            val jsonString = errorString.substringAfter("Body: ").trim()

            Log.d("errorbody", "errorbody: $jsonString")

            if (!jsonString.isNullOrEmpty()) {
                val jsonError = JSONObject(jsonString)
                val errorCode = jsonError.optString("errorCode")
                when (errorCode) {
                    "PHONE_NUMBER_ALREADY_EXISTS" -> {
                        bePhoneNumberError.apply {
                            visibility = TextView.VISIBLE
                        }
                        setButtonMarginTop(signUpBtn, 35)
                    }
                    "FIND_DUPLICATED_CARNUMBER" -> {
                        beDuplicatedCarError.apply {
                            visibility = TextView.VISIBLE

                        }
                        carNumberText.isEnabled = true
                        nameText.isEnabled = true
                        verifyCarBtn.isEnabled = true
                        setEditTextMarginTop(nicknameText, 35)

                    }
                    "CAR_NUMBER_AND_PHONE_NUMBER_ALREADY_EXISTS" -> {
                        bePhoneNumberError.apply {
                            visibility = TextView.VISIBLE
                        }
                        setButtonMarginTop(signUpBtn, 35)
                        beDuplicatedCarError.apply {
                            visibility = TextView.VISIBLE
                        }
                        setEditTextMarginTop(nicknameText, 35)
                    }
                    else -> {
                        Log.d("UnknownErrorCode", "Unknown error code: $errorCode")
                    }
                }
            } else {
                Log.e("Error", "Error body is null or empty")
            }
        } catch (e: JSONException) {
            Log.e("JSONError", "Failed to parse JSON error: ${e.message}", e)
        } catch (e: Exception) {
            Log.e("GeneralError", "An unexpected error occurred: ${e.message}", e)
        }
    }
    private fun showCustomDialog(message: String) {
        val dialogView = LayoutInflater.from(this).inflate(R.layout.custom_dialog_two_btns, null)
        val messageTextView = dialogView.findViewById<TextView>(R.id.text_dialog_message)
        val confirmButton = dialogView.findViewById<ImageButton>(R.id.btn_dialog_check)
        val cancelButton = dialogView.findViewById<ImageButton>(R.id.btn_dialog_cancel)

        messageTextView.text = message

        val dialog = AlertDialog.Builder(this)
            .setView(dialogView)
            .setCancelable(false)
            .create()

        confirmButton.setOnClickListener {
            signUpRepository.postSignUp(
                PostSignUpReq(usernameText.text.toString(), passwordText.text.toString(), null, nicknameText.text.toString(), phoneNumberText.text.toString())
            ) { result ->
                Log.d("result", "result+$result ")
                result.fold(
                    onSuccess = { response ->
                        // Navigate to LoginActivity on successful sign-up
                        response?.let {
                            Intent(this, LoginActivity::class.java).also {
                                startActivity(it)
                                finish()
                            }
                        }
                    },
                    onFailure = { error ->
                        runOnUiThread {
                            handleSignUpError(error)
                        }
                        signUpBtn.isEnabled=true
                        dialog.dismiss()
                    }
                )
            }
        }

        cancelButton.setOnClickListener {
            signUpBtn.isEnabled=true
            dialog.dismiss()
        }

        dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        dialog.show()

        dialog.window?.setLayout(
            (resources.displayMetrics.widthPixels * 0.8).toInt(),
            ViewGroup.LayoutParams.WRAP_CONTENT
        )
    }


    private fun initialSet() {
        imgGif = findViewById(R.id.img_signup_gif) as ImageView
    }
}
