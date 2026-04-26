package com.example.notesapp.data

import android.content.Context
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

private val Context.dataStore by preferencesDataStore("settings")

class SettingsDataStore(private val context: Context) {

    companion object {
        val DARK_THEME = booleanPreferencesKey("dark_theme")
        val SORT_ORDER = stringPreferencesKey("sort_order")
    }

    val isDarkTheme: Flow<Boolean> = context.dataStore.data
        .map { it[DARK_THEME] ?: false }

    val sortOrder: Flow<String> = context.dataStore.data
        .map { it[SORT_ORDER] ?: "DATE" }

    suspend fun setDarkTheme(value: Boolean) =
        context.dataStore.edit { it[DARK_THEME] = value }

    suspend fun setSortOrder(value: String) =
        context.dataStore.edit { it[SORT_ORDER] = value }
}