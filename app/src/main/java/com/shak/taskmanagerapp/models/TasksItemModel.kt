package com.shak.taskmanagerapp.models

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "tasks")
data class TasksItemModel(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val isCompleted: Boolean,
    val title: String,
    val date: String
)
