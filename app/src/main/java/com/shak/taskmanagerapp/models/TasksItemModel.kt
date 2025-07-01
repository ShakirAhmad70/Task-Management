package com.shak.taskmanagerapp.models

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "tasks")
data class TasksItemModel(
    @PrimaryKey(autoGenerate = true)
    var id: Int = 0,
    var isCompleted: Boolean,
    var title: String,
    var date: String
)
