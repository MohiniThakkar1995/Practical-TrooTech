package com.example.prac.activity

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import androidx.databinding.DataBindingUtil
import com.example.jetpackdemo.MainActivity
import com.example.prac.R
import com.example.prac.databinding.ActivitySplashBinding
import com.example.prac.navigator.SplashNavigator

class SplashActivity : AppCompatActivity(), SplashNavigator {
    lateinit var binding: ActivitySplashBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this,
            R.layout.activity_splash
        )
        navigate()
    }
    override fun navigate() {
        Handler(Looper.getMainLooper()).postDelayed({
            startActivity(MainActivity.newIntent(this))
            finish()
        }, 2000)

    }
}
