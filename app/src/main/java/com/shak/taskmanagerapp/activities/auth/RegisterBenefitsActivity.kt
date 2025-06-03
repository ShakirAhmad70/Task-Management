package com.shak.taskmanagerapp.activities.auth

import android.content.Intent
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.shak.taskmanagerapp.R
import com.shak.taskmanagerapp.activities.ui.MainActivity
import com.shak.taskmanagerapp.adapters.RegisterBenefitsViewPagerAdapter
import com.shak.taskmanagerapp.databinding.ActivityRegisterBenefitsBinding
import com.shak.taskmanagerapp.models.RegisterBenefitsItemModel
import kotlin.reflect.KClass

class RegisterBenefitsActivity : AppCompatActivity() {

    private lateinit var binding: ActivityRegisterBenefitsBinding
    private val itemsList = listOf(
        RegisterBenefitsItemModel(
            R.drawable.smart_task_management,
            "Plan tasks effectively and stay focused with our smart task manager."
        ),
        RegisterBenefitsItemModel(
            R.drawable.daily_reminders,
            "Get daily reminders and manage your calendar in one organized space."
        ),
        RegisterBenefitsItemModel(
            R.drawable.smart_prioritization,
            "Prioritize tasks smartly using the Eisenhower Matrix built-in system."
        ),
        RegisterBenefitsItemModel(
            R.drawable.productivity_booster,
            "Boost productivity with built-in Pomodoro timer and focus sessions."
        ),
        RegisterBenefitsItemModel(
            R.drawable.daily_habits,
            "Track your daily habits and see your growth with simple progress visuals."
        )
    )


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        binding = ActivityRegisterBenefitsBinding.inflate(layoutInflater)
        setContentView(binding.root)
        ViewCompat.setOnApplyWindowInsetsListener(binding.root) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        binding.apply {

            regBenefitsVp.adapter = RegisterBenefitsViewPagerAdapter(itemsList)
            dotsIndicator.attachTo(regBenefitsVp)

            nextBtn.setOnClickListener {
                if(regBenefitsVp.currentItem < itemsList.size - 1) {
                    regBenefitsVp.currentItem += 1
                } else {
                    regBenefitsVp.currentItem = 0
                }
            }

            skipBtn.setOnClickListener {
                navigateTo(MainActivity::class)
                finish()
            }

            loginBtn.setOnClickListener {
                navigateTo(LoginActivity::class)
            }

            registerBtn.setOnClickListener {
                navigateTo(RegisterActivity::class)
            }

        }

    }

    private fun navigateTo(klass: KClass<*>) {
        val intent = Intent(this, klass.java)
        startActivity(intent)
    }
}