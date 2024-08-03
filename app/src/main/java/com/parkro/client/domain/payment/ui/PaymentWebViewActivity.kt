package com.parkro.client.domain.payment.ui

import android.content.ActivityNotFoundException
import android.content.Intent
import android.icu.number.IntegerWidth
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.webkit.*
import androidx.appcompat.app.AppCompatActivity
import com.parkro.client.BuildConfig
import com.parkro.client.R
import com.parkro.client.domain.payment.api.PostPaymentReq
import com.parkro.client.domain.payment.data.PaymentData
import com.parkro.client.domain.payment.data.PaymentRepository
import java.net.URISyntaxException

// 결제창 웹뷰로 띄울 Activity
class PaymentWebViewActivity : AppCompatActivity() {
    private lateinit var webView: WebView
    private lateinit var paymentData: PaymentData
    private lateinit var paymentRepository: PaymentRepository

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_payment_web_view)

        // Intent로부터 데이터 가져오기
        val intent = intent
        paymentData = PaymentData(
            amount = intent.getStringExtra("amount") ?: "0",
            orderId = intent.getStringExtra("orderId") ?: "ORDER_ID",
            username = intent.getStringExtra("username") ?: "here12314",
            orderName = intent.getStringExtra("orderName") ?: "주차장 사전 결제",
            customerName = intent.getStringExtra("customerName") ?: "",
            parkingId = intent.getStringExtra("parkingId") ?: "",
            memberCouponId = intent.getStringExtra("memberCouponId") ?: "",
            receiptId = intent.getStringExtra("receiptId") ?: "",
            couponDiscountTime = intent.getStringExtra("couponDiscountTime") ?: "0",
            receiptDiscountTime = intent.getStringExtra("receiptDiscountTime") ?: "0",
            totalParkingTime = intent.getStringExtra("totalParkingTime") ?: "0",
            totalPrice = intent.getStringExtra("totalPrice") ?: "0",
            card = intent.getStringExtra("card") ?: ""
        )

        Log.d("PaymentWebViewActivity", "페이먼츠 결제 내역: $paymentData")

        webView = findViewById(R.id.webview)

        webView.settings.run {
            javaScriptEnabled = true
            javaScriptCanOpenWindowsAutomatically = true
            cacheMode = WebSettings.LOAD_NO_CACHE
            defaultTextEncodingName = "utf-8"
            setSupportMultipleWindows(true)
        }

        // JavaScript 인터페이스 추가
        webView.addJavascriptInterface(WebAppInterface(), "Android")

        webView.webViewClient = object : WebViewClient() {
            override fun shouldOverrideUrlLoading(view: WebView, request: WebResourceRequest): Boolean {
                val url = request.url.toString()
                return handleUrl(url)
            }

            @Deprecated("Deprecated in Java")
            override fun shouldOverrideUrlLoading(view: WebView, url: String): Boolean {
                return if (Build.VERSION.SDK_INT < Build.VERSION_CODES.N) {
                    handleUrl(url)
                } else super.shouldOverrideUrlLoading(view, url)
            }

            // WebViewActivity에서 결제 성공 후
            private fun handleUrl(url: String): Boolean {
                return if (!URLUtil.isNetworkUrl(url) && !URLUtil.isJavaScriptUrl(url)) {
                    val uri = Uri.parse(url)
                    if ("parkro" == uri.scheme) {

                        val orderId = uri.getQueryParameter("orderId")
                        val amount = uri.getQueryParameter("amount")
                        val paymentKey = uri.getQueryParameter("paymentKey")

                        paymentKey?.let {
                            sendPaymentSuccessToServer(paymentKey)
                        }
                        val intent = Intent(this@PaymentWebViewActivity, PaymentSuccessActivity::class.java)
                        intent.putExtra("orderId", orderId)
                        intent.putExtra("amount", amount)
                        // 여기에서 결제 내역 삽입을 서버로 요청보내는 로직 추가
                        startActivity(intent)
                        finish() // 현재 Activity 종료
                        true
                    } else {
                        handleScheme(url)
                    }
                } else {
                    false
                }
            }

            private fun handleScheme(url: String): Boolean {
                val schemeIntent: Intent = try {
                    Intent.parseUri(url, Intent.URI_INTENT_SCHEME)
                } catch (e: URISyntaxException) {
                    return false
                }
                return try {
                    startActivity(schemeIntent)
                    true
                } catch (e: ActivityNotFoundException) {
                    schemeIntent.`package`?.let {
                        startActivity(Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=$it")))
                    }
                    false
                }
            }
        }

        val webAppInterface = WebAppInterface()
        webView.addJavascriptInterface(webAppInterface, "Android")

        // 웹뷰 로드
        webView.loadUrl("file:///android_asset/payment.html")
    }

    private fun sendPaymentSuccessToServer(paymentKey: String) {
        val paymentRepository = PaymentRepository()

        val paymentRequest = PostPaymentReq(
            username = paymentData.username,
            parkingId = paymentData.parkingId?.toIntOrNull() ?: 0,  // null일 경우 기본값 0
            memberCouponId = paymentData.memberCouponId?.toIntOrNull(),  // null일 경우 기본값 0
            receiptId = paymentData.receiptId?.toIntOrNull(),  // null일 경우 기본값 0
            couponDiscountTime = paymentData.couponDiscountTime?.toIntOrNull() ?: 0,  // null 허용
            receiptDiscountTime = paymentData.receiptDiscountTime?.toIntOrNull() ?: 0,  // null 허용
            paymentKey = paymentKey,
            totalParkingTime = paymentData.totalParkingTime?.toIntOrNull() ?: 0,  // null일 경우 기본값 0
            totalPrice = paymentData.totalPrice?.toIntOrNull() ?: 0,  // null일 경우 기본값 0
            card = paymentData.card.orEmpty(),  // null일 경우 빈 문자열
        )

        paymentRepository.addPayment(paymentRequest) { result ->
            result.fold(
                onSuccess = { paymentId ->
                    // 결제 내역이 성공적으로 추가되었을 때 처리
                    Log.d("PaymentWebViewActivity", "결제 내역 추가 성공: $paymentId")
                },
                onFailure = { error ->
                    // 결제 내역 추가 실패 시 처리
                    Log.d("PaymentWebViewActivity", "결제 내역 추가 실패: ${error.message}")
                }
            )
        }
    }

    private inner class WebAppInterface {
        @JavascriptInterface
        fun getClientKey(): String {
            return BuildConfig.PAYMENT_CLIENT_KEY
        }

        @JavascriptInterface
        fun getServerBaseUrl(): String {
            return BuildConfig.SERVER_BASEURL
        }

        @JavascriptInterface
        fun getPaymentData(key: String): String {
            return when (key) {
                "amount" -> paymentData.amount
                "orderId" -> paymentData.orderId
                "orderName" -> paymentData.orderName
                "customerName" -> paymentData.customerName
                "parkingId" -> paymentData.parkingId
                "memberCouponId" -> paymentData.memberCouponId
                "receiptId" -> paymentData.receiptId
                "couponDiscountTime" -> paymentData.couponDiscountTime
                "receiptDiscountTime" -> paymentData.receiptDiscountTime
                "totalParkingTime" -> paymentData.totalParkingTime
                "totalPrice" -> paymentData.totalPrice
                "card" -> paymentData.card
                else -> ""
            }
        }
    }
}
