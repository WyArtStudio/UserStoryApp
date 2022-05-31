package com.wahyuhw.userstoryapp.data.room

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.wahyuhw.userstoryapp.data.remote.RemoteKeys
import com.wahyuhw.userstoryapp.data.response.StoryItem

@Database(entities = [StoryItem::class, UserEntity::class, BookmarkStoryEntity::class, RemoteKeys::class], exportSchema = false, version = 3)
abstract class StoryDatabase: RoomDatabase() {
    abstract fun storyDao(): StoryDao
    abstract fun bookmarkStoryDao(): BookmarkStoryDao
    abstract fun userDao(): UserDao
    abstract fun remoteKeysDao(): RemoteKeysDao

    companion object {
        @Volatile
        private var INSTANCE: StoryDatabase? = null

        @JvmStatic
        fun getDatabase(context: Context): StoryDatabase {
            if (INSTANCE == null) {
                synchronized(StoryDatabase::class.java) {
                    INSTANCE = Room.databaseBuilder(context.applicationContext,
                        StoryDatabase::class.java, "story_database").fallbackToDestructiveMigration().build()
                }
            }

            return INSTANCE as StoryDatabase
        }
    }
}