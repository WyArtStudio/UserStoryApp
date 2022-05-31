package com.wahyuhw.userstoryapp.data.room

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "user_table")
data class UserEntity(
    @ColumnInfo(name = "userId")
    @PrimaryKey
    val userId: String,

    @ColumnInfo(name = "name")
    val name: String
)
