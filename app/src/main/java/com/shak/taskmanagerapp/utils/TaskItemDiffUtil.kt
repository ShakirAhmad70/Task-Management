package com.shak.taskmanagerapp.utils

import androidx.recyclerview.widget.DiffUtil
import com.shak.taskmanagerapp.models.TasksItemModel

class TaskItemDiffUtil(private val oldList: List<TasksItemModel>, private val newList: List<TasksItemModel>): DiffUtil.Callback() {
    override fun getOldListSize() = oldList.size

    override fun getNewListSize() = newList.size

    override fun areItemsTheSame(
        oldItemPosition: Int,
        newItemPosition: Int
    ): Boolean {
        return oldList[oldItemPosition].id == newList[newItemPosition].id
    }

    override fun areContentsTheSame(
        oldItemPosition: Int,
        newItemPosition: Int
    ): Boolean {
        return oldList[oldItemPosition] == newList[newItemPosition]
    }
}