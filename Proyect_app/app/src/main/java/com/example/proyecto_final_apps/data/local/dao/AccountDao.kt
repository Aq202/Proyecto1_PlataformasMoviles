package com.example.proyecto_final_apps.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.example.proyecto_final_apps.data.local.entity.AccountModel

@Dao
interface AccountDao {

    @Query("SELECT COUNT(localId) FROM AccountModel")
    suspend fun getNumberOfAccounts():Int

    @Insert
    suspend fun insertManyAccounts(accounts:List<AccountModel>)

    @Query("SELECT * FROM AccountModel ORDER BY defaultAccount DESC")
    suspend fun getAccounts():List<AccountModel>

    @Query("DELETE FROM AccountModel")
    suspend fun deleteAll()
}