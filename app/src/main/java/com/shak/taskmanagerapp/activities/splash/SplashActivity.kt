package com.shak.taskmanagerapp.activities.splash

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.view.animation.BounceInterpolator
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.google.firebase.auth.FirebaseAuth
import com.shak.taskmanagerapp.BuildConfig
import com.shak.taskmanagerapp.activities.auth.RegisterBenefitsActivity
import com.shak.taskmanagerapp.activities.ui.MainActivity
import com.shak.taskmanagerapp.activities.ui.OnboardingActivity
import com.shak.taskmanagerapp.databinding.ActivitySplashBinding

@SuppressLint("CustomSplashScreen")
class SplashActivity : AppCompatActivity() {

    private lateinit var binding: ActivitySplashBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivitySplashBinding.inflate(layoutInflater)
        setContentView(binding.root)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(binding.root.id)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }


        val firebaseAuth = FirebaseAuth.getInstance()
        val currentUser = firebaseAuth.currentUser

        val mainPref = getSharedPreferences(BuildConfig.MAIN_PREFERENCE_KEY, MODE_PRIVATE)
        val isLoggedOut = mainPref.getBoolean(BuildConfig.IS_LOGGED_OUT_KEY, true)
        val isOnboardingCompleted = mainPref.getBoolean(BuildConfig.IS_ONBOARDING_COMPLETED_KEY, false)


        binding.splashImg.animate()
            .translationY(-100f)
            .scaleX(0.8f)
            .scaleY(0.8f)
            .setDuration(1500)
            .setInterpolator(BounceInterpolator())
            .start()

        binding.splashTxt.animate()
            .alpha(1f)
            .translationY(-100f)
            .scaleX(1.1f)
            .scaleY(1.1f)
            .setDuration(1500)
            .withEndAction {
                if ( currentUser != null || !isLoggedOut ) {
                    startActivity(Intent(this, MainActivity::class.java))
                } else if ( !isOnboardingCompleted ) {
                    startActivity(Intent(this, OnboardingActivity::class.java))
                } else {
                    startActivity(Intent(this, RegisterBenefitsActivity::class.java))
                }
                finish()
            }
            .start()

    }
}