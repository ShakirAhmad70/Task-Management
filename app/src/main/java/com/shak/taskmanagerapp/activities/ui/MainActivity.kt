package com.shak.taskmanagerapp.activities.ui

import android.content.Intent
import android.os.Bundle
import android.view.Menu
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.edit
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.google.firebase.auth.FirebaseAuth
import com.shak.taskmanagerapp.BuildConfig
import com.shak.taskmanagerapp.R
import com.shak.taskmanagerapp.activities.auth.RegisterBenefitsActivity
import com.shak.taskmanagerapp.activities.splash.SplashActivity
import com.shak.taskmanagerapp.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    private val currentUser = FirebaseAuth.getInstance().currentUser

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

        val mainPref = getSharedPreferences(BuildConfig.MAIN_PREFERENCE_KEY, MODE_PRIVATE)
        val isLoggedOut = mainPref.getBoolean(BuildConfig.IS_LOGGED_OUT_KEY, false)
        if(!(currentUser != null || !isLoggedOut)){
            startActivity(Intent(this, RegisterBenefitsActivity::class.java))
            finish()
        }

        binding.apply {
            //TODO: Remove this temporary logout button
            logoutBtn.setOnClickListener {
                mainPref.edit {
                    putBoolean(BuildConfig.IS_LOGGED_OUT_KEY, true)
                    commit()
                }

                if(FirebaseAuth.getInstance().currentUser != null){
                    FirebaseAuth.getInstance().signOut()
                }

                val intent = Intent(this@MainActivity, SplashActivity::class.java)
                    .apply {
                        flags = Intent.FLAG_ACTIVITY_CLEAR_TOP
                    }
                startActivity(intent)
                finish()
            }

            setSupportActionBar(mainToolbar)

        }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.toolbar_menu, menu)

        val logoutItem = menu?.findItem(R.id.logout)
        logoutItem?.setOnMenuItemClickListener {
            val mainPref = getSharedPreferences(BuildConfig.MAIN_PREFERENCE_KEY, MODE_PRIVATE)
            mainPref.edit {
                putBoolean(BuildConfig.IS_LOGGED_OUT_KEY, true)
                commit()
            }

            if(FirebaseAuth.getInstance().currentUser != null){
                FirebaseAuth.getInstance().signOut()
            }

            val intent = Intent(this@MainActivity, SplashActivity::class.java)
                .apply {
                    flags = Intent.FLAG_ACTIVITY_CLEAR_TOP
                }
            startActivity(intent)
            finish()
            true
        }

        return true
    }
}