package com.shak.taskmanagerapp.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.DatePicker
import androidx.appcompat.widget.AppCompatButton
import androidx.appcompat.widget.AppCompatEditText
import androidx.appcompat.widget.AppCompatTextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.transition.AutoTransition
import androidx.transition.TransitionManager
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.shak.taskmanagerapp.R
import com.shak.taskmanagerapp.adapters.TasksRecyclerAdapter
import com.shak.taskmanagerapp.daos.TaskItemDao
import com.shak.taskmanagerapp.databases.TaskItemDB
import com.shak.taskmanagerapp.databinding.FragmentTasksBinding
import com.shak.taskmanagerapp.models.TasksItemModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.util.Calendar


class TasksFragment : Fragment() {
    private lateinit var binding: FragmentTasksBinding
    private lateinit var taskItemDao: TaskItemDao
    private lateinit var tasksDb: TaskItemDB
    private val allTasksList = mutableListOf<TasksItemModel>()

    private lateinit var overDueAdapter: TasksRecyclerAdapter
    private lateinit var completedAdapter: TasksRecyclerAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        tasksDb = TaskItemDB.getDatabase(requireContext())
        taskItemDao = tasksDb.taskItemDao()
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        binding = FragmentTasksBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupAdapters()
        observeTasks()
        updateUI(overDueAdapter.currentList, completedAdapter.currentList)

