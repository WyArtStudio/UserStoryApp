package com.wahyuhw.userstoryapp.data.room

import androidx.room.*

@Dao
interface BookmarkStoryDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(story: BookmarkStoryEntity)

    @Delete
    suspend fun delete(story: BookmarkStoryEntity)

    @Query("SELECT * from bookmark_table WHERE name= :name")
    suspend fun getDetailStory(name: String): BookmarkStoryEntity?

    @Query("SELECT * from bookmark_table ORDER BY name ASC")
    suspend fun getListBookmarkStory(): List<BookmarkStoryEntity>

    @Query("DELETE from bookmark_table")
    suspend fun clearStory()
}