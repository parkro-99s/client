package com.parkro.client.domain.login.ui

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.parkro.client.AdminActivity
import com.parkro.client.MainActivity
import com.parkro.client.R
import com.parkro.client.Utils
import com.parkro.client.domain.example.api.PostLoginReq
import com.parkro.client.domain.example.data.LoginRepository
import org.json.JSONObject
import java.util.Base64

class LoginActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)
        Utils.init(applicationContext)
        val userNameText: EditText = findViewById(R.id.edittext_username)
        val passwordText: EditText = findViewById(R.id.edittext_password)
        val submitButton: Button = findViewById(R.id.btn_logIn)
        val loginRepository = LoginRepository()
        val errorText: TextView = findViewById(R.id.textview_error)

        submitButton.setOnClickListener {
            val username = userNameText.text.toString().trim()
            val password = passwordText.text.toString().trim()

            if (username.isEmpty() || password.isEmpty()) {
                Toast.makeText(this, "Username and password cannot be empty", Toast.LENGTH_LONG).show()
                return@setOnClickListener
            }

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
                                val intent = Intent(this, MainActivity::class.java)
                                startActivity(intent)
                            }
                            finish()
                        }
                    },
                    onFailure = { error ->
                        runOnUiThread {
                            errorText.visibility = TextView.VISIBLE
                        }
                    }
                )
            }
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


}
