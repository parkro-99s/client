package com.parkro.client.domain.login.ui

import android.content.Intent
import android.os.Bundle
import android.util.TypedValue
import android.view.ViewGroup
import android.view.animation.Animation
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.marginTop
import com.parkro.client.AdminActivity
import com.parkro.client.MainActivity
import com.parkro.client.R
import com.parkro.client.Utils
import com.parkro.client.domain.login.api.PostLoginReq
import com.parkro.client.domain.login.data.LoginRepository
import com.parkro.client.domain.signup.ui.SignUpActivity
import org.json.JSONObject
import java.util.Base64
import android.view.animation.AnimationUtils
import android.widget.*


class LoginActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(com.parkro.client.R.layout.activity_login)
        Utils.init(applicationContext)
        val usernameText: EditText = findViewById(com.parkro.client.R.id.edittext_username)
        val passwordText: EditText = findViewById(com.parkro.client.R.id.edittext_password)
        val submitButton: Button = findViewById(com.parkro.client.R.id.btn_login)
        val signUpButton: TextView = findViewById(com.parkro.client.R.id.sign_up)
        val loginRepository = LoginRepository()
        val errorText: TextView = findViewById(com.parkro.client.R.id.textview_error)
        val car: ImageView = findViewById(com.parkro.client.R.id.car_shadow)

        submitButton.setOnClickListener {
            val username = usernameText.text.toString().trim()
            val password = passwordText.text.toString().trim()

            val loginDTO = PostLoginReq(username, password)
            loginRepository.postLoginInfo(loginDTO) { result ->
                result.fold(
                    onSuccess = { response ->
                        response?.let {
                            Utils.setAccessToken(it.token)
                            Utils.setUsername(it.username)

                            val payload = decodeJWT(it.token)
                            val rolesList = extractRolesFromPayload(payload) // Extract roles as a list of strings
                            if (rolesList.contains("ROLE_ADMIN")) {
                                val intent = Intent(this, AdminActivity::class.java)
                                startActivity(intent)
                            } else {

                                val animCarOut: Animation =
                                    AnimationUtils.loadAnimation(application, com.parkro.client.R.anim.anim_car_out)
                                car.startAnimation(animCarOut)
                                animCarOut.setAnimationListener(object : Animation.AnimationListener {
                                    override fun onAnimationStart(animation: Animation?) {
                                    }

                                    override fun onAnimationEnd(animation: Animation?) {
                                        val intent = Intent(this@LoginActivity, MainActivity::class.java)
                                        startActivity(intent)
                                    }

                                    override fun onAnimationRepeat(animation: Animation?) {
                                    }
                                })

                                car.startAnimation(animCarOut)
                            }
                        }
                    },
                    onFailure = { error ->
                        runOnUiThread {
                            errorText.visibility = TextView.VISIBLE
                            setButtonMarginTop(submitButton, 60)
                        }
                    }
                )
            }
        }

        signUpButton.setOnClickListener {
            val intent = Intent(this, SignUpActivity::class.java)
            startActivity(intent)
        }
    }

    private fun decodeJWT(token: String): String {
        val parts = token.split(".")
        if (parts.size != 3) {
            throw IllegalArgumentException("Invalid JWT token")
        }
        val payload = String(Base64.getUrlDecoder().decode(parts[1]))
        return payload
    }

    fun extractRolesFromPayload(payload: String): List<String> {
        val jsonObject = JSONObject(payload)

        val rolesArray = jsonObject.getJSONArray("roles")

        val rolesList = mutableListOf<String>()
        for (i in 0 until rolesArray.length()) {
            rolesList.add(rolesArray.getString(i))
        }

        return rolesList
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

}
