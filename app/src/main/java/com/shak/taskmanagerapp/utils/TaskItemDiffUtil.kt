package com.shak.taskmanagerapp.utils

import androidx.recyclerview.widget.DiffUtil
import com.shak.taskmanagerapp.models.TasksItemModel

class TaskItemDiffUtil: DiffUtil.ItemCallback<TasksItemModel>() {
    override fun areItemsTheSame(
        oldItem: TasksItemModel,
        newItem: TasksItemModel
    ): Boolean {
        return oldItem.id == newItem.id
    }

    override fun areContentsTheSame(
        oldItem: TasksItemModel,
        newItem: TasksItemModel
    ): Boolean {
        return oldItem == newItem
    }

}