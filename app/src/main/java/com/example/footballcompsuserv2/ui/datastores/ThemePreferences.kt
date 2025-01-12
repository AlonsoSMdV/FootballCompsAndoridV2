package com.example.footballcompsuserv2.ui.datastores

import android.content.Context
import androidx.datastore.preferences.preferencesDataStore
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map


//DataStore implementado para cambiar el tema de la applicacion movil con un botón, se ha sacado en base a mirar stackoverflow y Android Developers
val Context.dataStore by preferencesDataStore(name = "theme_preferences")
class ThemePreferences(private val context: Context) {
    private val THEME_KEY = booleanPreferencesKey("theme_key")

    val themeFlow: Flow<Boolean> = context.dataStore.data
        .map { preferences ->
            preferences[THEME_KEY] ?: false
        }

    suspend fun saveTheme(isDark: Boolean) {
        context.dataStore.edit { preferences ->
            preferences[THEME_KEY] = isDark
        }
    }
}