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
                R.id.navigation_home, R.id.navigation_dashboard, R.id.navigation_notifications
            )
        )

        setupActionBarWithNavController(navController, appBarConfiguration)
        navView.setupWithNavController(navController)

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
            toolbarLogo.setImageResource(R.drawable.toolbar_logo)
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
