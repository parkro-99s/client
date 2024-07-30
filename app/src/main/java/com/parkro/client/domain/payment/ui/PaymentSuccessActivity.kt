package com.parkro.client.domain.payment.ui

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.TextView
import com.parkro.client.R

// 결제가 성공적으로 완료됐을 때 돌아오는 Activity
class PaymentSuccessActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_payment_success)

        val orderId = intent.getStringExtra("orderId")
        val amount = intent.getStringExtra("amount")

        val textView = findViewById<TextView>(R.id.successTextView)
        textView.text = "Order ID: $orderId\nAmount: $amount"
    }
}
