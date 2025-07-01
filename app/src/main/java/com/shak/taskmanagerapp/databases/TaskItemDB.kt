package com.shak.taskmanagerapp.databases

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.shak.taskmanagerapp.daos.TaskItemDao
import com.shak.taskmanagerapp.models.TasksItemModel

@Database(entities = [TasksItemModel::class], version = 1, exportSchema = false)
abstract class TaskItemDB : RoomDatabase() {

    abstract fun taskItemDao(): TaskItemDao

    companion object {
        // Singleton to prevent multiple instances
        @Volatile
        private var INSTANCE: TaskItemDB? = null

        fun getDatabase(context: Context): TaskItemDB {
            if(INSTANCE == null) {
                synchronized(this){
                    INSTANCE = Room.databaseBuilder(
                        context.applicationContext,
                        TaskItemDB::class.java,
                        "tasks.db"
                    ).build()
                }
            }
            return INSTANCE!!
        }
    }
}