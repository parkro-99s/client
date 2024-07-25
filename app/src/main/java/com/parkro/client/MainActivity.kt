package com.parkro.client

import android.graphics.Typeface
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

        // Disable the title of the toolbar
        supportActionBar?.setDisplayShowTitleEnabled(false)

        val navView: BottomNavigationView = binding.navView

        navController = findNavController(R.id.nav_host_fragment_activity_main)
        val appBarConfiguration = AppBarConfiguration(
            setOf(
                // 관리자 네비
                // R.id.navigation_parkinglist_admin, R.id.navigation_logout_admin
                 R.id.navigation_map, R.id.navigation_parkinglist, R.id.navigation_payment, R.id.navigation_mypage
            )
        )


        setupActionBarWithNavController(navController, appBarConfiguration)
        navView.setupWithNavController(navController)
        navView.labelVisibilityMode = LabelVisibilityMode.LABEL_VISIBILITY_LABELED

        // 네비게이션 아이템을 선택할 때 호출되는 리스너 설정
        navView.setOnNavigationItemSelectedListener { item ->
            when (item.itemId) {
                R.id.navigation_map -> {
                    item.setIcon(R.drawable.ic_map_navy)
                    true
                }
                R.id.navigation_parkinglist -> {
                    item.setIcon(R.drawable.ic_parkinglist_navy)
                    true
                }
                R.id.navigation_payment -> {
                    item.setIcon(R.drawable.ic_payment_navy)
                    true
                }
                R.id.navigation_mypage -> {
                    item.setIcon(R.drawable.ic_mypage_navy)
                    true
                }
                R.id.navigation_parkinglist_admin -> {
                    item.setIcon(R.drawable.ic_parkinglist_navy)
                    true
                }
                R.id.navigation_logout_admin -> {
                    item.setIcon(R.drawable.ic_logout_navy)
                    true
                }
                else -> false
            }
        }

        navController.addOnDestinationChangedListener { _, destination, _ ->
            updateToolbarTitle("", showBackBtn = navController.previousBackStackEntry != null)
        }
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
            toolbarLogo.setImageResource(R.drawable.logo)
        } else {
            toolbarTitle.visibility = View.VISIBLE
            toolbarLogo.visibility = View.GONE
            if (showBackBtn) {
                toolbarBackBtn.visibility = View.VISIBLE
                toolbarBackBtn.setImageResource(R.drawable.left_chevron)
                setMargins(toolbarTitle, 54.0f)
                toolbarBackBtn.setOnClickListener {
                    // 여기 수정 예정
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
