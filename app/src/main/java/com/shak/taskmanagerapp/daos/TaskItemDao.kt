package com.shak.taskmanagerapp.daos

import androidx.lifecycle.LiveData
import androidx.room.*
import com.shak.taskmanagerapp.models.TasksItemModel

@Dao
interface TaskItemDao {

    @Query("SELECT * FROM tasks")
    fun getTaskItems(): LiveData<List<TasksItemModel>>

    @Query("SELECT * FROM tasks WHERE id = :id")
    suspend fun getTaskItemById(id: Int): TasksItemModel

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTaskItem(task: TasksItemModel)

    @Delete
    suspend fun deleteTaskItem(task: TasksItemModel)

    @Update
    suspend fun updateTaskItem(task: TasksItemModel)
}