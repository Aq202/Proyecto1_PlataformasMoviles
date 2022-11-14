package com.example.proyecto_final_apps.data.remote

import com.example.proyecto_final_apps.data.remote.dto.AccountDto
import com.example.proyecto_final_apps.data.remote.dto.ContactDto
import com.example.proyecto_final_apps.data.remote.dto.DebtsAcceptedDto
import com.example.proyecto_final_apps.data.remote.dto.UserDto
import com.example.proyecto_final_apps.data.remote.dto.accountListResponse.AccountListDto
import com.example.proyecto_final_apps.data.remote.dto.contactListResponse.ContactListResponseDto
import com.example.proyecto_final_apps.data.remote.dto.deleteAccountResponse.DeleteAccountDto
import com.example.proyecto_final_apps.data.remote.dto.getContactDataResponse.GetContactDataDto
import com.example.proyecto_final_apps.data.remote.dto.getOperationsResponse.GetOperationsDto
import com.example.proyecto_final_apps.data.remote.dto.loginResponse.LoginResponse
import com.example.proyecto_final_apps.data.remote.dto.requests.*
import com.example.proyecto_final_apps.data.remote.dto.searchUsersResponse.SearchUsersDto
import com.example.proyecto_final_apps.data.remote.dto.signUpResponse.SignUpResponse
import com.example.proyecto_final_apps.data.remote.dto.operationDto.OperationDto
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Response
import retrofit2.http.*

interface API {

    @POST("/user/login")
    suspend fun login(
        @Body body: LoginRequest
    ): Response<LoginResponse>

    @GET("/user/sessionUserData")
    suspend fun getSessionUserData(
        @Header("authorization") token:String
    ): Response<UserDto>

    @Multipart
    @POST("/user/register")
    suspend fun signUp(
        @PartMap() data:MutableMap<String, RequestBody>,
        @Part image: MultipartBody.Part
    ): Response<SignUpResponse>


    @GET("/operation/getAll")
    suspend fun getAllOperations(
        @Header("authorization") token:String
    ):Response<GetOperationsDto>

    @GET("/account/list")
    suspend fun getAccountList(
        @Header("authorization") token:String
    ):Response<AccountListDto>

    @POST("/account/{accountId}/setAsDefault")
    suspend fun setAsDefaultAccount(
        @Header("authorization") token:String,
        @Path("accountId") accountId:String
    ):Response<Void>

    @DELETE("/account/{accountId}")
    suspend fun deleteAccount(
        @Header("authorization") token:String,
        @Path("accountId") accountId:String
    ):Response<DeleteAccountDto>

    @POST("/account/create")
    suspend fun createAccount(
        @Header("authorization") token:String,
        @Body body: NewAccountRequest
    ): Response<AccountDto>

    @GET("/account/getAccount/{accountId}")
    suspend fun getAccount(
        @Header("authorization") token: String,
        @Path("accountId") accountId:String
    ): Response<AccountDto>

    @POST("/operation/create")
    suspend fun createOperation(
        @Header("authorization") token:String,
        @Body body: NewOperationRequest
    ): Response<OperationDto>

    @POST("/account/update/{accountId}")
    suspend fun updateAccount(
        @Header("authorization") token:String,
        @Path("accountId") accountId:String,
        @Body body: UpdateAccountRequest
    ): Response<AccountDto>

    @GET("/user/contactsList")
    suspend fun getContactsList(
        @Header("authorization") token:String
    ):Response<ContactListResponseDto>

    @GET("/user/data/{userId}")
    suspend fun getUserData(
        @Header("authorization") token:String,
        @Path("userId") accountId:String,
    ):Response<UserDto>


    @GET("/user/search")
    suspend fun searchUsers(
        @Header("authorization") token:String,
        @Query("query") query:String
    ):Response<SearchUsersDto>

    @GET("/contact/data/{contactId}")
    suspend fun getContactData(
        @Header("authorization") token:String,
        @Path("contactId") contactId:String,
    ):Response<GetContactDataDto>

    @POST("/user/addContact")
    suspend fun newContact(
        @Header("authorization") token:String,
        @Body body: NewContactRequest
    ): Response<ContactDto>

    @DELETE("/contact/{contactId}")
    suspend fun deleteContact(
        @Header("authorization") token:String,
        @Path("contactId") contactId:String,
    ):Response<Void>


    @POST("/debt/create")
    suspend fun newDebt(
        @Header("authorization") token:String,
        @Body body: NewDebtRequest
    ): Response<DebtsAcceptedDto>

}