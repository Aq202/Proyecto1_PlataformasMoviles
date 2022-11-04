package com.example.proyecto_final_apps.di

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStore
import androidx.room.Room
import com.example.proyecto_final_apps.data.local.Database
import com.example.proyecto_final_apps.data.local.MyDataStore
import com.example.proyecto_final_apps.data.remote.API
import com.example.proyecto_final_apps.data.repository.OperationRepository
import com.example.proyecto_final_apps.data.repository.OperationRepositoryImp
import com.example.proyecto_final_apps.data.repository.UserRepository
import com.example.proyecto_final_apps.data.repository.UserRepositoryImp
import com.example.proyecto_final_apps.helpers.apiUrl
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import javax.inject.Named
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
class AppModule {

    @Provides
    @Singleton
    fun provideApi(): API {
        return Retrofit.Builder()
            .addConverterFactory(GsonConverterFactory.create())
            .baseUrl(apiUrl)
            .build()
            .create(API::class.java)
    }

    @Provides
    @Singleton
    fun provideUserRepository(
        api: API,
        @ApplicationContext context: Context,
        database: Database
    ): UserRepository {
        return UserRepositoryImp(
            api = api,
            context = context,
            database = database


        )
    }

    @Provides
    @Singleton
    fun provideDatabase(@ApplicationContext context: Context): Database {
        return Room.databaseBuilder(
            context,
            Database::class.java,
            "database"
        ).build()
    }


    @Provides
    @Singleton
    fun provideOperationRepository(
        api: API,
        @ApplicationContext context: Context,
        database: Database
    ): OperationRepository {
        return OperationRepositoryImp(
            api = api,
            context = context,
            database = database
        )
    }

}


/*
 companion object {
        private val retrofit by lazy {
            val logging = HttpLoggingInterceptor()
            logging.setLevel(HttpLoggingInterceptor.Level.BODY)
            val client = OkHttpClient.Builder()
                .addInterceptor(logging)
                .build()

            Retrofit.Builder()
                .baseUrl(Constants.RICK_AND_MORTY_API_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .client(client)
                .build()
        }

        val api by lazy {
            retrofit.create(RickAndMortyApi::class.java)
        }
    }

* */