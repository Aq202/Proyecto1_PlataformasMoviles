package com.example.proyecto_final_apps.data.local.dao

import androidx.room.*
import com.example.proyecto_final_apps.data.local.entity.AccountModel
import com.example.proyecto_final_apps.data.local.entity.OperationModel

@Dao
interface AccountDao {

    @Query("SELECT COUNT(localId) FROM AccountModel WHERE deletionPending=${false}")
    suspend fun getNumberOfAccounts():Int

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertManyAccounts(accounts:List<AccountModel>)

    @Query("SELECT * FROM AccountModel WHERE deletionPending=${false} ORDER BY defaultAccount DESC")
    suspend fun getAccounts():List<AccountModel>

    @Query("SELECT * FROM AccountModel WHERE deletionPending=${false} AND editable=${true} ORDER BY defaultAccount DESC")
    suspend fun getEditableAccounts():List<AccountModel>

    @Query("DELETE FROM AccountModel")
    suspend fun deleteAll()

    @Query("SELECT remoteId FROM AccountModel WHERE localId=:localId")
    suspend fun getRemoteId(localId:Int):String?

    @Query("SELECT * FROM AccountModel WHERE localId=:localId")
    suspend fun getAccountById(localId:Int):AccountModel?

    @Update
    suspend fun updateAccount(account:AccountModel)

    @Delete
    suspend fun deleteAccount(account:AccountModel):Int

    @Query("UPDATE AccountModel SET deletionPending=${true} WHERE localId=:localId")
    suspend fun setDeletionPendingById(localId:Int)

    @Query("SELECT * FROM AccountModel WHERE defaultAccount=${true}")
    suspend fun getDefaultAccount():AccountModel?

    @Query("SELECT * FROM AccountModel WHERE deletionPending=${true}")
    suspend fun getAllPendingToDeleteAccount():List<AccountModel>

}