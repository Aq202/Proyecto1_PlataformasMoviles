package com.example.proyecto_final_apps.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import com.example.proyecto_final_apps.data.local.dao.UserDao
import com.example.proyecto_final_apps.data.local.entity.UserModel

@Database(
    entities = [UserModel::class],
    version=1
)
abstract class Database : RoomDatabase() {
    abstract fun userDao():UserDao
}