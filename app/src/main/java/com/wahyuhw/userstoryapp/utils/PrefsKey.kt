package com.wahyuhw.userstoryapp.utils

import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey

object PrefsKey {
    const val PREFS_NAME = "USER_PREFERENCES"
    val TOKEN_KEY = stringPreferencesKey("TOKEN_PREFERENCES_KEY")
    val SESSION_KEY = booleanPreferencesKey("SESSION_PREFERENCES_KEY")
}