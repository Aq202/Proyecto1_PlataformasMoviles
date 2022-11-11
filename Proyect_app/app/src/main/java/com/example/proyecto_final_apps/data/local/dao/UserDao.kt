package com.example.proyecto_final_apps.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.proyecto_final_apps.data.local.entity.UserModel

@Dao
interface UserDao {

    @Query("SELECT * FROM UserModel WHERE id=:id LIMIT 1")
    suspend fun getUser(id:String):UserModel?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertUser(user:UserModel)

    @Query("DELETE FROM UserModel")
    suspend fun deleteAll()

    @Query("SELECT * FROM UserModel WHERE id!=:userInSessionId AND deletionPending=${false}")
    suspend fun getAllUsers(userInSessionId:String):List<UserModel>


}