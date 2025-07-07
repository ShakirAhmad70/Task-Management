package com.shak.taskmanagerapp.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.AppCompatCheckBox
import androidx.appcompat.widget.AppCompatTextView
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.shak.taskmanagerapp.R
import com.shak.taskmanagerapp.models.TasksItemModel
import com.shak.taskmanagerapp.utils.TaskItemDiffUtil

class TasksRecyclerAdapter(
    private val listener: OnTaskInteractionListener
): ListAdapter<TasksItemModel, TasksRecyclerAdapter.TasksRecyclerViewHolder>(TaskItemDiffUtil()) {

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): TasksRecyclerViewHolder {
        val view:View = LayoutInflater.from(parent.context).inflate(R.layout.tasks_item_layout, parent, false)
        return TasksRecyclerViewHolder(view)
    }

    override fun onBindViewHolder(
        holder: TasksRecyclerViewHolder,
        position: Int
    ) {
        val taskItem = getItem(position)

        holder.bindData(taskItem)

    }


    interface OnTaskInteractionListener {
        fun onTaskCheckedChange(taskItem: TasksItemModel, isChecked: Boolean)

        fun onTaskItemClicked(taskItem: TasksItemModel)

        fun onTasksItemLongClicked(taskItem: TasksItemModel)
    }

    inner class TasksRecyclerViewHolder(itemView: View): RecyclerView.ViewHolder(itemView) {
        val tasksTitleChkBox: AppCompatCheckBox = itemView.findViewById(R.id.tasksTitleChkBox)
        val tasksTitleTxt: AppCompatTextView = itemView.findViewById(R.id.tasksTitleTxt)
        val showDate: AppCompatTextView = itemView.findViewById(R.id.showDate)


        fun bindData(tasksItemModel: TasksItemModel) {
            // clear previous listener if any to prevent multiple
            tasksTitleChkBox.setOnCheckedChangeListener(null)

            tasksTitleChkBox.isChecked = tasksItemModel.isCompleted
            tasksTitleTxt.text = tasksItemModel.title
            showDate.text = tasksItemModel.date

            tasksTitleChkBox.setOnCheckedChangeListener { _, isChecked ->
                tasksItemModel.isCompleted = isChecked
                listener.onTaskCheckedChange(tasksItemModel, isChecked)
            }

            itemView.setOnClickListener {
                listener.onTaskItemClicked(tasksItemModel)
            }

            itemView.setOnLongClickListener {
                listener.onTasksItemLongClicked(tasksItemModel)
                true  // We are returning true because we want to consume the event
            }
        }
    }

}