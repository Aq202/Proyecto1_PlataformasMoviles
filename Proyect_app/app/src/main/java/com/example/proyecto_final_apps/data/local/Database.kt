package com.example.proyecto_final_apps.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import com.example.proyecto_final_apps.data.local.dao.*
import com.example.proyecto_final_apps.data.local.entity.*

@Database(
    entities = [UserModel::class, OperationModel::class, AccountModel::class, ContactModel::class, DebtAcceptedModel::class],
    version=1
)
abstract class Database : RoomDatabase() {
    abstract fun userDao():UserDao
    abstract fun operationDao():OperationDao
    abstract fun accountDao():AccountDao
    abstract fun contactDao():ContactDao
    abstract fun debtDao():DebtDao
}