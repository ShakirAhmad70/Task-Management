package com.shak.taskmanagerapp.activities.ui

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.DatePicker
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.AppCompatButton
import androidx.appcompat.widget.AppCompatEditText
import androidx.core.content.edit
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.firebase.auth.FirebaseAuth
import com.shak.taskmanagerapp.BuildConfig
import com.shak.taskmanagerapp.R
import com.shak.taskmanagerapp.activities.auth.RegisterBenefitsActivity
import com.shak.taskmanagerapp.activities.splash.SplashActivity
import com.shak.taskmanagerapp.adapters.TasksRecyclerAdapter
import com.shak.taskmanagerapp.databinding.ActivityMainBinding
import com.shak.taskmanagerapp.models.TasksItemModel
import com.shak.taskmanagerapp.utils.TaskItemDiffUtil
import com.skydoves.balloon.Balloon
import com.skydoves.balloon.BalloonAnimation
import java.util.Calendar

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    private val currentUser = FirebaseAuth.getInstance().currentUser
    private val taskItemsList = mutableListOf<TasksItemModel>()


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


        // Setup the Recycler view for Overdue tasks
        checkEmptyAndLoad()
        setUpTasksRecyclerViews()


        binding.apply {

            // Setup toolbar
            setSupportActionBar(mainToolbar)


            // Setup bottom navigation bar item clicks
            bottomNavBar.setOnItemSelectedListener { pos ->
                setUpBottomNavBallons(pos)
            }


            // Add items
            addTasksFab.setOnClickListener {
                showAddEditTaskBottomSheet(binding)
            }

        }

    }

    private fun setUpBottomNavBallons(pos: Int) {
        val tooltipTxt = when(pos) {
            0 -> "Tasks"
            1 -> "Calendar"
            2 -> "Eisenhower Matrix"
            3 -> "Pomodoro Timer"
            4 -> "Profile"
            else -> "Unknown"
        }

        val balloon = createBalloon(tooltipTxt)
        balloon.showAlignTop(binding.bottomNavBar, ( pos - 2 ) * window.decorView.width / 5, 0)

    }

    private fun checkEmptyAndLoad() {
        binding.apply {
            val overDueTasksList = taskItemsList.filter { !it.isCompleted }
            val completedTasksList = taskItemsList.filter { it.isCompleted }

            if (taskItemsList.isNotEmpty()) {
                mainEmptyTasksContainerRLay.visibility = View.GONE
            } else {
                mainEmptyTasksContainerRLay.visibility = View.VISIBLE
                overDueTasksContainer.visibility = View.GONE
                completedTasksContainer.visibility = View.GONE
            }

            if(overDueTasksList.isNotEmpty()){
                overDueTasksContainer.visibility = View.VISIBLE
                overDueTasksCountTxt.text = overDueTasksList.size.toString()
                overDueTasksArrowTxt.text = ">"
            } else {
                overDueTasksContainer.visibility = View.GONE
                overDueTasksArrowTxt.text = "0"
            }

            if(completedTasksList.isNotEmpty()){
                completedTasksContainer.visibility = View.VISIBLE
                completedTasksCountTxt.text = completedTasksList.size.toString()
                completedTasksArrowTxt.text = ">"
            } else {
                completedTasksContainer.visibility = View.GONE
                completedTasksArrowTxt.text = "0"
            }
        }

    }

    private fun showAddEditTaskBottomSheet(binding: ActivityMainBinding) {
        val bottomSheetView = layoutInflater.inflate(
            R.layout.add_edit_tasks_bottom_sheet_layout,
            binding.root,
            false
        )

        val bottomSheetDialog = BottomSheetDialog(this)
        bottomSheetDialog.setContentView(bottomSheetView)
        bottomSheetDialog.setCanceledOnTouchOutside(false)
        bottomSheetDialog.show()

        val bottomSheetTitleEdt = bottomSheetView.findViewById<AppCompatEditText>(R.id.bottomSheetTitleEdt)
        val bottomSheetDatePicker = bottomSheetView.findViewById<DatePicker>(R.id.bottomSheetDatePicker)
        val bottomSheetSaveBtn = bottomSheetView.findViewById<AppCompatButton>(R.id.bottomSheetSaveBtn)

        bottomSheetSaveBtn.setOnClickListener {
            val title = bottomSheetTitleEdt.text.toString()
            val day = bottomSheetDatePicker.dayOfMonth
            val month = bottomSheetDatePicker.month
            val year = bottomSheetDatePicker.year


            if(title.isNotEmpty()) {
                val dueDate = "$day/${month+1}/$year"

                val calendar = Calendar.getInstance()
                val todayDay = calendar.get(Calendar.DAY_OF_MONTH)
                val todayMonth = calendar.get(Calendar.MONTH)
                val todayYear = calendar.get(Calendar.YEAR)

                val todayDate = "$todayDay/${todayMonth + 1}/$todayYear"

                val taskItem = TasksItemModel(
                    title = title,
                    date = if (dueDate == todayDate) "Today" else dueDate,
                    isCompleted = false
                )

                val newTaskItemsList = mutableListOf<TasksItemModel>()
                newTaskItemsList.addAll(taskItemsList)
                newTaskItemsList.add(taskItem)

                val taskItemDiffUtil = TaskItemDiffUtil(taskItemsList, newTaskItemsList)
                val taskItemDiffResult = DiffUtil.calculateDiff(taskItemDiffUtil)

                taskItemsList.clear()
                taskItemsList.addAll(newTaskItemsList)

                taskItemDiffResult.dispatchUpdatesTo(binding.overDueTasksRV.adapter as TasksRecyclerAdapter)
                taskItemDiffResult.dispatchUpdatesTo(binding.completedTasksRV.adapter as TasksRecyclerAdapter)

                binding.apply {
                    overDueTasksRV.adapter?.notifyItemChanged(taskItemsList.size-1)
                    completedTasksRV.adapter?.notifyItemChanged(taskItemsList.size-1)

                }

                checkEmptyAndLoad()

                bottomSheetDialog.dismiss()
            } else {
                bottomSheetTitleEdt.error = "Please enter a title"
            }
        }
    }

    private fun setUpTasksRecyclerViews() {
        binding.apply {
            overDueTasksRV.layoutManager = LinearLayoutManager(this@MainActivity)
            completedTasksRV.layoutManager = LinearLayoutManager(this@MainActivity)


            overDueTasksRV.adapter = TasksRecyclerAdapter(
                taskItemsList.filter {
                    !it.isCompleted
                }
            )

            completedTasksRV.adapter = TasksRecyclerAdapter(
                taskItemsList.filter {
                    it.isCompleted
                }
            )

        }
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