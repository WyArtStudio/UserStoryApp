package com.wahyuhw.userstoryapp.data.room

import androidx.paging.PagingSource
import androidx.room.*
import com.wahyuhw.userstoryapp.data.response.StoryItem

@Dao
interface StoryDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(story: List<StoryItem>)

    @Query("SELECT * FROM story_table")
    fun getAllStory(): PagingSource<Int, StoryItem>

    @Query("DELETE FROM story_table")
    suspend fun clearStory()
}