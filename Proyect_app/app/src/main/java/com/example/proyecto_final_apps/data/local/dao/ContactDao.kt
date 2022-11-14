package com.example.proyecto_final_apps.data.local.dao

import androidx.room.*
import com.example.proyecto_final_apps.data.local.entity.AccountModel
import com.example.proyecto_final_apps.data.local.entity.ContactModel
import com.example.proyecto_final_apps.data.local.entity.DebtAcceptedModel

@Dao
interface ContactDao {

    @Query("SELECT COUNT(localId) FROM ContactModel WHERE deletionPending=${false}")
    suspend fun getNumberOfContacts():Int

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertContact(contact:ContactModel):Long

    @Query("SELECT * FROM ContactModel WHERE deletionPending=${false}")
    suspend fun getContactsList():List<ContactModel>

    @Query("SELECT * FROM ContactModel WHERE localId=:localId AND deletionPending=${false}")
    suspend fun getContactById(localId:Int):ContactModel?

    @Query("DELETE FROM ContactModel")
    suspend fun deleteAll()

    @Delete
    suspend fun deleteContact(contact:ContactModel)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertManyContacts(accounts:List<ContactModel>)

    @Query("SELECT * FROM ContactModel WHERE userAsContact=:userId AND deletionPending=${false}")
    suspend fun getContactByUserAsContact(userId:String):ContactModel?

    @Update
    suspend fun updateContact(contact:ContactModel)

    @Query("SELECT * FROM ContactModel WHERE deletionPending=${true}")
    suspend fun getAllContactsPendingToDelete():List<ContactModel>

    @Query("SELECT * FROM ContactModel WHERE deletionPending=${false} AND requiresUpdate=${true}")
    suspend fun getAllContactsRequiringUpdate():List<ContactModel>
}