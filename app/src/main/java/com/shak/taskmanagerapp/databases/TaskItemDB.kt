package com.shak.taskmanagerapp.databases

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.shak.taskmanagerapp.daos.TaskItemDao
import com.shak.taskmanagerapp.models.TasksItemModel

@Database(entities = [TasksItemModel::class], version = 1)
abstract class TaskItemDB : RoomDatabase() {

    abstract fun taskItemDao(): TaskItemDao

    companion object {
        // Singleton to prevent multiple instances
        @Volatile
        private var INSTANCE: TaskItemDB? = null

        fun getInstance(context: Context): TaskItemDB {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    TaskItemDB::class.java,
                    "taskItem.db"
                ).build()
                INSTANCE = instance
                instance
            }
        }
    }
}