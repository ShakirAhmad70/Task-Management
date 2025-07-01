package com.shak.taskmanagerapp.daos

import androidx.lifecycle.LiveData
import androidx.room.*
import com.shak.taskmanagerapp.models.TasksItemModel
import kotlinx.coroutines.flow.Flow

@Dao
interface TaskItemDao {

    @Query("SELECT * from tasks")
    fun getAllTasks(): LiveData<List<TasksItemModel>>

    @Query("SELECT * FROM tasks WHERE isCompleted = 0")
    fun getOverDueTasks(): LiveData<List<TasksItemModel>>

    @Query("SELECT * FROM tasks WHERE isCompleted = 1")
    fun getCompletedTasks(): LiveData<List<TasksItemModel>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTask(taskItemModel: TasksItemModel)

    @Update
    suspend fun updateTask(taskItemModel: TasksItemModel)

    @Delete
    suspend fun deleteTask(taskItemModel: TasksItemModel)
}