package com.wahyuhw.userstoryapp.di

import android.content.Context
import com.wahyuhw.userstoryapp.data.network.RetrofitClient
import com.wahyuhw.userstoryapp.data.prefs.SettingsPreferences
import com.wahyuhw.userstoryapp.data.repository.MainRepository
import com.wahyuhw.userstoryapp.data.room.StoryDatabase

object Injection {
    fun provideRepository(context: Context): MainRepository {
        val database = StoryDatabase.getDatabase(context)
        val apiService = RetrofitClient.apiInterface
        val prefs = SettingsPreferences.getInstance(context)
        return MainRepository(database, apiService, prefs)
    }
}