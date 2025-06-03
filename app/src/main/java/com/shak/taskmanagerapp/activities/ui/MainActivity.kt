package com.shak.taskmanagerapp.activities.ui

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.view.Menu
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.airbnb.lottie.LottieAnimationView
import com.shak.taskmanagerapp.activities.splash.SplashActivity
import com.shak.taskmanagerapp.databinding.ActivityMainBinding
import com.google.firebase.auth.FirebaseAuth
import com.shak.taskmanagerapp.R

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityMainBinding.inflate(layoutInflater)

        setContentView(binding.root)
        ViewCompat.setOnApplyWindowInsetsListener(binding.root) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        binding.apply {
            //TODO: Remove this temporary logout button
            logoutBtn.setOnClickListener {
                FirebaseAuth.getInstance().signOut()
                startActivity(Intent(this@MainActivity, SplashActivity::class.java))
                finish()
            }

            setSupportActionBar(mainToolbar)

        }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.toolbar_menu, menu)

        // Get Lottie view from the custom layout
        val logoutMenuItem = menu?.findItem(R.id.logout)
        val actionView = logoutMenuItem?.actionView
        val menuLogoutLottieAnimView = actionView?.findViewById<LottieAnimationView>(R.id.menuLogoutLottieAnimView)

        // Set click listener on the animation itself
        menuLogoutLottieAnimView?.setOnClickListener {
            menuLogoutLottieAnimView.playAnimation()

            val durationOfLottieAnimation = menuLogoutLottieAnimView.duration
            Handler().postDelayed(
                {
                    FirebaseAuth.getInstance().signOut()
                    startActivity(
                        Intent(this@MainActivity, SplashActivity::class.java)
                            .apply {
                                flags = Intent.FLAG_ACTIVITY_CLEAR_TOP
                            }
                    )
                    finish()
                },
                durationOfLottieAnimation + 300L
            )
        }

        return true
    }
}