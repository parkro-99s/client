package com.parkro.client.common.ui

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.animation.AnimationUtils
import com.parkro.client.R
import com.parkro.client.databinding.ActivitySplashBinding
import android.os.Handler
import android.view.View
import android.view.animation.Animation
import com.parkro.client.domain.login.ui.LoginActivity

class SplashActivity : AppCompatActivity() {

    private lateinit var binding: ActivitySplashBinding

    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        binding = ActivitySplashBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // splashLogo 초기에는 안 보이게 설정
        binding.splashLogo.visibility = View.INVISIBLE

        // 애니메이션 설정
        val slideIn = AnimationUtils.loadAnimation(this, R.anim.anim_car_in)
        slideIn.setAnimationListener(object : Animation.AnimationListener {
            override fun onAnimationStart(animation: Animation?) {}

            override fun onAnimationEnd(animation: Animation?) {
                // 애니메이션이 끝난 후 splash_logo를 보이게 설정
                binding.splashLogo.visibility = View.VISIBLE

                val fadeIn = AnimationUtils.loadAnimation(this@SplashActivity, R.anim.anim_logo_pop)
                binding.splashLogo.startAnimation(fadeIn)
            }

            override fun onAnimationRepeat(animation: Animation?) {}
        })
        binding.splashCar.startAnimation(slideIn)

        // 1.8초 후에 로그인 액티비티로 전환
        Handler().postDelayed({
            val intent = Intent(this, LoginActivity::class.java)
            startActivity(intent)
            finish()
        }, 1800) // 1.8초 대기 시간
    }
}