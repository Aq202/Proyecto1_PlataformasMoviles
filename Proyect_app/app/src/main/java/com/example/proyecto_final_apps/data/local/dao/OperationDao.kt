package com.example.proyecto_final_apps.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.example.proyecto_final_apps.data.local.entity.OperationModel

@Dao
interface OperationDao {

    @Query("SELECT COUNT(localId) FROM OperationModel")
    suspend fun getOperationsStoredNumber():Int

    @Insert
    suspend fun insertMany(operations:List<OperationModel>)

    @Query("SELECT * FROM OperationModel ORDER BY date DESC")
    suspend fun getAllOperations():List<OperationModel>

    @Query("SELECT * FROM OperationModel ORDER BY date DESC LIMIT :max")
    suspend fun getRecentOperations(max:Int):List<OperationModel>

    @Query("DELETE FROM OperationModel")
    suspend fun deleteAllOperations()

}