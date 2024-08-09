package com.parkro.client.domain.admin.ui

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
import com.parkro.client.R
import com.parkro.client.databinding.ActivityAdminBinding
import com.parkro.client.util.PreferencesUtil
/**
 * 관리자 액티비티
 *
 * @author 양재혁
 * @since 2024.08.02
 *
 * <pre>
 * 수정일자       수정자        수정내용
 * ------------ --------    ---------------------------
 * 2024.08.02   양재혁      최초 생성
 * </pre>
 */
class AdminActivity : AppCompatActivity() {

    private lateinit var binding: ActivityAdminBinding
    private lateinit var navController: NavController

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAdminBinding.inflate(layoutInflater)
        setContentView(binding.root)

        PreferencesUtil.init(context = this)
        // Custom toolbar setup
        val toolbar: Toolbar = findViewById(R.id.toolbar)
        setSupportActionBar(toolbar)

        supportActionBar?.setDisplayShowTitleEnabled(false) // 기본 타이틀 비활성화

        val navView: BottomNavigationView = binding.navView
//
        navController = findNavController(R.id.nav_host_fragment_activity_admin)

        val appBarConfiguration = AppBarConfiguration(
            setOf(
                R.id.navigation_parkinglist_admin,
                R.id.navigation_logout_admin
            )
        )
        setupActionBarWithNavController(navController, appBarConfiguration)
        navView.setupWithNavController(navController)
        navView.labelVisibilityMode = LabelVisibilityMode.LABEL_VISIBILITY_LABELED

        // 네비게이션 아이템을 선택할 때 호출되는 리스너 설정
        navController.addOnDestinationChangedListener { _, destination, _ ->

            val fragmentsWithoutUpButton = setOf(
                R.id.navigation_parkingdetail_admin,
                R.id.navigation_parkinglist_admin,
                R.id.navigation_logout_admin
            )

            // 기본 네비게이션 아이콘(Back Button) 제거
            if (destination.id in fragmentsWithoutUpButton) {
                supportActionBar?.setDisplayHomeAsUpEnabled(false)
                toolbar.navigationIcon = null
            }
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