        binding.apply {

            addTasksFab.setOnClickListener {
                showAddEditTaskBottomSheet(getTodayDate())
            }

            emptyImg.setOnClickListener {
                addTasksFab.performClick()
            }

            var isOverDueExpanded = true
            overDueTasksTitleContainerRLay.setOnClickListener {
                isOverDueExpanded = expandOrCollapse(isOverDueExpanded, overDueTasksRV, overDueTasksArrowTxt)
            }

            var isCompletedExpanded = true
            completedTasksTitleContainerRLay.setOnClickListener {
                isCompletedExpanded = expandOrCollapse(isCompletedExpanded, completedTasksRV, completedTasksArrowTxt)
            }

        }

    }

    private fun expandOrCollapse(isExpanded: Boolean, tasksRV: RecyclerView, tasksArrowTxt: AppCompatTextView): Boolean {
        TransitionManager.beginDelayedTransition(binding.root, AutoTransition())

        if (isExpanded) {
            tasksRV.visibility = View.GONE
            tasksArrowTxt.animate()
                .rotation(90f)
                .setDuration(400)
                .start()
        } else {
            tasksRV.visibility = View.VISIBLE
            tasksArrowTxt.animate()
                .rotation(0f)
                .setDuration(400)
                .start()
        }
        return !isExpanded
    }

    private fun observeTasks() {
        taskItemDao.getOverDueTasks().observe(viewLifecycleOwner) { overDueTasksList ->
            overDueAdapter.submitList(overDueTasksList)
            updateUI(overDueTasksList, completedAdapter.currentList)
        }
        taskItemDao.getCompletedTasks().observe(viewLifecycleOwner) { completedTasksList ->
            completedAdapter.submitList(completedTasksList)
            updateUI(overDueAdapter.currentList, completedTasksList)
        }
    }

    private fun setupAdapters() {
        overDueAdapter = TasksRecyclerAdapter(object : TasksRecyclerAdapter.OnTaskInteractionListener {
            override fun onTaskCheckedChange(taskItem: TasksItemModel, isChecked: Boolean) {
                taskItem.isCompleted = isChecked
                lifecycleScope.launch(Dispatchers.IO) {
                    taskItemDao.updateTask(taskItem)
                    withContext(Dispatchers.Main){
                        updateUI(overDueAdapter.currentList, completedAdapter.currentList)
                    }
                }
            }

            override fun onTaskItemClicked(taskItem: TasksItemModel) {
                showAddEditTaskBottomSheet(
                    taskItem.date,
                    taskItem.title
                )
            }
        })

        completedAdapter = TasksRecyclerAdapter(object : TasksRecyclerAdapter.OnTaskInteractionListener {
                override fun onTaskCheckedChange(taskItem: TasksItemModel, isChecked: Boolean) {
                    taskItem.isCompleted = isChecked
                    lifecycleScope.launch(Dispatchers.IO) {
                        taskItemDao.updateTask(taskItem)
                        withContext(Dispatchers.Main) {
                            updateUI(overDueAdapter.currentList, completedAdapter.currentList)
                        }
                    }
                }

            override fun onTaskItemClicked(taskItem: TasksItemModel) {
                showAddEditTaskBottomSheet(
                    taskItem.date,
                    taskItem.title
                )
            }
        })

        binding.apply {
            overDueTasksRV.layoutManager = LinearLayoutManager(requireContext())
            overDueTasksRV.adapter = overDueAdapter

            completedTasksRV.layoutManager = LinearLayoutManager(requireContext())
            completedTasksRV.adapter = completedAdapter
        }
    }

    private fun showAddEditTaskBottomSheet(currentDate: String, currentTitle: String? = null) {
        val bottomSheetView = layoutInflater.inflate(
            R.layout.add_edit_tasks_bottom_sheet_layout,
            binding.root,
            false
        )

        val bottomSheetDialog = BottomSheetDialog(requireContext())
        bottomSheetDialog.setContentView(bottomSheetView)
        bottomSheetDialog.setCanceledOnTouchOutside(false)
        bottomSheetDialog.show()

        val titleEdt = bottomSheetView.findViewById<AppCompatEditText>(R.id.bottomSheetTitleEdt)
        val datePicker = bottomSheetView.findViewById<DatePicker>(R.id.bottomSheetDatePicker)
        val saveBtn = bottomSheetView.findViewById<AppCompatButton>(R.id.bottomSheetSaveBtn)

        if(currentTitle != null){
            titleEdt.setText(currentTitle)
        }
        val dateList = if(currentDate == "Today") {
            getTodayDate().split("/")
        } else {
            currentDate.split("/")
        }
        val day = dateList[0].toInt()
        val month = dateList[1].toInt() - 1
        val year = dateList[2].toInt()
        datePicker.updateDate(year, month, day)

        saveBtn.setOnClickListener {
            val title = titleEdt.text.toString()
            val day = datePicker.dayOfMonth
            val month = datePicker.month
            val year = datePicker.year

            if (title.isNotEmpty()) {
                val dueDate = "$day/${month + 1}/$year"

                val newTask = TasksItemModel(
                    title = title,
                    date = if (dueDate == getTodayDate()) "Today" else dueDate,
                    isCompleted = false
                )

                allTasksList.add(newTask)
                lifecycleScope.launch(Dispatchers.IO) {
                    taskItemDao.insertTask(newTask)
                }
                updateUI(overDueAdapter.currentList, completedAdapter.currentList)

                bottomSheetDialog.dismiss()
            } else {
                titleEdt.error = "Please enter a title"
            }
        }
    }

    private fun getTodayDate(): String {
        val today = Calendar.getInstance()
        val todayDate = "${today.get(Calendar.DAY_OF_MONTH)}/${today.get(Calendar.MONTH) + 1}/${today.get(Calendar.YEAR)}"
        return todayDate
    }

    fun updateUI(
        overDueTasksList: List<TasksItemModel>,
        completedTasksList: List<TasksItemModel>
    ) {
        binding.apply {
            val noTasks = overDueTasksList.isEmpty() && completedTasksList.isEmpty()

            // Empty-state visibility
            mainEmptyTasksContainerRLay.visibility =
                if (noTasks) View.VISIBLE else View.GONE

            // Section visibility
            overDueTasksContainer.visibility =
                if (overDueTasksList.isNotEmpty()) View.VISIBLE else View.GONE
            completedTasksContainer.visibility =
                if (completedTasksList.isNotEmpty()) View.VISIBLE else View.GONE

            // Counters
            overDueTasksCountTxt.text = overDueTasksList.size.toString()
            completedTasksCountTxt.text = completedTasksList.size.toString()
        }
    }
}
