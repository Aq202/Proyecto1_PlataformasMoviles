package com.example.proyecto_final_apps.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.proyecto_final_apps.data.local.entity.UserModel

@Dao
interface UserDao {

    @Query("SELECT * FROM UserModel LIMIT 1")
    suspend fun getUser():UserModel?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertUser(user:UserModel)

    @Query("DELETE FROM UserModel")
    suspend fun deleteAll()

}