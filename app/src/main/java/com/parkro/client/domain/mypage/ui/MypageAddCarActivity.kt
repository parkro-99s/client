package com.parkro.client.domain.mypage.ui

import android.app.AlertDialog
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.google.gson.Gson
import com.parkro.client.BuildConfig
import com.parkro.client.R
import com.parkro.client.domain.mypage.data.MypageRepository
import com.parkro.client.domain.signup.api.PostCarReq
import org.json.JSONObject
import java.io.BufferedReader
import java.io.InputStreamReader
import java.io.OutputStreamWriter
import java.net.HttpURLConnection
import java.net.URL
import java.util.concurrent.Executors

class MypageAddCarActivity : AppCompatActivity() {
    private val executor = Executors.newSingleThreadExecutor()
    private val mypageRepository = MypageRepository()
    private lateinit var registerButton: Button
    private lateinit var imgGif: ImageView
    private var memberId = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_mypage_add_car)
        initialSet()
        registerButton = findViewById(R.id.btn_mypage_add_car)

        val toolbarLogo: ImageView = findViewById(R.id.toolbar_logo)
        toolbarLogo.visibility = View.GONE
        updateToolbarTitle("차량 등록", showBackBtn = true)

        val checkBox: CheckBox = findViewById(R.id.cb_mypage_add_car)
        mypageRepository.getUserDetails { result ->
            result.fold(
                onSuccess = { userDetails ->
                    runOnUiThread {
                        Log.d("memberId","memberId+${userDetails.memberId}")
                        memberId = userDetails.memberId
                        Log.d("User Details", "Member ID: $memberId")
                    }
                },
                onFailure = { error ->
                    runOnUiThread {
                        Log.e("User Details Error", error.toString())
                    }
                }
            )
        }
        checkBox.setOnCheckedChangeListener { _, isChecked ->
            registerButton.isEnabled = isChecked
        }

        val carNumberText: TextView = findViewById(R.id.edt_mypage_add_car_car_number)
        val ownerNameText: TextView = findViewById(R.id.edt_mypage_add_car_car_owner)
        registerButton.setOnClickListener {
            imgGif.setVisibility(View.VISIBLE);
            imgGif.bringToFront();
            imgGif?.let {
                Glide.with(this)
                    .asGif()
                    .load(R.raw.loading_animation)
                    .diskCacheStrategy(DiskCacheStrategy.RESOURCE)
                    .into(imgGif!!)
            }
            registerButton.isEnabled = false
            val carNumber = carNumberText.text.toString().trim()
            val name = ownerNameText.text.toString().trim()
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

                    Log.d("HTTP Request", "Opening connection to $queryUrl with POST method")

                    val gson = Gson()
                    val jsonInputString = gson.toJson(postCarReq)
                    connection.outputStream.use { os ->
                        val writer = OutputStreamWriter(os, "UTF-8")
                        writer.write(jsonInputString)
                        writer.flush()
                    }

                    Log.d("HTTP Request", "Request payload: $jsonInputString")

                    val responseBody = BufferedReader(
                        InputStreamReader(
                            connection.inputStream,
                            "UTF-8"
                        )
                    ).use { reader ->
                        reader.readText()
                    }

                    Log.d("HTTP Response", "Response body: $responseBody")

                    val jsonObject = JSONObject(responseBody)

                    val errCode = jsonObject.optString("errCode")
                    runOnUiThread {
                        Log.d("HTTP Response", "Error code: $errCode")
                        if (errCode == "0000") {
                            carNumberText.isEnabled = false
                            ownerNameText.isEnabled = false
                            val carnum = carNumberText.text.toString()
                            mypageRepository.postCarDetails(memberId, carnum) { result ->
                                when {
                                    result.isSuccess -> {
                                        Log.d("Post Car Details", "Car registration succeeded.")
                                        showCustomDialog2("차량 등록에 성공했습니다.")
                                    }
                                    result.isFailure -> {
                                        ownerNameText.isEnabled = true
                                        carNumberText.isEnabled = true
                                        Log.d(
                                            "Post Car Details",
                                            "Car registration failed: Already registered."
                                        )
                                        showCustomDialog("이미 등록된 차량 번호입니다.")
                                    }
                                }
                            }
                        } else if (errCode == "6112" || errCode == "6114") {
                            ownerNameText.isEnabled = true
                            carNumberText.isEnabled = true
                            Log.d("Error", "Invalid car number.")
                            showCustomDialog("없는 차량 번호입니다.")
                        } else {
                            carNumberText.isEnabled = true
                            ownerNameText.isEnabled = true
                            Log.d("Error", "Unknown error. Please try again later.")
                            showCustomDialog("잠시 후 다시 시도해 주세요.")
                        }
                    }
                    imgGif.visibility = View.INVISIBLE
                } catch (e: Exception) {
                    Log.e("Exception", "Exception occurred: ${e.message}")
                    runOnUiThread {
                        ownerNameText.isEnabled = true
                        carNumberText.isEnabled = true
                        showCustomDialog("잠시 후 다시 시도해 주세요.")
                    }
                    imgGif.visibility = View.INVISIBLE
                }
            }

        }
    }

    // Update toolbar title
    fun updateToolbarTitle(title: String, showBackBtn: Boolean = false, showLogo: Boolean = false) {
        val toolbarTitle: TextView = findViewById(R.id.toolbar_title)
        val toolbarBackBtn: ImageButton = findViewById(R.id.toolbar_back)
        val toolbarLogo: ImageView = findViewById(R.id.toolbar_logo)

        toolbarTitle.text = title
        if (title.isEmpty()) {
            toolbarBackBtn.visibility = View.GONE
            toolbarTitle.visibility = View.GONE
            toolbarLogo.visibility = View.VISIBLE
            toolbarLogo.setImageResource(R.drawable.ic_toolbar_logo)
        } else {
            toolbarTitle.visibility = View.VISIBLE
            toolbarLogo.visibility = View.GONE
            if (showBackBtn) {
                toolbarBackBtn.visibility = View.VISIBLE
                toolbarBackBtn.setImageResource(R.drawable.left_chevron)
                setMargins(toolbarTitle, 54.0f)
                toolbarBackBtn.setOnClickListener {
                    onBackPressed()
                }
            } else {
                toolbarBackBtn.visibility = View.GONE
                setMargins(toolbarTitle, 17.0f)
            }
        }
    }

    private fun setMargins(view: View, marginDp: Float) {
        val params = view.layoutParams as RelativeLayout.LayoutParams
        val marginPx = (marginDp * resources.displayMetrics.density).toInt()
        params.marginStart = marginPx
        view.layoutParams = params
    }

    private fun showCustomDialog(message: String) {
        val dialogView = LayoutInflater.from(this).inflate(R.layout.custom_dialog, null)
        val messageTextView = dialogView.findViewById<TextView>(R.id.text_dialog_message)
        val confirmButton = dialogView.findViewById<ImageButton>(R.id.btn_dialog_check)

        messageTextView.text = message

        val dialog = AlertDialog.Builder(this)
            .setView(dialogView)
            .setCancelable(false)
            .create()

        confirmButton.setOnClickListener {
            registerButton.isEnabled=true
            dialog.dismiss()
        }


        dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        dialog.show()

        dialog.window?.setLayout(
            (resources.displayMetrics.widthPixels * 0.8).toInt(),
            ViewGroup.LayoutParams.WRAP_CONTENT
        )
    }

    private fun showCustomDialog2(message: String) {
        val dialogView = LayoutInflater.from(this).inflate(R.layout.custom_dialog, null)
        val messageTextView = dialogView.findViewById<TextView>(R.id.text_dialog_message)
        val confirmButton = dialogView.findViewById<ImageButton>(R.id.btn_dialog_check)

        messageTextView.text = message

        val dialog = AlertDialog.Builder(this)
            .setView(dialogView)
            .setCancelable(false)
            .create()

        confirmButton.setOnClickListener {
            // Dismiss the dialog
            dialog.dismiss()
            onBackPressed()
        }

        dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        dialog.show()

        dialog.window?.setLayout(
            (resources.displayMetrics.widthPixels * 0.8).toInt(),
            ViewGroup.LayoutParams.WRAP_CONTENT
        )
    }

    private fun initialSet() {
        imgGif = findViewById(R.id.img_mypage_add_car_gif) as ImageView
    }
}
