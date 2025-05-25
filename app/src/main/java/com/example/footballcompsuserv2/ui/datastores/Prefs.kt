package com.example.footballcompsuserv2.ui.datastores

import android.content.Context
import android.content.res.Configuration
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import java.util.Locale

object LangPrefs {
    private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "lang_preferences")


    fun langPref(context: Context): Flow<Boolean> {
        return context.dataStore.data.map { preferences ->
            preferences[LANG] ?: false
        }
    }

    private val LANG = booleanPreferencesKey("language")
}


object LanguagePrefs {
    fun setLocale(langCode: String, context: Context) {
        val locale = Locale(langCode)
        Locale.setDefault(locale)

        val config = Configuration()
        config.setLocale(locale)

        context.resources.updateConfiguration(config,
            context.resources.displayMetrics)
    }

    fun saveLanguagePreference(context: Context, langCode: String) {
        val sharedPrefs = context.getSharedPreferences("football_prefs", Context.MODE_PRIVATE)
        sharedPrefs.edit().putString("changed_language", langCode).apply()
    }

    fun getSavedLanguage(context: Context): String {
        val sharedPrefs = context.getSharedPreferences("football_prefs", Context.MODE_PRIVATE)
        return sharedPrefs.getString("changed_language", "es") ?: "es"
    }
}