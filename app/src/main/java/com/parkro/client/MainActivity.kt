package com.parkro.client

import android.os.Bundle
import android.view.View
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.navigation.NavController
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.bottomnavigation.LabelVisibilityMode
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import com.parkro.client.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var navController: NavController

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Custom toolbar setup
        val toolbar: Toolbar = findViewById(R.id.toolbar)
        setSupportActionBar(toolbar)

        supportActionBar?.setDisplayShowTitleEnabled(false) // 기본 타이틀 비활성화

        val navView: BottomNavigationView = binding.navView
//
        navController = findNavController(R.id.nav_host_fragment_activity_main)
        val appBarConfiguration = AppBarConfiguration(
            setOf(
                R.id.navigation_example, // map 대신 example
                R.id.navigation_parkinglist,
                R.id.navigation_payment,
                R.id.navigation_mypage
            )
        )

        setupActionBarWithNavController(navController, appBarConfiguration)
        navView.setupWithNavController(navController)
        navView.labelVisibilityMode = LabelVisibilityMode.LABEL_VISIBILITY_LABELED

        // 네비게이션 아이템을 선택할 때 호출되는 리스너 설정
        navController.addOnDestinationChangedListener { _, destination, _ ->

            val fragmentsWithoutUpButton = setOf(
                R.id.navigation_map,
                R.id.navigation_receipt,
                R.id.navigation_barcode_scan,
//                R.id.navigation_example, // map 대신 example
                R.id.navigation_coupon,
                R.id.navigation_receipt,
                R.id.navigation_barcode_scan,
                R.id.navigation_parkinglist,
                R.id.navigation_payment,
                R.id.navigation_mypage,
                R.id.navigation_parkinglist_admin,
                R.id.navigation_logout_admin
            )

            // 기본 네비게이션 아이콘(Back Button) 제거
            if (destination.id in fragmentsWithoutUpButton) {
                supportActionBar?.setDisplayHomeAsUpEnabled(false)
                toolbar.navigationIcon = null
            }

            // 기본 사용자 아이콘
            navView.menu.findItem(R.id.navigation_map)?.setIcon(R.drawable.ic_map_gray)
//            navView.menu.findItem(R.id.navigation_example)?.setIcon(R.drawable.ic_map_gray) // map 대신 example
            navView.menu.findItem(R.id.navigation_parkinglist)?.setIcon(R.drawable.ic_parkinglist_gray)
            navView.menu.findItem(R.id.navigation_payment)?.setIcon(R.drawable.ic_payment_gray)
            navView.menu.findItem(R.id.navigation_mypage)?.setIcon(R.drawable.ic_mypage_gray)

            // 현재 선택된 아이템 아이콘 업데이트
            when (destination.id) {
                R.id.navigation_map -> {
//                R.id.navigation_example -> { // map 대신 example
                    navView.menu.findItem(R.id.navigation_map)?.setIcon(R.drawable.ic_map_navy)
//                    navView.menu.findItem(R.id.navigation_example)?.setIcon(R.drawable.ic_map_navy) // map 대신 example
                }
                R.id.navigation_parkinglist -> {
                    navView.menu.findItem(R.id.navigation_parkinglist)?.setIcon(R.drawable.ic_parkinglist_navy)
                }
                R.id.navigation_payment -> {
                    navView.menu.findItem(R.id.navigation_payment)?.setIcon(R.drawable.ic_payment_navy)
                }
                R.id.navigation_mypage -> {
                    navView.menu.findItem(R.id.navigation_mypage)?.setIcon(R.drawable.ic_mypage_navy)
                }
            }

            updateToolbarTitle("", showBackBtn = navController.previousBackStackEntry != null)
        }

        // TODO: 로그인 시, FCM Token 헤더에 함께 전달
//        FirebaseMessaging.getInstance().token.addOnCompleteListener { task ->
//            if (!task.isSuccessful) {
//                Log.w("FCM", "Fetching FCM registration token failed", task.exception)
//                return@addOnCompleteListener
//            }
//            // 새로운 FCM Token 발급
//            val token = task.result
//            Log.d("FCM-Token", token)
//        }
    }

    // Update toolbar title
    fun updateToolbarTitle(title: String, showBackBtn: Boolean = false, showLogo: Boolean = false) {
        val toolbarTitle: TextView = findViewById(R.id.toolbar_title)
        val toolbarBackBtn : ImageButton = findViewById(R.id.toolbar_back)
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
}
