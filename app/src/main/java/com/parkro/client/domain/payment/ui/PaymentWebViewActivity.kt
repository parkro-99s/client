package com.parkro.client.domain.payment.ui

import android.content.ActivityNotFoundException
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.webkit.*
import androidx.appcompat.app.AppCompatActivity
import com.parkro.client.BuildConfig
import com.parkro.client.R
import java.net.URISyntaxException

// 결제창 웹뷰로 띄울 Activity
class PaymentWebViewActivity : AppCompatActivity() {
    private lateinit var webView: WebView
    private val paymentData = mutableMapOf<String, String>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_payment_web_view)

        // Intent로부터 데이터 가져오기
        val intent = intent
        paymentData["amount"] = intent.getStringExtra("amount") ?: "0"
        paymentData["orderId"] = intent.getStringExtra("orderId") ?: "ORDER_ID"
        paymentData["orderName"] = intent.getStringExtra("orderName") ?: "주차장 사전 결제"
        paymentData["customerName"] = intent.getStringExtra("customerName") ?: ""

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
                        val intent = Intent(this@PaymentWebViewActivity, PaymentSuccessActivity::class.java)
                        intent.putExtra("orderId", paymentData["orderId"])
                        intent.putExtra("amount", paymentData["amount"])
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
            println("getpaymentdata: " + key + " " + paymentData[key])
            return paymentData[key] ?: ""
        }
    }
}
