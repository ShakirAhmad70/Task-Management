package com.shak.taskmanagerapp.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.AppCompatCheckBox
import androidx.appcompat.widget.AppCompatTextView
import androidx.recyclerview.widget.RecyclerView
import com.shak.taskmanagerapp.R
import com.shak.taskmanagerapp.models.TasksItemModel

class TasksRecyclerAdapter(
    private val taskItemsList: List<TasksItemModel>
): RecyclerView.Adapter<TasksRecyclerAdapter.TasksRecyclerViewHolder>() {

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): TasksRecyclerAdapter.TasksRecyclerViewHolder {
        val view:View = LayoutInflater.from(parent.context).inflate(R.layout.tasks_item_layout, parent, false)
        return TasksRecyclerViewHolder(view)
    }

    override fun onBindViewHolder(
        holder: TasksRecyclerAdapter.TasksRecyclerViewHolder,
        position: Int
    ) {
        val taskItem = taskItemsList[position]

        holder.tasksTitleChkBox.isChecked = taskItem.isCompleted
        holder.tasksTitleTxt.text = taskItem.title
        holder.showDate.text = taskItem.date
    }

    override fun getItemCount() = taskItemsList.size

    class TasksRecyclerViewHolder(itemView: View): RecyclerView.ViewHolder(itemView) {
        val tasksTitleChkBox: AppCompatCheckBox = itemView.findViewById<AppCompatCheckBox>(R.id.tasksTitleChkBox)
        val tasksTitleTxt: AppCompatTextView = itemView.findViewById<AppCompatTextView>(R.id.tasksTitleTxt)
        val showDate: AppCompatTextView = itemView.findViewById<AppCompatTextView>(R.id.showDate)
    }

}