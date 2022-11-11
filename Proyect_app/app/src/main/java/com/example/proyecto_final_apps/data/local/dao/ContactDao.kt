package com.example.proyecto_final_apps.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.proyecto_final_apps.data.local.entity.AccountModel
import com.example.proyecto_final_apps.data.local.entity.ContactModel

@Dao
interface ContactDao {

    @Query("SELECT COUNT(localId) FROM ContactModel WHERE deletionPending=${false}")
    suspend fun getNumberOfContacts():Int

    @Insert
    suspend fun insertContact(contact:ContactModel)

    @Query("SELECT * FROM ContactModel WHERE deletionPending=${false}")
    suspend fun getContactsList():List<ContactModel>

    @Query("SELECT * FROM ContactModel WHERE localId=:localId AND deletionPending=${false}")
    suspend fun getContactById(localId:Int):ContactModel?

    @Query("DELETE FROM ContactModel")
    suspend fun deleteAll()

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertManyContacts(accounts:List<ContactModel>)

    @Query("SELECT * FROM ContactModel WHERE userAsContact=:userId AND deletionPending=${false}")
    suspend fun getContactByUserAsContact(userId:String):ContactModel?
}