package com.example.proyecto_final_apps.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.proyecto_final_apps.data.local.entity.DebtAcceptedModel

@Dao
interface DebtDao{

    @Query("SELECT * FROM DebtAcceptedModel WHERE userInvolved=:userId AND deletionPending=${false}")
    suspend fun getAcceptedDebtsByUser(userId:String):List<DebtAcceptedModel>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertManyAcceptedDebts(acceptedDebts:List<DebtAcceptedModel>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAcceptedDebt(acceptedDebt: DebtAcceptedModel)

    @Query("DELETE FROM DebtAcceptedModel")
    suspend fun deleteAllAcceptedDebts():Int

}