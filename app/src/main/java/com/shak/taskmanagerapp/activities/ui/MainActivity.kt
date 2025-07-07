package com.shak.taskmanagerapp.activities.ui

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.edit
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.fragment.app.Fragment
import com.google.firebase.auth.FirebaseAuth
import com.shak.taskmanagerapp.BuildConfig
import com.shak.taskmanagerapp.R
import com.shak.taskmanagerapp.activities.auth.RegisterBenefitsActivity
import com.shak.taskmanagerapp.activities.splash.SplashActivity
import com.shak.taskmanagerapp.databinding.ActivityMainBinding
import com.shak.taskmanagerapp.fragments.CalendarFragment
import com.shak.taskmanagerapp.fragments.EisenhowerMatrixFragment
import com.shak.taskmanagerapp.fragments.PomodoroTimerFragment
import com.shak.taskmanagerapp.fragments.ProfileFragment
import com.shak.taskmanagerapp.fragments.TasksFragment
import com.skydoves.balloon.Balloon
import com.skydoves.balloon.BalloonAnimation

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

        if (savedInstanceState == null) {  // First time
            loadFragment(TasksFragment())
        } else {  // Restoring on configuration change
            binding.bottomNavBar.itemActiveIndex = savedInstanceState.getInt("BOTTOM_NAV_INDEX")
        }

        binding.apply {

            // Setup toolbar
            setSupportActionBar(mainToolbar)

            // Setup bottom navigation bar item clicks
            bottomNavBar.setOnItemSelectedListener {pos ->
                setUpBottomNavClicks(pos)
            }

        }

    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putInt("BOTTOM_NAV_INDEX", binding.bottomNavBar.itemActiveIndex)
    }

    private fun setUpBottomNavClicks(pos: Int) {
        var tooltipTxt: String

        when(pos) {
            0 ->{
                binding.mainToolbar.title = "Tasks"
                loadFragment(TasksFragment())
                tooltipTxt = "Tasks"
            }
            1 ->{
                binding.mainToolbar.title = "Calendar"
                loadFragment(CalendarFragment())
                tooltipTxt = "Calendar"
            }
            2 ->{
                binding.mainToolbar.title = "Eisenhower Matrix"
                loadFragment(EisenhowerMatrixFragment())
                tooltipTxt = "Eisenhower Matrix"
            }
            3 ->{
                binding.mainToolbar.title = "Pomodoro Timer"
                loadFragment(PomodoroTimerFragment())
                tooltipTxt = "Pomodoro Timer"
            }
            4 ->{
                binding.mainToolbar.title = "Profile"
                loadFragment(ProfileFragment())
                tooltipTxt = "Profile"
            }
            else -> tooltipTxt = "Unknown"
        }

        val balloon = createBalloon(tooltipTxt)
        balloon.showAlignTop(binding.bottomNavBar, ( pos - 2 ) * window.decorView.width / 5, 0)

    }

    private fun loadFragment(fragment: Fragment) {
        val transaction = supportFragmentManager.beginTransaction()
        transaction.replace(R.id.mainFrameLay, fragment)
        transaction.commit()
    }


    private fun showToast(msg: String) {
        Toast.makeText(
            this,
            msg,
            Toast.LENGTH_SHORT
        ).show()
    }

    fun createBalloon(tooltipTxt: String): Balloon {
        Log.d("BalloonDebug", "Creating balloon for: $tooltipTxt")
        return Balloon.Builder(this)
            .setText(tooltipTxt)
            .setTextColorResource(R.color.green)
            .setTextSize(12f)
            .setPadding(8)
            .setCornerRadius(20f)
            .setBackgroundColorResource(R.color.orange)
            .setBalloonAnimation(BalloonAnimation.CIRCULAR)
            .setAutoDismissDuration(1000)
            .setLifecycleOwner(this)
            .build()
    }


    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.toolbar_menu, menu)

        val menuBuilderClass = Class.forName("androidx.appcompat.view.menu.MenuBuilder")
        if (menuBuilderClass.isInstance(menu)) {
            try {
                val method = menuBuilderClass.getDeclaredMethod("setOptionalIconsVisible", Boolean::class.javaPrimitiveType)
                method.invoke(menu, true)
            } catch (e: Exception) {
                Log.e("MenuError", "Failed to set optional icons visible", e)
            }
        }

        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.search -> {
                showToast("Search Clicked")
                true
            }
            R.id.select -> {
                showToast("Select Clicked")
                true
            }
            R.id.view -> {
                showToast("View Clicked")
                true
            }
            R.id.sort -> {
                showToast("Sort Clicked")
                true
            }
            R.id.settings -> {
                showToast("Settings Clicked")
                true
            }
            R.id.logout -> {
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
            else -> super.onOptionsItemSelected(item)
        }
    }
}