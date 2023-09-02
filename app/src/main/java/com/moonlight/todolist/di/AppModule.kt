package com.moonlight.todolist.di

import android.content.Context
import android.content.SharedPreferences
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.moonlight.todolist.data.datasource.ToDoDataSource
import com.moonlight.todolist.data.datasource.AuthDataSource
import com.moonlight.todolist.data.datasource.UserDataSource
import com.moonlight.todolist.data.repo.ToDoRepository
import com.moonlight.todolist.data.repo.AuthRepository
import com.moonlight.todolist.data.repo.UserRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
class AppModule {

    @Provides
    @Singleton
    fun provideToDoRepository(tds: ToDoDataSource) : ToDoRepository {
        return ToDoRepository(tds)
    }

    @Provides
    @Singleton
    fun provideToDoDataSource(refDB: DatabaseReference) : ToDoDataSource {
        return ToDoDataSource(refDB)
    }

    @Provides
    @Singleton
    fun provideToDoDatabaseReference() : DatabaseReference {
        val db = FirebaseDatabase.getInstance()
        db.setPersistenceEnabled(true)
        return db.reference
    }

    @Provides
    @Singleton
    fun provideUserRepository(uds: UserDataSource) : UserRepository {
        return UserRepository(uds)
    }

    @Provides
    @Singleton
    fun provideUserDataSource(refDB: DatabaseReference, sp: SharedPreferences) : UserDataSource {
        return UserDataSource(refDB, sp)
    }

    @Provides
    @Singleton
    fun provideAuthRepository(ads: AuthDataSource) : AuthRepository {
        return AuthRepository(ads)
    }

    @Provides
    @Singleton
    fun provideAuthDataSource(sp: SharedPreferences) = AuthDataSource(sp)

    @Singleton
    @Provides
    fun provideSharedPreference(@ApplicationContext context: Context): SharedPreferences {
        return context.getSharedPreferences("login", Context.MODE_PRIVATE)
    }
}