package com.wahyuhw.userstoryapp.data.room

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query

@Dao
interface UserDao {
    @Insert
    suspend fun insertUser(user: UserEntity)

    @Query("SELECT * from user_table")
    suspend fun getUser(): UserEntity?

    @Query("DELETE from user_table")
    suspend fun clearUser()
}