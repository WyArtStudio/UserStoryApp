package com.wahyuhw.userstoryapp.data.prefs

import android.annotation.SuppressLint
import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.preferencesDataStore
import com.wahyuhw.userstoryapp.utils.PrefsKey
import kotlinx.coroutines.flow.map

class SettingsPreferences (private val context: Context) {
    private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(
        name = PrefsKey.PREFS_NAME)

    fun getTokenSetting() =
        context.dataStore.data.map { prefs ->
            prefs[PrefsKey.TOKEN_KEY]
        }

    suspend fun saveTokenSetting(token: String) {
        context.dataStore.edit { prefs ->
            prefs[PrefsKey.TOKEN_KEY] = token
        }
    }

    fun getSessionSetting() =
        context.dataStore.data.map { prefs ->
            prefs[PrefsKey.SESSION_KEY]
        }

    suspend fun saveSessionSetting(isAlreadyLogged: Boolean) {
        context.dataStore.edit { prefs ->
            prefs[PrefsKey.SESSION_KEY] = isAlreadyLogged
        }
    }

    companion object {
        @SuppressLint("StaticFieldLeak")
        @Volatile
        private var INSTANCE: SettingsPreferences? = null

        fun getInstance(context: Context): SettingsPreferences {
            return INSTANCE ?: synchronized(this) {
                val instance = SettingsPreferences(context)
                INSTANCE = instance
                instance
            }
        }
    }
}