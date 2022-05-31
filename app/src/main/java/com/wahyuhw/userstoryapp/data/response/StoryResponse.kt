package com.wahyuhw.userstoryapp.data.response

import android.os.Parcelable
import androidx.annotation.NonNull
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.google.gson.annotations.SerializedName
import kotlinx.parcelize.Parcelize

@Parcelize
data class StoryResponse(
	@field:SerializedName("listStory")
	val listStory: List<StoryItem>? = null,

	@field:SerializedName("error")
	val error: Boolean? = null,

	@field:SerializedName("message")
	val message: String? = null
) : Parcelable

@Parcelize
@Entity(tableName = "story_table")
data class StoryItem(
	@ColumnInfo(name = "id")
	@field:SerializedName("id")
	@PrimaryKey
	@NonNull
	val id: String = "",

	@ColumnInfo(name = "photoUrl")
	@field:SerializedName("photoUrl")
	val photoUrl: String? = null,

	@ColumnInfo(name = "createdAt")
	@field:SerializedName("createdAt")
	val createdAt: String? = null,

	@ColumnInfo(name = "name")
	@field:SerializedName("name")
	val name: String? = null,

	@ColumnInfo(name = "description")
	@field:SerializedName("description")
	val description: String? = null,

	@ColumnInfo(name = "lon")
	@field:SerializedName("lon")
	val lon: Double? = null,

	@ColumnInfo(name = "lat")
	@field:SerializedName("lat")
	val lat: Double? = null,

	@ColumnInfo(name = "isBookmark")
	var isBookmark: Boolean? = false
) : Parcelable
