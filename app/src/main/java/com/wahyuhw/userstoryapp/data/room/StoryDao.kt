package com.wahyuhw.userstoryapp.data.room

import androidx.paging.PagingSource
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
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