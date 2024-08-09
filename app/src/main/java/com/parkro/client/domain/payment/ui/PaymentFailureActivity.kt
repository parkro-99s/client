package com.parkro.client.domain.payment.ui

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.ImageButton
import com.parkro.client.MainActivity
import com.parkro.client.R

/**
 * 결제 실패 시 이동할 Activity
 *
 * @author 김지수
 * @since 2024.08.04
 *
 * <pre>
 * 수정일자       수정자        수정내용
 * ------------ --------    ---------------------------
 * 2024.08.04   김지수      최초 생성
 * </pre>
 */
class PaymentFailureActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_payment_failure)

        val orderId = intent.getStringExtra("orderId")
        val amount = intent.getStringExtra("amount")

        Log.d("PaymentFailureActivity", "[결제 실패] orderID: $orderId, amount: $amount")

        // 정산 페이지로 이동 버튼 클릭 리스너 설정
        val btnPaymentFailureMovePayment: ImageButton = findViewById(R.id.btn_payment_failure_move_payment)
        btnPaymentFailureMovePayment.setOnClickListener {
            val mainIntent = Intent(this, MainActivity::class.java).apply {
                // 바로 특정 fragment로 이동하기 위해 필요한 설정
                flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP
                putExtra("navigate_to", "payment_fragment")
            }
            startActivity(mainIntent)
            finish()
        }
    }
}