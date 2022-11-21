package com.example.proyecto_final_apps.data.local.dao

import androidx.room.*
import com.example.proyecto_final_apps.data.local.entity.AccountModel
import com.example.proyecto_final_apps.data.local.entity.OperationModel

@Dao
interface OperationDao {

    @Query("SELECT COUNT(localId) FROM OperationModel WHERE deletionPending=${false}")
    suspend fun getOperationsStoredNumber():Int

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertMany(operations:List<OperationModel>)

    @Query("SELECT * FROM OperationModel WHERE deletionPending=${false} ORDER BY localId DESC")
    suspend fun getAllOperations():List<OperationModel>

    @Update
    suspend fun updateOperation(operation:OperationModel):Int

    @Delete
    suspend fun deleteOperation(operation:OperationModel):Int

    @Query("SELECT * FROM OperationModel WHERE deletionPending=${false} ORDER BY localId DESC LIMIT :max")
    suspend fun getRecentOperations(max:Int):List<OperationModel>

    @Query("DELETE FROM OperationModel")
    suspend fun deleteAllOperations()

    @Query("SELECT * FROM OperationModel WHERE deletionPending=${false} AND accountLocalId=:accountLocalId")
    suspend fun getAccountOperations(accountLocalId:Int):List<OperationModel>

    @Query("DELETE FROM OperationModel WHERE accountLocalId=:accountLocalId")
    suspend fun deleteAllAccountOperations(accountLocalId: Int):Int

    @Query("UPDATE OperationModel SET deletionPending=${true} WHERE accountLocalId=:accountLocalId")
    suspend fun setDeletionPendingToAccountOperations(accountLocalId:Int)

    @Query("SELECT * FROM OperationModel WHERE deletionPending=${false} AND localId=:localId")
    suspend fun getOperationById(localId:Int): OperationModel?

    @Query("SELECT * FROM OperationModel WHERE deletionPending=${true}")
    suspend fun getAllPendingToDeleteOperation():List<OperationModel>

    @Query("SELECT * FROM OperationModel WHERE requiresUpdate=${true}")
    suspend fun getAllOperationsRequiringUpdate():List<OperationModel>

    @Query("UPDATE OperationModel SET accountRemoteId=:accountRemoteId WHERE accountLocalId=:accountLocalId")
    suspend fun setAccountRemoteIdToAccountOperations(accountLocalId: Int, accountRemoteId:String)
}