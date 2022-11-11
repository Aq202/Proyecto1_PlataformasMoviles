package com.example.proyecto_final_apps.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.proyecto_final_apps.data.local.entity.OperationModel

@Dao
interface OperationDao {

    @Query("SELECT COUNT(localId) FROM OperationModel")
    suspend fun getOperationsStoredNumber():Int

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertMany(operations:List<OperationModel>)

    @Query("SELECT * FROM OperationModel ORDER BY date DESC")
    suspend fun getAllOperations():List<OperationModel>

    @Query("SELECT * FROM OperationModel ORDER BY date DESC LIMIT :max")
    suspend fun getRecentOperations(max:Int):List<OperationModel>

    @Query("DELETE FROM OperationModel")
    suspend fun deleteAllOperations()

    @Query("SELECT * FROM OperationModel WHERE accountLocalId=:accountLocalId")
    suspend fun getAccountOperations(accountLocalId:Int):List<OperationModel>

    @Query("DELETE FROM OperationModel WHERE accountLocalId=:accountLocalId")
    suspend fun deleteAllAccountOperations(accountLocalId: Int):Int

    @Query("UPDATE OperationModel SET deletionPending=${true} WHERE accountLocalId=:accountLocalId")
    suspend fun setDeletionPendingToAccountOperations(accountLocalId:Int)

    @Query("SELECT * FROM OperationModel WHERE deletionPending=${true}")
    suspend fun getAllPendingToDeleteOperations():List<OperationModel>
}