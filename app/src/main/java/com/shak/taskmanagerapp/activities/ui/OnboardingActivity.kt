package com.shak.taskmanagerapp.activities.ui

import android.content.Intent
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.shak.taskmanagerapp.R
import com.shak.taskmanagerapp.activities.auth.RegisterBenefitsActivity
import com.shak.taskmanagerapp.adapters.OnboardViewPagerAdapter
import com.shak.taskmanagerapp.databinding.ActivityOnboardingBinding
import com.shak.taskmanagerapp.models.OnboardingItemModel
import kotlin.reflect.KClass

class OnboardingActivity : AppCompatActivity() {

    private lateinit var binding: ActivityOnboardingBinding
    private val itemsList = listOf(
        OnboardingItemModel(
            R.drawable.task_manager,
            "Task Manager",
            "Easily organize and manage your tasks to stay productive all day."
        ),
        OnboardingItemModel(
            R.drawable.calendar_sync,
            "Calendar Sync",
            "Sync all your events and deadlines in one place for better planning."
        ),
        OnboardingItemModel(
            R.drawable.eisenhower_tower,
            "Eisenhower Matrix",
            "Sort your tasks based on importance and urgency to work smarter."
        ),
        OnboardingItemModel(
            R.drawable.pomodoro_timer,
            "Pomodoro Timer",
            "Stay focused using time-blocked sessions and short breaks."
        ),
        OnboardingItemModel(
            R.drawable.habits_tracker,
            "Habits Tracker",
            "Create daily routines and track your habit progress visually."
        )
    )


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityOnboardingBinding.inflate(layoutInflater)
        setContentView(binding.root)
        ViewCompat.setOnApplyWindowInsetsListener(binding.root) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        binding.apply {

            viewPager.adapter = OnboardViewPagerAdapter(itemsList)

            continueBtn.setOnClickListener {
                if(viewPager.currentItem < itemsList.size - 1) {
                    viewPager.currentItem  += 1
                } else {
                    navigateToAndFinishCurrentActivity(RegisterBenefitsActivity::class)
                }
            }

            skipTxt.setOnClickListener {
                navigateToAndFinishCurrentActivity(RegisterBenefitsActivity::class)
            }
        }
    }

    private fun navigateToAndFinishCurrentActivity(klass: KClass<*>) {
        val intent = Intent(this, klass.java)
        startActivity(intent)
        finish()
    }

}