package com.wahyuhw.userstoryapp.data.room

import android.os.Parcelable
import androidx.annotation.NonNull
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.google.gson.annotations.SerializedName
import kotlinx.parcelize.Parcelize

@Parcelize
@Entity(tableName = "bookmark_table")
data class BookmarkStoryEntity(
    @ColumnInfo(name = "id")
    @PrimaryKey
    @NonNull
    val id: String = "",

    @ColumnInfo(name = "photoUrl")
    val photoUrl: String? = null,

    @ColumnInfo(name = "createdAt")
    val createdAt: String? = null,

    @ColumnInfo(name = "name")
    val name: String? = null,

    @ColumnInfo(name = "description")
    val description: String? = null,

    @ColumnInfo(name = "lon")
    val lon: Double? = null,

    @ColumnInfo(name = "lat")
    val lat: Double? = null,

    @ColumnInfo(name = "isBookmark")
    var isBookmark: Boolean? = false
): Parcelable
