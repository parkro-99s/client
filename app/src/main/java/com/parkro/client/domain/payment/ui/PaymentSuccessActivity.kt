package com.parkro.client.domain.payment.ui

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.parkro.client.MainActivity
import com.parkro.client.R

class PaymentSuccessActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_payment_success)

        // 필요한 데이터 가져오기
        val orderId = intent.getStringExtra("orderId")
        val amount = intent.getStringExtra("amount")

        // 결제 성공 메시지 설정
        val successTextView: TextView = findViewById(R.id.successTextView)
        successTextView.text = "결제 완료!\nOrder ID: $orderId \nAmount: $amount"

        // 결제 완료 버튼 클릭 리스너 설정
        val btnCompletePayment: Button = findViewById(R.id.btnCompletePayment)
        btnCompletePayment.setOnClickListener {
            // MainActivity로 이동
            val mainIntent = Intent(this, MainActivity::class.java)
            startActivity(mainIntent)
            finish()
        }
    }
}
