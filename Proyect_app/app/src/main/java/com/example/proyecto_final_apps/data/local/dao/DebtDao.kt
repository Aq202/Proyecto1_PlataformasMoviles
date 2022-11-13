package com.example.proyecto_final_apps.data.local.dao

import androidx.room.*
import com.example.proyecto_final_apps.data.local.entity.DebtAcceptedModel

@Dao
interface DebtDao{

    @Query("SELECT * FROM DebtAcceptedModel WHERE userInvolved=:userId AND deletionPending=${false}")
    suspend fun getAcceptedDebtsByUser(userId:String):List<DebtAcceptedModel>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertManyAcceptedDebts(acceptedDebts:List<DebtAcceptedModel>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAcceptedDebt(acceptedDebt: DebtAcceptedModel):Long

    @Query("DELETE FROM DebtAcceptedModel")
    suspend fun deleteAllAcceptedDebts():Int

    @Query("DELETE FROM DebtAcceptedModel WHERE userInvolved=:userId")
    suspend fun deleteAllAcceptedDebtsByUser(userId:String):Int

    @Query("UPDATE DebtAcceptedModel SET deletionPending=${true} WHERE userInvolved=:userId")
    suspend fun setDeletionPendingToAcceptedDebtsByUserId(userId:String)

    @Query("SELECT * FROM DebtAcceptedModel WHERE deletionPending=${true}")
    suspend fun getAllAcceptedDebtsPendingToDelete():List<DebtAcceptedModel>

    @Query("SELECT * FROM DebtAcceptedModel WHERE deletionPending=${false} AND requiresUpdate=${true}")
    suspend fun getAllAcceptedDebtsRequiringUpdate():List<DebtAcceptedModel>

    @Update
    suspend fun updateAcceptedDebt(acceptedDebt:DebtAcceptedModel)

}