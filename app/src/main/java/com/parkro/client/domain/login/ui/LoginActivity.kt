package com.parkro.client.domain.login.ui

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.util.TypedValue
import android.view.ViewGroup
import android.view.animation.Animation
import androidx.appcompat.app.AppCompatActivity
import com.parkro.client.domain.admin.ui.AdminActivity
import com.parkro.client.MainActivity
import com.parkro.client.util.PreferencesUtil
import com.parkro.client.domain.login.api.PostLoginReq
import com.parkro.client.domain.login.data.LoginRepository
import com.parkro.client.domain.signup.ui.SignUpActivity
import org.json.JSONObject
import java.util.Base64
import android.view.animation.AnimationUtils
import android.widget.*
/**
 * 로그인 액티비티
 *
 * @author 양재혁
 * @since 2024.07.25
 *
 * <pre>
 * 수정일자       수정자        수정내용
 * ------------ --------    ---------------------------
 * 2024.07.31   양재혁      최초 생성
 * </pre>
 */
class LoginActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(com.parkro.client.R.layout.activity_login)
        PreferencesUtil.init(applicationContext)

        val usernameText: EditText = findViewById(com.parkro.client.R.id.edt_login_username)
        val passwordText: EditText = findViewById(com.parkro.client.R.id.edt_login_password)
        val submitButton: Button = findViewById(com.parkro.client.R.id.btn_login)
        val signupButton: TextView = findViewById(com.parkro.client.R.id.tv_signup)
        val errorText: TextView = findViewById(com.parkro.client.R.id.tv_login_error)
        val car: ImageView = findViewById(com.parkro.client.R.id.img_login_car_shadow)
        val checkbox: CheckBox = findViewById(com.parkro.client.R.id.cb_login)
        val loginRepository = LoginRepository()

        val storedUsername = PreferencesUtil.getUsername(null)
        val storedPassword = PreferencesUtil.getPassword(null)
        if (storedUsername != null && storedPassword != null) {
            usernameText.setText(storedUsername)
            passwordText.setText(storedPassword)
            checkbox.isChecked = true
            loginChecked(storedUsername, storedPassword, car, errorText, submitButton, loginRepository)
        }

        submitButton.setOnClickListener {
            val username = usernameText.text.toString().trim()
            val password = passwordText.text.toString().trim()
            if(checkbox.isChecked) loginChecked(username, password, car, errorText, submitButton, loginRepository)
            else loginUnChecked(username, password, car, errorText, submitButton, loginRepository)
        }

        signupButton.setOnClickListener {
            val intent = Intent(this, SignUpActivity::class.java)
            startActivity(intent)
        }
    }

    private fun loginChecked(username: String, password: String, car: ImageView, errorText: TextView, submitButton: Button, loginRepository: LoginRepository) {
        submitButton.isEnabled = false
        val loginDTO = PostLoginReq(username, password)
        loginRepository.postLogin(loginDTO) { result ->
            result.fold(
                onSuccess = { response ->
                    response?.let {
                        PreferencesUtil.setAccessToken(it.token)
                        PreferencesUtil.setPassword(password)
                        PreferencesUtil.setUsername(it.username)
                        PreferencesUtil.setCarProfile(it.carProfile)
                        val payload = decodeJWT(it.token)
                        val rolesList = extractRolesFromPayload(payload)
                        if (rolesList.contains("ROLE_ADMIN")) {
                            animateCarAndNavigate(car, AdminActivity::class.java)
                        } else {
                            animateCarAndNavigate(car, MainActivity::class.java)
                        }
                    }
                },
                onFailure = { error ->
                    val animCarOut: Animation = AnimationUtils.loadAnimation(application, com.parkro.client.R.anim.anim_car_out)
                    car.startAnimation(animCarOut)
                    animCarOut.setAnimationListener(object : Animation.AnimationListener {
                        override fun onAnimationStart(animation: Animation?) {}
                        override fun onAnimationEnd(animation: Animation?) {
                            submitButton.isEnabled = true
                            runOnUiThread {
                                errorText.visibility = TextView.VISIBLE
                            }
                        }
                        override fun onAnimationRepeat(animation: Animation?) {}
                    })
                }
            )
        }
    }

    private fun loginUnChecked(username: String, password: String, car: ImageView, errorText: TextView, submitButton: Button, loginRepository: LoginRepository) {
        submitButton.isEnabled = false
        val loginDTO = PostLoginReq(username, password)
        loginRepository.postLogin(loginDTO) { result ->
            result.fold(
                onSuccess = { response ->
                    response?.let {
                        PreferencesUtil.setAccessToken(it.token)
                        PreferencesUtil.setUsername(it.username)
                        PreferencesUtil.setCarProfile(it.carProfile)
                        val payload = decodeJWT(it.token)
                        val rolesList = extractRolesFromPayload(payload)
                        if (rolesList.contains("ROLE_ADMIN")) {
                            animateCarAndNavigate(car, AdminActivity::class.java)
                        } else {
                            animateCarAndNavigate(car, MainActivity::class.java)
                        }
                    }
                },
                onFailure = { error ->
                    val animCarOut: Animation = AnimationUtils.loadAnimation(application, com.parkro.client.R.anim.anim_car_out)
                    car.startAnimation(animCarOut)
                    animCarOut.setAnimationListener(object : Animation.AnimationListener {
                        override fun onAnimationStart(animation: Animation?) {}
                        override fun onAnimationEnd(animation: Animation?) {
                            submitButton.isEnabled = true
                            runOnUiThread {
                                errorText.visibility = TextView.VISIBLE
                            }
                        }
                        override fun onAnimationRepeat(animation: Animation?) {}
                    })
                }
            )
        }
    }

    private fun animateCarAndNavigate(car: ImageView, destination: Class<*>) {
        val animCarOut: Animation = AnimationUtils.loadAnimation(application, com.parkro.client.R.anim.anim_car_out)
        car.startAnimation(animCarOut)
        animCarOut.setAnimationListener(object : Animation.AnimationListener {
            override fun onAnimationStart(animation: Animation?) {}
            override fun onAnimationEnd(animation: Animation?) {
                val intent = Intent(this@LoginActivity, destination)
                startActivity(intent)
                finish()
            }
            override fun onAnimationRepeat(animation: Animation?) {}
        })
    }

    private fun decodeJWT(token: String): String {
        val parts = token.split(".")
        if (parts.size != 3) {
            throw IllegalArgumentException("Invalid JWT token")
        }
        return String(Base64.getUrlDecoder().decode(parts[1]))
    }

    private fun extractRolesFromPayload(payload: String): List<String> {
        val jsonObject = JSONObject(payload)
        val rolesArray = jsonObject.getJSONArray("roles")
        val rolesList = mutableListOf<String>()
        for (i in 0 until rolesArray.length()) {
            rolesList.add(rolesArray.getString(i))
        }
        return rolesList
    }
}
